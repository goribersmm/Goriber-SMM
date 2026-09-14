package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SmmPlatform
import com.example.data.model.SmmService
import com.example.ui.theme.*
import com.example.ui.viewmodel.SmmViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServicesScreen(
    viewModel: SmmViewModel,
    onSelectServiceAndOrder: (SmmService) -> Unit
) {
    val filteredServices by viewModel.filteredServices.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedPlatform by viewModel.selectedPlatform.collectAsState()
    val isLoading by viewModel.isLoadingServices.collectAsState()

    var activeDetailService by remember { mutableStateOf<SmmService?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(SmmDarkBackground)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 110.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "সার্ভিস তালিকা",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Goriber SMM এর সকল লাইভ সোশ্যাল সার্ভিস",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }

                IconButton(
                    onClick = { viewModel.loadServices() },
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(SmmDarkCard)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Sync",
                        tint = SmmSecondary
                    )
                }
            }
        }

        // Search bar
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.setSearchQuery(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("services_search_input"),
                placeholder = { Text("সার্ভিস নাম বা ID দিয়ে খুঁজুন...", fontSize = 13.sp, color = TextMuted) },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = SmmSecondary)
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.setSearchQuery("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear", tint = TextSecondary)
                        }
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

        // Platform Filter Chips
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    FilterChip(
                        selected = selectedPlatform == null,
                        onClick = { viewModel.setSelectedPlatform(null) },
                        label = { Text("সকল (${filteredServices.size})", fontSize = 12.sp) },
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
                        selected = selectedPlatform == platform,
                        onClick = { viewModel.setSelectedPlatform(platform) },
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

        if (isLoading) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = SmmSecondary)
                }
            }
        } else if (filteredServices.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = SmmDarkCard),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.SearchOff,
                            contentDescription = null,
                            tint = TextMuted,
                            modifier = Modifier.size(44.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "কোন সার্ভিস খুঁজে পাওয়া যায়নি",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "অন্য কোনো কিওয়ার্ড লিখে অনুসন্ধান করুন",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                }
            }
        } else {
            items(filteredServices) { service ->
                ServiceItemCard(
                    service = service,
                    onOrderClick = { onSelectServiceAndOrder(service) },
                    onDetailsClick = { activeDetailService = service }
                )
            }
        }
    }

    // Detail Dialog
    if (activeDetailService != null) {
        val s = activeDetailService!!
        AlertDialog(
            onDismissRequest = { activeDetailService = null },
            title = {
                Text(
                    text = "[#${s.serviceId}] ${s.name}",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Surface(
                        color = Color(s.platform.badgeColor).copy(alpha = 0.2f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = s.category,
                            color = Color(s.platform.badgeColor),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    Text(
                        text = "রেট: ৳${s.rateBdt} BDT / ১০০০ টি",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = SmmGoldLight
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("মিনিমাম: ${s.min}", color = TextSecondary, fontSize = 12.sp)
                        Text("ম্যাক্সিমাম: ${s.max}", color = TextSecondary, fontSize = 12.sp)
                    }

                    Divider(color = SmmDarkCardBorder)

                    Text("বিবরণ:", fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 12.sp)
                    Text(
                        text = s.description,
                        color = TextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "⚡ গড় ডেলিভারি সময়: ${s.averageTime}",
                        color = SmmGreen,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        activeDetailService = null
                        onSelectServiceAndOrder(s)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SmmPrimary)
                ) {
                    Text("অর্ডার করুন")
                }
            },
            dismissButton = {
                TextButton(onClick = { activeDetailService = null }) {
                    Text("বন্ধ করুন", color = TextSecondary)
                }
            },
            containerColor = SmmDarkSurface
        )
    }
}

@Composable
fun ServiceItemCard(
    service: SmmService,
    onOrderClick: () -> Unit,
    onDetailsClick: () -> Unit
) {
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
                Surface(
                    color = Color(service.platform.badgeColor).copy(alpha = 0.2f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = service.platform.displayName,
                        color = Color(service.platform.badgeColor),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Text(
                    text = "ID: #${service.serviceId}",
                    fontSize = 11.sp,
                    color = TextMuted
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = service.name,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = "৳${service.rateBdt}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = SmmGoldLight
                        )
                        Text(
                            text = " / ১০০০",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                    Text(
                        text = "Min: ${service.min} | Max: ${service.max}",
                        fontSize = 10.sp,
                        color = TextMuted
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = onDetailsClick,
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, SmmDarkCardBorder)
                    ) {
                        Text("তথ্য", fontSize = 11.sp, color = SmmSecondary)
                    }

                    Button(
                        onClick = onOrderClick,
                        colors = ButtonDefaults.buttonColors(containerColor = SmmPrimary),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("অর্ডার", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
