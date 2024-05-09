package habits

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.util.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.codebot.models.TodoItem
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

fun detectValidDateTime(input: String): Boolean {
    val formatter = DateTimeFormatter.ofPattern("HH:mm")
    return try {
        LocalTime.parse(input, formatter)
        true
    } catch (e: DateTimeParseException) {
        false
    }
}

fun detectValidInteger(value: String): Boolean {
    return try {
        value.toInt()
        true
    } catch (e: NumberFormatException) {
        false
    }
}

fun dateIsInRange(date: String, start: String, end: String): Boolean {
    val formatter = DateTimeFormatter.ofPattern("yyyyMMdd")
    val targetDate = LocalDate.parse(date, formatter)
    val startDate = LocalDate.parse(start, formatter)
    val endDate = LocalDate.parse(end, formatter)
    return !targetDate.isBefore(startDate) && !targetDate.isAfter(endDate)
}

suspend fun deleteTodo(todoId: Int) {
    val client = HttpClient(CIO)
    val response: HttpResponse = client.delete("http://localhost:8080/todos/$todoId") {
        contentType(ContentType.Application.Json)
    }
    client.close()
}
@OptIn(InternalAPI::class)
suspend fun updateTodoItem(todoId: Int, updatedTodo: TodoItem) {
    val client = HttpClient(CIO)
    val response: HttpResponse = client.post("http://localhost:8080/update/$todoId") {
        contentType(ContentType.Application.Json)
        body = Json.encodeToString(updatedTodo)
    }
    client.close()
}

suspend fun fetchTodos(): List<TodoItem> {
    val client = HttpClient(CIO)
    val response: HttpResponse = client.get("http://localhost:8080/todos")
    val jsonString = response.bodyAsText()
    client.close()
    return Json.decodeFromString(jsonString)
}
@OptIn(InternalAPI::class)
suspend fun create(todoItem: TodoItem) {
    val client = HttpClient(CIO)
    val response: HttpResponse = client.post("http://localhost:8080/todos") {
        contentType(ContentType.Application.Json)
        body = Json.encodeToString(todoItem)
    }
}

fun validateDate(dateStr: String): Boolean {
    return try {
        val formatter = DateTimeFormatter.ofPattern("yyyyMMdd")
        LocalDate.parse(dateStr, formatter)
        true
    } catch (e: DateTimeParseException) {
        false
    }
}