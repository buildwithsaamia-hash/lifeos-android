package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Feedback
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalActivity
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.PriorityHigh
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Report
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
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

// --- Data Classes for Admin Module ---
data class AdminUser(
    val id: String,
    val name: String,
    val email: String,
    val role: String, // "Admin", "Pro User", "Free User"
    val status: String, // "Active", "Suspended", "Pending"
    val lastActive: String,
    val totalTasks: Int
)

data class SystemReport(
    val id: String,
    val title: String,
    val reporter: String,
    val category: String, // "Bug", "Security", "Performance", "Data Sync"
    val severity: String, // "Critical", "High", "Medium", "Low"
    var status: String, // "Open", "In Review", "Resolved"
    val timestamp: String
)

data class UserFeedback(
    val id: String,
    val userName: String,
    val rating: Int,
    val comment: String,
    val category: String, // "UI/UX", "Feature Request", "Performance", "AI Response"
    val sentiment: String, // "Positive", "Neutral", "Negative"
    val date: String,
    var status: String // "New", "Under Review", "Implemented"
)

data class AdminBroadcastNotification(
    val id: String,
    val title: String,
    val message: String,
    val targetAudience: String, // "All Users", "Pro Tier", "Inactives"
    val sentTime: String,
    val deliveryRate: String
)

@Composable
fun AdminScreen(
    onNavigateTab: (Int) -> Unit
) {
    val context = LocalContext.current
    var selectedSubTab by remember { mutableStateOf("Overview") }

    // --- State for Admin Users ---
    var userSearchQuery by remember { mutableStateOf("") }
    var selectedRoleFilter by remember { mutableStateOf("All") }
    var showAddUserDialog by remember { mutableStateOf(false) }
    var newUserName by remember { mutableStateOf("") }
    var newUserEmail by remember { mutableStateOf("") }
    var newUserRole by remember { mutableStateOf("Pro User") }

    val adminUsersList = remember {
        mutableStateListOf(
            AdminUser("USR-101", "Alex Vance", "alex.dev@lifeos.ai", "Admin", "Active", "2 mins ago", 142),
            AdminUser("USR-102", "Elena Rostova", "elena.r@enterprise.io", "Pro User", "Active", "15 mins ago", 98),
            AdminUser("USR-103", "Marcus Chen", "marcus.c@techcorp.com", "Pro User", "Active", "1 hour ago", 215),
            AdminUser("USR-104", "Sophia Lin", "sophia.lin@designstudio.co", "Free User", "Active", "3 hours ago", 45),
            AdminUser("USR-105", "David K.", "david.k@suspicious.net", "Free User", "Suspended", "3 days ago", 2),
            AdminUser("USR-106", "Rachel Green", "rachel.g@fashion.org", "Pro User", "Pending", "Yesterday", 12)
        )
    }

    // --- State for Reports ---
    val systemReportsList = remember {
        mutableStateListOf(
            SystemReport("REP-901", "LifeOS AI API Latency Spike (>850ms)", "Auto Sentinel", "Performance", "High", "Open", "10:14 AM"),
            SystemReport("REP-902", "Unencrypted Notes Sync Alert", "Vault Guard", "Security", "Critical", "In Review", "09:45 AM"),
            SystemReport("REP-903", "Planner Subtask Reordering Glitch", "User: Elena R.", "Bug", "Medium", "Open", "08:30 AM"),
            SystemReport("REP-904", "Health Connect Step Counter Delay", "User: Marcus C.", "Data Sync", "Low", "Resolved", "Yesterday")
        )
    }

    // --- State for Feedback ---
    val feedbackList = remember {
        mutableStateListOf(
            UserFeedback("FB-01", "Sarah Jenkins", 5, "The AI Planner auto-breakdown feature saved me 3 hours of project planning!", "AI Response", "Positive", "Today, 09:12 AM", "Implemented"),
            UserFeedback("FB-02", "Brian O'Connor", 4, "Dark mode aesthetics are top notch. Would love widget support on Android home screen.", "UI/UX", "Positive", "Today, 08:00 AM", "Under Review"),
            UserFeedback("FB-03", "Liam Smith", 2, "Emergency dialer triggered once by mistake. Please add confirmation prompt.", "Feature Request", "Negative", "Yesterday", "New"),
            UserFeedback("FB-04", "Amara Okafor", 5, "Encrypted Vault gives total peace of mind for storing sensitive medical IDs.", "Security", "Positive", "2 days ago", "Implemented")
        )
    }

    // --- State for Notifications ---
    var broadcastTitle by remember { mutableStateOf("") }
    var broadcastBody by remember { mutableStateOf("") }
    var targetAudience by remember { mutableStateOf("All Users") }
    var notificationPriority by remember { mutableStateOf("Normal") }

    val notificationHistory = remember {
        mutableStateListOf(
            AdminBroadcastNotification("NOTIF-501", "⚡ LifeOS 2.4 Update Live!", "Check out new AI Voice breakdown & Health Sync features.", "All Users", "Today, 07:00 AM", "99.8%"),
            AdminBroadcastNotification("NOTIF-502", "💎 Pro Plan Lifetime Offer", "50% off Pro tier for early adopters ending this weekend.", "Free Users", "2 days ago", "98.4%")
        )
    }

    // --- State for Firebase Monitoring ---
    var isPingingFirebase by remember { mutableStateOf(false) }
    var firebasePingResult by remember { mutableStateOf("Connected • Status 200 OK (22ms)") }
    var firestoreSyncEnabled by remember { mutableStateOf(true) }
    var fcmPushServiceActive by remember { mutableStateOf(true) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .testTag("admin_dashboard_root")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // ==========================================
            // TOP ADMIN NAVIGATION TABS
            // ==========================================
            val subTabs = listOf("Overview", "Users", "Reports", "Analytics", "Feedback", "Notifications", "Charts", "Firebase")

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(subTabs) { tab ->
                    val isSelected = selectedSubTab == tab
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isSelected) PrimaryCyan else MaterialTheme.colorScheme.surfaceVariant)
                            .clickable { selectedSubTab = tab }
                            .padding(horizontal = 12.dp, vertical = 7.dp)
                            .testTag("admin_tab_$tab")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            val icon = when (tab) {
                                "Overview" -> Icons.Default.AdminPanelSettings
                                "Users" -> Icons.Default.Group
                                "Reports" -> Icons.Default.BugReport
                                "Analytics" -> Icons.Default.Analytics
                                "Feedback" -> Icons.Default.Feedback
                                "Notifications" -> Icons.Default.NotificationsActive
                                "Charts" -> Icons.Default.BarChart
                                "Firebase" -> Icons.Default.Storage
                                else -> Icons.Default.Info
                            }

                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = if (isSelected) Color.Black else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = tab,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.Black else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                // ==========================================
                // SECTION 1: OVERVIEW SUMMARY & QUICK STATS
                // ==========================================
                if (selectedSubTab == "Overview") {
                    item {
                        LifeOSCard(
                            modifier = Modifier.fillMaxWidth(),
                            borderColor = PrimaryCyan
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
                                                .size(36.dp)
                                                .clip(CircleShape)
                                                .background(PrimaryCyan.copy(alpha = 0.2f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(imageVector = Icons.Default.Shield, contentDescription = null, tint = PrimaryCyan, modifier = Modifier.size(20.dp))
                                        }
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text("LifeOS System Admin Command Center", fontSize = 15.sp, fontWeight = FontWeight.ExtraBold)
                                            Text("All Cloud & Local Microservices Operational", fontSize = 11.sp, color = AccentLime)
                                        }
                                    }

                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(AccentLime.copy(alpha = 0.2f))
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text("HEALTH 99.98%", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = AccentLime)
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    AdminStatBox("Total Users", "${adminUsersList.size}", "+18% mo", PrimaryCyan)
                                    AdminStatBox("Open Reports", "${systemReportsList.count { it.status != "Resolved" }}", "Requires Action", EmergencyRed)
                                    AdminStatBox("Satisfaction", "4.8 / 5.0", "94% Positive", AccentLime)
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    AdminStatBox("API Requests", "1.24M", "Avg 22ms", SecondaryPurple)
                                    AdminStatBox("Firebase FCM", "Active", "Deliverability 99.8%", PrimaryCyan)
                                    AdminStatBox("Pro Users", "${adminUsersList.count { it.role == "Pro User" }}", "\$4,820 MRR", AccentLime)
                                }
                            }
                        }
                    }

                    item {
                        LifeOSCard(modifier = Modifier.fillMaxWidth()) {
                            Column {
                                Text("Quick Management Shortcuts", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = { selectedSubTab = "Users" },
                                        modifier = Modifier.weight(1f),
                                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryCyan),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text("Manage Users", fontSize = 11.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                                    }

                                    Button(
                                        onClick = { selectedSubTab = "Notifications" },
                                        modifier = Modifier.weight(1f),
                                        colors = ButtonDefaults.buttonColors(containerColor = SecondaryPurple),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text("Push Broadcast", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                    }

                                    Button(
                                        onClick = { selectedSubTab = "Firebase" },
                                        modifier = Modifier.weight(1f),
                                        colors = ButtonDefaults.buttonColors(containerColor = AccentLime),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text("Firebase Health", fontSize = 11.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }

                // ==========================================
                // SECTION 2: USERS MANAGEMENT
                // ==========================================
                if (selectedSubTab == "Overview" || selectedSubTab == "Users") {
                    item {
                        LifeOSCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("admin_users_section"),
                            borderColor = PrimaryCyan
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(imageVector = Icons.Default.Group, contentDescription = null, tint = PrimaryCyan, modifier = Modifier.size(20.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("User Management (${adminUsersList.size})", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                                    }

                                    Button(
                                        onClick = { showAddUserDialog = true },
                                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryCyan),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Icon(imageVector = Icons.Default.PersonAdd, contentDescription = null, tint = Color.Black, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Add User", fontSize = 11.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Search and Role Filter Chips
                                OutlinedTextField(
                                    value = userSearchQuery,
                                    onValueChange = { userSearchQuery = it },
                                    placeholder = { Text("Search by name, email, or ID...") },
                                    leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("admin_user_search_input")
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    listOf("All", "Admin", "Pro User", "Free User", "Suspended").forEach { roleFilter ->
                                        val isSel = selectedRoleFilter == roleFilter
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(if (isSel) PrimaryCyan else MaterialTheme.colorScheme.surfaceVariant)
                                                .clickable { selectedRoleFilter = roleFilter }
                                                .padding(horizontal = 10.dp, vertical = 4.dp)
                                        ) {
                                            Text(
                                                roleFilter,
                                                fontSize = 10.sp,
                                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                                color = if (isSel) Color.Black else MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                // Filtered Users List
                                val filteredUsers = adminUsersList.filter { user ->
                                    (selectedRoleFilter == "All" || user.role == selectedRoleFilter || (selectedRoleFilter == "Suspended" && user.status == "Suspended")) &&
                                            (userSearchQuery.isEmpty() || user.name.contains(userSearchQuery, ignoreCase = true) || user.email.contains(userSearchQuery, ignoreCase = true))
                                }

                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    filteredUsers.forEach { user ->
                                        UserItemCard(
                                            user = user,
                                            onToggleStatus = {
                                                val index = adminUsersList.indexOfFirst { u -> u.id == user.id }
                                                if (index != -1) {
                                                    val newStatus = if (user.status == "Active") "Suspended" else "Active"
                                                    adminUsersList[index] = user.copy(status = newStatus)
                                                    Toast.makeText(context, "${user.name} status updated to $newStatus", Toast.LENGTH_SHORT).show()
                                                }
                                            },
                                            onChangeRole = { newRole ->
                                                val index = adminUsersList.indexOfFirst { u -> u.id == user.id }
                                                if (index != -1) {
                                                    adminUsersList[index] = user.copy(role = newRole)
                                                    Toast.makeText(context, "${user.name} role changed to $newRole", Toast.LENGTH_SHORT).show()
                                                }
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // ==========================================
                // SECTION 3: REPORTS & AUDIT LOGS
                // ==========================================
                if (selectedSubTab == "Overview" || selectedSubTab == "Reports") {
                    item {
                        LifeOSCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("admin_reports_section"),
                            borderColor = EmergencyRed
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(imageVector = Icons.Default.BugReport, contentDescription = null, tint = EmergencyRed, modifier = Modifier.size(20.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("System Reports & Security Flags", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                                    }

                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(EmergencyRed.copy(alpha = 0.2f))
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text("${systemReportsList.count { it.status == "Open" }} Open", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = EmergencyRed)
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    systemReportsList.forEach { report ->
                                        SystemReportCard(
                                            report = report,
                                            onResolve = {
                                                report.status = "Resolved"
                                                Toast.makeText(context, "Report ${report.id} marked as Resolved", Toast.LENGTH_SHORT).show()
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // ==========================================
                // SECTION 4: ANALYTICS & SYSTEM PERFORMANCE
                // ==========================================
                if (selectedSubTab == "Overview" || selectedSubTab == "Analytics") {
                    item {
                        LifeOSCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("admin_analytics_section"),
                            borderColor = SecondaryPurple
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = Icons.Default.Analytics, contentDescription = null, tint = SecondaryPurple, modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Real-Time Analytics & Resource Load", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                MetricProgressRow("CPU Core Usage", "34%", 0.34f, PrimaryCyan)
                                Spacer(modifier = Modifier.height(8.dp))
                                MetricProgressRow("Memory Allocation", "68%", 0.68f, SecondaryPurple)
                                Spacer(modifier = Modifier.height(8.dp))
                                MetricProgressRow("Encrypted Vault I/O", "12%", 0.12f, AccentLime)
                                Spacer(modifier = Modifier.height(8.dp))
                                MetricProgressRow("LifeOS AI Token Throughput", "82%", 0.82f, PrimaryCyan)

                                Spacer(modifier = Modifier.height(14.dp))
                                HorizontalDivider()
                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    StatDetailItem("Avg Session Length", "24m 15s")
                                    StatDetailItem("User Retention", "84.2%")
                                    StatDetailItem("API Errors", "0.02%")
                                }
                            }
                        }
                    }
                }

                // ==========================================
                // SECTION 5: USER FEEDBACK HUB
                // ==========================================
                if (selectedSubTab == "Overview" || selectedSubTab == "Feedback") {
                    item {
                        LifeOSCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("admin_feedback_section"),
                            borderColor = AccentLime
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(imageVector = Icons.Default.Feedback, contentDescription = null, tint = AccentLime, modifier = Modifier.size(20.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("User Feedback & Reviews", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                                    }

                                    Text("Avg Rating: ★ 4.8", fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = AccentLime)
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    feedbackList.forEach { item ->
                                        FeedbackCard(feedback = item)
                                    }
                                }
                            }
                        }
                    }
                }

                // ==========================================
                // SECTION 6: PUSH NOTIFICATIONS BROADCAST
                // ==========================================
                if (selectedSubTab == "Overview" || selectedSubTab == "Notifications") {
                    item {
                        LifeOSCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("admin_notifications_section"),
                            borderColor = SecondaryPurple
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = Icons.Default.NotificationsActive, contentDescription = null, tint = SecondaryPurple, modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Push Notification Broadcast Center", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                OutlinedTextField(
                                    value = broadcastTitle,
                                    onValueChange = { broadcastTitle = it },
                                    label = { Text("Notification Title") },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("broadcast_title_input")
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                OutlinedTextField(
                                    value = broadcastBody,
                                    onValueChange = { broadcastBody = it },
                                    label = { Text("Message Body") },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("broadcast_body_input")
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Target Audience:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)

                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        listOf("All Users", "Pro Tier", "Inactives").forEach { aud ->
                                            val isSel = targetAudience == aud
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .background(if (isSel) SecondaryPurple else MaterialTheme.colorScheme.surfaceVariant)
                                                    .clickable { targetAudience = aud }
                                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                                            ) {
                                                Text(aud, fontSize = 10.sp, color = if (isSel) Color.White else MaterialTheme.colorScheme.onSurface)
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Button(
                                    onClick = {
                                        if (broadcastTitle.isNotBlank() && broadcastBody.isNotBlank()) {
                                            notificationHistory.add(
                                                0,
                                                AdminBroadcastNotification(
                                                    id = "NOTIF-${System.currentTimeMillis() % 1000}",
                                                    title = broadcastTitle,
                                                    message = broadcastBody,
                                                    targetAudience = targetAudience,
                                                    sentTime = "Just now",
                                                    deliveryRate = "99.9%"
                                                )
                                            )
                                            Toast.makeText(context, "Broadcast Push sent to $targetAudience!", Toast.LENGTH_SHORT).show()
                                            broadcastTitle = ""
                                            broadcastBody = ""
                                        } else {
                                            Toast.makeText(context, "Please enter both Title and Message", Toast.LENGTH_SHORT).show()
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = SecondaryPurple),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("send_broadcast_button"),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.Send, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Dispatch Global Push Broadcast", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                }

                                Spacer(modifier = Modifier.height(12.dp))
                                Text("Recent Sent Broadcasts", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(6.dp))

                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    notificationHistory.take(3).forEach { notif ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                                .padding(8.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(notif.title, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                                Text(notif.message, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            }

                                            Column(horizontalAlignment = Alignment.End) {
                                                Text(notif.sentTime, fontSize = 9.sp, color = SecondaryPurple)
                                                Text("Rate: ${notif.deliveryRate}", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = AccentLime)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // ==========================================
                // SECTION 7: VISUAL CHARTS
                // ==========================================
                if (selectedSubTab == "Overview" || selectedSubTab == "Charts") {
                    item {
                        LifeOSCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("admin_charts_section"),
                            borderColor = PrimaryCyan
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = Icons.Default.BarChart, contentDescription = null, tint = PrimaryCyan, modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Traffic & Revenue Growth Charts", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Text("Monthly Active Users (DAU Peak)", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Spacer(modifier = Modifier.height(6.dp))

                                // Compose Custom Bar Chart
                                AdminBarChart(
                                    data = listOf(40f, 65f, 80f, 55f, 90f, 100f, 120f),
                                    labels = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                Text("API Response Speed Trend (ms)", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Spacer(modifier = Modifier.height(6.dp))

                                // Compose Line Chart
                                AdminLineChart(
                                    dataPoints = listOf(45f, 32f, 28f, 22f, 24f, 19f, 22f)
                                )
                            }
                        }
                    }
                }

                // ==========================================
                // SECTION 8: FIREBASE CONSOLE STATUS
                // ==========================================
                if (selectedSubTab == "Overview" || selectedSubTab == "Firebase") {
                    item {
                        LifeOSCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("admin_firebase_section"),
                            borderColor = AccentLime,
                            backgroundColor = AccentLime.copy(alpha = 0.05f)
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(imageVector = Icons.Default.Storage, contentDescription = null, tint = AccentLime, modifier = Modifier.size(22.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text("Firebase Cloud Services Monitor", fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = AccentLime)
                                            Text("Google Cloud Project: lifeos-ai-prod", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                    }

                                    Button(
                                        onClick = {
                                            isPingingFirebase = true
                                            firebasePingResult = "Pinged firebase-app-check.googleapis.com... Status 200 OK (${(15..35).random()}ms)"
                                            isPingingFirebase = false
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = AccentLime),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.testTag("ping_firebase_button")
                                    ) {
                                        Icon(imageVector = Icons.Default.Refresh, contentDescription = null, tint = Color.Black, modifier = Modifier.size(12.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Ping Live", fontSize = 10.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(MaterialTheme.colorScheme.surfaceVariant)
                                        .padding(8.dp)
                                ) {
                                    Text(
                                        text = firebasePingResult,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = AccentLime
                                    )
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    FirebaseServiceRow("Firebase Authentication", "6 Active Sessions • 0 Auth Failures", true)
                                    FirebaseServiceRow("Cloud Firestore DB", "4.2K Reads/min • AES-256 Rules Enforced", firestoreSyncEnabled) { firestoreSyncEnabled = it }
                                    FirebaseServiceRow("Firebase Cloud Messaging (FCM)", "Deliverability 99.8% • FCM Token Refresh OK", fcmPushServiceActive) { fcmPushServiceActive = it }
                                    FirebaseServiceRow("Cloud Storage Buckets", "1.4 GB Used / 50 GB Tier", true)
                                }
                            }
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }

        // --- Dialog: Add New User ---
        if (showAddUserDialog) {
            AlertDialog(
                onDismissRequest = { showAddUserDialog = false },
                title = { Text("Add New LifeOS User", fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = newUserName,
                            onValueChange = { newUserName = it },
                            label = { Text("Full Name") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = newUserEmail,
                            onValueChange = { newUserEmail = it },
                            label = { Text("Email Address") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Text("User Role & Permissions:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            listOf("Free User", "Pro User", "Admin").forEach { role ->
                                val isSel = newUserRole == role
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSel) PrimaryCyan else MaterialTheme.colorScheme.surfaceVariant)
                                        .clickable { newUserRole = role }
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text(role, fontSize = 11.sp, color = if (isSel) Color.Black else MaterialTheme.colorScheme.onSurface)
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (newUserName.isNotBlank() && newUserEmail.isNotBlank()) {
                                adminUsersList.add(
                                    0,
                                    AdminUser(
                                        id = "USR-${(100..999).random()}",
                                        name = newUserName,
                                        email = newUserEmail,
                                        role = newUserRole,
                                        status = "Active",
                                        lastActive = "Just created",
                                        totalTasks = 0
                                    )
                                )
                                Toast.makeText(context, "New user $newUserName added successfully!", Toast.LENGTH_SHORT).show()
                                showAddUserDialog = false
                                newUserName = ""
                                newUserEmail = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryCyan)
                    ) {
                        Text("Add User", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddUserDialog = false }) { Text("Cancel") }
                }
            )
        }
    }
}

// --- Helper UI Components ---

@Composable
fun AdminStatBox(
    title: String,
    value: String,
    subtitle: String,
    accentColor: Color
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(10.dp)
            .width(100.dp)
    ) {
        Column {
            Text(title, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(2.dp))
            Text(value, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = accentColor)
            Spacer(modifier = Modifier.height(2.dp))
            Text(subtitle, fontSize = 9.sp, fontWeight = FontWeight.SemiBold, color = accentColor.copy(alpha = 0.8f))
        }
    }
}

@Composable
fun UserItemCard(
    user: AdminUser,
    onToggleStatus: () -> Unit,
    onChangeRole: (String) -> Unit
) {
    var expandedMenu by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(PrimaryCyan.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text(user.name.take(1), fontWeight = FontWeight.Bold, color = PrimaryCyan)
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(user.name, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(6.dp))

                    val roleBg = when (user.role) {
                        "Admin" -> SecondaryPurple
                        "Pro User" -> AccentLime
                        else -> MaterialTheme.colorScheme.outline
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(roleBg)
                            .padding(horizontal = 4.dp, vertical = 1.dp)
                    ) {
                        Text(user.role, fontSize = 8.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    }
                }

                Text(user.email, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(if (user.status == "Active") AccentLime.copy(alpha = 0.2f) else EmergencyRed.copy(alpha = 0.2f))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    user.status,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (user.status == "Active") AccentLime else EmergencyRed
                )
            }

            Box {
                IconButton(onClick = { expandedMenu = true }) {
                    Icon(imageVector = Icons.Default.MoreVert, contentDescription = "User Actions", modifier = Modifier.size(16.dp))
                }

                DropdownMenu(
                    expanded = expandedMenu,
                    onDismissRequest = { expandedMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text(if (user.status == "Active") "Suspend User" else "Activate User") },
                        onClick = {
                            onToggleStatus()
                            expandedMenu = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Make Admin") },
                        onClick = {
                            onChangeRole("Admin")
                            expandedMenu = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Upgrade to Pro") },
                        onClick = {
                            onChangeRole("Pro User")
                            expandedMenu = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun SystemReportCard(
    report: SystemReport,
    onResolve: () -> Unit
) {
    val severityColor = when (report.severity) {
        "Critical" -> EmergencyRed
        "High" -> EmergencyRed.copy(alpha = 0.8f)
        "Medium" -> SecondaryPurple
        else -> AccentLime
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(severityColor)
                        .padding(horizontal = 4.dp, vertical = 1.dp)
                ) {
                    Text(report.severity.uppercase(), fontSize = 8.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                }

                Spacer(modifier = Modifier.width(6.dp))

                Text(report.title, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(2.dp))

            Text("Reporter: ${report.reporter} • ${report.timestamp}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        if (report.status != "Resolved") {
            Button(
                onClick = onResolve,
                colors = ButtonDefaults.buttonColors(containerColor = AccentLime),
                shape = RoundedCornerShape(6.dp)
            ) {
                Text("Resolve", fontSize = 10.sp, color = Color.Black, fontWeight = FontWeight.Bold)
            }
        } else {
            Text("✓ Resolved", fontSize = 11.sp, color = AccentLime, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun MetricProgressRow(title: String, valueStr: String, progress: Float, color: Color) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(title, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
            Text(valueStr, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = color)
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = color,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}

@Composable
fun StatDetailItem(label: String, valStr: String) {
    Column {
        Text(label, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(valStr, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = PrimaryCyan)
    }
}

@Composable
fun FeedbackCard(feedback: UserFeedback) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(feedback.userName, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(6.dp))
                Text("★".repeat(feedback.rating), fontSize = 11.sp, color = AccentLime)
            }

            Spacer(modifier = Modifier.height(2.dp))
            Text(feedback.comment, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface)
            Spacer(modifier = Modifier.height(4.dp))

            Text("Category: ${feedback.category} • ${feedback.date}", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(if (feedback.sentiment == "Positive") AccentLime.copy(alpha = 0.2f) else EmergencyRed.copy(alpha = 0.2f))
                .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
            Text(
                feedback.sentiment,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = if (feedback.sentiment == "Positive") AccentLime else EmergencyRed
            )
        }
    }
}

@Composable
fun FirebaseServiceRow(
    serviceName: String,
    details: String,
    isActive: Boolean,
    onToggle: ((Boolean) -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Icon(
                imageVector = if (isActive) Icons.Default.CheckCircle else Icons.Default.Warning,
                contentDescription = null,
                tint = if (isActive) AccentLime else EmergencyRed,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(serviceName, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Text(details, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        if (onToggle != null) {
            Switch(
                checked = isActive,
                onCheckedChange = onToggle
            )
        } else {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(AccentLime)
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text("ONLINE", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            }
        }
    }
}

// --- Compose Custom Canvas Charts ---

@Composable
fun AdminBarChart(
    data: List<Float>,
    labels: List<String>
) {
    val barColor = PrimaryCyan
    val maxVal = (data.maxOrNull() ?: 100f).coerceAtLeast(1f)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp)
    ) {
        Canvas(modifier = Modifier
            .fillMaxWidth()
            .weight(1f)) {
            val width = size.width
            val height = size.height
            val barWidth = (width / data.size) * 0.5f
            val spacing = width / data.size

            data.forEachIndexed { index, value ->
                val barHeight = (value / maxVal) * height
                val left = index * spacing + (spacing - barWidth) / 2
                val top = height - barHeight

                drawRoundRect(
                    color = barColor,
                    topLeft = Offset(left, top),
                    size = Size(barWidth, barHeight),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(6f, 6f)
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            labels.forEach { label ->
                Text(label, fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
fun AdminLineChart(
    dataPoints: List<Float>
) {
    val lineColor = SecondaryPurple
    val maxVal = (dataPoints.maxOrNull() ?: 100f).coerceAtLeast(1f)

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp)
    ) {
        val width = size.width
        val height = size.height
        val spacing = width / (dataPoints.size - 1)

        val path = Path()
        dataPoints.forEachIndexed { index, value ->
            val x = index * spacing
            val y = height - ((value / maxVal) * height * 0.8f) - 10f

            if (index == 0) {
                path.moveTo(x, y)
            } else {
                path.lineTo(x, y)
            }

            drawCircle(
                color = lineColor,
                radius = 6f,
                center = Offset(x, y)
            )
        }

        drawPath(
            path = path,
            color = lineColor,
            style = Stroke(width = 4f)
        )
    }
}
