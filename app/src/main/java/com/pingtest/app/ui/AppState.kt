package com.pingtest.app.ui

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.pingtest.app.data.TargetStore
import com.pingtest.app.model.AppSettings
import com.pingtest.app.model.Filter
import com.pingtest.app.model.GeoInfo
import com.pingtest.app.model.SortMode
import com.pingtest.app.model.Status
import com.pingtest.app.model.Target
import com.pingtest.app.model.TargetRow
import com.pingtest.app.model.TestResult
import com.pingtest.app.net.Engine
import com.pingtest.app.net.GeoIp
import com.pingtest.app.net.NetSnapshot
import com.pingtest.app.net.NetStateReader
import com.pingtest.app.net.Transport
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.coroutines.withContext
import java.util.UUID
import kotlin.math.roundToInt

/**
 * Uygulamanın süreç-geneli durum sahibi (singleton). Hem arayüz hem de arka plan
 * servisi (TestService) aynı örneği kullanır.
 *
 * Önemli: bir test çalışırken arama/filtre/sıralama/ekleme gibi arayüz işlemleri
 * çalışan işi ETKİLEMEZ; test hedef kimlikleri (id) üzerinden yürür ve UI
 * değişse de devam eder. Sonuçlar sürekli diske yazılır.
 */
class AppState private constructor(private val appContext: Context) {

    companion object {
        @Volatile
        private var instance: AppState? = null

        private const val PREFS = "pingtest_prefs"
        private const val KEY_LANGUAGE = "language"

        fun get(context: Context): AppState =
            instance ?: synchronized(this) {
                instance ?: AppState(context.applicationContext).also { instance = it }
            }
    }

    /** Sayaçlar: toplam / çalışan / çalışmayan / test edilmemiş. */
    data class Counts(val total: Int, val up: Int, val down: Int, val untested: Int)

    private val store = TargetStore(appContext)
    private val prefs = appContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    val rows = mutableStateListOf<TargetRow>()

    /** Seçili dil. Ayarlar'dan değiştirilir ve diske yazılır. */
    var language by mutableStateOf(AppLanguage.fromCode(prefs.getString(KEY_LANGUAGE, null)))
        private set

    /** Geçerli dile ait metinler. Dil değişince otomatik güncellenir. */
    val strings: Strings get() = Strings(language)

    var settings by mutableStateOf(AppSettings())
        private set
    var filter by mutableStateOf(Filter.ALL)
    var sort by mutableStateOf(SortMode.DEFAULT)
    var search by mutableStateOf("")
    var collapsed by mutableStateOf<Set<String>>(emptySet())
        private set

    var running by mutableStateOf(false)
        private set
    var done by mutableIntStateOf(0)
        private set
    var total by mutableIntStateOf(0)
        private set

    var net by mutableStateOf(NetSnapshot(vpn = false, transport = Transport.UNKNOWN))
        private set

    var message by mutableStateOf<String?>(null)

    // --- Arka plan servisi kancaları ---
    var onProgress: ((Int, Int) -> Unit)? = null
    var onFinished: (() -> Unit)? = null

    private var autoRefreshJob: Job? = null
    private var runJob: Job? = null
    private var persistJob: Job? = null
    private var pendingRun: List<String>? = null

    init {
        rows.addAll(store.load())
        refreshNetwork()
    }

    fun setAppLanguage(lang: AppLanguage) {
        if (language == lang) return
        language = lang
        prefs.edit().putString(KEY_LANGUAGE, lang.code).apply()
    }

    fun refreshNetwork() {
        net = NetStateReader.read(appContext)
    }

    // ---- Kalıcılık (sonuçlar kaybolmasın) ----

    /** Değişiklikleri kısa bir gecikmeyle diske yazar (yazma fırtınasını önler). */
    private fun persistSoon() {
        persistJob?.cancel()
        persistJob = scope.launch {
            delay(600)
            store.save(rows)
        }
    }

    private fun persistNow() {
        persistJob?.cancel()
        store.save(rows)
    }

    fun updateSettings(new: AppSettings) {
        settings = new
        applyAutoRefresh()
    }

    // ---- Arama / gruplama ----

    fun groupKey(t: Target): String {
        if (t.group.isNotBlank()) return t.group
        val derived = t.label.replace(Regex("\\s+(TLD|\\d+)$"), "").trim()
        return derived.ifBlank { t.label.ifBlank { strings.otherGroup } }
    }

    /** Grup adının geçerli dildeki görüntü karşılığı. */
    fun groupLabel(name: String): String = strings.groupLabel(name)

    fun toggleCollapsed(key: String) {
        collapsed = if (key in collapsed) collapsed - key else collapsed + key
    }

    fun setAllCollapsed(keys: Collection<String>, value: Boolean) {
        collapsed = if (value) keys.toSet() else emptySet()
    }

    fun counts(): Counts {
        var up = 0
        var down = 0
        var untested = 0
        for (r in rows) {
            val s = r.result.status
            when {
                s == Status.UP -> up++
                isDownStatus(s) -> down++
                else -> untested++
            }
        }
        return Counts(rows.size, up, down, untested)
    }

    private fun isUp(s: Status) = s == Status.UP
    private fun isDown(s: Status) = isDownStatus(s)
    private fun isUntested(s: Status) = isUntestedStatus(s)

    fun rowById(id: String): TargetRow? = rows.firstOrNull { it.target.id == id }
    private fun indexOfId(id: String): Int = rows.indexOfFirst { it.target.id == id }

    /** Görünür (filtre + arama + sıralama) hedeflerin kimlikleri, sıralı. */
    fun visibleIds(): List<String> {
        val q = search.trim().lowercase()
        val filtered = rows.indices.filter { i ->
            val r = rows[i]
            val g = groupKey(r.target)
            val matchQ = q.isEmpty() ||
                r.target.label.lowercase().contains(q) ||
                r.target.host.lowercase().contains(q) ||
                g.lowercase().contains(q) ||
                strings.groupLabel(g).lowercase().contains(q) ||
                r.geo?.country?.lowercase()?.contains(q) == true ||
                r.geo?.isp?.lowercase()?.contains(q) == true
            val matchF = when (filter) {
                Filter.ALL -> true
                Filter.UP -> isUp(r.result.status)
                Filter.DOWN -> isDown(r.result.status)
                Filter.UNTESTED -> isUntested(r.result.status)
            }
            matchQ && matchF
        }
        val sorted = when (sort) {
            SortMode.DEFAULT -> filtered
            SortMode.LATENCY -> filtered.sortedWith(
                compareBy({ if (rows[it].result.reachable) 0 else 1 }, { rows[it].result.avgMs })
            )
            SortMode.NAME -> filtered.sortedBy { rows[it].target.label.lowercase() }
            SortMode.COUNTRY -> filtered.sortedBy { rows[it].geo?.country ?: "zzz" }
            SortMode.STATUS -> filtered.sortedBy { rows[it].result.status.ordinal }
        }
        return sorted.map { rows[it].target.id }
    }

    fun idsOfGroup(name: String): List<String> =
        rows.filter { groupKey(it.target) == name }.map { it.target.id }

    /** Mevcut gruplar/menüler: ad → hedef sayısı. */
    fun groupCounts(): List<Pair<String, Int>> {
        val map = LinkedHashMap<String, Int>()
        for (r in rows) {
            val g = groupKey(r.target)
            map[g] = (map[g] ?: 0) + 1
        }
        return map.entries.sortedBy { it.key.lowercase() }.map { it.key to it.value }
    }

    // ---- Ekleme / düzenleme ----

    private fun key(t: Target) =
        "${t.method}|${t.host.lowercase()}|${t.port}|${t.dnsName?.lowercase()}|${t.label.lowercase()}|${t.group.lowercase()}"

    private fun addMissing(list: List<Target>): Int {
        val existing = rows.map { key(it.target) }.toHashSet()
        var added = 0
        for (t in list) {
            val k = key(t)
            if (k in existing) continue
            rows.add(TargetRow(t))
            existing.add(k)
            added++
        }
        if (added > 0) persistSoon()
        return added
    }

    private fun idsFor(list: List<Target>): List<String> {
        val wanted = list.map { key(it) }.toHashSet()
        return rows.filter { key(it.target) in wanted }.map { it.target.id }
    }

    fun addTargets(list: List<Target>, testNow: Boolean = false) {
        if (list.isEmpty()) return
        val added = addMissing(list)
        message = if (added == 0) strings.msgNoNewTargets else strings.msgAdded(added)
        if (testNow && added > 0) runVisible()
    }

    fun addAsMenu(list: List<Target>, menuName: String) {
        val name = menuName.trim()
        if (name.isEmpty() || list.isEmpty()) return
        val added = addMissing(list.map { it.copy(group = name) })
        message = if (added == 0) strings.msgMenuExists(name) else strings.msgMenuCreated(name, added)
    }

    fun updateTarget(id: String, t: Target) {
        val i = indexOfId(id)
        if (i < 0) return
        rows[i] = rows[i].copy(target = t)
        persistSoon()
    }

    fun duplicate(id: String) {
        val i = indexOfId(id)
        if (i < 0) return
        val r = rows[i]
        rows.add(
            i + 1,
            TargetRow(r.target.copy(id = UUID.randomUUID().toString(), label = r.target.label + strings.copySuffix))
        )
        persistSoon()
    }

    fun remove(id: String) {
        val i = indexOfId(id)
        if (i >= 0) {
            rows.removeAt(i)
            persistSoon()
        }
    }

    fun removeGroup(name: String) {
        val before = rows.size
        rows.removeAll { groupKey(it.target) == name }
        if (rows.size != before) {
            persistNow()
            message = strings.msgGroupDeleted(strings.groupLabel(name), before - rows.size)
        }
    }

    fun removeAll() {
        if (rows.isEmpty()) return
        rows.clear()
        persistNow()
        message = strings.msgAllDeleted
    }

    fun clearResults() {
        for (i in rows.indices) rows[i] = rows[i].copy(result = TestResult(), geo = null)
        persistNow()
    }

    // ---- Test çalıştırma ----

    fun testOne(id: String) {
        scope.launch { testAndUpdate(id); persistSoon() }
    }

    /** Arka plan servisinin çalıştıracağı hedefleri hazırlar (id listesi). */
    fun requestRun(list: List<Target>? = null, ids: List<String>? = null) {
        pendingRun = when {
            ids != null -> ids
            list != null -> {
                addMissing(list)
                idsFor(list)
            }
            else -> null
        }
    }

    fun consumePendingRun(): List<String>? {
        val p = pendingRun
        pendingRun = null
        return p
    }

    /** Verilen ID'leri test eder. `running` senkron olarak true yapılır. */
    fun startRun(ids: List<String>) {
        if (running) return
        val actual = ids.filter { indexOfId(it) >= 0 }
        if (actual.isEmpty()) {
            message = strings.msgNothingToTest
            onFinished?.invoke()
            return
        }
        done = 0
        total = actual.size
        running = true
        refreshNetwork()
        runJob = scope.launch { execute(actual) }
    }

    fun runVisible() = startRun(visibleIds())

    /** Çalışan testi durdurur (sonuçlar korunur). */
    fun stopRun() {
        if (!running) return
        runJob?.cancel()
        message = strings.msgStopped
    }

    private suspend fun execute(ids: List<String>) {
        try {
            coroutineScope {
                val sem = Semaphore(settings.concurrency.coerceIn(1, 32))
                ids.map { id ->
                    launch {
                        sem.withPermit { testAndUpdate(id) }
                        done += 1
                        onProgress?.invoke(done, total)
                        persistSoon()
                    }
                }.forEach { it.join() }
            }
        } finally {
            running = false
            onProgress?.invoke(total, total)
            persistNow()
            onFinished?.invoke()
        }
    }

    private suspend fun testAndUpdate(id: String) {
        val i = indexOfId(id)
        if (i < 0) return
        rows[i] = rows[i].copy(result = rows[i].result.copy(status = Status.TESTING))
        val target = rows[i].target
        val res = withContext(Dispatchers.IO) { Engine.run(target, settings) }
        var geo: GeoInfo? = null
        if (settings.geoEnabled) {
            val ip = res.resolvedIp ?: target.host
            geo = withContext(Dispatchers.IO) { GeoIp.lookup(ip) }
        }
        val j = indexOfId(id)
        if (j < 0) return
        rows[j] = rows[j].copy(result = res, geo = geo ?: rows[j].geo)
    }

    // ---- Otomatik yenileme ----

    private fun applyAutoRefresh() {
        autoRefreshJob?.cancel()
        val sec = settings.autoRefreshSec
        if (sec <= 0) return
        autoRefreshJob = scope.launch {
            while (isActive) {
                delay(sec * 1000L)
                if (!running) runVisible()
            }
        }
    }

    // ---- Dışa aktarma ----

    fun exportText(): String {
        val s = strings
        val sb = StringBuilder()
        val c = counts()
        sb.appendLine(s.reportTitle)
        sb.appendLine(s.reportConnection(s.transportLabel(net.transport), net.vpn))
        sb.appendLine(s.reportTotals(c.total, c.up, c.down, c.untested))
        sb.appendLine()
        val sorted = rows.sortedWith(compareBy({ if (it.result.reachable) 0 else 1 }, { it.result.avgMs }))
        for (r in sorted) {
            val res = r.result
            sb.append("• ").append(r.target.label).append("  <").append(r.target.host)
            r.target.port?.let { sb.append(":").append(it) }
            sb.appendLine(">")
            sb.append("   ").append(s.detailStatus).append("=").append(s.statusLabel(res.status))
            if (res.reachable) sb.append("  ").append(s.sortLabel(SortMode.LATENCY)).append("=").append(res.avgMs.roundToInt()).append(" ms")
            if (res.samples.isNotEmpty()) {
                sb.append("  (min ").append(res.minMs.roundToInt())
                    .append(", max ").append(res.maxMs.roundToInt())
                    .append(", jitter ").append(res.jitterMs.roundToInt()).append(" ms)")
            }
            sb.append("  ").append(s.detailMethod).append("=").append(res.method.ifBlank { s.methodLabel(r.target.method) })
            if (res.lossPct > 0) sb.append("  ").append(s.reportLoss).append("=").append(res.lossPct).append("%")
            r.geo?.let { g ->
                val loc = formatCountry(g, s)
                if (loc.isNotBlank()) sb.append("  ").append(loc)
                val isp = formatIsp(g)
                if (isp.isNotBlank()) sb.append("  ").append(isp)
            }
            sb.appendLine()
        }
        sb.appendLine()
        sb.appendLine(s.reportFooter())
        return sb.toString()
    }
}
