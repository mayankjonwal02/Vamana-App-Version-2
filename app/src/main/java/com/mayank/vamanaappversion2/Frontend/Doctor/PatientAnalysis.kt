package com.mayank.vamanaappversion2.Frontend.Doctor

import android.annotation.SuppressLint
import android.app.TimePickerDialog
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
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
import kotlinx.coroutines.launch
import java.util.Calendar

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalysisQuestionScreen(
    patient: Patient,
    apiviewmodel: API_ViewModel,

    onSubmitResponse: (onSubmitted: () -> Unit) -> Unit,
    onSubmitResponseFinal: () -> Unit
) {
    val questions by apiviewmodel.all_analysis_questions.collectAsState()
    var drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()



    val configuration = LocalConfiguration.current

    // Get screen width and height in pixels
    val screenWidth = configuration.screenWidthDp
    val screenHeight = configuration.screenHeightDp
    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = true,
//        onDismissRequest = { drawerState = false },
        modifier = Modifier.width((screenWidth ).toFloat().dp),
        drawerContent = {

        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Analyse Patient ") }
                )
            }
        ) {
            Box(modifier = Modifier.fillMaxSize().padding(top = 30.dp)) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Patient UHID/PID: ${patient.uhid}",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))



                    Spacer(modifier = Modifier.height(16.dp))

                    Column(
                        modifier = Modifier.verticalScroll(rememberScrollState())
                    ) {
                        AnalysisQuestionsSection(
                            questions = questions,
                            onOptionSelected = { questionId, option -> },
                            getCheckedState = { questionId, option -> false },
                            patient,
                            apiviewmodel
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        AnalysisNavigationButtons(
                         
                          
                            onNextOrSubmit = {
                                
                                    onSubmitResponseFinal()
                                
                            }
                        )
                    }
                }
            }
        }
    }
}




@Composable
fun AnalysisQuestionsSection(
    questions: List<QuestionDetail>,
    onOptionSelected: (String, String) -> Unit,
    getCheckedState: (String, String) -> Boolean,
    patient: Patient,
    apiviewmodel: API_ViewModel
) {
    val context = LocalContext.current
    val role = getSharedPreferences(context).getString("role", "admin")
    val isDoctor = role == "staff"

    Column(modifier = Modifier.fillMaxWidth()) {
        questions.forEach { question ->
            val existingQuestion = patient.Analysis!!.find { it.questionUID == question.id }

            Text(
                text = question.question,
                style = MaterialTheme.typography.bodyLarge,
                color = Constants.SecondaryColor,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            when (question.inputtype) {
                "text", "textarea" -> {
                    var textValue by remember { mutableStateOf(existingQuestion?.answers?.get(0) ?: "") }
                    OutlinedTextField(
                        value = textValue,
                        readOnly = !isDoctor,
                        onValueChange = {
                            textValue = it
//                            existingQuestion?.answers = listOf(textValue)
                            apiviewmodel.updatePatientAnalysisResponce(patient.uhid, question.id!!, it, question.question,false)
                        },
                        label = { Text("Enter your response") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                "number" -> {
                    var numberValue by remember { mutableStateOf(existingQuestion?.answers?.get(0) ?: "") }
                    OutlinedTextField(
                        value = numberValue,
                        readOnly = !isDoctor,
                        onValueChange = {
                            if (it.all { char -> char.isDigit() }) {
                                numberValue = it
//                                existingQuestion?.answers = listOf(numberValue)
                                apiviewmodel.updatePatientAnalysisResponce(patient.uhid, question.id!!, it, question.question,false)
                            }
                        },
                        label = { Text("Enter a number") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                "radio" -> {
                    question.options.forEach { option ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onOptionSelected(question.id!!, option) }
                                .padding(vertical = 4.dp)
                        ) {
                            RadioButton(
                                selected = isOptionCheckedAnalysis(patient, question.id, option),
                                onClick = {
                                    apiviewmodel.updatePatientAnalysisResponce(patient.uhid, question.id!!, option, question.question,false)
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
                }

                "checkbox" -> {
                    question.options.forEach { option ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onOptionSelected(question.id!!, option) }
                                .padding(vertical = 4.dp)
                        ) {
                            Checkbox(
                                checked = isOptionCheckedAnalysis(patient, question.id, option),
                                onCheckedChange = {
                                    apiviewmodel.updatePatientAnalysisResponce(patient.uhid, question.id!!, option, question.question,true)
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
                }

                "dropdown" -> {
                    var expanded by remember { mutableStateOf(false) }
                    var selectedOption by remember { mutableStateOf(existingQuestion?.answers?.get(0) ?: "") }

                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedButton(onClick = { expanded = true }, enabled = isDoctor) {
                            Text(text = if (selectedOption.isEmpty()) "Select an option" else selectedOption)
                        }
                        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                            question.options.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option) },
                                    onClick = {
                                        selectedOption = option
                                        expanded = false
//                                        existingQuestion?.answers = listOf(selectedOption)
                                        apiviewmodel.updatePatientAnalysisResponce(patient.uhid, question.id!!, option, question.question,false)
                                    }
                                )
                            }
                        }
                    }
                }

                "date", "datetime", "time" -> {
                    var selectedValue by remember { mutableStateOf(existingQuestion?.answers?.get(0) ?: "") }
//                    if(!selectedValue.isNullOrEmpty())
//                    {
//                        existingQuestion?.answers = listOf(selectedValue)
//                    }

                    when (question.inputtype) {
                        "date" -> {
                            val context = LocalContext.current
                            val calendar = Calendar.getInstance()
                            val year = calendar.get(Calendar.YEAR)
                            val month = calendar.get(Calendar.MONTH)
                            val day = calendar.get(Calendar.DAY_OF_MONTH)

                            val datePickerDialog = android.app.DatePickerDialog(
                                context,
                                { _, selectedYear, selectedMonth, selectedDay ->
                                    val formattedDate =
                                        "$selectedDay/${selectedMonth + 1}/$selectedYear"
                                    selectedValue = formattedDate
                                    apiviewmodel.updatePatientAnalysisResponce(
                                        patient.uhid,
                                        question.id!!,
                                        formattedDate,
                                        question.question,
                                        false
                                    )
                                },
                                year, month, day
                            )

                            OutlinedButton(onClick = { datePickerDialog.show() }, enabled = isDoctor) {
                                Text(text = if (selectedValue.isEmpty()) "Select Date" else selectedValue)
                            }
                        }

                        "time" -> {
                            val context = LocalContext.current
                            val calendar = Calendar.getInstance()
                            val hour = calendar.get(Calendar.HOUR_OF_DAY)
                            val minute = calendar.get(Calendar.MINUTE)

                            val timePickerDialog = TimePickerDialog(
                                context,
                                { _, selectedHour, selectedMinute ->
                                    val formattedTime = String.format("%02d:%02d", selectedHour, selectedMinute)
                                    selectedValue = formattedTime
                                    apiviewmodel.updatePatientAnalysisResponce(patient.uhid, question.id!!, formattedTime, question.question,false)
                                },
                                hour, minute, true
                            )

                            OutlinedButton(onClick = { timePickerDialog.show() }, enabled = isDoctor) {
                                Text(text = if (selectedValue.isEmpty()) "Select Time" else selectedValue)
                            }
                        }

                        "datetime" -> {
                            val context = LocalContext.current
                            val calendar = Calendar.getInstance()
                            val year = calendar.get(Calendar.YEAR)
                            val month = calendar.get(Calendar.MONTH)
                            val day = calendar.get(Calendar.DAY_OF_MONTH)
                            val hour = calendar.get(Calendar.HOUR_OF_DAY)
                            val minute = calendar.get(Calendar.MINUTE)

                            val datePickerDialog = android.app.DatePickerDialog(
                                context,
                                { _, selectedYear, selectedMonth, selectedDay ->
                                    val selectedDate =
                                        "$selectedDay/${selectedMonth + 1}/$selectedYear"

                                    val timePickerDialog = TimePickerDialog(
                                        context,
                                        { _, selectedHour, selectedMinute ->
                                            val formattedDateTime = "$selectedDate ${
                                                String.format(
                                                    "%02d:%02d",
                                                    selectedHour,
                                                    selectedMinute
                                                )
                                            }"
                                            selectedValue = formattedDateTime
                                            apiviewmodel.updatePatientAnalysisResponce(
                                                patient.uhid,
                                                question.id!!,
                                                formattedDateTime,
                                                question.question,
                                                false
                                            )
                                        },
                                        hour, minute, true
                                    )
                                    timePickerDialog.show()
                                },
                                year, month, day
                            )

                            OutlinedButton(onClick = { datePickerDialog.show() }, enabled = isDoctor) {
                                Text(text = if (selectedValue.isEmpty()) "Select Date & Time" else selectedValue)
                            }
                        }
                    }
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
fun AnalysisNavigationButtons(
 
    onNextOrSubmit: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
     
        Button(onClick = onNextOrSubmit) {
            Text( "Submit")
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

private fun isOptionCheckedAnalysis(patient: Patient, id: String?, option: String): Boolean {
    // Check if questions are null
    if (patient.Analysis.isNullOrEmpty()) {
        return false
    }

    // Find the question with the given ID
    val question = patient.Analysis!!.find { it.questionUID == id }
    if (question != null) {
        // Check if the option exists in the answers list
        return question.answers.contains(option)
    }

    // Return false if the question does not exist
    return false
}
