package com.example.persianflashcard.data.repository

import androidx.lifecycle.LiveData
import com.example.persianflashcard.data.Database
import com.example.persianflashcard.data.dao.DeckDao
import com.example.persianflashcard.data.entity.Deck
import com.example.persianflashcard.data.entity.DeckWithCards
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DeckRepository(private val deckDao: DeckDao) {
    fun getAllDecks(): LiveData<List<Deck>> = deckDao.getAllDecks()
    fun getDeckById(id: Int): LiveData<Deck?> = deckDao.getDeckById(id)
    fun getDeckWithCards(id: Int): LiveData<DeckWithCards> = deckDao.getDeckWithCards(id)
    fun searchDecks(query: String): LiveData<List<Deck>> = deckDao.searchDecks(query)

    suspend fun insert(deck: Deck): Long = withContext(Dispatchers.IO) { deckDao.insert(deck) }
    suspend fun update(deck: Deck) = withContext(Dispatchers.IO) { deckDao.update(deck) }
    suspend fun delete(deck: Deck) = withContext(Dispatchers.IO) { deckDao.delete(deck) }

    companion object {
        @Volatile private var INSTANCE: DeckRepository? = null
        fun get(context: android.content.Context): DeckRepository {
            return INSTANCE ?: synchronized(this) {
                val dao = Database.getDatabase(context).deckDao()
                val instance = DeckRepository(dao)
                INSTANCE = instance
                instance
            }
        }
    }
}