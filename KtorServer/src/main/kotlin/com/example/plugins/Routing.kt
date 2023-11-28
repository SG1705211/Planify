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
    val deleted:Int,
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


fun Application.configureRouting() {
    Database.connect("jdbc:sqlite:chinook.db", "org.sqlite.JDBC")
//    Database.connect("jdbc:sqlite:/app/chinook.db", "org.sqlite.JDBC")
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
            call.respondText("a")
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
        // Get a single todo item by id
        get("/todos/{id}") {
            val todoId = call.parameters["id"]?.toIntOrNull()
            if (todoId == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid todo ID")
                return@get
            }
            val todo = transaction {
                TodoTable.select { TodoTable.id eq todoId }
                    .mapNotNull { it.toTodoItem() }
                    .singleOrNull()
            }
            if (todo == null) {
                call.respond(HttpStatusCode.NotFound, "Todo not found")
            } else {
                call.respond(todo)
            }
        }

        // Create a new todo item
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

// Update an existing todo item
        post("/update/{id}") {
            val newTodo = call.receive<TodoItem>()
            val todoId = call.parameters["id"]?.toIntOrNull()
            if (todoId == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid todo ID")
                return@post
            }
            val todo = transaction {
                TodoTable.update({ TodoTable.id eq todoId }) {
                    it[completed] = newTodo.completed
                    it[recur] = newTodo.recur
                }
            }
            if (todo != null) {
                call.respond(HttpStatusCode.Created, todo)
            } else {
                call.respond(HttpStatusCode.InternalServerError, "Failed to create todo item")
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
            if (isDeleted > 0) {
                call.respond(HttpStatusCode.OK, "Todo item deleted successfully")
            } else {
                call.respond(HttpStatusCode.NotFound, "Todo item not found")
            }
        }
    }
}

// Utility function to convert a ResultRow to a TodoItem
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
