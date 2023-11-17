import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import java.io.FileInputStream
import java.io.InputStream
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import ui.theme.AppTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import summary.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.width
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.singleWindowApplication
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.xml.sax.InputSource
import java.io.File
import java.io.IOException
import androidx.compose.ui.window.*
import java.awt.Dimension
import androidx.compose.ui.window.Window
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.unit.dp
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.ViewConfiguration
import androidx.compose.ui.text.input.ImeAction
import org.jetbrains.exposed.sql.transactions.transaction
import androidx.compose.ui.window.application
import androidx.compose.material3.*
import androidx.compose.*
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.vector.*
import androidx.compose.ui.graphics.withSave
import androidx.compose.ui.res.*
//import androidx.compose.ui.input.key.Key.Companion.R
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.*
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.random.Random
import java.util.Date
import org.jetbrains.exposed.sql.batchInsert
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
    override val primaryKey = PrimaryKey(id, name = "PK_User_ID")
}

data class TodoItem(
    val id: Int, val primaryTask: String, val secondaryTask: String, val priority: Int,
    var completed: Boolean, val section: String, val date_time: String, val start_time: String,
    val duration: String
)

@Composable
fun CreateTodoDialog(onCreate: (TodoItem) -> Unit,onClose: () -> Unit) {
    var primaryTask by remember { mutableStateOf("") }
    var secondaryTask by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf(1) }
    var section by remember { mutableStateOf("") }
    var dueDate by remember { mutableStateOf("") }
    var start_time by remember { mutableStateOf("") }
    var isDateValid by remember { mutableStateOf(true) }
    var areFieldsValid by remember { mutableStateOf(true) }
    var duration_in by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = { /* dismiss dialog */ },
        title = {
            Text(text = "Create New Todo Item")
        },
        text = {
            Column {
                TextField(
                    value = primaryTask,
                    onValueChange = { primaryTask = it },
                    label = { Text("Primary Task") }
                )
                TextField(
                    value = secondaryTask,
                    onValueChange = { secondaryTask = it },
                    label = { Text("Secondary Task") }
                )
                TextField(
                    value = priority.toString(),
                    onValueChange = { priority = it.toIntOrNull() ?: 1 },
                    label = { Text("Priority") },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
                )
                TextField(
                    value = section,
                    onValueChange = { section = it },
                    label = { Text("Section") }
                )
                TextField(
                    value = start_time,
                    onValueChange = { start_time = it },
                    label = { Text("Start Time (HH:MM)") }
                )
                TextField(
                    value = duration_in,
                    onValueChange = { duration_in = it },
                    label = { Text("Duration (in Hours)") }
                )
                TextField(
                    value = dueDate,
                    onValueChange = { dueDate = it },
                    label = { Text("Due Date (yyyy-MM-dd)") }
                )
                // Error messages appear here, inside the AlertDialog
                if (!isDateValid) {
                    Text("Invalid date format", color = Color.Red)
                }
                if (!areFieldsValid) {
                    Text("All fields are required", color = Color.Red)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    isDateValid = validateDate(dueDate)
                    areFieldsValid = primaryTask.isNotEmpty() && secondaryTask.isNotEmpty() && section.isNotEmpty() && isDateValid

                    if (areFieldsValid) {
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
                                start_time = start_time
                            )
                        )
                    }
                }
            ) {
                Text("Create")
            }
        },
        // Add this part to introduce a close button
        dismissButton = {
            Button(
                onClick = {
                    onClose() // Closes the dialog when clicked
                }
            ) {
                Text("Close")
            }
        }
    )
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



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToDoList() {
    val manager = DatabaseManager()
                val db = manager.setupDatabase()
    val (selectedSection, setSelectedSection) = remember { mutableStateOf("Work") }
    val todoListFromDb = remember { mutableStateListOf<TodoItem>()}

    // Moved database fetching to LaunchedEffect to minimize recomposition
    LaunchedEffect(selectedSection) {
        transaction {
            todoListFromDb.clear()
            TodoTable.select { TodoTable.section eq selectedSection }.forEach {
                todoListFromDb.add(
                    TodoItem(
                        it[TodoTable.id],
                        it[TodoTable.primaryTask],
                        it[TodoTable.secondaryTask],
                        it[TodoTable.priority],
                        it[TodoTable.completed],
                        it[TodoTable.section],
                        it[TodoTable.datetime],
                        "null", "2"
                    )
                )
            }
            //uploadDatabaseToCloud()
        }
    }
    // New state variable to control dialog visibility
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
            // other modifiers, content, etc.
        ) {
            val triggerRecomposition = remember { mutableStateOf(false) }
            LazyColumn() {
                todoListFromDb.forEachIndexed { index, todoItem ->
                    item {
                        ListItem(headlineContent = { Text(todoItem.primaryTask) },
                            supportingContent = { Text(todoItem.secondaryTask) },
                            trailingContent = { Text("Priority ${todoItem.priority}") },
                            leadingContent = {
                                Checkbox(checked = todoItem.completed, onCheckedChange = { isChecked ->
                                    todoListFromDb[index] = todoListFromDb[index].copy(completed = isChecked)

                                    // Update database
                                    transaction {
                                        TodoTable.update({ TodoTable.id eq todoItem.id }) {
                                            it[completed] = isChecked
                                        }
                                    }
                                    uploadDatabaseToCloud()
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
                },
                    onCreate = { newItem ->
                        isDialogOpen = false  // Close the dialog
                        transaction {
                            // Insert new item into the database
                            val newId = TodoTable.insert {
                                it[primaryTask] = newItem.primaryTask
                                it[secondaryTask] = newItem.secondaryTask
                                it[priority] = newItem.priority
                                it[completed] = newItem.completed
                                it[section] = newItem.section
                                it[datetime] = newItem.date_time
                                it[duration] = newItem.duration.toInt()
                                it[starttime] = newItem.start_time
                            }
                            // Add new item to the list
                            todoListFromDb.add(newItem.copy(id = 20))
                        }
                        uploadDatabaseToCloud()
                    })
            }
        }
    }
}


