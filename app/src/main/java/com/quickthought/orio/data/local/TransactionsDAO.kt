package com.quickthought.orio.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.quickthought.orio.data.local.entity.TransactionData
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionsDAO {
    @Query("SELECT * FROM transactions ORDER BY date DESC")
    fun getAllTransactions(): Flow<List<TransactionData>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionData)

    @Delete
    suspend fun deleteTransaction(transaction: TransactionData)
}