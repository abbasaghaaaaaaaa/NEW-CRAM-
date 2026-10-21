package com.example.persianflashcard.import_export

import com.example.persianflashcard.data.entity.Card
import com.example.persianflashcard.data.entity.Deck

class DeckMerger {
    fun merge(sourceDecks: List<Deck>, sourceCardsByDeck: Map<Int, List<Card>>, targetDeck: Deck): MergeResult {
        var nextPosition = sourceCardsByDeck.values.flatMap { it }.maxByOrNull { it.position }?.position ?: 0
        val mergedCards = mutableListOf<Card>()
        var imported = 0

        for (sourceDeck in sourceDecks) {
            if (sourceDeck.id == targetDeck.id) continue
            val cards = sourceCardsByDeck[sourceDeck.id] ?: emptyList()
            for (card in cards) {
                nextPosition++
                mergedCards.add(
                    Card(
                        deckId = targetDeck.id,
                        front = card.front,
                        back = card.back,
                        hint = card.hint,
                        boxLevel = card.boxLevel,
                        starred = card.starred,
                        position = nextPosition,
                        lastStudied = card.lastStudied,
                        correctCount = card.correctCount,
                        incorrectCount = card.incorrectCount
                    )
                )
                imported++
            }
        }
        return MergeResult(targetDeck, mergedCards, imported)
    }

    data class MergeResult(
        val targetDeck: Deck,
        val newCards: List<Card>,
        val importedCount: Int
    )
}