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
import kotlinx.serialization.builtins.ListSerializer

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
    val starttime: String
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
    override val primaryKey = PrimaryKey(id, name = "PK_User_ID")
}


fun Application.configureRouting() {
    Database.connect("jdbc:sqlite:chinook.db", "org.sqlite.JDBC")
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
                            starttime = row[TodoTable.starttime]
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
                }
            }
            if (todo != null) {
                call.respond(HttpStatusCode.Created, todo)
            } else {
                call.respond(HttpStatusCode.InternalServerError, "Failed to create todo item")
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
    starttime = this[TodoTable.starttime]
)
