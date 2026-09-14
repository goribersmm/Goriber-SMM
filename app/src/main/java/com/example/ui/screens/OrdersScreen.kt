package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.OrderEntity
import com.example.ui.theme.*
import com.example.ui.viewmodel.SmmViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersScreen(
    viewModel: SmmViewModel,
    onNavigateToNewOrder: () -> Unit
) {
    val orders by viewModel.orders.collectAsState()
    var selectedStatusFilter by remember { mutableStateOf("All") }
    val clipboardManager = LocalClipboardManager.current

    val filteredOrders = if (selectedStatusFilter == "All") {
        orders
    } else {
        orders.filter { it.status.equals(selectedStatusFilter, ignoreCase = true) }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(SmmDarkBackground)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 110.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Column {
                Text(
                    text = "অর্ডার হিস্টোরি",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextPrimary
                )
                Text(
                    text = "আপনার সকল সোশ্যাল মিডিয়া ক্যাম্পেইনের লাইভ স্ট্যাটাস",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }
        }

        // Status Tabs
        item {
            val statusList = listOf("All", "In Progress", "Completed", "Pending", "Canceled")
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(statusList) { status ->
                    FilterChip(
                        selected = selectedStatusFilter == status,
                        onClick = { selectedStatusFilter = status },
                        label = {
                            val count = if (status == "All") orders.size else orders.count { it.status.equals(status, ignoreCase = true) }
                            Text("$status ($count)", fontSize = 12.sp)
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = SmmPrimary,
                            selectedLabelColor = Color.White,
                            containerColor = SmmDarkCard,
                            labelColor = TextSecondary
                        )
                    )
                }
            }
        }

        if (filteredOrders.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = SmmDarkCard),
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SmmDarkCardBorder)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.ReceiptLong,
                            contentDescription = null,
                            tint = TextMuted,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "কোন অর্ডার রেকর্ড নেই",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "আপনার পছন্দের সোশ্যাল সার্ভিসের জন্য এখনই নতুন অর্ডার তৈরি করুন",
                            fontSize = 12.sp,
                            color = TextSecondary,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Button(
                            onClick = onNavigateToNewOrder,
                            colors = ButtonDefaults.buttonColors(containerColor = SmmPrimary),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(imageVector = Icons.Default.AddShoppingCart, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("নতুন অর্ডার দিন", fontSize = 13.sp)
                        }
                    }
                }
            }
        } else {
            items(filteredOrders) { order ->
                OrderCard(
                    order = order,
                    onSync = { viewModel.syncOrder(order) },
                    onCopyLink = {
                        clipboardManager.setText(AnnotatedString(order.link))
                    }
                )
            }
        }
    }
}

@Composable
fun OrderCard(
    order: OrderEntity,
    onSync: () -> Unit,
    onCopyLink: () -> Unit
) {
    val dateStr = remember(order.timestamp) {
        val sdf = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
        sdf.format(Date(order.timestamp))
    }

    val statusColor = when (order.status.lowercase()) {
        "completed" -> SmmGreen
        "in progress", "processing" -> SmmSecondary
        "pending" -> SmmGold
        else -> SmmRed
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SmmDarkCard),
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, SmmDarkCardBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "অর্ডার #${order.id}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextPrimary
                    )
                    if (order.remoteOrderId > 0) {
                        Text(
                            text = " (API: #${order.remoteOrderId})",
                            fontSize = 10.sp,
                            color = TextMuted
                        )
                    }
                }

                Surface(
                    color = statusColor.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = order.status,
                        color = statusColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = order.serviceName,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Link row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF0C1322))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = order.link,
                    fontSize = 11.sp,
                    color = SmmSecondary,
                    maxLines = 1,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onCopyLink, modifier = Modifier.size(20.dp)) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy",
                        tint = TextSecondary,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "পরিমাণ: ${order.quantity}",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                    Text(
                        text = "অবশিষ্ট: ${order.remains}",
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "চার্জ: ৳${String.format("%.2f", order.chargeBdt)}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = SmmGoldLight
                    )
                    Text(
                        text = dateStr,
                        fontSize = 10.sp,
                        color = TextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                OutlinedButton(
                    onClick = onSync,
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SmmDarkCardBorder)
                ) {
                    Icon(imageVector = Icons.Default.Sync, contentDescription = null, modifier = Modifier.size(14.dp), tint = SmmSecondary)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("লাইভ স্ট্যাটাস রিফ্রেশ", fontSize = 11.sp, color = SmmSecondary)
                }
            }
        }
    }
}
