package com.mayank.vamanaapp.Frontend.Doctor

import android.util.Log
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mayank.vamanaappversion2.Backend.API_ViewModel
import com.mayank.vamanaappversion2.Backend.getSharedPreferences
import com.mayank.vamanaappversion2.Constants
import com.mayank.vamanaappversion2.Modals.Patient
import com.mayank.vamanaappversion2.Modals.PatientQuestion
import com.mayank.vamanaappversion2.Modals.Question
import com.mayank.vamanaappversion2.Modals.QuestionDetail

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnswerQuestionScreen(
    patient: Patient,
    apiviewmodel: API_ViewModel,
    categories: List<Question>,
    onSubmitResponse: (onSubmitted: () -> Unit) -> Unit,
    onSubmitResponseFinal: () -> Unit
) {


    LaunchedEffect(patient) {
        Log.i("Questions", patient.questions.toString())
    }



    var selectedCategoryIndex by remember { mutableStateOf(0) }
    val totalCategories = categories.size
    val selectedCategory = categories.getOrElse(selectedCategoryIndex) { categories.last() }

    val progress = selectedCategoryIndex.toFloat() / totalCategories.coerceAtLeast(1)

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "Answer Questions",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Text(
                text = "Patient UHID/PID: ${patient.uhid}",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            CategoryDropdown(
                categories = categories,
                selectedCategoryIndex = selectedCategoryIndex,
                onCategorySelected = { selectedCategoryIndex = it }
            )

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
            )
            Text(
                text = "Progress: ${(progress * 100).toInt()}%",
                fontSize = 14.sp,
                textAlign = TextAlign.End,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Column(
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                QuestionsSection(
                    questions = selectedCategory.questions,

                    onOptionSelected = { questionId, option ->
//                        handleOptionSelection(patient, questionId, option)
                    },
                    getCheckedState = { questionId, option ->
                                      false
//                        isOptionChecked(patient.questions!!, questionId, option)
                    },
                    patient,
                    apiviewmodel
                )

                Spacer(modifier = Modifier.height(16.dp))

                NavigationButtons(
                    currentIndex = selectedCategoryIndex,
                    totalCategories = totalCategories,
                    onPrevious = { if (selectedCategoryIndex > 0) selectedCategoryIndex-- },
                    onNextOrSubmit = {
                        if (selectedCategoryIndex < totalCategories - 1) {
                            onSubmitResponse()
                            {
                                selectedCategoryIndex++

                            }

                        } else {
                            onSubmitResponseFinal()

                        }
                    }
                )
            }
        }
    }
}

@Composable
fun QuestionsSection(
    questions: List<QuestionDetail>,
    onOptionSelected: (String, String) -> Unit,
    getCheckedState: (String, String) -> Boolean,
    patient: Patient,
    apiviewmodel: API_ViewModel
) {
    var context = LocalContext.current
    var role = getSharedPreferences(context).getString("role","admin")
    var isDoctor = role == "staff"
    Column(modifier = Modifier.fillMaxWidth()) {
        questions.forEach { question ->
            val existingQuestion = patient.questions!!.find { it.questionUID == question.id }
            


            Text(
                text = question.question,
                style = MaterialTheme.typography.bodyLarge,
                color = Constants.SecondaryColor,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            question.options.forEach { option ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onOptionSelected(question.id!!, option) }
                        .padding(vertical = 4.dp)
                ) {
//                    var ischecked = remember {
//                        mutableStateOf(getCheckedState(question.id!!, option))
//                    }


                    Checkbox(
                        checked = isOptionChecked(patient,question.id,option),
                        onCheckedChange = {
                            apiviewmodel.updatePatientResponce(patient.uhid,question.id!! , option,question.question)
                            Log.d("Check",option+" clicked")
                        },
                        enabled = isDoctor
                    )
                    Text(
                        text = option,
                        modifier = Modifier.padding(start = 8.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            Divider(
                modifier = Modifier.padding(vertical = 8.dp),
                color = Constants.TertiaryColor
            )
        }
    }
}

@Composable
fun NavigationButtons(
    currentIndex: Int,
    totalCategories: Int,
    onPrevious: () -> Unit,
    onNextOrSubmit: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        if (currentIndex > 0) {
            Button(onClick = onPrevious) {
                Text("Previous")
            }
        }
        Button(onClick = onNextOrSubmit) {
            Text(if (currentIndex < totalCategories - 1) "Submit & Next" else "Submit")
        }
    }
}

@Composable
fun CategoryDropdown(
    categories: List<Question>,
    selectedCategoryIndex: Int,
    onCategorySelected: (Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(10.dp)
                .border(4.dp, Constants.SecondaryColor, RoundedCornerShape(10.dp)),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = categories.getOrNull(selectedCategoryIndex)?.category
                    ?: "Select Category",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .wrapContentWidth()
                    .padding(8.dp)
                    .clickable { expanded = true }
            )
            Icon(
                imageVector = Icons.Filled.KeyboardArrowDown,
                contentDescription = "Expand Dropdown",
                tint = Color.Black,
                modifier = Modifier
                    .padding(5.dp)
                    .clickable { expanded = true }
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            categories.forEachIndexed { index, category ->
                DropdownMenuItem(
                    text = { Text(category.category) },
                    onClick = {
                        onCategorySelected(index)
                        expanded = false
                    }
                )
            }
        }
    }
}

private fun handleOptionSelection(
    patient: Patient,
    questionId: String,
    option: String
) {
    val existingQuestion = patient.questions!!.find { it.questionUID == questionId }
    if (existingQuestion != null) {
        val updatedAnswers = existingQuestion.answers.toMutableList().apply {
            if (contains(option)) remove(option) else add(option)
        }
        val updatedQuestion = existingQuestion.copy(answers = updatedAnswers)
        val index = patient.questions!!.indexOf(existingQuestion)
        patient.questions = patient.questions!!.toMutableList().apply {
            set(index, updatedQuestion)
        }
    } else {
        patient.questions = patient.questions!!.toMutableList().apply {
            add(PatientQuestion(questionUID = questionId, question = "", answers = listOf(option)))
        }
    }
}

private fun isOptionChecked(patient: Patient, id: String?, option: String): Boolean {
    // Check if questions are null
    if (patient.questions.isNullOrEmpty()) {
        return false
    }

    // Find the question with the given ID
    val question = patient.questions!!.find { it.questionUID == id }
    if (question != null) {
        // Check if the option exists in the answers list
        return question.answers.contains(option)
    }

    // Return false if the question does not exist
    return false
}
