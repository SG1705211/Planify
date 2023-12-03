package com.example.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.*
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq


@Serializable
data class FileNamePara(val name: String, val folderName: String, val content: String, val id: Int)

@Serializable
data class TodoItem(
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

fun Application.configureRouting() {
    Database.connect("jdbc:sqlite:chinook.db", "org.sqlite.JDBC")
    //     Database.connect("jdbc:sqlite:/app/chinook.db", "org.sqlite.JDBC")
    //                Database.connect("jdbc:sqlite:chinook.db")
    //                transaction {
    //                    SchemaUtils.createMissingTablesAndColumns(TodoTable) // Create table if not exists
    //                    // Delete all existing records (Optional, if you want to start fresh)
    //                    TodoTable.deleteAll()
    //                    // Work section
    //                    TodoTable.insert {
    //                        it[primaryTask] = "Write report"
    //                        it[secondaryTask] = "Due next week"
    //                        it[priority] = 1
    //                        it[starttime] = "08:00"
    //                        it[completed] = false
    //                        it[section] = "Work"
    //                        it[duration] = 3
    //                        it[datetime] = "20231030"
    //                        it[recur] = "None"
    //                        it[pid] = 0
    //                        it[deleted] = 0
    //                        it[misc1] = 0
    //                        it[misc2] = 0
    //                    }
    //
    //                    TodoTable.insert {
    //                        it[primaryTask] = "Email client"
    //                        it[secondaryTask] = "Urgent"
    //                        it[priority] = 2
    //                        it[completed] = false
    //                        it[starttime] = "08:00"
    //                        it[section] = "Work"
    //                        it[duration] = 3
    //                        it[datetime] = "20231029"
    //                        it[recur] = "None"
    //                        it[pid] = 0
    //                        it[deleted] = 0
    //                        it[misc1] = 0
    //                        it[misc2] = 0
    //                    }
    //
    //                    // Study section
    //                    TodoTable.insert {
    //                        it[primaryTask] = "Study for exam"
    //                        it[secondaryTask] = "Chapter 1-5"
    //                        it[priority] = 1
    //                        it[completed] = false
    //                        it[starttime] = "08:00"
    //                        it[section] = "Study"
    //                        it[duration] = 3
    //                        it[datetime] = "20231030"
    //                        it[recur] = "None"
    //                        it[pid] = 0
    //                        it[deleted] = 0
    //                        it[misc1] = 0
    //                        it[misc2] = 0
    //                    }
    //
    //                    TodoTable.insert {
    //                        it[primaryTask] = "Complete assignment"
    //                        it[secondaryTask] = "Submit online"
    //                        it[priority] = 2
    //                        it[completed] = false
    //                        it[starttime] = "08:00"
    //                        it[section] = "Study"
    //                        it[duration] = 3
    //                        it[datetime] = "20231030"
    //                        it[recur] = "None"
    //                        it[pid] = 0
    //                        it[deleted] = 0
    //                        it[misc1] = 0
    //                        it[misc2] = 0
    //                    }
    //
    //                    // Hobby section
    //                    TodoTable.insert {
    //                        it[primaryTask] = "Learn guitar"
    //                        it[secondaryTask] = "Practice chords"
    //                        it[priority] = 3
    //                        it[completed] = false
    //                        it[starttime] = "08:00"
    //                        it[section] = "Hobby"
    //                        it[duration] = 3
    //                        it[datetime] = "20231030"
    //                        it[recur] = "None"
    //                        it[pid] = 0
    //                        it[deleted] = 0
    //                        it[misc1] = 0
    //                        it[misc2] = 0
    //                    }
    //
    //                    TodoTable.insert {
    //                        it[primaryTask] = "Go fishing"
    //                        it[secondaryTask] = "This weekend"
    //                        it[priority] = 4
    //                        it[completed] = false
    //                        it[starttime] = "08:00"
    //                        it[section] = "Hobby"
    //                        it[duration] = 3
    //                        it[datetime] = "20231030"
    //                        it[recur] = "None"
    //                        it[pid] = 0
    //                        it[deleted] = 0
    //                        it[misc1] = 0
    //                        it[misc2] = 0
    //                    }
    //
    //                    // Life section
    //                    TodoTable.insert {
    //                        it[primaryTask] = "Buy groceries"
    //                        it[secondaryTask] = "Fruits, Vegetables"
    //                        it[priority] = 3
    //                        it[completed] = false
    //                        it[starttime] = "08:00"
    //                        it[section] = "Life"
    //                        it[duration] = 3
    //                        it[datetime] = "20231030"
    //                        it[recur] = "None"
    //                        it[pid] = 0
    //                        it[deleted] = 0
    //                        it[misc1] = 0
    //                        it[misc2] = 0
    //                    }
    //
    //                    TodoTable.insert {
    //                        it[primaryTask] = "Call mom"
    //                        it[secondaryTask] = "Weekend catchup"
    //                        it[priority] = 4
    //                        it[completed] = false
    //                        it[starttime] = "08:00"
    //                        it[section] = "Life"
    //                        it[duration] = 1
    //                        it[datetime] = "20231028"
    //                        it[recur] = "None"
    //                        it[pid] = 0
    //                        it[deleted] = 0
    //                        it[misc1] = 0
    //                        it[misc2] = 0
    //                    }
    //                }
    routing {
        get("/") {
            call.respondText("Hello Sherlock")
        }
        get("/todos") {
            call.respond(
                transaction {
                    TodoTable.selectAll().map { row ->
                        TodoItem(
                            id = row[TodoTable.id],
                            primaryTask = row[TodoTable.primaryTask],
                            secondaryTask = row[TodoTable.secondaryTask],
                            priority = row[TodoTable.priority],
                            completed = row[TodoTable.completed],
                            datetime = row[TodoTable.datetime],
                            section = row[TodoTable.section],
                            duration = row[TodoTable.duration],
                            starttime = row[TodoTable.starttime],
                            recur = row[TodoTable.recur],
                            pid = row[TodoTable.pid],
                            deleted = row[TodoTable.deleted],
                            misc1 = row[TodoTable.misc1],
                            misc2 = row[TodoTable.misc2]
                        )
                    }
                }
            )
        }

        get("/todos/{id}") {
            val todoId = call.parameters["id"]?.toIntOrNull()
            if (todoId == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid todo ID")
            }
            else {
                transaction {
                    TodoTable.select { TodoTable.id eq todoId }.mapNotNull { it.toTodoItem() }.singleOrNull()
                }
            }
        }
        post("/todos") {
            val newTodo = call.receive<TodoItem>()
            val todoId = call.parameters["id"]?.toIntOrNull()
            val todo = transaction {
                TodoTable.insert {
                    it[primaryTask] = newTodo.primaryTask
                    it[secondaryTask] = newTodo.secondaryTask
                    it[priority] = newTodo.priority
                    it[completed] = newTodo.completed
                    it[datetime] = newTodo.datetime
                    it[section] = newTodo.section
                    it[duration] = newTodo.duration
                    it[starttime] = newTodo.starttime
                    it[recur] = newTodo.recur
                    it[pid] = newTodo.pid
                    it[deleted] = newTodo.deleted
                    it[misc1] = newTodo.misc1
                    it[misc2] = newTodo.misc2
                }.resultedValues?.singleOrNull()?.toTodoItem()
            }
            if (todo != null) {
                call.respond(HttpStatusCode.Created, todo)
            } else {
                call.respond(HttpStatusCode.InternalServerError, "Failed to create todo item")
            }
        }


        post("/notes") {
            val newItem = call.receive<FileItem>()
            val todoId = call.parameters["id"]?.toIntOrNull()
            val todo = transaction {
                Table__File.insert {
                    it[name] = newItem.name
                    it[content] = newItem.content
                    it[folderName] = newItem.folder
                    it[folderID] = 0
                    it[marked] = false
                }
            }
        }

        post("/Folder") {
            val newItem = call.receive<FolderItem>()
            val todoId = call.parameters["id"]?.toIntOrNull()
            val todo = transaction {
                Folders__Table.insert {
                    it[name] = newItem.name
                    it[parentFolder] = newItem.parentFolderName
                    it[parentID] = 0
                    it[marked] = false
                }
            }
        }
        post("/update/{id}") {
            val newTodo = call.receive<TodoItem>()
            val todoId = call.parameters["id"]?.toIntOrNull()
            if (todoId == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid todo ID")
            }else {
                transaction {
                    TodoTable.update({ TodoTable.id eq todoId }) {
                        it[completed] = newTodo.completed
                        it[recur] = newTodo.recur
                    }
                }
            }
        }

        post("/updateFile/{id}") {
            val requestData = call.receive<FileItem>()
            val content_tem = requestData.content
            val id = requestData.id
            // Perform the transaction
            val updateCount = transaction {
                Table__File.update({ (Table__File.id eq id) }) {
                    it[content] = content_tem
                }
            }
        }


        delete("/todos/{id}") {
            val todoId = call.parameters["id"]?.toIntOrNull()
            if (todoId == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid ID")
                return@delete
            }
            val isDeleted = transaction {
                TodoTable.deleteWhere { TodoTable.id eq todoId }
            }
        }


        delete("/notes/{id}") {
            val todoId = call.parameters["id"]?.toIntOrNull()
            if (todoId == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid ID")
            } else {
                transaction {
                    Table__File.deleteWhere { Table__File.id eq todoId }
                }
                call.respond(HttpStatusCode.OK, "Yes")
            }
        }



        delete("/folder/{id}") {
            val todoId = call.parameters["id"]?.toIntOrNull()
            if (todoId == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid ID")
            }
            else {
                transaction {
                    Folders__Table.deleteWhere { Folders__Table.id eq todoId }
                    Table__File.deleteWhere { Table__File.id eq todoId }
                }
            }

        }

        get("/notes_name") {
            call.respond(
                transaction {
                    Table__File.selectAll().map { row ->
                        FileItem(
                            id = row[Table__File.id],
                            content = row[Table__File.content],
                            folder = row[Table__File.folderName],
                            name = row[Table__File.name],
                            marked = row[Table__File.marked]
                        )
                    }
                }
            )
        }

        get("/folder_name") {
            call.respond(
                transaction {
                    Folders__Table.selectAll().map { row ->
                        FolderItem(
                            id = row[Folders__Table.id],
                            parentFolderName = row[Folders__Table.parentFolder],
                            name = row[Folders__Table.name],
                            marked = row[Folders__Table.marked]
                        )
                    }
                }
            )
        }
    }
}


fun ResultRow.toTodoItem() = TodoItem(
    id = this[TodoTable.id],
    primaryTask = this[TodoTable.primaryTask],
    secondaryTask = this[TodoTable.secondaryTask],
    priority = this[TodoTable.priority],
    completed = this[TodoTable.completed],
    datetime = this[TodoTable.datetime],
    section = this[TodoTable.section],
    duration = this[TodoTable.duration],
    starttime = this[TodoTable.starttime],
    recur = this[TodoTable.recur],
    pid = this[TodoTable.pid],
    deleted = this[TodoTable.deleted],
    misc1 = this[TodoTable.misc1],
    misc2 = this[TodoTable.misc2]
)
