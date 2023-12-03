package com.example.plugins

import com.example.plugins.configureRouting

import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.server.testing.*
import kotlin.test.Test

class ConfigureRoutingTest {

    @Test
    fun testGet() = testApplication {
        application {
            configureRouting()
        }
        client.get("/").apply {
            TODO("Please write your test here")
        }
    }

    @Test
    fun testPostFolder() = testApplication {
        application {
            configureRouting()
        }
        client.post("/Folder").apply {
            TODO("Please write your test here")
        }
    }

    @Test
    fun testDeleteFolderId() = testApplication {
        application {
            configureRouting()
        }
        client.delete("/folder/{id}").apply {
            TODO("Please write your test here")
        }
    }

    @Test
    fun testGetFoldername() = testApplication {
        application {
            configureRouting()
        }
        client.get("/folder_name").apply {
            TODO("Please write your test here")
        }
    }

    @Test
    fun testPostNotes() = testApplication {
        application {
            configureRouting()
        }
        client.post("/notes").apply {
            TODO("Please write your test here")
        }
    }

    @Test
    fun testDeleteNotesId() = testApplication {
        application {
            configureRouting()
        }
        client.delete("/notes/{id}").apply {
            TODO("Please write your test here")
        }
    }

    @Test
    fun testGetNotesname() = testApplication {
        application {
            configureRouting()
        }
        client.get("/notes_name").apply {
            TODO("Please write your test here")
        }
    }

    @Test
    fun testGetTodos() = testApplication {
        application {
            configureRouting()
        }
        client.get("/todos").apply {
            TODO("Please write your test here")
        }
    }

    @Test
    fun testPostTodos() = testApplication {
        application {
            configureRouting()
        }
        client.post("/todos").apply {
            TODO("Please write your test here")
        }
    }

    @Test
    fun testDeleteTodosId() = testApplication {
        application {
            configureRouting()
        }
        client.delete("/todos/{id}").apply {
            TODO("Please write your test here")
        }
    }

    @Test
    fun testGetTodosId() = testApplication {
        application {
            configureRouting()
        }
        client.get("/todos/{id}").apply {
            TODO("Please write your test here")
        }
    }

    @Test
    fun testPostUpdateId() = testApplication {
        application {
            configureRouting()
        }
        client.post("/update/{id}").apply {
            TODO("Please write your test here")
        }
    }

    @Test
    fun testPostUpdatefileId() = testApplication {
        application {
            configureRouting()
        }
        client.post("/updateFile/{id}").apply {
            TODO("Please write your test here")
        }
    }
}