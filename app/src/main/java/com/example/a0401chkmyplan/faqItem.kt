package com.example.a0401chkmyplan

data class FaqItem(
    val question: String,
    val answer: String,
    var isExpanded: Boolean = false
)
