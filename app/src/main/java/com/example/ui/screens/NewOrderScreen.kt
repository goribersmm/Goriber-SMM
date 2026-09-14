package com.example.ui.screens

import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SmmPlatform
import com.example.data.model.SmmService
import com.example.ui.theme.*
import com.example.ui.viewmodel.SmmViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewOrderScreen(
    viewModel: SmmViewModel,
    onNavigateToAddFunds: () -> Unit,
    onOrderSuccess: () -> Unit
) {
    val services by viewModel.services.collectAsState()
    val selectedService by viewModel.selectedService.collectAsState()
    val targetLink by viewModel.targetLink.collectAsState()
    val quantity by viewModel.quantity.collectAsState()
    val isPlacingOrder by viewModel.isPlacingOrder.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()

    var selectedPlatformFilter by remember { mutableStateOf<SmmPlatform?>(null) }
    var showServiceDialog by remember { mutableStateOf(false) }
    var serviceSearchText by remember { mutableStateOf("") }
    var showConfirmDialog by remember { mutableStateOf(false) }

    val clipboardManager = LocalClipboardManager.current
    val balance = userProfile?.balanceBdt ?: 0.0

    val currentQty = quantity.toIntOrNull() ?: 0
    val totalCharge = if (selectedService != null && currentQty > 0) {
        (selectedService!!.rateBdt * currentQty) / 1000.0
    } else 0.0

    val isBalanceSufficient = balance >= totalCharge

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(SmmDarkBackground)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 110.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "নতুন অর্ডার প্লেস করুন",
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TextPrimary
            )
            Text(
                text = "আপনার সোশ্যাল মিডিয়া অ্যাকাউন্ট বুস্ট করতে তথ্য পূরণ করুন",
                fontSize = 12.sp,
                color = TextSecondary
            )
        }

        // Platform Category selector
        item {
            Text(
                text = "১. প্ল্যাটফর্ম সিলেক্ট করুন",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = SmmSecondary
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    FilterChip(
                        selected = selectedPlatformFilter == null,
                        onClick = { selectedPlatformFilter = null },
                        label = { Text("সকল প্ল্যাটফর্ম", fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = SmmPrimary,
                            selectedLabelColor = Color.White,
                            containerColor = SmmDarkCard,
                            labelColor = TextSecondary
                        )
                    )
                }
                val platforms = listOf(
                    SmmPlatform.FACEBOOK,
                    SmmPlatform.YOUTUBE,
                    SmmPlatform.TIKTOK,
                    SmmPlatform.INSTAGRAM,
                    SmmPlatform.TELEGRAM,
                    SmmPlatform.TWITTER
                )
                items(platforms) { platform ->
                    FilterChip(
                        selected = selectedPlatformFilter == platform,
                        onClick = { selectedPlatformFilter = platform },
                        label = { Text(platform.displayName, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(platform.badgeColor),
                            selectedLabelColor = Color.White,
                            containerColor = SmmDarkCard,
                            labelColor = TextSecondary
                        )
                    )
                }
            }
        }

        // Service Selector Card
        item {
            Text(
                text = "২. সার্ভিস নির্বাচন করুন",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = SmmSecondary
            )
            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showServiceDialog = true }
                    .testTag("select_service_card"),
                colors = CardDefaults.cardColors(containerColor = SmmDarkCard),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, SmmDarkCardBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = selectedService?.name ?: "সার্ভিস বেছে নিন...",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (selectedService != null) TextPrimary else TextSecondary,
                            maxLines = 2
                        )
                        if (selectedService != null) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "রেট: ৳${selectedService!!.rateBdt} / ১০০০ টি | Min: ${selectedService!!.min} | Max: ${selectedService!!.max}",
                                fontSize = 12.sp,
                                color = SmmGoldLight,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Select",
                        tint = SmmSecondary,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }

        // Service Details Info Box
        if (selectedService != null) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1B30)),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SmmSecondary.copy(alpha = 0.3f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = SmmSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "সার্ভিস বিবরণ ও গ্যারান্টি",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = SmmSecondary
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = selectedService!!.description,
                            fontSize = 12.sp,
                            color = TextSecondary,
                            lineHeight = 16.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            Text(
                                text = "⚡ স্পিড: ${selectedService!!.averageTime}",
                                fontSize = 11.sp,
                                color = SmmGreen,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "🛡️ রিফিল: ${if (selectedService!!.refill) "গ্যারান্টিড" else "নো রিফিল"}",
                                fontSize = 11.sp,
                                color = SmmGoldLight,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }

        // Link Input
        item {
            Text(
                text = "৩. টার্গেট লিংক (প্রোফাইল/পোস্ট/ভিডিও লিংক)",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = SmmSecondary
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = targetLink,
                onValueChange = { viewModel.setTargetLink(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("target_link_input"),
                placeholder = {
                    Text(
                        "যেমন: https://facebook.com/username অথবা পোস্টের লিংক",
                        fontSize = 12.sp,
                        color = TextMuted
                    )
                },
                trailingIcon = {
                    IconButton(
                        onClick = {
                            val clipData = clipboardManager.getText()?.text
                            if (!clipData.isNullOrBlank()) {
                                viewModel.setTargetLink(clipData)
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentPaste,
                            contentDescription = "Paste",
                            tint = SmmSecondary
                        )
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = SmmPrimary,
                    unfocusedBorderColor = SmmDarkCardBorder,
                    focusedContainerColor = SmmDarkCard,
                    unfocusedContainerColor = SmmDarkCard,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )
        }

        // Quantity Input
        item {
            Text(
                text = "৪. পরিমাণ (Quantity)",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = SmmSecondary
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = quantity,
                onValueChange = { viewModel.setQuantity(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("quantity_input"),
                placeholder = {
                    Text(
                        "যেমন: 1000",
                        fontSize = 13.sp,
                        color = TextMuted
                    )
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = SmmPrimary,
                    unfocusedBorderColor = SmmDarkCardBorder,
                    focusedContainerColor = SmmDarkCard,
                    unfocusedContainerColor = SmmDarkCard,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Quick preset chips
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(500, 1000, 2000, 5000, 10000).forEach { preset ->
                    Surface(
                        modifier = Modifier.clickable {
                            viewModel.setQuantity(preset.toString())
                        },
                        color = SmmDarkCard,
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, SmmDarkCardBorder)
                    ) {
                        Text(
                            text = "+$preset",
                            color = SmmSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        // Cost & Balance Breakdown Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SmmDarkCard),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, SmmDarkCardBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("মোট চার্জ (টাকা):", color = TextSecondary, fontSize = 13.sp)
                        Text(
                            "৳${String.format("%.2f", totalCharge)} BDT",
                            color = SmmGoldLight,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("আপনার বর্তমান ব্যালেন্স:", color = TextSecondary, fontSize = 13.sp)
                        Text(
                            "৳${String.format("%.2f", balance)} BDT",
                            color = TextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (totalCharge > 0) {
                        Spacer(modifier = Modifier.height(6.dp))
                        val remains = balance - totalCharge
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("অর্ডারের পর অবশিষ্ট ব্যালেন্স:", color = TextSecondary, fontSize = 13.sp)
                            Text(
                                "৳${String.format("%.2f", remains)} BDT",
                                color = if (remains >= 0) SmmGreen else SmmRed,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    if (!isBalanceSufficient && totalCharge > 0) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "⚠️ ব্যালেন্স অপর্যাপ্ত!",
                                color = SmmRed,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            TextButton(onClick = onNavigateToAddFunds) {
                                Text("ফান্ড এড করুন ➔", color = BkashPink, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Place Order Action Button
        item {
            Button(
                onClick = {
                    if (isBalanceSufficient) {
                        showConfirmDialog = true
                    } else {
                        onNavigateToAddFunds()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("submit_order_button"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isBalanceSufficient) SmmPrimary else BkashPink
                ),
                shape = RoundedCornerShape(14.dp),
                enabled = !isPlacingOrder
            ) {
                if (isPlacingOrder) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Icon(
                        imageVector = if (isBalanceSufficient) Icons.Default.RocketLaunch else Icons.Default.AddCard,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isBalanceSufficient) "অর্ডার কনফার্ম করুন (৳${String.format("%.2f", totalCharge)})" else "ব্যালেন্স এড করুন",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }

    // Service Selection Dialog
    if (showServiceDialog) {
        AlertDialog(
            onDismissRequest = { showServiceDialog = false },
            title = {
                Column {
                    Text("সার্ভিস বেছে নিন", fontWeight = FontWeight.Bold, color = TextPrimary)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = serviceSearchText,
                        onValueChange = { serviceSearchText = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("সার্চ করুন...", fontSize = 12.sp) },
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) }
                    )
                }
            },
            text = {
                val filtered = services.filter { s ->
                    (selectedPlatformFilter == null || s.platform == selectedPlatformFilter) &&
                            (serviceSearchText.isBlank() || s.name.contains(serviceSearchText, ignoreCase = true) || s.category.contains(serviceSearchText, ignoreCase = true))
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(350.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filtered) { s ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.selectService(s)
                                    showServiceDialog = false
                                },
                            colors = CardDefaults.cardColors(
                                containerColor = if (selectedService?.serviceId == s.serviceId) SmmPrimary.copy(alpha = 0.2f) else SmmDarkCard
                            ),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (selectedService?.serviceId == s.serviceId) SmmPrimary else SmmDarkCardBorder
                            )
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "[#${s.serviceId}] ${s.name}",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "রেট: ৳${s.rateBdt} / 1K",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = SmmGoldLight
                                    )
                                    Text(
                                        text = "Min: ${s.min} | Max: ${s.max}",
                                        fontSize = 11.sp,
                                        color = TextSecondary
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showServiceDialog = false }) {
                    Text("বন্ধ করুন", color = SmmSecondary)
                }
            },
            containerColor = SmmDarkSurface
        )
    }

    // Order Confirmation Dialog
    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = {
                Text("অর্ডার নিশ্চিতকরণ", fontWeight = FontWeight.Bold, color = TextPrimary)
            },
            text = {
                Column {
                    Text("আপনি কি নিম্নলিখিত অর্ডারটি কনফার্ম করতে চান?", color = TextSecondary, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("সার্ভিস: ${selectedService?.name}", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Text("টার্গেট লিংক: $targetLink", color = SmmSecondary, fontSize = 12.sp)
                    Text("পরিমাণ: $currentQty", color = TextPrimary, fontSize = 13.sp)
                    Text("টোটাল চার্জ: ৳${String.format("%.2f", totalCharge)} BDT", color = SmmGoldLight, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showConfirmDialog = false
                        viewModel.placeOrder()
                        onOrderSuccess()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SmmPrimary)
                ) {
                    Text("হ্যাঁ, অর্ডার করুন", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) {
                    Text("বাতিল", color = TextSecondary)
                }
            },
            containerColor = SmmDarkSurface
        )
    }
}
