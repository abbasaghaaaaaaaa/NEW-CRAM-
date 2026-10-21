package com.example.persianflashcard.import_export

data class CardInput(
    val front: String,
    val back: String,
    val hint: String? = null
)

data class ImportResult(
    val cards: List<CardInput> = emptyList(),
    val errors: List<String> = emptyList(),
    val success: Boolean get() = errors.isEmpty()
)