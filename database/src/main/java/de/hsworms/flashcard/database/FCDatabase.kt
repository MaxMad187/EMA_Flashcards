package de.hsworms.flashcard.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import de.hsworms.flashcard.database.dao.FlashcardDao
import de.hsworms.flashcard.database.entity.Flashcard
import de.hsworms.flashcard.database.entity.FlashcardNormal

@Database(entities = [Flashcard::class, FlashcardNormal::class], version = 1)
abstract class FCDatabase : RoomDatabase() {
    abstract fun flashcardDao(): FlashcardDao

    companion object {
        @Volatile
        private var INSTANCE: FCDatabase? = null

        fun getDatabase(context: Context): FCDatabase {
            if(INSTANCE == null){
                synchronized(this) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext, FCDatabase::class.java, "flashcard_database").build()
                }
            }
            return INSTANCE!!
        }
    }
}