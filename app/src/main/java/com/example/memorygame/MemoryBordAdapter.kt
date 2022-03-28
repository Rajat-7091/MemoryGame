package com.example.memorygame

import android.content.Context
import android.nfc.Tag
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.appcompat.view.menu.ActionMenuItemView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.min
class MemoryBordAdapter(
    private val context: Context,
    private val boardSIze: BoardSIze,
    private val cards: List<MemoryCards>,
    private val cardClickListener: CardClickListener
) :
    RecyclerView.Adapter<MemoryBordAdapter.ViewHolder>(){

    companion object{
        private const val  MARGIN_SIZE = 12
        private const val Tag = "MemoryBordAdapter"
    }

    interface CardClickListener{
        fun onCardClicked(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val cardWidth : Int = parent.width / boardSIze.getWidth() -(2 * MARGIN_SIZE)
        val cardHeight = parent.height / boardSIze.getWidth() -(2 * MARGIN_SIZE)
        val cardSideLength = min(cardWidth,cardHeight)
        val view = LayoutInflater.from(context).inflate(R.layout.memory_card, parent, false)
        val layoutParams = view.findViewById<CardView>(R.id.cardView).layoutParams as ViewGroup.MarginLayoutParams
        layoutParams.width = cardSideLength
        layoutParams.height = cardSideLength
        layoutParams.setMargins(MARGIN_SIZE, MARGIN_SIZE, MARGIN_SIZE, MARGIN_SIZE)
        return ViewHolder(view)
    }

    override fun getItemCount() = boardSIze.numCard

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)

    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val  imageButton = itemView.findViewById<ImageButton>(R.id.imageButton)
        fun bind(position: Int) {
            val memoryCard : MemoryCards = cards[position]
            imageButton.setImageResource( if (cards[position].isFaceUp) cards[position].identifier else R.drawable.ic_launcher_background)

            imageButton.alpha = if (memoryCard.isMatched) .4f else 1.0f
            val colorStateList = if (memoryCard.isMatched) ContextCompat.getColorStateList(context ,R.color.color_Gray) else null
            ViewCompat.setBackgroundTintList(imageButton, colorStateList)
            imageButton.setOnClickListener {
                Log.i(Tag, "Clicked on position $position")
                cardClickListener.onCardClicked(position)
            }
        }
    }

}
