package de.hsworms.flashcard.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "flashcard_normal", foreignKeys = [ForeignKey(entity = Flashcard::class, parentColumns = ["id"], childColumns = ["id"], onDelete = ForeignKey.CASCADE)])
data class FlashcardNormal (
    val front: String,
    val back: String
) : Flashcard(type = 0)