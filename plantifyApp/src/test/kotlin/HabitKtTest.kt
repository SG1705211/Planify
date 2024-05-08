
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import net.codebot.models.TodoItem
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import kotlin.test.BeforeTest
import kotlin.test.assertEquals

class HabitKtTest {
    @BeforeTest
    fun reset() = runTest {
        runBlocking {
            val client = HttpClient(CIO)
            client.post("http://localhost:8080/Reset")
        }
    }

    @Test
    fun Testcreate() = runTest{
        val to_be_created = TodoItem(
            1000, "Impress Orange Again", "Impress Orange", 1, true, "20231126", "Work", 3, "10:00", "None", 13, 0, 0, 0
        )
        create(to_be_created)
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
            ),
            TodoItem(
                1000, "Impress Orange Again", "Impress Orange", 1, true, "20231126", "Work", 3, "10:00", "None", 13, 0, 0, 0
            )
        )
        val todoItems = fetchTodos()
        var count = 0
        todoItems.forEach { jsonItem ->
            assertEquals(jsonItem.primaryTask, todoList[count].primaryTask)
            count ++
        }
    }

    @Test
    fun TestfetchTodos() = runTest {
        runBlocking {
            val todoItems = fetchTodos()
            assertTrue(todoItems.isNotEmpty())
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
            assertEquals( todoList,todoItems)
        }
    }


    @Test
    fun TestdeleteTodo() = runTest{
        deleteTodo(0)
        val todoItems = fetchTodos()
        val todoList = listOf(
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
        assertEquals( todoList,todoItems)
    }

    @Test
    fun TestUpdate() = runTest{
        val to_be_updated = TodoItem(
            1000, "Impress Orange Again and Again", "Impress Orange", 1, true, "20231126", "Work", 3, "10:00", "None", 13, 0, 0, 0
        )
        updateTodoItem(0, to_be_updated)
        val todoList = listOf(
            TodoItem(
                0, "Impress Orange Again and Again", "Impress Orange", 1, true, "20231126", "Work", 3, "10:00", "None", 13, 0, 0, 0
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
        // this is already tested separately in the Ktor Server
        val todoItems = fetchTodos()
        assertEquals(todoItems, todoList)
    }


}