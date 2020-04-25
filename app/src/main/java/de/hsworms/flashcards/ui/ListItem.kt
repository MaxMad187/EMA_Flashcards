package de.hsworms.flashcards.ui

import de.hsworms.flashcards.model.CardStack

/**
 * Base item for all ListItems
 */
sealed class ListItem

/**
 * Represents a card stack in a list, holding a [CardStack] object
 */
data class CardStackItem(val cardStack: CardStack) : ListItem()