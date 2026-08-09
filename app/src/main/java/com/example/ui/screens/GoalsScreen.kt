package com.example.ui.screens

import android.widget.Toast
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.GoalItem
import com.example.ui.components.CategoryChip
import com.example.ui.components.LifeOSCard
import com.example.ui.theme.AccentLime
import com.example.ui.theme.PrimaryCyan
import com.example.ui.theme.SecondaryPurple

@Composable
fun GoalsScreen(
    goals: List<GoalItem>,
    onIncrementProgress: (GoalItem) -> Unit,
    onAddGoal: (title: String, category: String, targetDays: Int, deadline: String) -> Unit,
    onDeleteGoal: (Int) -> Unit
) {
    val context = LocalContext.current
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedCategoryFilter by remember { mutableStateOf("All") }

    val categories = listOf("All", "Study", "Coding", "Fitness", "Reading", "Savings", "Habit")

    val filteredGoals = if (selectedCategoryFilter == "All") {
        goals
    } else {
        goals.filter { it.category.equals(selectedCategoryFilter, ignoreCase = true) }
    }

    val completedGoalsCount = goals.count { it.isCompleted || it.currentProgress >= it.targetDays }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .testTag("goals_screen_root")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // --- 1. Firestore Sync & Milestone Header ---
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
                        text = "Firestore Goal Sync Active",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = AccentLime
                    )
                }

                Text(
                    text = "$completedGoalsCount / ${goals.size} Accomplished",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = PrimaryCyan
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // --- 2. Goals & Habit Progress Chart Summary Card ---
            LifeOSCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = PrimaryCyan,
                backgroundColor = PrimaryCyan.copy(alpha = 0.08f)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.BarChart,
                                contentDescription = "Progress Chart",
                                tint = PrimaryCyan,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Weekly Habit & Goal Progress Chart",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(AccentLime)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text("+200 XP", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Progress Chart Visual Bars (Study, Coding, Fitness, Reading, Savings)
                    val categoriesChart = listOf(
                        "Study" to 0.75f,
                        "Coding" to 0.90f,
                        "Fitness" to 0.60f,
                        "Reading" to 0.80f,
                        "Savings" to 0.50f
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        categoriesChart.forEach { (catLabel, fillFraction) ->
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    modifier = Modifier
                                        .height(50.dp)
                                        .width(28.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(MaterialTheme.colorScheme.surfaceVariant),
                                    contentAlignment = Alignment.BottomCenter
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height((50 * fillFraction).dp)
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(
                                                when (catLabel) {
                                                    "Study" -> SecondaryPurple
                                                    "Coding" -> PrimaryCyan
                                                    "Fitness" -> AccentLime
                                                    "Reading" -> Color(0xFFFFB74D)
                                                    else -> Color(0xFF81C784)
                                                }
                                            )
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(catLabel, fontSize = 9.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // --- 3. Category Filter Chips ---
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(categories) { cat ->
                    val isSelected = selectedCategoryFilter == cat
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isSelected) PrimaryCyan else MaterialTheme.colorScheme.surfaceVariant)
                            .clickable { selectedCategoryFilter = cat }
                            .padding(horizontal = 14.dp, vertical = 7.dp)
                    ) {
                        Text(
                            text = when (cat) {
                                "Study" -> "📚 Study"
                                "Coding" -> "💻 Coding"
                                "Fitness" -> "🏋️ Fitness"
                                "Reading" -> "📖 Reading"
                                "Savings" -> "💰 Savings"
                                "Habit" -> "🔁 Habit"
                                else -> "All Goals"
                            },
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.Black else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // --- 4. Goals List ---
            if (filteredGoals.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp),
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
                                .padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(PrimaryCyan.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.TrackChanges,
                                    contentDescription = null,
                                    tint = PrimaryCyan,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "No Goals in $selectedCategoryFilter",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Track your long-term habits and targets here. Tap '+' to create your first goal!",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = { showAddDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryCyan),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Create Goal", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
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
                    items(filteredGoals, key = { it.id }) { goal ->
                        val progressFraction = (goal.currentProgress.toFloat() / goal.targetDays.coerceAtLeast(1)).coerceIn(0f, 1f)
                        val isFinished = goal.isCompleted || goal.currentProgress >= goal.targetDays

                        LifeOSCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("goal_item_${goal.id}")
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = goal.title,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            CategoryChip(category = goal.category)
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = "Target: ${goal.deadline}",
                                                fontSize = 11.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }

                                    IconButton(onClick = { onDeleteGoal(goal.id) }) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Delete Goal",
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Progress Row
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Progress: ${goal.currentProgress} / ${goal.targetDays} Days Streak 🔥",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (isFinished) AccentLime else PrimaryCyan
                                    )

                                    if (!isFinished) {
                                        Button(
                                            onClick = {
                                                onIncrementProgress(goal)
                                                Toast.makeText(context, "Progress updated! 🔥", Toast.LENGTH_SHORT).show()
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryCyan),
                                            shape = RoundedCornerShape(12.dp)
                                        ) {
                                            Text(
                                                text = "+1 Day",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.Black
                                            )
                                        }
                                    } else {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Default.CheckCircle,
                                                contentDescription = "Completed",
                                                tint = AccentLime,
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = "Goal Achieved!",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = AccentLime
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                LinearProgressIndicator(
                                    progress = { progressFraction },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(8.dp)
                                        .clip(RoundedCornerShape(4.dp)),
                                    color = if (isFinished) AccentLime else PrimaryCyan,
                                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                            }
                        }
                    }

                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }
            }
        }

        // --- Add Goal FAB ---
        FloatingActionButton(
            onClick = { showAddDialog = true },
            containerColor = AccentLime,
            contentColor = Color.Black,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
                .testTag("add_goal_fab")
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Add Goal")
        }

        // --- Add Goal Dialog ---
        if (showAddDialog) {
            var goalTitle by remember { mutableStateOf("") }
            var goalCategory by remember { mutableStateOf("Coding") }
            var targetDaysStr by remember { mutableStateOf("30") }
            var deadlineStr by remember { mutableStateOf("30 Days") }

            AlertDialog(
                onDismissRequest = { showAddDialog = false },
                title = { Text("Set New Goal or Habit", fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = goalTitle,
                            onValueChange = { goalTitle = it },
                            label = { Text("Goal / Habit Title (e.g. Study Algo)") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("add_goal_title_input")
                        )
                        OutlinedTextField(
                            value = goalCategory,
                            onValueChange = { goalCategory = it },
                            label = { Text("Category (Study, Coding, Fitness, Reading, Savings, Habit)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = targetDaysStr,
                            onValueChange = { targetDaysStr = it },
                            label = { Text("Target Days / Streak Target") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = deadlineStr,
                            onValueChange = { deadlineStr = it },
                            label = { Text("Target Deadline (e.g., Aug 30, 2026)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val days = targetDaysStr.toIntOrNull() ?: 30
                            if (goalTitle.isNotBlank()) {
                                onAddGoal(goalTitle, goalCategory, days, deadlineStr)
                                showAddDialog = false
                                Toast.makeText(context, "Goal added successfully!", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AccentLime)
                    ) {
                        Text("Save Goal", color = Color.Black, fontWeight = FontWeight.Bold)
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
