package net.codebot.models


import kotlinx.serialization.Serializable
import io.ktor.http.*
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq


@Serializable
data class TodoItem(
    val id: Int,
    val primaryTask: String,
    val secondaryTask: String,
    val priority: Int,
    var completed: Boolean,
    var datetime: String,
    val section: String,
    val duration: Int,
    val starttime: String,
    var recur: String,
    var pid: Int,
    val deleted: Int,
    val misc1: Int,
    val misc2: Int
)



object TodoTable : Table() {
    val id = integer("id").autoIncrement()
    val primaryTask = varchar("primaryTask", 255)
    val secondaryTask = varchar("secondaryTask", 255)
    val priority = integer("priority")
    val completed = bool("completed")
    val datetime = varchar("datetime", 255)
    val section = varchar("section", 255)
    val duration = integer("duration")
    val starttime = varchar("starttime", 255)
    val recur = varchar("recur", 255)
    val pid = integer("pid")
    val deleted = integer("deleted")
    val misc1 = integer("misc1")
    val misc2 = integer("misc2")
    override val primaryKey = PrimaryKey(id, name = "PK_User_ID")
}


object Table__File : Table() {
    val id = integer("id").autoIncrement()
    var name = varchar("name", 255)
    var content = text("content")
    val folderName = varchar("folderName", 255)
    val folderID = integer("folderID")
    val marked = bool("isStared")
    override val primaryKey = PrimaryKey(id, name = "PK_User_ID")
}

object Folders__Table : Table() {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 255)
    val parentID = integer("parentID")
    val parentFolder = varchar("parentName", 255)
    val marked = bool("isStared")
    override val primaryKey = PrimaryKey(id, name = "PK_User_ID")
}


@Serializable
data class FileItem(
    val id: Int,
    var name: String,
    var content: String,
    var folder: String,
    var marked: Boolean
)

@Serializable
data class FolderItem(
    val id: Int,
    var name: String,
    var parentFolderName: String,
    var marked: Boolean
)

@Serializable
data class TodoItemjson(
    val id: Int,
    val primaryTask: String,
    val secondaryTask: String,
    val priority: Int,
    val completed: Boolean,
    val datetime: String,
    val section: String,
    val duration: Int,
    val starttime: String,
    val recur: String,
    val pid: Int,
    val deleted: Int,
    val misc1: Int,
    val misc2: Int
)



// ********************

//object TodoTable : Table() {
//    val id = integer("id").autoIncrement()
//    val primaryTask = varchar("primaryTask", 255)
//    val secondaryTask = varchar("secondaryTask", 255)
//    val priority = integer("priority")
//    val completed = bool("completed")
//    val datetime = varchar("datetime", 255)
//    val section = varchar("section", 255)
//    val duration = integer("duration")
//    val starttime = varchar("starttime", 255)
//    val recur = varchar("recur", 255)
//    val pid = integer("pid")
//    val deleted = integer("deleted")
//    val misc1 = integer("misc1")
//    val misc2 = integer("misc2")
//    override val primaryKey = PrimaryKey(id, name = "PK_User_ID")
//}

//data class TodoItem(
//    val id: Int,
//    val primaryTask: String,
//    val secondaryTask: String,
//    val priority: Int,
//    var completed: Boolean,
//    val section: String,
//    var date_time: String,
//    val start_time: String,
//    val duration: String,
//    var recur: String,
//    var pid: Int,
//    val deleted: Int,
//    val misc1: Int,
//    val misc2: Int
//)
