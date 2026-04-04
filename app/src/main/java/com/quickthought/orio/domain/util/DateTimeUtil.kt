package com.quickthought.orio.domain.util

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

fun Long.toDateString(): String {
    val instant = Instant.ofEpochMilli(this)
    val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy")
        .withZone(ZoneId.systemDefault())
    return formatter.format(instant)
}