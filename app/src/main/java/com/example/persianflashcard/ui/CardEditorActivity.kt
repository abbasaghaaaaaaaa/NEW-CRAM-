package com.example.persianflashcard.ui

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.persianflashcard.R
import com.example.persianflashcard.data.entity.Card
import com.example.persianflashcard.ui.viewmodel.CardViewModel
import com.example.persianflashcard.databinding.ActivityCardEditorBinding

class CardEditorActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCardEditorBinding
    private lateinit var cardViewModel: CardViewModel
    private var deckId: Int = -1
    private var editingCardId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCardEditorBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        deckId = intent.getIntExtra("deckId", -1)
        editingCardId = intent.getIntExtra("cardId", -1)

        cardViewModel = ViewModelProvider(this)[CardViewModel::class.java]

        if (editingCardId != -1) {
            supportActionBar?.title = resources.getString(R.string.card_editor_title_edit)
            cardViewModel.getCardById(editingCardId).observe(this) { card ->
                card?.let {
                    binding.etFront.setText(it.front)
                    binding.etBack.setText(it.back)
                    binding.etHint.setText(it.hint.orEmpty())
                    binding.cbStarred.isChecked = it.starred
                }
            }
        } else {
            supportActionBar?.title = resources.getString(R.string.card_editor_title_new)
        }

        binding.btnSave.setOnClickListener { saveCard() }
        binding.btnCancel.setOnClickListener { finish() }
    }

    private fun saveCard() {
        val front = binding.etFront.text.toString().trim()
        val back = binding.etBack.text.toString().trim()
        val hint = binding.etHint.text.toString().trim().takeIf { it.isNotEmpty() }

        if (front.isEmpty() && back.isEmpty()) {
            Toast.makeText(this, "حداقل یکی از فیلدها را پر کنید", Toast.LENGTH_SHORT).show()
            return
        }

        val position = if (editingCardId != -1) 0 else System.currentTimeMillis().toInt()
        val card = Card(
            id = if (editingCardId != -1) editingCardId else 0,
            deckId = deckId,
            front = front,
            back = back,
            hint = hint,
            starred = binding.cbStarred.isChecked,
            position = position
        )

        if (editingCardId != -1) {
            cardViewModel.update(card)
        } else {
            cardViewModel.insert(card)
        }
        finish()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}