package com.dayakar.Routes

import com.dayakar.API_VERSION
import com.dayakar.auth.MySession
import com.dayakar.models.Note
import com.dayakar.repository.Repository
import io.ktor.application.application
import io.ktor.application.call
import io.ktor.application.log
import io.ktor.auth.authenticate
import io.ktor.http.HttpStatusCode
import io.ktor.http.Parameters
import io.ktor.locations.*
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.delete
import io.ktor.sessions.get
import io.ktor.sessions.sessions

const val NOTES = "$API_VERSION/notes"


@KtorExperimentalLocationsAPI
@Location(NOTES)
class NoteRoute


@KtorExperimentalLocationsAPI
fun Route.notes(db:Repository){

         authenticate("jwt"){

             post<NoteRoute>{
                   //Getting All Parameters from request
                   val noteParameters=call.receive<Parameters>()
                   val title=noteParameters["title"]?: "Title"
                   val note=noteParameters["note"]?: " "
                   val  creationDate=noteParameters["updateTime"]?: System.currentTimeMillis().toString()

                   //Getting use details from session id
                   val user = call.sessions.get<MySession>()?.let {
                       db.findUser(it.userId)
                   }

                   if (user == null) {
                       call.respond(
                           HttpStatusCode.BadRequest, "Problems retrieving User")
                       return@post
                   }

                   try{
                       //adding note
                       val currentNote=db.addNote(user.userId,title,note,creationDate)

                       //responding added note
                       currentNote?.id?.let {
                           call.respond(HttpStatusCode.OK, currentNote)
                       }

                   }catch (e:Throwable){
                       application.log.error("Failed to add Note", e)
                       call.respond(HttpStatusCode.BadRequest, "Problems Saving Note")
                   }




               }
             get<NoteRoute>{
                 //Getting use details from session id

                 println("-----------------------Session id= ${call.sessions.get<MySession>()}")
                 val user = call.sessions.get<MySession>()?.let {
                     db.findUser(it.userId)
                 }

                 if (user == null) {
                     call.respond(
                         HttpStatusCode.BadRequest, "Problems retrieving User")
                     return@get
                 }

                 try {
                     val notes=db.getNotes(user.userId)
                     call.respond(notes)
                 }catch (e:Throwable){

                     application.log.error("Failed to Retrieve Notes", e)
                     call.respond(HttpStatusCode.BadRequest, "Failed to Retrieve Notes")

                 }

             }
             put<NoteRoute>{
                 //Getting All Parameters from request
                 val note=call.receive<Note>()
                 //Getting use details from session id
                 val user = call.sessions.get<MySession>()?.let {
                     db.findUser(it.userId)
                 }

                 if (user == null) {
                     call.respond(
                         HttpStatusCode.BadRequest, "Problems retrieving User")
                     return@put
                 }

                 try{
                     //update note
                     val updatedNote=db.updateNote(note)

                     //responding added note
                     updatedNote?.id?.let {
                         call.respond(HttpStatusCode.OK, updatedNote)
                     }

                 }catch (e:Throwable){
                     application.log.error("Failed to add Note", e)
                     call.respond(HttpStatusCode.BadRequest, "Problems Saving Note")
                 }




             }
             delete<NoteRoute>{
                 //Getting All Parameters from request
                 val note=call.receive<Note>()
                 //Getting use details from session id
                 val user = call.sessions.get<MySession>()?.let {
                     db.findUser(it.userId)
                 }

                 if (user == null) {
                     call.respond(
                         HttpStatusCode.BadRequest, "Problems retrieving User")
                     return@delete
                 }

                 try{
                     //update note
                     val isDeleted=db.deleteNote(note.id)

                     //responding added note
                        if (isDeleted){
                         call.respond(HttpStatusCode.OK, isDeleted)
                     }else call.respond(HttpStatusCode.NotAcceptable,"Failed to delete Note")

                 }catch (e:Throwable){
                     application.log.error("Failed to add Note", e)
                     call.respond(HttpStatusCode.BadRequest, "Problems Saving Note")
                 }




             }


         }

}