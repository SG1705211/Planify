package note
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mohamedrejeb.richeditor.model.rememberRichTextState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration

@Composable
fun NotesEditor() {
    var state = rememberRichTextState()
    var isFile by remember { mutableStateOf(false) }
    var folderPath = remember { mutableStateListOf<String>("")}
    var selectedFile by remember { mutableStateOf(FileItem(2,"a","a","sherlock beautiful",true)) }
    var currentFolder by remember { mutableStateOf("/") }

    // Set the default config of the rich text state
    state.setConfig(
        linkColor = Color.Blue,
        linkTextDecoration = TextDecoration.Underline,
        codeColor = Color.Yellow,
        codeBackgroundColor = Color.Transparent,
        codeStrokeColor = Color.LightGray,
    )

    Row(
        modifier = Modifier.fillMaxHeight()
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .weight(0.26f)
                .padding(top = 10.dp)

        ) {

            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                Box(modifier = Modifier.fillMaxWidth().weight(0.95f)) {
                    val (bo, li, fi, fo) = NoteList(state)
                    isFile = bo
                    selectedFile = fi
                    currentFolder = fo
                    folderPath.clear()
                    li.forEach {
                        folderPath.add(it)
                    }
                }
                Box(modifier = Modifier.fillMaxWidth().weight(0.05f)) {
                    LazyRow(modifier = Modifier.align(Alignment.TopStart)) {
                        items(folderPath.size) {
                            if (folderPath[it] == "") {
                                Text("Root", color = MaterialTheme.colorScheme.tertiary)
                            } else {
                                Text(" -> " + folderPath[it], color = MaterialTheme.colorScheme.tertiary)
                            }
                        }
                    }
                }
            }


        }
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .weight(0.74f)

        ) {
            if (isFile) {
                EditorInterface(state, selectedFile, currentFolder)
            }
        }
    }
}
