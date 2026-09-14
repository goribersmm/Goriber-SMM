package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.TransactionEntity
import com.example.ui.theme.*
import com.example.ui.viewmodel.SmmViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFundsScreen(
    viewModel: SmmViewModel
) {
    val userProfile by viewModel.userProfile.collectAsState()
    val transactions by viewModel.transactions.collectAsState()

    var selectedMethodTab by remember { mutableIntStateOf(0) } // 0: Automated UddoktaPay, 1: Manual bKash/Nagad
    var amountInput by remember { mutableStateOf("250") }
    var manualMethod by remember { mutableStateOf("bKash") }
    var manualTrxId by remember { mutableStateOf("") }
    var manualAmount by remember { mutableStateOf("250") }

    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val balance = userProfile?.balanceBdt ?: 0.0

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(SmmDarkBackground)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 110.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column {
                Text(
                    text = "ফান্ড এড করুন (ডিপোজিট)",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextPrimary
                )
                Text(
                    text = "bKash, Nagad, Rocket ও UddoktaPay এর মাধ্যমে ইনস্ট্যান্ট ব্যালেন্স যোগ করুন",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }
        }

        // Current Balance Banner
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SmmDarkCard),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, SmmDarkCardBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "বর্তমান একাউন্ট ব্যালেন্স", fontSize = 12.sp, color = TextSecondary)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "৳ ${String.format("%.2f", balance)} BDT",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black,
                            color = SmmGoldLight
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(SmmGold.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountBalanceWallet,
                            contentDescription = null,
                            tint = SmmGold,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }

        // Bonus Promotion Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF1E1430)
                ),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, SmmPurple.copy(alpha = 0.4f))
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "🎁", fontSize = 24.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "স্পেশাল ডিপোজিট বোনাস!",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = SmmPrimaryLight
                        )
                        Text(
                            text = "৳৫০০ বা তার বেশি ডিপোজিটে পাবেন ৫% ক্যাশব্যাক বোনাস ইনস্ট্যান্ট!",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                }
            }
        }

        // Method Toggle: Automated (UddoktaPay) vs Manual
        item {
            TabRow(
                selectedTabIndex = selectedMethodTab,
                containerColor = SmmDarkCard,
                contentColor = SmmSecondary,
                divider = {}
            ) {
                Tab(
                    selected = selectedMethodTab == 0,
                    onClick = { selectedMethodTab = 0 },
                    text = {
                        Text(
                            text = "⚡ UddoktaPay (অটোমেটিক)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                )
                Tab(
                    selected = selectedMethodTab == 1,
                    onClick = { selectedMethodTab = 1 },
                    text = {
                        Text(
                            text = "📝 ম্যানুয়াল (bKash/Nagad)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                )
            }
        }

        if (selectedMethodTab == 0) {
            // Automated Paymently / UddoktaPay
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = SmmDarkCard),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SmmDarkCardBorder)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                color = UddoktaTeal.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "UddoktaPay Gateway",
                                    color = UddoktaTeal,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "bKash / Nagad / Rocket / Cards",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = "ডিপোজিট পরিমাণ লিখুন (BDT):",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = amountInput,
                            onValueChange = { amountInput = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("uddoktapay_amount_input"),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            leadingIcon = {
                                Text("৳", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = SmmGoldLight)
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = SmmPrimary,
                                unfocusedBorderColor = SmmDarkCardBorder,
                                focusedContainerColor = Color(0xFF0D1424),
                                unfocusedContainerColor = Color(0xFF0D1424),
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Quick Presets
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf("50", "100", "250", "500", "1000", "2000").forEach { preset ->
                                Surface(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { amountInput = preset },
                                    color = if (amountInput == preset) SmmPrimary.copy(alpha = 0.3f) else Color(0xFF0D1424),
                                    shape = RoundedCornerShape(8.dp),
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        if (amountInput == preset) SmmPrimary else SmmDarkCardBorder
                                    )
                                ) {
                                    Text(
                                        text = "৳$preset",
                                        color = if (amountInput == preset) Color.White else TextSecondary,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(vertical = 6.dp),
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        Button(
                            onClick = {
                                val amt = amountInput.toDoubleOrNull() ?: 0.0
                                viewModel.launchUddoktaPay(amt)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("pay_via_uddoktapay_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = BkashPink),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Bolt, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "UddoktaPay দিয়ে অটোমেটিক পে করুন",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Fast Test Verification button for instant demo
                        OutlinedButton(
                            onClick = {
                                val amt = amountInput.toDoubleOrNull() ?: 0.0
                                viewModel.processAddFunds(amt, "UddoktaPay", "UPAY${System.currentTimeMillis().toString().takeLast(6)}")
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, SmmSecondary)
                        ) {
                            Text(
                                text = "ইনস্ট্যান্ট টেস্ট ডিপোজিট ভেরিফাই (৳$amountInput)",
                                fontSize = 12.sp,
                                color = SmmSecondary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        } else {
            // Manual Payment bKash / Nagad / Rocket
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = SmmDarkCard),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SmmDarkCardBorder)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "ম্যানুয়াল সেন্ড মানি নির্দেশিকা",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("bKash", "Nagad", "Rocket").forEach { m ->
                                FilterChip(
                                    selected = manualMethod == m,
                                    onClick = { manualMethod = m },
                                    label = { Text(m, fontSize = 12.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = when (m) {
                                            "bKash" -> BkashPink
                                            "Nagad" -> NagadOrange
                                            else -> RocketPurple
                                        },
                                        selectedLabelColor = Color.White,
                                        containerColor = Color(0xFF0D1424),
                                        labelColor = TextSecondary
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Number Card
                        val targetNumber = when (manualMethod) {
                            "bKash" -> "01789-234567 (Personal)"
                            "Nagad" -> "01876-543210 (Personal)"
                            else -> "01912-345678-9 (Personal)"
                        }

                        Surface(
                            color = Color(0xFF0D1424),
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, SmmDarkCardBorder)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(text = "$manualMethod সেন্ড মানি নম্বর:", fontSize = 11.sp, color = TextSecondary)
                                    Text(text = targetNumber, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                }
                                IconButton(
                                    onClick = {
                                        clipboardManager.setText(AnnotatedString(targetNumber.split(" ")[0]))
                                    }
                                ) {
                                    Icon(imageVector = Icons.Default.ContentCopy, contentDescription = "Copy", tint = SmmSecondary)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        OutlinedTextField(
                            value = manualAmount,
                            onValueChange = { manualAmount = it },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("টাকার পরিমাণ (BDT)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = SmmPrimary,
                                unfocusedBorderColor = SmmDarkCardBorder,
                                focusedContainerColor = Color(0xFF0D1424),
                                unfocusedContainerColor = Color(0xFF0D1424),
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = manualTrxId,
                            onValueChange = { manualTrxId = it },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Transaction ID (TrxID)") },
                            placeholder = { Text("যেমন: BK8934JFK2", fontSize = 12.sp) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = SmmPrimary,
                                unfocusedBorderColor = SmmDarkCardBorder,
                                focusedContainerColor = Color(0xFF0D1424),
                                unfocusedContainerColor = Color(0xFF0D1424),
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                val amt = manualAmount.toDoubleOrNull() ?: 0.0
                                viewModel.processAddFunds(amt, manualMethod, manualTrxId)
                                manualTrxId = ""
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SmmGreen),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("ট্রানজেকশন সাবমিট করুন", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Transactions History List
        item {
            Column {
                Text(
                    text = "সাম্প্রতিক ডিপোজিট হিস্টোরি",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))

                if (transactions.isEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = SmmDarkCard),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "কোন ডিপোজিট হিস্টোরি পাওয়া যায়নি",
                                fontSize = 12.sp,
                                color = TextMuted
                            )
                        }
                    }
                } else {
                    transactions.forEach { trx ->
                        TransactionRowItem(trx = trx)
                    }
                }
            }
        }
    }
}

@Composable
fun TransactionRowItem(trx: TransactionEntity) {
    val dateStr = remember(trx.timestamp) {
        val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
        sdf.format(Date(trx.timestamp))
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        colors = CardDefaults.cardColors(containerColor = SmmDarkCard),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, SmmDarkCardBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = trx.paymentMethod,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Trx: ${trx.trxId}",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = dateStr,
                    fontSize = 10.sp,
                    color = TextMuted
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "+৳${String.format("%.2f", trx.amountBdt)}",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = SmmGreen
                )
                Text(
                    text = trx.status,
                    fontSize = 10.sp,
                    color = SmmGreen,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
