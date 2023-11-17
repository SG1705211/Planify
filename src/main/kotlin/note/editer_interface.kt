package note

import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.input.key.*
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import com.mohamedrejeb.richeditor.model.RichTextState

import com.mohamedrejeb.richeditor.ui.material3.OutlinedRichTextEditor



@Composable
fun EditorInterface(state: RichTextState) {
    Column(
        modifier = Modifier.fillMaxSize().padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(1.dp)
    ) {

        controlBar(
            state = state,
        )

        // the place to text
        RichTextEditor(
            state = state,
            modifier = Modifier
                .fillMaxSize()
                .onKeyEvent {
                    if (it.isCtrlPressed) {
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
                            Key.T -> {
                                state.toggleSpanStyle(SpanStyle(textDecoration = TextDecoration.LineThrough))
                                true
                            }
                            Key.H -> {
                                state.toggleSpanStyle(SpanStyle(fontSize = 28.sp))
                                true
                            }
                            Key.D -> {
                                state.toggleSpanStyle(SpanStyle(color = Color.Red))
                                true
                            }
                            Key.Y -> {
                                state.toggleSpanStyle(SpanStyle(background = Color.Yellow))
                                true
                            }

                            else -> false
                        }
                    } else false
                },
        )
    }
}
