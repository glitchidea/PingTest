package com.pingtest.app.data

import android.content.Context
import com.pingtest.app.model.GeoInfo
import com.pingtest.app.model.GeoStatus
import com.pingtest.app.model.Status
import com.pingtest.app.model.Target
import com.pingtest.app.model.TargetRow
import com.pingtest.app.model.TestMethod
import com.pingtest.app.model.TestResult
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

/**
 * Hedefleri **ve test sonuçlarını** kalıcı olarak saklar (JSON, bağımlılıksız).
 * Böylece uygulama kapansa/ölse de çalışan/çalışmayan sonuçları kaybolmaz.
 */
class TargetStore(context: Context) {

    private val file = File(context.filesDir, "pingtest_data.json")
    private val legacyFile = File(context.filesDir, "pingtest_targets.json")

    fun load(): List<TargetRow> {
        val f = when {
            file.exists() -> file
            legacyFile.exists() -> legacyFile
            else -> return emptyList()
        }
        return try {
            val arr = JSONArray(f.readText())
            buildList {
                for (i in 0 until arr.length()) {
                    val o = arr.getJSONObject(i)
                    val host = o.optString("host").trim()
                    if (host.isEmpty()) continue
                    val target = Target(
                        id = o.optString("id").ifBlank { java.util.UUID.randomUUID().toString() },
                        label = o.optString("label").ifBlank { host },
                        host = host,
                        port = if (o.has("port") && !o.isNull("port")) o.optInt("port") else null,
                        method = TestMethod.fromName(o.optString("method")),
                        group = o.optString("group", ""),
                        url = o.optString("url", "").ifBlank { null },
                        dnsName = o.optString("dnsName", "").ifBlank { null },
                        enabled = o.optBoolean("enabled", true)
                    )
                    val result = o.optJSONObject("result")?.let { parseResult(it) } ?: TestResult()
                    val geo = o.optJSONObject("geo")?.let { parseGeo(it) }
                    add(TargetRow(target, result, geo))
                }
            }
        } catch (_: Exception) {
            emptyList()
        }
    }

    fun save(rows: List<TargetRow>) {
        try {
            val arr = JSONArray()
            for (r in rows) {
                val t = r.target
                val o = JSONObject().apply {
                    put("id", t.id)
                    put("label", t.label)
                    put("host", t.host)
                    if (t.port != null) put("port", t.port) else put("port", JSONObject.NULL)
                    put("method", t.method.name)
                    put("group", t.group)
                    put("url", t.url ?: "")
                    put("dnsName", t.dnsName ?: "")
                    put("enabled", t.enabled)
                    put("result", resultToJson(r.result))
                    r.geo?.let { put("geo", geoToJson(it)) }
                }
                arr.put(o)
            }
            file.writeText(arr.toString())
        } catch (_: Exception) {
            // Depolama hatası uygulamayı çökertmesin.
        }
    }

    // ---- serileştirme yardımcıları ----

    private fun resultToJson(r: TestResult): JSONObject = JSONObject().apply {
        put("status", r.status.name)
        put("method", r.method)
        put("sent", r.sent)
        put("received", r.received)
        put("minMs", r.minMs)
        put("avgMs", r.avgMs)
        put("maxMs", r.maxMs)
        put("jitterMs", r.jitterMs)
        put("resolvedIp", r.resolvedIp ?: "")
        put("error", r.error ?: "")
        put("timestamp", r.timestamp)
        val samples = JSONArray()
        // Dosya şişmesin diye en fazla 16 örnek sakla
        r.samples.take(16).forEach { samples.put(it) }
        put("samples", samples)
    }

    private fun parseResult(o: JSONObject): TestResult {
        val samples = ArrayList<Double>()
        o.optJSONArray("samples")?.let { a ->
            for (i in 0 until a.length()) samples.add(a.optDouble(i))
        }
        return TestResult(
            status = runCatching { Status.valueOf(o.optString("status", "UNTESTED")) }.getOrDefault(Status.UNTESTED),
            method = o.optString("method", ""),
            sent = o.optInt("sent", 0),
            received = o.optInt("received", 0),
            minMs = o.optDouble("minMs", 0.0),
            avgMs = o.optDouble("avgMs", 0.0),
            maxMs = o.optDouble("maxMs", 0.0),
            jitterMs = o.optDouble("jitterMs", 0.0),
            samples = samples,
            resolvedIp = o.optString("resolvedIp", "").ifBlank { null },
            error = o.optString("error", "").ifBlank { null },
            timestamp = o.optLong("timestamp", 0L)
        )
    }

    private fun geoToJson(g: GeoInfo): JSONObject = JSONObject().apply {
        put("ip", g.ip)
        put("country", g.country ?: "")
        put("countryCode", g.countryCode ?: "")
        put("city", g.city ?: "")
        put("isp", g.isp ?: "")
        put("asn", g.asn ?: "")
        put("isPrivate", g.isPrivate)
        put("status", g.status.name)
    }

    private fun parseGeo(o: JSONObject): GeoInfo = GeoInfo(
        ip = o.optString("ip", ""),
        country = o.optString("country", "").ifBlank { null },
        countryCode = o.optString("countryCode", "").ifBlank { null },
        city = o.optString("city", "").ifBlank { null },
        isp = o.optString("isp", "").ifBlank { null },
        asn = o.optString("asn", "").ifBlank { null },
        isPrivate = o.optBoolean("isPrivate", false),
        status = runCatching { GeoStatus.valueOf(o.optString("status", "NONE")) }.getOrDefault(GeoStatus.NONE)
    )
}
