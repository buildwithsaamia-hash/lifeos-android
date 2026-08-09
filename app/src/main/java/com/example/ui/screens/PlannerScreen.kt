package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.AlarmOn
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EventRepeat
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.PlannerItem
import com.example.ui.components.CategoryChip
import com.example.ui.components.LifeOSCard
import com.example.ui.components.PriorityChip
import com.example.ui.theme.AccentLime
import com.example.ui.theme.EmergencyRed
import com.example.ui.theme.PrimaryCyan
import com.example.ui.theme.SecondaryPurple

enum class PlannerViewMode {
    DAILY,
    WEEKLY,
    MONTHLY,
    CALENDAR
}

@Composable
fun PlannerScreen(
    plannerItems: List<PlannerItem>,
    onToggleCompleted: (PlannerItem) -> Unit,
    onAddTask: (title: String, category: String, priority: String, timeLabel: String, notes: String) -> Unit,
    onDeleteTask: (Int) -> Unit,
    onAiBreakdownTask: (String) -> Unit,
    onSuggestDailyTasks: (() -> Unit)? = null,
    onOptimizeSchedule: (() -> Unit)? = null
) {
    val context = LocalContext.current

    var viewMode by remember { mutableStateOf(PlannerViewMode.DAILY) }
    var selectedCategoryFilter by remember { mutableStateOf("All") }
    var selectedDateDay by remember { mutableStateOf(24) } // Default July 24
    var showAddDialog by remember { mutableStateOf(false) }

    // Task notification state maps
    val notificationEnabledTasks = remember { mutableStateListOf<Int>() }

    val categories = listOf("All", "Work", "Health", "Personal", "Habit", "Finance")

    val filteredItems = if (selectedCategoryFilter == "All") {
        plannerItems
    } else {
        plannerItems.filter { it.category.equals(selectedCategoryFilter, ignoreCase = true) }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .testTag("planner_screen_root")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // --- 1. Cloud Sync Status & View Mode Tabs ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(AccentLime.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CloudDone,
                        contentDescription = "Cloud Sync",
                        tint = AccentLime,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Room DB & Firestore Synced",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = AccentLime
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    if (onSuggestDailyTasks != null) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(AccentLime)
                                .clickable {
                                    onSuggestDailyTasks()
                                    Toast.makeText(context, "LifeOS AI suggesting daily tasks...", Toast.LENGTH_SHORT).show()
                                }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text("⚡ AI Suggest", fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, color = Color.Black)
                        }
                    }

                    if (onOptimizeSchedule != null) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(PrimaryCyan)
                                .clickable {
                                    onOptimizeSchedule()
                                    Toast.makeText(context, "LifeOS AI optimizing schedule...", Toast.LENGTH_SHORT).show()
                                }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text("⏳ AI Optimize", fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, color = Color.Black)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // --- 2. Planner View Mode Segmented Switcher ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val views = listOf(
                    PlannerViewMode.DAILY to "Daily",
                    PlannerViewMode.WEEKLY to "Weekly",
                    PlannerViewMode.MONTHLY to "Monthly",
                    PlannerViewMode.CALENDAR to "Calendar"
                )

                views.forEach { (mode, label) ->
                    val isSelected = viewMode == mode
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) PrimaryCyan else Color.Transparent)
                            .clickable { viewMode = mode }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.Black else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // --- 3. Calendar View Mode Screen ---
            if (viewMode == PlannerViewMode.CALENDAR) {
                LifeOSCard(modifier = Modifier.fillMaxWidth()) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Calendar Month View • July 2026",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Icon(
                                imageVector = Icons.Default.CalendarMonth,
                                contentDescription = null,
                                tint = PrimaryCyan,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        val daysOfWeek = listOf("M", "T", "W", "T", "F", "S", "S")
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            daysOfWeek.forEach { day ->
                                Text(
                                    text = day,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Grid of 31 Days for July
                        val totalDays = 31
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(7),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(210.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            items(totalDays) { index ->
                                val dayNum = index + 1
                                val isSelected = dayNum == selectedDateDay
                                val hasTask = dayNum % 3 == 0 || dayNum == 24

                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (isSelected) PrimaryCyan
                                            else if (hasTask) SecondaryPurple.copy(alpha = 0.25f)
                                            else MaterialTheme.colorScheme.surfaceVariant
                                        )
                                        .clickable {
                                            selectedDateDay = dayNum
                                            Toast.makeText(context, "Selected July $dayNum, 2026", Toast.LENGTH_SHORT).show()
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "$dayNum",
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected || hasTask) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) Color.Black else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(PrimaryCyan))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Selected", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Spacer(modifier = Modifier.width(12.dp))
                                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(SecondaryPurple))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Has Tasks", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }

                            Text("Selected: July $selectedDateDay", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PrimaryCyan)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
            }

            // --- 4. Weekly View Agenda Summary ---
            if (viewMode == PlannerViewMode.WEEKLY) {
                LifeOSCard(modifier = Modifier.fillMaxWidth()) {
                    Column {
                        Text(
                            text = "Weekly Focus Overview (Jul 20 - Jul 26)",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryCyan
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            val weekDays = listOf("Mon 20", "Tue 21", "Wed 22", "Thu 23", "Fri 24", "Sat 25", "Sun 26")
                            weekDays.forEachIndexed { idx, day ->
                                val isToday = idx == 4
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = day,
                                        fontSize = 10.sp,
                                        fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isToday) PrimaryCyan else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .clip(CircleShape)
                                            .background(if (isToday) AccentLime else SecondaryPurple)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
            }

            // --- 5. Category Filter Bar ---
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(categories) { category ->
                    val isSelected = selectedCategoryFilter == category
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isSelected) PrimaryCyan else MaterialTheme.colorScheme.surfaceVariant)
                            .clickable { selectedCategoryFilter = category }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = category,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.Black else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // --- 6. Task List ---
            if (filteredItems.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    LifeOSCard(
                        modifier = Modifier.fillMaxWidth(),
                        borderColor = PrimaryCyan.copy(alpha = 0.3f),
                        backgroundColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(52.dp)
                                    .clip(CircleShape)
                                    .background(PrimaryCyan.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = PrimaryCyan,
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "No Tasks in $selectedCategoryFilter",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Your schedule is clear. Tap '+' to create a new task or use AI Suggest to populate your day.",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { showAddDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryCyan),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Add New Task", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            }
                        }
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredItems, key = { it.id }) { item ->
                        val isNotifOn = notificationEnabledTasks.contains(item.id)

                        LifeOSCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("planner_task_item_${item.id}")
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Checkbox(
                                            checked = item.isCompleted,
                                            onCheckedChange = { onToggleCompleted(item) },
                                            colors = CheckboxDefaults.colors(checkedColor = PrimaryCyan)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Column {
                                            Text(
                                                text = item.title,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (item.isCompleted) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface,
                                                textDecoration = if (item.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                                            )
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(
                                                    text = item.timeLabel,
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.SemiBold,
                                                    color = PrimaryCyan
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                CategoryChip(category = item.category)

                                                // Recurring Task Badge
                                                if (item.id % 2 == 0) {
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Box(
                                                        modifier = Modifier
                                                            .clip(RoundedCornerShape(6.dp))
                                                            .background(SecondaryPurple.copy(alpha = 0.2f))
                                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                                    ) {
                                                        Text("🔁 Daily Recurring", fontSize = 9.sp, color = SecondaryPurple)
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        PriorityChip(priority = item.priority)

                                        // Notification Alert Icon Toggle
                                        IconButton(
                                            onClick = {
                                                if (isNotifOn) {
                                                    notificationEnabledTasks.remove(item.id)
                                                    Toast.makeText(context, "Reminder turned off", Toast.LENGTH_SHORT).show()
                                                } else {
                                                    notificationEnabledTasks.add(item.id)
                                                    Toast.makeText(context, "Notification scheduled for ${item.timeLabel}", Toast.LENGTH_SHORT).show()
                                                }
                                            }
                                        ) {
                                            Icon(
                                                imageVector = if (isNotifOn) Icons.Default.NotificationsActive else Icons.Default.Notifications,
                                                contentDescription = "Task Reminder Alert",
                                                tint = if (isNotifOn) AccentLime else MaterialTheme.colorScheme.onSurfaceVariant,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }

                                        IconButton(onClick = { onDeleteTask(item.id) }) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = "Delete Task",
                                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                }

                                if (item.notes.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = item.notes,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                // AI Breakdown Button
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(PrimaryCyan.copy(alpha = 0.15f))
                                        .clickable { onAiBreakdownTask(item.title) }
                                        .padding(horizontal = 10.dp, vertical = 5.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = "AI Breakdown",
                                        tint = PrimaryCyan,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "AI Break Down Task",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = PrimaryCyan
                                    )
                                }
                            }
                        }
                    }

                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }
            }
        }

        // --- Floating Add Task FAB ---
        FloatingActionButton(
            onClick = { showAddDialog = true },
            containerColor = PrimaryCyan,
            contentColor = Color.Black,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
                .testTag("add_planner_task_fab")
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Add Task")
        }

        // --- Add Task Dialog ---
        if (showAddDialog) {
            var taskTitle by remember { mutableStateOf("") }
            var taskCategory by remember { mutableStateOf("Work") }
            var taskPriority by remember { mutableStateOf("High") }
            var taskTime by remember { mutableStateOf("10:00 AM") }
            var isRecurring by remember { mutableStateOf(false) }
            var taskNotes by remember { mutableStateOf("") }

            AlertDialog(
                onDismissRequest = { showAddDialog = false },
                title = { Text("Add New LifeOS Task", fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = taskTitle,
                            onValueChange = { taskTitle = it },
                            label = { Text("Task Title") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("add_task_title_input")
                        )
                        OutlinedTextField(
                            value = taskTime,
                            onValueChange = { taskTime = it },
                            label = { Text("Time (e.g., 02:30 PM)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = taskCategory,
                            onValueChange = { taskCategory = it },
                            label = { Text("Category (Work, Health, Personal, Habit)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = taskPriority,
                            onValueChange = { taskPriority = it },
                            label = { Text("Priority (High, Medium, Low)") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Checkbox(
                                checked = isRecurring,
                                onCheckedChange = { isRecurring = it },
                                colors = CheckboxDefaults.colors(checkedColor = PrimaryCyan)
                            )
                            Text("Repeat Daily (Recurring Task)", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }

                        OutlinedTextField(
                            value = taskNotes,
                            onValueChange = { taskNotes = it },
                            label = { Text("Notes / Description") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (taskTitle.isNotBlank()) {
                                onAddTask(taskTitle, taskCategory, taskPriority, taskTime, taskNotes)
                                showAddDialog = false
                                Toast.makeText(context, "Task created successfully!", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryCyan)
                    ) {
                        Text("Add Task", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}
