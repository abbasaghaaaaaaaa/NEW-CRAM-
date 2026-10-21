package com.example.persianflashcard.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.persianflashcard.data.entity.Card

@Dao
interface CardDao {
    @Query("SELECT * FROM cards WHERE deckId = :deckId ORDER BY position ASC, id ASC")
    fun getCardsByDeck(deckId: Int): LiveData<List<Card>>

    @Query("SELECT * FROM cards WHERE deckId = :deckId AND starred = 1 ORDER BY position ASC, id ASC")
    fun getStarredCardsByDeck(deckId: Int): LiveData<List<Card>>

    @Query("SELECT * FROM cards WHERE id = :id")
    fun getCardById(id: Int): LiveData<Card?>

    @Query("""
        SELECT * FROM cards 
        WHERE deckId = :deckId AND (front LIKE %:query% OR back LIKE %:query% OR hint LIKE %:query%)
        ORDER BY position ASC, id ASC
    """)
    fun searchCards(deckId: Int, query: String): LiveData<List<Card>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(card: Card): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(cards: List<Card>): List<Long>

    @Update
    suspend fun update(card: Card)

    @Update
    suspend fun updateAll(cards: List<Card>)

    @Delete
    suspend fun delete(card: Card)

    @Query("DELETE FROM cards WHERE deckId = :deckId")
    suspend fun deleteAllForDeck(deckId: Int)

    @Query("UPDATE cards SET boxLevel = :boxLevel WHERE id = :id")
    suspend fun updateBoxLevel(id: Int, boxLevel: Int)

    @Query("UPDATE cards SET position = :position WHERE id = :id")
    suspend fun updatePosition(id: Int, position: Int)

    @Query("UPDATE cards SET starred = :starred WHERE id = :id")
    suspend fun toggleStar(id: Int, starred: Boolean)

    @Query("UPDATE cards SET lastStudied = :ts, correctCount = correctCount + 1 WHERE id = :id")
    suspend fun markCorrect(id: Int, ts: Long)

    @Query("UPDATE cards SET lastStudied = :ts, incorrectCount = incorrectCount + 1 WHERE id = :id")
    suspend fun markWrong(id: Int, ts: Long)
}