package com.example.persianflashcard.util

import com.example.persianflashcard.model.StudyMode

data class SessionState(
    val deckId: Int = -1,
    val mode: StudyMode = StudyMode.CARD,
    val cardIds: List<Int> = emptyList(),
    val currentIndex: Int = 0,
    val boxAssignments: Map<Int, Int> = emptyMap(), // cardId -> boxLevel
    val knownCards: Set<Int> = emptySet(), // for Memorize mode
    val correctCount: Int = 0,
    val wrongCount: Int = 0,
    val studiedCount: Int = 0,
    val starredOnly: Boolean = false,
    val shuffled: Boolean = false,
    val startedAt: Long = System.currentTimeMillis()
)