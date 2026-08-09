package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.FolderSpecial
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material.icons.filled.ViewSidebar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.GoalItem
import com.example.data.PlannerItem
import com.example.ui.components.CategoryChip
import com.example.ui.components.LifeOSCard
import com.example.ui.components.PriorityChip
import com.example.ui.theme.AccentLime
import com.example.ui.theme.PrimaryCyan
import com.example.ui.theme.SecondaryPurple
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun DashboardScreen(
    userName: String,
    plannerItems: List<PlannerItem>,
    goals: List<GoalItem>,
    onTogglePlannerItem: (PlannerItem) -> Unit,
    onNavigateTab: (Int) -> Unit,
    onQuickAiPrompt: (String) -> Unit,
    onEmergencyCall: () -> Unit
) {
    val completedPlannerCount = plannerItems.count { it.isCompleted }
    val totalPlannerCount = plannerItems.size
    val dailyScore = if (totalPlannerCount > 0) {
        ((completedPlannerCount.toFloat() / totalPlannerCount) * 100).toInt()
    } else 88

    var showSidebarDrawer by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    // Dynamic Time-Based Greeting
    val currentHour = remember { Calendar.getInstance().get(Calendar.HOUR_OF_DAY) }
    val greetingPrefix = when (currentHour) {
        in 0..11 -> "Good morning"
        in 12..16 -> "Good afternoon"
        else -> "Good evening"
    }
    val formattedDate = remember {
        val sdf = SimpleDateFormat("EEEE, MMMM d", Locale.getDefault())
        sdf.format(Date())
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .testTag("dashboard_screen_root")
    ) {
        val isWideScreen = maxWidth > 600.dp

        Row(modifier = Modifier.fillMaxSize()) {
            // --- Wide Screen Navigation Sidebar ---
            if (isWideScreen || showSidebarDrawer) {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier
                        .width(220.dp)
                        .fillMaxHeight(),
                    tonalElevation = 4.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = PrimaryCyan,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "LifeOS Sidebar",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            val navMenuItems = listOf(
                                "Home" to 0,
                                "AI Assistant" to 2,
                                "Planner" to 1,
                                "Vault Notes" to 4,
                                "Goals & Streaks" to 3,
                                "Finance" to 8,
                                "Health & Vitals" to 9,
                                "Emergency SOS" to 5,
                                "Admin Console" to 10,
                                "Profile & Settings" to 6
                            )

                            navMenuItems.forEach { (label, index) ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (index == 0) PrimaryCyan.copy(alpha = 0.2f) else Color.Transparent)
                                        .clickable {
                                            onNavigateTab(index)
                                            if (!isWideScreen) showSidebarDrawer = false
                                        }
                                        .padding(horizontal = 12.dp, vertical = 10.dp)
                                ) {
                                    Text(
                                        text = label,
                                        fontSize = 13.sp,
                                        fontWeight = if (index == 0) FontWeight.Bold else FontWeight.Normal,
                                        color = if (index == 0) PrimaryCyan else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }

                        if (!isWideScreen) {
                            Button(
                                onClick = { showSidebarDrawer = false },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surface),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Close Sidebar", color = MaterialTheme.colorScheme.onSurface)
                            }
                        }
                    }
                }
            }

            // --- Main Content Stream ---
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item { Spacer(modifier = Modifier.height(8.dp)) }

                // --- 1. Dynamic Time-Based Greeting Header ---
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "$greetingPrefix, $userName 👋",
                                fontSize = 21.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onSurface,
                                letterSpacing = (-0.5).sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "$formattedDate • High Energy Focus Day",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (!isWideScreen) {
                                IconButton(onClick = { showSidebarDrawer = !showSidebarDrawer }) {
                                    Icon(
                                        imageVector = Icons.Default.ViewSidebar,
                                        contentDescription = "Toggle Sidebar",
                                        tint = PrimaryCyan
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(PrimaryCyan.copy(alpha = 0.15f))
                                    .border(1.dp, PrimaryCyan.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "Score: $dailyScore/100",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryCyan
                                )
                            }
                        }
                    }
                }

                // --- 2. AI Daily Brief with "View Full Plan" Button ---
                item {
                    LifeOSCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("ai_daily_brief_card"),
                        borderColor = PrimaryCyan.copy(alpha = 0.5f),
                        backgroundColor = PrimaryCyan.copy(alpha = 0.08f)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clip(CircleShape)
                                            .background(PrimaryCyan),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.AutoAwesome,
                                            contentDescription = "AI Brief",
                                            tint = Color.Black,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "AI Daily Executive Brief",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }

                                Text(
                                    text = "Smart AI Engine",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AccentLime
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = "“You have $totalPlannerCount tasks scheduled today. Prioritize completing high-impact items during peak morning focus, then review your active goal streaks.”",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurface,
                                lineHeight = 18.sp
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // "View Full Plan" Button
                            Button(
                                onClick = { onNavigateTab(1) },
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryCyan.copy(alpha = 0.18f)),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.height(36.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("View Full Plan", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PrimaryCyan)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        imageVector = Icons.Default.ChevronRight,
                                        contentDescription = "View Full Plan",
                                        tint = PrimaryCyan,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // --- 3. Premium AI Search Bar ---
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .border(1.dp, PrimaryCyan.copy(alpha = 0.35f), RoundedCornerShape(16.dp))
                            .padding(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                tint = PrimaryCyan,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))

                            OutlinedTextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                placeholder = {
                                    Text(
                                        "Ask LifeOS AI to summarize, plan, or organize...",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                                keyboardActions = KeyboardActions(
                                    onSend = {
                                        if (searchQuery.isNotBlank()) {
                                            onQuickAiPrompt(searchQuery)
                                            onNavigateTab(2)
                                            searchQuery = ""
                                        }
                                    }
                                ),
                                colors = OutlinedTextFieldDefaults.colors(
                                    unfocusedBorderColor = Color.Transparent,
                                    focusedBorderColor = Color.Transparent,
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent
                                ),
                                modifier = Modifier.weight(1f)
                            )

                            IconButton(
                                onClick = {
                                    if (searchQuery.isNotBlank()) {
                                        onQuickAiPrompt(searchQuery)
                                        onNavigateTab(2)
                                        searchQuery = ""
                                    } else {
                                        onNavigateTab(2)
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Send,
                                    contentDescription = "Send AI",
                                    tint = PrimaryCyan,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Prompt Chip Shortcuts
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            val examplePrompts = listOf(
                                "Build my schedule",
                                "Show my documents",
                                "Create task",
                                "Analyze goals"
                            )
                            items(examplePrompts) { prompt ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(MaterialTheme.colorScheme.surface)
                                        .border(1.dp, PrimaryCyan.copy(alpha = 0.25f), RoundedCornerShape(12.dp))
                                        .clickable {
                                            onQuickAiPrompt(prompt)
                                            onNavigateTab(2)
                                        }
                                        .padding(horizontal = 10.dp, vertical = 5.dp)
                                ) {
                                    Text(
                                        text = "✨ $prompt",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }

                // --- 4. Today's Tasks (Top 3) ---
                item {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Today's Tasks",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "View Planner →",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryCyan,
                                modifier = Modifier.clickable { onNavigateTab(1) }
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        if (plannerItems.isEmpty()) {
                            LifeOSCard(
                                modifier = Modifier.fillMaxWidth(),
                                borderColor = PrimaryCyan.copy(alpha = 0.3f),
                                backgroundColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(44.dp)
                                            .clip(CircleShape)
                                            .background(PrimaryCyan.copy(alpha = 0.15f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            tint = PrimaryCyan,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        text = "All Clear for Today!",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "No pending tasks in your schedule. Tap below to add a new task or let AI optimize your agenda.",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        textAlign = TextAlign.Center
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Button(
                                        onClick = { onNavigateTab(1) },
                                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryCyan),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Text("+ Add First Task", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    }
                                }
                            }
                        } else {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                plannerItems.take(3).forEach { item ->
                                    LifeOSCard(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { onTogglePlannerItem(item) }
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Checkbox(
                                                    checked = item.isCompleted,
                                                    onCheckedChange = { onTogglePlannerItem(item) },
                                                    colors = CheckboxDefaults.colors(checkedColor = PrimaryCyan)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Column {
                                                    Text(
                                                        text = item.title,
                                                        fontSize = 13.sp,
                                                        fontWeight = FontWeight.SemiBold,
                                                        color = if (item.isCompleted) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface,
                                                        textDecoration = if (item.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                                                    )
                                                    Text(
                                                        text = "${item.timeLabel} • ${item.category}",
                                                        fontSize = 10.sp,
                                                        color = PrimaryCyan
                                                    )
                                                }
                                            }
                                            PriorityChip(priority = item.priority)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // --- 5. Quick Actions ---
                item {
                    Column {
                        Text(
                            text = "Quick Actions",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            val actions = listOf(
                                Triple("+ Task", Icons.Default.Add, 1),
                                Triple("Ask AI", Icons.Default.AutoAwesome, 2),
                                Triple("Vault", Icons.Default.FolderSpecial, 4),
                                Triple("More Tools", Icons.Default.Apps, -1)
                            )

                            actions.forEach { (label, icon, tabIndex) ->
                                Button(
                                    onClick = {
                                        if (tabIndex == -1) {
                                            showSidebarDrawer = true
                                        } else {
                                            onNavigateTab(tabIndex)
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier.padding(vertical = 4.dp)
                                    ) {
                                        Icon(
                                            imageVector = icon,
                                            contentDescription = null,
                                            tint = PrimaryCyan,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = label,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            maxLines = 1
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // --- 6. Progress Card (Goals + Focus Time Combined) ---
                item {
                    LifeOSCard(modifier = Modifier.fillMaxWidth()) {
                        Column {
                            // Section A: Goals Progress
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.TrackChanges,
                                        contentDescription = "Goals",
                                        tint = AccentLime,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Active Goals Streak",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                Text(
                                    text = "14/30 Days (46%)",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AccentLime
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            LinearProgressIndicator(
                                progress = { 0.46f },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = AccentLime,
                                trackColor = MaterialTheme.colorScheme.surfaceVariant
                            )

                            Spacer(modifier = Modifier.height(14.dp))
                            HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
                            Spacer(modifier = Modifier.height(14.dp))

                            // Section B: Focus Time Progress
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Timer,
                                        contentDescription = "Focus Time",
                                        tint = PrimaryCyan,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Daily Focus Time",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                Text(
                                    text = "2h 45m / 4h Goal (68%)",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryCyan
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            LinearProgressIndicator(
                                progress = { 0.68f },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = PrimaryCyan,
                                trackColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        }
                    }
                }

                // --- 7. Mini Calendar Preview ---
                item {
                    LifeOSCard(modifier = Modifier.fillMaxWidth()) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.DateRange,
                                        contentDescription = "Calendar",
                                        tint = PrimaryCyan,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "July 2026 • Week View",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                Text(
                                    text = "Today: Fri 24",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryCyan
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Mini Week Days Row
                            val weekDays = listOf(
                                "M" to "20",
                                "T" to "21",
                                "W" to "22",
                                "T" to "23",
                                "F" to "24",
                                "S" to "25",
                                "S" to "26"
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                weekDays.forEachIndexed { idx, (dayLetter, dateNum) ->
                                    val isToday = idx == 4 // Fri 24
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(if (isToday) PrimaryCyan else Color.Transparent)
                                            .border(
                                                width = 1.dp,
                                                color = if (isToday) PrimaryCyan else MaterialTheme.colorScheme.surfaceVariant,
                                                shape = RoundedCornerShape(12.dp)
                                            )
                                            .padding(horizontal = 10.dp, vertical = 8.dp)
                                    ) {
                                        Text(
                                            text = dayLetter,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isToday) Color.Black else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = dateNum,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = if (isToday) Color.Black else MaterialTheme.colorScheme.onSurface
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Box(
                                            modifier = Modifier
                                                .size(4.dp)
                                                .clip(CircleShape)
                                                .background(if (isToday) Color.Black else if (idx < 5) PrimaryCyan else Color.Transparent)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(20.dp)) }
            }
        }
    }
}
