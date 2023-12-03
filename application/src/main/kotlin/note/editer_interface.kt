package note

import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mohamedrejeb.richeditor.model.rememberRichTextState
import com.mohamedrejeb.richeditor.ui.material3.RichTextEditor
import androidx.compose.runtime.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.input.key.*
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import com.mohamedrejeb.richeditor.model.RichTextState
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.awt.FileDialog
import java.io.File


@Composable
fun EditorInterface(state: RichTextState, selectedFile: FileItem, currentFolder: String) {
    Database.connect("jdbc:sqlite:chinook.db")
    var isSaveDialogOpen by remember { mutableStateOf(false) }
    if (isSaveDialogOpen) {
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

    Column(
        modifier = Modifier.fillMaxSize().padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(1.dp)
    ) {

        controlBar(
            state = state,
            fileName = selectedFile.name
        )


        // the place to text
        RichTextEditor(
            state = state,
            modifier = Modifier
                .fillMaxSize()
                .onKeyEvent {
                    if (it.type == KeyEventType.KeyDown) {
                        if (it.isMetaPressed ) {
                            when(it.key) {
                                Key.L -> {
                                    state.addParagraphStyle(ParagraphStyle(textAlign = TextAlign.Left))
                                    true
                                }
                                Key.E -> {
                                    state.addParagraphStyle(ParagraphStyle(textAlign = TextAlign.Center))
                                    true
                                }
                                Key.R -> {
                                    state.addParagraphStyle(ParagraphStyle(textAlign = TextAlign.Right))
                                    true
                                }
                                Key.B -> {
                                    state.toggleSpanStyle(SpanStyle(fontWeight = FontWeight.Bold))
                                    true
                                }
                                Key.I -> {
                                    state.toggleSpanStyle(SpanStyle(fontStyle = FontStyle.Italic))
                                    true
                                }
                                Key.U -> {
                                    state.toggleSpanStyle(SpanStyle(textDecoration = TextDecoration.Underline))
                                    true
                                }
                                Key.Minus -> {
                                    state.toggleSpanStyle(SpanStyle(textDecoration = TextDecoration.LineThrough))
                                    true
                                }
                                Key.T -> {
                                    state.toggleSpanStyle(SpanStyle(fontSize = 28.sp))
                                    true
                                }
                                Key.D -> {
                                    print("DD")
                                    state.toggleSpanStyle(SpanStyle(color = Color.Red))
                                    true
                                }

                                Key.Y -> {
                                    print("YYYY")
                                    state.toggleSpanStyle(SpanStyle(background = Color.Yellow))
                                    true
                                }

                                // save file
                                Key.S -> {
                                    isSaveDialogOpen = true
                                    // highlight database access ============================================
//                                    transaction {
//                                        Table__File.update({(Table__File.name eq selectedFile) and
//                                                (Table__File.folderName eq currentFolder) }) {
//                                            it[content] = state.toHtml()
//                                        }
//                                    }
                                    runBlocking {
                                        launch {
                                            val updateRequest = updateTodoItem(selectedFile.id,
                                                FileItem(
                                                    id = selectedFile.id,
                                                    content = state.toHtml(),
                                                    folder = "",
                                                    marked = false,
                                                    name = "a"
                                                ))
                                        }
                                    }
                                    // highlight database access ============================================
                                    true
                                }

                                // export file
                                Key.P -> {
                                    val window = ComposeWindow()
                                    val fileDialog = FileDialog(window, "Save File", FileDialog.SAVE)
                                    fileDialog.file = "${selectedFile}.html" // Default file name
                                    fileDialog.isVisible = true

                                    val file = fileDialog.file?.let { fileName ->
                                        File(fileDialog.directory, fileName)
                                    }

                                    // Write the HTML content to the file
                                    file?.let {
                                        it.writeText(state.toHtml())
                                    }
                                    true
                                }

                                // open file
                                Key.O -> {
                                    // Open file dialog to choose a file
                                    val window = ComposeWindow()
                                    val fileDialog = FileDialog(window, "Select a File", FileDialog.LOAD)
                                    fileDialog.isVisible = true

                                    val file = fileDialog.file?.let { fileName ->
                                        File(fileDialog.directory, fileName)
                                    }

                                    // Read the file
                                    file?.let {
                                        val content = it.readText()
                                        if (file.extension.equals("html", ignoreCase = true)) {
                                            state.setHtml(content)
                                        }

                                    }
                                    true
                                }

                                else -> false
                            }
                        } else false
                    } else false
                }
        )

    }
}


