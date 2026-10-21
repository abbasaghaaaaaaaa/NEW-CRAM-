package com.example.persianflashcard.util

import com.example.persianflashcard.data.entity.Card
import com.example.persianflashcard.model.BoxLevels

object CramEngine {
    val MAX_BOX = BoxLevels.MAX

    fun initialBoxes(cardIds: List<Int>): Map<Int, Int> {
        return cardIds.associateWith { 1 }
    }

    fun nextBox(correct: Boolean, current: Int): Int {
        return if (correct) {
            (current + 1).coerceAtMost(MAX_BOX)
        } else {
            1
        }
    }

    fun lowestNonEmptyBox(assignments: Map<Int, Int>, remainingIds: Set<Int>): Int? {
        for (level in BoxLevels.ALL) {
            if (remainingIds.any { assignments[it] == level }) {
                return level
            }
        }
        return null
    }

    fun cardsInBox(assignments: Map<Int, Int>, remainingIds: Set<Int>, level: Int): List<Int> {
        return remainingIds.filter { assignments[it] == level }
    }

    fun isComplete(assignments: Map<Int, Int>, remainingIds: Set<Int>): Boolean {
        return remainingIds.all { assignments[it] == MAX_BOX }
    }
}