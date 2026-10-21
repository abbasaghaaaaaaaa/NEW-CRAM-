package com.example.persianflashcard.ui

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.persianflashcard.R
import com.example.persianflashcard.data.entity.Card
import com.example.persianflashcard.databinding.ActivityStudyBinding
import com.example.persianflashcard.model.StudyMode
import com.example.persianflashcard.ui.viewmodel.StudyViewModel

class StudyActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStudyBinding
    private lateinit var viewModel: StudyViewModel

    private var deckId: Int = -1
    private var mode: StudyMode = StudyMode.CARD
    private var cards: List<Card> = emptyList()

    private var isFlipped: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStudyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        viewModel = ViewModelProvider(this)[StudyViewModel::class.java]

        deckId = intent.getIntExtra("deckId", -1)
        val modeName = intent.getStringExtra("mode") ?: "CARD"
        mode = StudyMode.valueOf(modeName)

        binding.toolbar.title = when (mode) {
            StudyMode.CARD -> resources.getString(R.string.mode_card)
            StudyMode.MEMORIZE -> resources.getString(R.string.mode_memorize)
            StudyMode.CRAM -> resources.getString(R.string.mode_cram)
            StudyMode.QUICK_REVIEW -> resources.getString(R.string.mode_quick)
            StudyMode.TEST -> resources.getString(R.string.mode_test)
        }

        observeViewModel()
        setupButtons()
    }

    private fun observeViewModel() {
        viewModel.currentCard.observe(this) { card ->
            if (card == null) {
                showSessionComplete()
                return
            }
            binding.tvCardFront.text = card.front
            binding.tvCardBack.text = card.back
            binding.tvCardHint.text = card.hint
            binding.tvCardHint.visibility = View.GONE
            binding.tvCardFront.visibility = View.VISIBLE
            binding.tvCardBack.visibility = View.GONE
            isFlipped = false

            val level = viewModel.boxLevelFor(card.id)
            binding.tvProgress.text = resources.getString(R.string.cram_current_box, level)
        }

        viewModel.progress.observe(this) { binding.tvProgress.text = it }
        viewModel.sessionComplete.observe(this) { complete ->
            if (complete) showSessionComplete()
        }
        viewModel.boxCounts.observe(this) { counts ->
            updateBoxIndicators(counts)
        }
    }
    private fun showSessionComplete() {
        binding.cardView.visibility = View.GONE
        binding.actionButtons.visibility = View.GONE
        binding.answerButtons.visibility = View.GONE
        binding.tvShortcuts.visibility = View.GONE
        val dialog = androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle(resources.getString(R.string.cram_all_mastered))
            .setMessage(resources.getString(R.string.memorize_complete))
            .setPositiveButton(resources.getString(R.string.close)) { _, _ -> finish() }
            .setCancelable(false)
            .create()
        dialog.show()
    }

    private fun setupButtons() {
        binding.btnFlip.setOnClickListener { flipCard() }
        binding.btnNext.setOnClickListener { nextCard() }
        binding.btnPrev.setOnClickListener { previousCard() }
        binding.btnCorrect.setOnClickListener { onCorrect() }
        binding.btnWrong.setOnClickListener { onWrong() }

        updateButtonVisibility(mode)
    }

    private fun updateButtonVisibility(mode: StudyMode) {
        val isCramOrMemorize = mode == StudyMode.CRAM || mode == StudyMode.MEMORIZE
        binding.answerButtons.visibility = if (isCramOrMemorize) View.VISIBLE else View.GONE
        binding.actionButtons.visibility = if (isCramOrMemorize) View.GONE else View.VISIBLE
        binding.tvShortcuts.visibility = if (mode == StudyMode.CRAM) View.VISIBLE else View.GONE
    }

    private fun flipCard() {
        if (isFlipped) {
            binding.tvCardFront.visibility = View.VISIBLE
            binding.tvCardBack.visibility = View.GONE
        } else {
            binding.tvCardFront.visibility = View.GONE
            binding.tvCardBack.visibility = View.VISIBLE
        }
        isFlipped = !isFlipped
    }

    private fun nextCard() {
        viewModel.nextCard()
    }

    private fun previousCard() {
        viewModel.previousCard()
    }

    private fun onCorrect() {
        viewModel.onCorrect()
    }

    private fun onWrong() {
        viewModel.onWrong()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (mode != StudyMode.CRAM) return super.onKeyDown(keyCode, event)
        when (keyCode) {
            KeyEvent.KEYCODE_SPACE -> {
                flipCard()
                return true
            }
            KeyEvent.KEYCODE_R -> {
                onCorrect()
                return true
            }
            KeyEvent.KEYCODE_W -> {
                onWrong()
                return true
            }
            KeyEvent.KEYCODE_H -> {
                val hint = viewModel.getHint()
                if (hint != null) {
                    binding.tvCardHint.text = hint
                    binding.tvCardHint.visibility = View.VISIBLE
                }
                return true
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            viewModel.saveSession()
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        viewModel.saveSession()
        super.onBackPressed()
    }

    private fun updateBoxIndicators(counts: Map<Int, Int>) {
        val container = binding.boxIndicators
        container.removeAllViews()
        for (level in 1..5) {
            val count = counts[level] ?: 0
            val tv = TextView(this)
            tv.text = "ب $level: $count"
            tv.textSize = 12f
            tv.setPadding(6, 2, 6, 2)
            tv.setBackgroundColor(boxColor(level))
            tv.setTextColor(if (level == currentBoxLevel(counts)) android.graphics.Color.WHITE else android.graphics.Color.BLACK)
            container.addView(tv)
        }
    }

    private fun currentBoxLevel(counts: Map<Int, Int>): Int {
        for (level in 1..5) {
            if ((counts[level] ?: 0) > 0) return level
        }
        return 1
    }

    private fun boxColor(level: Int): Int = when (level) {
        1 -> ContextCompat.getColor(this, R.color.color_box1)
        2 -> ContextCompat.getColor(this, R.color.color_box2)
        3 -> ContextCompat.getColor(this, R.color.color_box3)
        4 -> ContextCompat.getColor(this, R.color.color_box4)
        5 -> ContextCompat.getColor(this, R.color.color_box5)
        else -> ContextCompat.getColor(this, R.color.color_divider)
    }
}
