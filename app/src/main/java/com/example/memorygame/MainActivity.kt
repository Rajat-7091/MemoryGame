package com.example.memorygame

import android.animation.ArgbEvaluator
import android.icu.text.CaseMap
import android.nfc.Tag
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.memorygame.utils.DEFAULT_ICONS
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {
    companion object{
        private const val TAG = "MainActivity"
    }

    private lateinit var clRoot: ConstraintLayout
    private lateinit var rvBord: RecyclerView
    private lateinit var tvNumMoves: TextView
    private lateinit var tvNumPairs: TextView

    private lateinit var memoryGame: MemoryGame
    private lateinit var adapter: MemoryBordAdapter
    private  var boardSIze: BoardSIze = BoardSIze.HARD

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        clRoot = findViewById(R.id.clRoot)
        rvBord = findViewById(R.id.rvBord)
        tvNumMoves = findViewById(R.id.tvNumMoves)
        tvNumPairs =findViewById(R.id.tvNumPairs)


        val chosenImages : List<Int> = DEFAULT_ICONS.shuffled().take(boardSIze.getNumPair())
        val randomizedImages : List<Int> = (chosenImages + chosenImages).shuffled()
        val memoryCards: List<MemoryCards> = randomizedImages.map { MemoryCards(it) }


        setupBoard()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.mi_refresh -> {
                // setup the game again
                if (memoryGame.getNumMoves() > 0 && !memoryGame.haveWonGame()) {
                    showAlertDiaog("Quit your current game?", null, View.OnClickListener {
                        setupBoard()
                    })
                } else {
                    setupBoard()
                }
            }
            R.id.mi_new_size ->{
                showNewSizeDialog()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showNewSizeDialog() {
        LayoutInflater.from(this).inflate(R.layout.dialog_board_size,null)
        val boardSizeView = LayoutInflater.from(this).inflate(R.layout.dialog_board_size,null)
        val radioGroupSize = boardSizeView.findViewById<RadioGroup>(R.id.radioGroup)
        showAlertDiaog("Choose new size ",boardSizeView,View.OnClickListener {
            // set the new size of the board size
            when (boardSIze){
                BoardSIze.EASY -> radioGroupSize.check(R.id.rbEasy)
                BoardSIze.MEDIUM -> radioGroupSize.check(R.id.rbMedium)
                BoardSIze.HARD -> radioGroupSize.check(R.id.rbHard)
            }
            boardSIze = when (radioGroupSize.checkedRadioButtonId) {
                R.id.rbEasy -> BoardSIze.EASY
                R.id.rbMedium -> BoardSIze.MEDIUM
                else -> BoardSIze.HARD

            }
            setupBoard()
        })
    }

    private fun showAlertDiaog(title: String,  view: View?,positiveClickListener: View.OnClickListener) {
        AlertDialog.Builder(this)
            .setTitle(title)
            .setView(view)
            .setNegativeButton("Cancel", null)
            .setPositiveButton("OK"){ _, _ ->
                positiveClickListener.onClick(null)
            }.show()
    }

    private fun setupBoard() {
        when (boardSIze){
            BoardSIze.EASY -> {
                tvNumMoves.text = "Easy: 4 x 2"
                tvNumPairs.text = "Pairs: 0 / 4"
            }
            BoardSIze.MEDIUM -> {
                tvNumMoves.text = "Medium: 6 x 3"
                tvNumPairs.text = "Pairs: 0 / 9"
            }
            BoardSIze.HARD -> {
                tvNumMoves.text = "Hard: 6 x 6"
                tvNumPairs.text = "Pairs: 0 / 12"
            }
        }
        tvNumPairs.setTextColor(ContextCompat.getColor(this, R.color.color_progress_none))
        memoryGame = MemoryGame(boardSIze)
        adapter = MemoryBordAdapter(
            this,
            boardSIze,
            memoryGame.cards,
            object : MemoryBordAdapter.CardClickListener {
                override fun onCardClicked(position: Int) {
                    updateGameWithFlip(position)
                }

            })
        rvBord.adapter = adapter
        rvBord.setHasFixedSize(true)
        rvBord.layoutManager = GridLayoutManager(this, boardSIze.getWidth())
    }
    private fun updateGameWithFlip(position: Int) {
        // Error checking
        if (memoryGame.haveWonGame()){
            // Alert the user of an invalid move
                Snackbar.make(clRoot, "You Already WON!",Snackbar.LENGTH_LONG).show()
            return
        }
        if (memoryGame.isCardFaceUp(position)){
            // Alert the user of an invalid move
            Snackbar.make(clRoot, "Invalid Move!",Snackbar.LENGTH_SHORT).show()
            return
        }
        // Actually flipped over the card
        if (memoryGame.flipCard(position)){
            Log.i(TAG,"found a match! num pairs found: ${memoryGame.numPairsFound}")
            val color = ArgbEvaluator().evaluate(
              memoryGame.numPairsFound.toFloat() / boardSIze.getNumPair(),
              ContextCompat.getColor(this,R.color.color_progress_none),
                ContextCompat.getColor(this,R.color.color_progress_full),
            ) as Int
            tvNumPairs.setTextColor(color)
            tvNumPairs.text = "pairs:${memoryGame.numPairsFound} / ${boardSIze.getNumPair()}"
            if (memoryGame.haveWonGame()){
                Snackbar.make(clRoot,"Congratulations! You WON.",Snackbar.LENGTH_LONG).show()
            }
        }
        tvNumMoves.text = "Moves: ${memoryGame.getNumMoves()}"
        adapter.notifyDataSetChanged()
    }
}