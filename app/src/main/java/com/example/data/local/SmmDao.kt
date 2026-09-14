package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SmmDao {
    @Query("SELECT * FROM orders ORDER BY timestamp DESC")
    fun getAllOrders(): Flow<List<OrderEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: OrderEntity): Long

    @Update
    suspend fun updateOrder(order: OrderEntity)

    @Query("UPDATE orders SET status = :status, remains = :remains, startCount = :startCount WHERE id = :id")
    suspend fun updateOrderStatus(id: Long, status: String, remains: String, startCount: String)

    @Query("SELECT * FROM transactions ORDER BY timestamp DESC")
    fun getAllTransactions(): Flow<List<TransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity): Long

    @Query("SELECT * FROM user_profile WHERE userId = :id LIMIT 1")
    fun getUserProfileFlow(id: String = "goriber_user_1"): Flow<UserProfileEntity?>

    @Query("SELECT * FROM user_profile WHERE userId = :id LIMIT 1")
    suspend fun getUserProfile(id: String = "goriber_user_1"): UserProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserProfile(profile: UserProfileEntity)

    @Query("UPDATE user_profile SET balanceBdt = :newBalance WHERE userId = :userId")
    suspend fun updateBalance(userId: String = "goriber_user_1", newBalance: Double)
}
