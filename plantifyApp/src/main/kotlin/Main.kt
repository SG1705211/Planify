import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import com.mohamedrejeb.richeditor.annotation.ExperimentalRichTextApi
import com.mohamedrejeb.richeditor.model.rememberRichTextState
import com.mohamedrejeb.richeditor.ui.material3.RichTextEditor
import java.awt.Desktop
import java.awt.Dimension
import java.awt.Window
import java.io.File
import java.net.URI
import java.util.prefs.Preferences
import note.Notes
import summary.Summary
import ui.theme.AppTheme

// To generate different button based on if the button is currently selected.
@Composable
fun colorButton(boo: Boolean): ButtonColors {
    val tonalButtonColors =
        ButtonDefaults.buttonColors(
            containerColor =
                if (boo) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.secondaryContainer,
            contentColor =
                if (boo) MaterialTheme.colorScheme.onPrimary
                else MaterialTheme.colorScheme.onSurface,
        )
    return tonalButtonColors
}

enum class Section(val displayName: String) {
    Home("Home"),
    Calendar("Calendar"),
    Summary("Summary"),
    ToDoList("To-Do List"),
    Notes("Notes"),
    Help("Help"),
}

@Composable
fun appLayout(window: Window) {
    val sections = remember { Section.entries.toTypedArray() }
    val (selectedSection, setSelectedSection) = remember { mutableStateOf(Section.Home) }

    Row {
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxHeight(),
        ) {
            val commonButtonModifier =
                Modifier.weight(1f)
                    .padding(14.dp)
                    .size(width = 150.dp, height = 50.dp) // Adjusted height for practicality

            sections.forEach { section ->
                val isSelected = selectedSection == section
                Button(
                    onClick = { setSelectedSection(section) },
                    modifier = commonButtonModifier,
                    colors = colorButton(isSelected),
                ) {
                    Text(section.displayName)
                }
            }
        }
        Box(modifier = Modifier.weight(3f).fillMaxWidth(), contentAlignment = Alignment.Center) {
            when (selectedSection) {
                Section.Home -> magicHome()
                Section.Calendar -> Calendar()
                Section.Summary -> Summary()
                Section.ToDoList -> ToDoList()
                Section.Notes -> Notes()
                Section.Help -> {}
            }
        }
    }
}

@OptIn(ExperimentalRichTextApi::class)
@Composable
fun dialog(onClose: () -> Unit) {
    val state = rememberRichTextState()
    val path = "index.html"
    val content = File(path).readText(Charsets.UTF_8)
    println(content)
    state.setHtml(content)
    RichTextEditor(state = state, modifier = Modifier.fillMaxSize())
    state.setConfig(
        linkColor = Color.Blue,
        linkTextDecoration = TextDecoration.Underline,
        codeColor = Color.Yellow,
        codeBackgroundColor = Color.Transparent,
        codeStrokeColor = Color.LightGray,
    )
    Row(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        horizontalArrangement = Arrangement.End) {
            Button(onClick = onClose) { Text("Close") }
        }
}

fun main() = application {
    val openDialogue = remember { mutableStateOf(false) }
    val prefNode = Preferences.userRoot().node("Root")
    val windowState =
        WindowState(
            width = prefNode.getInt("Width", 1300).dp,
            height = prefNode.getInt("Height", 800).dp,
            position = WindowPosition(prefNode.getInt("X", 100).dp, prefNode.getInt("Y", 100).dp),
        )

    Window(onCloseRequest = ::exitApplication, title = "My Journal", state = windowState) {
        MenuBar { Menu("File", mnemonic = 'F') { Item("Help Menu", onClick = ::openHelp) } }
        window.minimumSize = Dimension(1300, 800)
        AppTheme { mainContent(window, openDialogue) }
    }
}

fun openHelp() {
    Desktop.getDesktop().browse(URI("https://sites.google.com/view/myjournalhelp"))
}

@Composable
fun mainContent(window: Window, openDialogue: MutableState<Boolean>) {
    Box(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
        appLayout(window)
        if (openDialogue.value) {
            dialog(onClose = { openDialogue.value = false })
        }
    }
}
