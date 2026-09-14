package com.example.data.model

data class SmmService(
    val serviceId: Int,
    val name: String,
    val category: String,
    val platform: SmmPlatform,
    val rateUsd: Double,
    val rateBdt: Double,
    val min: Int,
    val max: Int,
    val description: String = "",
    val averageTime: String = "10-60 mins",
    val refill: Boolean = true
)

enum class SmmPlatform(val displayName: String, val badgeColor: Long) {
    FACEBOOK("Facebook", 0xFF1877F2),
    YOUTUBE("YouTube", 0xFFFF0000),
    TIKTOK("TikTok", 0xFF000000),
    INSTAGRAM("Instagram", 0xFFE1306C),
    TELEGRAM("Telegram", 0xFF229ED9),
    TWITTER("Twitter/X", 0xFF1DA1F2),
    OTHER("Other", 0xFF6366F1)
}

enum class OrderStatus(val label: String, val colorHex: Long) {
    PENDING("Pending", 0xFFF59E0B),
    IN_PROGRESS("In Progress", 0xFF3B82F6),
    PROCESSING("Processing", 0xFF8B5CF6),
    COMPLETED("Completed", 0xFF10B981),
    PARTIAL("Partial", 0xFFEC4899),
    CANCELED("Canceled", 0xFFEF4444)
}

data class MotherPanelServiceResponse(
    val service: Int,
    val name: String,
    val type: String?,
    val category: String?,
    val rate: String?,
    val min: String?,
    val max: String?,
    val desc: String?
)

data class MotherPanelAddOrderResponse(
    val order: Long?,
    val error: String?
)

data class MotherPanelStatusResponse(
    val charge: String?,
    val start_count: String?,
    val status: String?,
    val remains: String?,
    val currency: String?,
    val error: String?
)

data class MotherPanelBalanceResponse(
    val balance: String?,
    val currency: String?,
    val error: String?
)

data class UddoktaPayCreateResponse(
    val status: Boolean?,
    val message: String?,
    val payment_url: String?
)
