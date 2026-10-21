package com.example.persianflashcard.import_export

import com.example.persianflashcard.data.entity.Card
import com.example.persianflashcard.data.entity.Deck
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.io.File
import java.io.FileWriter

data class DeckExport(
    val deckName: String,
    val exportedAt: Long,
    val cards: List<CardExport>
)

data class CardExport(
    val front: String,
    val back: String,
    val hint: String?,
    val boxLevel: Int,
    val starred: Boolean,
    val position: Int
)

class DeckExporter {
    private val gson = GsonBuilder().setPrettyPrinting().create()

    fun toJson(deck: Deck, cards: List<Card>): String {
        val export = DeckExport(
            deck.name,
            System.currentTimeMillis(),
            cards.map { CardExport(it.front, it.back, it.hint, it.boxLevel, it.starred, it.position) }
        )
        return gson.toJson(export)
    }

    fun toCsv(cards: List<Card>): StringBuilder {
        val sb = StringBuilder()
        sb.append("Front,Back,Hint\n")
        for (card in cards) {
            sb.append(escapeCsv(card.front)).append(",")
            sb.append(escapeCsv(card.back)).append(",")
            sb.append(escapeCsv(card.hint.orEmpty())).append("\n")
        }
        return sb
    }

    fun saveJson(file: File, deck: Deck, cards: List<Card>) {
        FileWriter(file).use { it.write(toJson(deck, cards)) }
    }

    fun saveCsv(file: File, cards: List<Card>) {
        FileWriter(file).use { it.write(toCsv(cards).toString()) }
    }

    private fun escapeCsv(value: String): String {
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\""
        }
        return value
    }
}