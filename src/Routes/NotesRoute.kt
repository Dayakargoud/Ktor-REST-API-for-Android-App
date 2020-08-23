package com.dayakar.Routes

import com.dayakar.API_VERSION
import com.dayakar.auth.MySession
import com.dayakar.models.Note
import com.dayakar.repository.Repository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.ktor.application.application
import io.ktor.application.call
import io.ktor.application.log
import io.ktor.auth.authenticate
import io.ktor.http.HttpStatusCode
import io.ktor.http.Parameters
import io.ktor.locations.*
import io.ktor.request.receive
import io.ktor.request.receiveParameters
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.delete
import io.ktor.sessions.get
import io.ktor.sessions.sessions
import kotlin.reflect.typeOf

const val NOTES = "$API_VERSION/notes"
const val ADDNOTE = "$API_VERSION/notes/addNote"


@KtorExperimentalLocationsAPI
@Location(ADDNOTE)
class AddNoteRoute

@KtorExperimentalLocationsAPI
@Location(NOTES)
class NoteRoute


@KtorExperimentalLocationsAPI
@Location("v1/notes/{id}")
class NoteRouteDelete(val id: String)


@KtorExperimentalLocationsAPI
fun Route.notes(db:Repository){

         authenticate("jwt") {
             post<AddNoteRoute> {
                 val note = call.receive<Note>()
                 println("----------Added Note $note")

                 val user = call.sessions.get<MySession>()?.let {
                     db.findUser(it.userId)
                 }

                 if (user == null) {
                     call.respond(
                         HttpStatusCode.BadRequest, "Problems retrieving User"
                     )
                     return@post
                 }

                 try {
                     //adding note
                     val addedNote = db.addNote(note)
                     addedNote?.id?.let {
                         call.respond(HttpStatusCode.OK, addedNote)
                     }
                 } catch (e: Throwable) {
                     application.log.error("Failed to add Note", e)
                     call.respond(HttpStatusCode.BadRequest, "Problems Saving Note")
                 }


             }


             post<NoteRoute> {
                 val param = call.receive<List<*>>()

                 val jsonString=Gson().toJson(param)
                 val notesList = Gson().fromJson<List<Note>>(jsonString, object: TypeToken<List<Note>>(){}.type)
                 val user = call.sessions.get<MySession>()?.let {
                     db.findUser(it.userId)
                 }

                 if (user == null) {
                     call.respond(
                         HttpStatusCode.BadRequest, "Problems retrieving User"
                     )
                     return@post
                 }

                 try {
                     //adding note
                      db.addNotes(user.userId,notesList)
                     val notes = db.getNotes(user.userId)
                     call.respond(notes)

                 } catch (e: Throwable) {
                     application.log.error("Failed to add Note", e)
                     call.respond(HttpStatusCode.BadRequest, "Problems Saving Note")
                 }


             }
             get<NoteRoute> {
                 //Getting use details from session id

                 println("-----------------Get method---Session id= ${call.sessions.get<MySession>()}")

                 val user = call.sessions.get<MySession>()?.let {
                     db.findUser(it.userId)
                 }

                 if (user == null) {
                     call.respond(
                         HttpStatusCode.BadRequest, "Problems retrieving User"
                     )
                     return@get
                 }

                 try {
                     val notes = db.getNotes(user.userId)
                     call.respond(notes)
                 } catch (e: Throwable) {

                     application.log.error("Failed to Retrieve Notes", e)
                     call.respond(HttpStatusCode.BadRequest, "Failed to Retrieve Notes")

                 }

             }
             patch<NoteRoute> {
                 //Getting All Parameters from request
                 val note = call.receive<Note>()

                 //Getting use details from session id
                 val user = call.sessions.get<MySession>()?.let {
                     db.findUser(it.userId)
                 }

                 if (user == null) {
                     call.respond(
                         HttpStatusCode.BadRequest, "Problems retrieving User"
                     )
                     return@patch
                 }

                 try {
                     //update note
                     val updatedNote = db.updateNote(note)
                     //responding added note
                     updatedNote?.id?.let {
                         call.respond(HttpStatusCode.OK, updatedNote)
                     }

                    // call.respond(HttpStatusCode.OK,"Done")
                 } catch (e: Throwable) {
                     application.log.error("Failed to add Note", e)
                     call.respond(HttpStatusCode.BadRequest, "Problems Saving Note")
                 }


             }
             delete<NoteRouteDelete> {
                 //Getting All Parameters from request

                 val noteId = call.parameters["id"] ?: throw IllegalArgumentException("Parameter id not found")

                 //Getting use details from session id
                 println("Note---------${noteId}")
                 val user = call.sessions.get<MySession>()?.let {
                     db.findUser(it.userId)
                 }

                 if (user == null) {
                     call.respond(
                         HttpStatusCode.BadRequest, "Problems retrieving User"
                     )
                     return@delete
                 }

                 try {
                     //update note
                     val isDeleted = db.deleteNote(noteId.toInt())

                     //responding added note
                     if (isDeleted) {
                         call.respond(HttpStatusCode.OK, isDeleted)
                     } else call.respond(HttpStatusCode.NotAcceptable, "Failed to delete Note")

                 } catch (e: Throwable) {
                     application.log.error("Failed to add Note", e)
                     call.respond(HttpStatusCode.BadRequest, "Problems Saving Note")
                 }


             }


         }


}
inline fun <reified T> fromJson(json: String?): T {
    return Gson().fromJson<T>(json, object: TypeToken<T>(){}.type)
}