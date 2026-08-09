package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.MonitorWeight
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.LifeOSCard
import com.example.ui.theme.AccentLime
import com.example.ui.theme.EmergencyRed
import com.example.ui.theme.PrimaryCyan
import com.example.ui.theme.SecondaryPurple

data class MedicineReminderItem(
    val id: Int,
    val name: String,
    val dosage: String,
    val time: String,
    var isTaken: Boolean
)

data class ExerciseLogItem(
    val id: Int,
    val title: String,
    val category: String, // Running, Gym, Yoga, Cycling
    val durationMinutes: Int,
    val caloriesBurned: Int,
    val date: String = "Today"
)

@Composable
fun HealthScreen(
    onNavigateTab: (Int) -> Unit = {}
) {
    val context = LocalContext.current

    var selectedSectionFilter by remember { mutableStateOf("Overview") }

    // --- State 1: Water Intake ---
    var currentWaterMl by remember { mutableStateOf(1750) }
    val targetWaterMl = 2500

    // --- State 2: Sleep Tracker ---
    var sleepHours by remember { mutableStateOf(7.5f) }
    var sleepQuality by remember { mutableStateOf("88% Optimal") }

    // --- State 3: Weight Tracker ---
    var currentWeightKg by remember { mutableStateOf(72.5) }
    val targetWeightKg = 70.0
    var userHeightCm by remember { mutableStateOf(178.0) }
    val bmi = currentWeightKg / ((userHeightCm / 100) * (userHeightCm / 100))

    // --- State 4: Medicine Reminders ---
    val medicineList = remember {
        mutableStateListOf(
            MedicineReminderItem(1, "Multivitamin + Omega 3", "1 Tablet", "08:00 AM", true),
            MedicineReminderItem(2, "Vitamin D3 2000 IU", "1 Capsule", "01:00 PM", true),
            MedicineReminderItem(3, "Magnesium Glycinate", "2 Capsules", "09:30 PM", false)
        )
    }

    // --- State 5: Exercise Logs ---
    val exerciseList = remember {
        mutableStateListOf(
            ExerciseLogItem(1, "Morning HIIT Cardio", "Running", 30, 320, "Today"),
            ExerciseLogItem(2, "Evening Core & Resistance Workout", "Gym", 45, 410, "Today")
        )
    }

    var showAddMedDialog by remember { mutableStateOf(false) }
    var showAddExerciseDialog by remember { mutableStateOf(false) }
    var showUpdateWeightDialog by remember { mutableStateOf(false) }

    val totalCaloriesBurned = exerciseList.sumOf { it.caloriesBurned }
    val totalExerciseMinutes = exerciseList.sumOf { it.durationMinutes }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .testTag("health_tracker_root")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // --- 1. Health Connect & Firestore Sync Banner ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(AccentLime.copy(alpha = 0.15f))
                        .padding(horizontal = 10.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CloudDone,
                        contentDescription = "Health Connect Sync",
                        tint = AccentLime,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Google Health Connect Sync Active",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = AccentLime
                    )
                }

                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(PrimaryCyan.copy(alpha = 0.2f))
                        .clickable {
                            Toast.makeText(context, "Exporting Daily Health Report (PDF)...", Toast.LENGTH_SHORT).show()
                        }
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Download,
                        contentDescription = "Export Report",
                        tint = PrimaryCyan,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Daily Report",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryCyan
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // --- 2. Category Filter Chips ---
            val sections = listOf("Overview", "Water", "Sleep", "Medicine", "Weight", "Exercise", "Daily Report")
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(sections) { section ->
                    val isSelected = selectedSectionFilter == section
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isSelected) AccentLime else MaterialTheme.colorScheme.surfaceVariant)
                            .clickable { selectedSectionFilter = section }
                            .padding(horizontal = 14.dp, vertical = 7.dp)
                    ) {
                        Text(
                            text = when (section) {
                                "Water" -> "💧 Water"
                                "Sleep" -> "🌙 Sleep"
                                "Medicine" -> "💊 Medicine"
                                "Weight" -> "⚖️ Weight"
                                "Exercise" -> "🏃 Exercise"
                                "Daily Report" -> "📋 Daily Report"
                                else -> "❤️ Overview"
                            },
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.Black else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                // --- 3. Overview Highlights Header ---
                if (selectedSectionFilter == "Overview" || selectedSectionFilter == "Daily Report") {
                    item {
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
                                        Icon(imageVector = Icons.Default.Favorite, contentDescription = null, tint = EmergencyRed, modifier = Modifier.size(20.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "Daily Health Vitals Score",
                                            fontSize = 15.sp,
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
                                        Text("92 / 100", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = Color.Black)
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    VitalSummaryTile("Water", "${currentWaterMl}ml", PrimaryCyan)
                                    VitalSummaryTile("Sleep", "${sleepHours} hrs", SecondaryPurple)
                                    VitalSummaryTile("Weight", "${String.format("%.1f", currentWeightKg)} kg", AccentLime)
                                    VitalSummaryTile("Meds", "${medicineList.count { it.isTaken }}/${medicineList.size}", Color(0xFFFFB74D))
                                }
                            }
                        }
                    }
                }

                // --- 4. Water Intake Section ---
                if (selectedSectionFilter == "Overview" || selectedSectionFilter == "Water") {
                    item {
                        val waterFraction = (currentWaterMl.toFloat() / targetWaterMl).coerceIn(0f, 1f)
                        LifeOSCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("water_intake_card"),
                            borderColor = PrimaryCyan
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(imageVector = Icons.Default.WaterDrop, contentDescription = null, tint = PrimaryCyan, modifier = Modifier.size(20.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text("Water Intake Tracker", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                            Text("Goal: $targetWaterMl ml / day", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                    }

                                    Text(
                                        text = "$currentWaterMl / $targetWaterMl ml",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = PrimaryCyan
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                LinearProgressIndicator(
                                    progress = { waterFraction },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(10.dp)
                                        .clip(RoundedCornerShape(5.dp)),
                                    color = PrimaryCyan,
                                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = {
                                            currentWaterMl += 250
                                            Toast.makeText(context, "+250 ml added!", Toast.LENGTH_SHORT).show()
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryCyan.copy(alpha = 0.2f)),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("+250 ml", color = PrimaryCyan, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    }

                                    Button(
                                        onClick = {
                                            currentWaterMl += 500
                                            Toast.makeText(context, "+500 ml added!", Toast.LENGTH_SHORT).show()
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryCyan),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("+500 ml", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }
                }

                // --- 5. Sleep Tracking Section ---
                if (selectedSectionFilter == "Overview" || selectedSectionFilter == "Sleep") {
                    item {
                        LifeOSCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("sleep_tracker_card"),
                            borderColor = SecondaryPurple
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(imageVector = Icons.Default.Nightlight, contentDescription = null, tint = SecondaryPurple, modifier = Modifier.size(20.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text("Sleep Duration & Quality", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                            Text("Bedtime: 11:15 PM • Wake: 06:45 AM", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                    }

                                    Text(
                                        text = "$sleepHours hrs",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = SecondaryPurple
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(SecondaryPurple.copy(alpha = 0.15f))
                                            .padding(8.dp)
                                    ) {
                                        Text("Deep Sleep: 2h 15m (30%)", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = SecondaryPurple)
                                    }

                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(AccentLime.copy(alpha = 0.15f))
                                            .padding(8.dp)
                                    ) {
                                        Text("Score: $sleepQuality", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AccentLime)
                                    }
                                }
                            }
                        }
                    }
                }

                // --- 6. Medicine Reminders Section ---
                if (selectedSectionFilter == "Overview" || selectedSectionFilter == "Medicine") {
                    item {
                        LifeOSCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("medicine_reminders_card"),
                            borderColor = Color(0xFFFFB74D)
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(imageVector = Icons.Default.Medication, contentDescription = null, tint = Color(0xFFFFB74D), modifier = Modifier.size(20.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Medicine & Supplement Reminders", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                    }

                                    IconButton(onClick = { showAddMedDialog = true }) {
                                        Icon(imageVector = Icons.Default.Add, contentDescription = "Add Pill", tint = Color(0xFFFFB74D))
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                medicineList.forEach { med ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(MaterialTheme.colorScheme.surfaceVariant)
                                            .clickable {
                                                val index = medicineList.indexOf(med)
                                                if (index != -1) {
                                                    medicineList[index] = med.copy(isTaken = !med.isTaken)
                                                }
                                            }
                                            .padding(10.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = if (med.isTaken) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                                contentDescription = null,
                                                tint = if (med.isTaken) AccentLime else MaterialTheme.colorScheme.onSurfaceVariant,
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Column {
                                                Text(med.name, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                                Text("${med.dosage} • ${med.time}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            }
                                        }

                                        Text(
                                            text = if (med.isTaken) "TAKEN" else "PENDING",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = if (med.isTaken) AccentLime else EmergencyRed
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // --- 7. Weight & BMI Section ---
                if (selectedSectionFilter == "Overview" || selectedSectionFilter == "Weight") {
                    item {
                        LifeOSCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("weight_tracker_card"),
                            borderColor = AccentLime
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(imageVector = Icons.Default.MonitorWeight, contentDescription = null, tint = AccentLime, modifier = Modifier.size(20.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text("Weight & BMI Tracker", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                            Text("Target: $targetWeightKg kg", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                    }

                                    IconButton(onClick = { showUpdateWeightDialog = true }) {
                                        Text("${String.format("%.1f", currentWeightKg)} kg", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = AccentLime)
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("BMI Score: ${String.format("%.1f", bmi)}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PrimaryCyan)
                                    Text(
                                        text = when {
                                            bmi < 18.5 -> "Underweight"
                                            bmi <= 24.9 -> "Normal / Healthy Weight"
                                            else -> "Overweight"
                                        },
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AccentLime
                                    )
                                }
                            }
                        }
                    }
                }

                // --- 8. Exercise & Workout Logs Section ---
                if (selectedSectionFilter == "Overview" || selectedSectionFilter == "Exercise") {
                    item {
                        LifeOSCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("exercise_tracker_card"),
                            borderColor = PrimaryCyan
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(imageVector = Icons.Default.FitnessCenter, contentDescription = null, tint = PrimaryCyan, modifier = Modifier.size(20.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text("Workouts & Active Exercise", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                            Text("$totalExerciseMinutes mins • $totalCaloriesBurned kcal burned today", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                    }

                                    IconButton(onClick = { showAddExerciseDialog = true }) {
                                        Icon(imageVector = Icons.Default.Add, contentDescription = "Log Exercise", tint = PrimaryCyan)
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                exerciseList.forEach { ex ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(MaterialTheme.colorScheme.surfaceVariant)
                                            .padding(10.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(imageVector = Icons.Default.DirectionsRun, contentDescription = null, tint = PrimaryCyan, modifier = Modifier.size(18.dp))
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Column {
                                                Text(ex.title, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                                Text("${ex.category} • ${ex.durationMinutes} mins", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            }
                                        }

                                        Text("${ex.caloriesBurned} kcal", fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = AccentLime)
                                    }
                                }
                            }
                        }
                    }
                }

                // --- 9. Daily AI Health Report Summary Card ---
                if (selectedSectionFilter == "Overview" || selectedSectionFilter == "Daily Report") {
                    item {
                        LifeOSCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("daily_health_report_card"),
                            borderColor = AccentLime,
                            backgroundColor = AccentLime.copy(alpha = 0.08f)
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = AccentLime, modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("AI Daily Health & Wellness Summary", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = AccentLime)
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = "Great job today! Water intake is at 70% of daily quota. Sleep quality score was 88% with optimal REM cycle duration. All morning and afternoon supplements have been taken. Active energy expenditure was 730 kcal across 2 workout sessions.",
                                    fontSize = 11.sp,
                                    lineHeight = 16.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }

        // --- Add Medicine Dialog ---
        if (showAddMedDialog) {
            var medName by remember { mutableStateOf("") }
            var medDosage by remember { mutableStateOf("1 Tablet") }
            var medTime by remember { mutableStateOf("08:00 AM") }

            AlertDialog(
                onDismissRequest = { showAddMedDialog = false },
                title = { Text("Add Medicine Reminder", fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = medName,
                            onValueChange = { medName = it },
                            label = { Text("Medicine / Supplement Name") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = medDosage,
                            onValueChange = { medDosage = it },
                            label = { Text("Dosage (e.g., 1 Tablet, 2 Capsules)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = medTime,
                            onValueChange = { medTime = it },
                            label = { Text("Scheduled Time (e.g., 08:00 AM)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (medName.isNotBlank()) {
                                medicineList.add(MedicineReminderItem(medicineList.size + 1, medName, medDosage, medTime, false))
                                showAddMedDialog = false
                                Toast.makeText(context, "Medicine reminder saved!", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFB74D))
                    ) {
                        Text("Save Reminder", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddMedDialog = false }) { Text("Cancel") }
                }
            )
        }

        // --- Log Exercise Dialog ---
        if (showAddExerciseDialog) {
            var exTitle by remember { mutableStateOf("") }
            var exCategory by remember { mutableStateOf("Running") }
            var exDurationStr by remember { mutableStateOf("30") }
            var exCaloriesStr by remember { mutableStateOf("250") }

            AlertDialog(
                onDismissRequest = { showAddExerciseDialog = false },
                title = { Text("Log Workout / Exercise", fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = exTitle,
                            onValueChange = { exTitle = it },
                            label = { Text("Workout Title (e.g. Afternoon Run)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = exCategory,
                            onValueChange = { exCategory = it },
                            label = { Text("Category (Running, Gym, Yoga, Cycling)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = exDurationStr,
                            onValueChange = { exDurationStr = it },
                            label = { Text("Duration (Minutes)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = exCaloriesStr,
                            onValueChange = { exCaloriesStr = it },
                            label = { Text("Calories Burned (kcal)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val duration = exDurationStr.toIntOrNull() ?: 30
                            val cals = exCaloriesStr.toIntOrNull() ?: 200
                            if (exTitle.isNotBlank()) {
                                exerciseList.add(ExerciseLogItem(exerciseList.size + 1, exTitle, exCategory, duration, cals))
                                showAddExerciseDialog = false
                                Toast.makeText(context, "Workout logged!", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryCyan)
                    ) {
                        Text("Save Exercise", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddExerciseDialog = false }) { Text("Cancel") }
                }
            )
        }

        // --- Update Weight Dialog ---
        if (showUpdateWeightDialog) {
            var weightStr by remember { mutableStateOf(currentWeightKg.toString()) }

            AlertDialog(
                onDismissRequest = { showUpdateWeightDialog = false },
                title = { Text("Update Current Weight", fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = weightStr,
                            onValueChange = { weightStr = it },
                            label = { Text("Current Weight (kg)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val w = weightStr.toDoubleOrNull()
                            if (w != null && w > 0) {
                                currentWeightKg = w
                                showUpdateWeightDialog = false
                                Toast.makeText(context, "Weight updated!", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AccentLime)
                    ) {
                        Text("Update", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showUpdateWeightDialog = false }) { Text("Cancel") }
                }
            )
        }
    }
}

@Composable
private fun VitalSummaryTile(title: String, value: String, accentColor: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(title, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.height(2.dp))
        Text(value, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = accentColor)
    }
}
