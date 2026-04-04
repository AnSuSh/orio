package com.quickthought.orio.data.mapper

import com.quickthought.orio.data.local.entity.TransactionData
import com.quickthought.orio.domain.model.TransactionDomain
import com.quickthought.orio.domain.model.TransactionType

fun TransactionData.toTransactionDomain(): TransactionDomain {
    return TransactionDomain(
        transactionId = id,
        amount = amount,
        type = TransactionType.valueOf(type),
        category = category,
        date = date,
        note = note
    )
}