package summary



import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.round

data class TodoItem(
    val id: Int, val primaryTask: String, val secondaryTask: String, val priority: Int,
    var completed: Boolean, val section: String, var datetime: String, val duration: Int,
    val pid: Int, val recur: String
)

@Composable
fun TaskProgress(
    progress: Float,
    modifier: Modifier = Modifier
        .padding(20.dp)
        .fillMaxSize()
) {
    Box(
        modifier = modifier.size(200.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            backgroundColor = MaterialTheme.colorScheme.onSecondary,
            color = MaterialTheme.colorScheme.primary,
            progress = progress,
            modifier = Modifier.size(200.dp),
            strokeWidth = 12.dp
        )
        Column (
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ){
            Text(
                text = "Task Progress",
                color = MaterialTheme.colorScheme.primary,
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            var tem_str = round((progress * 100).toDouble()).toString() + "%"
            Text(
                text = "${tem_str}",
                color = MaterialTheme.colorScheme.primary,
                style = TextStyle(
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                ),
            )
        }


    }
}
