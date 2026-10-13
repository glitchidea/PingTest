package com.pingtest.app.ui

import androidx.compose.runtime.staticCompositionLocalOf
import com.pingtest.app.model.Filter
import com.pingtest.app.model.SortMode
import com.pingtest.app.model.Status
import com.pingtest.app.model.TestMethod
import com.pingtest.app.net.Transport

/**
 * Uygulamada desteklenen diller. Varsayılan Türkçe'dir; kullanıcı Ayarlar'dan
 * anında değiştirebilir (uygulama yeniden başlatılmaz).
 */
enum class AppLanguage(val code: String, val nativeName: String) {
    TR("tr", "Türkçe"),
    EN("en", "English");

    companion object {
        fun fromCode(code: String?): AppLanguage =
            entries.firstOrNull { it.code.equals(code, ignoreCase = true) } ?: TR
    }
}

/**
 * Tüm arayüz metinlerinin tek kaynağı. `language == EN` ise İngilizce, aksi
 * halde Türkçe metin döndürür. Arayüz bu sınıfı [LocalStrings] üzerinden okur.
 */
class Strings(val language: AppLanguage) {

    private val en = language == AppLanguage.EN
    private fun tr(tr: String, en: String) = if (this.en) en else tr

    // ---- Marka / üst bar ----
    val appName = "PingTest"
    val shareReport = tr("Raporu paylaş", "Share report")
    val menusTemplatesDesc = tr("Menüler ve şablonlar", "Menus and templates")
    val searchDesc = tr("Ara", "Search")
    val settingsDesc = tr("Ayarlar", "Settings")
    val addTargetDesc = tr("Hedef ekle", "Add target")

    // ---- Bağlantı / VPN ----
    val connection = tr("Bağlantı", "Connection")
    val vpnOnShort = tr("VPN açık", "VPN on")
    val vpnOn = tr("VPN: AÇIK", "VPN: ON")
    val vpnOff = tr("VPN: kapalı", "VPN: off")
    val vpnWarning = tr(
        "VPN açıkken ölçülen gecikme hedefin değil VPN yolunun değeridir.",
        "While a VPN is on, the measured latency is that of the VPN path, not the target."
    )

    fun transportLabel(t: Transport): String = when (t) {
        Transport.WIFI -> "Wi-Fi"
        Transport.CELLULAR -> tr("Mobil", "Mobile")
        Transport.VPN -> "VPN"
        Transport.NONE -> tr("Yok", "None")
        Transport.UNKNOWN -> tr("Bilinmiyor", "Unknown")
    }

    // ---- Enum etiketleri ----
    fun methodLabel(m: TestMethod): String = when (m) {
        TestMethod.AUTO -> tr("Oto", "Auto")
        TestMethod.TCP -> "TCP"
        TestMethod.ICMP -> "Ping"
        TestMethod.DNS -> "DNS"
        TestMethod.HTTP -> "HTTP"
    }

    fun statusLabel(s: Status): String = when (s) {
        Status.UNTESTED -> tr("Bekliyor", "Pending")
        Status.TESTING -> tr("Test ediliyor", "Testing")
        Status.UP -> tr("Çalışıyor", "Up")
        Status.DOWN -> tr("Kapalı", "Down")
        Status.TIMEOUT -> tr("Zaman aşımı", "Timeout")
        Status.ERROR -> tr("Hata", "Error")
        Status.UNSUPPORTED -> tr("Desteklenmiyor", "Unsupported")
    }

    fun filterLabel(f: Filter): String = when (f) {
        Filter.ALL -> tr("Tümü", "All")
        Filter.UP -> tr("Çalışan", "Up")
        Filter.DOWN -> tr("Çalışmayan", "Down")
        Filter.UNTESTED -> tr("Test edilmedi", "Untested")
    }

    fun sortLabel(s: SortMode): String = when (s) {
        SortMode.DEFAULT -> tr("Gruplu (ülke)", "Grouped (country)")
        SortMode.LATENCY -> tr("Gecikme", "Latency")
        SortMode.NAME -> tr("İsim", "Name")
        SortMode.COUNTRY -> tr("Ülke", "Country")
        SortMode.STATUS -> tr("Durum", "Status")
    }

    // ---- Sayaçlar / liste ----
    fun collapseAll() = tr("Tümünü kapat", "Collapse all")
    fun expandAll() = tr("Tümünü aç", "Expand all")
    fun noMatch() = tr(
        "Bu filtreye uyan hedef yok.\nFiltreyi veya aramayı değiştir.",
        "No targets match this filter.\nChange the filter or search."
    )

    fun upCount(n: Int) = tr("$n çalışan", "$n up")
    fun downCount(n: Int) = tr("$n çalışmayan", "$n down")
    fun pendingCount(n: Int) = tr("$n bekleyen", "$n pending")
    val runningBackground = tr("Arka planda sürüyor…", "Running in background…")
    val testAll = tr("Tümünü Test Et", "Test All")
    val stop = tr("Durdur", "Stop")
    val clear = tr("Temizle", "Clear")
    val screenOffHint = tr(
        "Ekranı kapatabilirsin — test arka planda devam eder.",
        "You can turn off the screen — the test keeps running in the background."
    )
    val progressLabel = tr("İlerleme", "Progress")

    // ---- Boş durum ----
    val noTargetsTitle = tr("Henüz hedef yok", "No targets yet")
    val noTargetsDesc = tr(
        "IP / DNS / site ekle, test et; hangisinin çalıştığını, hangi ülkede olduğunu ve kaç ms gecikmeyle yanıt verdiğini gör.",
        "Add an IP / DNS / site and test it; see which one works, which country it is in, and how many ms it responds with."
    )
    val emptyInternetBtn = tr(
        "🌐 İnternet testi (arama + tarayıcı + site)",
        "🌐 Internet test (search + browsers + sites)"
    )
    val emptyMenusBtn = tr("📚 Menüler & şablonlar (VPS dâhil)", "📚 Menus & templates (incl. VPS)")

    // ---- Hedef kartı ----
    val cardMenuDesc = tr("Menü", "Menu")
    val testOne = tr("Test et", "Test")
    val edit = tr("Düzenle", "Edit")
    val duplicate = tr("Kopyala", "Duplicate")
    val delete = tr("Sil", "Delete")
    val notTestedYet = tr("henüz test edilmedi", "not tested yet")
    fun lossJitter(loss: Int, jitter: Int) = tr("kayıp %$loss · jitter $jitter ms", "loss $loss% · jitter $jitter ms")

    // ---- Grup başlığı ----
    val expandGroup = tr("Grubu aç", "Expand group")
    val collapseGroup = tr("Grubu kapat", "Collapse group")
    val testGroup = tr("Bu grubu test et", "Test this group")
    val testGroupShort = tr("Grubu test et", "Test group")
    val deleteGroup = tr("Grubu sil", "Delete group")

    // ---- Ekle / Düzenle ----
    val addTitle = tr("Hedef ekle", "Add target")
    val editTitle = tr("Hedefi düzenle", "Edit target")
    val labelField = tr("Etiket (örn. Cloudflare)", "Label (e.g. Cloudflare)")
    val hostField = tr("IP veya alan adı (zorunlu)", "IP or hostname (required)")
    val hostPlaceholder = tr("1.1.1.1 veya vpn.example.com", "1.1.1.1 or vpn.example.com")
    val portField = tr("Port (opsiyonel)", "Port (optional)")
    val portPlaceholder = "443 / 53 / 1194 ..."
    val testMethodField = tr("Test yöntemi", "Test method")
    fun methodDesc(m: TestMethod): String = when (m) {
        TestMethod.AUTO -> tr(
            "Otomatik: TCP 443 → TCP 80 → Ping → (IP ise) DNS sırayla denenir.",
            "Auto: tries TCP 443 → TCP 80 → Ping → (if IP) DNS in order."
        )
        TestMethod.TCP -> tr(
            "TCP el sıkışması ölçülür (root gerekmez, en güvenilir).",
            "Measures a TCP handshake (no root needed, most reliable)."
        )
        TestMethod.ICMP -> tr(
            "Root'suz ping. Bazı cihaz/sürümlerde engellenebilir.",
            "Ping without root. May be blocked on some devices/versions."
        )
        TestMethod.DNS -> tr(
            "Sunucuya gerçek DNS sorgusu gönderilir (port 53).",
            "Sends a real DNS query to the server (port 53)."
        )
        TestMethod.HTTP -> tr(
            "HTTPS/HTTP HEAD isteği ile gecikme ölçülür.",
            "Measures latency with an HTTPS/HTTP HEAD request."
        )
    }
    val urlField = tr("URL (opsiyonel)", "URL (optional)")
    val urlPlaceholder = "https://example.com/health"
    val dnsNameField = tr("Sorgulanacak ad (opsiyonel)", "Name to query (optional)")
    val dnsNamePlaceholder = "example.com"
    val groupField = tr("Grup (opsiyonel, örn. VPN)", "Group (optional, e.g. VPN)")
    val cancel = tr("İptal", "Cancel")
    val save = tr("Kaydet", "Save")
    val saveAndTest = tr("Kaydet ve hemen test et", "Save and test now")

    // ---- İçe aktar ----
    val importTitle = tr("Toplu içe aktar", "Bulk import")
    val importHelp = tr(
        "Her satıra bir adres. Örnekler:\n" +
            "• 8.8.8.8\n" +
            "• 1.2.3.4:443\n" +
            "• 1.1.1.1 , Cloudflare   (etiket)\n" +
            "• dns://9.9.9.9\n" +
            "• tcp://vpn.example.com:1194\n" +
            "• https://example.com",
        "One address per line. Examples:\n" +
            "• 8.8.8.8\n" +
            "• 1.2.3.4:443\n" +
            "• 1.1.1.1 , Cloudflare   (label)\n" +
            "• dns://9.9.9.9\n" +
            "• tcp://vpn.example.com:1194\n" +
            "• https://example.com"
    )
    val allCountriesBtn = tr("🌍 Tüm ülkeler (~730)", "🌍 All countries (~730)")
    val internetTestShort = tr("🌐 İnternet test et", "🌐 Test internet")
    val dnsListBtn = tr("DNS listesi", "DNS list")
    val generalListBtn = tr("Genel liste", "General list")
    val addressesField = tr("Adresler", "Addresses")
    val addressesPlaceholder = "8.8.8.8\n1.1.1.1\n9.9.9.9"
    val waitingValid = tr("Geçerli satır bekleniyor…", "Waiting for a valid line…")
    fun foundValid(n: Int) = tr("$n geçerli hedef bulundu", "$n valid target(s) found")
    val addAndTest = tr("Ekle ve test et", "Add and test")

    // ---- Detay ----
    val retestDesc = tr("Tekrar test et", "Test again")
    val detailStatus = tr("Durum", "Status")
    val detailMethod = tr("Yöntem", "Method")
    val detailAverage = tr("Ortalama", "Average")
    val detailMinMax = tr("Min / Maks", "Min / Max")
    val detailJitter = "Jitter"
    val detailPacket = tr("Paket", "Packets")
    fun packetLoss(received: Int, sent: Int, pct: Int) = tr(
        "$received/$sent (kayıp %$pct)",
        "$received/$sent (loss $pct%)"
    )
    val detailResolvedIp = tr("Çözümlenen IP", "Resolved IP")
    val detailCountry = tr("Ülke", "Country")
    val detailProvider = tr("Sağlayıcı", "Provider")
    val detailLastTest = tr("Son test", "Last test")
    val detailError = tr("Hata", "Error")
    val detailSamples = tr("Ölçümler", "Samples")
    val close = tr("Kapat", "Close")
    val copy = tr("Kopyala", "Copy")

    // ---- Ayarlar ----
    val settingsTitle = tr("Ayarlar", "Settings")
    val packetCount = tr("Paket sayısı (her hedef için)", "Packet count (per target)")
    val timeoutLabel = tr("Zaman aşımı", "Timeout")
    val concurrencyLabel = tr("Eşzamanlılık", "Concurrency")
    val autoRefreshLabel = tr("Otomatik yenileme", "Auto refresh")
    val offLabel = tr("Kapalı", "Off")
    val s30 = tr("30 sn", "30 s")
    val m1 = tr("1 dk", "1 min")
    val m5 = tr("5 dk", "5 min")
    fun seconds(n: Int) = tr("$n sn", "$n s")
    val geoTitle = tr("Ülke / sağlayıcı bilgisi", "Country / provider info")
    val geoSubtitle = tr(
        "ipwho.is üzerinden IP konumu getirir (internet gerekir)",
        "Fetches IP location via ipwho.is (requires internet)"
    )
    val dnsQueryField = tr("DNS testlerinde sorgulanacak ad", "Name to query in DNS tests")
    val clearResults = tr("Sonuçları temizle", "Clear results")
    val deleteAll = tr("Tümünü sil", "Delete all")
    val languageLabel = tr("Dil", "Language")

    // ---- Menüler & şablonlar ----
    val menuTitle = tr("Menüler & Şablonlar", "Menus & Templates")
    val menuDesc = tr(
        "Bir şablonu doğrudan test edebilir ya da kendi menünü oluşturup içine koyabilirsin.",
        "You can test a template directly, or create your own menu and put the template into it."
    )
    val searchTemplateField = tr("Şablon ara (ülke, VPS…)", "Search templates (country, VPS…)")
    fun readyTemplates(shown: Int, total: Int) = tr("Hazır şablonlar ($shown/$total)", "Ready templates ($shown/$total)")
    fun targetsCount(n: Int) = tr("$n hedef", "$n target(s)")
    val makeMenu = tr("Menü yap", "Make menu")
    val allCountries = tr("🌍 Tüm ülkeler", "🌍 All countries")
    val bulkImport = tr("📥 Toplu içe aktar", "📥 Bulk import")
    val groupsHeader = tr("Gruplar / Menülerim", "Groups / My menus")
    val noGroupsYet = tr("Henüz grup yok. Yukarıdan bir şablon ekle.", "No groups yet. Add a template above.")
    val createMenuTitle = tr("Menü oluştur", "Create menu")
    val createMenuDesc = tr("Bu şablon yeni bir menüye eklenecek:", "This template will be added to a new menu:")
    val menuNameField = tr("Menü adı", "Menu name")
    val create = tr("Oluştur", "Create")

    // ---- İçe aktarma / durum mesajları ----
    val msgNoNewTargets = tr("Yeni hedef yok (tümü zaten listede)", "No new targets (all already in the list)")
    fun msgAdded(n: Int) = tr("$n hedef eklendi", "$n target(s) added")
    fun msgMenuExists(name: String) = tr(
        "'$name' menüsündeki hedefler zaten listede",
        "Targets in '$name' are already in the list"
    )
    fun msgMenuCreated(name: String, n: Int) = tr("'$name' menüsü oluşturuldu ($n hedef)", "Menu '$name' created ($n targets)")
    fun msgGroupDeleted(name: String, n: Int) = tr("'$name' silindi ($n hedef)", "'$name' deleted ($n targets)")
    val msgAllDeleted = tr("Tüm hedefler silindi", "All targets deleted")
    val msgNothingToTest = tr("Test edilecek hedef yok", "No targets to test")
    val msgStopped = tr("Test durduruldu — sonuçlar kaydedildi", "Test stopped — results saved")
    val msgCountriesNotFound = tr("Ülke listesi bulunamadı", "Country list not found")
    val copySuffix = tr(" (kopya)", " (copy)")
    val otherGroup = tr("Diğer", "Other")
    val localNetwork = tr("🏠 Yerel ağ", "🏠 Local network")

    // ---- Hata (test sonucu) etiketleri ----
    fun errorLabel(code: String?): String? = when (code) {
        null -> null
        "timeout" -> tr("zaman aşımı", "timeout")
        "conn_refused" -> tr("bağlantı reddedildi", "connection refused")
        "no_route" -> tr("yol yok", "no route to host")
        "port_unreachable" -> tr("port ulaşılamaz", "port unreachable")
        "dns_fail" -> tr("ad çözümlenemedi", "name not resolved")
        "ping_blocked" -> tr(
            "ping yürütülemedi (cihaz izin vermiyor olabilir)",
            "ping could not run (device may block it)"
        )
        "ping_failed" -> tr("ping yürütülemedi", "ping could not run")
        else -> legacyError(code) ?: code
    }

    // Eski (Türkçe) kayıtlı sonuçların hata metinlerini de çevirelim.
    private fun legacyError(code: String): String? = if (!en) code else when (code) {
        "zaman aşımı" -> "timeout"
        "bağlantı reddedildi" -> "connection refused"
        "yol yok" -> "no route to host"
        "port ulaşılamaz" -> "port unreachable"
        "ad çözümlenemedi" -> "name not resolved"
        else -> null
    }

    // ---- Dışa aktarma raporu ----
    val reportTitle = tr("PingTest raporu", "PingTest report")
    fun reportConnection(transport: String, vpn: Boolean) = tr(
        "Bağlantı: $transport · VPN: ${if (vpn) "AÇIK" else "kapalı"}",
        "Connection: $transport · VPN: ${if (vpn) "ON" else "off"}"
    )
    fun reportTotals(total: Int, up: Int, down: Int, pending: Int) = tr(
        "Toplam $total · Çalışan $up · Çalışmayan $down · Bekleyen $pending",
        "Total $total · Up $up · Down $down · Pending $pending"
    )
    val reportLoss = tr("Kayıp", "Loss")

    // ---- Arka plan servisi bildirimi ----
    val channelName = tr("Test çalışması", "Test run")
    val channelDesc = tr("Arka planda süren bağlantı testleri", "Connection tests running in the background")
    val notifTitle = tr("PingTest çalışıyor", "PingTest is running")
    fun notifProgress(done: Int, total: Int) = tr("$done/$total hedef test edildi", "$done/$total targets tested")
    val notifStarting = tr("Başlatılıyor…", "Starting…")

    // ---- Geliştirici / marka ----
    val developerLabel = tr("Geliştirici", "Developer")
    val developerName = "Glitchİdea"
    val developerTagline = tr("DevSecOps & Uygulama Güvenliği", "DevSecOps & Application Security")
    val visitWebsite = tr("Web sitesini aç", "Open website")
    val websiteUrl = "https://glitchidea.com"
    val websiteLabel = "glitchidea.com"
    fun reportFooter() = tr(
        "PingTest · Glitchİdea tarafından geliştirildi · glitchidea.com",
        "PingTest · developed by Glitchİdea · glitchidea.com"
    )

    // ---- Şablon ve grup adları (içerik çevirisi) ----
    fun templateLabel(key: String, fallback: String): String = when (key) {
        "internet" -> tr("🌐 İnternet testi (arama + tarayıcı + siteler)", "🌐 Internet test (search + browsers + sites)")
        "search" -> tr("🔎 Arama motorları", "🔎 Search engines")
        "browsers" -> tr("🧭 Tarayıcılar", "🧭 Browsers")
        "sites" -> tr("⭐ Popüler siteler", "⭐ Popular sites")
        "vps" -> tr("🗄️ Tüm VPS sağlayıcıları (116 ülke)", "🗄️ All VPS providers (116 countries)")
        "dns" -> tr("📡 DNS sunucuları", "📡 DNS servers")
        "general" -> tr("⚙️ Genel adresler", "⚙️ General addresses")
        else -> fallback
    }

    /** Hazır grup/menü adlarını (kayıtlı Türkçe anahtar) görüntü için çevirir. */
    fun groupLabel(name: String): String = when (name) {
        "Arama motorları" -> tr("Arama motorları", "Search engines")
        "Tarayıcılar" -> tr("Tarayıcılar", "Browsers")
        "Popüler siteler" -> tr("Popüler siteler", "Popular sites")
        "DNS" -> "DNS"
        "Genel" -> tr("Genel", "General")
        else -> name
    }
}

/** Arayüz ağacına geçerli dil metinlerini taşıyan CompositionLocal. */
val LocalStrings = staticCompositionLocalOf { Strings(AppLanguage.TR) }
