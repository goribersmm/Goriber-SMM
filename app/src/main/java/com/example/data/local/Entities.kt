package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val remoteOrderId: Long = 0,
    val serviceId: Int,
    val serviceName: String,
    val category: String,
    val link: String,
    val quantity: Int,
    val chargeBdt: Double,
    val status: String, // Pending, In Progress, Completed, Canceled
    val startCount: String = "0",
    val remains: String = "0",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val trxId: String,
    val paymentMethod: String, // bKash, Nagad, Rocket, UddoktaPay
    val amountBdt: Double,
    val status: String, // Completed, Pending, Failed
    val timestamp: Long = System.currentTimeMillis(),
    val invoiceUrl: String? = null
)

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey
    val userId: String = "goriber_user_1",
    val userName: String = "Goriber SMM User",
    val email: String = "user@goribersmm.com",
    val balanceBdt: Double = 500.0,
    val totalSpentBdt: Double = 0.0,
    val totalOrders: Int = 0,
    val isVip: Boolean = true
)
