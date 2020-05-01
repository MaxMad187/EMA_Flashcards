package de.hsworms.flashcard.database.dao

import androidx.room.*
import de.hsworms.flashcard.database.entity.Flashcard
import de.hsworms.flashcard.database.entity.Repository
import de.hsworms.flashcard.database.entity.RepositoryCardCrossRef
import de.hsworms.flashcard.database.entity.RepositoryWithCards

@Dao
interface RepositoryDao {

    /**
     * Gets all [Repository]s with their respective [Flashcard].
     *
     * @return All [Repository]s
     */
    @Transaction
    @Query("SELECT * from repository")
    fun getAllRepositoriesWithCards(): List<RepositoryWithCards>

    /**
     * Gets one [Repository] with its [Flashcard]s by its [Repository.repoId].
     *
     * @param repoId The [Repository.repoId] of the [Repository].
     *
     * @return The [Repository] with the [repoId] or null if not found.
     */
    @Transaction
    @Query("SELECT * from repository WHERE repoId = :repoId")
    fun getRepositoryWithCards(repoId: Int): RepositoryWithCards?

    /**
     * Gets one [Repository] without its [Flashcard]s by its [Repository.repoId].
     *
     * @param repoId The [Repository.repoId] of the [Repository].
     *
     * @return The [Repository] with the [repoId] or null if not found.
     */
    @Query("SELECT * from repository WHERE repoId = :repoId")
    fun getRepository(repoId: Int): Repository?

    /**
     * Adds one or more [Flashcard] to a [Repository].
     *
     * @param cards The [RepositoryCardCrossRef]s to add.
     */
    @Insert
    fun addCard(vararg cards: RepositoryCardCrossRef)

    /**
     * Removes one or more [Flashcard] from a [Repository].
     *
     * @param cards The [RepositoryCardCrossRef]s to remove.
     */
    @Delete
    fun removeCard(vararg cards: RepositoryCardCrossRef)

    /**
     * Creates one or more [Repository].
     *
     * @param repo The [Repository]s to add.
     */
    @Insert
    fun insert(vararg repo: Repository)

    /**
     * Deletes one or more [Repository].
     *
     * @param repo The [Repository]s to delete.
     */
    @Delete
    fun delete(vararg repo: Repository)

    /**
     * Updates one ore more [Repository].
     *
     * @param repo The [Repository]s to update.
     */
    @Update
    fun update(vararg repo: Repository)
}