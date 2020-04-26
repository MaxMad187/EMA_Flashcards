package de.hsworms.flashcard.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import de.hsworms.flashcard.database.entity.Flashcard
import de.hsworms.flashcard.database.entity.FlashcardNormal

@Dao
interface FlashcardDao {

    /**
     * Gets one [Flashcard] by its [id].
     *
     * @param id The id of the [Flashcard].
     *
     * @return The [Flashcard] with the [id] or null if not found.
     */
    fun getOne(id: Long): Flashcard? {
        return when(getType(id)) {
            0L -> getFlashcardNormal(id)
            else -> null;
        }
    }

    /**
     * Inserts one or more [Flashcard] according to their type.
     *
     * @param flashcard The [Flashcard]s to insert.
     */
    fun insert(vararg flashcard: Flashcard) {
        insertAbstract(*flashcard)
        insertNormal(*flashcard.filterIsInstance<FlashcardNormal>().toTypedArray())
    }

    /**
     * Inserts one or more [Flashcard].
     *
     * @param flashcard The [Flashcard]s to insert.
     */
    @Insert
    fun insertAbstract(vararg  flashcard: Flashcard)

    /**
     * Gets the [Flashcard.type] of a [Flashcard].
     *
     * @param id The [Flashcard.id] of the [Flashcard].
     *
     * @return The [Flashcard.type] of a [Flashcard] with the [id] or null if not found.
     */
    @Query("SELECT type FROM flashcard WHERE id = :id")
    fun getType(id: Long): Long?

    /**
     * Deletes one or more [Flashcard].
     *
     * @param flashcard The [Flashcard]s to delete.
     */
    @Delete
    fun delete(vararg flashcard: Flashcard)


    /* FLASHCARD_NORMAL */

    /**
     * Gets one [FlashcardNormal] by its [Flashcard.id].
     *
     * @param id The [Flashcard.id] of the [FlashcardNormal].
     *
     * @return The [FlashcardNormal] with the [id] or null if not found.
     */
    @Query(value = "SELECT * FROM flashcard_normal WHERE id = :id")
    fun getFlashcardNormal(id: Long): FlashcardNormal?

    /**
     * Inserts one or more [FlashcardNormal].
     *
     * @param flashcard The [FlashcardNormal]s to insert.
     */
    @Insert
    fun insertNormal(vararg flashcard: FlashcardNormal)
}