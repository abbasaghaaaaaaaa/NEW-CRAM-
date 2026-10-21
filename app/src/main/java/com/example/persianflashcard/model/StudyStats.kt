package com.example.persianflashcard.model

data class StudyStats(
    val totalCards: Int = 0,
    val masteredCards: Int = 0,
    val correctAnswers: Int = 0,
    val wrongAnswers: Int = 0,
    val lastStudied: Long = 0,
    val boxCounts: Map<Int, Int> = emptyMap()
) {
    val masteryPercent: Int
        get() = if (totalCards == 0) 0 else (masteredCards * 100 / totalCards)
    val accuracyPercent: Int
        get() = {
            val total = correctAnswers + wrongAnswers
            if (total == 0) 0 else (correctAnswers * 100 / total)
        }
}