import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import ui.theme.AppTheme
import androidx.compose.runtime.Composable

import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import summary.*
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.width
import java.awt.Dimension
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.*
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.window.*
import com.mohamedrejeb.richeditor.model.rememberRichTextState
import com.mohamedrejeb.richeditor.ui.material3.RichTextEditor
import com.mohamedrejeb.richeditor.*
import note.*
import java.awt.Window
import java.io.File
import java.util.prefs.Preferences



@Composable
fun MagicHome() {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start
    ) {
        items(4) { index ->
            when (index) {
                0 -> HomeSummary()
                1 -> homeCalendar()
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
fun AppLayout(window: Window) {
    val perf = Preferences.userRoot().node("Root")
    perf.putInt("X", window.x)
    perf.putInt("Y", window.y)
    perf.putInt("Width", window.width)
    perf.putInt("High", window.height)
    val (selectedSection, setSelectedSection) = remember { mutableStateOf("Section 1") }
    val listOfBooleans = remember {
        listOf(
            mutableStateOf(true),
            mutableStateOf(false),
            mutableStateOf(false),
            mutableStateOf(false),
            mutableStateOf(false),
            mutableStateOf(false)
        )
    }
    Row() {
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxHeight()
                .onSizeChanged {
                    perf.putInt("X", window.x)
                    perf.putInt("Y", window.y)
                    perf.putInt("Width", window.width)
                    perf.putInt("High", window.height)
                }

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
                    listOfBooleans[5].value = false
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
                    listOfBooleans[5].value = false
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
                    listOfBooleans[5].value = false
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
                    listOfBooleans[5].value = false
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
                    listOfBooleans[5].value = false
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
                "Help" -> {}
                else -> MagicHome()
            }
        }
    }
}

@Composable
fun Dialog(onClose: () -> Unit) {
    val state = rememberRichTextState()
    val path = "index.html"
    val content = File(path).readText(Charsets.UTF_8)
    println(content)
    state.setHtml(content)

    RichTextEditor(
        state = state,
        modifier = Modifier.fillMaxSize()
    )
    state.setConfig(
        linkColor = Color.Blue,
        linkTextDecoration = TextDecoration.Underline,
        codeColor = Color.Yellow,
        codeBackgroundColor = Color.Transparent,
        codeStrokeColor = Color.LightGray)
    Row(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        horizontalArrangement = Arrangement.End
    ) {
        Button(onClick = onClose) {
            Text("Close")
        }
    }
}


fun main() = application {
    var openDialogue = remember { mutableStateOf(false) }
    val window = Window(
        onCloseRequest = ::exitApplication,
        title = "My Journal",
        state = WindowState(
            width = Preferences.userRoot().node("Root").getInt("Width", 1300).dp,
            height = Preferences.userRoot().node("Root").getInt("High", 800).dp,
            position = WindowPosition(
                Preferences.userRoot().node("Root").getInt("X", 100).dp,
                Preferences.userRoot().node("Root").getInt("Y", 100).dp
            )
        )
    ) {
        MenuBar{
            Menu("Help", mnemonic = 'F') {
                Item(
                    text = "Help Menu",
                    onClick = {
                        openDialogue.value = true
                    }
                )
            }
        }
        window.minimumSize = Dimension(1300, 800)
        AppTheme {
            Box(
                modifier = Modifier.background(MaterialTheme.colorScheme.background)
            ) {
                AppLayout(window)
                if (openDialogue.value) {
                    Dialog(onClose = { openDialogue.value = false })
                }
            }
        }
    }
}

