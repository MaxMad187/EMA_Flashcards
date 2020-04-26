package de.hsworms.flashcard.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "flashcard")
open class Flashcard (
    @PrimaryKey var id: Long? = null,
    @ColumnInfo(name = "type") var type: Int
)