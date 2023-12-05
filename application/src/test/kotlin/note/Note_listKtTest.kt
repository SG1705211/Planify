package note

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import net.codebot.models.FileItem
import net.codebot.models.FolderItem
import org.junit.jupiter.api.Test
import kotlin.test.BeforeTest
import kotlin.test.assertEquals

class Note_listKtTest {
    @BeforeTest
    fun reset() = runTest {
        runBlocking {
            val client = HttpClient(CIO)
            client.post("http://localhost:8080/Reset")
        }
    }
    @Test
    fun TestcreateFolder()  = runTest{
        var to_be_create = FolderItem(id = 6, name = "Test 2 To Be Deleted", parentFolderName = "Test Folder 2", marked = false)
        create(to_be_create)
        var actual = fetchFolder()
        val folderItems = listOf(
            FolderItem(id = 2, name = "Test Folder 1", parentFolderName = "", marked = false),
            FolderItem(id = 3, name = "Test Folder 3", parentFolderName = "", marked = false),
            FolderItem(id = 4, name = "Test Folder 4", parentFolderName = "", marked = false),
            FolderItem(id = 5, name = "Test 2", parentFolderName = "Test Folder 2", marked = false),
            FolderItem(id = 6, name = "Test 2 To Be Deleted", parentFolderName = "Test Folder 2", marked = false)
        )
        var count = 0
        actual.forEach { jsonItem ->
            assertEquals(jsonItem.name, folderItems[count].name)
            count ++
        }

    }

    @Test
    fun testCreateFile() = runTest{
        val tem_file = FileItem(
            id = 3,
            name = "Test File 3",
            content = "<p style=\"text-align: left;\">Sherlock is always beautiful</p>",
            folder = "Test 2",
            marked = false
        )
        create(tem_file)
        var actual = fetchnotes()
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
            ), FileItem(
                id = 3,
                name = "Test File 3",
                content = "<p style=\"text-align: left;\">Sherlock is always beautiful</p>",
                folder = "Test 2",
                marked = false
            )
        )
        var count = 0
        actual.forEach { jsonItem ->
            assertEquals(jsonItem.content, fileItems[count].content)
            count ++
        }
    }

    @Test
    fun testupdateTodoItem() = runTest {
        val tem_file = FileItem(
            id = 2,
            name = "Test File 1",
            content = "<p style=\"text-align: left;\">Sherlock is always beautiful</p>",
            folder = "Test 2",
            marked = false
        )
        updateTodoItem(2,tem_file)
        var actual = fetchnotes()
        val fileItems = listOf(
            FileItem(
                id = 2,
                name = "Test File 1",
                content = "<p style=\"text-align: left;\">Sherlock is always beautiful</p>",
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
        assertEquals(fileItems, actual)
    }

    @Test
    fun testdeleteFolder()  = runTest{
        deleteFolder(5)
        val folderItems = listOf(
            FolderItem(id = 2, name = "Test Folder 1", parentFolderName = "", marked = false),
            FolderItem(id = 3, name = "Test Folder 3", parentFolderName = "", marked = false),
            FolderItem(id = 4, name = "Test Folder 4", parentFolderName = "", marked = false)
        )
        var actual = fetchFolder()
        assertEquals(
            actual, folderItems
        )

    }

    @Test
    fun testdeleteNotes() = runTest {
        deleteNotes(3)
        val fileItems = listOf(
            FileItem(
                id = 2,
                name = "Test File 1",
                content = "<p style=\"text-align: left;\">Sherlock is beautiful</p>",
                folder = "Test 2",
                marked = false
            )
        )
        var actual = fetchnotes()
        assertEquals(actual, fileItems)
    }

    @Test
    fun testfetchnotes() = runTest {
        var actual = fetchnotes()
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
        assertEquals(actual, fileItems)
    }

    @Test
    fun testfetchFolder() = runTest {
        var actual = fetchFolder()
        val folderItems = listOf(
            FolderItem(id = 2, name = "Test Folder 1", parentFolderName = "", marked = false),
            FolderItem(id = 3, name = "Test Folder 3", parentFolderName = "", marked = false),
            FolderItem(id = 4, name = "Test Folder 4", parentFolderName = "", marked = false),
            FolderItem(id = 5, name = "Test 2", parentFolderName = "Test Folder 2", marked = false)
        )
        assertEquals(
            actual, folderItems
        )
    }
}