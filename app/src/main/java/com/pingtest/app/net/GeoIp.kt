package com.pingtest.app.net

import com.pingtest.app.model.GeoInfo
import com.pingtest.app.model.GeoStatus
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.ConcurrentHashMap

/**
 * IP → ülke / şehir / sağlayıcı bilgisi. ipwho.is (HTTPS, anahtar gerekmez) kullanır.
 * Sonuçlar bellekte önbelleğe alınır.
 */
object GeoIp {

    private val cache = ConcurrentHashMap<String, GeoInfo>()

    fun isPrivate(ip: String): Boolean {
        if (ip.isEmpty()) return true
        if (ip.contains(':')) {
            val l = ip.lowercase()
            return l == "::1" || l.startsWith("fe80") || l.startsWith("fc") || l.startsWith("fd")
        }
        val p = ip.split('.').mapNotNull { it.toIntOrNull() }
        if (p.size != 4) return true
        val (a, b) = p
        return a == 0 || a == 10 || a == 127 ||
            (a == 192 && b == 168) ||
            (a == 172 && b in 16..31) ||
            (a == 169 && b == 254) ||
            (a == 100 && b in 64..127)
    }

    fun lookup(ip: String): GeoInfo {
        cache[ip]?.let { return it }
        if (isPrivate(ip)) {
            return GeoInfo(ip = ip, isPrivate = true, status = GeoStatus.OK).also { cache[ip] = it }
        }
        val info = try {
            val conn = (URL("https://ipwho.is/$ip").openConnection() as HttpURLConnection).apply {
                connectTimeout = 5000
                readTimeout = 5000
                setRequestProperty("User-Agent", "PingTest/1.0")
            }
            val body = conn.inputStream.bufferedReader().use { it.readText() }
            conn.disconnect()
            val o = JSONObject(body)
            if (!o.optBoolean("success", false)) {
                GeoInfo(ip = ip, status = GeoStatus.FAILED)
            } else {
                val c = o.optJSONObject("connection")
                GeoInfo(
                    ip = ip,
                    country = o.optString("country").ifBlank { null },
                    countryCode = o.optString("country_code").ifBlank { null },
                    city = o.optString("city").ifBlank { null },
                    isp = c?.optString("isp")?.ifBlank { null },
                    asn = c?.optString("asn")?.toIntOrNull()?.let { "AS$it" },
                    status = GeoStatus.OK
                )
            }
        } catch (_: Exception) {
            GeoInfo(ip = ip, status = GeoStatus.FAILED)
        }
        cache[ip] = info
        return info
    }
}
