package de.hsworms.flashcard.database.entity

import androidx.room.*
import java.io.Serializable

@Entity
data class Repository(
    @PrimaryKey(autoGenerate = true) val repoId: Int? = null,
    val name: String
)

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