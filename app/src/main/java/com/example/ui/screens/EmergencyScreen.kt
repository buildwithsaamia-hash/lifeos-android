package com.example.ui.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.filled.Bloodtype
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContactPhone
import androidx.compose.material.icons.filled.CopyAll
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Emergency
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PhoneInTalk
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.EmergencyContact
import com.example.ui.components.LifeOSCard
import com.example.ui.theme.AccentLime
import com.example.ui.theme.EmergencyRed
import com.example.ui.theme.PrimaryCyan
import com.example.ui.theme.SecondaryPurple

data class MedicalProfile(
    val fullName: String = "Alexander Wright",
    val bloodType: String = "O+",
    val age: Int = 29,
    val allergies: String = "Penicillin, Shellfish",
    val chronicConditions: String = "Mild Asthma",
    val medications: String = "Albuterol Inhaler (as needed), Multivitamins",
    val organDonor: Boolean = true,
    val insuranceProvider: String = "BlueCross Health Protection",
    val policyNumber: String = "POL-99281-US",
    val physicianName: String = "Dr. Sarah Jenkins (Cardiology)",
    val physicianPhone: String = "+1 (555) 234-5678"
)

@Composable
fun EmergencyScreen(
    contacts: List<EmergencyContact>,
    onCallContact: (String) -> Unit,
    onAddContact: (name: String, relation: String, phone: String, bloodType: String, isPrimary: Boolean, notes: String) -> Unit,
    onDeleteContact: (Int) -> Unit
) {
    val context = LocalContext.current

    var medicalProfile by remember { mutableStateOf(MedicalProfile()) }
    var selectedSection by remember { mutableStateOf("SOS & Beacon") }

    // SOS Trigger state
    var isSosActive by remember { mutableStateOf(false) }
    var sosCountdown by remember { mutableIntStateOf(5) }
    var isCountdownRunning by remember { mutableStateOf(false) }
    var isSirenPlaying by remember { mutableStateOf(false) }
    var isStrobeActive by remember { mutableStateOf(false) }

    // Live GPS state
    var isLiveSharingEnabled by remember { mutableStateOf(true) }
    var gpsLat by remember { mutableStateOf(37.7749) }
    var gpsLng by remember { mutableStateOf(-122.4194) }
    val mockAddress = "742 Evergreen Terrace, San Francisco, CA 94107"

    var showEditProfileDialog by remember { mutableStateOf(false) }
    var showAddContactDialog by remember { mutableStateOf(false) }

    val primaryContact = contacts.firstOrNull { it.isPrimary } ?: contacts.firstOrNull()

    // Countdown effect when ONE CLICK SOS is pressed
    LaunchedEffect(isCountdownRunning, sosCountdown) {
        if (isCountdownRunning && sosCountdown > 0) {
            kotlinx.coroutines.delay(1000L)
            sosCountdown--
            if (sosCountdown == 0) {
                isCountdownRunning = false
                isSosActive = true
                isSirenPlaying = true
                Toast.makeText(context, "🚨 ONE CLICK SOS DISPATCHED TO ICE CONTACTS & 911!", Toast.LENGTH_LONG).show()
                primaryContact?.let { onCallContact(it.phone) }
            }
        }
    }

    // SOS Pulsing animation
    val infiniteTransition = rememberInfiniteTransition()
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.12f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .testTag("sos_emergency_screen_root")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // --- 1. Top Section Filter Chips ---
            val tabs = listOf("SOS & Beacon", "Emergency Contacts", "Medical Profile", "Live Location Map")
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(tabs) { tab ->
                    val isSelected = selectedSection == tab
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isSelected) EmergencyRed else MaterialTheme.colorScheme.surfaceVariant)
                            .clickable { selectedSection = tab }
                            .padding(horizontal = 14.dp, vertical = 7.dp)
                    ) {
                        Text(
                            text = when (tab) {
                                "SOS & Beacon" -> "🚨 One-Click SOS"
                                "Emergency Contacts" -> "📞 ICE Contacts (${contacts.size})"
                                "Medical Profile" -> "🩺 Medical Profile"
                                else -> "📍 Live GPS Location"
                            },
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                // ==========================================
                // SECTION 1: ONE CLICK SOS & BEACON
                // ==========================================
                if (selectedSection == "SOS & Beacon" || selectedSection == "Live Location Map") {
                    item {
                        LifeOSCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("one_click_sos_hero_card"),
                            borderColor = EmergencyRed,
                            backgroundColor = EmergencyRed.copy(alpha = 0.12f)
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Emergency,
                                            contentDescription = null,
                                            tint = EmergencyRed,
                                            modifier = Modifier.size(22.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "ONE-CLICK SOS SYSTEM",
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = EmergencyRed
                                        )
                                    }

                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(EmergencyRed)
                                            .padding(horizontal = 8.dp, vertical = 3.dp)
                                    ) {
                                        Text("ICE ACTIVE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                // GIANT ONE-CLICK SOS BUTTON
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.padding(vertical = 10.dp)
                                ) {
                                    // Outer animated ripple ring
                                    Box(
                                        modifier = Modifier
                                            .size(130.dp)
                                            .scale(if (isCountdownRunning || isSosActive) pulseScale else 1f)
                                            .clip(CircleShape)
                                            .background(EmergencyRed.copy(alpha = 0.25f))
                                    )

                                    // Main SOS Button
                                    Button(
                                        onClick = {
                                            if (!isCountdownRunning && !isSosActive) {
                                                isCountdownRunning = true
                                                sosCountdown = 5
                                            } else {
                                                // Cancel SOS
                                                isCountdownRunning = false
                                                isSosActive = false
                                                isSirenPlaying = false
                                                Toast.makeText(context, "SOS Dispatch Cancelled", Toast.LENGTH_SHORT).show()
                                            }
                                        },
                                        shape = CircleShape,
                                        colors = ButtonDefaults.buttonColors(containerColor = EmergencyRed),
                                        modifier = Modifier
                                            .size(110.dp)
                                            .testTag("one_click_sos_big_button")
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Icon(
                                                imageVector = Icons.Default.PhoneInTalk,
                                                contentDescription = "SOS",
                                                tint = Color.White,
                                                modifier = Modifier.size(32.dp)
                                            )
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = if (isCountdownRunning) "$sosCountdown SEC" else if (isSosActive) "CANCEL" else "SOS",
                                                fontSize = if (isCountdownRunning) 14.sp else 16.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = Color.White
                                            )
                                        }
                                    }
                                }

                                Text(
                                    text = if (isCountdownRunning) "⚠️ DISPATCHING IN $sosCountdown SECONDS — TAP BUTTON TO CANCEL"
                                    else if (isSosActive) "🚨 SOS ACTIVE! EMERGENCY BEACON & LIVE LOCATION BROADCASTING"
                                    else "Tap big red button to trigger 1-Click SOS call & instant SMS to ICE contacts",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSosActive || isCountdownRunning) EmergencyRed else MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(horizontal = 12.dp)
                                )

                                Spacer(modifier = Modifier.height(14.dp))

                                Divider(color = EmergencyRed.copy(alpha = 0.3f))

                                Spacer(modifier = Modifier.height(12.dp))

                                // Quick Utility Controls (Siren & Flashlight Strobe)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(if (isSirenPlaying) EmergencyRed.copy(alpha = 0.3f) else MaterialTheme.colorScheme.surfaceVariant)
                                            .clickable {
                                                isSirenPlaying = !isSirenPlaying
                                                Toast.makeText(context, if (isSirenPlaying) "🔊 Loud Emergency Alarm Siren Playing!" else "Siren Stopped", Toast.LENGTH_SHORT).show()
                                            }
                                            .padding(horizontal = 12.dp, vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.VolumeUp,
                                            contentDescription = "Siren Alarm",
                                            tint = if (isSirenPlaying) EmergencyRed else MaterialTheme.colorScheme.onSurface,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = if (isSirenPlaying) "Siren ON" else "Sound Siren",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSirenPlaying) EmergencyRed else MaterialTheme.colorScheme.onSurface
                                        )
                                    }

                                    Row(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(if (isStrobeActive) AccentLime.copy(alpha = 0.3f) else MaterialTheme.colorScheme.surfaceVariant)
                                            .clickable {
                                                isStrobeActive = !isStrobeActive
                                                Toast.makeText(context, if (isStrobeActive) "⚡ Strobe Flashlight Pulsing!" else "Flashlight Off", Toast.LENGTH_SHORT).show()
                                            }
                                            .padding(horizontal = 12.dp, vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.FlashOn,
                                            contentDescription = "Strobe Light",
                                            tint = if (isStrobeActive) AccentLime else MaterialTheme.colorScheme.onSurface,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = if (isStrobeActive) "Strobe ON" else "SOS Strobe",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isStrobeActive) Color.Black else MaterialTheme.colorScheme.onSurface
                                        )
                                    }

                                    Row(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(PrimaryCyan.copy(alpha = 0.2f))
                                            .clickable {
                                                if (primaryContact != null) {
                                                    onCallContact(primaryContact.phone)
                                                } else {
                                                    onCallContact("911")
                                                }
                                            }
                                            .padding(horizontal = 12.dp, vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Phone,
                                            contentDescription = "Call Primary",
                                            tint = PrimaryCyan,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Call 911 / Primary",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = PrimaryCyan
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // ==========================================
                // SECTION 2: LIVE LOCATION READY UI
                // ==========================================
                if (selectedSection == "SOS & Beacon" || selectedSection == "Live Location Map") {
                    item {
                        LifeOSCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("live_location_ready_card"),
                            borderColor = PrimaryCyan
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.GpsFixed,
                                            contentDescription = null,
                                            tint = PrimaryCyan,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text("Live Location Beacon", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                                            Text("GPS Coordinates & Address Ready", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                    }

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = if (isLiveSharingEnabled) "BEACON ON" else "BEACON OFF",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isLiveSharingEnabled) AccentLime else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Switch(
                                            checked = isLiveSharingEnabled,
                                            onCheckedChange = { isLiveSharingEnabled = it },
                                            colors = SwitchDefaults.colors(checkedThumbColor = AccentLime, checkedTrackColor = AccentLime.copy(alpha = 0.3f))
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Visual Map Graphic Canvas Simulator
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(110.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color(0xFF1E293B))
                                        .border(1.dp, PrimaryCyan.copy(alpha = 0.4f), RoundedCornerShape(12.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Canvas(modifier = Modifier.fillMaxSize()) {
                                        val gridColor = Color(0xFF334155)
                                        val mapLineColor = Color(0xFF475569)

                                        // Draw simulated street grid
                                        for (x in 0..size.width.toInt() step 60) {
                                            drawLine(gridColor, Offset(x.toFloat(), 0f), Offset(x.toFloat(), size.height), strokeWidth = 1f)
                                        }
                                        for (y in 0..size.height.toInt() step 40) {
                                            drawLine(gridColor, Offset(0f, y.toFloat()), Offset(size.width, y.toFloat()), strokeWidth = 1f)
                                        }

                                        // Draw diagonal main road
                                        drawLine(mapLineColor, Offset(0f, size.height * 0.8f), Offset(size.width, size.height * 0.2f), strokeWidth = 8f)
                                        drawLine(Color(0xFFF59E0B), Offset(0f, size.height * 0.8f), Offset(size.width, size.height * 0.2f), strokeWidth = 3f)

                                        // Draw GPS Radar Circle around current location
                                        drawCircle(
                                            color = PrimaryCyan.copy(alpha = 0.25f),
                                            radius = 45f,
                                            center = Offset(size.width / 2, size.height / 2)
                                        )
                                        drawCircle(
                                            color = PrimaryCyan,
                                            radius = 10f,
                                            center = Offset(size.width / 2, size.height / 2)
                                        )
                                    }

                                    // Map Badge overlay
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier
                                            .align(Alignment.BottomStart)
                                            .padding(8.dp)
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(Color.Black.copy(alpha = 0.75f))
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text("Lat: $gpsLat • Lng: $gpsLng", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = PrimaryCyan)
                                        Text("Accuracy: ±4m • Updated: Just now", fontSize = 9.sp, color = Color.White)
                                    }

                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .padding(8.dp)
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(AccentLime)
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text("LIVE GPS ACTIVE", fontSize = 9.sp, fontWeight = FontWeight.ExtraBold, color = Color.Black)
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Text(
                                    text = "Current Address: $mockAddress",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = {
                                            Toast.makeText(context, "Location coordinates copied to clipboard!", Toast.LENGTH_SHORT).show()
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(imageVector = Icons.Default.CopyAll, contentDescription = null, modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Copy Pin", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }

                                    Button(
                                        onClick = {
                                            Toast.makeText(context, "Sending SMS with GPS coordinates to ${contacts.size} contacts...", Toast.LENGTH_SHORT).show()
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryCyan),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(imageVector = Icons.Default.Sms, contentDescription = null, tint = Color.Black, modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Send SMS Pin", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // ==========================================
                // SECTION 3: MEDICAL PROFILE & BLOOD GROUP
                // ==========================================
                if (selectedSection == "SOS & Beacon" || selectedSection == "Medical Profile") {
                    item {
                        LifeOSCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("medical_profile_card"),
                            borderColor = EmergencyRed
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.MedicalServices,
                                            contentDescription = null,
                                            tint = EmergencyRed,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text("Medical ID & Profile", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                                            Text("${medicalProfile.fullName}, Age ${medicalProfile.age}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                    }

                                    IconButton(onClick = { showEditProfileDialog = true }) {
                                        Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit Profile", tint = EmergencyRed)
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Prominent Blood Group Badge Banner
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(EmergencyRed.copy(alpha = 0.15f))
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(42.dp)
                                                .clip(CircleShape)
                                                .background(EmergencyRed),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = medicalProfile.bloodType,
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = Color.White
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(10.dp))

                                        Column {
                                            Text("Blood Group: ${medicalProfile.bloodType}", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = EmergencyRed)
                                            Text(
                                                text = if (medicalProfile.organDonor) "Registered Organ Donor • Donor ID #991" else "Organ Donor: No",
                                                fontSize = 11.sp,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                    }

                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(EmergencyRed)
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text("CRITICAL ID", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                // Detailed Medical Info Grid
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("Known Allergies", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text(medicalProfile.allergies, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = EmergencyRed)
                                    }

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("Chronic Conditions", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text(medicalProfile.chronicConditions, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("Active Medications", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text(medicalProfile.medications, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                    }

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("Insurance Provider", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text("${medicalProfile.insuranceProvider} (${medicalProfile.policyNumber})", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = "Primary Physician: ${medicalProfile.physicianName} • ${medicalProfile.physicianPhone}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryCyan
                                )
                            }
                        }
                    }
                }

                // ==========================================
                // SECTION 4: EMERGENCY CONTACTS (ICE)
                // ==========================================
                if (selectedSection == "SOS & Beacon" || selectedSection == "Emergency Contacts") {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Emergency Contacts (In Case of Emergency - ICE)",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            IconButton(onClick = { showAddContactDialog = true }) {
                                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Contact", tint = EmergencyRed)
                            }
                        }
                    }

                    items(contacts, key = { it.id }) { contact ->
                        LifeOSCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("emergency_contact_item_${contact.id}")
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(42.dp)
                                            .clip(CircleShape)
                                            .background(if (contact.isPrimary) EmergencyRed.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Phone,
                                            contentDescription = "Phone",
                                            tint = if (contact.isPrimary) EmergencyRed else MaterialTheme.colorScheme.onSurface,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(10.dp))

                                    Column {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = contact.name,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            if (contact.isPrimary) {
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Box(
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(6.dp))
                                                        .background(EmergencyRed)
                                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                                ) {
                                                    Text("PRIMARY ICE", fontSize = 9.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                                                }
                                            }
                                        }

                                        Text(
                                            text = "${contact.relation} • ${contact.phone}",
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )

                                        if (contact.medicalNotes.isNotBlank()) {
                                            Text(
                                                text = contact.medicalNotes,
                                                fontSize = 11.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(EmergencyRed)
                                            .clickable { onCallContact(contact.phone) }
                                            .padding(horizontal = 10.dp, vertical = 6.dp)
                                    ) {
                                        Text("CALL", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    }

                                    Spacer(modifier = Modifier.width(4.dp))

                                    IconButton(onClick = { onDeleteContact(contact.id) }) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Delete",
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }

        // --- Add Contact FAB ---
        FloatingActionButton(
            onClick = { showAddContactDialog = true },
            containerColor = EmergencyRed,
            contentColor = Color.White,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
                .testTag("add_emergency_contact_fab")
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Add Contact")
        }

        // --- Add Contact Dialog ---
        if (showAddContactDialog) {
            var contactName by remember { mutableStateOf("") }
            var contactRelation by remember { mutableStateOf("Family") }
            var contactPhone by remember { mutableStateOf("") }
            var contactBloodType by remember { mutableStateOf("O+") }
            var isPrimary by remember { mutableStateOf(false) }
            var contactNotes by remember { mutableStateOf("") }

            AlertDialog(
                onDismissRequest = { showAddContactDialog = false },
                title = { Text("Add Emergency Contact (ICE)", fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = contactName,
                            onValueChange = { contactName = it },
                            label = { Text("Full Name") },
                            modifier = Modifier.fillMaxWidth().testTag("add_contact_name_input")
                        )
                        OutlinedTextField(
                            value = contactRelation,
                            onValueChange = { contactRelation = it },
                            label = { Text("Relation (Spouse, Sister, Physician)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = contactPhone,
                            onValueChange = { contactPhone = it },
                            label = { Text("Phone Number") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = contactNotes,
                            onValueChange = { contactNotes = it },
                            label = { Text("Medical / ICE Notes") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = isPrimary,
                                onCheckedChange = { isPrimary = it }
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Set as Primary ICE Contact", fontSize = 13.sp)
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (contactName.isNotBlank() && contactPhone.isNotBlank()) {
                                onAddContact(contactName, contactRelation, contactPhone, contactBloodType, isPrimary, contactNotes)
                                showAddContactDialog = false
                                Toast.makeText(context, "Emergency contact added!", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = EmergencyRed)
                    ) {
                        Text("Save Contact", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddContactDialog = false }) { Text("Cancel") }
                }
            )
        }

        // --- Edit Medical Profile Dialog ---
        if (showEditProfileDialog) {
            var nameInput by remember { mutableStateOf(medicalProfile.fullName) }
            var bloodInput by remember { mutableStateOf(medicalProfile.bloodType) }
            var ageInput by remember { mutableStateOf(medicalProfile.age.toString()) }
            var allergiesInput by remember { mutableStateOf(medicalProfile.allergies) }
            var conditionsInput by remember { mutableStateOf(medicalProfile.chronicConditions) }
            var medsInput by remember { mutableStateOf(medicalProfile.medications) }
            var insuranceInput by remember { mutableStateOf(medicalProfile.insuranceProvider) }

            AlertDialog(
                onDismissRequest = { showEditProfileDialog = false },
                title = { Text("Edit Emergency Medical ID", fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = nameInput,
                            onValueChange = { nameInput = it },
                            label = { Text("Full Name") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedTextField(
                                value = bloodInput,
                                onValueChange = { bloodInput = it },
                                label = { Text("Blood Group (e.g. O+, A-)") },
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = ageInput,
                                onValueChange = { ageInput = it },
                                label = { Text("Age") },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        OutlinedTextField(
                            value = allergiesInput,
                            onValueChange = { allergiesInput = it },
                            label = { Text("Allergies") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = conditionsInput,
                            onValueChange = { conditionsInput = it },
                            label = { Text("Chronic Conditions") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = medsInput,
                            onValueChange = { medsInput = it },
                            label = { Text("Daily Medications") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = insuranceInput,
                            onValueChange = { insuranceInput = it },
                            label = { Text("Health Insurance Provider & Policy") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val ageVal = ageInput.toIntOrNull() ?: 29
                            medicalProfile = medicalProfile.copy(
                                fullName = nameInput,
                                bloodType = bloodInput,
                                age = ageVal,
                                allergies = allergiesInput,
                                chronicConditions = conditionsInput,
                                medications = medsInput,
                                insuranceProvider = insuranceInput
                            )
                            showEditProfileDialog = false
                            Toast.makeText(context, "Medical ID updated successfully!", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = EmergencyRed)
                    ) {
                        Text("Update Medical ID", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showEditProfileDialog = false }) { Text("Cancel") }
                }
            )
        }
    }
}
