package de.hsworms.flashcards.ui

import de.hsworms.flashcard.database.entity.Repository
import de.hsworms.flashcard.database.entity.RepositoryWithCards
import de.hsworms.flashcards.model.Set

/**
 * Base item for all ListItems
 */
sealed class ListItem

/**
 * Represents a card stack in a list, holding a [Set] object
 */
data class CardSetItem(val set: RepositoryWithCards) : ListItem()

