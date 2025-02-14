package com.mayank.vamanaapp.Frontend.Doctor

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
fun AnswerQuestionScreen(
    patient: Patient,
    apiviewmodel: API_ViewModel,
    categories: List<Question>,
    onSubmitResponse: (onSubmitted: () -> Unit) -> Unit,
    onSubmitResponseFinal: () -> Unit
) {
    var drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var selectedCategoryIndex by remember { mutableStateOf(0) }
    val totalCategories = categories.size
    val selectedCategory = categories.getOrElse(selectedCategoryIndex) { categories.last() }
    val progress = selectedCategoryIndex.toFloat() / totalCategories.coerceAtLeast(1)

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
            SideNavBar(
                patient = patient,
                categories = categories,
                selectedCategoryIndex = selectedCategoryIndex,
                onCategorySelected = {
                    selectedCategoryIndex = it
                    scope.launch {
                        drawerState.apply {
                            close()
                        }
                    }
                }
            )
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Answer : " + selectedCategory.category) },
                    navigationIcon = {
                        IconButton(onClick = {scope.launch {
                            drawerState.apply {
                                if (isClosed) open() else close()
                            }
                        } }) {
                            Icon(
                                imageVector = Icons.Filled.Menu,
                                contentDescription = "Hamburger Menu"
                            )
                        }
                    }
                )
            }
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
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
                            onOptionSelected = { questionId, option -> },
                            getCheckedState = { questionId, option -> false },
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
                                    onSubmitResponse { selectedCategoryIndex++ }
                                } else {
                                    onSubmitResponseFinal()
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SideNavBar(
    patient:Patient,
    categories: List<Question>,
    selectedCategoryIndex: Int,
    onCategorySelected: (Int) -> Unit
) {
    val configuration = LocalConfiguration.current

    // Get screen width and height in pixels
    val screenWidth = configuration.screenWidthDp
    val screenHeight = configuration.screenHeightDp
        

    Column(
        modifier = Modifier
            .background(Constants.PrimaryColor)
            .width((screenWidth * 0.7).toFloat().dp)

            .fillMaxHeight()
//            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceAround, modifier = Modifier
            .background(Constants.PrimaryColor)
            .padding(16.dp)
        ) {
            Icon(imageVector = Icons.Filled.Person, contentDescription = ""  , tint = Constants.SecondaryColor, modifier = Modifier
                .border(
                    border = BorderStroke(2.dp, color = Constants.SecondaryColor),
                    shape = RoundedCornerShape(20.dp)
                )
                .size(50.dp))
            Text(
                text = "UHID: " + patient.uhid ,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
            )
        }


        categories.forEachIndexed { index, category ->
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start) {
                Icon(imageVector = Icons.Filled.ArrowForwardIos , contentDescription = "", tint = if (index == selectedCategoryIndex) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface , modifier = Modifier.size(16.dp))
                Text(
                    text = category.category,
                    style = MaterialTheme.typography.titleMedium,
                    color = if (index == selectedCategoryIndex) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .padding(start = 3.dp)
                        .clickable { onCategorySelected(index) }
                )
            }

        }
        Spacer(modifier = Modifier.height(30.dp))
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
    val context = LocalContext.current
    val role = getSharedPreferences(context).getString("role", "admin")
    val isDoctor = role == "staff"
    var selectedFilter by remember { mutableStateOf("") }


    val filterOptions = listOf("Day 1","Day 2","Day 3","Day 4","Day 5","Day 6","Day 7","Day 8", "Entry-1", "Entry-2", "Entry-3", "Entry-4", "Entry-5", "Entry-6",
        "Entry-7", "Entry-8", "Entry-9", "Entry-10", "Entry-11", "Entry-12",
        "Entry-13", "Entry-14", "Entry-15", "Entry-16", "Entry-17", "Entry-18",
        "Entry-19", "Entry-20", "Entry-21", "Entry-22", "Entry-23", "Entry-24") // Add relevant options
    val filteredQuestions = if (selectedFilter.isEmpty()) {
        questions
    } else {
        questions.filter { it.question.contains(selectedFilter, ignoreCase = true) }
    }



    Column(modifier = Modifier.fillMaxWidth()) {

        var expanded by remember { mutableStateOf(false) }
        Box {
            OutlinedButton(onClick = { expanded = true }) {
                Text(text = if (selectedFilter.isEmpty()) "Filter Questions" else "Filter: $selectedFilter")
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }, modifier = Modifier.height(400.dp)) {
                DropdownMenuItem(text = { Text("All") }, onClick = {
                    selectedFilter = ""
                    expanded = false
                })
                filterOptions.forEach { option ->
                    DropdownMenuItem(text = { Text(option) }, onClick = {
                        selectedFilter = option
                        expanded = false
                    })
                }
            }
        }

        filteredQuestions.forEach { question ->
            val existingQuestion = patient.questions!!.find { it.questionUID == question.id }

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
                            apiviewmodel.updatePatientResponce(patient.uhid, question.id!!, it, question.question,false)
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
                                apiviewmodel.updatePatientResponce(patient.uhid, question.id!!, it, question.question,false)
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
                                selected = isOptionChecked(patient, question.id, option),
                                onClick = {
                                    apiviewmodel.updatePatientResponce(patient.uhid, question.id!!, option, question.question,false)
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
                                checked = isOptionChecked(patient, question.id, option),
                                onCheckedChange = {
                                    apiviewmodel.updatePatientResponce(patient.uhid, question.id!!, option, question.question,true)
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
                                        apiviewmodel.updatePatientResponce(patient.uhid, question.id!!, option, question.question,false)
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
                                    apiviewmodel.updatePatientResponce(
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
                                    apiviewmodel.updatePatientResponce(patient.uhid, question.id!!, formattedTime, question.question,false)
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
                                            apiviewmodel.updatePatientResponce(
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
