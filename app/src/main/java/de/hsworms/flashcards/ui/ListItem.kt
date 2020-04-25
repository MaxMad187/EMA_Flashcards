package de.hsworms.flashcards.ui

import de.hsworms.flashcards.model.Set

/**
 * Base item for all ListItems
 */
sealed class ListItem

/**
 * Represents a card stack in a list, holding a [Set] object
 */
data class CardSetItem(val set: Set) : ListItem()