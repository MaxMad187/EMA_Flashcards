package de.hsworms.flashcard.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import de.hsworms.flashcard.database.dao.FlashcardDao
import de.hsworms.flashcard.database.dao.RepositoryDao
import de.hsworms.flashcard.database.entity.Flashcard
import de.hsworms.flashcard.database.entity.FlashcardNormal
import de.hsworms.flashcard.database.entity.Repository
import de.hsworms.flashcard.database.entity.RepositoryCardCrossRef

/**
 * Manages access to the database
 *
 * @constructor Empty constructor for no greater use
 */
@Database(entities = [Flashcard::class, FlashcardNormal::class, Repository::class, RepositoryCardCrossRef::class], version = 2)
abstract class FCDatabase : RoomDatabase() {

    /**
     * Gets the DAO for the [Flashcard]s.
     * Use it to manage the [Flashcard]s in the database
     *
     * @return  The DAO for the [Flashcard]s
     */
    abstract fun flashcardDao(): FlashcardDao

    /**
     * Gets the DAO for the [Repository]s.
     * Use it to manage the [Repository]s in the database
     *
     * @return  The DAO for the [Repository]s
     */
    abstract fun repositoryDao(): RepositoryDao

    companion object {
        @Volatile
        private var INSTANCE: FCDatabase? = null

        /**
         * Gets the current instance or creates a new one.
         *
         * @param   context The context of the database
         *
         * @return  The current instance of the [FCDatabase] adhearing to the given context
         */
        fun getDatabase(context: Context): FCDatabase {
            if (INSTANCE == null) {
                synchronized(this) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext, FCDatabase::class.java, "flashcard_database")
                        .fallbackToDestructiveMigration().build()
                }
            }
            return INSTANCE!!
        }
    }
}