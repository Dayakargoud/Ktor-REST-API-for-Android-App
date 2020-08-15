package com.dayakar.models

import io.ktor.auth.Principal
import java.io.Serializable

data class User(
    val userId: Int,
    val email: String,
    val displayName: String,
    val passwordHash: String,
    val creationTime:String): Serializable, Principal