package com.example.persianflashcard.ui.viewmodel

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.persianflashcard.data.entity.Card
import com.example.persianflashcard.data.repository.CardRepository
import com.example.persianflashcard.model.StudyMode
import com.example.persianflashcard.util.CramEngine
import kotlinx.coroutines.launch

class StudyViewModel(application: Application) : AndroidViewModel(application) {
    private val cardViewModel = CardViewModel(application)
    private val prefs: SharedPreferences = application.getSharedPreferences("flashcard_session", Context.MODE_PRIVATE)

    private val _mode = MutableLiveData<StudyMode>(StudyMode.CARD)
    val mode: LiveData<StudyMode> = _mode

    private val _cards = MutableLiveData<List<Card>>(emptyList())
    val cards: LiveData<List<Card>> = _cards

    private val _currentCard = MutableLiveData<Card?>(null)
    val currentCard: LiveData<Card?> = _currentCard

    private val _progress = MutableLiveData<String>("")
    val progress: LiveData<String> = _progress

    private val _boxCounts = MutableLiveData<Map<Int, Int>>(emptyMap())
    val boxCounts: LiveData<Map<Int, Int>> = _boxCounts

    private val _sessionComplete = MutableLiveData<Boolean>(false)
    val sessionComplete: LiveData<Boolean> = _sessionComplete

    var deckId: Int = -1
    var starredOnly: Boolean = false
    var shuffled: Boolean = false

    private var allCards: List<Card> = emptyList()
    private var remainingIds: MutableSet<Int> = mutableSetOf()
    private var cardMap: Map<Int, Card> = emptyMap()
    private var currentIndex: Int = 0
    private var cramAssignments: Map<Int, Int> = emptyMap()
    private var memorizeKnown: MutableSet<Int> = mutableSetOf()
    private var correctCount: Int = 0
    private var wrongCount: Int = 0
    private var studiedCount: Int = 0
    private var isFlipped: Boolean = false
    private var answerRevealed: Boolean = false

    fun startSession(deckId: Int, mode: StudyMode, starredOnly: Boolean, shuffled: Boolean, cards: List<Card>) {
        this.deckId = deckId
        this.starredOnly = starredOnly
        this.shuffled = shuffled
        this.allCards = cards.filter { if (starredOnly) it.starred else true }
        this._mode.value = mode

        val ids = allCards.map { it.id }.toMutableList()
        if (shuffled) ids.shuffle()

        this.cardMap = allCards.associateBy { it.id }
        this.remainingIds = ids.toMutableSet()
        this.currentIndex = 0
        this.correctCount = 0
        this.wrongCount = 0
        this.studiedCount = 0
        this.isFlipped = false
        this.answerRevealed = false
        this.memorizeKnown = mutableSetOf()

        when (mode) {
            StudyMode.CRAM -> {
                cramAssignments = CramEngine.initialBoxes(ids)
            }
            else -> cramAssignments = emptyMap()
        }

        saveSession()
        showCurrent()
        refreshProgress()
    }

    fun resumeSession() {
        val savedDeck = prefs.getInt("session_deck_id", -1)
        val savedMode = StudyMode.valueOf(prefs.getString("session_mode", "CARD") ?: "CARD")
        val savedStarred = prefs.getBoolean("session_starred", false)
        val savedShuffled = prefs.getBoolean("session_shuffled", false)
        val savedIndex = prefs.getInt("session_index", 0)
        val savedCorrect = prefs.getInt("session_correct", 0)
        val savedWrong = prefs.getInt("session_wrong", 0)
        val savedStudied = prefs.getInt("session_studied", 0)

        deckId = savedDeck
        this.starredOnly = savedStarred
        this.shuffled = savedShuffled
        this.currentIndex = savedIndex
        this.correctCount = savedCorrect
        this.wrongCount = savedWrong
        this.studiedCount = savedStudied
        _mode.value = savedMode
    }

    fun hasSavedSession(): Boolean {
        return prefs.getInt("session_deck_id", -1) != -1
    }

    private fun showCurrent() {
        if (remainingIds.isEmpty()) {
            _currentCard.value = null
            _sessionComplete.value = true
            refreshProgress()
            return
        }
        _sessionComplete.value = false

        var id: Int
        when (val mode = _mode.value) {
            StudyMode.CRAM -> {
                val level = CramEngine.lowestNonEmptyBox(cramAssignments, remainingIds)
                    ?: 5
                val boxCards = CramEngine.cardsInBox(cramAssignments, remainingIds, level)
                if (boxCards.isEmpty()) {
                    _currentCard.value = null
                    _sessionComplete.value = true
                    refreshProgress()
                    return
                }
                id = boxCards[currentIndex % boxCards.size]
            }
            else -> {
                id = remainingIds.elementAtOrElse(currentIndex % remainingIds.size) ?: return
            }
        }

        _currentCard.value = cardMap[id]
        isFlipped = false
        answerRevealed = false
        updateBoxIndicators()
        refreshProgress()
    }
    fun flip() {
        if (_currentCard.value != null && !answerRevealed) {
            isFlipped = true
            answerRevealed = true
        }
    }

    fun nextCard() {
        val card = _currentCard.value ?: return
        isFlipped = false
        answerRevealed = false
        remainingIds.remove(card.id)
        currentIndex++
        if (currentIndex >= remainingIds.size && _mode.value != StudyMode.CRAM) currentIndex = 0
        showCurrent()
    }

    fun previousCard() {
        if (_mode.value != StudyMode.CARD && _mode.value != StudyMode.QUICK_REVIEW) return
        currentIndex = (currentIndex - 1).coerceAtLeast(0)
        showCurrent()
    }

    fun onCorrect() {
        val card = _currentCard.value ?: return
        correctCount++
        studiedCount++
        cardViewModel.markCorrect(card.id, System.currentTimeMillis())
        when (_mode.value) {
            StudyMode.CRAM -> {
                val newBox = CramEngine.nextBox(true, cramAssignments[card.id] ?: 1)
                cramAssignments = cramAssignments + (card.id to newBox)
                cardViewModel.updateBoxLevel(card.id, newBox)
            }
            StudyMode.MEMORIZE -> memorizeKnown.add(card.id)
        }
        remainingIds.remove(card.id)
        currentIndex = 0
        showCurrent()
        refreshProgress()
    }

    fun onWrong() {
        val card = _currentCard.value ?: return
        wrongCount++
        studiedCount++
        cardViewModel.markWrong(card.id, System.currentTimeMillis())
        when (_mode.value) {
            StudyMode.CRAM -> {
                val newBox = CramEngine.nextBox(false, cramAssignments[card.id] ?: 1)
                cramAssignments = cramAssignments + (card.id to newBox)
                cardViewModel.updateBoxLevel(card.id, newBox)
            }
        }
        remainingIds.remove(card.id)
        currentIndex = 0
        showCurrent()
        refreshProgress()
    }

    fun getHint(): String? {
        return _currentCard.value?.hint
    }

    fun boxLevelFor(cardId: Int): Int {
        return cramAssignments[cardId] ?: 1
    }

    private fun updateBoxIndicators() {
        val counts = mutableMapOf<Int, Int>()
        for (level in 1..5) {
            counts[level] = remainingIds.count { cramAssignments[it] == level }
        }
        _boxCounts.value = counts
    }

    private fun refreshProgress() {
        val total = allCards.size
        val remaining = remainingIds.size
        _progress.value = "پیشرفت: $studiedCount/$total  •  بقیه: $remaining  •  درست: $correctCount  •  غلط: $wrongCount"
    }

    fun saveSession() {
        prefs.edit().putInt("session_deck_id", deckId)
            .putString("session_mode", _mode.value?.name ?: "CARD")
            .putBoolean("session_starred", starredOnly)
            .putBoolean("session_shuffled", shuffled)
            .putInt("session_index", currentIndex)
            .putInt("session_correct", correctCount)
            .putInt("session_wrong", wrongCount)
            .putInt("session_studied", studiedCount)
            .apply()
    }

    fun clearSession() {
        prefs.edit().clear().apply()
    }

    fun boxStatusText(): String {
        val sb = StringBuilder("وضعیت باکس‌ها: ")
        for (level in 1..5) {
            val c = remainingIds.count { cramAssignments[it] == level }
            sb.append("باکس $level: $c  ")
        }
        return sb.toString()
    }
}
