package com.quickthought.orio.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import com.quickthought.orio.data.local.dao.AccountsDAO
import com.quickthought.orio.data.local.dao.CategoryDAO
import com.quickthought.orio.data.local.dao.DebtsDAO
import com.quickthought.orio.data.local.dao.TransactionsDAO
import com.quickthought.orio.data.local.entity.AccountData
import com.quickthought.orio.data.local.entity.CategoryData
import com.quickthought.orio.data.local.entity.DebtData
import com.quickthought.orio.data.local.entity.TransactionData

@Database(
    entities = [TransactionData::class, AccountData::class, DebtData::class, CategoryData::class],
    version = 8,
    exportSchema = true,
)
@TypeConverters(Converters::class)
abstract class OrioDatabase : RoomDatabase() {

    abstract fun transactionsDao(): TransactionsDAO
    abstract fun accountsDao(): AccountsDAO
    abstract fun debtsDao(): DebtsDAO
    abstract fun categoryDao(): CategoryDAO

    companion object {
        const val DATABASE_NAME = "orio_db"
    }
}

val MIGRATION_7_8 = object : Migration(7, 8) {
    override fun migrate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `categories` (
                `id` TEXT PRIMARY KEY NOT NULL, 
                `name` TEXT NOT NULL, 
                `iconName` TEXT NOT NULL, 
                `colorHex` TEXT NOT NULL
            )
            """.trimIndent()
        )
    }
}

val MIGRATION_6_7 = object : Migration(6, 7) {
    override fun migrate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `debts` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 
                `transactionId` INTEGER NOT NULL, 
                `amount` REAL NOT NULL, 
                `personName` TEXT, 
                `isPaid` INTEGER NOT NULL, 
                `date` INTEGER NOT NULL
            )
            """.trimIndent()
        )
    }
}

val MIGRATION_5_6 = object : Migration(5, 6) {
    override fun migrate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `accounts` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 
                `name` TEXT NOT NULL, 
                `type` TEXT NOT NULL, 
                `balance` REAL NOT NULL, 
                `initialBalance` REAL NOT NULL, 
                `iconRes` TEXT, 
                `colorHex` TEXT, 
                `lastModified` INTEGER NOT NULL
            )
            """.trimIndent()
        )
        db.execSQL("ALTER TABLE transactions ADD COLUMN accountId INTEGER")
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