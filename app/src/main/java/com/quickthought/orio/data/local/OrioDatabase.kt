package com.quickthought.orio.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.quickthought.orio.data.local.entity.TransactionData

@Database(
    entities = [TransactionData::class],
    version = 1,
    exportSchema = true,
)
abstract class OrioDatabase: RoomDatabase() {

    abstract fun transactionsDao(): TransactionsDAO

    companion object {
        const val DATABASE_NAME = "orio_db"
    }
}