package com.dayakar.repository

import com.google.gson.Gson
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table


object Notes: Table() {
    val id : Column<Int> = integer("id").autoIncrement().primaryKey()
    val userId : Column<Int> = integer("userId").references(Users.userId)
    val title=varchar("title",128)
    val note = varchar("note", 2048)
    val updatetime = varchar("updatetime",64)
}



