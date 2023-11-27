import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import java.io.FileInputStream
import java.io.InputStream

import ui.theme.AppTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import summary.*
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.materialIcon
import java.awt.Dimension
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.material3.*
import androidx.compose.runtime.getValue

import androidx.compose.ui.res.*

//import androidx.compose.ui.input.key.Key.Companion.R

import note.*
// Sample Composable functions for each section

import org.jetbrains.exposed.sql.Database
import java.io.*
import com.google.cloud.storage.BlobId
import com.google.cloud.storage.BlobInfo
import com.google.cloud.storage.StorageOptions
import com.google.auth.oauth2.ServiceAccountCredentials
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import java.awt.Button
import java.nio.file.Files
import java.nio.file.Paths


import java.util.logging.Logger

val logger = Logger.getLogger("DatabaseLogger")


fun getDatabasePath(): String {
    val dbFileName = "chinook.db"
    // Check if a typical development directory/file exists
    val isDevelopment = File(".gradle").exists()
    return if (isDevelopment) {
        "jdbc:sqlite:$dbFileName"
    } else {
        val appDir = System.getProperty("user.dir")
        "jdbc:sqlite:$appDir/$dbFileName"
    }
}

@Composable
fun BoxItem(color: Color, text: String) {
    Box(modifier = Modifier.size(200.dp, 200.dp).background(color)) {
        Text(text)
    }
}

@Composable
fun MagicHome() {
    downloadDatabaseFromCloud()
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start
    ) {
        items(4) { index ->
            when (index) {
                0 -> HomeSummary()

                1 -> homeCalendar()
//                2 -> BoxItem(Color.Blue, "Todo section")
//                3 -> BoxItem(Color.Green, "Notes section")
            }
        }
    }
}

@Composable
fun Notes() {
    NotesEditor()
}
@Composable
fun color_button(boo: Boolean):ButtonColors {
    val tonalButtonColors = ButtonDefaults.buttonColors(
        containerColor = if (boo) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondaryContainer,
        contentColor = if (boo) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
    )
    return tonalButtonColors
}
@Composable
fun AppLayout() {
    val (selectedSection, setSelectedSection) = remember { mutableStateOf("Section 1") }
    val listOfBooleans = remember {
        listOf(
            mutableStateOf(true),  // First item is true
            mutableStateOf(false), // Second item is false
            mutableStateOf(false), // Third item is false
            mutableStateOf(false), // Fourth item is false
            mutableStateOf(false)  // Fifth item is false
        )
    }
    Row() {
        Column(
            verticalArrangement = Arrangement.SpaceBetween, // Controls vertical arrangement
            horizontalAlignment = Alignment.CenterHorizontally, // Controls horizontal alignment
            modifier = Modifier.fillMaxHeight()

        ) {
            val commonButtonModifier = Modifier
                .weight(1f)
                .padding(14.dp)
                .size(width = 150.dp, height = 1000.dp)
            Button(
                onClick = {
                    setSelectedSection("garbage")
                    listOfBooleans[0].value = true
                    listOfBooleans[1].value = false
                    listOfBooleans[2].value = false
                    listOfBooleans[3].value = false
                    listOfBooleans[4].value = false
                }, modifier = commonButtonModifier, colors = color_button(listOfBooleans[0].value)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        painter = painterResource("home.svg"),
                        contentDescription = null
                    )

                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Home")
                }
            }
            FilledTonalButton(
                onClick = {
                    setSelectedSection("Calendar")
                    listOfBooleans[0].value = false
                    listOfBooleans[1].value = true
                    listOfBooleans[2].value = false
                    listOfBooleans[3].value = false
                    listOfBooleans[4].value = false
                }, modifier = commonButtonModifier, colors = color_button(listOfBooleans[1].value)
            ) {
                Text(
                    text = "Calendar"
                )
            }
            FilledTonalButton(
                onClick = {
                    setSelectedSection("Summary")
                    listOfBooleans[0].value = false
                    listOfBooleans[1].value = false
                    listOfBooleans[2].value = true
                    listOfBooleans[3].value = false
                    listOfBooleans[4].value = false
                }, modifier = commonButtonModifier, colors = color_button(listOfBooleans[2].value)
            ) {
                Text("Summary")
            }
            FilledTonalButton(
                onClick = {
                    setSelectedSection("To-Do-List")
                    listOfBooleans[0].value = false
                    listOfBooleans[1].value = false
                    listOfBooleans[2].value = false
                    listOfBooleans[3].value = true
                    listOfBooleans[4].value = false
                }, modifier = commonButtonModifier, colors = color_button(listOfBooleans[3].value)
            ) {
                Text("To-Do-List")
            }
            FilledTonalButton(
                onClick = {
                    setSelectedSection("Notes")
                    listOfBooleans[0].value = false
                    listOfBooleans[1].value = false
                    listOfBooleans[2].value = false
                    listOfBooleans[3].value = false
                    listOfBooleans[4].value = true
                }, modifier = commonButtonModifier, colors = color_button(listOfBooleans[4].value)
            ) {
                Text("Notes")
            }

        }
        Box(
            modifier = Modifier.weight(3f).fillMaxWidth(), contentAlignment = Alignment.Center
        ) {
            when (selectedSection) {
                "Home" -> MagicHome()
                "Calendar" -> Calendar()
                "Summary" -> Summary()
                "To-Do-List" -> ToDoList()
                "Notes" -> Notes()
                else -> MagicHome()
            }
        }
    }
}


class DatabaseManager {
    fun setupDatabase(): Database {
        val dbName = "chinook.db"
        val persistentDir = File(System.getProperty("user.home"), ".myApp")
        val persistentDBFile = File(persistentDir, dbName)
        // Check if the database file exists in a persistent location
        if (!persistentDBFile.exists()) {
            // Ensure the directory exists
            persistentDir.mkdirs()
            // Copy the database from the resources to the persistent location
            val resourceStream = this::class.java.getResourceAsStream("/$dbName")
            FileOutputStream(persistentDBFile).use { output ->
                resourceStream.copyTo(output)
            }
        }

        // Load SQLite JDBC driver (required for some configurations)
        Class.forName("org.sqlite.JDBC")

        // Connect to the SQLite database in the persistent location
        val connectionUrl = "jdbc:sqlite:${persistentDBFile.absolutePath}"
        return Database.connect(connectionUrl)
    }
}


fun uploadDatabaseToCloud() {
    val storage = StorageOptions.newBuilder()
        .setCredentials(ServiceAccountCredentials.fromStream(FileInputStream("/Users/seangong/IdeaProjects/CS346-project/application/src/main/resources/key.json")))
        .build().service

    // Define the directory and file path for the database
    val persistentDir = File(System.getProperty("user.home"), ".myApp")
    val persistentDBFile = File(persistentDir, "chinook.db")

    val blobId = BlobId.of("cs346bucket", "chinook.db")
    val blobInfo = BlobInfo.newBuilder(blobId).build()

    // Use the correct file path to read the database
    storage.create(blobInfo, Files.readAllBytes(persistentDBFile.toPath()))
}

fun downloadDatabaseFromCloud() {
    val storage = StorageOptions.newBuilder()
        .setCredentials(ServiceAccountCredentials.fromStream(FileInputStream("/Users/seangong/IdeaProjects/CS346-project/application/src/main/resources/key.json")))
        .build().service
    val blob = storage.get(BlobId.of("cs346bucket", "chinook.db"))
    val readChannel = blob.reader()

    // Define the directory and file path for the database
    val persistentDir = File(System.getProperty("user.home"), ".myApp")
    val persistentDBFile = File(persistentDir, "chinook.db")

    // Ensure the directory exists
    persistentDir.mkdirs()

    // Use FileOutputStream to write to the correct location
    FileOutputStream(persistentDBFile).channel.transferFrom(readChannel, 0, Long.MAX_VALUE)
}


fun main() = application {
    val window = Window(
        onCloseRequest = ::exitApplication, title = "My Journal"
    ) {
        window.minimumSize = Dimension(1300, 800)
        AppTheme {
            Box(
                modifier = Modifier.background(MaterialTheme.colorScheme.background)
            ) {
                //
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
//                    }
//                }
                AppLayout()
            }
        }
    }
}
