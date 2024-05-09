package habits

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import net.codebot.models.TodoItem
import org.jetbrains.exposed.sql.Database

enum class RecurOption {
    None,
    Daily,
    Weekly
}

@Composable
fun createTodoDialog(onCreate: (TodoItem) -> Unit, onClose: () -> Unit, defaultTodo: TodoItem) {
    var dialogState by remember { mutableStateOf(DialogState()) }
    val formatter = DateTimeFormatter.ofPattern("yyyyMMdd")

    LaunchedEffect(defaultTodo) {
        dialogState = updateDialogStateBasedOnDefault(dialogState, defaultTodo)
    }

    AlertDialog(
        onDismissRequest = { onClose() },
        title = { Text(text = "${dialogState.createOrEdit} New Todo Item") },
        text = { todoDialogContent(dialogState, formatter) },
        confirmButton = { confirmButtons(dialogState, onCreate) },
        dismissButton = { Button(onClick = onClose) { Text("Close") } })
}

private fun updateDialogStateBasedOnDefault(
    state: DialogState,
    defaultTodo: TodoItem
): DialogState {
    return if (defaultTodo.primaryTask != "This is a dummy variable") {
        state.copy(
            createOrEdit = "Edit",
            primaryTask = defaultTodo.primaryTask,
            secondaryTask = defaultTodo.secondaryTask,
            priority = defaultTodo.priority,
            section = defaultTodo.section,
            dueDate = defaultTodo.datetime,
            starttime = defaultTodo.starttime,
            isDateValid = true,
            areFieldsValid = true,
            durationIn = defaultTodo.duration.toString(),
            recurOption =
                when (defaultTodo.recur) {
                    "Weekly" -> RecurOption.Weekly
                    "Daily" -> RecurOption.Daily
                    else -> RecurOption.None
                },
            recurUntil = defaultTodo.misc1.toString())
    } else state
}

@Composable
private fun todoDialogContent(state: DialogState, formatter: DateTimeFormatter) {
    Column {
        TextField(
            value = state.primaryTask,
            onValueChange = { state.primaryTask = it },
            label = { Text("Primary Task") })
        TextField(
            value = state.secondaryTask,
            onValueChange = { state.secondaryTask = it },
            label = { Text("Secondary Task") })
        TextField(
            value = state.priority.toString(),
            onValueChange = { state.priority = it.toIntOrNull() ?: 1 },
            label = { Text("Priority") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number))
        TextField(
            value = state.section,
            onValueChange = { state.section = it },
            label = { Text("Section") })
        TextField(
            value = state.starttime,
            onValueChange = { state.starttime = it },
            label = { Text("Start Time (HH:MM)") })
        TextField(
            value = state.durationIn,
            onValueChange = { state.durationIn = it },
            label = { Text("Duration (in Hours)") })
        TextField(
            value = state.dueDate,
            onValueChange = { state.dueDate = it },
            label = { Text("Due Date (yyyyMMdd)") })
        TextButton(onClick = { state.dueDate = LocalDate.now().format(formatter) }) {
            Text("Today")
        }
        Text("Repeat Option")
        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(
                selected = state.recurOption == RecurOption.Daily,
                onClick = { state.recurOption = RecurOption.Daily })
            Text("Daily")
            RadioButton(
                selected = state.recurOption == RecurOption.Weekly,
                onClick = { state.recurOption = RecurOption.Weekly })
            Text("Weekly")
            RadioButton(
                selected = state.recurOption == RecurOption.None,
                onClick = { state.recurOption = RecurOption.None })
            Text("None")
        }
        if (state.recurOption == RecurOption.Daily || state.recurOption == RecurOption.Weekly) {
            TextField(
                value = state.recurUntil,
                onValueChange = { state.recurUntil = it },
                label = { Text("Repeat Until: (yyyyMMdd)") })
        }

        // Display validation and error messages
        if (state.emptyInput) {
            Text("All fields are required", color = Color.Red)
        } else if (!state.isDateValid) {
            Text("Invalid date/time format", color = Color.Red)
        } else if (state.durationInvalid) {
            Text("Duration is invalid, exceeds current day", color = Color.Red)
        } else if (!state.sectionValid) {
            Text("Section must be Work, Study, Hobby or Life", color = Color.Red)
        }
    }
}

@Composable
private fun confirmButtons(state: DialogState, onCreate: (TodoItem) -> Unit) {
    Button(
        onClick = {
            onCreate(
                TodoItem(
                    id = 0,
                    primaryTask = state.primaryTask,
                    secondaryTask = state.secondaryTask,
                    priority = state.priority,
                    completed = false,
                    section = state.section,
                    datetime = state.dueDate,
                    duration = state.durationIn.toInt(),
                    starttime = state.starttime,
                    recur = state.recurOption.name,
                    deleted = 0,
                    pid = 0,
                    misc1 = state.recurUntil.toInt(),
                    misc2 = 0))
        }) {
            Text(state.createOrEdit)
        }
}

data class DialogState(
    var createOrEdit: String = "Create",
    var primaryTask: String = "",
    var secondaryTask: String = "",
    var priority: Int = 1,
    var section: String = "",
    var dueDate: String = "",
    var starttime: String = "",
    var isDateValid: Boolean = true,
    var durationInvalid: Boolean = false,
    var areFieldsValid: Boolean = true,
    var durationIn: String = "",
    var sectionValid: Boolean = true,
    var recurOption: RecurOption = RecurOption.None,
    var emptyInput: Boolean = false,
    var recurUntil: String = "0"
)

fun checkDateInMonth(year: Int, month: Int, date: Int): Int {
    val maxDateInMonth = LocalDate.of(year, month, 1).lengthOfMonth()
    return if (date > maxDateInMonth) maxDateInMonth else date
}

val monthNames = listOf(
    "January", "February", "March", "April", "May", "June",
    "July", "August", "September", "October", "November", "December"
)

suspend fun updateTodoListFromDb(todoList: SnapshotStateList<TodoItem>, selectedSection: String, selectedDate: LocalDate) {
    val formatter = DateTimeFormatter.ofPattern("yyyyMMdd")
    todoList.clear()
    val result = fetchTodos() // This function should ideally be defined to fetch todos from your data source
    result.forEach { jsonItem ->
        if (isItemRelevant(jsonItem, selectedSection, selectedDate, formatter)) {
            todoList.add(jsonItem)
        }
    }
}

fun isItemRelevant(item: TodoItem, selectedSection: String, selectedDate: LocalDate, formatter: DateTimeFormatter): Boolean {
    val itemDate = LocalDate.parse(item.datetime, formatter)
    return item.section == selectedSection &&
            itemDate == selectedDate &&
            item.recur !in listOf("Daily", "Weekly")
}

@Composable
fun showTodoList() {
    val currentDate = LocalDate.now()
    var month by remember { mutableStateOf(currentDate.monthValue) }
    var year by remember { mutableStateOf(currentDate.year) }
    var date by remember { mutableStateOf(currentDate.dayOfMonth) }
    date = checkDateInMonth(year, month, date)
    var selectedDate = LocalDate.of(year, month, date)
    val currentMonth = monthNames[month - 1]
    val formatter = DateTimeFormatter.ofPattern("yyyyMMdd")
    val (selectedSection, setSelectedSection) = remember { mutableStateOf("Work") }
    var todoListFromDb = remember { mutableStateListOf<TodoItem>() }
    var ifUpdate by remember { mutableStateOf(false) }
    var ifCreate by remember { mutableStateOf(false) }
    var currentId by remember {
        mutableStateOf(
            TodoItem(
                id = 0, primaryTask = "Initial", secondaryTask = "initial", priority = 0,
                completed = false, section = "test", datetime = "test", starttime = "test",
                duration = 3, recur = "test", pid = 0, deleted = 0, misc1 = 0, misc2 = 0
            )
        )
    }
    LaunchedEffect(selectedSection, selectedDate) {
        updateTodoListFromDb(todoListFromDb, selectedSection, selectedDate)
    }
    var isDialogOpen by remember { mutableStateOf(false) }
    Column(modifier = Modifier.fillMaxHeight().padding(top = 24.dp)) {
        Column(modifier = Modifier.fillMaxHeight().padding(top = 24.dp)) {
            Box(modifier = Modifier.fillMaxWidth().weight(0.05f)) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "$date $currentMonth $year",
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f, fill = false)
                    )

                    Row(modifier = Modifier.weight(1f, fill = false)) {
                        Spacer(Modifier.weight(0.9f))
                        Text(
                            "Today",
                            modifier = Modifier.clickable {
                                val today = LocalDate.now()
                                date = today.dayOfMonth
                                month = today.monthValue
                                year = today.year
                                selectedDate = today
                            }
                        )
                        Spacer(Modifier.weight(0.1f))
                        FilledTonalButton(
                            onClick = {
                                val newDate = if (date > 1) {
                                    LocalDate.of(year, month, date - 1)
                                } else if (month > 1) {
                                    LocalDate.of(year, month - 1, LocalDate.of(year, month - 1, 1).lengthOfMonth())
                                } else {
                                    LocalDate.of(year - 1, 12, 31)
                                }
                                date = newDate.dayOfMonth
                                month = newDate.monthValue
                                year = newDate.year
                                selectedDate = newDate
                            },
                            modifier = Modifier.size(70.dp, 30.dp)
                        ) {
                            androidx.compose.material.Text("<")
                        }
                        FilledTonalButton(
                            onClick = {
                                val newDate = if (date < selectedDate.lengthOfMonth()) {
                                    LocalDate.of(year, month, date + 1)
                                } else if (month < 12) {
                                    LocalDate.of(year, month + 1, 1)
                                } else {
                                    LocalDate.of(year + 1, 1, 1)
                                }
                                date = newDate.dayOfMonth
                                month = newDate.monthValue
                                year = newDate.year
                                selectedDate = newDate
                            },
                            modifier = Modifier.size(70.dp, 30.dp)
                        ) {
                            androidx.compose.material.Text(">")
                        }
                    }
                }
            }
        }

        Box(modifier = Modifier.fillMaxWidth().weight(0.08f)) {
            Row() {
                val commonButtonModifier =
                    Modifier.padding(14.dp).size(width = 240.dp, height = 40.dp)
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    OutlinedButton(
                        onClick = { setSelectedSection("Work") }, modifier = commonButtonModifier) {
                            Text("Work")
                        }
                }
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    OutlinedButton(
                        onClick = { setSelectedSection("Study") },
                        modifier = commonButtonModifier) {
                            Text("Study")
                        }
                }
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    OutlinedButton(
                        onClick = { setSelectedSection("Hobby") },
                        modifier = commonButtonModifier) {
                            Text("Hobby")
                        }
                }
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    OutlinedButton(
                        onClick = { setSelectedSection("Life") }, modifier = commonButtonModifier) {
                            Text("Life")
                        }
                }
            }
        }
        Box(modifier = Modifier.fillMaxWidth().weight(0.8f)) {
            LazyColumn() {
                todoListFromDb.forEachIndexed { index, todoItem ->
                    item {
                        ListItem(
                            headlineContent = { Text(todoItem.primaryTask) },
                            supportingContent = { Text(todoItem.secondaryTask) },
                            trailingContent = {
                                Column() {
                                    TextButton(
                                        onClick = {
                                            runBlocking {
                                                var whole_data = fetchTodos()
                                                launch {
                                                    if (todoItem.pid != 0) {
                                                        var to_be_deleted =
                                                            whole_data.filter {
                                                                it.pid == todoItem.pid
                                                            }
                                                        var parent =
                                                            whole_data.filter {
                                                                it.id == todoItem.pid
                                                            }
                                                        to_be_deleted.forEach { each ->
                                                            deleteTodo(each.id)
                                                        }
                                                        deleteTodo(parent[0].id)
                                                        todoListFromDb.remove(todoItem)
                                                    } else if (todoItem.recur == "Daily" ||
                                                        todoItem.recur == "Weekly") {
                                                        var to_be_deleted =
                                                            whole_data.filter {
                                                                it.pid == todoItem.id
                                                            }
                                                        to_be_deleted.forEach { each ->
                                                            deleteTodo(each.id)
                                                        }
                                                        deleteTodo(todoItem.id)
                                                        todoListFromDb.remove(todoItem)
                                                    } else {
                                                        deleteTodo(todoItem.id)
                                                        todoListFromDb.remove(todoItem)
                                                    }
                                                }
                                            }
                                        },
                                        modifier = Modifier.size(width = 100.dp, height = 35.dp)) {
                                            Text("Delete", style = TextStyle(fontSize = 13.sp))
                                        }
                                }
                            },
                            leadingContent = {
                                Checkbox(
                                    checked = todoItem.completed,
                                    onCheckedChange = { isChecked ->
                                        if (todoItem.recur != "Daily" &&
                                            todoItem.recur != "Weekly") {
                                            todoListFromDb[index] =
                                                todoListFromDb[index].copy(completed = isChecked)
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
                                                launch { create(copy_of_copy) }
                                            } // update fetched data
                                            runBlocking {
                                                var result: List<TodoItem> = fetchTodos()
                                                launch {
                                                    result = fetchTodos()
                                                    todoListFromDb.clear()
                                                    result.forEach { jsonItem ->
                                                        if (jsonItem.section == selectedSection &&
                                                            jsonItem.datetime ==
                                                                selectedDate.format(formatter) &&
                                                            jsonItem.recur != "Daily" &&
                                                            jsonItem.recur != "Weekly") {
                                                            todoListFromDb.add(
                                                                TodoItem(
                                                                    id = jsonItem.id,
                                                                    primaryTask =
                                                                        jsonItem.primaryTask,
                                                                    secondaryTask =
                                                                        jsonItem.secondaryTask,
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
                                                                    misc2 = 0))
                                                        }
                                                    }
                                                    result.forEach { jsonItem ->
                                                        if (jsonItem.section == selectedSection &&
                                                            jsonItem.recur == "Daily" &&
                                                            dateIsInRange(
                                                                selectedDate.format(formatter),
                                                                jsonItem.datetime,
                                                                jsonItem.misc1.toString())) {
                                                            val duplicate_item =
                                                                todoListFromDb.find {
                                                                    it.pid == jsonItem.id
                                                                }
                                                            if (duplicate_item == null) {
                                                                todoListFromDb.add(
                                                                    TodoItem(
                                                                        id = jsonItem.id,
                                                                        primaryTask =
                                                                            jsonItem.primaryTask,
                                                                        secondaryTask =
                                                                            jsonItem.secondaryTask,
                                                                        priority =
                                                                            jsonItem.priority,
                                                                        completed =
                                                                            jsonItem.completed,
                                                                        section = jsonItem.section,
                                                                        datetime =
                                                                            jsonItem.datetime,
                                                                        starttime =
                                                                            jsonItem.starttime,
                                                                        duration =
                                                                            jsonItem.duration,
                                                                        recur = jsonItem.recur,
                                                                        pid = jsonItem.pid,
                                                                        deleted = jsonItem.deleted,
                                                                        misc1 = 0,
                                                                        misc2 = 0))
                                                            }
                                                        } else if (jsonItem.recur == "Weekly" &&
                                                            jsonItem.section == selectedSection &&
                                                            LocalDate.parse(
                                                                    jsonItem.datetime, formatter)
                                                                .dayOfWeek ==
                                                                selectedDate.dayOfWeek &&
                                                            dateIsInRange(
                                                                selectedDate.format(formatter),
                                                                jsonItem.datetime,
                                                                jsonItem.misc1.toString())) {
                                                            val duplicate_item =
                                                                todoListFromDb.find {
                                                                    it.pid == jsonItem.id
                                                                }
                                                            if (duplicate_item == null) {
                                                                todoListFromDb.add(
                                                                    TodoItem(
                                                                        id = jsonItem.id,
                                                                        primaryTask =
                                                                            jsonItem.primaryTask,
                                                                        secondaryTask =
                                                                            jsonItem.secondaryTask,
                                                                        priority =
                                                                            jsonItem.priority,
                                                                        completed =
                                                                            jsonItem.completed,
                                                                        section = jsonItem.section,
                                                                        datetime =
                                                                            jsonItem.datetime,
                                                                        starttime =
                                                                            jsonItem.starttime,
                                                                        duration =
                                                                            jsonItem.duration,
                                                                        recur = jsonItem.recur,
                                                                        pid = jsonItem.pid,
                                                                        deleted = jsonItem.deleted,
                                                                        misc1 = 0,
                                                                        misc2 = 0))
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
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomEnd) {
                ExtendedFloatingActionButton(
                    modifier = Modifier.padding(bottom = 16.dp, end = 16.dp),
                    onClick = {
                        isDialogOpen = true
                        ifCreate = true
                    }) {
                        Text(text = "Create New")
                    }
            }
            if (isDialogOpen) {
                var tem_todo =
                    TodoItem(
                        0, "This is a dummy variable", "", 0, false, "", "", 0, "", "", 0, 0, 0, 0)
                var tem_todo_reset =
                    TodoItem(
                        0, "This is a dummy variable", "", 0, false, "", "", 0, "", "", 0, 0, 0, 0)
                if (ifUpdate) {
                    tem_todo = currentId.copy()
                    println("2")
                }
                createTodoDialog(
                    onClose = {
                        isDialogOpen = false
                        if (ifCreate) {
                            ifCreate = false
                        } else if (ifUpdate) {
                            ifUpdate = false
                            tem_todo = tem_todo_reset.copy()
                        }
                    },
                    onCreate = { newItem ->
                        isDialogOpen = false
                        runBlocking {
                            launch {
                                if (ifCreate) {
                                    create(newItem)
                                    ifCreate = false
                                } else if (ifUpdate) {
                                    updateTodoItem(currentId.id, newItem.copy())
                                    println(currentId.id)
                                    ifUpdate = false
                                    tem_todo = tem_todo_reset.copy()
                                }
                                todoListFromDb.clear()
                                var result: List<TodoItem> = fetchTodos()
                                result.forEach { jsonItem ->
                                    if (jsonItem.section == selectedSection &&
                                        jsonItem.datetime == selectedDate.format(formatter) &&
                                        jsonItem.recur != "Daily" &&
                                        jsonItem.recur != "Weekly") {
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
                                                misc2 = jsonItem.misc2))
                                    }
                                }
                                result.forEach { jsonItem ->
                                    if (jsonItem.section == selectedSection &&
                                        jsonItem.recur == "Daily" &&
                                        dateIsInRange(
                                            selectedDate.format(formatter),
                                            jsonItem.datetime,
                                            jsonItem.misc1.toString())) {
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
                                                    misc1 = jsonItem.misc1,
                                                    misc2 = jsonItem.misc2))
                                        }
                                    } else if (jsonItem.recur == "Weekly" &&
                                        jsonItem.section == selectedSection &&
                                        LocalDate.parse(jsonItem.datetime, formatter).dayOfWeek ==
                                            selectedDate.dayOfWeek &&
                                        dateIsInRange(
                                            selectedDate.format(formatter),
                                            jsonItem.datetime,
                                            jsonItem.misc1.toString())) {
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
                                                    misc1 = jsonItem.misc1,
                                                    misc2 = jsonItem.misc2))
                                        }
                                    }
                                }
                            }
                        }
                    },
                    defaultTodo = tem_todo)
            }
        }
    }
}
