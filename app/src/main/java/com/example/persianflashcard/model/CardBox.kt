package com.example.persianflashcard.model

data class CardBox(
    val level: Int,
    val count: Int,
    val cards: List<Int> = emptyList()
)

object BoxLevels {
    const val MIN = 1
    const val MAX = 5
    val ALL = listOf(1, 2, 3, 4, 5)
}