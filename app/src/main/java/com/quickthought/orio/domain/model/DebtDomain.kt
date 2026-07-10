package com.quickthought.orio.domain.model

import com.quickthought.orio.data.local.entity.DebtData

data class DebtDomain(
    val id: Int = 0,
    val transactionId: Int,
    val amount: Double,
    val personName: String? = null,
    val isPaid: Boolean = false,
    val date: Long = System.currentTimeMillis()
)

fun DebtDomain.toDebtData(): DebtData {
    return DebtData(
        id = id,
        transactionId = transactionId,
        amount = amount,
        personName = personName,
        isPaid = isPaid,
        date = date
    )
}

fun DebtData.toDebtDomain(): DebtDomain {
    return DebtDomain(
        id = id,
        transactionId = transactionId,
        amount = amount,
        personName = personName,
        isPaid = isPaid,
        date = date
    )
}
