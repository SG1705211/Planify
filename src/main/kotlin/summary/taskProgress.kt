package summary

import DatabaseManager
import TodoTable
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
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.math.round
import kotlin.random.Random

data class TodoItem(
    val id: Int, val primaryTask: String, val secondaryTask: String, val priority: Int,
    var completed: Boolean, val section: String, val datetime: String, val duration: Int
)

@Composable
fun TaskProgress(
    progress: Float,
    modifier: Modifier = Modifier
        .padding(20.dp)
        .fillMaxSize()
) {
    val manager = DatabaseManager()
                val db = manager.setupDatabase()
    val todoListFromDb: MutableList<TodoItem> = mutableListOf()

    transaction {
        TodoTable.selectAll().forEach {
            todoListFromDb.add(
                TodoItem(
                    it[TodoTable.id],
                    it[TodoTable.primaryTask],
                    it[TodoTable.secondaryTask],
                    it[TodoTable.priority],
                    it[TodoTable.completed],
                    it[TodoTable.section],
                    it[TodoTable.datetime],
                    it[TodoTable.duration]
                )
            )
        }
    }
    var progress_cur = (todoListFromDb.count{it.completed == true}.toDouble() / todoListFromDb.size).toFloat()
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
