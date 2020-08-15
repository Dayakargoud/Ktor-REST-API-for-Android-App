package com.dayakar

import com.dayakar.Routes.notes
import com.dayakar.Routes.users
import com.dayakar.auth.JwtService
import com.dayakar.auth.MySession
import com.dayakar.auth.hash
import com.dayakar.repository.DatabaseFactory
import com.dayakar.repository.NotesRepository
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.request.*
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.sessions.*
import io.ktor.auth.*
import io.ktor.auth.jwt.jwt
import io.ktor.gson.*
import io.ktor.features.*
import io.ktor.util.KtorExperimentalAPI

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@KtorExperimentalLocationsAPI
@KtorExperimentalAPI
@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    install(Locations) {
    }

    install(Sessions) {
        cookie<MySession>("MY_SESSION") {
            cookie.extensions["SameSite"] = "lax"
        }
    }

    DatabaseFactory.init()
    val db=NotesRepository()
    val jwtService=JwtService()
    val hashFunction={s:String-> hash(s)}


    install(Authentication) {
        jwt("jwt"){
            verifier(jwtService.verifier)
            realm="Notes Server"
            validate {
              val payload=it.payload
                val claim = payload.getClaim("id")
                val claimString = claim.asInt()
                val user = db.findUser(claimString)
                user

            }
        }
    }

    install(ContentNegotiation) {
        gson {
        }
    }

    routing {
        users(db,jwtService,hashFunction)
        notes(db)

        get("/") {
            call.respondText("HELLO WORLD!", contentType = ContentType.Text.Plain)
        }


    }
}

const val API_VERSION = "/v1"
