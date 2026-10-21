package com.example.persianflashcard.ui

import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.persianflashcard.R
import com.example.persianflashcard.databinding.ActivityStatsBinding
import com.example.persianflashcard.ui.viewmodel.DeckViewModel

class StatsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStatsBinding
    private lateinit var deckViewModel: DeckViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStatsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        deckViewModel = ViewModelProvider(this)[DeckViewModel::class.java]

        deckViewModel.allDecks.observe(this) { decks ->
            binding.tvTotalDecks.text = decks.size.toString()
            binding.tvTotalCards.text = "0"
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) { finish(); return true }
        return super.onOptionsItemSelected(item)
    }
}