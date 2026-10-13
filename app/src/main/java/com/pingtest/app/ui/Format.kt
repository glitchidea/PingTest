package com.pingtest.app.ui

import androidx.compose.ui.graphics.Color
import com.pingtest.app.model.GeoInfo
import com.pingtest.app.model.Status
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

/** Ülke kodunu bayrak emojisine çevirir (ek dosya gerekmez). */
fun countryFlag(code: String?): String {
    if (code.isNullOrBlank() || code.length != 2) return "🌐"
    val cc = code.uppercase(Locale.ROOT)
    if (cc[0] !in 'A'..'Z' || cc[1] !in 'A'..'Z') return "🌐"
    val a = Character.toChars(0x1F1E6 + (cc[0] - 'A'))
    val b = Character.toChars(0x1F1E6 + (cc[1] - 'A'))
    return String(a) + String(b)
}

fun formatMs(ms: Double): String = if (ms <= 0.0) "—" else "${ms.roundToInt()} ms"

/** Konum satırı: bayrak + ülke · şehir. */
fun formatCountry(geo: GeoInfo?, strings: Strings = Strings(AppLanguage.TR)): String {
    if (geo == null) return ""
    if (geo.isPrivate) return strings.localNetwork
    val flag = countryFlag(geo.countryCode)
    val c = geo.country ?: geo.countryCode ?: return ""
    return if (!geo.city.isNullOrBlank()) "$flag $c · ${geo.city}" else "$flag $c"
}

fun formatIsp(geo: GeoInfo?): String {
    val isp = geo?.isp ?: return ""
    val asn = geo?.asn
    return if (!asn.isNullOrBlank()) "$isp ($asn)" else isp
}

private val timeFmt = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

fun formatTime(ts: Long): String = if (ts <= 0L) "—" else timeFmt.format(Date(ts))

/** Durum gruplama yardımcıları (filtre ve sayaçlar aynı mantığı kullansın). */
fun isDownStatus(s: Status): Boolean =
    s == Status.DOWN || s == Status.TIMEOUT || s == Status.ERROR || s == Status.UNSUPPORTED

fun isUntestedStatus(s: Status): Boolean =
    s == Status.UNTESTED || s == Status.TESTING

/** Duruma göre nokta rengi. */
fun statusColor(status: Status): Color = when (status) {
    Status.UP -> Color(0xFF22C55E)
    Status.DOWN -> Color(0xFFEF4444)
    Status.TIMEOUT -> Color(0xFFF59E0B)
    Status.ERROR -> Color(0xFFF97316)
    Status.UNSUPPORTED -> Color(0xFF94A3B8)
    Status.TESTING -> Color(0xFF3B82F6)
    Status.UNTESTED -> Color(0xFF64748B)
}
