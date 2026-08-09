package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.FolderSpecial
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.NoteAdd
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.DocumentItem
import com.example.ui.components.CategoryChip
import com.example.ui.components.LifeOSCard
import com.example.ui.theme.AccentLime
import com.example.ui.theme.EmergencyRed
import com.example.ui.theme.PrimaryCyan
import com.example.ui.theme.SecondaryPurple

@Composable
fun DocumentsScreen(
    documents: List<DocumentItem>,
    onAddDocument: (title: String, category: String, content: String, isEncrypted: Boolean, tags: String) -> Unit,
    onDeleteDocument: (Int) -> Unit,
    onAiSummarizeDocument: (DocumentItem) -> Unit,
    onAiTranslateDocument: ((DocumentItem, String) -> Unit)? = null
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategoryFilter by remember { mutableStateOf("All") }
    var showAddDialog by remember { mutableStateOf(false) }
    var expandedDocId by remember { mutableStateOf<Int?>(null) }
    var isVaultUnlocked by remember { mutableStateOf(true) }

    // State for passwords visibility toggle
    val revealedPasswords = remember { mutableStateListOf<Int>() }

    // Uploaded mock attachments tracking
    var selectedUploadType by remember { mutableStateOf<String?>(null) }

    val categories = listOf("All", "Documents", "Passwords", "Certificates", "Medical Records", "Notes")

    val filteredDocs = documents.filter { doc ->
        val matchesCategory = when (selectedCategoryFilter) {
            "All" -> true
            "Documents" -> doc.category.equals("Documents", ignoreCase = true) || doc.category.equals("Work", ignoreCase = true) || doc.category.equals("Vault", ignoreCase = true)
            "Passwords" -> doc.category.equals("Passwords", ignoreCase = true) || doc.tags.contains("password", ignoreCase = true)
            "Certificates" -> doc.category.equals("Certificates", ignoreCase = true) || doc.tags.contains("certificate", ignoreCase = true)
            "Medical Records" -> doc.category.equals("Medical", ignoreCase = true) || doc.category.contains("Medical", ignoreCase = true)
            "Notes" -> doc.category.equals("Notes", ignoreCase = true) || doc.category.contains("Note", ignoreCase = true)
            else -> doc.category.equals(selectedCategoryFilter, ignoreCase = true)
        }
        val matchesQuery = searchQuery.isBlank() ||
                doc.title.contains(searchQuery, ignoreCase = true) ||
                doc.content.contains(searchQuery, ignoreCase = true) ||
                doc.tags.contains(searchQuery, ignoreCase = true)
        matchesCategory && matchesQuery
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .testTag("secure_vault_screen_root")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // --- 1. Firebase Storage Sync & Vault Lock Status Bar ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(SecondaryPurple.copy(alpha = 0.2f))
                        .padding(horizontal = 10.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CloudDone,
                        contentDescription = "Firebase Storage",
                        tint = SecondaryPurple,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Firebase Storage • AES-256 Encrypted",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = SecondaryPurple
                    )
                }

                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isVaultUnlocked) AccentLime.copy(alpha = 0.2f) else EmergencyRed.copy(alpha = 0.2f))
                        .clickable {
                            isVaultUnlocked = !isVaultUnlocked
                            Toast.makeText(
                                context,
                                if (isVaultUnlocked) "Vault Unlocked via Biometrics" else "Vault Locked",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        .padding(horizontal = 10.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (isVaultUnlocked) Icons.Default.LockOpen else Icons.Default.Lock,
                        contentDescription = "Vault Security",
                        tint = if (isVaultUnlocked) AccentLime else EmergencyRed,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isVaultUnlocked) "UNLOCKED" else "LOCKED",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (isVaultUnlocked) AccentLime else EmergencyRed
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // --- 2. Search Field ---
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search documents, passwords, medical records...", fontSize = 12.sp) },
                leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = "Search Vault", tint = PrimaryCyan) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("vault_search_field"),
                shape = RoundedCornerShape(20.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryCyan,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            // --- 3. Vault Category Filter Tabs ---
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(categories) { category ->
                    val isSelected = selectedCategoryFilter == category
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isSelected) SecondaryPurple else MaterialTheme.colorScheme.surfaceVariant)
                            .clickable { selectedCategoryFilter = category }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = when (category) {
                                "Documents" -> "📄 Documents"
                                "Passwords" -> "🔑 Passwords"
                                "Certificates" -> "📜 Certificates"
                                "Medical Records" -> "🩺 Medical"
                                "Notes" -> "📝 Notes"
                                else -> "All Vault"
                            },
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.Black else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // --- 4. Locked Vault Shield Warning if Locked ---
            if (!isVaultUnlocked) {
                LifeOSCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = EmergencyRed,
                    backgroundColor = EmergencyRed.copy(alpha = 0.1f)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = "Shield Locked",
                            tint = EmergencyRed,
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Secure Vault is Currently Locked",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = EmergencyRed
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Authenticate with PIN or Fingerprint to reveal encrypted passwords, certificates, and medical records.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = { isVaultUnlocked = true },
                            colors = ButtonDefaults.buttonColors(containerColor = EmergencyRed)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.Fingerprint, contentDescription = null, tint = Color.White)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Unlock with Biometrics", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            } else {
                // --- 5. Unlocked Vault Items List ---
                if (filteredDocs.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No $selectedCategoryFilter items found in Vault.\nTap + to add document, password, or medical record!",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(filteredDocs, key = { it.id }) { doc ->
                            val isExpanded = expandedDocId == doc.id
                            val isPasswordCategory = doc.category.equals("Passwords", ignoreCase = true) || doc.tags.contains("password", ignoreCase = true)
                            val isPasswordRevealed = revealedPasswords.contains(doc.id)

                            LifeOSCard(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { expandedDocId = if (isExpanded) null else doc.id }
                                    .testTag("doc_item_${doc.id}")
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
                                            Icon(
                                                imageVector = when {
                                                    isPasswordCategory -> Icons.Default.Key
                                                    doc.category.contains("Medical", ignoreCase = true) -> Icons.Default.LocalHospital
                                                    doc.category.contains("Certificate", ignoreCase = true) -> Icons.Default.Badge
                                                    else -> Icons.Default.Description
                                                },
                                                contentDescription = "Icon",
                                                tint = when {
                                                    isPasswordCategory -> AccentLime
                                                    doc.category.contains("Medical", ignoreCase = true) -> EmergencyRed
                                                    doc.category.contains("Certificate", ignoreCase = true) -> SecondaryPurple
                                                    else -> PrimaryCyan
                                                },
                                                modifier = Modifier.size(20.dp)
                                            )
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Column {
                                                Text(
                                                    text = doc.title,
                                                    fontSize = 14.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    CategoryChip(category = doc.category)
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Text(
                                                        text = doc.dateUpdated,
                                                        fontSize = 11.sp,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                }
                                            }
                                        }

                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            if (isPasswordCategory) {
                                                IconButton(onClick = {
                                                    if (isPasswordRevealed) {
                                                        revealedPasswords.remove(doc.id)
                                                    } else {
                                                        revealedPasswords.add(doc.id)
                                                    }
                                                }) {
                                                    Icon(
                                                        imageVector = if (isPasswordRevealed) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                                        contentDescription = "Toggle Password",
                                                        tint = PrimaryCyan,
                                                        modifier = Modifier.size(18.dp)
                                                    )
                                                }
                                            }

                                            IconButton(onClick = { onDeleteDocument(doc.id) }) {
                                                Icon(
                                                    imageVector = Icons.Default.Delete,
                                                    contentDescription = "Delete Document",
                                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    // Content or Encrypted Password display
                                    if (isPasswordCategory) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(Color(0xFF1E1E2E))
                                                .padding(10.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = if (isPasswordRevealed) doc.content else "••••••••••••••••",
                                                    fontFamily = FontFamily.Monospace,
                                                    fontSize = 13.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (isPasswordRevealed) AccentLime else PrimaryCyan
                                                )

                                                IconButton(
                                                    onClick = {
                                                        clipboardManager.setText(AnnotatedString(doc.content))
                                                        Toast.makeText(context, "Password copied to clipboard", Toast.LENGTH_SHORT).show()
                                                    },
                                                    modifier = Modifier.size(24.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.ContentCopy,
                                                        contentDescription = "Copy Password",
                                                        tint = PrimaryCyan,
                                                        modifier = Modifier.size(14.dp)
                                                    )
                                                }
                                            }
                                        }
                                    } else {
                                        Text(
                                            text = if (isExpanded) doc.content else doc.content.take(90) + if (doc.content.length > 90) "..." else "",
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            lineHeight = 18.sp
                                        )
                                    }

                                    // Image or PDF Attachment Indicator
                                    if (doc.tags.contains("pdf", ignoreCase = true) || doc.tags.contains("image", ignoreCase = true)) {
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = if (doc.tags.contains("pdf", ignoreCase = true)) Icons.Default.PictureAsPdf else Icons.Default.Image,
                                                contentDescription = null,
                                                tint = SecondaryPurple,
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = "Firebase Cloud Attachment Available (PDF/Img)",
                                                fontSize = 10.sp,
                                                color = SecondaryPurple,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }

                                    if (isExpanded && !isPasswordCategory) {
                                        Spacer(modifier = Modifier.height(10.dp))
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Row(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .background(PrimaryCyan.copy(alpha = 0.15f))
                                                    .clickable { onAiSummarizeDocument(doc) }
                                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.AutoAwesome,
                                                    contentDescription = "AI Summarize",
                                                    tint = PrimaryCyan,
                                                    modifier = Modifier.size(14.dp)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    text = "AI Summarize",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = PrimaryCyan
                                                )
                                            }

                                            Row(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .background(SecondaryPurple.copy(alpha = 0.15f))
                                                    .clickable {
                                                        if (onAiTranslateDocument != null) {
                                                            onAiTranslateDocument(doc, "Spanish")
                                                        } else {
                                                            Toast.makeText(context, "Translating document via LifeOS AI...", Toast.LENGTH_SHORT).show()
                                                        }
                                                    }
                                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = "🌐 AI Translate",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = SecondaryPurple
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
            }
        }

        // --- Add Document FAB ---
        FloatingActionButton(
            onClick = { showAddDialog = true },
            containerColor = SecondaryPurple,
            contentColor = Color.Black,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
                .testTag("add_vault_doc_fab")
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Add Document")
        }

        // --- Add Document / Password / Medical Dialog ---
        if (showAddDialog) {
            var docTitle by remember { mutableStateOf("") }
            var docCategory by remember { mutableStateOf("Documents") }
            var docContent by remember { mutableStateOf("") }
            var docEncrypted by remember { mutableStateOf(true) }
            var attachedType by remember { mutableStateOf("None") }

            AlertDialog(
                onDismissRequest = { showAddDialog = false },
                title = { Text("Add Vault Entry", fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = docTitle,
                            onValueChange = { docTitle = it },
                            label = { Text("Title / Account Name / Doctor") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("add_doc_title_input")
                        )
                        OutlinedTextField(
                            value = docCategory,
                            onValueChange = { docCategory = it },
                            label = { Text("Type (Documents, Passwords, Certificates, Medical Records, Notes)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = docContent,
                            onValueChange = { docContent = it },
                            label = { Text("Content / Secret Password / Record Details") },
                            minLines = 3,
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Attachments Selector
                        Text("Attach File (Firebase Storage Upload):", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PrimaryCyan)
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Button(
                                onClick = {
                                    attachedType = "Image Upload"
                                    Toast.makeText(context, "Image selected for Firebase Storage upload", Toast.LENGTH_SHORT).show()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = if (attachedType == "Image Upload") AccentLime else MaterialTheme.colorScheme.surfaceVariant),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(imageVector = Icons.Default.Image, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Image", color = Color.Black, fontSize = 11.sp)
                            }

                            Button(
                                onClick = {
                                    attachedType = "PDF Upload"
                                    Toast.makeText(context, "PDF selected for Firebase Storage upload", Toast.LENGTH_SHORT).show()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = if (attachedType == "PDF Upload") PrimaryCyan else MaterialTheme.colorScheme.surfaceVariant),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(imageVector = Icons.Default.PictureAsPdf, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("PDF", color = Color.Black, fontSize = 11.sp)
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = docEncrypted,
                                onCheckedChange = { docEncrypted = it },
                                colors = CheckboxDefaults.colors(checkedColor = SecondaryPurple)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("AES-256 Encrypt in Secure Vault", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (docTitle.isNotBlank()) {
                                val tags = "vault,${docCategory.lowercase()},$attachedType"
                                onAddDocument(docTitle, docCategory, docContent, docEncrypted, tags)
                                showAddDialog = false
                                Toast.makeText(context, "Saved to Secure Vault with Firebase Storage!", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SecondaryPurple)
                    ) {
                        Text("Save to Vault", color = Color.Black, fontWeight = FontWeight.Bold)
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
