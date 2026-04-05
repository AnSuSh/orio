package com.quickthought.orio.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import com.quickthought.orio.data.local.entity.TransactionData

@Database(
    entities = [TransactionData::class],
    version = 2,
    exportSchema = true,
)
abstract class OrioDatabase : RoomDatabase() {

    abstract fun transactionsDao(): TransactionsDAO

    companion object {
        const val DATABASE_NAME = "orio_db"
    }
}

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
        // transactions Table column category was changed t categoryId.
        db.execSQL(
            """
                ALTER TABLE transactions RENAME COLUMN category TO categoryId;
            """.trimIndent()
        )
    }
}