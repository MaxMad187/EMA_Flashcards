package de.hsworms.flashcards.ui

import de.hsworms.flashcard.database.entity.Flashcard
import de.hsworms.flashcard.database.entity.Repository
import de.hsworms.flashcard.database.entity.RepositoryCardCrossRef
import de.hsworms.flashcard.database.entity.RepositoryWithCards

/**
 * Base item for all ListItems
 */
sealed class ListItem

/**
 * Represents a card stack in a list, holding a [RepositoryWithCards] object
 */
data class CardSetItem(val set: RepositoryWithCards) : ListItem()

/**
 * Represents a card in a list, holding a [Flashcard] object
 */
data class CardItem(val repo: Repository, val card: Flashcard, val cross: RepositoryCardCrossRef) : ListItem()
