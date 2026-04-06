package com.quickthought.orio.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.quickthought.orio.data.local.entity.TransactionData
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionsDAO {
    @Query("SELECT * FROM transactions ORDER BY date DESC")
    fun getAllTransactions(): Flow<List<TransactionData>>

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertTransaction(transaction: TransactionData)

    @Delete
    suspend fun deleteTransaction(transaction: TransactionData)

    @Update
    suspend fun updateTransaction(transaction: TransactionData)
}