package com.example.persianflashcard.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.persianflashcard.data.entity.Deck
import com.example.persianflashcard.databinding.ItemDeckBinding

class DeckAdapter : ListAdapter<Deck, DeckAdapter.DeckViewHolder>(DIFF_CALLBACK) {
    private var onClick: ((Deck) -> Unit)? = null
    private var onDelete: ((Deck) -> Unit)? = null

    class DeckViewHolder(val binding: ItemDeckBinding) :
        androidx.recyclerview.widget.RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeckViewHolder {
        val binding = ItemDeckBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DeckViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DeckViewHolder, position: Int) {
        val deck = getItem(position)
        holder.binding.deckName.text = deck.name
        holder.binding.deckCount.text = "${deck.cardsCount()}" // placeholder
        holder.binding.deckColorDot.setBackgroundColor(deck.color)
        holder.binding.root.setOnClickListener { onClick?.invoke(deck) }
        holder.binding.root.setOnLongClickListener { onDelete?.invoke(deck); true }
    }

    fun setOnClick(listener: (Deck) -> Unit) { onClick = listener }
    fun setOnDelete(listener: (Deck) -> Unit) { onDelete = listener }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Deck>() {
            override fun areItemsTheSame(oldItem: Deck, newItem: Deck): Boolean = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: Deck, newItem: Deck): Boolean = oldItem == newItem
        }
    }
}

// Temporary helper extension to avoid extra data model changes
fun Deck.cardsCount(): Int = 0