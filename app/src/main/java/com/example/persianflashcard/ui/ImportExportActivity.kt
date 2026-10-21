package com.example.persianflashcard.ui

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.OpenDocument
import androidx.activity.result.contract.ActivityResultContracts.CreateDocument
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.persianflashcard.R
import com.example.persianflashcard.data.entity.Card
import com.example.persianflashcard.import_export.CsvImporter
import com.example.persianflashcard.import_export.DeckExporter
import com.example.persianflashcard.import_export.TextImporter
import com.example.persianflashcard.databinding.ActivityImportExportBinding
import com.example.persianflashcard.ui.viewmodel.CardViewModel
import com.example.persianflashcard.ui.viewmodel.DeckViewModel
import java.io.File

class ImportExportActivity : AppCompatActivity() {
    private lateinit var binding: ActivityImportExportBinding
    private lateinit var deckViewModel: DeckViewModel
    private lateinit var cardViewModel: CardViewModel
    private var deckId: Int = -1

    private val pickFileLauncher = registerForActivityResult(OpenDocument()) { uri ->
        uri?.let { importFromUri(it) }
    }
    private val saveFileLauncher = registerForActivityResult(CreateDocument("text/csv")) { uri ->
        uri?.let { exportCsvToUri(it) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImportExportBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        deckId = intent.getIntExtra("deckId", -1)
        deckViewModel = ViewModelProvider(this)[DeckViewModel::class.java]
        cardViewModel = ViewModelProvider(this)[CardViewModel::class.java]

        binding.btnImportCsv.setOnClickListener { pickFileLauncher.launch(null) }
        binding.btnImportText.setOnClickListener { showImportTextDialog() }
        binding.btnDoImportText.setOnClickListener {
            val text = binding.etPasteText.text.toString()
            val sep = binding.etSeparator.text.toString()
            val result = TextImporter().importText(text, sep)
            if (result.success && result.cards.isNotEmpty()) {
                cardViewModel.insertAll(result.cards.map { c ->
                    Card(deckId = deckId, front = c.front, back = c.back, hint = c.hint)
                })
                Toast.makeText(this, resources.getString(R.string.import_success, result.cards.size), Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, result.errors.joinToString("\n"), Toast.LENGTH_LONG).show()
            }
        }
        binding.btnExportCsv.setOnClickListener { saveFileLauncher.launch(null) }
        binding.btnExportJson.setOnClickListener { exportJson() }
        binding.btnMerge.setOnClickListener { mergeDecks() }
    }

    private fun showImportTextDialog() {
        binding.etPasteText.visibility = View.VISIBLE
        binding.etSeparator.visibility = View.VISIBLE
        binding.btnDoImportText.visibility = View.VISIBLE
    }
    private fun importFromUri(uri: android.net.Uri) {
        try {
            val input = contentResolver.openInputStream(uri) ?: return
            val result = CsvImporter().importStream(input)
            input.close()
            if (result.success && result.cards.isNotEmpty()) {
                cardViewModel.insertAll(result.cards.map { c ->
                    Card(deckId = deckId, front = c.front, back = c.back, hint = c.hint)
                })
                Toast.makeText(this, resources.getString(R.string.import_success, result.cards.size), Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, result.errors.joinToString("\n"), Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, resources.getString(R.string.import_error, e.message), Toast.LENGTH_LONG).show()
        }
    }

    private fun exportCsvToUri(uri: android.net.Uri) {
        try {
            cardViewModel.getCardsByDeck(deckId).observe(this) { cards ->
                val cardList = cards.orEmpty()
                val exporter = DeckExporter()
                val file = File(cacheDir, "export.csv")
                exporter.saveCsv(file, cardList)
                contentResolver.openOutputStream(uri)?.use { out ->
                    java.io.FileInputStream(file).use { in -> in.copyTo(out) }
                }
                Toast.makeText(this, resources.getString(R.string.export_success, uri.path), Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, resources.getString(R.string.export_error, e.message), Toast.LENGTH_LONG).show()
        }
    }

    private fun exportJson() {
        try {
            cardViewModel.getCardsByDeck(deckId).observe(this) { cards ->
                val cardList = cards.orEmpty()
                val deckName = if (deckId != -1) "deck_$deckId" else "deck"
                val exporter = DeckExporter()
                val file = File(cacheDir, "${deckName}_${System.currentTimeMillis()}.json")
                exporter.saveJson(file, com.example.persianflashcard.data.entity.Deck(id = deckId, name = deckName), cardList)
                Toast.makeText(this, resources.getString(R.string.export_success, file.absolutePath), Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, resources.getString(R.string.export_error, e.message), Toast.LENGTH_LONG).show()
        }
    }

    private fun mergeDecks() {
        Toast.makeText(this, "ادغام مجموعه‌ها در حال آماده‌سازی", Toast.LENGTH_SHORT).show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) { finish(); return true }
        return super.onOptionsItemSelected(item)
    }
}
    private fun importFromUri(uri: android.net.Uri) {
        try {
            val input = contentResolver.openInputStream(uri) ?: return
            val result = CsvImporter().importStream(input)
            input.close()
            if (result.success && result.cards.isNotEmpty()) {
                cardViewModel.insertAll(result.cards.map { c ->
                    Card(deckId = deckId, front = c.front, back = c.back, hint = c.hint)
                })
                Toast.makeText(this, resources.getString(R.string.import_success, result.cards.size), Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, result.errors.joinToString("\n"), Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, resources.getString(R.string.import_error, e.message), Toast.LENGTH_LONG).show()
        }
    }

    private fun exportCsvToUri(uri: android.net.Uri) {
        try {
            cardViewModel.getCardsByDeck(deckId).observe(this) { cards ->
                val cardList = cards.orEmpty()
                val exporter = DeckExporter()
                val file = File(cacheDir, "export.csv")
                exporter.saveCsv(file, cardList)
                contentResolver.openOutputStream(uri)?.use { out ->
                    java.io.FileInputStream(file).use { in ->
                        in.copyTo(out)
                    }
                }
                Toast.makeText(this, resources.getString(R.string.export_success, uri.path), Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, resources.getString(R.string.export_error, e.message), Toast.LENGTH_LONG).show()
        }
    }

    private fun exportJson() {
        try {
            cardViewModel.getCardsByDeck(deckId).observe(this) { cards ->
                val cardList = cards.orEmpty()
                val exporter = DeckExporter()
                val file = File(cacheDir, "deck_${deckId}_${System.currentTimeMillis()}.json")
                exporter.saveJson(file, com.example.persianflashcard.data.entity.Deck(id = deckId, name = "deck"), cardList)
                Toast.makeText(this, resources.getString(R.string.export_success, file.absolutePath), Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, resources.getString(R.string.export_error, e.message), Toast.LENGTH_LONG).show()
        }
    }

    private fun mergeDecks() {
        Toast.makeText(this, "ادغام مجموعه‌ها در حال آماده‌سازی", Toast.LENGTH_SHORT).show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) { finish(); return true }
        return super.onOptionsItemSelected(item)
    }
}
