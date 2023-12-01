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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
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
import androidx.compose.foundation.clickable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp

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


data class TodoItem(
    val id: Int,
    val primaryTask: String,
    val secondaryTask: String,
    val priority: Int,
    var completed: Boolean,
    val section: String,
    var date_time: String,
    val start_time: String,
    val duration: String,
    var recur: String,
    var pid: Int,
    val deleted: Int,
    val misc1: Int,
    val misc2: Int
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
        recur = this.recur,
        pid = this.pid,
        deleted = this.deleted,
        misc1 = this.misc1,
        misc2 = this.misc2
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
                    RadioButton(
                        selected = recurOption == RecurOption.Daily,
                        onClick = { recurOption = RecurOption.Daily })
                    Text("Daily")
                }
                // Radio button for 'Monthly'
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = recurOption == RecurOption.Monthly,
                        onClick = { recurOption = RecurOption.Monthly })
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
                        id = 0,
                        primaryTask = primaryTask,
                        secondaryTask = secondaryTask,
                        priority = priority,
                        completed = false,
                        section = section,
                        date_time = dueDate,
                        duration = duration_in,
                        start_time = start_time,
                        recur = tem_str,
                        deleted = 0,
                        pid = 0,
                        misc1 = 0,
                        misc2 = 0
                    )
                )
            }
        }) {
            Text("Create")
        }
    }, dismissButton = {
        Button(onClick = {
            onClose()
        }) {
            Text("Close")
        }
    })
}

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
    println(jsonString)
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
        body = Json.encodeToString(updatedTodo.toTodoItemJson())
    }
    client.close()
}

@OptIn(InternalAPI::class)
suspend fun deleteTodo(todoId: Int) {
    val client = HttpClient(CIO)
    val response: HttpResponse = client.delete("http://localhost:8080/todos/$todoId") {
        contentType(ContentType.Application.Json)
    }
    client.close()
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToDoList() {
    Database.connect("jdbc:sqlite:chinook.db")
    val currentDate = LocalDate.now()
    var month by remember { mutableStateOf(currentDate.monthValue) }
    var year by remember { mutableStateOf(currentDate.year) }
    val formatter = DateTimeFormatter.ofPattern("yyyyMMdd")
    var date by remember { mutableStateOf(currentDate.dayOfMonth) }
    val maxDateInMonth = LocalDate.of(year, month, 1).lengthOfMonth()
    if (date > maxDateInMonth) {
        date = maxDateInMonth
    }
    var selectedDate by mutableStateOf(LocalDate.of(year, month, date))
    val monthNames = listOf(
        "January",
        "February",
        "March",
        "April",
        "May",
        "June",
        "July",
        "August",
        "September",
        "October",
        "November",
        "December"
    )
    val current_month = monthNames[month - 1]
    val (selectedSection, setSelectedSection) = remember { mutableStateOf("Work") }
    var todoListFromDb = remember { mutableStateListOf<TodoItem>() }
    LaunchedEffect(selectedSection, selectedDate) {
        var result: List<TodoItemjson>
        todoListFromDb.clear()
        // update fetched data
        runBlocking {
            launch {
                result = fetchTodos()

                result.forEach { jsonItem ->
                    if (jsonItem.section == selectedSection && jsonItem.datetime == selectedDate.format(formatter) && jsonItem.recur != "Daily" && jsonItem.recur != "Monthly") {
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
                                recur = jsonItem.recur,
                                pid = jsonItem.pid,
                                deleted = jsonItem.deleted,
                                misc1 = 0,
                                misc2 = 0
                            )
                        )
                    }
                }
                result.forEach { jsonItem ->
                    if (jsonItem.section == selectedSection && jsonItem.recur == "Daily") {
                        val duplicate_item = todoListFromDb.find { it.pid == jsonItem.id }
                        if (duplicate_item == null) {
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
                                    recur = jsonItem.recur,
                                    pid = jsonItem.pid,
                                    deleted = jsonItem.deleted,
                                    misc1 = 0,
                                    misc2 = 0
                                )
                            )
                        }
                    } else if (jsonItem.recur == "Monthly" && jsonItem.section == selectedSection && LocalDate.parse(
                            jsonItem.datetime,
                            formatter
                        ).dayOfWeek == selectedDate.dayOfWeek
                    ) {
                        val duplicate_item = todoListFromDb.find { it.pid == jsonItem.id }
                        if (duplicate_item == null) {
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
                                    recur = jsonItem.recur,
                                    pid = jsonItem.pid,
                                    deleted = jsonItem.deleted,
                                    misc1 = 0,
                                    misc2 = 0
                                )
                            )
                        }
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
            modifier = Modifier.fillMaxWidth().weight(0.05f)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "$date $current_month $year",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f, fill = false)
                )

                Row(modifier = Modifier.weight(1f, fill = false)) {
                    Spacer(Modifier.weight(0.9f))
                    Text("Today", modifier = Modifier.clickable {
                        val today = LocalDate.now()
                        date = today.dayOfMonth
                        month = today.monthValue
                        year = today.year
                        selectedDate = today
                    })
                    Spacer(Modifier.weight(0.1f))
                    FilledTonalButton(
                        onClick = {
                            if (date > 1) {
                                date--
                            } else {
                                if (month > 1) {
                                    month--
                                    date = LocalDate.of(year, month, 1).lengthOfMonth()

                                } else {
                                    month = 12
                                    year--
                                    date = 31
                                }
                                selectedDate = LocalDate.of(year, month, date)
                            }
                        }, modifier = Modifier.size(70.dp, 30.dp)
                    ) {
                        androidx.compose.material.Text("<")
                    }
                    FilledTonalButton(
                        onClick = {
                            if (date < selectedDate.lengthOfMonth()) {
                                date++
                            } else {
                                if (month < 12) {
                                    month++
                                    date = 1
                                } else {
                                    month = 1
                                    date = 1
                                    year++
                                }
                            }
                            selectedDate = LocalDate.of(year, month, date)
                        }, modifier = Modifier.size(70.dp, 30.dp)
                    ) {
                        androidx.compose.material.Text(">")
                    }
                }
            }
        }

        Box(
            modifier = Modifier.fillMaxWidth().weight(0.08f)  // 10% of parent's height
        ) {
            Row() {
                val commonButtonModifier =
                    Modifier.padding(14.dp).size(width = 240.dp, height = 40.dp) // Adjust the size as needed

                // Wrapping each button with a Box to center it and apply the same weight
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    OutlinedButton(
                        onClick = { setSelectedSection("Work") }, modifier = commonButtonModifier
                    ) {
                        Text("Work")
                    }
                }
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    OutlinedButton(
                        onClick = { setSelectedSection("Study") }, modifier = commonButtonModifier
                    ) {
                        Text("Study")
                    }
                }
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    OutlinedButton(
                        onClick = { setSelectedSection("Hobby") }, modifier = commonButtonModifier
                    ) {
                        Text("Hobby")
                    }
                }
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    OutlinedButton(
                        onClick = { setSelectedSection("Life") }, modifier = commonButtonModifier
                    ) {
                        Text("Life")
                    }
                }
            }
        }
        Box(
            modifier = Modifier.fillMaxWidth().weight(0.8f)
        ) {
            val sortedTodoList = todoListFromDb.sortedWith(compareBy<TodoItem> { it.completed }.thenBy { it.priority })
            LazyColumn() {
                todoListFromDb.forEachIndexed { index, todoItem ->
                    item {
                        ListItem(headlineContent = { Text(todoItem.primaryTask) },
                            supportingContent = { Text(todoItem.secondaryTask) },
                            trailingContent = {
                                Column() {
//                                    Text("Priority ${todoItem.priority}")
                                    TextButton(
                                        onClick = { setSelectedSection("Life") },
                                        modifier = Modifier.size(width = 50.dp, height = 35.dp)
                                    ) {
                                        Text("Edit", style = TextStyle(fontSize = 5.sp))
                                    }
                                    TextButton(
                                        onClick = {
                                            runBlocking {
                                                launch {
                                                    deleteTodo(todoItem.id)
                                                }
                                            }
                                        }, modifier = Modifier.size(width = 50.dp, height = 35.dp)
                                    ) {
                                        Text("Delete", style = TextStyle(fontSize = 13.sp))
                                    }
                                }
                            },
                            leadingContent = {
                                Checkbox(checked = todoItem.completed, onCheckedChange = { isChecked ->
                                    if (todoItem.recur != "Daily" && todoItem.recur != "Monthly") {
                                        todoListFromDb[index] = todoListFromDb[index].copy(completed = isChecked)
                                        var copy_todo = todoItem.copy()
                                        copy_todo.completed = isChecked
                                        runBlocking {
                                            launch {
                                                updateTodoItem(todoItem.id, copy_todo)
                                            }
                                        }
                                        if (!isChecked) {
                                            runBlocking {
                                                launch {
                                                    deleteTodo(copy_todo.id)
                                                }
                                            }
                                        }
                                    } else {
                                        var copy_of_copy = todoListFromDb[index].copy()
                                        copy_of_copy.date_time = selectedDate.format(formatter)
                                        copy_of_copy.recur = "None"
                                        copy_of_copy.completed = true
                                        copy_of_copy.pid = todoItem.id
                                        runBlocking {
                                            launch {
                                                create(copy_of_copy)
                                            }
                                        }
                                        // update fetched data
                                        runBlocking {
                                            var result: List<TodoItemjson> = fetchTodos()
                                            launch {
                                                result = fetchTodos()
                                                todoListFromDb.clear()
                                                result.forEach { jsonItem ->
                                                    if (jsonItem.section == selectedSection && jsonItem.datetime == selectedDate.format(
                                                            formatter
                                                        ) && jsonItem.recur != "Daily" && jsonItem.recur != "Monthly"
                                                    ) {
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
                                                                recur = jsonItem.recur,
                                                                pid = jsonItem.pid,
                                                                deleted = jsonItem.deleted,
                                                                misc1 = 0,
                                                                misc2 = 0
                                                            )
                                                        )
                                                    }
                                                }
                                                result.forEach { jsonItem ->
                                                    if (jsonItem.section == selectedSection && jsonItem.recur == "Daily") {
                                                        val duplicate_item =
                                                            todoListFromDb.find { it.pid == jsonItem.id }
                                                        if (duplicate_item == null) {
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
                                                                    recur = jsonItem.recur,
                                                                    pid = jsonItem.pid,
                                                                    deleted = jsonItem.deleted,
                                                                    misc1 = 0,
                                                                    misc2 = 0
                                                                )
                                                            )
                                                        }
                                                    } else if (jsonItem.recur == "Monthly" && jsonItem.section == selectedSection && LocalDate.parse(
                                                            jsonItem.datetime, formatter
                                                        ).dayOfWeek == selectedDate.dayOfWeek
                                                    ) {
                                                        val duplicate_item =
                                                            todoListFromDb.find { it.pid == jsonItem.id }
                                                        if (duplicate_item == null) {
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
                                                                    recur = jsonItem.recur,
                                                                    pid = jsonItem.pid,
                                                                    deleted = jsonItem.deleted,
                                                                    misc1 = 0,
                                                                    misc2 = 0
                                                                )
                                                            )
                                                        }
                                                    }
                                                    
                                                }
                                            }
                                        }

                                    }

                                })
                            })
                        Divider()
                    }
                }
            }
            Box(
                modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomEnd
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
                    isDialogOpen = false
                    // update fetched data
                    runBlocking {
                        launch {
                            create(newItem)
                            todoListFromDb.clear()
                            var result: List<TodoItemjson> = fetchTodos()
                            result.forEach { jsonItem ->
                                if (jsonItem.section == selectedSection && jsonItem.datetime == selectedDate.format(
                                        formatter
                                    ) && jsonItem.recur != "Daily" && jsonItem.recur != "Monthly"
                                ) {
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
                                            recur = jsonItem.recur,
                                            pid = jsonItem.pid,
                                            deleted = jsonItem.deleted,
                                            misc1 = 0,
                                            misc2 = 0
                                        )
                                    )
                                }
                            }
                            result.forEach { jsonItem ->

                                if (jsonItem.section == selectedSection && jsonItem.recur == "Daily") {

                                    val duplicate_item = todoListFromDb.find { it.pid == jsonItem.id }
                                    if (duplicate_item == null) {
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
                                                recur = jsonItem.recur,
                                                pid = jsonItem.pid,
                                                deleted = jsonItem.deleted,
                                                misc1 = 0,
                                                misc2 = 0
                                            )
                                        )
                                    }
                                } else if (jsonItem.recur == "Monthly" && jsonItem.section == selectedSection && LocalDate.parse(
                                        jsonItem.datetime,
                                        formatter
                                    ).dayOfWeek == selectedDate.dayOfWeek
                                ) {
                                    val duplicate_item = todoListFromDb.find { it.pid == jsonItem.id }
                                    if (duplicate_item == null) {
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
                                                recur = jsonItem.recur,
                                                pid = jsonItem.pid,
                                                deleted = jsonItem.deleted,
                                                misc1 = 0,
                                                misc2 = 0
                                            )
                                        )
                                    }
                                }
                                
                            }
                        }
                    }
                })
            }
        }
    }
}