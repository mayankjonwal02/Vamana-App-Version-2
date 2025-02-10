import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import androidx.compose.foundation.Canvas
import androidx.compose.ui.geometry.Offset

@Composable
fun CustomCircularProgressIndicator(
    progress: Float, // Progress between 0f and 1f
    size: Float = 100f, // Size of the indicator
    strokeWidth: Float = 8f,
    backgroundColor: Color = Color.Gray.copy(alpha = 0.3f),
    progressColor: Color = Color.White
) {
    Canvas(
        modifier = Modifier
            .size(size.dp)
            .clip(CircleShape)
    ) {
        val canvasSize = size.dp.toPx()
        val stroke = strokeWidth.dp.toPx()

        // Draw background circle
        drawArc(
            color = backgroundColor,
            startAngle = 0f,
            sweepAngle = 360f,
            useCenter = false,
            topLeft = Offset(stroke / 2, stroke / 2),
            size = Size(canvasSize - stroke, canvasSize - stroke),
            style = Stroke(width = stroke, cap = StrokeCap.Round)
        )

        // Draw progress arc
        drawArc(
            color = progressColor,
            startAngle = -90f, // Start from top
            sweepAngle = 360 * progress, // Progress
            useCenter = false,
            topLeft = Offset(stroke / 2, stroke / 2),
            size = Size(canvasSize - stroke, canvasSize - stroke),
            style = Stroke(width = stroke, cap = StrokeCap.Round)
        )
    }
}

@Composable
fun AnimatedCircularProgressIndicator() {
    var progress by remember { mutableStateOf(0f) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(50)
            progress += 0.02f
            if (progress >= 1f) progress = 0f
        }
    }

    CustomCircularProgressIndicator(progress = progress)
}

@Composable
fun FullScreenLoader() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.Black.copy(alpha = 0.5f) // Semi-transparent background
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            AnimatedCircularProgressIndicator()
        }
    }
}
