package com.example.persianflashcard.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.persianflashcard.data.entity.Card
import com.example.persianflashcard.databinding.ItemCardBinding

class CardAdapter : ListAdapter<Card, CardAdapter.CardViewHolder>(DIFF_CALLBACK) {
    private var onClick: ((Card) -> Unit)? = null
    private var onStarToggle: ((Card, Boolean) -> Unit)? = null

    class CardViewHolder(val binding: ItemCardBinding) :
        androidx.recyclerview.widget.RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val binding = ItemCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CardViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val card = getItem(position)
        holder.binding.cardFront.text = card.front
        holder.binding.cardBackPreview.text = card.back
        holder.binding.cardStar.setColorFilter(
            if (card.starred) android.graphics.Color.parseColor("#FFC107") else android.graphics.Color.parseColor("#9E9E9E")
        )
        holder.binding.cardBoxDot.setBackgroundColor(boxColor(card.boxLevel))
        holder.binding.cardStar.setOnClickListener {
            onStarToggle?.invoke(card, !card.starred)
        }
        holder.binding.root.setOnClickListener { onClick?.invoke(card) }
    }

    fun setOnClick(listener: (Card) -> Unit) { onClick = listener }
    fun setOnStarToggle(listener: (Card, Boolean) -> Unit) { onStarToggle = listener }

    private fun boxColor(level: Int): Int = when (level) {
        1 -> 0xFFE3F2FD
        2 -> 0xFFBBDEFB
        3 -> 0xFFFFF9C4
        4 -> 0xFFFFCC80
        5 -> 0xFFC8E6C9
        else -> 0xFFE0E0E0
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Card>() {
            override fun areItemsTheSame(oldItem: Card, newItem: Card): Boolean = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: Card, newItem: Card): Boolean = oldItem == newItem
        }
    }
}