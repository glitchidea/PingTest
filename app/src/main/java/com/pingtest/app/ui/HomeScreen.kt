package com.pingtest.app.ui

import android.content.Intent
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.pingtest.app.TestService
import com.pingtest.app.data.Presets
import com.pingtest.app.model.Filter
import com.pingtest.app.model.SortMode
import com.pingtest.app.model.Status
import com.pingtest.app.model.TargetRow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(state: AppState) {
    val context = LocalContext.current
    val strings = LocalStrings.current
    val snackbar = remember { SnackbarHostState() }

    var showAdd by remember { mutableStateOf(false) }
    var editId by remember { mutableStateOf<String?>(null) }
    var showImport by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }
    var showSettings by remember { mutableStateOf(false) }
    var detailId by remember { mutableStateOf<String?>(null) }
    var showSearch by remember { mutableStateOf(false) }

    LaunchedEffect(state.message) {
        state.message?.let {
            snackbar.showSnackbar(it)
            state.message = null
        }
    }

    // Ülke listesini (asset) okuyup doğrudan ekler.
    val loadCountries: () -> Unit = {
        val text = runCatching {
            context.assets.open("countries.txt").bufferedReader().use { it.readText() }
        }.getOrNull()
        if (text.isNullOrBlank()) {
            state.message = strings.msgCountriesNotFound
        } else {
            state.addTargets(Presets.parse(text))
        }
    }

    val snap by remember { derivedStateOf { homeSnapshot(state) } }
    val counts = snap.counts
    val visible = snap.visible
    val byId = snap.byId
    val grouped = snap.grouped
    val groups = snap.groups
    val headerKeys = snap.headerKeys

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(strings.appName, fontWeight = FontWeight.SemiBold)
                        Text(
                            strings.transportLabel(state.net.transport) +
                                if (state.net.vpn) " · ${strings.vpnOnShort}" else "",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        val intent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, state.exportText())
                        }
                        context.startActivity(Intent.createChooser(intent, strings.shareReport))
                    }) { Icon(Icons.Default.Share, contentDescription = strings.shareReport) }
                    IconButton(onClick = { showMenu = true }) {
                        Icon(Icons.Default.Add, contentDescription = strings.menusTemplatesDesc)
                    }
                    IconButton(onClick = { showSearch = !showSearch }) {
                        Icon(Icons.Default.Search, contentDescription = strings.searchDesc)
                    }
                    IconButton(onClick = { showSettings = true }) {
                        Icon(Icons.Default.Settings, contentDescription = strings.settingsDesc)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAdd = true }) {
                Icon(Icons.Default.Add, contentDescription = strings.addTargetDesc)
            }
        },
        snackbarHost = { SnackbarHost(snackbar) }
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            NetBanner(state, counts, onStop = { state.stopRun() })

            Row(
                Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Filter.entries.forEach { f ->
                    val n = when (f) {
                        Filter.ALL -> counts.total
                        Filter.UP -> counts.up
                        Filter.DOWN -> counts.down
                        Filter.UNTESTED -> counts.untested
                    }
                    FilterChip(
                        selected = state.filter == f,
                        onClick = { state.filter = f },
                        label = { Text("${strings.filterLabel(f)} $n") }
                    )
                }
            }

            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (showSearch) {
                    OutlinedTextField(
                        value = state.search,
                        onValueChange = { state.search = it },
                        label = { Text(strings.searchDesc) },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                } else {
                    Spacer(Modifier.weight(1f))
                }
                if (grouped && headerKeys.isNotEmpty()) {
                    TextButton(onClick = {
                        state.setAllCollapsed(headerKeys, state.collapsed.isEmpty())
                    }) {
                        Text(if (state.collapsed.isEmpty()) strings.collapseAll() else strings.expandAll())
                    }
                }
                SortMenu(state)
            }

            val renderCard: @Composable (String) -> Unit = { id ->
                val row = byId[id]
                if (row != null) {
                    TargetCard(
                        row = row,
                        onClick = { detailId = id },
                        onTest = { state.testOne(id) },
                        onEdit = { editId = id },
                        onDuplicate = { state.duplicate(id) },
                        onDelete = { state.remove(id) }
                    )
                }
            }

            Box(Modifier.weight(1f)) {
                when {
                    state.rows.isEmpty() -> EmptyState(
                        onInternet = {
                            state.requestRun(list = Presets.internet)
                            TestService.start(context)
                        },
                        onMenus = { showMenu = true },
                        onAdd = { showAdd = true }
                    )

                    visible.isEmpty() -> CenteredNote(strings.noMatch())

                    else -> LazyColumn(
                        Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 96.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (grouped) {
                            groups.forEach { (key, list) ->
                                if (list.size >= 2) {
                                    item(key = "h:$key") {
                                        GroupHeader(
                                            title = key,
                                            up = list.count { byId[it]?.result?.reachable == true },
                                            tested = list.count { !isUntestedStatus(byId[it]?.result?.status ?: Status.UNTESTED) },
                                            total = list.size,
                                            collapsed = key in state.collapsed,
                                            onToggle = { state.toggleCollapsed(key) },
                                            onRun = {
                                                state.requestRun(ids = list)
                                                TestService.start(context)
                                            }
                                        )
                                    }
                                    if (key !in state.collapsed) {
                                        items(list, key = { it }) { id -> renderCard(id) }
                                    }
                                } else {
                                    items(list, key = { it }) { id -> renderCard(id) }
                                }
                            }
                        } else {
                            items(visible, key = { it }) { id -> renderCard(id) }
                        }
                    }
                }
            }
        }
    }

    // ---- Alt sayfalar ----

    if (showAdd) {
        AddEditSheet(
            initial = null,
            onDismiss = { showAdd = false },
            onSave = { state.addTargets(listOf(it)); showAdd = false },
            onSaveAndTest = { state.addTargets(listOf(it), testNow = true); showAdd = false }
        )
    }

    val editing = editId?.let { byId[it]?.target }
    if (editId != null && editing != null) {
        val id = editId!!
        AddEditSheet(
            initial = editing,
            onDismiss = { editId = null },
            onSave = { state.updateTarget(id, it); editId = null },
            onSaveAndTest = { state.updateTarget(id, it); editId = null; state.testOne(id) }
        )
    }

    val detailed = detailId?.let { byId[it] }
    if (detailId != null && detailed != null) {
        val id = detailId!!
        DetailSheet(
            row = detailed,
            onDismiss = { detailId = null },
            onRetest = { state.testOne(id) }
        )
    }

    if (showImport) {
        ImportSheet(
            onDismiss = { showImport = false },
            onImport = { list, test ->
                state.addTargets(list, testNow = test)
                showImport = false
            },
            onImportCountries = {
                loadCountries()
                showImport = false
            },
            onImportBrowsers = {
                state.requestRun(list = Presets.internet)
                showImport = false
                TestService.start(context)
            }
        )
    }

    if (showMenu) {
        MenuSheet(
            templates = Presets.allTemplates,
            groups = state.groupCounts(),
            onDismiss = { showMenu = false },
            onTestTemplate = { tpl ->
                state.requestRun(list = tpl.items)
                showMenu = false
                TestService.start(context)
            },
            onCreateMenu = { tpl, name -> state.addAsMenu(tpl.items, name) },
            onTestGroup = { name ->
                state.requestRun(ids = state.idsOfGroup(name))
                showMenu = false
                TestService.start(context)
            },
            onDeleteGroup = { state.removeGroup(it) },
            onCountries = {
                loadCountries()
                showMenu = false
            },
            onImportPaste = {
                showMenu = false
                showImport = true
            }
        )
    }

    if (showSettings) {
        SettingsSheet(
            settings = state.settings,
            language = state.language,
            onLanguage = { state.setAppLanguage(it) },
            onDismiss = { showSettings = false },
            onSave = { state.updateSettings(it) },
            onClearResults = { state.clearResults() },
            onDeleteAll = { state.removeAll() }
        )
    }
}

@Composable
private fun NetBanner(state: AppState, counts: AppState.Counts, onStop: () -> Unit) {
    val context = LocalContext.current
    val s = LocalStrings.current
    Surface(color = MaterialTheme.colorScheme.surface) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Pill("${s.connection}: ${s.transportLabel(state.net.transport)}")
                Spacer(Modifier.size(6.dp))
                Pill(
                    if (state.net.vpn) s.vpnOn else s.vpnOff,
                    color = if (state.net.vpn) Color(0xFF134E4A) else null
                )
                Spacer(Modifier.weight(1f))
                if (state.running) {
                    Text(
                        "${state.done}/${state.total}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(Modifier.size(10.dp))
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = { state.requestRun(); TestService.start(context) },
                    enabled = !state.running,
                    modifier = Modifier.weight(1f)
                ) {
                    if (state.running) {
                        CircularProgressIndicator(
                            Modifier.size(18.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Spacer(Modifier.size(8.dp))
                        Text(s.runningBackground)
                    } else {
                        Icon(Icons.Default.Refresh, contentDescription = null)
                        Spacer(Modifier.size(8.dp))
                        Text(s.testAll)
                    }
                }
                if (state.running) {
                    OutlinedButton(onClick = onStop) { Text(s.stop) }
                } else {
                    OutlinedButton(onClick = { state.clearResults() }) { Text(s.clear) }
                }
            }

            if (state.running) {
                Spacer(Modifier.size(8.dp))
                LinearProgressIndicator(Modifier.fillMaxWidth())
                Spacer(Modifier.size(6.dp))
                Text(
                    s.screenOffHint,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else if (counts.total > 0) {
                Spacer(Modifier.size(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    StatText(s.upCount(counts.up), statusColor(Status.UP))
                    StatText(s.downCount(counts.down), statusColor(Status.DOWN))
                    StatText(s.pendingCount(counts.untested), MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            if (state.net.vpn) {
                Spacer(Modifier.size(6.dp))
                Text(
                    s.vpnWarning,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun StatText(text: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Surface(color = color, shape = CircleShape) {
            Spacer(Modifier.size(8.dp))
        }
        Spacer(Modifier.size(6.dp))
        Text(text, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurface)
    }
}

@Composable
private fun SortMenu(state: AppState) {
    val s = LocalStrings.current
    var open by remember { mutableStateOf(false) }
    Box {
        TextButton(onClick = { open = true }) {
            Icon(Icons.AutoMirrored.Filled.List, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.size(6.dp))
            Text(s.sortLabel(state.sort))
        }
        DropdownMenu(expanded = open, onDismissRequest = { open = false }) {
            SortMode.entries.forEach { mode ->
                DropdownMenuItem(
                    text = { Text(s.sortLabel(mode)) },
                    onClick = { state.sort = mode; open = false }
                )
            }
        }
    }
}

@Composable
private fun EmptyState(
    onInternet: () -> Unit,
    onMenus: () -> Unit,
    onAdd: () -> Unit
) {
    val s = LocalStrings.current
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("🌐", style = MaterialTheme.typography.displayMedium)
        Spacer(Modifier.size(12.dp))
        Text(
            s.noTargetsTitle,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(Modifier.size(6.dp))
        Text(
            s.noTargetsDesc,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.size(20.dp))
        Button(onClick = onAdd, modifier = Modifier.fillMaxWidth()) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(Modifier.size(8.dp))
            Text(s.addTargetDesc)
        }
        Spacer(Modifier.size(8.dp))
        OutlinedButton(onClick = onInternet, modifier = Modifier.fillMaxWidth()) {
            Text(s.emptyInternetBtn)
        }
        Spacer(Modifier.size(8.dp))
        OutlinedButton(onClick = onMenus, modifier = Modifier.fillMaxWidth()) {
            Text(s.emptyMenusBtn)
        }
    }
}

@Composable
private fun CenteredNote(text: String) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

// ---- Ekran anlık görüntüsü (performans: tek türetilmiş durum) ----

private data class HomeSnapshot(
    val counts: AppState.Counts,
    val visible: List<String>,
    val byId: Map<String, TargetRow>,
    val grouped: Boolean,
    val groups: LinkedHashMap<String, MutableList<String>>,
    val headerKeys: List<String>
)

private fun homeSnapshot(state: AppState): HomeSnapshot {
    val byId = state.rows.associateBy { it.target.id }
    val visible = state.visibleIds()
    val grouped = state.sort == SortMode.DEFAULT
    val groups = LinkedHashMap<String, MutableList<String>>()
    if (grouped) {
        for (id in visible) {
            val row = byId[id] ?: continue
            groups.getOrPut(state.groupKey(row.target)) { mutableListOf() }.add(id)
        }
    }
    return HomeSnapshot(
        counts = state.counts(),
        visible = visible,
        byId = byId,
        grouped = grouped,
        groups = groups,
        headerKeys = groups.filterValues { it.size >= 2 }.keys.toList()
    )
}
