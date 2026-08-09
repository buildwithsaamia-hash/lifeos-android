package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Emergency
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FolderSpecial
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material.icons.filled.Web
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.EmergencyRed
import com.example.ui.theme.PrimaryCyan
import com.example.ui.theme.SecondaryPurple

data class NavItem(
    val label: String,
    val icon: ImageVector,
    val index: Int
)

val primaryNavItems = listOf(
    NavItem("Home", Icons.Default.GridView, 0),
    NavItem("AI", Icons.Default.AutoAwesome, 2),
    NavItem("Planner", Icons.Default.DateRange, 1),
    NavItem("Vault", Icons.Default.FolderSpecial, 4),
    NavItem("Profile", Icons.Default.Person, 6),
    NavItem("More", Icons.Default.Apps, -1)
)

data class MoreItem(
    val label: String,
    val subtitle: String,
    val icon: ImageVector,
    val index: Int,
    val color: Color
)

val moreNavItems = listOf(
    MoreItem("Finance", "Track Wealth & SaaS", Icons.Default.MonetizationOn, 8, PrimaryCyan),
    MoreItem("Health", "Vitals & Heart Metrics", Icons.Default.Favorite, 9, SecondaryPurple),
    MoreItem("Goals", "Streaks & Habits", Icons.Default.TrackChanges, 3, Color(0xFF10B981)),
    MoreItem("Emergency SOS", "ICE Emergency Dialer", Icons.Default.Emergency, 5, EmergencyRed),
    MoreItem("Admin", "Console & Analytics", Icons.Default.AdminPanelSettings, 10, Color(0xFFFFB74D)),
    MoreItem("Showcase", "LifeOS AI Features", Icons.Default.Web, 7, Color(0xFF64B5F6))
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomNav(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    var showMoreSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    NavigationBar(
        windowInsets = WindowInsets.navigationBars,
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        primaryNavItems.forEach { item ->
            val isMoreTab = item.index == -1
            val isMoreActive = isMoreTab && (selectedTab in listOf(3, 5, 7, 8, 9, 10))
            val isSelected = selectedTab == item.index || isMoreActive

            val iconTint = if (isSelected) PrimaryCyan else MaterialTheme.colorScheme.onSurfaceVariant

            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    if (isMoreTab) {
                        showMoreSheet = true
                    } else {
                        onTabSelected(item.index)
                    }
                },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label,
                        tint = iconTint,
                        modifier = Modifier.size(20.dp)
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = iconTint
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                modifier = Modifier.testTag("nav_item_${item.label.lowercase()}")
            )
        }
    }

    // --- More Apps & Features Bottom Sheet ---
    if (showMoreSheet) {
        ModalBottomSheet(
            onDismissRequest = { showMoreSheet = false },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Apps,
                            contentDescription = "More Tools",
                            tint = PrimaryCyan,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "More LifeOS Modules",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    IconButton(onClick = { showMoreSheet = false }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(moreNavItems) { item ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .border(1.dp, item.color.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                                .clickable {
                                    onTabSelected(item.index)
                                    showMoreSheet = false
                                },
                            color = item.color.copy(alpha = 0.1f)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(item.color.copy(alpha = 0.25f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = item.icon,
                                        contentDescription = item.label,
                                        tint = item.color,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(10.dp))

                                Column {
                                    Text(
                                        text = item.label,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = item.subtitle,
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}
