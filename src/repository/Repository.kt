package com.dayakar.repository

import com.dayakar.models.Note
import com.dayakar.models.User

interface Repository {

    suspend fun addUser(
        email: String,
        displayName: String,
        passwordHash: String,
        creationTime:String): User?

    suspend fun findUser(userId:Int):User?

    suspend fun findUserByEmail(email: String):User?

    suspend fun addNote(note: Note): Note?

    suspend fun  getNotes(userId: Int):List<Note>

    suspend fun updateNote(note:Note):Note?

    suspend fun deleteNote(noteId:Int):Boolean

    suspend fun addNotes(userId: Int,notes:List<Note>)


}