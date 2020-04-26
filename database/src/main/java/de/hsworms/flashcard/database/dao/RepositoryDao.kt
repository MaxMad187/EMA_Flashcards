package de.hsworms.flashcard.database.dao

import androidx.room.*
import de.hsworms.flashcard.database.entity.Repository
import de.hsworms.flashcard.database.entity.RepositoryCardCrossRef
import de.hsworms.flashcard.database.entity.RepositoryWithCards

@Dao
interface RepositoryDao {

    /**
     * TODO
     */
    @Transaction
    @Query("SELECT * from repository WHERE repoId = :repoId")
    fun getRepositoryWithCards(repoId: Int): RepositoryWithCards?

    /**
     * TODO
     */
    @Query("SELECT * from repository WHERE repoId = :repoId")
    fun getRepository(repoId: Int): Repository?

    /**
     * TODO
     */
    @Insert
    fun addCard(vararg cards: RepositoryCardCrossRef)

    /**
     * TODO
     */
    @Delete
    fun removeCard(vararg cards: RepositoryCardCrossRef)

    /**
     * TODO
     */
    @Insert
    fun insert(vararg repo: Repository)

    /**
     * TODO
     */
    @Delete
    fun delete(vararg repo: Repository)
}