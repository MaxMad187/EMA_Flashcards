package de.hsworms.flashcard.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "flashcard")
open class Flashcard(
    @PrimaryKey var cardId: Long? = null,
    var type: Int
)

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
    constructor(cardId: Long?, front: String, back: String) : this(front, back) {
        super.cardId = cardId
    }
}