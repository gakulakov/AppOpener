package com.example.appopenerwidget.utils

fun maxLengthString(value: String): String {
    return if(value.length >= 20) String.format("%.20s...", value).trim() else value
}