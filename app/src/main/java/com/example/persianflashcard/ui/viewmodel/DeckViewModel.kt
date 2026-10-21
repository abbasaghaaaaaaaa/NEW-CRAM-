package com.example.persianflashcard.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.persianflashcard.data.entity.Deck
import com.example.persianflashcard.data.entity.DeckWithCards
import com.example.persianflashcard.data.repository.DeckRepository
import kotlinx.coroutines.launch

class DeckViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = DeckRepository.get(application)
    val allDecks: LiveData<List<Deck>> = repository.getAllDecks()

    fun searchDecks(query: String): LiveData<List<Deck>> = repository.searchDecks(query)
    fun getDeckWithCards(id: Int): LiveData<DeckWithCards> = repository.getDeckWithCards(id)

    fun insert(deck: Deck) = viewModelScope.launch { repository.insert(deck) }
    fun update(deck: Deck) = viewModelScope.launch { repository.update(deck) }
    fun delete(deck: Deck) = viewModelScope.launch { repository.delete(deck) }
}