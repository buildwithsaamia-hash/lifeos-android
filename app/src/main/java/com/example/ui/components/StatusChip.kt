package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FolderSpecial
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Label
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AccentLime
import com.example.ui.theme.EmergencyRed
import com.example.ui.theme.PrimaryCyan
import com.example.ui.theme.SecondaryPurple

@Composable
fun PriorityChip(priority: String) {
    val (bgColor, textColor) = when (priority.lowercase()) {
        "high" -> EmergencyRed.copy(alpha = 0.2f) to EmergencyRed
        "medium" -> Color(0xFFF59E0B).copy(alpha = 0.2f) to Color(0xFFF59E0B)
        else -> AccentLime.copy(alpha = 0.2f) to AccentLime
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = priority.uppercase(),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}

@Composable
fun CategoryChip(category: String) {
    val (bgColor, textColor, icon) = when (category.lowercase()) {
        "work" -> Triple(PrimaryCyan.copy(alpha = 0.2f), PrimaryCyan, Icons.Default.Work)
        "health", "fitness" -> Triple(AccentLime.copy(alpha = 0.2f), AccentLime, Icons.Default.Favorite)
        "medical", "emergency" -> Triple(EmergencyRed.copy(alpha = 0.2f), EmergencyRed, Icons.Default.HealthAndSafety)
        "habit", "streak" -> Triple(SecondaryPurple.copy(alpha = 0.2f), SecondaryPurple, Icons.Default.Repeat)
        "personal" -> Triple(Color(0xFF8B5CF6).copy(alpha = 0.2f), Color(0xFFA78BFA), Icons.Default.Person)
        "finance" -> Triple(Color(0xFF10B981).copy(alpha = 0.2f), Color(0xFF34D399), Icons.Default.AccountBalance)
        "vault", "document" -> Triple(PrimaryCyan.copy(alpha = 0.2f), PrimaryCyan, Icons.Default.FolderSpecial)
        else -> Triple(Color(0xFF64748B).copy(alpha = 0.2f), Color(0xFF94A3B8), Icons.Default.Label)
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = textColor,
                modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = category,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = textColor
            )
        }
    }
}

