package com.example.persianflashcard.data.repository

import androidx.lifecycle.LiveData
import com.example.persianflashcard.data.Database
import com.example.persianflashcard.data.dao.CardDao
import com.example.persianflashcard.data.entity.Card
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CardRepository(private val cardDao: CardDao) {
    fun getCardsByDeck(deckId: Int): LiveData<List<Card>> = cardDao.getCardsByDeck(deckId)
    fun getStarredCardsByDeck(deckId: Int): LiveData<List<Card>> = cardDao.getStarredCardsByDeck(deckId)
    fun getCardById(id: Int): LiveData<Card?> = cardDao.getCardById(id)
    fun searchCards(deckId: Int, query: String): LiveData<List<Card>> = cardDao.searchCards(deckId, query)

    suspend fun insert(card: Card): Long = withContext(Dispatchers.IO) { cardDao.insert(card) }
    suspend fun insertAll(cards: List<Card>): List<Long> = withContext(Dispatchers.IO) { cardDao.insertAll(cards) }
    suspend fun update(card: Card) = withContext(Dispatchers.IO) { cardDao.update(card) }
    suspend fun updateAll(cards: List<Card>) = withContext(Dispatchers.IO) { cardDao.updateAll(cards) }
    suspend fun delete(card: Card) = withContext(Dispatchers.IO) { cardDao.delete(card) }
    suspend fun deleteAllForDeck(deckId: Int) = withContext(Dispatchers.IO) { cardDao.deleteAllForDeck(deckId) }

    suspend fun updateBoxLevel(id: Int, boxLevel: Int) = withContext(Dispatchers.IO) { cardDao.updateBoxLevel(id, boxLevel) }
    suspend fun updatePosition(id: Int, position: Int) = withContext(Dispatchers.IO) { cardDao.updatePosition(id, position) }
    suspend fun toggleStar(id: Int, starred: Boolean) = withContext(Dispatchers.IO) { cardDao.toggleStar(id, starred) }
    suspend fun markCorrect(id: Int, ts: Long) = withContext(Dispatchers.IO) { cardDao.markCorrect(id, ts) }
    suspend fun markWrong(id: Int, ts: Long) = withContext(Dispatchers.IO) { cardDao.markWrong(id, ts) }

    companion object {
        @Volatile private var INSTANCE: CardRepository? = null
        fun get(context: android.content.Context): CardRepository {
            return INSTANCE ?: synchronized(this) {
                val dao = Database.getDatabase(context).cardDao()
                val instance = CardRepository(dao)
                INSTANCE = instance
                instance
            }
        }
    }
}