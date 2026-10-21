package com.example.persianflashcard.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.persianflashcard.R
import com.example.persianflashcard.data.entity.Card
import com.example.persianflashcard.data.entity.Deck
import com.example.persianflashcard.ui.adapter.CardAdapter
import com.example.persianflashcard.ui.viewmodel.CardViewModel
import com.example.persianflashcard.ui.viewmodel.DeckViewModel
import com.example.persianflashcard.databinding.ActivityDeckDetailBinding

class DeckDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDeckDetailBinding
    private lateinit var deckViewModel: DeckViewModel
    private lateinit var cardViewModel: CardViewModel
    private lateinit var adapter: CardAdapter
    private var deckId: Int = -1
    private var deckName: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDeckDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        deckId = intent.getIntExtra("deckId", -1)
        deckName = intent.getStringExtra("deckName") ?: ""
        binding.toolbar.title = deckName

        deckViewModel = ViewModelProvider(this)[DeckViewModel::class.java]
        cardViewModel = ViewModelProvider(this)[CardViewModel::class.java]

        adapter = CardAdapter()
        adapter.setOnClick { card ->
            val intent = Intent(this, CardEditorActivity::class.java).apply {
                putExtra("cardId", card.id)
                putExtra("deckId", deckId)
            }
            startActivity(intent)
        }
        adapter.setOnStarToggle { card, starred ->
            cardViewModel.toggleStar(card.id, starred)
        }

        binding.cardList.layoutManager = LinearLayoutManager(this)
        binding.cardList.adapter = adapter

        binding.fabAddCard.setOnClickListener {
            val intent = Intent(this, CardEditorActivity::class.java).apply {
                putExtra("deckId", deckId)
            }
            startActivity(intent)
        }

        binding.filterStarred.setOnCheckedChangeListener { _, isChecked ->
            updateCards(isChecked)
        }

        binding.searchCards.setEndIconOnClickListener {
            binding.searchCards.editText?.text?.clear()
        }
        binding.searchCards.editText?.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_DONE) {
                val q = binding.searchCards.editText?.text?.toString().orEmpty()
                if (q.isNotEmpty()) {
                    cardViewModel.searchCards(deckId, q).observe(this) { cards ->
                        adapter.submitList(cards.orEmpty())
                    }
                } else {
                    updateCards(binding.filterStarred.isChecked)
                }
                true
            } else false
        }

        updateCards(false)
    }

    private fun updateCards(starredOnly: Boolean) {
        val live = if (starredOnly) {
            cardViewModel.getStarredCardsByDeck(deckId)
        } else {
            cardViewModel.getCardsByDeck(deckId)
        }
        live.observe(this) { cards ->
            adapter.submitList(cards.orEmpty())
            binding.emptyText.visibility = if (cards.isNullOrEmpty()) View.VISIBLE else View.GONE
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_deck, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> { finish(); return true }
            R.id.action_edit_deck -> showEditDeckDialog()
            R.id.action_delete_deck -> confirmDeleteDeck()
            R.id.action_study -> openStudyOptions()
            R.id.action_import_export -> {
                startActivity(Intent(this, ImportExportActivity::class.java).apply {
                    putExtra("deckId", deckId)
                })
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
    private fun openStudyOptions() {
        val modes = arrayOf(
            resources.getString(R.string.mode_card),
            resources.getString(R.string.mode_memorize),
            resources.getString(R.string.mode_cram),
            resources.getString(R.string.mode_quick),
            resources.getString(R.string.mode_test)
        )
        AlertDialog.Builder(this)
            .setTitle(resources.getString(R.string.choose_mode))
            .setItems(modes) { _, which ->
                val mode = when (which) {
                    1 -> "MEMORIZE"
                    2 -> "CRAM"
                    3 -> "QUICK_REVIEW"
                    4 -> "TEST"
                    else -> "CARD"
                }
                val intent = Intent(this, StudyActivity::class.java).apply {
                    putExtra("deckId", deckId)
                    putExtra("mode", mode)
                }
                startActivity(intent)
            }
            .setNegativeButton(resources.getString(R.string.cancel), null)
            .show()
    }

    private fun showEditDeckDialog() {
        val input = android.widget.EditText(this)
        input.setText(deckName)
        input.isLayoutDirection = android.view.View.LAYOUT_DIRECTION_RTL
        input.textDirection = android.view.View.TEXT_DIRECTION_RTL
        AlertDialog.Builder(this)
            .setTitle(resources.getString(R.string.edit_deck))
            .setView(input)
            .setPositiveButton(resources.getString(R.string.ok)) { _, _ ->
                val name = input.text.toString().trim()
                if (name.isNotEmpty()) {
                    val deck = Deck(id = deckId, name = name)
                    deckViewModel.update(deck)
                    deckName = name
                    binding.toolbar.title = name
                }
            }
            .setNegativeButton(resources.getString(R.string.cancel), null)
            .show()
    }

    private fun confirmDeleteDeck() {
        AlertDialog.Builder(this)
            .setTitle(resources.getString(R.string.delete_deck))
            .setMessage(resources.getString(R.string.confirm_delete_deck))
            .setPositiveButton(resources.getString(R.string.yes)) { _, _ ->
                cardViewModel.deleteAllForDeck(deckId)
                val deck = Deck(id = deckId, name = deckName)
                deckViewModel.delete(deck)
                finish()
            }
            .setNegativeButton(resources.getString(R.string.cancel), null)
            .show()
    }
}