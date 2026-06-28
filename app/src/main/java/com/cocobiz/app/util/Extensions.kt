package com.cocobiz.app.util

import java.text.NumberFormat
import java.util.Locale

fun Double.toCurrencyString(): String {
    val format = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
    return format.format(this)
}

fun Double.toCompactCurrencyString(): String = when {
    this >= 1_00_00_000 -> "₹${"%.1f".format(this / 1_00_00_000)}Cr"
    this >= 1_00_000    -> "₹${"%.1f".format(this / 1_00_000)}L"
    this >= 1_000       -> "₹${"%.1f".format(this / 1_000)}K"
    else                -> "₹${toLong()}"
}

fun Double.toFormattedString(): String {
    return if (this == kotlin.math.floor(this)) {
        "%.0f".format(this)
    } else {
        "%.2f".format(this)
    }
}

fun String.isValidPhone(): Boolean =
    this.matches(Regex("^[6-9]\\d{9}$"))

fun String.isValidEmail(): Boolean =
    android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()

fun String.isValidPincode(): Boolean =
    this.matches(Regex("^[1-9][0-9]{5}$"))

fun String.capitalizeWords(): String =
    split(" ").joinToString(" ") { word ->
        word.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
    }
