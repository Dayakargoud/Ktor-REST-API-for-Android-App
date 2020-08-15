package com.dayakar.Routes

import com.dayakar.API_VERSION
import com.dayakar.auth.JwtService
import com.dayakar.auth.MySession
import com.dayakar.repository.Repository
import io.ktor.application.application
import io.ktor.application.call
import io.ktor.application.log
import io.ktor.http.HttpStatusCode
import io.ktor.http.Parameters
import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.locations.Location
import io.ktor.locations.post
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.Route
import io.ktor.sessions.sessions
import io.ktor.sessions.set

const val USERS = "$API_VERSION/users"
const val USER_LOGIN = "$USERS/login"
const val USER_CREATE = "$USERS/create"

@KtorExperimentalLocationsAPI
@Location(USER_LOGIN)
class UserLoginRoute

@KtorExperimentalLocationsAPI
@Location(USER_CREATE)
class UserCreateRoute

@KtorExperimentalLocationsAPI
fun Route.users(db: Repository, jwtService: JwtService, hashFunction:(String)->String){

    post<UserCreateRoute>{
          val signupParameters=call.receive<Parameters>()
          val password=signupParameters["password"]?:call.respond(HttpStatusCode.BadRequest,"Missing Fileds")

          val displayName = signupParameters["displayName"]
            ?: return@post call.respond(
                HttpStatusCode.Unauthorized, "Missing Fields")
        val email = signupParameters["email"]
            ?: return@post call.respond(
                HttpStatusCode.Unauthorized, "Missing Fields")
        val hash = hashFunction(password as String)
        val creationTime=System.currentTimeMillis().toString()

        try{
            val newUser=db.addUser(email,displayName,hash,creationTime)
            //Setting Session Id and generating Token
            newUser?.userId?.let {
                   call.sessions.set(MySession(it))
                   call.respondText(jwtService.generateToken(newUser),status = HttpStatusCode.Created)
            }
        }catch (e:Throwable){
               application.log.error("Failed to register user", e)
               call.respond(HttpStatusCode.BadRequest, "Problems creating User")

        }


    }

    post<UserLoginRoute>{
        val signinParameters=call.receive<Parameters>()
        val email=signinParameters["email"]?:return@post call.respond(HttpStatusCode.Unauthorized,"Missing Fileds")
        val password=signinParameters["password"]?:return@post call.respond(HttpStatusCode.Unauthorized,"Missing Fields")

        val hash=hashFunction(password)

        try{
               val currentUser=db.findUserByEmail(email)

            //Generate token and session id
               currentUser?.userId?.let {
                   if (currentUser.passwordHash == hash) {
                       call.sessions.set(MySession(it))
                       call.respondText(jwtService.generateToken(currentUser))
                   } else {
                       call.respond(
                           HttpStatusCode.BadRequest, "Problems retrieving User")
                   }
               }

        }catch (e:Throwable){
            application.log.error("Failed to register user", e)
            call.respond(HttpStatusCode.BadRequest, "Problems retrieving User")

        }





    }
}


