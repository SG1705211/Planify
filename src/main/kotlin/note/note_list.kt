package note

//import DatabaseManager
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.mohamedrejeb.richeditor.model.RichTextState

import androidx.compose.runtime.saveable.rememberSaveable
import getDatabasePath



import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.statements.InsertStatement
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

@Composable
fun myDialog(t: String, comment: String): Boolean {
    var result = false
    AlertDialog(
        onDismissRequest = { /* dismiss dialog */ },
        title = {
            Text(text = t)
        },
        text = {
            Text(text = comment)
        },
        confirmButton = {
            Button(onClick = {result = true}) {
                Text("Confirm")
            }
            Button(onClick = {result = false}) {
                Text("Confirm")
            }
        }
    )
    return result
}

object FilesTable : Table() {
    val id = integer("id").autoIncrement()
    var name = varchar("name", 255)
    var content = text("content")
    override val primaryKey = PrimaryKey(id, name = "PK_User_ID")
    // val folderId = integer("folder_id").references(FoldersTable.id)
}

data class FileItem(
    val id: Int,
    var name: String,
    var content: String
)

@Composable
fun CreateFileDialog(onCreate: (FileItem) -> Unit) {
    Database.connect("jdbc:sqlite:chinook.db")
    var id by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = { /* dismiss dialog */ },
        title = {
            Text(text = "Create File")
        },
        text = {
            Column {
                TextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name the File") }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotEmpty()) {
                        onCreate(FileItem(id = 0, name = name, content = ""))
                    }
                }
            ) {
                Text("Create")
            }
        }
    )
}

//object FoldersTable : Table() {
//    val id = integer("id")
//    val name = varchar("name", 255)
//    var numberOfItem = integer("num")
//    val parentFolderId = integer("parent_id").references(FoldersTable.id).nullable()
//}


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun NoteList(state: RichTextState) {
    Database.connect("jdbc:sqlite:chinook.db")
    transaction {
        SchemaUtils.create(FilesTable)
    }
    val idList = remember { mutableStateListOf<String>()}
    var isDialogOpen by remember { mutableStateOf(false) }
    var isSaveDialogOpen by remember { mutableStateOf(false) }
    var isDelDialogOpen by remember { mutableStateOf(false) }
    var canIdelete by remember { mutableStateOf(false) }
    val selectedNoteIndex = remember { mutableStateOf<Int>(0) }
    val numItemInFolder = remember { mutableStateOf<Int?>(0) }
    var isFile = true

    Box(Modifier.fillMaxSize()) {
        FlowRow(modifier = Modifier.align(Alignment.TopCenter)) {
            // add file button
            FloatingActionButton(
                modifier = Modifier
                    .padding(10.dp),
                onClick = {
                    isFile = true
                    isDialogOpen = true
                },
                containerColor = MaterialTheme.colorScheme.tertiary,
                elevation =  FloatingActionButtonDefaults.elevation(
                    defaultElevation = 10.dp
                )
            ) {
                Icon(Icons.Filled.Edit, "Localized description")
            }

            // add folder button
            FloatingActionButton(
                modifier = Modifier
                    .padding(10.dp),
                onClick = {
                    isFile = false
                    numItemInFolder.value = numItemInFolder.value?.plus(1)
                },
                containerColor = MaterialTheme.colorScheme.tertiary,
                elevation =  FloatingActionButtonDefaults.elevation(
                    defaultElevation = 10.dp
                )
            ) {
                Icon(Icons.Filled.Add, "Localized description")
            }

            // add save button
            FloatingActionButton(
                modifier = Modifier
                    .padding(10.dp),
                onClick = {
                    isSaveDialogOpen = true
                    transaction {
                        FilesTable.update({FilesTable.name eq idList[selectedNoteIndex.value]}) {
                            it[content] = state.toHtml()
                        }
                    }
                },
                containerColor = MaterialTheme.colorScheme.tertiary,
                elevation =  FloatingActionButtonDefaults.elevation(
                    defaultElevation = 10.dp
                )
            ) {
                Text("SAVE")
            }

            // add delete button
            FloatingActionButton(
                modifier = Modifier
                    .padding(10.dp),
                onClick = {
                    transaction {
                        if (FilesTable.selectAll().count().toInt() != 0) {
                            FilesTable.deleteWhere {
                                FilesTable.name eq idList[selectedNoteIndex.value]
                            }
                        }
                        idList.clear()
                        FilesTable.selectAll().map {
                            idList.add(it[FilesTable.name])
                        }
                    }
                },
                containerColor = MaterialTheme.colorScheme.tertiary,
                elevation =  FloatingActionButtonDefaults.elevation(
                    defaultElevation = 10.dp
                )
            ) {
                Text("DEL")
            }
        }

        idList.clear()
        transaction {
            FilesTable.selectAll().map {
                idList.add(it[FilesTable.name])
            }
        }

        LazyColumn(modifier = Modifier.padding(top = 150.dp)) {
            items(idList.size) {
                val backgroundColor = if (it == selectedNoteIndex.value) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onPrimary
                val fontWeight = if (it == selectedNoteIndex.value) FontWeight.Bold else FontWeight.Normal

                ElevatedCard (
                    // shadow
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 3.dp
                    ),
                    colors = CardDefaults.cardColors(
                        containerColor = backgroundColor
                    ),
                    shape = RoundedCornerShape(8.dp),
                ) {
                    Row (modifier = Modifier
                        .clickable {
                            selectedNoteIndex.value = it
                            transaction {
                                FilesTable.select { FilesTable.name eq idList[it] }.forEach {
                                    state.setHtml(it[FilesTable.content])
                                }
                            }

                        }
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                    ) {
                        Icon(
                            Icons.Filled.Edit, // icon image
                            contentDescription = "A Pen",
                            modifier = Modifier
                                .clickable {  }
                                .align(Alignment.CenterVertically)
                                .padding(start = 10.dp)
                        )

                        Text(
                            text = idList[it],
                            fontWeight = fontWeight,
                            modifier = Modifier
                                .padding(16.dp),
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }
        }

        if (isDialogOpen) {
            CreateFileDialog(onCreate = { newItem ->
                isDialogOpen = false
                transaction {
                    val newId = FilesTable.insert {
                        it[name] = newItem.name
                        it[content] = newItem.content
                    }
                    // idList.add(newItem.name)
                }

            })
        }

        if (isSaveDialogOpen) {
            var result = false
            AlertDialog(
                onDismissRequest = { /* dismiss dialog */ },
                title = {
                    Text(text = "Save Successfully")
                },
                confirmButton = {
                    Button(onClick = { isSaveDialogOpen = false }) {
                        Text("Confirm")
                    }
                }
            )
        }

        if (isDelDialogOpen) {
            var result = false
            AlertDialog(
                onDismissRequest = { /* dismiss dialog */ },
                title = {
                    Text(text = "Save Successfully")
                },
                confirmButton = {
                    Button(onClick = {
                        isDelDialogOpen = false
                        canIdelete = true
                    }) {
                        Text("Delete")
                    }
                    Button(onClick = {
                        isDelDialogOpen = false
                        canIdelete = false
                    }) {
                        Text("Cancel")
                    }
                }

            )
        }

    }
}
