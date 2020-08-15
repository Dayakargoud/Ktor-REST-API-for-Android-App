package com.dayakar.repository

import com.dayakar.models.Note
import com.dayakar.models.User
import com.dayakar.repository.DatabaseFactory.dbQuery
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.statements.InsertStatement
import org.jetbrains.exposed.sql.statements.UpdateStatement
import org.jetbrains.exposed.sql.transactions.transaction

class NotesRepository:Repository {
    override suspend fun addUser(
        email: String,
        displayName: String,
        passwordHash: String,
        creationTime: String
    ): User? {
        var statement:InsertStatement<Number>?=null

        dbQuery{
             statement=Users.insert {user->
                 user[Users.email]=email
                 user[Users.displayName]=displayName
                 user[Users.passwordHash]=passwordHash
                 user[Users.creationTime]=creationTime

             }
        }

        return rowToUser(statement?.resultedValues?.get(0))
    }

    override suspend fun findUser(userId: Int): User? {
        return transaction {
               Users.select{
                       Users.userId.eq(userId)
               }.map { rowToUser(it) }.singleOrNull()
        }

    }

    override suspend fun findUserByEmail(email: String): User? {
        return transaction {
            Users.select{
                Users.email.eq(email)
            }.map { rowToUser(it) }.singleOrNull()
        }
    }

    override suspend fun addNote(userId: Int, title: String, notes: String, updateTime: String): Note? {
         var statement:InsertStatement<Number>?=null

        dbQuery {
            statement=Notes.insert {note->
                note[Notes.userId]=userId
                note[Notes.title]=title
                note[Notes.note]=notes
                note[Notes.updatetime]=updateTime

            }
        }
        return rowToNote(statement?.resultedValues?.get(0))
    }

    override suspend fun getNotes(userId: Int): List<Note> {
        return transaction {
            Notes.select {
                Notes.userId.eq((userId)) // 3
            }.mapNotNull { rowToNote(it) }

        }

    }

    override suspend fun updateNote(note: Note): Note? {
        dbQuery {
         Notes.update({ Notes.id.eq(note.id) and Notes.userId.eq(note.userId) }) {
                it[title] = note.title
                it[this.note]=note.note
                it[updatetime]=System.currentTimeMillis().toString()

            }
        }
       return getNote(note.id)

    }

    override suspend fun deleteNote(noteId:Int): Boolean {
           return dbQuery {
               Notes.deleteWhere { Notes.id eq noteId } >0
           }

    }


    suspend fun getNote(id: Int): Note? = dbQuery {
        Notes.select {
            (Notes.id eq id)
        }.mapNotNull { rowToNote(it) }
            .singleOrNull()
    }

    private fun rowToUser(row: ResultRow?):User?{
        if (row == null) {
            return null
        }
       return User(
           userId = row[Users.userId],
           email = row[Users.email],
           displayName = row[Users.displayName],
           passwordHash = row[Users.passwordHash],
           creationTime = row[Users.creationTime]
       )

    }

    private fun rowToNote(row:ResultRow?):Note?{
        if (row==null){
            return null
        }

        return Note(

            id = row[Notes.id],
            userId = row[Notes.userId],
            title = row[Notes.title],
            note = row[Notes.note],
            updatetime = row[Notes.updatetime]


        )
    }
}