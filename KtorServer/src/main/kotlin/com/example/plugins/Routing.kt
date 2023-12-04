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
import net.codebot.models.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq


fun Application.configureRouting(testing: Boolean = false) {
    // set the url
    val potential_url = if (testing) "jdbc:sqlite:test.db" else "jdbc:sqlite:chinook.db"
    Database.connect(potential_url, "org.sqlite.JDBC")
    //     Database.connect("jdbc:sqlite:/app/chinook.db", "org.sqlite.JDBC")
    //                Database.connect("jdbc:sqlite:chinook.db")

    routing {
        get("/") {
            call.respondText("Connection Success")
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
//
//        get("/todos/{id}") {
//            val todoId = call.parameters["id"]?.toIntOrNull()
//            if (todoId == null) {
//                call.respond(HttpStatusCode.BadRequest, "Invalid todo ID")
//            }
//            else {
//                transaction {
//                    TodoTable.select { TodoTable.id eq todoId }.mapNotNull { it.toTodoItem() }.singleOrNull()
//                }
//            }
//            call.respond(HttpStatusCode.OK, "Good")
//        }
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
            call.respond(HttpStatusCode.Created, "Okay")
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
            call.respond(HttpStatusCode.OK, "Good")
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
            call.respond(HttpStatusCode.OK, "Good")
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
            call.respond(HttpStatusCode.OK, "Good")
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


        delete("/todos/{id}") {
            val todoId = call.parameters["id"]?.toIntOrNull()
            if (todoId == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid ID")
            }
            else {
                val isDeleted = transaction {
                    TodoTable.deleteWhere { TodoTable.id eq todoId }
                }
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
