package com.example.memorygame

import com.example.memorygame.utils.DEFAULT_ICONS

class MemoryGame(private val boardSize: BoardSize){
    val cards: List<MemoryCards>

    var numPairsFound = 0
    private  var numCardFlips = 0

    private  var indexOfStringSelectedCard: Int? = null
    init {
        val chosenImages : List<Int> = DEFAULT_ICONS.shuffled().take(boardSize.getNumPair())
        val randomizedImages : List<Int> = (chosenImages + chosenImages).shuffled()
        cards = randomizedImages.map { MemoryCards(it) }

    }
    fun flipCard(position: Int): Boolean {
        numCardFlips++
        val card : MemoryCards = cards[position]
        // three cases:
        // 0 card previously flipped over => restore cards + flip over the Selected card
        // 1 card previously flipped over => flipped over the selected card + check if the image match
        // 2 card previously flipped over => restore card + flip over the selected card
        var foundMatch = false
        if (indexOfStringSelectedCard == null){
            // 0 or 2 cards previously flipped over
            restoreCards()
            indexOfStringSelectedCard = position
        }
        else{
            //exactly 1 card previously flipped over
            foundMatch = checkForMatch(indexOfStringSelectedCard!!,position)
            indexOfStringSelectedCard = null
        }
        card.isFaceUp = !card.isFaceUp
        return foundMatch
    }

    private fun checkForMatch(position1: Int, position2: Int): Boolean {
        if (cards[position1].identifier != cards[position2].identifier) {
            return false
        }
        cards[position1].isMatched = true
        cards[position2].isMatched = true
        numPairsFound++
        return true
    }

    private fun restoreCards() {
        for ( card in cards ){
            if(!card.isMatched){
                card.isFaceUp = false
            }

        }
    }

    fun haveWonGame(): Boolean {
        return numPairsFound == boardSize.getNumPair()
    }

    fun isCardFaceUp(position: Int): Boolean {
        return cards[position].isFaceUp
    }

    fun getNumMoves(): Int {
      return  numCardFlips / 2
    }
}
