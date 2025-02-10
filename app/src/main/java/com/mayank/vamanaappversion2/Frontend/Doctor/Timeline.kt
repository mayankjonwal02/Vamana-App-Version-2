import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mayank.vamanaappversion2.Backend.API_ViewModel
import com.mayank.vamanaappversion2.Modals.Patient
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Timeline(apiViewmodel: API_ViewModel) {
    val patients by apiViewmodel.all_patients.collectAsState()
    var showDaily by remember { mutableStateOf(true) }
    val chartData = processPatientData(patients, showDaily)

    LaunchedEffect(Unit) {
        apiViewmodel.GetAllPatients()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Patient Admissions Over Time",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Toggle between daily/monthly
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            FilterChip(
                selected = showDaily,
                onClick = { showDaily = true },
                label = { Text("Daily") }
            )
            FilterChip(
                selected = !showDaily,
                onClick = { showDaily = false },
                label = { Text("Monthly") }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (chartData.isEmpty()) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Text("No admission data available")
            }
        } else {
            LineChart(
                dataPoints = chartData,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            )
        }
    }
}

@Composable
fun LineChart(dataPoints: List<Pair<Date, Int>>, modifier: Modifier = Modifier) {
    val maxY = dataPoints.maxOf { it.second }.toFloat()
    val minY = dataPoints.minOf { it.second }.toFloat()
    val dateFormat = if (dataPoints.size > 30)
        SimpleDateFormat("MMM yyyy", Locale.getDefault())
    else
        SimpleDateFormat("dd MMM", Locale.getDefault())

    Canvas(modifier = modifier) {
        val xSpace = (size.width*0.7.toFloat()) / (dataPoints.size - 1)
        val ySpace = size.height / (maxY - minY)

        // Draw Y-axis labels and points
        for (i in 0..4) {
            val yPos = size.height - (i * size.height / 4)
            val value = (minY + (maxY - minY) * (i / 4f)).toInt()

            // Draw Y-axis grid line
            drawLine(
                color = Color.LightGray,
                start = Offset(0f, yPos),
                end = Offset(size.width, yPos),
                strokeWidth = 1.dp.toPx()
            )

            // Draw Y-axis value
            drawContext.canvas.nativeCanvas.apply {
                drawText(
                    value.toString(),
                    10f, // X position for Y-axis labels
                    yPos + 15, // Y position for Y-axis labels
                    android.graphics.Paint().apply {
                        textSize = 12.sp.toPx()
                        color = android.graphics.Color.BLACK
                    }
                )
            }

            // Draw Y-axis point (small circle)
            drawCircle(
                color = Color.Black,
                radius = 3.dp.toPx(),
                center = Offset(0f, yPos)
            )
        }

        // Draw line path
        val path = Path().apply {
            dataPoints.forEachIndexed { index, (date, count) ->
                val x = xSpace * index
                val y = size.height - (count - minY) * ySpace
                if (index == 0) moveTo(x, y) else lineTo(x, y)
            }
        }

        drawPath(
            path = path,
            color = Color.Blue,
            style = Stroke(width = 2.dp.toPx())
        )

        // Draw data points
        dataPoints.forEachIndexed { index, (date, count) ->
            val x = xSpace * index
            val y = size.height - (count - minY) * ySpace

            // Draw circle for data point
            drawCircle(
                color = Color.Blue,
                radius = 4.dp.toPx(),
                center = Offset(x, y)
            )

            // Draw value above the data point
            drawContext.canvas.nativeCanvas.apply {
                drawText(
                    count.toString(),
                    x - 10, // Adjust X position for value label
                    y - 10, // Adjust Y position for value label
                    android.graphics.Paint().apply {
                        textSize = 12.sp.toPx()
                        color = android.graphics.Color.BLACK
                    }
                )
            }

            // Draw X-axis labels and points
            if (index % 3 == 0 || index == dataPoints.size - 1) {
                drawContext.canvas.nativeCanvas.apply {
                    drawText(
                        dateFormat.format(date),
                        x,
                        size.height - 20,
                        android.graphics.Paint().apply {
                            textSize = 12.sp.toPx()
                            color = android.graphics.Color.BLACK
                        }
                    )
                }

                // Draw X-axis point (small circle)
                drawCircle(
                    color = Color.Black,
                    radius = 3.dp.toPx(),
                    center = Offset(x, size.height)
                )
            }
        }
    }
}

private fun processPatientData(patients: List<Patient>, daily: Boolean): List<Pair<Date, Int>> {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
    val groupFormat = if (daily)
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    else
        SimpleDateFormat("yyyy-MM", Locale.getDefault())

    val groupedData = patients.groupBy {
        val date = dateFormat.parse(it.createdAt)
        groupFormat.format(date)
    }

    return groupedData.entries
        .sortedBy { groupFormat.parse(it.key) }
        .map {
            Pair(groupFormat.parse(it.key), it.value.size)
        }
}