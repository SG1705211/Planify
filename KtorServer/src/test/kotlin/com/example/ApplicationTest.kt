package com.example.plugins

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import io.ktor.util.*
import junit.framework.TestCase.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.codebot.models.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import kotlin.test.BeforeTest
import kotlin.test.Test

class ConfigureRoutingTest {
    @BeforeTest
    fun reset() = testApplication {
        application {
            configureSecurity()
            configureMonitoring()
            configureSerialization()
            configureDatabases()
            configureRouting(testing = true)
        }
        client.post("/Reset")

    }

    @Test
    fun testGet() = testApplication {
        application {
            configureSecurity()
            configureMonitoring()
            configureSerialization()
            configureDatabases()
            configureRouting(testing = true)
        }
        client.get("/").apply {
            assertEquals("Connection Success", bodyAsText())
        }
    }

    @OptIn(InternalAPI::class)
    @Test
    fun testPostFolder() = testApplication {
        application {
            configureSecurity()
            configureMonitoring()
            configureSerialization()
            configureDatabases()
            configureRouting(testing = true)
        }
        val tem_file = FolderItem(
            id = 100, name = "Folder File To_Be_Deleted", marked = false, parentFolderName = "Null"
        )
        client.post("/Folder") {
            contentType(ContentType.Application.Json)
            body = Json.encodeToString(tem_file)
        }.apply {
            // get the real data
            transaction {
                val createdNote =
                    Folders__Table.select { Folders__Table.name eq "Folder File To_Be_Deleted" }.singleOrNull()
                assertNotNull(createdNote)
                Folders__Table.deleteWhere { Folders__Table.name eq "Folder File To_Be_Deleted" }
            }
        }
    }

    @Test
    fun testGetFoldername() = testApplication {
        application {
            configureSecurity()
            configureMonitoring()
            configureSerialization()
            configureDatabases()
            configureRouting(testing = true)
        }
        client.get("/folder_name").apply {
//            assertEquals(HttpStatusCode.OK, status)
            val todos = Json.decodeFromString<List<FolderItem>>(bodyAsText())
            val folderItems = listOf(
                FolderItem(id = 2, name = "Test Folder 1", parentFolderName = "", marked = false),
                FolderItem(id = 3, name = "Test Folder 3", parentFolderName = "", marked = false),
                FolderItem(id = 4, name = "Test Folder 4", parentFolderName = "", marked = false),
                FolderItem(id = 5, name = "Test 2", parentFolderName = "Test Folder 2", marked = false)
            )
            assertEquals(
                todos, folderItems
            )
        }
    }


    @Test
    fun testGetNotesname() = testApplication {
        application {
            configureSecurity()
            configureMonitoring()
            configureSerialization()
            configureDatabases()
            configureRouting(testing = true)
        }
        client.get("/notes_name").apply {
            val todos = Json.decodeFromString<List<FileItem>>(bodyAsText())
            val fileItems = listOf(
                FileItem(
                    id = 2,
                    name = "Test File 1",
                    content = "<p style=\"text-align: left;\">Sherlock is beautiful</p>",
                    folder = "Test 2",
                    marked = false
                ), FileItem(
                    id = 3,
                    name = "Test File 2",
                    content = "<p style=\"text-align: left;\">Sherlock is not beautiful</p>",
                    folder = "Test Folder 2",
                    marked = false
                )
            )
            assertEquals(fileItems, todos)
        }
    }

    //
    @Test
    fun testGetTodos() = testApplication {
        application {
            configureSecurity()
            configureMonitoring()
            configureSerialization()
            configureDatabases()
            configureRouting(testing = true)
        }
        client.get("/todos").apply {
            val todos = Json.decodeFromString<List<TodoItem>>(bodyAsText())
            val todoList = listOf(
                TodoItem(
                    0, "Impress Orange", "Impress Orange", 1, true, "20231126", "Work", 3, "10:00", "None", 13, 0, 0, 0
                ),
                TodoItem(
                    1, "Write report", "Due next week", 1, false, "20231030", "Work", 3, "08:00", "None", 0, 0, 0, 0
                ),
                TodoItem(2, "Email client", "Urgent", 2, false, "20231029", "Work", 3, "08:00", "None", 0, 0, 0, 0),
                TodoItem(
                    3, "Study for exam", "Chapter 1-5", 1, false, "20231030", "Study", 3, "08:00", "None", 0, 0, 0, 0
                ),
                TodoItem(
                    4,
                    "Complete assignment",
                    "Submit online",
                    2,
                    false,
                    "20231030",
                    "Study",
                    3,
                    "08:00",
                    "None",
                    0,
                    0,
                    0,
                    0
                )
            )
            assertEquals(todos, todoList)
        }
    }

    //
    @OptIn(InternalAPI::class)
    @Test
    fun testPostTodos() = testApplication {
        application {
            configureSecurity()
            configureMonitoring()
            configureSerialization()
            configureDatabases()
            configureRouting(testing = true)
        }
        var tem_todo = TodoItem(
            10, "To_Be_Deleted", "Due next week", 1, false, "20231030", "Work", 3, "08:00", "None", 0, 0, 0, 0
        )
        client.post("/todos") {
            contentType(ContentType.Application.Json)
            body = Json.encodeToString(tem_todo)
        }.apply {
            transaction {
                val createdNote = TodoTable.select { TodoTable.primaryTask eq tem_todo.primaryTask }.singleOrNull()
                assertNotNull(createdNote)
                TodoTable.deleteWhere { TodoTable.primaryTask eq tem_todo.primaryTask }
            }
        }
    }

    @Test
    fun testDeleteTodosId() = testApplication {
        application {
            configureRouting(testing = true)
        }
        client.delete("/todos/4").apply {
            // fetch data firstly
            transaction {
                val createdNote = TodoTable.select { TodoTable.primaryTask eq "Complete assignment" }.singleOrNull()
                assertNull(createdNote)
                TodoTable.insert {
                    it[id] = 4
                    it[primaryTask] = "Complete assignment"
                    it[secondaryTask] = "Submit online"
                    it[priority] = 2
                    it[completed] = false
                    it[starttime] = "08:00"
                    it[section] = "Study"
                    it[duration] = 3
                    it[datetime] = "20231030"
                    it[recur] = "None"
                    it[pid] = 0
                    it[deleted] = 0
                    it[misc1] = 0
                    it[misc2] = 0
                }
            }
        }
    }


    @Test
    fun testDeleteNotesId() = testApplication {
        application {
            configureRouting(testing = true)
        }
        client.delete("/notes/3").apply {
            transaction {
                val createdNote = Table__File.select { Table__File.id eq 3 }.singleOrNull()
                assertNull(createdNote)
                Table__File.insert {
                    it[id] = 3
                    it[name] = "Test File 2"
                    it[content] = "<p style=\"text-align: left;\">Sherlock is not beautiful</p>"
                    it[marked] = false
                    it[folderName] = "Test Folder 2"
                    it[folderID] = 0
                }
            }

        }
    }


    @Test
    fun testDeleteFolderId() = testApplication {

        application {
            configureRouting(testing = true)
        }
        FolderItem(id = 5, name = "Test 2", parentFolderName = "Test Folder 2", marked = false)
        client.delete("/folder/5").apply {
            transaction {
                val createdNote = Folders__Table.select { Folders__Table.id eq 5 }.singleOrNull()
                assertNull(createdNote)
                Folders__Table.insert {
                    it[id] = 5
                    it[name] = "Test 2"
                    it[marked] = false
                    it[parentFolder] = "Test Folder 2"
                    it[parentID] = 0
                }
            }
        }
    }

    @OptIn(InternalAPI::class)
    @Test
    fun testPostUpdateId() = testApplication {
        application {
            configureSecurity()
            configureMonitoring()
            configureSerialization()
            configureDatabases()
            configureRouting(testing = true)
        }
        var tem_todo = TodoItem(
            0, "Impress Orange",
            "Impress Orange", 1, false, "20231126",
            "Work", 3, "10:00", "None",
            13, 0, 0, 0
        )
        client.post("/update/0") {
            contentType(ContentType.Application.Json)
            body = Json.encodeToString(tem_todo)
        }.apply {
            transaction {
                val createdNote = TodoTable.select { TodoTable.id eq tem_todo.id }.singleOrNull()
                assertNotNull(createdNote)
                assertEquals(createdNote?.get(TodoTable.completed), false)
                TodoTable.update({ TodoTable.id eq tem_todo.id }) {
                    it[completed] = true
                }
            }
        }
    }

    @OptIn(InternalAPI::class)
    @Test
    fun testPostUpdatefileId() = testApplication {
        application {
            configureSecurity()
            configureMonitoring()
            configureSerialization()
            configureDatabases()
            configureRouting(testing = true)
        }
        val tem_file = FileItem(
            id = 2,
            name = "Test File To_Be_Deleted",
            content = "<p style=\"text-align: left;\">Sherlock is always beautiful</p>",
            folder = "Test 2",
            marked = false
        )
        client.post("/updateFile/2") {
            contentType(ContentType.Application.Json)
            body = Json.encodeToString(tem_file)
        }.apply {
            // get the real data
            transaction {
                val createdNote = Table__File.select { Table__File.id eq 2 }.singleOrNull()
                assertNotNull(createdNote)
                assertEquals(createdNote?.get(Table__File.content), tem_file.content)
                Table__File.update({ Table__File.id eq 2 }) {
                    it[content] = "<p style=\"text-align: left;\">Sherlock is beautiful</p>"
                }
            }
        }
    }


    @OptIn(InternalAPI::class)
    @Test
    fun testPostNotes() = testApplication {
        application {
            configureSecurity()
            configureMonitoring()
            configureSerialization()
            configureDatabases()
            configureRouting(testing = true)
        }
        val tem_file = FileItem(
            id = 100,
            name = "Test File To_Be_Deleted",
            content = "<p style=\"text-align: left;\">Sherlock is beautiful</p>",
            folder = "Test 2",
            marked = false
        )
        client.post("/notes") {
            contentType(ContentType.Application.Json)
            body = Json.encodeToString(tem_file)
        }.apply {
            // get the real data
            transaction {
                val createdNote = Table__File.select { Table__File.name eq "Test File To_Be_Deleted" }.singleOrNull()
                assertNotNull(createdNote)
                Table__File.deleteWhere { Table__File.name eq tem_file.name }
            }
        }
    }
}