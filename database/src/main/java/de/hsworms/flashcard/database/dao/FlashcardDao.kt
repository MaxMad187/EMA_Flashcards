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
     * Gets all [Flashcard]s with their respective type.
     *
     * @return All [Flashcard]s.
     */
    fun getAll(): List<Flashcard> {
        val flc = getAllAbstract()
        val cards = mutableListOf<Flashcard>()

        cards.addAll(getAllNormalByIds(*(flc.filter { it.type == 0 }.map { it.cardId!! }.toLongArray())))

        return cards
    }

    /**
     * Gets all [Flashcard]s without their respective type.
     *
     * @return All [Flashcard]s.
     */
    @Query("SELECT * FROM flashcard")
    fun getAllAbstract(): List<Flashcard>

    /**
     * Gets one [Flashcard] by its [cardId].
     *
     * @param cardId The cardId of the [Flashcard].
     *
     * @return The [Flashcard] with the [cardId] or null if not found.
     */
    fun getOne(cardId: Long): Flashcard? {
        return when(getType(cardId)) {
            0L -> getFlashcardNormal(cardId)
            else -> null
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
     * @param cardId The [Flashcard.cardId] of the [Flashcard].
     *
     * @return The [Flashcard.type] of a [Flashcard] with the [cardId] or null if not found.
     */
    @Query("SELECT type FROM flashcard WHERE cardId = :cardId")
    fun getType(cardId: Long): Long?

    /**
     * Deletes one or more [Flashcard].
     *
     * @param flashcard The [Flashcard]s to delete.
     */
    @Delete
    fun delete(vararg flashcard: Flashcard)


    /* FLASHCARD_NORMAL */

    /**
     * Gets one [FlashcardNormal] by its [Flashcard.cardId].
     *
     * @param cardId The [Flashcard.cardId] of the [FlashcardNormal].
     *
     * @return The [FlashcardNormal] with the [cardId] or null if not found.
     */
    @Query("SELECT * FROM flashcard_normal WHERE cardId = :cardId")
    fun getFlashcardNormal(cardId: Long): FlashcardNormal?

    /**
     * Gets all [FlashcardNormal]s by their respective [Flashcard.cardId]s.
     *
     * @param cardId One or more [Flashcard.cardId].
     *
     * @return The [FlashcardNormal]s who have the asked [Flashcard.cardId]s.
     */
    @Query("SELECT * FROM flashcard_normal WHERE cardID IN (:cardId)")
    fun getAllNormalByIds(vararg cardId: Long): List<FlashcardNormal>

    /**
     * Inserts one or more [FlashcardNormal].
     *
     * @param flashcard The [FlashcardNormal]s to insert.
     */
    @Insert
    fun insertNormal(vararg flashcard: FlashcardNormal)
}