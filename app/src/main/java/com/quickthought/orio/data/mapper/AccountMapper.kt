package com.quickthought.orio.data.mapper

import com.quickthought.orio.data.local.entity.AccountData
import com.quickthought.orio.domain.model.AccountDomain

fun AccountData.toAccountDomain(): AccountDomain {
    return AccountDomain(
        id = id,
        name = name,
        type = type,
        balance = balance,
        initialBalance = initialBalance,
        iconRes = iconRes,
        colorHex = colorHex,
        lastModified = lastModified
    )
}
