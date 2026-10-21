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
import com.example.persianflashcard.data.entity.Deck
import com.example.persianflashcard.util.PersianUtil
import com.example.persianflashcard.util.ThemeHelper
import com.example.persianflashcard.ui.adapter.DeckAdapter
import com.example.persianflashcard.ui.viewmodel.DeckViewModel
import com.example.persianflashcard.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: DeckViewModel
    private lateinit var adapter: DeckAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeHelper.apply(this)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        viewModel = ViewModelProvider(this)[DeckViewModel::class.java]
        adapter = DeckAdapter()
        adapter.setOnClick { deck ->
            val intent = Intent(this, DeckDetailActivity::class.java).apply {
                putExtra("deckId", deck.id)
                putExtra("deckName", deck.name)
            }
            startActivity(intent)
        }
        adapter.setOnDelete { deck ->
            confirmDeleteDeck(deck)
        }

        binding.deckList.layoutManager = LinearLayoutManager(this)
        binding.deckList.adapter = adapter

        viewModel.allDecks.observe(this) { decks ->
            adapter.submitList(decks.orEmpty())
            binding.emptyText.visibility = if (decks.isNullOrEmpty()) View.VISIBLE else View.GONE
        }

        binding.fabAdd.setOnClickListener { showAddDeckDialog() }

        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.action_decks -> true
                R.id.action_study -> {
                    startActivity(Intent(this, DeckDetailActivity::class.java))
                    true
                }
                R.id.action_stats -> {
                    startActivity(Intent(this, StatsActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }

    private fun showAddDeckDialog() {
        val input = android.widget.EditText(this)
        input.hint = resources.getString(R.string.deck_name_hint)
        input.isLayoutDirection = android.view.View.LAYOUT_DIRECTION_RTL
        input.textDirection = android.view.View.TEXT_DIRECTION_RTL
        AlertDialog.Builder(this)
            .setTitle(resources.getString(R.string.create_deck))
            .setView(input)
            .setPositiveButton(resources.getString(R.string.ok)) { _, _ ->
                val name = input.text.toString().trim()
                if (name.isNotEmpty()) {
                    viewModel.insert(Deck(name = name))
                } else {
                    Toast.makeText(this, "نام را وارد کنید", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton(resources.getString(R.string.cancel), null)
            .show()
    }

    private fun confirmDeleteDeck(deck: Deck) {
        AlertDialog.Builder(this)
            .setTitle(resources.getString(R.string.delete_deck))
            .setMessage(resources.getString(R.string.confirm_delete_deck))
            .setPositiveButton(resources.getString(R.string.yes)) { _, _ ->
                viewModel.delete(deck)
            }
            .setNegativeButton(resources.getString(R.string.no), null)
            .show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_theme -> {
                toggleTheme()
                return true
            }
            R.id.action_import_export -> {
                startActivity(Intent(this, ImportExportActivity::class.java))
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun toggleTheme() {
        val current = ThemeHelper.getSavedTheme(this)
        val next = when (current) {
            ThemeHelper.THEME_LIGHT -> ThemeHelper.THEME_DARK
            ThemeHelper.THEME_DARK -> ThemeHelper.THEME_SYSTEM
            else -> ThemeHelper.THEME_LIGHT
        }
        ThemeHelper.saveTheme(this, next)
        ThemeHelper.apply(this)
        recreate()
    }
}