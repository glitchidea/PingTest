package com.pingtest.app.net

import com.pingtest.app.model.Status
import com.pingtest.app.model.TestResult
import java.net.ConnectException
import java.net.HttpURLConnection
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.NoRouteToHostException
import java.net.PortUnreachableException
import java.net.Socket
import java.net.SocketTimeoutException
import java.net.URI
import java.net.UnknownHostException
import java.util.concurrent.TimeUnit
import kotlin.math.abs
import kotlin.math.ceil

/**
 * Ağ istisnalarını dile bağımsız kısa bir koda çevirir. Kod, sonuçla birlikte
 * saklanır ve gösterim sırasında geçerli dile çevrilir (bkz. Strings.errorLabel).
 */
internal fun Throwable.kind(): String = when (this) {
    is SocketTimeoutException -> "timeout"
    is ConnectException -> "conn_refused"
    is NoRouteToHostException -> "no_route"
    is PortUnreachableException -> "port_unreachable"
    is UnknownHostException -> "dns_fail"
    else -> this.javaClass.simpleName + (message?.let { ": $it" } ?: "")
}

/** Örneklem listesinden min/avg/max/jitter üretir ve durumu belirler. */
internal fun buildResult(
    method: String,
    sent: Int,
    samples: List<Double>,
    errors: List<String>,
    resolvedIp: String?,
    unsupported: Boolean = false
): TestResult {
    val received = samples.size
    val status = when {
        unsupported -> Status.UNSUPPORTED
        received > 0 -> Status.UP
        errors.any { it == "timeout" } -> Status.TIMEOUT
        errors.isNotEmpty() -> Status.DOWN
        else -> Status.DOWN
    }
    val min = samples.minOrNull() ?: 0.0
    val max = samples.maxOrNull() ?: 0.0
    val avg = if (samples.isEmpty()) 0.0 else samples.average()
    val jitter = if (samples.size < 2) 0.0 else {
        var sum = 0.0
        for (i in 1 until samples.size) sum += abs(samples[i] - samples[i - 1])
        sum / (samples.size - 1)
    }
    return TestResult(
        status = status,
        method = method,
        sent = sent,
        received = received,
        minMs = min,
        avgMs = avg,
        maxMs = max,
        jitterMs = jitter,
        samples = samples,
        resolvedIp = resolvedIp,
        error = errors.firstOrNull(),
        timestamp = System.currentTimeMillis()
    )
}

/** TCP el sıkışma testi — root gerektirmez, en güvenilir yöntem. */
object TcpTester {
    fun test(host: String, port: Int, count: Int, timeoutMs: Int): TestResult {
        val samples = ArrayList<Double>()
        val errors = ArrayList<String>()
        var resolved: String? = null
        val n = count.coerceAtLeast(1)
        for (i in 0 until n) {
            val t0 = System.nanoTime()
            try {
                Socket().use { s ->
                    s.tcpNoDelay = true
                    s.connect(InetSocketAddress(host, port), timeoutMs)
                    resolved = s.inetAddress?.hostAddress ?: resolved
                }
                samples.add((System.nanoTime() - t0) / 1_000_000.0)
            } catch (e: Exception) {
                errors.add(e.kind())
            }
        }
        return buildResult("TCP:$port", n, samples, errors, resolved)
    }
}

/**
 * ICMP ping — /system/bin/ping üzerinden, root gerektirmez.
 * Bazı cihaz/sürümlerde engellenebilir; bu durumda DESTEKLENMİYOR döner.
 */
object IcmpTester {
    private val timeRe = Regex("time[=<]([0-9]+(?:\\.[0-9]+)?)\\s*ms")
    private val statRe = Regex("(\\d+)\\s+packets transmitted,?\\s+(\\d+)\\s+(?:packets\\s+)?received")

    fun test(host: String, count: Int, timeoutMs: Int): TestResult {
        val n = count.coerceAtLeast(1)
        val perReply = ceil(timeoutMs / 1000.0).toInt().coerceAtLeast(1)
        val cmd = listOf(
            "/system/bin/ping", "-c", n.toString(),
            "-i", "0.3",
            "-W", perReply.toString(),
            host
        )
        return try {
            val p = ProcessBuilder(cmd).redirectErrorStream(true).start()
            val out = p.inputStream.bufferedReader().readText()
            val finished = p.waitFor(perReply.toLong() * n + 4000, TimeUnit.MILLISECONDS)
            if (!finished) p.destroy()

            val blocked = out.contains("not found", true) ||
                out.contains("permission denied", true) ||
                out.contains("operation not permitted", true) ||
                out.contains("not permitted", true)
            if (blocked) {
                return TestResult(
                    status = Status.UNSUPPORTED,
                    method = "Ping",
                    error = "ping_blocked",
                    timestamp = System.currentTimeMillis()
                )
            }

            val samples = timeRe.findAll(out).mapNotNull { it.groupValues[1].toDoubleOrNull() }.toList()
            val stat = statRe.find(out)
            val usedSent = stat?.groupValues?.get(1)?.toIntOrNull() ?: n
            val usedRecv = stat?.groupValues?.get(2)?.toIntOrNull() ?: samples.size

            if (samples.isEmpty() && usedRecv == 0) {
                val timeoutish = out.contains("100% packet loss") || out.contains("0 received")
                val status = if (timeoutish) Status.TIMEOUT else Status.DOWN
                return TestResult(status, "Ping", usedSent, 0, error = out.lineSequence().lastOrNull { it.isNotBlank() }?.take(120), timestamp = System.currentTimeMillis())
            }
            // Örneklem yoksa ama alındı varsa ortalamayı bilmiyoruz: sent/recv ile UP say.
            if (samples.isEmpty()) {
                return buildResult("Ping", usedSent, List(usedRecv) { 0.0 }, emptyList(), host)
            }
            return buildResult("Ping", usedSent, samples, emptyList(), host).copy(received = usedRecv)
        } catch (e: Exception) {
            TestResult(
                status = Status.UNSUPPORTED,
                method = "Ping",
                error = "ping_failed",
                timestamp = System.currentTimeMillis()
            )
        }
    }
}

/** DNS sunucusuna gerçek sorgu gönderip yanıt süresini ölçer. */
object DnsTester {
    fun test(server: String, name: String, count: Int, timeoutMs: Int): TestResult {
        val samples = ArrayList<Double>()
        val errors = ArrayList<String>()
        val n = count.coerceAtLeast(1)
        for (i in 0 until n) {
            try {
                val a = DnsWire.query(server, name, timeoutMs)
                samples.add(a.rttMs)
            } catch (e: Exception) {
                errors.add(e.kind())
            }
        }
        // Coğrafi konum için temas edilen IP, çözümleyicinin kendisidir.
        return buildResult("DNS:53", n, samples, errors, server)
    }
}

/** HTTPS/HTTP HEAD isteği ile gerçek dünya gecikmesini ölçer. */
object HttpTester {
    fun test(host: String, port: Int?, url: String?, count: Int, timeoutMs: Int): TestResult {
        val target = url?.takeIf { it.startsWith("http") } ?: buildUrl(host, port)
        val samples = ArrayList<Double>()
        val errors = ArrayList<String>()
        var resolved: String? = null
        val n = count.coerceAtLeast(1)
        for (i in 0 until n) {
            var conn: HttpURLConnection? = null
            val t0 = System.nanoTime()
            try {
                conn = (URI(target).toURL().openConnection() as HttpURLConnection).apply {
                    requestMethod = "HEAD"
                    connectTimeout = timeoutMs
                    readTimeout = timeoutMs
                    instanceFollowRedirects = true
                    setRequestProperty("User-Agent", "PingTest/1.0")
                }
                conn.responseCode // yanıt geldi = erişilebilir
                samples.add((System.nanoTime() - t0) / 1_000_000.0)
                resolved = try {
                    InetAddress.getByName(conn.url.host).hostAddress
                } catch (_: Exception) {
                    resolved
                }
            } catch (e: Exception) {
                errors.add(e.kind())
            } finally {
                conn?.disconnect()
            }
        }
        return buildResult("HTTP", n, samples, errors, resolved)
    }

    fun buildUrl(host: String, port: Int?): String {
        val scheme = if (port == 80) "http" else "https"
        val p = if (port != null && port != 80 && port != 443) ":$port" else ""
        return "$scheme://$host$p/"
    }
}
