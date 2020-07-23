package de.hsworms.flashcards.ui.flashcard

import de.hsworms.flashcard.database.entity.Flashcard

data class ViewCard(val card: Flashcard, val time: Long, val interval: Int, val ef: Double = 2.5)