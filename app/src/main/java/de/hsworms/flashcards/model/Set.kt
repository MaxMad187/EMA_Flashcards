package de.hsworms.flashcards.model

data class Set(
    val id: Long,
    val name: String,

    // Experimental vals (!)
    val shortTimeCardCount: Int,
    val middleTimeCardCount: Int,
    val longTimeCardCount: Int
    //val cards: List<Card>
)