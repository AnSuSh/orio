package com.quickthought.orio.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.quickthought.orio.data.local.entity.AccountData
import kotlinx.coroutines.flow.Flow

@Dao
interface AccountsDAO {
    @Query("SELECT * FROM accounts ORDER BY name ASC")
    fun getAllAccounts(): Flow<List<AccountData>>

    @Query("SELECT * FROM accounts WHERE id = :id")
    suspend fun getAccountById(id: Int): AccountData?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAccount(account: AccountData): Long

    @Update
    suspend fun updateAccount(account: AccountData)

    @Delete
    suspend fun deleteAccount(account: AccountData)

    @Query("UPDATE accounts SET balance = balance + :amount WHERE id = :accountId")
    suspend fun updateBalance(accountId: Int, amount: Double)
}
