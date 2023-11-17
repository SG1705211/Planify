package note
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mohamedrejeb.richeditor.model.rememberRichTextState
import com.mohamedrejeb.richeditor.ui.material3.RichTextEditor
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.style.TextDecoration

@Composable
fun NotesEditor() {
    var state = rememberRichTextState()
    var isFile by remember { mutableStateOf(false) }
    var folderPath = remember { mutableStateListOf<String>("")}

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
                .weight(0.26f)  // 15% of parent's height
                .padding(top = 10.dp)
            // other modifiers, content, etc.
        ) {
            /////////// do some shit arrange
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                Box(modifier = Modifier.fillMaxWidth().weight(0.95f)) {
                    val (bo, li) = NoteList(state)
                    isFile = bo
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
                .weight(0.74f)  // 85% of parent's height
            // other modifiers, content, etc.
        ) {
            if (isFile) {
                EditorInterface(state)
            }
        }
    }
}
