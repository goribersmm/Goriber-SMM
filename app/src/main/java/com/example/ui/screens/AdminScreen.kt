package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.ui.viewmodel.SmmViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    viewModel: SmmViewModel,
    onBack: () -> Unit
) {
    val usdToBdt by viewModel.usdToBdt.collectAsState()
    val profitMargin by viewModel.profitMargin.collectAsState()
    val motherPanelBal by viewModel.motherPanelBalance.collectAsState()
    val noticeText by viewModel.noticeText.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()

    var rateInput by remember(usdToBdt) { mutableStateOf(usdToBdt.toString()) }
    var marginInput by remember(profitMargin) { mutableStateOf(profitMargin.toString()) }
    var balanceAdjustInput by remember { mutableStateOf("500") }
    var noticeInput by remember(noticeText) { mutableStateOf(noticeText) }

    val clipboardManager = LocalClipboardManager.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(SmmDarkBackground)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 60.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(SmmDarkCard)
                ) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Admin Control Center",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = SmmGoldLight
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            color = SmmGold.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "SUPERADMIN",
                                color = SmmGold,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Text(
                        text = "Goriber SMM - API & রেট ম্যানেজমেন্ট",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
            }
        }

        // Live Provider & Gateway Status Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SmmDarkCard),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, SmmDarkCardBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "📡 কানেক্টেড এপিআই সার্ভার স্ট্যাটাস",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // MotherPanel Status
                    Surface(
                        color = Color(0xFF0D1424),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("MotherPanel SMM Provider", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TextPrimary)
                                Text("API URL: https://motherpanel.com/api/v2", fontSize = 10.sp, color = TextMuted)
                                Text("ব্যালেন্স: $motherPanelBal", fontSize = 11.sp, color = SmmGreen, fontWeight = FontWeight.Bold)
                            }
                            IconButton(onClick = { viewModel.checkMotherPanelBalance() }) {
                                Icon(Icons.Default.Refresh, contentDescription = "Check", tint = SmmSecondary)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // UddoktaPay Status
                    Surface(
                        color = Color(0xFF0D1424),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("UddoktaPay / Paymently Gateway", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TextPrimary)
                                Text("Base: https://goribersmm.paymently.io/api", fontSize = 10.sp, color = TextMuted)
                                Text("স্ট্যাটাস: সক্রিয় (Active & Ready)", fontSize = 11.sp, color = SmmGreen, fontWeight = FontWeight.Bold)
                            }
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = SmmGreen)
                        }
                    }
                }
            }
        }

        // Pricing & Exchange Rate Controller
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SmmDarkCard),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, SmmDarkCardBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "💵 কারেন্সি ও লাভ (মার্জিন) কন্ট্রোলার",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = rateInput,
                            onValueChange = { rateInput = it },
                            label = { Text("১ ডলার = কত টাকা") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = SmmPrimary,
                                unfocusedBorderColor = SmmDarkCardBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            shape = RoundedCornerShape(10.dp)
                        )

                        OutlinedTextField(
                            value = marginInput,
                            onValueChange = { marginInput = it },
                            label = { Text("লাভের মার্জিন (%)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = SmmPrimary,
                                unfocusedBorderColor = SmmDarkCardBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            val r = rateInput.toDoubleOrNull() ?: 125.0
                            val m = marginInput.toDoubleOrNull() ?: 25.0
                            viewModel.adminSetUsdToBdt(r)
                            viewModel.adminSetMargin(m)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SmmPrimary),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("রেট ও মার্জিন সেভ করুন", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Direct Balance Top-up for User
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SmmDarkCard),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, SmmDarkCardBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "👤 ইউজার ব্যালেন্স সমন্বয় (Direct Balance Adjust)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "বর্তমান ইউজার ব্যালেন্স: ৳${String.format("%.2f", userProfile?.balanceBdt ?: 0.0)} BDT",
                        fontSize = 12.sp,
                        color = SmmGoldLight,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = balanceAdjustInput,
                            onValueChange = { balanceAdjustInput = it },
                            label = { Text("নতুন ব্যালেন্স (৳)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = SmmPrimary,
                                unfocusedBorderColor = SmmDarkCardBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            shape = RoundedCornerShape(10.dp)
                        )

                        Button(
                            onClick = {
                                val b = balanceAdjustInput.toDoubleOrNull() ?: 0.0
                                viewModel.adminUpdateUserBalance(b)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = SmmGreen),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("আপডেট", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Announcement Notice Editor
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SmmDarkCard),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, SmmDarkCardBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "📢 ইউজার নোটিশ বোর্ড পরিবর্তন করুন",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = noticeInput,
                        onValueChange = { noticeInput = it },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SmmPrimary,
                            unfocusedBorderColor = SmmDarkCardBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = { viewModel.setNotice(noticeInput) },
                        colors = ButtonDefaults.buttonColors(containerColor = SmmPrimary),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("নোটিশ ব্রডকাস্ট করুন")
                    }
                }
            }
        }
    }
}
