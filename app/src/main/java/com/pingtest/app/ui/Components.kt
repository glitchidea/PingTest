package com.pingtest.app.ui

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.pingtest.app.model.Status
import com.pingtest.app.model.TargetRow

@Composable
fun StatusDot(status: Status, size: Dp = 12.dp) {
    val color = statusColor(status)
    val alpha = if (status == Status.TESTING) {
        val transition = rememberInfiniteTransition(label = "pulse")
        val a by transition.animateFloat(
            initialValue = 1f,
            targetValue = 0.25f,
            animationSpec = infiniteRepeatable(tween(650), RepeatMode.Reverse),
            label = "alpha"
        )
        a
    } else 1f
    Box(
        Modifier
            .size(size)
            .clip(CircleShape)
            .background(color.copy(alpha = alpha))
    )
}

@Composable
fun Pill(text: String, color: Color? = null) {
    Surface(
        color = color ?: MaterialTheme.colorScheme.surfaceVariant,
        shape = CircleShape
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TargetCard(
    row: TargetRow,
    onClick: () -> Unit,
    onTest: () -> Unit,
    onEdit: () -> Unit,
    onDuplicate: () -> Unit,
    onDelete: () -> Unit
) {
    val s = LocalStrings.current
    val res = row.result
    var menu by remember { mutableStateOf(false) }
    val endpoint = buildString {
        append(row.target.host)
        row.target.port?.let { append(":").append(it) }
    }
    val primary = if (res.reachable) formatMs(res.avgMs) else s.statusLabel(res.status)
    val secondary = when {
        res.reachable && res.received > 0 -> s.lossJitter(res.lossPct, res.jitterMs.toInt())
        res.error != null -> s.errorLabel(res.error) ?: res.error!!
        else -> s.notTestedYet
    }

    ElevatedCard(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(horizontal = 14.dp, vertical = 12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                StatusDot(res.status, 12.dp)
                Spacer(Modifier.size(10.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        row.target.label,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        endpoint,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Spacer(Modifier.size(8.dp))
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        primary,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (res.reachable) statusColor(Status.UP) else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        secondary,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Box {
                    IconButton(onClick = { menu = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = s.cardMenuDesc)
                    }
                    DropdownMenu(expanded = menu, onDismissRequest = { menu = false }) {
                        DropdownMenuItem(
                            text = { Text(s.testOne) },
                            leadingIcon = { Icon(Icons.Default.Refresh, null) },
                            onClick = { menu = false; onTest() }
                        )
                        DropdownMenuItem(
                            text = { Text(s.edit) },
                            leadingIcon = { Icon(Icons.Default.Edit, null) },
                            onClick = { menu = false; onEdit() }
                        )
                        DropdownMenuItem(
                            text = { Text(s.duplicate) },
                            leadingIcon = { Icon(Icons.Default.Add, null) },
                            onClick = { menu = false; onDuplicate() }
                        )
                        HorizontalDivider()
                        DropdownMenuItem(
                            text = { Text(s.delete) },
                            leadingIcon = { Icon(Icons.Default.Delete, null) },
                            onClick = { menu = false; onDelete() }
                        )
                    }
                }
            }

            val loc = formatCountry(row.geo, s)
            val isp = formatIsp(row.geo)
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(top = 2.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Pill(res.method.ifBlank { s.methodLabel(row.target.method) })
                if (loc.isNotBlank()) Pill(loc)
                if (isp.isNotBlank()) {
                    Row(Modifier.weight(1f)) {
                        Pill(isp)
                    }
                }
            }
        }
        if (res.status == Status.TESTING) {
            LinearProgressIndicator(Modifier.fillMaxWidth().height(3.dp))
        }
    }
}

/**
 * Kalabalık listeleri toparlayan, katlanabilir grup başlığı.
 * Başlığa dokununca grup açılır/kapanır.
 */
@Composable
fun GroupHeader(
    title: String,
    up: Int,
    tested: Int,
    total: Int,
    collapsed: Boolean,
    onToggle: () -> Unit,
    onRun: () -> Unit
) {
    val s = LocalStrings.current
    Surface(color = MaterialTheme.colorScheme.background, modifier = Modifier.fillMaxWidth()) {
        Row(
            Modifier
                .fillMaxWidth()
                .clickable(onClick = onToggle)
                .padding(start = 6.dp, end = 2.dp, top = 4.dp, bottom = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (collapsed) Icons.AutoMirrored.Filled.KeyboardArrowRight
                else Icons.Default.KeyboardArrowDown,
                contentDescription = if (collapsed) s.expandGroup else s.collapseGroup,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.size(4.dp))
            Text(
                s.groupLabel(title),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            val color = when {
                up > 0 -> statusColor(Status.UP)
                tested >= total -> statusColor(Status.DOWN)
                else -> MaterialTheme.colorScheme.onSurfaceVariant
            }
            Text(
                "$up/$total",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = color
            )
            IconButton(onClick = onRun, modifier = Modifier.size(36.dp)) {
                Icon(
                    Icons.Default.PlayArrow,
                    contentDescription = s.testGroup,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}
