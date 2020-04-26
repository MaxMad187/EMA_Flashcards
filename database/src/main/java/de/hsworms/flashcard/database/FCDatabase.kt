package de.hsworms.flashcard.database

import androidx.room.Database
import androidx.room.RoomDatabase
import de.hsworms.flashcard.database.dao.FlashcardDao
import de.hsworms.flashcard.database.entity.Flashcard
import de.hsworms.flashcard.database.entity.FlashcardNormal

@Database(entities = [Flashcard::class, FlashcardNormal::class], version = 1)
abstract class FCDatabase : RoomDatabase() {
    abstract fun flashcardDao(): FlashcardDao
}