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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mayank.vamanaappversion2.Backend.API_ViewModel
import com.mayank.vamanaappversion2.Backend.getSharedPreferences
import com.mayank.vamanaappversion2.Modals.Patient
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Timeline(apiViewmodel: API_ViewModel) {
    val patients by apiViewmodel.all_patients.collectAsState()
    var showDaily by remember { mutableStateOf(true) }
    val chartData = processPatientData(patients, showDaily)
    var context = LocalContext.current
    LaunchedEffect(Unit) {
        var instituteId = getSharedPreferences(context).getString("institute_id","Not Available")?:""
        apiViewmodel.GetAllPatientsByInstituteID(instituteId)
    }

    if(!patients.isNullOrEmpty()){
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
}

@Composable
fun LineChart(dataPoints: List<Pair<Date, Int>>, modifier: Modifier = Modifier) {
    if (dataPoints.isEmpty()) return // Prevent drawing if no data

    val maxY = dataPoints.maxOf { it.second }.toFloat()
    val minY = dataPoints.minOf { it.second }.toFloat()
    val dateFormat = if (dataPoints.size > 30)
        SimpleDateFormat("MMM yyyy", Locale.getDefault())
    else
        SimpleDateFormat("dd MMM", Locale.getDefault())

    Canvas(modifier = modifier) {
        val safeDataSize = maxOf(1, dataPoints.size - 1) // Avoid division by zero
        val xSpace = (size.width * 0.7f) / safeDataSize
        val ySpace = if (maxY == minY) 1f else size.height / (maxY - minY)

        // Draw line path
        val path = Path().apply {
            dataPoints.forEachIndexed { index, (_, count) ->
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

            drawCircle(
                color = Color.Blue,
                radius = 4.dp.toPx(),
                center = Offset(x, y)
            )

            // Draw value above the point
            drawContext.canvas.nativeCanvas.apply {
                drawText(
                    count.toString(),
                    x - 10,
                    y - 10,
                    android.graphics.Paint().apply {
                        textSize = 12.sp.toPx()
                        color = android.graphics.Color.BLACK
                    }
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