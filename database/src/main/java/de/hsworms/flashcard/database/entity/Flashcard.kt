package de.hsworms.flashcard.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

/**
 * Base class for the flashcards.
 * It manages id and type.
 *
 * @param   cardId  The Id of the flashcard. If null than the database will generate one on insert.
 * @param   type    The type of the flashcard. 0 is the only type available at the moment
 */
@Entity(tableName = "flashcard")
open class Flashcard(
    @PrimaryKey var cardId: Long? = null,
    var type: Int
)

/**
 * Type 0 of the flashcard.
 * It adds a front site and a back site.
 *
 * @param   front   The front site of the the flashcard.
 * @param   back    The back site of the flashcard
 */
@Entity(
    tableName = "flashcard_normal",
    foreignKeys = [ForeignKey(
        entity = Flashcard::class,
        parentColumns = ["cardId"],
        childColumns = ["cardId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class FlashcardNormal(
    val front: String,
    val back: String
) : Flashcard(type = 0) {

    /**
     * Constructor to hand over the cardId to the super class
     */
    constructor(cardId: Long?, front: String, back: String) : this(front, back) {
        super.cardId = cardId
    }
}