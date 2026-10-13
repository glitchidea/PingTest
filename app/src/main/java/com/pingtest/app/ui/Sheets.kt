package com.pingtest.app.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.pingtest.app.R
import com.pingtest.app.data.Presets
import com.pingtest.app.model.AppSettings
import com.pingtest.app.model.Target
import com.pingtest.app.model.TargetRow
import com.pingtest.app.model.TestMethod
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SheetScaffold(
    onDismiss: () -> Unit,
    content: @Composable () -> Unit
) {
    val state = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = state) {
        Column(
            Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(bottom = 24.dp)
                .navigationBarsPadding()
        ) {
            content()
        }
    }
}

@Composable
private fun Title(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(bottom = 12.dp)
    )
}

@Composable
private fun ChipGroup(
    options: List<Pair<String, Int>>,
    selected: Int,
    onSelect: (Int) -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        options.forEach { (label, value) ->
            FilterChip(
                selected = selected == value,
                onClick = { onSelect(value) },
                label = { Text(label) }
            )
        }
    }
}

@Composable
private fun OptionRow(
    label: String,
    options: List<Pair<String, Int>>,
    selected: Int,
    onSelect: (Int) -> Unit
) {
    Text(label, style = MaterialTheme.typography.labelLarge, modifier = Modifier.padding(top = 8.dp, bottom = 6.dp))
    ChipGroup(options, selected, onSelect)
}

// ---------------------------------------------------------------- Ekle / Düzenle

@Composable
fun AddEditSheet(
    initial: Target?,
    onDismiss: () -> Unit,
    onSave: (Target) -> Unit,
    onSaveAndTest: (Target) -> Unit
) {
    val t = LocalStrings.current
    var label by remember { mutableStateOf(initial?.label ?: "") }
    var host by remember { mutableStateOf(initial?.host ?: "") }
    var port by remember { mutableStateOf(initial?.port?.toString() ?: "") }
    var method by remember { mutableStateOf(initial?.method ?: TestMethod.AUTO) }
    var group by remember { mutableStateOf(initial?.group ?: "") }
    var url by remember { mutableStateOf(initial?.url ?: "") }
    var dnsName by remember { mutableStateOf(initial?.dnsName ?: "") }

    val hostOk = host.isNotBlank()

    fun build() = Target(
        id = initial?.id ?: java.util.UUID.randomUUID().toString(),
        label = label.ifBlank { host.trim() },
        host = host.trim(),
        port = port.trim().toIntOrNull(),
        method = method,
        group = group.trim(),
        url = url.trim().ifBlank { null },
        dnsName = dnsName.trim().ifBlank { null }
    )

    SheetScaffold(onDismiss) {
        Title(if (initial == null) t.addTitle else t.editTitle)

        OutlinedTextField(
            value = label,
            onValueChange = { label = it },
            label = { Text(t.labelField) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.size(10.dp))
        OutlinedTextField(
            value = host,
            onValueChange = { host = it },
            label = { Text(t.hostField) },
            singleLine = true,
            isError = host.isNotEmpty() && !hostOk,
            placeholder = { Text(t.hostPlaceholder) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.size(10.dp))
        OutlinedTextField(
            value = port,
            onValueChange = { port = it.filter { c -> c.isDigit() } },
            label = { Text(t.portField) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            placeholder = { Text(t.portPlaceholder) },
            modifier = Modifier.fillMaxWidth()
        )

        Text(
            t.testMethodField,
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(top = 14.dp, bottom = 6.dp)
        )
        Row(
            Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TestMethod.entries.forEach { m ->
                FilterChip(
                    selected = method == m,
                    onClick = { method = m },
                    label = { Text(t.methodLabel(m)) }
                )
            }
        }
        Text(
            t.methodDesc(method),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 6.dp)
        )

        if (method == TestMethod.HTTP) {
            Spacer(Modifier.size(10.dp))
            OutlinedTextField(
                value = url,
                onValueChange = { url = it },
                label = { Text(t.urlField) },
                singleLine = true,
                placeholder = { Text(t.urlPlaceholder) },
                modifier = Modifier.fillMaxWidth()
            )
        }
        if (method == TestMethod.DNS) {
            Spacer(Modifier.size(10.dp))
            OutlinedTextField(
                value = dnsName,
                onValueChange = { dnsName = it },
                label = { Text(t.dnsNameField) },
                singleLine = true,
                placeholder = { Text(t.dnsNamePlaceholder) },
                modifier = Modifier.fillMaxWidth()
            )
        }
        Spacer(Modifier.size(10.dp))
        OutlinedTextField(
            value = group,
            onValueChange = { group = it },
            label = { Text(t.groupField) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.size(18.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) { Text(t.cancel) }
            Button(
                onClick = { if (hostOk) onSave(build()) },
                enabled = hostOk,
                modifier = Modifier.weight(1f)
            ) { Text(t.save) }
        }
        if (initial == null) {
            Spacer(Modifier.size(8.dp))
            Button(
                onClick = { if (hostOk) onSaveAndTest(build()) },
                enabled = hostOk,
                modifier = Modifier.fillMaxWidth()
            ) { Text(t.saveAndTest) }
        }
    }
}

// ---------------------------------------------------------------- İçe aktar

@Composable
fun ImportSheet(
    onDismiss: () -> Unit,
    onImport: (List<Target>, Boolean) -> Unit,
    onImportCountries: () -> Unit,
    onImportBrowsers: () -> Unit
) {
    val t = LocalStrings.current
    var text by remember { mutableStateOf("") }
    val parsed = remember(text) { if (text.isBlank()) emptyList() else Presets.parse(text) }

    SheetScaffold(onDismiss) {
        Title(t.importTitle)

        Text(
            t.importHelp,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.size(10.dp))

        Row(
            Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(onClick = onImportCountries) { Text(t.allCountriesBtn) }
            Button(onClick = onImportBrowsers) { Text(t.internetTestShort) }
            OutlinedButton(onClick = { text = presetsToText(Presets.dns) }) { Text(t.dnsListBtn) }
            OutlinedButton(onClick = { text = presetsToText(Presets.common) }) { Text(t.generalListBtn) }
        }

        Spacer(Modifier.size(10.dp))
        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            label = { Text(t.addressesField) },
            placeholder = { Text(t.addressesPlaceholder) },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 140.dp)
        )

        Spacer(Modifier.size(8.dp))
        Text(
            if (parsed.isEmpty()) t.waitingValid
            else t.foundValid(parsed.size),
            style = MaterialTheme.typography.labelLarge,
            color = if (parsed.isEmpty()) MaterialTheme.colorScheme.onSurfaceVariant
            else MaterialTheme.colorScheme.primary
        )

        Spacer(Modifier.size(14.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) { Text(t.cancel) }
            Button(
                onClick = { onImport(parsed, true) },
                enabled = parsed.isNotEmpty(),
                modifier = Modifier.weight(1f)
            ) { Text(t.addAndTest) }
        }
    }
}

private fun presetsToText(list: List<Target>): String =
    list.joinToString("\n") { t ->
        val prefix = when (t.method) {
            TestMethod.DNS -> "dns://"
            else -> ""
        }
        val port = t.port?.let { ":$it" } ?: ""
        "$prefix${t.host}$port , ${t.label}"
    }

// ---------------------------------------------------------------- Detay

@Composable
fun DetailSheet(
    row: TargetRow,
    onDismiss: () -> Unit,
    onRetest: () -> Unit
) {
    val t = LocalStrings.current
    val res = row.result
    val clipboard = LocalClipboardManager.current

    SheetScaffold(onDismiss) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            StatusDot(res.status, 14.dp)
            Spacer(Modifier.size(10.dp))
            Column(Modifier.weight(1f)) {
                Text(row.target.label, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
                Text(
                    buildString {
                        append(row.target.host)
                        row.target.port?.let { append(":").append(it) }
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = onRetest) {
                Icon(Icons.Default.Refresh, contentDescription = t.retestDesc)
            }
        }

        Spacer(Modifier.size(12.dp))
        DetailRow(t.detailStatus, t.statusLabel(res.status))
        DetailRow(t.detailMethod, res.method.ifBlank { t.methodLabel(row.target.method) })
        DetailRow(t.detailAverage, formatMs(res.avgMs))
        DetailRow(t.detailMinMax, "${res.minMs.roundToInt()} / ${res.maxMs.roundToInt()} ms")
        DetailRow(t.detailJitter, "${res.jitterMs.roundToInt()} ms")
        DetailRow(t.detailPacket, t.packetLoss(res.received, res.sent, res.lossPct))
        DetailRow(t.detailResolvedIp, res.resolvedIp ?: "—")
        DetailRow(t.detailCountry, formatCountry(row.geo, t).ifBlank { "—" })
        DetailRow(t.detailProvider, formatIsp(row.geo).ifBlank { "—" })
        DetailRow(t.detailLastTest, formatTime(res.timestamp))
        res.error?.let { DetailRow(t.detailError, t.errorLabel(it) ?: it) }

        if (res.samples.isNotEmpty()) {
            Spacer(Modifier.size(10.dp))
            Text(t.detailSamples, style = MaterialTheme.typography.labelLarge)
            Spacer(Modifier.size(4.dp))
            Text(
                res.samples.joinToString("   ") { "${it.roundToInt()} ms" },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(Modifier.size(16.dp))
        HorizontalDivider()
        Spacer(Modifier.size(12.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) { Text(t.close) }
            Button(
                onClick = {
                    clipboard.setText(
                        AnnotatedString(
                            "${row.target.label} <${row.target.host}> — ${t.statusLabel(res.status)} " +
                                "${if (res.reachable) formatMs(res.avgMs) else ""} ${formatCountry(row.geo, t)}"
                        )
                    )
                },
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.Share, contentDescription = null)
                Spacer(Modifier.size(6.dp))
                Text(t.copy)
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(Modifier.fillMaxWidth().padding(vertical = 5.dp)) {
        Text(
            label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(120.dp)
        )
        Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
    }
}

// ---------------------------------------------------------------- Ayarlar

@Composable
fun SettingsSheet(
    settings: AppSettings,
    language: AppLanguage,
    onLanguage: (AppLanguage) -> Unit,
    onDismiss: () -> Unit,
    onSave: (AppSettings) -> Unit,
    onClearResults: () -> Unit,
    onDeleteAll: () -> Unit
) {
    val t = LocalStrings.current
    var s by remember { mutableStateOf(settings) }

    SheetScaffold(onDismiss) {
        Title(t.settingsTitle)

        // ---- Dil seçimi ----
        OptionRow(
            t.languageLabel,
            AppLanguage.entries.map { it.nativeName to it.ordinal },
            language.ordinal
        ) { idx -> onLanguage(AppLanguage.entries[idx]) }

        OptionRow(
            t.packetCount,
            listOf("1" to 1, "2" to 2, "4" to 4, "8" to 8, "16" to 16),
            s.count
        ) { s = s.copy(count = it) }

        OptionRow(
            t.timeoutLabel,
            listOf(t.seconds(1) to 1000, t.seconds(2) to 2000, t.seconds(3) to 3000, t.seconds(5) to 5000, t.seconds(10) to 10000),
            s.timeoutMs
        ) { s = s.copy(timeoutMs = it) }

        OptionRow(
            t.concurrencyLabel,
            listOf("1" to 1, "4" to 4, "8" to 8, "16" to 16, "32" to 32),
            s.concurrency
        ) { s = s.copy(concurrency = it) }

        OptionRow(
            t.autoRefreshLabel,
            listOf(t.offLabel to 0, t.s30 to 30, t.m1 to 60, t.m5 to 300),
            s.autoRefreshSec
        ) { s = s.copy(autoRefreshSec = it) }

        Spacer(Modifier.size(14.dp))
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(t.geoTitle, style = MaterialTheme.typography.bodyLarge)
                Text(
                    t.geoSubtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Switch(checked = s.geoEnabled, onCheckedChange = { s = s.copy(geoEnabled = it) })
        }

        Spacer(Modifier.size(12.dp))
        OutlinedTextField(
            value = s.dnsQueryName,
            onValueChange = { s = s.copy(dnsQueryName = it) },
            label = { Text(t.dnsQueryField) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.size(18.dp))
        Button(onClick = { onSave(s); onDismiss() }, modifier = Modifier.fillMaxWidth()) {
            Text(t.save)
        }
        Spacer(Modifier.size(8.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedButton(onClick = { onClearResults(); onDismiss() }, modifier = Modifier.weight(1f)) {
                Text(t.clearResults)
            }
            OutlinedButton(onClick = { onDeleteAll(); onDismiss() }, modifier = Modifier.weight(1f)) {
                Text(t.deleteAll)
            }
        }

        Spacer(Modifier.size(22.dp))
        HorizontalDivider()
        Spacer(Modifier.size(16.dp))
        DeveloperCard()
    }
}

/**
 * Geliştirici tanıtım kartı. Dokununca Glitchİdea web sitesini tarayıcıda açar.
 */
@Composable
private fun DeveloperCard() {
    val t = LocalStrings.current
    val context = LocalContext.current
    val openSite: () -> Unit = {
        runCatching {
            context.startActivity(
                Intent(Intent.ACTION_VIEW, Uri.parse(t.websiteUrl))
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            )
        }
    }
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = openSite)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(R.drawable.glitchidea_logo),
                contentDescription = t.developerName,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
            )
            Spacer(Modifier.size(14.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    t.developerLabel.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    t.developerName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    t.developerTagline,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    t.websiteLabel,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            Icon(
                Icons.AutoMirrored.Filled.ExitToApp,
                contentDescription = t.visitWebsite,
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

// ---------------------------------------------------------------- Menüler & Şablonlar

/**
 * Merkezi "menü" yönetimi: hazır bir şablonu doğrudan test edebilir ya da
 * kendi menünü oluşturup şablonu içine koyabilirsin. Modüler/komponent yapı.
 */
@Composable
fun MenuSheet(
    templates: List<Presets.Template>,
    groups: List<Pair<String, Int>>,
    onDismiss: () -> Unit,
    onTestTemplate: (Presets.Template) -> Unit,
    onCreateMenu: (Presets.Template, String) -> Unit,
    onTestGroup: (String) -> Unit,
    onDeleteGroup: (String) -> Unit,
    onCountries: () -> Unit,
    onImportPaste: () -> Unit
) {
    val t = LocalStrings.current
    var createFor by remember { mutableStateOf<Presets.Template?>(null) }
    var tplQuery by remember { mutableStateOf("") }

    SheetScaffold(onDismiss) {
        Title(t.menuTitle)
        Text(
            t.menuDesc,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.size(10.dp))
        OutlinedTextField(
            value = tplQuery,
            onValueChange = { tplQuery = it },
            label = { Text(t.searchTemplateField) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        val filtered = if (tplQuery.isBlank()) templates
        else templates.filter {
            val localized = t.templateLabel(it.key, it.label)
            localized.lowercase().contains(tplQuery.trim().lowercase()) ||
                it.label.lowercase().contains(tplQuery.trim().lowercase())
        }

        Spacer(Modifier.size(14.dp))
        Text(
            t.readyTemplates(filtered.size, templates.size),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(Modifier.size(4.dp))
        filtered.forEach { tpl ->
            Row(
                Modifier.fillMaxWidth().padding(vertical = 3.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(Modifier.weight(1f)) {
                    Text(
                        t.templateLabel(tpl.key, tpl.label),
                        style = MaterialTheme.typography.bodyLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        t.targetsCount(tpl.items.size),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                TextButton(onClick = { onTestTemplate(tpl) }) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.size(4.dp))
                    Text(t.testOne)
                }
                TextButton(onClick = { createFor = tpl }) { Text(t.makeMenu) }
            }
            HorizontalDivider()
        }

        Spacer(Modifier.size(14.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(onClick = onCountries, modifier = Modifier.weight(1f)) {
                Text(t.allCountries)
            }
            OutlinedButton(onClick = onImportPaste, modifier = Modifier.weight(1f)) {
                Text(t.bulkImport)
            }
        }

        Spacer(Modifier.size(18.dp))
        Text(t.groupsHeader, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.size(4.dp))
        if (groups.isEmpty()) {
            Text(
                t.noGroupsYet,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            groups.forEach { (name, count) ->
                Row(
                    Modifier.fillMaxWidth().padding(vertical = 1.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        t.groupLabel(name),
                        modifier = Modifier.weight(1f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        "$count",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    IconButton(onClick = { onTestGroup(name) }) {
                        Icon(Icons.Default.PlayArrow, contentDescription = t.testGroupShort, tint = MaterialTheme.colorScheme.primary)
                    }
                    IconButton(onClick = { onDeleteGroup(name) }) {
                        Icon(Icons.Default.Delete, contentDescription = t.deleteGroup)
                    }
                }
            }
        }
    }

    createFor?.let { tpl ->
        CreateMenuDialog(
            defaultName = t.templateLabel(tpl.key, tpl.label),
            onDismiss = { createFor = null },
            onConfirm = { name ->
                onCreateMenu(tpl, name)
                createFor = null
            }
        )
    }
}

@Composable
private fun CreateMenuDialog(
    defaultName: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    val t = LocalStrings.current
    var name by remember { mutableStateOf(defaultName) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(t.createMenuTitle) },
        text = {
            Column {
                Text(
                    t.createMenuDesc,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.size(8.dp))
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(t.menuNameField) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { if (name.isNotBlank()) onConfirm(name) }) { Text(t.create) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(t.cancel) }
        }
    )
}
