package com.example.persianflashcard.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "cards",
    foreignKeys = [
        ForeignKey(
            entity = Deck::class,
            parentColumns = ["id"],
            childColumns = ["deckId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("deckId"), Index("box_level"), Index("starred")]
)
data class Card(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val deckId: Int,
    val front: String,
    val back: String,
    val hint: String? = null,
    @ColumnInfo(name = "box_level") val boxLevel: Int = 1,
    val starred: Boolean = false,
    val position: Int = 0,
    val lastStudied: Long = 0,
    val correctCount: Int = 0,
    val incorrectCount: Int = 0
)