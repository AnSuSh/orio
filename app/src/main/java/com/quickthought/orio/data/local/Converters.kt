package com.quickthought.orio.data.local

import androidx.room.TypeConverter
import com.quickthought.orio.domain.model.AccountType

class Converters {
    @TypeConverter
    fun fromAccountType(value: AccountType): String {
        return value.name
    }

    @TypeConverter
    fun toAccountType(value: String): AccountType {
        return AccountType.valueOf(value)
    }
}
