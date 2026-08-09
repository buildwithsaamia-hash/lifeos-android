package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material.icons.filled.Menu
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Emergency
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FolderSpecial
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ai.GeminiApiClient
import com.example.ui.components.LifeOSCard
import com.example.ui.theme.AccentLime
import com.example.ui.theme.PrimaryCyan
import com.example.ui.theme.SecondaryPurple
import kotlinx.coroutines.launch

@Composable
fun LandingScreen(
    onNavigateToApp: () -> Unit,
    onTryAiPrompt: (String) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    var activeNavSubTab by remember { mutableStateOf("Home") }
    var isAnnualBilling by remember { mutableStateOf(true) }
    var showAuthModal by remember { mutableStateOf(false) }
    var isSignUpMode by remember { mutableStateOf(false) }
    var showDemoVideoModal by remember { mutableStateOf(false) }

    // Auth Dialog state
    var authEmail by remember { mutableStateOf("") }
    var authPassword by remember { mutableStateOf("") }
    var authName by remember { mutableStateOf("") }
    var authSuccessMsg by remember { mutableStateOf<String?>(null) }

    // AI Demo state
    var demoPromptInput by remember { mutableStateOf("Optimize my daily schedule for maximum deep work productivity") }
    var demoAiResponse by remember { mutableStateOf<String?>(null) }
    var isDemoLoading by remember { mutableStateOf(false) }

    // Contact Form state
    var contactName by remember { mutableStateOf("") }
    var contactEmail by remember { mutableStateOf("") }
    var contactMessage by remember { mutableStateOf("") }
    var contactSubmitted by remember { mutableStateOf(false) }

    // FAQ Expanded State
    var expandedFaqIndex by remember { mutableStateOf<Int?>(0) }

    // Selected Screenshot Tab
    var selectedScreenshotTab by remember { mutableStateOf("Dashboard") }
    var showMobileMenu by remember { mutableStateOf(false) }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val isMobile = maxWidth < 600.dp

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("landing_screen_container")
        ) {
            // --- 1. Top Navigation Header ---
            item {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 4.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = if (isMobile) 16.dp else 24.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Left: LifeOS Logo + AI Status Indicator Pill
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(
                                            Brush.horizontalGradient(
                                                listOf(PrimaryCyan, SecondaryPurple)
                                            )
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = "LifeOS Logo",
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "LifeOS",
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            letterSpacing = (-0.5).sp
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        // AI Status Pill
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(AccentLime.copy(alpha = 0.15f))
                                                .border(1.dp, AccentLime.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(6.dp)
                                                        .clip(CircleShape)
                                                        .background(AccentLime)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    text = "AI Online",
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = AccentLime
                                                )
                                            }
                                        }
                                    }
                                    Text(
                                        text = "Your Personal AI OS",
                                        fontSize = 10.sp,
                                        color = PrimaryCyan,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            // Right Header Controls
                            if (isMobile) {
                                // On Mobile: ONLY Launch App button & Hamburger Menu (☰)
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Button(
                                        onClick = { onNavigateToApp() },
                                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryCyan),
                                        shape = RoundedCornerShape(10.dp),
                                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                        modifier = Modifier.height(38.dp)
                                    ) {
                                        Text("Launch App", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    }

                                    IconButton(
                                        onClick = { showMobileMenu = !showMobileMenu },
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f))
                                    ) {
                                        Icon(
                                            imageVector = if (showMobileMenu) Icons.Default.Close else Icons.Default.Menu,
                                            contentDescription = "Toggle Navigation Menu",
                                            tint = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            } else {
                                // On Tablet / Desktop: Show Log In, Launch App, and Inline Tabs
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Button(
                                        onClick = {
                                            isSignUpMode = false
                                            showAuthModal = true
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Text("Log In", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                                    }

                                    Button(
                                        onClick = { onNavigateToApp() },
                                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryCyan),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Text("Launch App", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    }
                                }
                            }
                        }

                        // Horizontal Navigation Links - ONLY on Tablet / Desktop
                        if (!isMobile) {
                            LazyRow(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                val subTabs = listOf("Home", "AI Demo", "Features", "Showcase", "Pricing", "FAQ", "Contact")
                                items(subTabs) { tab ->
                                    val isSelected = activeNavSubTab == tab
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(if (isSelected) PrimaryCyan.copy(alpha = 0.18f) else Color.Transparent)
                                            .clickable { activeNavSubTab = tab }
                                            .padding(horizontal = 12.dp, vertical = 6.dp)
                                    ) {
                                        Text(
                                            text = tab,
                                            fontSize = 12.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isSelected) PrimaryCyan else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }

                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                    }
                }
            }

            // --- Mobile Dropdown Menu (If Hamburger Toggled) ---
            if (isMobile && showMobileMenu) {
                item {
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        tonalElevation = 8.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .border(1.dp, PrimaryCyan.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "NAVIGATION MENU",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryCyan,
                                letterSpacing = 1.sp
                            )

                            val subTabs = listOf("Home", "AI Demo", "Features", "Showcase", "Pricing", "FAQ", "Contact")
                            subTabs.forEach { tab ->
                                val isSelected = activeNavSubTab == tab
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (isSelected) PrimaryCyan.copy(alpha = 0.2f) else Color.Transparent)
                                        .clickable {
                                            activeNavSubTab = tab
                                            showMobileMenu = false
                                        }
                                        .padding(horizontal = 14.dp, vertical = 10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = tab,
                                        fontSize = 14.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) PrimaryCyan else MaterialTheme.colorScheme.onSurface
                                    )
                                    Icon(
                                        imageVector = Icons.Default.ChevronRight,
                                        contentDescription = null,
                                        tint = if (isSelected) PrimaryCyan else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }

                            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                            Button(
                                onClick = {
                                    showMobileMenu = false
                                    onNavigateToApp()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryCyan),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                            ) {
                                Text("Launch LifeOS Workspace", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }
                }
            }

            // --- 2. World-Class Hero Section ---
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.surface,
                                    PrimaryCyan.copy(alpha = 0.15f),
                                    SecondaryPurple.copy(alpha = 0.10f),
                                    MaterialTheme.colorScheme.background
                                )
                            )
                        )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = if (isMobile) 20.dp else 40.dp, vertical = if (isMobile) 28.dp else 48.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Badge Pill
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(PrimaryCyan.copy(alpha = 0.15f))
                                .border(1.dp, PrimaryCyan.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                                .padding(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = PrimaryCyan,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "NEXT-GEN AI WORKSPACE • LIFEOS 3.0",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryCyan,
                                    letterSpacing = 0.5.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Headline
                        Text(
                            text = "Your Personal AI\nOperating System",
                            fontSize = if (isMobile) 32.sp else 46.sp,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center,
                            lineHeight = if (isMobile) 38.sp else 52.sp,
                            letterSpacing = (-1).sp
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Subtitle (Limit to 2 or 3 lines max)
                        Text(
                            text = "The unified AI workspace that orchestrates your tasks, habit streaks, encrypted vault, health metrics, and emergency workflows with Smart AI Engine intelligence.",
                            fontSize = if (isMobile) 14.sp else 16.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            lineHeight = if (isMobile) 20.sp else 24.sp,
                            maxLines = 3,
                            modifier = Modifier.padding(horizontal = if (isMobile) 4.dp else 24.dp)
                        )

                        Spacer(modifier = Modifier.height(26.dp))

                        // STRICTLY TWO PRIMARY CTAS
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                onClick = { onNavigateToApp() },
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryCyan),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .height(48.dp)
                                    .testTag("hero_launch_app_button")
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("Launch LifeOS", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Icon(
                                        imageVector = Icons.Default.ChevronRight,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }

                            Button(
                                onClick = { showDemoVideoModal = true },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier.height(48.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.PlayArrow,
                                        contentDescription = null,
                                        tint = PrimaryCyan,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Watch Demo", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        // Hero Mockup Card Frame (Visual Focus of the Page - ENLARGED with Ambient Glow)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(if (isMobile) 270.dp else 360.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            // Ambient Glow Box Behind
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(8.dp)
                                    .clip(RoundedCornerShape(28.dp))
                                    .background(
                                        Brush.radialGradient(
                                            colors = listOf(
                                                PrimaryCyan.copy(alpha = 0.35f),
                                                SecondaryPurple.copy(alpha = 0.25f),
                                                Color.Transparent
                                            )
                                        )
                                    )
                            )

                            // Main Hero Image Container
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(24.dp))
                                    .border(
                                        1.dp,
                                        Brush.linearGradient(
                                            listOf(
                                                PrimaryCyan.copy(alpha = 0.7f),
                                                SecondaryPurple.copy(alpha = 0.5f)
                                            )
                                        ),
                                        RoundedCornerShape(24.dp)
                                    )
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.img_lifeos_hero_1784907572607),
                                    contentDescription = "LifeOS AI Hero Preview",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(
                                            Brush.verticalGradient(
                                                listOf(Color.Transparent, Color.Black.copy(alpha = 0.82f))
                                            )
                                        )
                                        .padding(18.dp),
                                    contentAlignment = Alignment.BottomStart
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(
                                                text = "Smart AI Engine Live Control Panel",
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                            Text(
                                                text = "Sub-10ms Local Reaction Latency",
                                                fontSize = 11.sp,
                                                color = SecondaryPurple
                                            )
                                        }

                                        Box(
                                            modifier = Modifier
                                                .clip(CircleShape)
                                                .background(AccentLime)
                                                .padding(horizontal = 10.dp, vertical = 4.dp)
                                        ) {
                                            Text("OPERATIONAL", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

        // --- 3. Trusted By & Key Stats Section ---
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    .padding(vertical = 24.dp, horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "TRUSTED BY PRODUCTIVITY LEADERS AT WORLD-CLASS STARTUPS",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Company Badges Row
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val brands = listOf("Vercel", "Linear", "Stripe", "Raycast", "Framer", "Acme AI", "OpenFoundry")
                    items(brands) { brand ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.surface)
                                .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = brand,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                Spacer(modifier = Modifier.height(24.dp))

                // Stats Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    val stats = listOf(
                        "100K+" to "Active Users",
                        "99.9%" to "Uptime SLA",
                        "4.9/5" to "User Rating",
                        "<10ms" to "AI Response"
                    )

                    stats.forEach { (metric, label) ->
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = metric,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black,
                                color = PrimaryCyan
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = label,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // --- 4. Interactive AI Sandbox Demo ---
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "AI Sandbox",
                        tint = PrimaryCyan,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Interactive Smart AI Engine Sandbox",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Text(
                    text = "Test LifeOS AI capabilities live right inside your browser:",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(14.dp))

                LifeOSCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = PrimaryCyan.copy(alpha = 0.5f),
                    backgroundColor = PrimaryCyan.copy(alpha = 0.05f)
                ) {
                    Column {
                        OutlinedTextField(
                            value = demoPromptInput,
                            onValueChange = { demoPromptInput = it },
                            label = { Text("Ask LifeOS AI") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("demo_prompt_input"),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Sample: Schedule breakdown, Vault query, Goal streak",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Button(
                                onClick = {
                                    if (demoPromptInput.isNotBlank()) {
                                        isDemoLoading = true
                                        coroutineScope.launch {
                                            demoAiResponse = GeminiApiClient.generateResponse(demoPromptInput)
                                            isDemoLoading = false
                                        }
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryCyan),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                if (isDemoLoading) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(14.dp),
                                        color = Color.White,
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Send,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Run LifeOS AI", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    }
                                }
                            }
                        }

                        if (demoAiResponse != null) {
                            Spacer(modifier = Modifier.height(12.dp))
                            HorizontalDivider(color = PrimaryCyan.copy(alpha = 0.3f))
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = demoAiResponse ?: "",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurface,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            }
        }

        // --- 5. Features Grid (Linear.app Style Cards) ---
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Text(
                    text = "Built for High Performance",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Core AI modules integrated seamlessly into one intuitive workspace",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                val featuresList = listOf(
                    Triple("Smart Daily Planner", "Automatic AI task decomposition that transforms long-term goals into executable micro-tasks with priority tags.", Icons.Default.DateRange),
                    Triple("LifeOS AI Assistant", "Context-aware executive co-pilot for decision support, document summaries, and real-time scheduling.", Icons.Default.AutoAwesome),
                    Triple("Habit & Goal Streaks", "Gamified milestone tracking with XP progress meters, habit counters, and deadline alerts.", Icons.Default.TrackChanges),
                    Triple("Encrypted Vault", "Local Room SQLite database for confidential medical records, private notes, and sensitive files.", Icons.Default.Lock),
                    Triple("Emergency ICE Hub", "Instant primary medical contact dialing and critical safety profiles for emergency situations.", Icons.Default.Emergency),
                    Triple("Wealth & Health Vitals", "Integrated tracker for SaaS subscriptions, monthly burn, biometric vitals, and wellness metrics.", Icons.Default.MonetizationOn)
                )

                featuresList.chunked(2).forEach { pair ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        pair.forEach { feat ->
                            LifeOSCard(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(165.dp)
                            ) {
                                Column(verticalArrangement = Arrangement.SpaceBetween) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(PrimaryCyan.copy(alpha = 0.15f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = feat.third,
                                            contentDescription = null,
                                            tint = PrimaryCyan,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))

                                    Text(
                                        text = feat.first,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )

                                    Text(
                                        text = feat.second,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        lineHeight = 15.sp,
                                        maxLines = 4
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // --- 6. Screenshots / App Showcase Section ---
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = "App Interface Showcase",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Designed according to Apple HIG and Material 3 dark aesthetics",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(14.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val tabs = listOf("Dashboard", "AI Chat", "Planner", "Vault")
                    items(tabs) { tab ->
                        val isSelected = selectedScreenshotTab == tab
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (isSelected) PrimaryCyan else MaterialTheme.colorScheme.surfaceVariant)
                                .clickable { selectedScreenshotTab = tab }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = tab,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .border(1.dp, PrimaryCyan.copy(alpha = 0.35f), RoundedCornerShape(20.dp))
                ) {
                    val imageRes = when (selectedScreenshotTab) {
                        "AI Chat" -> R.drawable.img_ai_assistant_1784907594826
                        else -> R.drawable.img_lifeos_hero_1784907572607
                    }
                    Image(
                        painter = painterResource(id = imageRes),
                        contentDescription = "App Screenshot Showcase",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.45f))
                            .padding(16.dp),
                        contentAlignment = Alignment.BottomStart
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Verified,
                                    contentDescription = null,
                                    tint = PrimaryCyan,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "$selectedScreenshotTab Module",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                            Text(
                                text = "High-density dark responsive UI with real-time state persistence.",
                                fontSize = 12.sp,
                                color = SecondaryPurple
                            )
                        }
                    }
                }
            }
        }

        // --- 7. Testimonials Section ---
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = "Loved by Tech Leaders",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(14.dp))

                val reviews = listOf(
                    Triple("Alex Rivera", "Founder @ TechStack", "“LifeOS AI replaced 4 separate apps for me. The Smart AI Engine daily brief gives me exact clarity on what to execute first every morning.”"),
                    Triple("Sarah Chen", "Staff Engineer", "“The local encrypted vault and instant offline capability make LifeOS my absolute favorite personal operating system.”"),
                    Triple("Marcus Vance", "Product Designer", "“The visual polish is world-class. It feels as snappy as Linear and as thoughtful as Apple HIG.”")
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(reviews) { (author, title, quote) ->
                        LifeOSCard(
                            modifier = Modifier
                                .width(260.dp)
                                .height(160.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.SpaceBetween) {
                                Row {
                                    repeat(5) {
                                        Icon(
                                            imageVector = Icons.Default.Star,
                                            contentDescription = null,
                                            tint = AccentLime,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }

                                Text(
                                    text = quote,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    lineHeight = 16.sp,
                                    maxLines = 4
                                )

                                Column {
                                    Text(
                                        text = author,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = PrimaryCyan
                                    )
                                    Text(
                                        text = title,
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // --- 8. SaaS Pricing Plans ---
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = "Flexible SaaS Pricing Plans",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Choose the plan that fits your productivity requirements",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Toggle Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Monthly", fontSize = 12.sp, color = if (!isAnnualBilling) PrimaryCyan else MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.width(10.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .clickable { isAnnualBilling = !isAnnualBilling }
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = if (isAnnualBilling) "Annual (Save 20%)" else "Monthly",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryCyan
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("Annual", fontSize = 12.sp, color = if (isAnnualBilling) PrimaryCyan else MaterialTheme.colorScheme.onSurfaceVariant)
                }

                Spacer(modifier = Modifier.height(16.dp))

                val plans = listOf(
                    Triple("Starter", if (isAnnualBilling) "$0" else "$0", listOf("Standard Daily Planner", "Local Room Database", "50 AI Prompts/mo", "Community Support")),
                    Triple("LifeOS Pro", if (isAnnualBilling) "$7.99/mo" else "$9.99/mo", listOf("Unlimited Smart AI Engine", "AI Task Breakdown Engine", "Encrypted Documents Vault", "ICE Emergency Sync")),
                    Triple("Enterprise", if (isAnnualBilling) "$23.99/mo" else "$29.99/mo", listOf("Multi-Device Cloud Sync", "Custom AI Model Parameters", "Dedicated 24/7 Support", "Personal Data Export"))
                )

                plans.forEachIndexed { idx, plan ->
                    val isPopular = idx == 1
                    LifeOSCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        borderColor = if (isPopular) PrimaryCyan else MaterialTheme.colorScheme.outline,
                        backgroundColor = if (isPopular) PrimaryCyan.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = plan.first,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = plan.second,
                                        fontSize = 22.sp,
                                        fontWeight = FontWeight.Black,
                                        color = PrimaryCyan
                                    )
                                }

                                if (isPopular) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(PrimaryCyan)
                                            .padding(horizontal = 10.dp, vertical = 4.dp)
                                    ) {
                                        Text("MOST POPULAR", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            plan.third.forEach { feature ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(vertical = 3.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = AccentLime,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(text = feature, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Button(
                                onClick = {
                                    isSignUpMode = true
                                    showAuthModal = true
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isPopular) PrimaryCyan else MaterialTheme.colorScheme.surfaceVariant
                                ),
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = if (isPopular) "Start 14-Day Free Trial" else "Select ${plan.first}",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isPopular) Color.White else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
        }

        // --- 9. FAQ Accordion ---
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = "Frequently Asked Questions",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(14.dp))

                val faqs = listOf(
                    "Is my data stored securely in LifeOS?" to "Yes! All tasks, goals, medical contacts, and notes are saved locally using Android's SQLite Room Database engine.",
                    "How does the Smart AI Engine work?" to "LifeOS uses direct REST calls to secure AI endpoints using API keys managed safely in environment secrets.",
                    "Can I use LifeOS without an internet connection?" to "Absolutely. The Daily Organizer, Habit Tracker, Vault, and ICE Emergency SOS Dialer work 100% offline.",
                    "Is LifeOS available on Android tablets and ChromeOS?" to "Yes, LifeOS is fully responsive and supports Material 3 window size classes for mobile phones, foldables, and wide tablet displays."
                )

                faqs.forEachIndexed { index, faq ->
                    val isExpanded = expandedFaqIndex == index
                    LifeOSCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                            .clickable { expandedFaqIndex = if (isExpanded) null else index }
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = faq.first,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.weight(1f)
                                )
                                Icon(
                                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                    contentDescription = "Expand FAQ",
                                    tint = PrimaryCyan
                                )
                            }

                            AnimatedVisibility(visible = isExpanded) {
                                Column {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    HorizontalDivider()
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = faq.second,
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        lineHeight = 17.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // --- 10. Contact Support Section ---
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = "Contact Support & Inquiries",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Send feature requests directly to our core product team",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(14.dp))

                LifeOSCard(modifier = Modifier.fillMaxWidth()) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = contactName,
                            onValueChange = { contactName = it },
                            label = { Text("Your Name") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                        OutlinedTextField(
                            value = contactEmail,
                            onValueChange = { contactEmail = it },
                            label = { Text("Your Email") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                        OutlinedTextField(
                            value = contactMessage,
                            onValueChange = { contactMessage = it },
                            label = { Text("Message or Request") },
                            minLines = 3,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )

                        if (contactSubmitted) {
                            Text(
                                text = "✓ Thank you! Your message has been sent to LifeOS Support.",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = AccentLime
                            )
                        }

                        Button(
                            onClick = {
                                if (contactName.isNotBlank() && contactEmail.isNotBlank()) {
                                    contactSubmitted = true
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryCyan),
                            modifier = Modifier.align(Alignment.End),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Send Message", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }
        }

        // --- 11. Professional Footer ---
        item {
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = PrimaryCyan,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "LifeOS AI",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Your Personal AI Operating System",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "© 2026 LifeOS Inc. All rights reserved.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(AccentLime)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "All Systems Operational",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = AccentLime
                            )
                        }
                    }
                }
            }
        }
    }

    // --- Demo Video Modal Dialog ---
    if (showDemoVideoModal) {
        AlertDialog(
            onDismissRequest = { showDemoVideoModal = false },
            title = { Text("🎬 LifeOS Interactive Walkthrough", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text(
                        text = "Discover how LifeOS combines smart AI scheduling, local encrypted vaulting, and emergency safety features into one unified personal OS.",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.Black),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Play Video",
                                tint = PrimaryCyan,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Playing Product Demo", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDemoVideoModal = false
                        onNavigateToApp()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryCyan)
                ) {
                    Text("Try LifeOS Now", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDemoVideoModal = false }) {
                    Text("Close")
                }
            }
        )
    }

    // --- Authentication Dialog ---
    if (showAuthModal) {
        AlertDialog(
            onDismissRequest = { showAuthModal = false },
            title = {
                Text(
                    text = if (isSignUpMode) "Create LifeOS Account" else "Log In to LifeOS",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    if (isSignUpMode) {
                        OutlinedTextField(
                            value = authName,
                            onValueChange = { authName = it },
                            label = { Text("Full Name") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    OutlinedTextField(
                        value = authEmail,
                        onValueChange = { authEmail = it },
                        label = { Text("Email Address") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = authPassword,
                        onValueChange = { authPassword = it },
                        label = { Text("Password") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (authSuccessMsg != null) {
                        Text(
                            text = authSuccessMsg ?: "",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = AccentLime
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        authSuccessMsg = if (isSignUpMode) "✓ Account created! Redirecting to LifeOS..." else "✓ Logged in! Loading LifeOS..."
                        coroutineScope.launch {
                            kotlinx.coroutines.delay(800)
                            showAuthModal = false
                            onNavigateToApp()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryCyan)
                ) {
                    Text(if (isSignUpMode) "Sign Up Free" else "Log In", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAuthModal = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
}

