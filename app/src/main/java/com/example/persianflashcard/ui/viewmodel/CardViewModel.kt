package com.example.persianflashcard.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.persianflashcard.data.entity.Card
import com.example.persianflashcard.data.repository.CardRepository
import kotlinx.coroutines.launch

class CardViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = CardRepository.get(application)

    fun getCardsByDeck(deckId: Int): LiveData<List<Card>> = repository.getCardsByDeck(deckId)
    fun getStarredCardsByDeck(deckId: Int): LiveData<List<Card>> = repository.getStarredCardsByDeck(deckId)
    fun getCardById(id: Int): LiveData<Card?> = repository.getCardById(id)
    fun searchCards(deckId: Int, query: String): LiveData<List<Card>> = repository.searchCards(deckId, query)

    fun insert(card: Card) = viewModelScope.launch { repository.insert(card) }
    fun insertAll(cards: List<Card>) = viewModelScope.launch { repository.insertAll(cards) }
    fun update(card: Card) = viewModelScope.launch { repository.update(card) }
    fun updateAll(cards: List<Card>) = viewModelScope.launch { repository.updateAll(cards) }
    fun delete(card: Card) = viewModelScope.launch { repository.delete(card) }
    fun deleteAllForDeck(deckId: Int) = viewModelScope.launch { repository.deleteAllForDeck(deckId) }

    fun toggleStar(id: Int, starred: Boolean) = viewModelScope.launch { repository.toggleStar(id, starred) }
    fun updatePosition(id: Int, position: Int) = viewModelScope.launch { repository.updatePosition(id, position) }
    fun updateBoxLevel(id: Int, boxLevel: Int) = viewModelScope.launch { repository.updateBoxLevel(id, boxLevel) }
    fun markCorrect(id: Int, ts: Long) = viewModelScope.launch { repository.markCorrect(id, ts) }
    fun markWrong(id: Int, ts: Long) = viewModelScope.launch { repository.markWrong(id, ts) }
}