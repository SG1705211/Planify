package com.example.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import net.codebot.models.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction


fun Application.configureRouting(testing: Boolean = false) {
    // set the url
//    val potential_url = if (testing) "jdbc:sqlite:test.db" else "jdbc:sqlite:/app/test.db"
    val potential_url = if (testing) "jdbc:sqlite:test.db" else "jdbc:sqlite:chinook.db"
    Database.connect(potential_url, "org.sqlite.JDBC")
//         Database.connect("jdbc:sqlite:/app/test.db", "org.sqlite.JDBC")

//    Database.connect("jdbc:sqlite:test.db", "org.sqlite.JDBC")

    routing {
        post("/Reset") {
            transaction {
                SchemaUtils.createMissingTablesAndColumns(TodoTable)
                TodoTable.deleteAll()
                Table__File.deleteAll()
                Folders__Table.deleteAll()
                TodoTable.insert {
                    it[id] = 0
                    it[primaryTask] = "Impress Orange"
                    it[secondaryTask] = "Impress Orange"
                    it[priority] = 1
                    it[starttime] = "10:00"
                    it[completed] = true
                    it[section] = "Work"
                    it[duration] = 3
                    it[datetime] = "20231126"
                    it[recur] = "None"
                    it[pid] = 13
                    it[deleted] = 0
                    it[misc1] = 0
                    it[misc2] = 0
                }
                TodoTable.insert {
                    it[id] = 1
                    it[primaryTask] = "Write report"
                    it[secondaryTask] = "Due next week"
                    it[priority] = 1
                    it[completed] = false
                    it[datetime] = "20231030"
                    it[section] = "Work"
                    it[duration] = 3
                    it[starttime] = "08:00"
                    it[recur] = "None"
                    it[pid] = 0
                    it[deleted] = 0
                    it[misc1] = 0
                    it[misc2] = 0
                }
                TodoTable.insert {
                    it[id] = 2
                    it[primaryTask] = "Email client"
                    it[secondaryTask] = "Urgent"
                    it[priority] = 2
                    it[completed] = false
                    it[starttime] = "08:00"
                    it[section] = "Work"
                    it[duration] = 3
                    it[datetime] = "20231029"
                    it[recur] = "None"
                    it[pid] = 0
                    it[deleted] = 0
                    it[misc1] = 0
                    it[misc2] = 0
                }

                // Study section
                TodoTable.insert {
                    it[id] = 3
                    it[primaryTask] = "Study for exam"
                    it[secondaryTask] = "Chapter 1-5"
                    it[priority] = 1
                    it[completed] = false
                    it[starttime] = "08:00"
                    it[section] = "Study"
                    it[duration] = 3
                    it[datetime] = "20231030"
                    it[recur] = "None"
                    it[pid] = 0
                    it[deleted] = 0
                    it[misc1] = 0
                    it[misc2] = 0
                }

                TodoTable.insert {
                    it[id] = 4
                    it[primaryTask] = "Complete assignment"
                    it[secondaryTask] = "Submit online"
                    it[priority] = 2
                    it[completed] = false
                    it[starttime] = "08:00"
                    it[section] = "Study"
                    it[duration] = 3
                    it[datetime] = "20231030"
                    it[recur] = "None"
                    it[pid] = 0
                    it[deleted] = 0
                    it[misc1] = 0
                    it[misc2] = 0
                }

                Table__File.insert {
                    it[id] = 2
                    it[name] = "Test File 1"
                    it[content] = "<p style=\"text-align: left;\">Sherlock is beautiful</p>"
                    it[folderName] = "Test 2"
                    it[folderID] = 0
                    it[marked] = false
                }

                Table__File.insert {
                    it[id] = 3
                    it[name] = "Test File 2"
                    it[content] = "<p style=\"text-align: left;\">Sherlock is not beautiful</p>"
                    it[folderName] = "Test Folder 2"
                    it[folderID] = 0
                    it[marked] = false
                }

                Folders__Table.insert {
                    it[id] = 2
                    it[name] = "Test Folder 1"
                    it[parentFolder] = ""
                    it[marked] = false
                    it[parentID] = 0
                }

                Folders__Table.insert {
                    it[id] = 3
                    it[name] = "Test Folder 3"
                    it[parentFolder] = ""
                    it[marked] = false
                    it[parentID] = 0
                }

                Folders__Table.insert {
                    it[id] = 4
                    it[name] = "Test Folder 4"
                    it[parentFolder] = ""
                    it[marked] = false
                    it[parentID] = 0
                }
                Folders__Table.insert {
                    it[id] = 5
                    it[name] = "Test 2"
                    it[parentFolder] = "Test Folder 2"
                    it[marked] = false
                    it[parentID] = 0
                }



            }
        }
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
            } else {
                var result = transaction {
                    TodoTable.update({ TodoTable.id eq todoId }) {
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
                    }
                }
                call.respond(HttpStatusCode.OK, "Todo updated successfully")
            }
        }


        delete("/todos/{id}") {
            val todoId = call.parameters["id"]?.toIntOrNull()
            if (todoId == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid ID")
            } else {
                val isDeleted = transaction {
                    TodoTable.deleteWhere { TodoTable.id eq todoId }
                }
                call.respond(HttpStatusCode.OK, "Good")
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
            } else {
                transaction {
                    Folders__Table.deleteWhere { Folders__Table.id eq todoId }
                    Table__File.deleteWhere { Table__File.id eq todoId }
                }
                call.respond(HttpStatusCode.OK, "Good")
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
