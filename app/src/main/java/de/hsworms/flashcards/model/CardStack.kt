package de.hsworms.flashcards.model

data class CardStack(
    val id: Long,
    val title: String,
    val shortTimeCardCount: Int,
    val middleTimeCardCount: Int,
    val longTimeCardCount: Int
    //val cards: List<Card>
)