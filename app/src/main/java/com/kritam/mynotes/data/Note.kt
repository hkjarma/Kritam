package com.kritam.mynotes.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class Note(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val description: String,
    val createdAt: Long,
    val dueDate: Long,
    val isDone: Boolean = false,
    val doneAt: Long? = null
)
