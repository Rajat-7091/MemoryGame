package com.example.memorygame

enum class BoardSIze(val  numCard : Int){
    EASY(numCard = 8),
    MEDIUM(numCard = 18),
    HARD(numCard = 24);

    fun getWidth():Int {
        return when(this){
            EASY -> 2
            MEDIUM -> 3
            HARD -> 4
        }
    }
    fun getHeight():Int {
        return numCard / getWidth()
    }
    fun getNumPair(): Int {
        return numCard / 2
    }
}