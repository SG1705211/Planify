import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import io.ktor.client.request.*
import io.ktor.http.*
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import org.jetbrains.exposed.sql.*
import io.ktor.client.engine.cio.*
import kotlinx.serialization.Serializable
import io.ktor.client.HttpClient
import io.ktor.client.statement.*
import io.ktor.util.*
import kotlinx.coroutines.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.Table
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

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
    override val primaryKey = PrimaryKey(id, name = "PK_User_ID")
}


data class TodoItem(
    val id: Int,
    val primaryTask: String,
    val secondaryTask: String,
    val priority: Int,
    var completed: Boolean,
    val section: String,
    val date_time: String,
    val start_time: String,
    val duration: String,
    val recur: String
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
    val recur: String
)

fun TodoItem.toTodoItemJson(): TodoItemjson {
    return TodoItemjson(
        id = this.id,
        primaryTask = this.primaryTask,
        secondaryTask = this.secondaryTask,
        priority = this.priority,
        completed = this.completed,
        datetime = this.date_time,
        section = this.section,
        duration = this.duration.toIntOrNull() ?: 0, // Converts String to Int, defaulting to 0 if conversion fails
        starttime = this.start_time,
        recur = this.recur
    )
}

enum class RecurOption {
    None, Daily, Monthly
}
@Composable
fun CreateTodoDialog(onCreate: (TodoItem) -> Unit, onClose: () -> Unit) {
    var primaryTask by remember { mutableStateOf("") }
    var secondaryTask by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf(1) }
    var section by remember { mutableStateOf("") }
    var dueDate by remember { mutableStateOf("") }
    var start_time by remember { mutableStateOf("") }
    var isDateValid by remember { mutableStateOf(true) }
    var areFieldsValid by remember { mutableStateOf(true) }
    var duration_in by remember { mutableStateOf("") }

    var recurOption by remember { mutableStateOf(RecurOption.None) }
    AlertDialog(onDismissRequest = { /* dismiss dialog */ }, title = {
        Text(text = "Create New Todo Item")
    }, text = {
        Column {
            TextField(value = primaryTask, onValueChange = { primaryTask = it }, label = { Text("Primary Task") })
            TextField(value = secondaryTask, onValueChange = { secondaryTask = it }, label = { Text("Secondary Task") })
            TextField(value = priority.toString(),
                onValueChange = { priority = it.toIntOrNull() ?: 1 },
                label = { Text("Priority") },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
            )
            TextField(value = section, onValueChange = { section = it }, label = { Text("Section") })
            TextField(value = start_time, onValueChange = { start_time = it }, label = { Text("Start Time (HH:MM)") })

            TextField(value = duration_in,
                onValueChange = { duration_in = it },
                label = { Text("Duration (in Hours)") })

            TextField(value = dueDate, onValueChange = { dueDate = it }, label = { Text("Due Date (yyyy-MM-dd)") })
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Recur")
                // Radio button for 'Daily'
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(selected = recurOption == RecurOption.Daily, onClick = { recurOption = RecurOption.Daily })
                    Text("Daily")
                }
                // Radio button for 'Monthly'
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(selected = recurOption == RecurOption.Monthly, onClick = { recurOption = RecurOption.Monthly })
                    Text("Monthly")
                }
            }
            // Error messages appear here, inside the AlertDialog
            if (!isDateValid) {
                Text("Invalid date format", color = Color.Red)
            }
            if (!areFieldsValid) {
                Text("All fields are required", color = Color.Red)
            }
        }
    }, confirmButton = {
        Button(onClick = {
            isDateValid = validateDate(dueDate)
            areFieldsValid =
                primaryTask.isNotEmpty() && secondaryTask.isNotEmpty() && section.isNotEmpty() && isDateValid
            if (areFieldsValid) {
                var tem_str = "None"
                if (recurOption == RecurOption.Daily) {
                    tem_str = "Daily"
                } else if (recurOption == RecurOption.Monthly) {
                    tem_str = "Monthly"
                }
                onCreate(
                    TodoItem(
                        id = 0, // Assuming your DB auto-increments IDs
                        primaryTask = primaryTask,
                        secondaryTask = secondaryTask,
                        priority = priority,
                        completed = false,
                        section = section,
                        date_time = dueDate,
                        duration = duration_in,
                        start_time = start_time,
                        recur = tem_str
                    )
                )
            }
        }) {
            Text("Create")
        }
    },
        // Add this part to introduce a close button
        dismissButton = {
            Button(onClick = {
                onClose() // Closes the dialog when clicked
            }) {
                Text("Close")
            }
        })
}

// Validates the given date string using a specific format
private fun validateDate(dateStr: String): Boolean {
    return try {
        val formatter = DateTimeFormatter.ofPattern("yyyyMMdd")
        LocalDate.parse(dateStr, formatter)
        true
    } catch (e: DateTimeParseException) {
        false
    }
}


@OptIn(InternalAPI::class)
suspend fun create(todoItem: TodoItem) {
    val client = HttpClient(CIO)
    val response: HttpResponse = client.post("http://localhost:8080/todos") {
        contentType(ContentType.Application.Json)
        println(Json.encodeToString(todoItem.toTodoItemJson()))
        body = Json.encodeToString(todoItem.toTodoItemJson())
    }
}

suspend fun fetchTodos(): List<TodoItemjson> {
    val client = HttpClient(CIO)
    val response: HttpResponse = client.get("http://localhost:8080/todos")
    val jsonString = response.bodyAsText()
    client.close()
    return Json.decodeFromString(jsonString)
}

suspend fun fetchTodosa(todoId: Int): TodoItemjson {
    val client = HttpClient(CIO)
    val response: HttpResponse = client.get("http://localhost:8080/todos/$todoId")
    val jsonString = response.bodyAsText()
    println(jsonString)
    client.close()
    return Json.decodeFromString(jsonString)
}


@OptIn(InternalAPI::class)
suspend fun updateTodoItem(todoId: Int, updatedTodo: TodoItem) {
    val client = HttpClient(CIO)
    val response: HttpResponse = client.post("http://localhost:8080/update/$todoId") {
        contentType(ContentType.Application.Json)
        println(Json.encodeToString(updatedTodo.toTodoItemJson()))
        body = Json.encodeToString(updatedTodo.toTodoItemJson())
    }
    // Handle the response
    println("Response status: ${response.status}")
    client.close()
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToDoList() {
    Database.connect("jdbc:sqlite:chinook.db")
    val (selectedSection, setSelectedSection) = remember { mutableStateOf("Work") }
    val todoListFromDb = remember { mutableStateListOf<TodoItem>() }
    LaunchedEffect(selectedSection) {
        var result: List<TodoItemjson>
        todoListFromDb.clear()
        runBlocking {
            launch {
                result = fetchTodos()
                result.forEach { jsonItem ->
                    if (jsonItem.section == selectedSection) {
                        todoListFromDb.add(
                            TodoItem(
                                id = jsonItem.id,
                                primaryTask = jsonItem.primaryTask,
                                secondaryTask = jsonItem.secondaryTask,
                                priority = jsonItem.priority,
                                completed = jsonItem.completed,
                                section = jsonItem.section,
                                date_time = jsonItem.datetime,
                                start_time = jsonItem.starttime,
                                duration = jsonItem.duration.toString(),
                                recur = jsonItem.recur
                            )
                        )
                    }
                }

            }
        }
    }
    var isDialogOpen by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier.fillMaxHeight().padding(top = 24.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxWidth().weight(0.1f)  // 10% of parent's height
            // other modifiers, content, etc.
        ) {
            Row() {
                val commonButtonModifier = Modifier.weight(1f).padding(14.dp).size(width = 150.dp, height = 1000.dp)
                OutlinedButton(
                    onClick = { setSelectedSection("Work") }, modifier = commonButtonModifier
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Work")
                    }

                }
                OutlinedButton(
                    onClick = { setSelectedSection("Study") }, modifier = commonButtonModifier
                ) {
                    Text(
                        text = "Study"
                    )
                }
                OutlinedButton(
                    onClick = { setSelectedSection("Hobby") }, modifier = commonButtonModifier
                ) {
                    Text("Hobby")
                }
                OutlinedButton(
                    onClick = { setSelectedSection("Life") }, modifier = commonButtonModifier
                ) {
                    Text("Life")
                }
            }
        }
        Box(
            modifier = Modifier.fillMaxWidth().weight(0.9f)  // 90% of parent's height
        ) {
            val triggerRecomposition = remember { mutableStateOf(false) }
            val sortedTodoList = todoListFromDb.sortedWith(compareBy<TodoItem> { it.completed }.thenBy { it.priority })
            LazyColumn() {
                todoListFromDb.forEachIndexed { index, todoItem ->
                    item {
                        ListItem(headlineContent = { Text(todoItem.primaryTask) },
                            supportingContent = { Text(todoItem.secondaryTask) },
                            trailingContent = { Text("Priority ${todoItem.priority}") },
                            leadingContent = {
                                Checkbox(checked = todoItem.completed, onCheckedChange = { isChecked ->
                                    todoListFromDb[index] = todoListFromDb[index].copy(completed = isChecked)
                                    var copy_todo = todoItem.copy()
                                    copy_todo.completed = isChecked
                                    runBlocking {
                                        launch {
                                            updateTodoItem(todoItem.id, copy_todo)
                                        }
                                    }
                                })
                            })
                        Divider()
                    }
                }
            }
            Box(
                modifier = Modifier.fillMaxSize(), // This will make the Box take up the entire available space
                contentAlignment = Alignment.BottomEnd // This will align its children to the bottom right corner
            ) {
                ExtendedFloatingActionButton(modifier = Modifier.padding(bottom = 16.dp, end = 16.dp),
                    onClick = { isDialogOpen = true }) {
                    Text(text = "Create New")
                }
            }
            if (isDialogOpen) {
                CreateTodoDialog(onClose = {
                    isDialogOpen = false
                }, onCreate = { newItem ->
                    isDialogOpen = false  // Close the dialog
                    runBlocking {
                        launch {
                            create(newItem)
                            var result: List<TodoItemjson>
                            todoListFromDb.clear()
                            result = fetchTodos()
                            result.forEach { jsonItem ->
                                if (jsonItem.section == selectedSection) {
                                    todoListFromDb.add(
                                        TodoItem(
                                            id = jsonItem.id,
                                            primaryTask = jsonItem.primaryTask,
                                            secondaryTask = jsonItem.secondaryTask,
                                            priority = jsonItem.priority,
                                            completed = jsonItem.completed,
                                            section = jsonItem.section,
                                            date_time = jsonItem.datetime,
                                            start_time = jsonItem.starttime,
                                            duration = jsonItem.duration.toString(),
                                            recur = jsonItem.recur
                                        )
                                    )
                                }
                            }
                        }
                    }
                })
            }
        }
    }
}