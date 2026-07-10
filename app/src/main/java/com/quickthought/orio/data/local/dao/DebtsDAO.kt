package com.quickthought.orio.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.quickthought.orio.data.local.entity.DebtData
import kotlinx.coroutines.flow.Flow

@Dao
interface DebtsDAO {
    @Query("SELECT * FROM debts ORDER BY date DESC")
    fun getAllDebts(): Flow<List<DebtData>>

    @Query("SELECT * FROM debts WHERE transactionId = :transactionId")
    suspend fun getDebtsForTransaction(transactionId: Int): List<DebtData>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDebt(debt: DebtData): Long

    @Update
    suspend fun updateDebt(debt: DebtData)

    @Delete
    suspend fun deleteDebt(debt: DebtData)

    @Query("SELECT SUM(amount) FROM debts WHERE isPaid = 0")
    fun getTotalReceivables(): Flow<Double?>
}
