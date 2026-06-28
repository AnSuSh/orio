package com.quickthought.orio.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import com.quickthought.orio.data.local.dao.TransactionsDAO
import com.quickthought.orio.data.local.entity.TransactionData

@Database(
    entities = [TransactionData::class],
    version = 5,
    exportSchema = true,
)
abstract class OrioDatabase : RoomDatabase() {

    abstract fun transactionsDao(): TransactionsDAO

    companion object {
        const val DATABASE_NAME = "orio_db"
    }
}

val MIGRATION_4_5 = object : Migration(4, 5) {
    override fun migrate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE transactions ADD COLUMN rawMessage TEXT")
    }
}

val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE transactions ADD COLUMN trackingMethod TEXT NOT NULL DEFAULT 'MANUAL'")
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

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE transactions ADD COLUMN isSynced INTEGER NOT NULL DEFAULT 0")
        db.execSQL("ALTER TABLE transactions ADD COLUMN remoteId TEXT")
        db.execSQL("ALTER TABLE transactions ADD COLUMN lastModified INTEGER NOT NULL DEFAULT 0")
    }
}