package com.example.persianflashcard.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.persianflashcard.data.entity.Deck
import com.example.persianflashcard.data.entity.DeckWithCards

@Dao
interface DeckDao {
    @Query("SELECT * FROM decks ORDER BY name COLLATE NOCASE ASC")
    fun getAllDecks(): LiveData<List<Deck>>

    @Query("SELECT * FROM decks WHERE id = :id")
    fun getDeckById(id: Int): LiveData<Deck?>

    @Query("SELECT * FROM decks WHERE name LIKE %:query% ORDER BY name COLLATE NOCASE ASC")
    fun searchDecks(query: String): LiveData<List<Deck>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(deck: Deck): Long

    @Update
    suspend fun update(deck: Deck)

    @Delete
    suspend fun delete(deck: Deck)

    @Transaction
    @Query("SELECT * FROM decks WHERE id = :id")
    fun getDeckWithCards(id: Int): LiveData<DeckWithCards>

    @Query("DELETE FROM decks")
    suspend fun deleteAll()
}