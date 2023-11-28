
package summary
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.math.round

//barchart
//https://github.com/developerchunk/BarGraph-JetpackCompose/tree/main/app/src/main/java/com/example/customchar

@Composable
fun Chart(
    data: Map<String, Float>,
    barwidth: Dp,
    graphWidth: Dp,
    max_value: Float
) {

    // BarGraph Dimensions
    val barGraphHeight by remember { mutableStateOf(300.dp) }
    val barGraphWidth by remember { mutableStateOf(barwidth) }
    // Scale Dimensions
    val scaleYAxisWidth by remember { mutableStateOf(50.dp) }
    val scaleLineWidth by remember { mutableStateOf(2.dp) }

        Column(
            modifier = Modifier
                .padding(50.dp)
                .width(graphWidth),
            verticalArrangement = Arrangement.Top
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(barGraphHeight),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.Start
            ) {
                // scale Y-Axis
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(scaleYAxisWidth),
                    contentAlignment = Alignment.BottomCenter
                ) {

                }


                // Y-Axis Line
                Column() {
                    Text("hrs")
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(scaleLineWidth)
                            .background(Color.Black)
                    )
                }


                // graph
                data.forEach {
                    Box(
                        modifier = Modifier
                            .padding(start = barGraphWidth, bottom = 5.dp)
                            .clip(CircleShape)
                            .width(barGraphWidth)
                            .fillMaxHeight(it.value / max_value)
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center,
                    ) {
                        BoxWithConstraints {
                            val text = it.value.toString() // You can format the text as needed

                            Text(
                                text = text,
                                color = Color.White
                            )
                        }
                    }
                }

            }

            // X-Axis Line
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(scaleLineWidth)
                    .background(Color.Black)
            )

            // Scale X-Axis
            Row(
                modifier = Modifier
                    .padding(start = scaleYAxisWidth + barGraphWidth + scaleLineWidth * 10)
                    .horizontalScroll(rememberScrollState())
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(barGraphWidth - 8.dp)
            ) {

                data.keys.forEach {
                    Text(
                        modifier = Modifier.width(barGraphWidth + 8.dp),
                        text = it,
                        textAlign = TextAlign.Center
                    )
                }

            }

        }

}