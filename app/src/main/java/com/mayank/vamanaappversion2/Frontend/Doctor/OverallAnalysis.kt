package com.example.analysis

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mayank.vamanaappversion2.Backend.API_ViewModel

// Constants Object for Theme Colors
object ThemeColors {
    val PrimaryColor = Color(0xffBCFFC2)
    val SecondaryColor = Color(0xff018749)
    val TertiaryColor = Color(0xff1CAC78)
    val PrimaryColorSecond = Color(0xff4FFFB0)
}

// Data Classes
data class OverAllAnalysisResponse(
    val message: String,
    val executed: Boolean,
    val questions: List<QuestionAnalysis>
)

data class QuestionAnalysis(
    val question: String,
    val options: List<OptionAnalysis>
)

data class OptionAnalysis(
    val option: String,
    val value: String
)

// Jetpack Compose Screen
@Composable
fun AnalysisScreen(apiViewmodel: API_ViewModel) {

    val overAllAnalysisResponse by apiViewmodel.analysis.collectAsState()
    LaunchedEffect(Unit) {
        apiViewmodel.GetOverallAnalysis()
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Overall Analysis",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = ThemeColors.SecondaryColor,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyColumn {
            items(overAllAnalysisResponse) { question ->
                AnalysisQuestionItem(question)
            }
        }
    }
}

// Individual Question Card
@Composable
fun AnalysisQuestionItem(question: com.mayank.vamanaappversion2.Backend.QuestionAnalysis) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = ThemeColors.PrimaryColor),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)

        ) {
            Text(
                text = "Q: "+question.question,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = ThemeColors.TertiaryColor,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            question.options.forEach { option ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = "• ${option.option}: ",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = ThemeColors.SecondaryColor
                    )
                    Text(
                        text = " -- ${option.value}",
                        fontSize = 16.sp,
                        color = Color.Black,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}



