package de.hsworms.flashcard.database.entity

import androidx.room.*
import java.io.Serializable

/**
 * Manages a card repository.
 *
 * @param   repoId  The id of the repository. If null than the database will generate one on insert.
 * @param   name    The name of the repository.
 */
@Entity
data class Repository(
    @PrimaryKey(autoGenerate = true) val repoId: Int? = null,
    val name: String
)

/**
 * The cross reference between [Repository] and [Flashcard].
 * It is used for the many to many relationship between them.
 *
 * @param   repoId      The id of the [Repository] in this relation.
 * @param   cardId      The id of the [Flashcard] in this relation.
 * @param   nextDate    The next date when the card is due.
 * @param   interval    The current learn interval.
 */
@Entity(
    tableName = "repository_cards",
    primaryKeys = ["repoId", "cardId"],
    foreignKeys = [ForeignKey(
        entity = Repository::class,
        parentColumns = ["repoId"],
        childColumns = ["repoId"],
        onDelete = ForeignKey.CASCADE
    ),
        ForeignKey(
            entity = Flashcard::class,
            parentColumns = ["cardId"],
            childColumns = ["cardId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(name = "repo_index", value = ["repoId"]), Index(name = "card_index", value = ["cardId"])]
)
data class RepositoryCardCrossRef(
    val repoId: Int,
    val cardId: Long,
    val nextDate: Long,
    val interval: Int
) : Serializable

/**
 * A POJO to get a repository with all its cards.
 *
 * @param   repository  The repository of this POJO.
 * @param   cards       All the cards in this POJO.
 * @param   crossRef    A list of all cross reference associated with the repository.
 */
data class RepositoryWithCards(
    @Embedded val repository: Repository,
    @Relation(
        parentColumn = "repoId",
        entityColumn = "cardId",
        associateBy = Junction(RepositoryCardCrossRef::class)
    ) val cards: List<Flashcard> = emptyList(),
    @Relation(
        entity = RepositoryCardCrossRef::class,
        entityColumn = "repoId",
        parentColumn = "repoId"
    ) val crossRef: List<RepositoryCardCrossRef>
)