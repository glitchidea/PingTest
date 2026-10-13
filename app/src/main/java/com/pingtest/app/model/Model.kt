package com.pingtest.app.model

import java.util.UUID

/** Bir hedefte kullanılacak test yöntemi. AUTO = otomatik seçim. */
enum class TestMethod(val label: String) {
    AUTO("Oto"),
    TCP("TCP"),
    ICMP("Ping"),
    DNS("DNS"),
    HTTP("HTTP");

    companion object {
        fun fromName(n: String?): TestMethod = entries.firstOrNull { it.name == n } ?: AUTO
    }
}

/** Test sonucu durumu. */
enum class Status(val label: String) {
    UNTESTED("Bekliyor"),
    TESTING("Test ediliyor"),
    UP("Çalışıyor"),
    DOWN("Kapalı"),
    TIMEOUT("Zaman aşımı"),
    ERROR("Hata"),
    UNSUPPORTED("Desteklenmiyor");
}

/** Listeleme filtresi. */
enum class Filter(val label: String) {
    ALL("Tümü"),
    UP("Çalışan"),
    DOWN("Çalışmayan"),
    UNTESTED("Test edilmedi");
}

/** Sıralama ölçütü. */
enum class SortMode(val label: String) {
    DEFAULT("Gruplu (ülke)"),
    LATENCY("Gecikme"),
    NAME("İsim"),
    COUNTRY("Ülke"),
    STATUS("Durum");
}

/**
 * Test edilecek hedef. host bir IP ya da alan adı olabilir.
 * port null ise yönteme göre varsayılan kullanılır (TCP/HTTP için 443, DNS için 53).
 */
data class Target(
    val id: String = UUID.randomUUID().toString(),
    val label: String,
    val host: String,
    val port: Int? = null,
    val method: TestMethod = TestMethod.AUTO,
    val group: String = "",
    val url: String? = null,
    val dnsName: String? = null,
    val enabled: Boolean = true
)

/** IP coğrafi konum / sağlayıcı bilgisi. */
enum class GeoStatus { NONE, OK, FAILED }

data class GeoInfo(
    val ip: String,
    val country: String? = null,
    val countryCode: String? = null,
    val city: String? = null,
    val isp: String? = null,
    val asn: String? = null,
    val isPrivate: Boolean = false,
    val status: GeoStatus = GeoStatus.NONE
)

/** Tek bir testin sonucu. */
data class TestResult(
    val status: Status = Status.UNTESTED,
    val method: String = "",
    val sent: Int = 0,
    val received: Int = 0,
    val minMs: Double = 0.0,
    val avgMs: Double = 0.0,
    val maxMs: Double = 0.0,
    val jitterMs: Double = 0.0,
    val samples: List<Double> = emptyList(),
    val resolvedIp: String? = null,
    val error: String? = null,
    val timestamp: Long = 0L
) {
    val lossPct: Int get() = if (sent <= 0) 0 else ((sent - received).coerceAtLeast(0) * 100) / sent
    val reachable: Boolean get() = status == Status.UP
}

/** UI'da gösterilen satır: hedef + son sonuç + coğrafi bilgi. */
data class TargetRow(
    val target: Target,
    val result: TestResult = TestResult(),
    val geo: GeoInfo? = null
)

/** Uygulama ayarları. */
data class AppSettings(
    val count: Int = 4,
    val timeoutMs: Int = 3000,
    val concurrency: Int = 8,
    val dnsQueryName: String = "example.com",
    val geoEnabled: Boolean = true,
    val autoRefreshSec: Int = 0
)
