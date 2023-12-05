
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.util.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.codebot.models.TodoItem
import net.codebot.models.TodoItemjson
import org.jetbrains.exposed.sql.Database
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException


fun TodoItem.toTodoItemJson(): TodoItemjson {
    return TodoItemjson(
        id = this.id,
        primaryTask = this.primaryTask,
        secondaryTask = this.secondaryTask,
        priority = this.priority,
        completed = this.completed,
        datetime = this.datetime,
        section = this.section,
        duration = this.duration,
        starttime = this.starttime,
        recur = this.recur,
        pid = this.pid,
        deleted = this.deleted,
        misc1 = this.misc1,
        misc2 = this.misc2
    )
}

enum class RecurOption {
    None, Daily, Weekly
}


@Composable
fun CreateTodoDialog(
    onCreate: (TodoItem) -> Unit,
    onClose: () -> Unit,
    defaultTodo: TodoItem
) {
    var create_or_edit by remember { mutableStateOf("Create") }
    val Formatter = DateTimeFormatter.ofPattern("yyyyMMdd")
    var primaryTask by remember { mutableStateOf("") }
    var secondaryTask by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf(1) }
    var section by remember { mutableStateOf("") }
    var dueDate by remember { mutableStateOf("") }
    var starttime by remember { mutableStateOf("") }
    var isDateValid by remember { mutableStateOf(true) }
    var areFieldsValid by remember { mutableStateOf(true) }
    var duration_in by remember { mutableStateOf("") }
    var recurOption by remember { mutableStateOf(RecurOption.None) }
    var recur_until by remember { mutableStateOf("0") }
    LaunchedEffect(defaultTodo) {
        if (defaultTodo.primaryTask != "This is a dummy variable") {
            create_or_edit = "Edit"
            primaryTask = defaultTodo.primaryTask
            secondaryTask = defaultTodo.secondaryTask
            priority = defaultTodo.priority
            section = defaultTodo.section
            dueDate = defaultTodo.datetime
            starttime = defaultTodo.starttime
            isDateValid = true
            areFieldsValid = true
            duration_in = defaultTodo.duration.toString()
            recurOption = when (defaultTodo.recur) {
                "Weekly" -> RecurOption.Weekly
                "Daily" -> RecurOption.Daily
                else -> RecurOption.None
            }
            recur_until = defaultTodo.misc1.toString()
        }
    }
    AlertDialog(onDismissRequest = {}, title = {
        Text(text = "$create_or_edit New Todo Item")
    }, text = {
        Column {
            TextField(value = primaryTask, onValueChange = { primaryTask = it }, label = { Text("Primary Task") })
            TextField(value = secondaryTask, onValueChange = { secondaryTask = it }, label = { Text("Secondary Task") })
            TextField(
                value = priority.toString(),
                onValueChange = { priority = it.toIntOrNull() ?: 1 },
                label = { Text("Priority") },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
            )
            TextField(value = section, onValueChange = { section = it }, label = { Text("Section") })
            TextField(value = starttime, onValueChange = { starttime = it }, label = { Text("Start Time (HH:MM)") })
            TextField(value = duration_in,
                onValueChange = { duration_in = it },
                label = { Text("Duration (in Hours)") })
            TextField(value = dueDate, onValueChange = { dueDate = it }, label = { Text("Due Date (yyyy-MM-dd)") })
            TextButton(onClick = {
                dueDate = LocalDate.now().format(Formatter)
            }) {
                Text("Today")
            }


            Text("Repeat Option")
            Row(verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = recurOption == RecurOption.Daily,
                        onClick = { recurOption = RecurOption.Daily }
                    )
                    Text("Daily")
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = recurOption == RecurOption.Weekly,
                        onClick = { recurOption = RecurOption.Weekly }
                    )
                    Text("Weekly")
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = recurOption == RecurOption.None,
                        onClick = { recurOption = RecurOption.None }
                    )
                    Text("None")
                }

            }
            if (recurOption == RecurOption.Daily || recurOption == RecurOption.Weekly) {
                TextField(
                    value = recur_until,
                    onValueChange = { recur_until = it },
                    label = { Text("Repeat Until: (yyyy-MM-DD)") }
                )
            }

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
                } else if (recurOption == RecurOption.Weekly) {
                    tem_str = "Weekly"
                }
                onCreate(
                    TodoItem(
                        id = 0,
                        primaryTask = primaryTask,
                        secondaryTask = secondaryTask,
                        priority = priority,
                        completed = defaultTodo.completed,
                        section = section,
                        datetime = dueDate,
                        duration = duration_in.toInt(),
                        starttime = starttime,
                        recur = tem_str,
                        deleted = 0,
                        pid = 0,
                        misc1 = recur_until.toInt(),
                        misc2 = 0
                    )
                )
            }
        }) {
            Text(create_or_edit)
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
    var if_update by remember { mutableStateOf(false) }
    var if_create by remember { mutableStateOf(false) }
    var currentid by remember {
        mutableStateOf(
            TodoItem(
                0, "Initial", "initial", 0, false, "test", "test", 3, "test", "test", 0, 0, 0, 0
            )
        )
    }
    LaunchedEffect(selectedSection, selectedDate) {
        var result: List<TodoItemjson>
        todoListFromDb.clear()
        runBlocking {
            launch {
                result = fetchTodos()
                result.forEach { jsonItem ->
                    if (jsonItem.section == selectedSection && jsonItem.datetime == selectedDate.format(formatter) && jsonItem.recur != "Daily" && jsonItem.recur != "Weekly") {
                        todoListFromDb.add(
                            TodoItem(
                                id = jsonItem.id,
                                primaryTask = jsonItem.primaryTask,
                                secondaryTask = jsonItem.secondaryTask,
                                priority = jsonItem.priority,
                                completed = jsonItem.completed,
                                section = jsonItem.section,
                                datetime = jsonItem.datetime,
                                starttime = jsonItem.starttime,
                                duration = jsonItem.duration,
                                recur = jsonItem.recur,
                                pid = jsonItem.pid,
                                deleted = jsonItem.deleted,
                                misc1 = jsonItem.misc1,
                                misc2 = jsonItem.misc2
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
                                    datetime = jsonItem.datetime,
                                    starttime = jsonItem.starttime,
                                    duration = jsonItem.duration,
                                    recur = jsonItem.recur,
                                    pid = jsonItem.pid,
                                    deleted = jsonItem.deleted,
                                    misc1 = jsonItem.misc1,
                                    misc2 = jsonItem.misc2
                                )
                            )
                        }
                    } else if (jsonItem.recur == "Weekly" && jsonItem.section == selectedSection && LocalDate.parse(
                            jsonItem.datetime, formatter
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
                                    datetime = jsonItem.datetime,
                                    starttime = jsonItem.starttime,
                                    duration = jsonItem.duration,
                                    recur = jsonItem.recur,
                                    pid = jsonItem.pid,
                                    deleted = jsonItem.deleted,
                                    misc1 = jsonItem.misc1,
                                    misc2 = jsonItem.misc2
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
            modifier = Modifier.fillMaxWidth().weight(0.08f)
        ) {
            Row() {
                val commonButtonModifier = Modifier.padding(14.dp).size(width = 240.dp, height = 40.dp)
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
            LazyColumn() {
                todoListFromDb.forEachIndexed { index, todoItem ->
                    item {
                        ListItem(headlineContent = { Text(todoItem.primaryTask) },
                            supportingContent = { Text(todoItem.secondaryTask) },
                            trailingContent = {
                                Column() { //                                    Text("Priority ${todoItem.priority}")
                                    TextButton(
                                        onClick = {
                                            currentid = todoItem.copy()
                                            if_update = true
                                            isDialogOpen = true
                                        }, modifier = Modifier.size(width = 100.dp, height = 35.dp)
                                    ) {
                                        Text("Edit", style = TextStyle(fontSize = 13.sp))
                                    }
                                    TextButton(
                                        onClick = {
                                            runBlocking {
                                                launch {
                                                    deleteTodo(todoItem.id)
                                                }
                                            }
                                        }, modifier = Modifier.size(width = 100.dp, height = 35.dp)
                                    ) {
                                        Text("Delete", style = TextStyle(fontSize = 13.sp))
                                    }
                                }
                            },
                            leadingContent = {
                                Checkbox(checked = todoItem.completed, onCheckedChange = { isChecked ->
                                    if (todoItem.recur != "Daily" && todoItem.recur != "Weekly") {
                                        todoListFromDb[index] = todoListFromDb[index].copy(completed = isChecked)
                                        var copy_todo = todoItem.copy()
                                        copy_todo.completed = isChecked
                                        runBlocking {
                                            launch {
                                                println(updateTodoItem(todoItem.id, copy_todo))
                                            }
                                        }
                                    } else {
                                        var copy_of_copy = todoListFromDb[index].copy()
                                        copy_of_copy.datetime = selectedDate.format(formatter)
                                        copy_of_copy.recur = "None"
                                        copy_of_copy.completed = true
                                        copy_of_copy.pid = todoItem.id
                                        runBlocking {
                                            launch {
                                                create(copy_of_copy)
                                            }
                                        } // update fetched data
                                        runBlocking {
                                            var result: List<TodoItemjson> = fetchTodos()
                                            launch {
                                                result = fetchTodos()
                                                todoListFromDb.clear()
                                                result.forEach { jsonItem ->
                                                    if (jsonItem.section == selectedSection && jsonItem.datetime == selectedDate.format(
                                                            formatter
                                                        ) && jsonItem.recur != "Daily" && jsonItem.recur != "Weekly"
                                                    ) {
                                                        todoListFromDb.add(
                                                            TodoItem(
                                                                id = jsonItem.id,
                                                                primaryTask = jsonItem.primaryTask,
                                                                secondaryTask = jsonItem.secondaryTask,
                                                                priority = jsonItem.priority,
                                                                completed = jsonItem.completed,
                                                                section = jsonItem.section,
                                                                datetime = jsonItem.datetime,
                                                                starttime = jsonItem.starttime,
                                                                duration = jsonItem.duration,
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
                                                                    datetime = jsonItem.datetime,
                                                                    starttime = jsonItem.starttime,
                                                                    duration = jsonItem.duration,
                                                                    recur = jsonItem.recur,
                                                                    pid = jsonItem.pid,
                                                                    deleted = jsonItem.deleted,
                                                                    misc1 = 0,
                                                                    misc2 = 0
                                                                )
                                                            )
                                                        }
                                                    } else if (jsonItem.recur == "Weekly" && jsonItem.section == selectedSection && LocalDate.parse(
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
                                                                    datetime = jsonItem.datetime,
                                                                    starttime = jsonItem.starttime,
                                                                    duration = jsonItem.duration,
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
                ExtendedFloatingActionButton(modifier = Modifier.padding(bottom = 16.dp, end = 16.dp), onClick = {
                    isDialogOpen = true
                    if_create = true
                }) {
                    Text(text = "Create New")
                }
            }
            if (isDialogOpen) {
                var tem_todo = TodoItem(
                    0, "This is a dummy variable", "", 0, false, "", "", 0, "", "", 0, 0, 0, 0
                )
                var tem_todo_reset = TodoItem(
                    0, "This is a dummy variable", "", 0, false, "", "", 0, "", "", 0, 0, 0, 0
                )
                if (if_update) {
                    tem_todo = currentid.copy()
                    println("2")
                }
                CreateTodoDialog(onClose = {
                    isDialogOpen = false
                    if (if_create) {
                        if_create = false
                    } else if (if_update) {
                        if_update = false
                        tem_todo = tem_todo_reset.copy()
                    }
                }, onCreate = { newItem ->
                    isDialogOpen = false
                    runBlocking {
                        launch {
                            if (if_create) {
                                create(newItem)
                                if_create = false
                            } else if (if_update) {
                                updateTodoItem(currentid.id, newItem.copy())
                                println(currentid.id)
                                if_update = false
                                tem_todo = tem_todo_reset.copy()
                            }
                            todoListFromDb.clear()
                            var result: List<TodoItemjson> = fetchTodos()
                            result.forEach { jsonItem ->
                                if (jsonItem.section == selectedSection && jsonItem.datetime == selectedDate.format(
                                        formatter
                                    ) && jsonItem.recur != "Daily" && jsonItem.recur != "Weekly"
                                ) {
                                    todoListFromDb.add(
                                        TodoItem(
                                            id = jsonItem.id,
                                            primaryTask = jsonItem.primaryTask,
                                            secondaryTask = jsonItem.secondaryTask,
                                            priority = jsonItem.priority,
                                            completed = jsonItem.completed,
                                            section = jsonItem.section,
                                            datetime = jsonItem.datetime,
                                            starttime = jsonItem.starttime,
                                            duration = jsonItem.duration,
                                            recur = jsonItem.recur,
                                            pid = jsonItem.pid,
                                            deleted = jsonItem.deleted,
                                            misc1 = jsonItem.misc1,
                                            misc2 = jsonItem.misc2
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
                                                datetime = jsonItem.datetime,
                                                starttime = jsonItem.starttime,
                                                duration = jsonItem.duration,
                                                recur = jsonItem.recur,
                                                pid = jsonItem.pid,
                                                deleted = jsonItem.deleted,
                                                misc1 = jsonItem.misc1,
                                                misc2 = jsonItem.misc2
                                            )
                                        )
                                    }
                                } else if (jsonItem.recur == "Weekly" && jsonItem.section == selectedSection && LocalDate.parse(
                                        jsonItem.datetime, formatter
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
                                                datetime = jsonItem.datetime,
                                                starttime = jsonItem.starttime,
                                                duration = jsonItem.duration,
                                                recur = jsonItem.recur,
                                                pid = jsonItem.pid,
                                                deleted = jsonItem.deleted,
                                                misc1 = jsonItem.misc1,
                                                misc2 = jsonItem.misc2
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }, defaultTodo = tem_todo)
            }
        }
    }
}