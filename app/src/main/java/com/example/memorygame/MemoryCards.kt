package com.example.memorygame
data class  MemoryCards(
    val identifier: Int,
    var isFaceUp: Boolean = false,
    var isMatched: Boolean = false
)
