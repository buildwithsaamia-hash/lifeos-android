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
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.GTranslate
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MarkEmailRead
import androidx.compose.material.icons.filled.MarkEmailUnread
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import com.example.ui.components.LifeOSCard
import com.example.ui.theme.AccentLime
import com.example.ui.theme.EmergencyRed
import com.example.ui.theme.PrimaryCyan
import com.example.ui.theme.SecondaryPurple

data class AchievementBadge(
    val title: String,
    val description: String,
    val iconEmoji: String,
    val xpBonus: Int,
    val isUnlocked: Boolean
)

@Composable
fun SettingsScreen(
    userName: String,
    userEmail: String,
    isLoggedIn: Boolean,
    isEmailVerified: Boolean,
    authProvider: String,
    isDarkTheme: Boolean,
    onUpdateUserName: (String) -> Unit,
    onToggleDarkTheme: (Boolean) -> Unit,
    onResetSeedData: () -> Unit,
    onLogout: () -> Unit,
    onSendVerification: () -> Unit,
    onShowAuthModal: () -> Unit
) {
    val context = LocalContext.current

    var selectedSectionFilter by remember { mutableStateOf("All") }
    var nameInput by remember(userName) { mutableStateOf(userName) }
    var selectedAvatarEmoji by remember { mutableStateOf("👨‍💻") }
    var selectedLanguage by remember { mutableStateOf("English (US) 🇺🇸") }
    var currentSubscriptionPlan by remember { mutableStateOf("LifeOS Pro SaaS (Active)") }

    // Settings Toggles
    var pushNotificationsEnabled by remember { mutableStateOf(true) }
    var cloudAutoSyncEnabled by remember { mutableStateOf(true) }
    var biometricUnlockEnabled by remember { mutableStateOf(true) }
    var pinLockEnabled by remember { mutableStateOf(true) }
    var twoFactorAuthEnabled by remember { mutableStateOf(true) }

    // Dialog States
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showAvatarDialog by remember { mutableStateOf(false) }
    var showLanguageDialog by remember { mutableStateOf(false) }
    var showSubscriptionDialog by remember { mutableStateOf(false) }
    var showChangePasswordDialog by remember { mutableStateOf(false) }
    var verificationSentMsg by remember { mutableStateOf<String?>(null) }

    // Gamification state
    val currentLevel = 12
    val currentXp = 3450
    val maxLevelXp = 5000

    val achievementsList = remember {
        listOf(
            AchievementBadge("7-Day Streak Master", "Maintained daily planner logs for 7 days", "🔥", 250, true),
            AchievementBadge("Goal Crusher", "Successfully completed 10 long-term milestones", "🎯", 500, true),
            AchievementBadge("Deep Focus Champion", "Logged over 25 hours in Deep Focus Mode", "⚡", 400, true),
            AchievementBadge("Hydration Hero", "Achieved daily water intake goal 5 days in a row", "💧", 150, true),
            AchievementBadge("Vault Guardian", "Secured 5 sensitive notes with AES-256 encryption", "🔐", 200, true),
            AchievementBadge("AI Co-Pilot Explorer", "Prompted Gemini AI Assistant 50+ times", "🤖", 350, true),
            AchievementBadge("Finance Guru", "Tracked budgets & maintained positive net savings", "💰", 300, false),
            AchievementBadge("Emergency Ready", "Configured ICE contact & Medical ID profile", "🚨", 150, true)
        )
    }

    val availableAvatars = listOf("👨‍💻", "👩‍💼", "🚀", "🧘", "🎨", "🛡️", "🤖", "🔥", "👑", "⚡")
    val availableLanguages = listOf(
        "English (US) 🇺🇸",
        "Spanish (Español) 🇪🇸",
        "German (Deutsch) 🇩🇪",
        "French (Français) 🇫🇷",
        "Japanese (日本語) 🇯🇵",
        "Chinese (中文) 🇨🇳",
        "Portuguese (Português) 🇧🇷"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .testTag("user_profile_settings_root")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // --- Section Filter Chips ---
            val filters = listOf("All", "Profile & Photo", "Achievements", "Statistics", "Subscription", "Settings", "Security & Language")
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(filters) { filter ->
                    val isSelected = selectedSectionFilter == filter
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isSelected) PrimaryCyan else MaterialTheme.colorScheme.surfaceVariant)
                            .clickable { selectedSectionFilter = filter }
                            .padding(horizontal = 14.dp, vertical = 7.dp)
                    ) {
                        Text(
                            text = when (filter) {
                                "Profile & Photo" -> "👤 Profile"
                                "Achievements" -> "🏆 Achievements"
                                "Statistics" -> "📊 Statistics"
                                "Subscription" -> "💎 Subscription"
                                "Settings" -> "⚙️ Settings"
                                "Security & Language" -> "🔐 Security"
                                else -> "✨ Overview"
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
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                // ==========================================
                // SECTION 1: PROFILE & PHOTO CARD
                // ==========================================
                if (selectedSectionFilter == "All" || selectedSectionFilter == "Profile & Photo") {
                    item {
                        LifeOSCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("user_profile_photo_card"),
                            borderColor = PrimaryCyan.copy(alpha = 0.5f)
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        // Avatar Circle with Edit Badge
                                        Box(
                                            contentAlignment = Alignment.BottomEnd,
                                            modifier = Modifier
                                                .clickable { showAvatarDialog = true }
                                                .testTag("profile_avatar_box")
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(56.dp)
                                                    .clip(CircleShape)
                                                    .background(PrimaryCyan.copy(alpha = 0.2f))
                                                    .border(2.dp, PrimaryCyan, CircleShape),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(selectedAvatarEmoji, fontSize = 28.sp)
                                            }

                                            Box(
                                                modifier = Modifier
                                                    .size(20.dp)
                                                    .clip(CircleShape)
                                                    .background(PrimaryCyan),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.PhotoCamera,
                                                    contentDescription = "Change Photo",
                                                    tint = Color.Black,
                                                    modifier = Modifier.size(12.dp)
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.width(12.dp))

                                        Column {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(
                                                    text = if (isLoggedIn) userName else "Guest User",
                                                    fontSize = 17.sp,
                                                    fontWeight = FontWeight.ExtraBold,
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Box(
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(6.dp))
                                                        .background(AccentLime)
                                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                                ) {
                                                    Text("LVL $currentLevel", fontSize = 9.sp, fontWeight = FontWeight.ExtraBold, color = Color.Black)
                                                }
                                            }

                                            Text(
                                                text = if (isLoggedIn) userEmail else "Offline Mode (Unauthenticated)",
                                                fontSize = 11.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }

                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(if (isLoggedIn) AccentLime.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant)
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = if (isLoggedIn) authProvider else "Guest",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isLoggedIn) AccentLime else MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))
                                HorizontalDivider()
                                Spacer(modifier = Modifier.height(12.dp))

                                if (isLoggedIn) {
                                    // Email Verification status
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = if (isEmailVerified) Icons.Default.MarkEmailRead else Icons.Default.MarkEmailUnread,
                                                contentDescription = null,
                                                tint = if (isEmailVerified) AccentLime else SecondaryPurple,
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = if (isEmailVerified) "Verified Account ✓" else "Email Pending Verification",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = if (isEmailVerified) AccentLime else SecondaryPurple
                                            )
                                        }

                                        if (!isEmailVerified) {
                                            Button(
                                                onClick = {
                                                    onSendVerification()
                                                    verificationSentMsg = "Verification email sent to $userEmail!"
                                                },
                                                colors = ButtonDefaults.buttonColors(containerColor = SecondaryPurple),
                                                shape = RoundedCornerShape(8.dp)
                                            ) {
                                                Text("Verify Now", fontSize = 11.sp, color = Color.White)
                                            }
                                        }
                                    }

                                    if (verificationSentMsg != null) {
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = verificationSentMsg ?: "",
                                            fontSize = 11.sp,
                                            color = AccentLime
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))

                                    // Display Name Edit Form
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        OutlinedTextField(
                                            value = nameInput,
                                            onValueChange = { nameInput = it },
                                            label = { Text("Display Name") },
                                            modifier = Modifier
                                                .weight(1f)
                                                .testTag("settings_name_input")
                                        )

                                        Button(
                                            onClick = {
                                                onUpdateUserName(nameInput)
                                                Toast.makeText(context, "Profile name saved!", Toast.LENGTH_SHORT).show()
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryCyan),
                                            modifier = Modifier
                                                .align(Alignment.CenterVertically)
                                                .testTag("save_name_button"),
                                            shape = RoundedCornerShape(10.dp)
                                        ) {
                                            Text("Save", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        }
                                    }
                                } else {
                                    Button(
                                        onClick = { onShowAuthModal() },
                                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryCyan),
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Text("Sign In or Register LifeOS Account", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }
                }

                // ==========================================
                // SECTION 2: ACHIEVEMENTS & GAMIFICATION
                // ==========================================
                if (selectedSectionFilter == "All" || selectedSectionFilter == "Achievements") {
                    item {
                        LifeOSCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("user_achievements_card"),
                            borderColor = AccentLime
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(imageVector = Icons.Default.Star, contentDescription = null, tint = AccentLime, modifier = Modifier.size(20.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text("Gamified Level & Achievements", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                                            Text("Level 12 • Productivity Specialist", fontSize = 11.sp, color = AccentLime)
                                        }
                                    }

                                    Text("$currentXp / $maxLevelXp XP", fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = AccentLime)
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                LinearProgressIndicator(
                                    progress = { currentXp.toFloat() / maxLevelXp },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(8.dp)
                                        .clip(RoundedCornerShape(4.dp)),
                                    color = AccentLime,
                                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                                )

                                Spacer(modifier = Modifier.height(14.dp))

                                Text("Unlocked Badges & Milestones (${achievementsList.count { it.isUnlocked }}/${achievementsList.size})", fontSize = 12.sp, fontWeight = FontWeight.Bold)

                                Spacer(modifier = Modifier.height(8.dp))

                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    achievementsList.forEach { badge ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(if (badge.isUnlocked) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                                                .padding(8.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(badge.iconEmoji, fontSize = 20.sp)
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Column {
                                                    Text(badge.title, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                                    Text(badge.description, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                                }
                                            }

                                            Text(
                                                text = "+${badge.xpBonus} XP",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = if (badge.isUnlocked) AccentLime else MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // ==========================================
                // SECTION 3: STATISTICS DASHBOARD
                // ==========================================
                if (selectedSectionFilter == "All" || selectedSectionFilter == "Statistics") {
                    item {
                        LifeOSCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("user_statistics_card"),
                            borderColor = SecondaryPurple
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = Icons.Default.Psychology, contentDescription = null, tint = SecondaryPurple, modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("LifeOS Productivity & Life Statistics", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    StatBox("Tasks Done", "142", PrimaryCyan)
                                    StatBox("Focus Hours", "38.5 hrs", SecondaryPurple)
                                    StatBox("Health Score", "92 / 100", AccentLime)
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    StatBox("Net Savings", "$14,200", AccentLime)
                                    StatBox("Goals Reached", "8 / 10", PrimaryCyan)
                                    StatBox("Vault Items", "18 Secured", SecondaryPurple)
                                }
                            }
                        }
                    }
                }

                // ==========================================
                // SECTION 4: SUBSCRIPTION (PRO TIER)
                // ==========================================
                if (selectedSectionFilter == "All" || selectedSectionFilter == "Subscription") {
                    item {
                        LifeOSCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("user_subscription_card"),
                            borderColor = AccentLime,
                            backgroundColor = AccentLime.copy(alpha = 0.08f)
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(imageVector = Icons.Default.WorkspacePremium, contentDescription = null, tint = AccentLime, modifier = Modifier.size(22.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(currentSubscriptionPlan, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = AccentLime)
                                            Text("Renews on Aug 24, 2026 • Unlimited AI Access", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                    }

                                    Button(
                                        onClick = { showSubscriptionDialog = true },
                                        colors = ButtonDefaults.buttonColors(containerColor = AccentLime),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text("Manage", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    PlanFeatureRow("✓ Unlimited Gemini 3.5 Flash AI Co-Pilot & Chat")
                                    PlanFeatureRow("✓ Real-time Google Health Connect Sync")
                                    PlanFeatureRow("✓ 256-Bit AES Hardware Vault Encryption")
                                    PlanFeatureRow("✓ Auto Cloud Backup & Multi-device Sync")
                                }
                            }
                        }
                    }
                }

                // ==========================================
                // SECTION 5: APP SETTINGS & DARK MODE
                // ==========================================
                if (selectedSectionFilter == "All" || selectedSectionFilter == "Settings") {
                    item {
                        LifeOSCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("app_settings_card")
                        ) {
                            Column {
                                Text("App Preferences & Theme", fontSize = 15.sp, fontWeight = FontWeight.Bold)

                                Spacer(modifier = Modifier.height(10.dp))

                                // Dark Mode Switch
                                SettingsSwitchRow(
                                    title = "Obsidian Dark Theme",
                                    subtitle = "High contrast eye-safe dark canvas",
                                    icon = if (isDarkTheme) Icons.Default.DarkMode else Icons.Default.LightMode,
                                    iconTint = PrimaryCyan,
                                    isChecked = isDarkTheme,
                                    onCheckedChange = { onToggleDarkTheme(it) },
                                    testTag = "theme_toggle_switch"
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                // Push Notifications Toggle
                                SettingsSwitchRow(
                                    title = "Push Notifications & Alarms",
                                    subtitle = "Planner reminders, pills & calendar alerts",
                                    icon = Icons.Default.Notifications,
                                    iconTint = SecondaryPurple,
                                    isChecked = pushNotificationsEnabled,
                                    onCheckedChange = { pushNotificationsEnabled = it },
                                    testTag = "notifications_toggle"
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                // Cloud Auto-Sync Toggle
                                SettingsSwitchRow(
                                    title = "Cloud Auto-Sync",
                                    subtitle = "Real-time sync across devices",
                                    icon = Icons.Default.Sync,
                                    iconTint = AccentLime,
                                    isChecked = cloudAutoSyncEnabled,
                                    onCheckedChange = { cloudAutoSyncEnabled = it },
                                    testTag = "cloud_sync_toggle"
                                )
                            }
                        }
                    }
                }

                // ==========================================
                // SECTION 6: SECURITY & LANGUAGE
                // ==========================================
                if (selectedSectionFilter == "All" || selectedSectionFilter == "Security & Language") {
                    item {
                        LifeOSCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("security_language_card"),
                            borderColor = PrimaryCyan
                        ) {
                            Column {
                                Text("Security, Privacy & Language", fontSize = 15.sp, fontWeight = FontWeight.Bold)

                                Spacer(modifier = Modifier.height(10.dp))

                                // Language Selector Row
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(MaterialTheme.colorScheme.surfaceVariant)
                                        .clickable { showLanguageDialog = true }
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(imageVector = Icons.Default.Language, contentDescription = null, tint = PrimaryCyan, modifier = Modifier.size(20.dp))
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text("App Language", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                            Text(selectedLanguage, fontSize = 11.sp, color = PrimaryCyan)
                                        }
                                    }

                                    Icon(imageVector = Icons.Default.GTranslate, contentDescription = null, tint = PrimaryCyan, modifier = Modifier.size(18.dp))
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Biometric Unlock Switch
                                SettingsSwitchRow(
                                    title = "Biometric Unlock (Fingerprint / Face)",
                                    subtitle = "Require biometrics when opening LifeOS",
                                    icon = Icons.Default.Fingerprint,
                                    iconTint = AccentLime,
                                    isChecked = biometricUnlockEnabled,
                                    onCheckedChange = { biometricUnlockEnabled = it },
                                    testTag = "biometric_switch"
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                // App PIN Lock
                                SettingsSwitchRow(
                                    title = "4-Digit PIN Lock Code",
                                    subtitle = "Secures encrypted vault and personal logs",
                                    icon = Icons.Default.Lock,
                                    iconTint = PrimaryCyan,
                                    isChecked = pinLockEnabled,
                                    onCheckedChange = { pinLockEnabled = it },
                                    testTag = "pin_lock_switch"
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                // 2FA Status
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(MaterialTheme.colorScheme.surfaceVariant)
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(imageVector = Icons.Default.Shield, contentDescription = null, tint = SecondaryPurple, modifier = Modifier.size(20.dp))
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text("Two-Factor Authentication (2FA)", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                            Text("Authenticator App / SMS 2FA Active", fontSize = 11.sp, color = AccentLime)
                                        }
                                    }

                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(AccentLime)
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text("ACTIVE", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Button(
                                    onClick = { showChangePasswordDialog = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("Change Password & Security Keys", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                                }
                            }
                        }
                    }
                }

                // Logout Button & Re-seed Data
                if (selectedSectionFilter == "All" || selectedSectionFilter == "Settings") {
                    item {
                        LifeOSCard(modifier = Modifier.fillMaxWidth()) {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(
                                    onClick = { onResetSeedData() },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(imageVector = Icons.Default.Refresh, contentDescription = null, tint = PrimaryCyan, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Re-Seed Sample Demo Data", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                                    }
                                }

                                if (isLoggedIn) {
                                    Button(
                                        onClick = { showLogoutDialog = true },
                                        colors = ButtonDefaults.buttonColors(containerColor = EmergencyRed.copy(alpha = 0.2f)),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("logout_button"),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(imageVector = Icons.Default.ExitToApp, contentDescription = null, tint = EmergencyRed, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("Log Out of LifeOS Account", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = EmergencyRed)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }

        // --- Avatar Chooser Dialog ---
        if (showAvatarDialog) {
            AlertDialog(
                onDismissRequest = { showAvatarDialog = false },
                title = { Text("Choose Profile Avatar Photo", fontWeight = FontWeight.Bold) },
                text = {
                    Column {
                        Text("Select your personal avatar style:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            availableAvatars.take(5).forEach { avatar ->
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape)
                                        .background(if (selectedAvatarEmoji == avatar) PrimaryCyan.copy(alpha = 0.3f) else MaterialTheme.colorScheme.surfaceVariant)
                                        .border(if (selectedAvatarEmoji == avatar) 2.dp else 0.dp, PrimaryCyan, CircleShape)
                                        .clickable {
                                            selectedAvatarEmoji = avatar
                                            showAvatarDialog = false
                                            Toast.makeText(context, "Avatar updated!", Toast.LENGTH_SHORT).show()
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(avatar, fontSize = 22.sp)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            availableAvatars.drop(5).forEach { avatar ->
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape)
                                        .background(if (selectedAvatarEmoji == avatar) PrimaryCyan.copy(alpha = 0.3f) else MaterialTheme.colorScheme.surfaceVariant)
                                        .border(if (selectedAvatarEmoji == avatar) 2.dp else 0.dp, PrimaryCyan, CircleShape)
                                        .clickable {
                                            selectedAvatarEmoji = avatar
                                            showAvatarDialog = false
                                            Toast.makeText(context, "Avatar updated!", Toast.LENGTH_SHORT).show()
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(avatar, fontSize = 22.sp)
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showAvatarDialog = false }) { Text("Close") }
                }
            )
        }

        // --- Language Selector Dialog ---
        if (showLanguageDialog) {
            AlertDialog(
                onDismissRequest = { showLanguageDialog = false },
                title = { Text("Select App Language", fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        availableLanguages.forEach { lang ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (selectedLanguage == lang) PrimaryCyan.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant)
                                    .clickable {
                                        selectedLanguage = lang
                                        showLanguageDialog = false
                                        Toast.makeText(context, "Language set to $lang", Toast.LENGTH_SHORT).show()
                                    }
                                    .padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(lang, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                                if (selectedLanguage == lang) {
                                    Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = PrimaryCyan, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showLanguageDialog = false }) { Text("Cancel") }
                }
            )
        }

        // --- Subscription Management Dialog ---
        if (showSubscriptionDialog) {
            AlertDialog(
                onDismissRequest = { showSubscriptionDialog = false },
                title = { Text("Manage SaaS Subscription Plan", fontWeight = FontWeight.Bold) },
                text = {
                    Column {
                        Text("Current Plan: $currentSubscriptionPlan", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = AccentLime)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("• Renews automatically on August 24, 2026", fontSize = 11.sp)
                        Text("• Billing Amount: $9.99 / month", fontSize = 11.sp)
                        Text("• Payment Method: Visa ending in **** 4821", fontSize = 11.sp)
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showSubscriptionDialog = false
                            Toast.makeText(context, "Subscription settings updated!", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AccentLime)
                    ) {
                        Text("OK", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showSubscriptionDialog = false }) { Text("Cancel") }
                }
            )
        }

        // --- Change Password Dialog ---
        if (showChangePasswordDialog) {
            var oldPass by remember { mutableStateOf("") }
            var newPass by remember { mutableStateOf("") }

            AlertDialog(
                onDismissRequest = { showChangePasswordDialog = false },
                title = { Text("Change Password", fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = oldPass,
                            onValueChange = { oldPass = it },
                            label = { Text("Current Password") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = newPass,
                            onValueChange = { newPass = it },
                            label = { Text("New Secure Password") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (newPass.length >= 6) {
                                showChangePasswordDialog = false
                                Toast.makeText(context, "Password changed successfully!", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryCyan)
                    ) {
                        Text("Update Password", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showChangePasswordDialog = false }) { Text("Cancel") }
                }
            )
        }

        // --- Logout Dialog ---
        if (showLogoutDialog) {
            AlertDialog(
                onDismissRequest = { showLogoutDialog = false },
                title = { Text("Log Out of LifeOS?", fontWeight = FontWeight.Bold) },
                text = { Text("Are you sure you want to log out? Your local data will remain saved in your encrypted Room Database.") },
                confirmButton = {
                    Button(
                        onClick = {
                            showLogoutDialog = false
                            onLogout()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = EmergencyRed)
                    ) {
                        Text("Log Out", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showLogoutDialog = false }) { Text("Cancel") }
                }
            )
        }
    }
}

@Composable
private fun StatBox(label: String, value: String, color: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(vertical = 8.dp, horizontal = 12.dp)
    ) {
        Text(label, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.height(2.dp))
        Text(value, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = color)
    }
}

@Composable
private fun PlanFeatureRow(text: String) {
    Text(text, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Medium)
}

@Composable
private fun SettingsSwitchRow(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    testTag: String
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
            Icon(imageVector = icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(title, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Text(subtitle, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = iconTint,
                checkedTrackColor = iconTint.copy(alpha = 0.3f)
            ),
            modifier = Modifier.testTag(testTag)
        )
    }
}
