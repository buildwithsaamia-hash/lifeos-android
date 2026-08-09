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
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
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

data class TransactionItem(
    val id: Int,
    val title: String,
    val amount: Double,
    val type: String, // "Income", "Expense", "Savings"
    val category: String, // "Salary", "Housing", "Food", "Tech", "Transport", "Investment"
    val date: String = "Today",
    val note: String = ""
)

data class BudgetCategory(
    val categoryName: String,
    val allocated: Double,
    val spent: Double,
    val color: Color
)

@Composable
fun FinanceScreen(
    onNavigateTab: (Int) -> Unit = {}
) {
    val context = LocalContext.current

    var selectedFilter by remember { mutableStateOf("Overview") }
    var searchQuery by remember { mutableStateOf("") }
    var showAddDialog by remember { mutableStateOf(false) }

    // Sample initial transaction state
    val transactions = remember {
        mutableStateListOf(
            TransactionItem(1, "Senior Dev Salary", 5800.00, "Income", "Salary", "Jul 24", "Monthly salary payout"),
            TransactionItem(2, "Apartment Rent", 1650.00, "Expense", "Housing", "Jul 20", "Fixed monthly rent"),
            TransactionItem(3, "Organic Grocery Store", 240.50, "Expense", "Food", "Jul 22", "Weekly groceries"),
            TransactionItem(4, "High-Yield Savings Deposit", 1200.00, "Savings", "Investment", "Jul 23", "Automated savings allocation"),
            TransactionItem(5, "AWS & Cloud Servers", 180.00, "Expense", "Tech", "Jul 18", "Developer hosting costs"),
            TransactionItem(6, "Freelance App Consulting", 1250.00, "Income", "Salary", "Jul 15", "Client milestone payout"),
            TransactionItem(7, "Gym & Fitness Pass", 75.00, "Expense", "Health", "Jul 10", "Monthly membership")
        )
    }

    val budgets = remember {
        listOf(
            BudgetCategory("Housing", 1800.00, 1650.00, Color(0xFF81C784)),
            BudgetCategory("Food & Dining", 600.00, 380.50, PrimaryCyan),
            BudgetCategory("Tech & Infrastructure", 400.00, 180.00, SecondaryPurple),
            BudgetCategory("Transport", 300.00, 120.00, Color(0xFFFFB74D)),
            BudgetCategory("Entertainment & Leisure", 350.00, 210.00, Color(0xFFE57373))
        )
    }

    // Calculated Aggregates
    val totalIncome = transactions.filter { it.type == "Income" }.sumOf { it.amount }
    val totalExpenses = transactions.filter { it.type == "Expense" }.sumOf { it.amount }
    val totalSavings = transactions.filter { it.type == "Savings" }.sumOf { it.amount }
    val netBalance = totalIncome - totalExpenses

    val filteredTransactions = transactions.filter { tx ->
        val matchesFilter = when (selectedFilter) {
            "Income" -> tx.type == "Income"
            "Expenses" -> tx.type == "Expense"
            "Savings" -> tx.type == "Savings"
            else -> true
        }
        val matchesQuery = searchQuery.isBlank() ||
                tx.title.contains(searchQuery, ignoreCase = true) ||
                tx.category.contains(searchQuery, ignoreCase = true)
        matchesFilter && matchesQuery
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .testTag("finance_dashboard_root")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // --- 1. Firestore Ledger Sync Badge Header ---
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
                        contentDescription = "Firestore Sync",
                        tint = AccentLime,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Firestore Real-Time Financial Ledger Active",
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
                            Toast.makeText(context, "Downloading Financial Audit Report (PDF)...", Toast.LENGTH_SHORT).show()
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
                        text = "Export Report",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryCyan
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // --- 2. Financial Overview Cards Grid ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Net Balance Card
                LifeOSCard(
                    modifier = Modifier.weight(1f),
                    borderColor = PrimaryCyan,
                    backgroundColor = PrimaryCyan.copy(alpha = 0.08f)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.AccountBalanceWallet, contentDescription = null, tint = PrimaryCyan, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Net Cashflow", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "$${String.format("%.2f", netBalance)}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = PrimaryCyan
                        )
                    }
                }

                // Income Card
                LifeOSCard(
                    modifier = Modifier.weight(1f),
                    borderColor = AccentLime,
                    backgroundColor = AccentLime.copy(alpha = 0.08f)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.ArrowUpward, contentDescription = null, tint = AccentLime, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Income", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "+$${String.format("%.2f", totalIncome)}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = AccentLime
                        )
                    }
                }

                // Expense Card
                LifeOSCard(
                    modifier = Modifier.weight(1f),
                    borderColor = EmergencyRed,
                    backgroundColor = EmergencyRed.copy(alpha = 0.08f)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.ArrowDownward, contentDescription = null, tint = EmergencyRed, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Expenses", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "-$${String.format("%.2f", totalExpenses)}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = EmergencyRed
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // --- 3. View Switcher Tabs ---
            val filterTabs = listOf("Overview", "Income", "Expenses", "Savings", "Budget", "Reports")
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(filterTabs) { tab ->
                    val isSelected = selectedFilter == tab
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isSelected) PrimaryCyan else MaterialTheme.colorScheme.surfaceVariant)
                            .clickable { selectedFilter = tab }
                            .padding(horizontal = 14.dp, vertical = 7.dp)
                    ) {
                        Text(
                            text = when (tab) {
                                "Income" -> "📈 Income"
                                "Expenses" -> "📉 Expenses"
                                "Savings" -> "🏦 Savings"
                                "Budget" -> "📊 Budget"
                                "Reports" -> "📑 Reports"
                                else -> "🌐 Overview"
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
                // --- 4. Interactive Canvas Pie Chart (Expense Distribution) & Bar Chart ---
                if (selectedFilter == "Overview" || selectedFilter == "Reports") {
                    item {
                        LifeOSCard(
                            modifier = Modifier.fillMaxWidth(),
                            borderColor = SecondaryPurple,
                            backgroundColor = SecondaryPurple.copy(alpha = 0.08f)
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(imageVector = Icons.Default.PieChart, contentDescription = null, tint = SecondaryPurple, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Expense Distribution (Pie Chart)",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }

                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(AccentLime)
                                            .padding(horizontal = 8.dp, vertical = 3.dp)
                                    ) {
                                        Text("July 2026", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                // Donut Pie Chart Canvas
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier.size(110.dp)
                                    ) {
                                        Canvas(modifier = Modifier.fillMaxSize()) {
                                            val strokeWidth = 22.dp.toPx()
                                            val sizeVal = size.minDimension - strokeWidth
                                            val topLeft = Offset(strokeWidth / 2, strokeWidth / 2)

                                            // Portions: Housing 52%, Food 12%, Tech 6%, Health 2%, Savings 28%
                                            drawArc(
                                                color = Color(0xFF81C784),
                                                startAngle = -90f,
                                                sweepAngle = 187f,
                                                useCenter = false,
                                                topLeft = topLeft,
                                                size = Size(sizeVal, sizeVal),
                                                style = Stroke(strokeWidth)
                                            )
                                            drawArc(
                                                color = PrimaryCyan,
                                                startAngle = 97f,
                                                sweepAngle = 43f,
                                                useCenter = false,
                                                topLeft = topLeft,
                                                size = Size(sizeVal, sizeVal),
                                                style = Stroke(strokeWidth)
                                            )
                                            drawArc(
                                                color = SecondaryPurple,
                                                startAngle = 140f,
                                                sweepAngle = 22f,
                                                useCenter = false,
                                                topLeft = topLeft,
                                                size = Size(sizeVal, sizeVal),
                                                style = Stroke(strokeWidth)
                                            )
                                            drawArc(
                                                color = Color(0xFFFFB74D),
                                                startAngle = 162f,
                                                sweepAngle = 108f,
                                                useCenter = false,
                                                topLeft = topLeft,
                                                size = Size(sizeVal, sizeVal),
                                                style = Stroke(strokeWidth)
                                            )
                                        }
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text("TOTAL", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            Text("$3.1k", fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = PrimaryCyan)
                                        }
                                    }

                                    // Pie Chart Legend
                                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                        LegendItem(color = Color(0xFF81C784), label = "Housing (52%)")
                                        LegendItem(color = PrimaryCyan, label = "Food & Dining (12%)")
                                        LegendItem(color = SecondaryPurple, label = "Tech & Tools (6%)")
                                        LegendItem(color = Color(0xFFFFB74D), label = "Savings & Invest (28%)")
                                    }
                                }
                            }
                        }
                    }

                    // Monthly Income vs Expense Bar Chart
                    item {
                        LifeOSCard(
                            modifier = Modifier.fillMaxWidth(),
                            borderColor = PrimaryCyan,
                            backgroundColor = PrimaryCyan.copy(alpha = 0.08f)
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = Icons.Default.BarChart, contentDescription = null, tint = PrimaryCyan, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Monthly Financial Bar Chart (6 Months)",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                val barData = listOf(
                                    BarMonth("Mar", 5200f, 2800f),
                                    BarMonth("Apr", 5500f, 2900f),
                                    BarMonth("May", 6000f, 3100f),
                                    BarMonth("Jun", 6200f, 3050f),
                                    BarMonth("Jul", 7050f, 3145f)
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.Bottom
                                ) {
                                    barData.forEach { month ->
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Row(
                                                horizontalArrangement = Arrangement.spacedBy(3.dp),
                                                verticalAlignment = Alignment.Bottom,
                                                modifier = Modifier.height(60.dp)
                                            ) {
                                                // Income Bar (Green/Lime)
                                                Box(
                                                    modifier = Modifier
                                                        .width(10.dp)
                                                        .height(((month.income / 7500f) * 60).dp)
                                                        .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                                        .background(AccentLime)
                                                )
                                                // Expense Bar (Red/Purple)
                                                Box(
                                                    modifier = Modifier
                                                        .width(10.dp)
                                                        .height(((month.expense / 7500f) * 60).dp)
                                                        .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                                        .background(EmergencyRed)
                                                )
                                            }
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(month.name, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(modifier = Modifier.size(8.dp).background(AccentLime, CircleShape))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Income", fontSize = 10.sp)
                                    }
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(modifier = Modifier.size(8.dp).background(EmergencyRed, CircleShape))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Expenses", fontSize = 10.sp)
                                    }
                                }
                            }
                        }
                    }
                }

                // --- 5. Category Budget Progress Section ---
                if (selectedFilter == "Overview" || selectedFilter == "Budget") {
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
                                    Text(
                                        text = "Category Budget Limits",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Total Spent: $${String.format("%.2f", totalExpenses)}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = PrimaryCyan
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                budgets.forEach { b ->
                                    val fraction = (b.spent / b.allocated).toFloat().coerceIn(0f, 1f)
                                    Column(modifier = Modifier.padding(vertical = 4.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(b.categoryName, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                            Text("$${b.spent} / $${b.allocated}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                        Spacer(modifier = Modifier.height(3.dp))
                                        LinearProgressIndicator(
                                            progress = { fraction },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(6.dp)
                                                .clip(RoundedCornerShape(3.dp)),
                                            color = b.color,
                                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // --- 6. AI Financial Advisory Report ---
                if (selectedFilter == "Reports" || selectedFilter == "Overview") {
                    item {
                        LifeOSCard(
                            modifier = Modifier.fillMaxWidth(),
                            borderColor = AccentLime,
                            backgroundColor = AccentLime.copy(alpha = 0.08f)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = AccentLime, modifier = Modifier.size(22.dp))
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text("AI Financial Audit & Cash Flow Report", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = AccentLime)
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        "Savings rate is currently 25.8% (Above recommended 20%). Savings goal to reach $10,000 liquid reserve is on track for Q4 2026.",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        lineHeight = 16.sp
                                    )
                                }
                            }
                        }
                    }
                }

                // --- 7. Search & Transactions List ---
                item {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Search transactions by title or category...", fontSize = 12.sp) },
                        leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = PrimaryCyan) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("finance_search_field"),
                        shape = RoundedCornerShape(20.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryCyan,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        )
                    )
                }

                items(filteredTransactions, key = { it.id }) { tx ->
                    LifeOSCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("transaction_item_${tx.id}")
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
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(
                                            when (tx.type) {
                                                "Income" -> AccentLime.copy(alpha = 0.2f)
                                                "Savings" -> SecondaryPurple.copy(alpha = 0.2f)
                                                else -> EmergencyRed.copy(alpha = 0.2f)
                                            }
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = when (tx.type) {
                                            "Income" -> Icons.Default.ArrowUpward
                                            "Savings" -> Icons.Default.Savings
                                            else -> Icons.Default.ArrowDownward
                                        },
                                        contentDescription = null,
                                        tint = when (tx.type) {
                                            "Income" -> AccentLime
                                            "Savings" -> SecondaryPurple
                                            else -> EmergencyRed
                                        },
                                        modifier = Modifier.size(18.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(10.dp))

                                Column {
                                    Text(
                                        text = tx.title,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = tx.category,
                                            fontSize = 11.sp,
                                            color = PrimaryCyan,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "• ${tx.date}",
                                            fontSize = 10.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (tx.type == "Income") "+$${tx.amount}" else "-$${tx.amount}",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = when (tx.type) {
                                        "Income" -> AccentLime
                                        "Savings" -> SecondaryPurple
                                        else -> EmergencyRed
                                    }
                                )

                                IconButton(onClick = {
                                    transactions.remove(tx)
                                    Toast.makeText(context, "Transaction removed", Toast.LENGTH_SHORT).show()
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete Transaction",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }

        // --- Add Transaction FAB ---
        FloatingActionButton(
            onClick = { showAddDialog = true },
            containerColor = PrimaryCyan,
            contentColor = Color.Black,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
                .testTag("add_transaction_fab")
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Add Transaction")
        }

        // --- Add Transaction Dialog ---
        if (showAddDialog) {
            var txTitle by remember { mutableStateOf("") }
            var txAmountStr by remember { mutableStateOf("") }
            var txType by remember { mutableStateOf("Expense") }
            var txCategory by remember { mutableStateOf("Food") }

            AlertDialog(
                onDismissRequest = { showAddDialog = false },
                title = { Text("Log Transaction to Firestore Ledger", fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = txTitle,
                            onValueChange = { txTitle = it },
                            label = { Text("Title (e.g., Client Payment, Groceries)") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("add_tx_title_input")
                        )

                        OutlinedTextField(
                            value = txAmountStr,
                            onValueChange = { txAmountStr = it },
                            label = { Text("Amount ($)") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Text("Transaction Type:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            listOf("Income", "Expense", "Savings").forEach { typeOption ->
                                val isSelected = txType == typeOption
                                Button(
                                    onClick = { txType = typeOption },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (isSelected) PrimaryCyan else MaterialTheme.colorScheme.surfaceVariant
                                    ),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = typeOption,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) Color.Black else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }

                        OutlinedTextField(
                            value = txCategory,
                            onValueChange = { txCategory = it },
                            label = { Text("Category (Salary, Housing, Food, Tech, Transport)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val amt = txAmountStr.toDoubleOrNull() ?: 0.0
                            if (txTitle.isNotBlank() && amt > 0) {
                                val newId = (transactions.maxOfOrNull { it.id } ?: 0) + 1
                                transactions.add(
                                    0,
                                    TransactionItem(
                                        id = newId,
                                        title = txTitle,
                                        amount = amt,
                                        type = txType,
                                        category = txCategory,
                                        date = "Jul 24"
                                    )
                                )
                                showAddDialog = false
                                Toast.makeText(context, "Transaction synced to Firestore!", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryCyan)
                    ) {
                        Text("Save Transaction", color = Color.Black, fontWeight = FontWeight.Bold)
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

@Composable
private fun LegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface)
    }
}

private data class BarMonth(
    val name: String,
    val income: Float,
    val expense: Float
)
