package com.mayank.vamanaappversion2.Frontend.Admin.Questions

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mayank.vamanaapp.Frontend.Admin.Questions.AddQuestionDialog
import com.mayank.vamanaapp.Frontend.Admin.Questions.ConfirmQuestionDelete
import com.mayank.vamanaapp.Frontend.Admin.Questions.EditQuestionDialog
import com.mayank.vamanaapp.Frontend.Admin.Questions.QuestionItem
import com.mayank.vamanaappversion2.Backend.API_ViewModel
import com.mayank.vamanaappversion2.Constants
import com.mayank.vamanaappversion2.Modals.Question
import com.mayank.vamanaappversion2.Modals.QuestionDetail

@Composable
fun AnalysisQuestions(apiviewmodel: API_ViewModel) {

    val questions by apiviewmodel.all_analysis_questions.collectAsState()
    var isAddQuestionDialogOpen by remember {
        mutableStateOf(false)
    }
    var context = LocalContext.current
    LaunchedEffect(Unit) {
        apiviewmodel.GetAnalysisQuestions()
    }


    LaunchedEffect(questions) {
        Log.i("NetworkCall","cat" + questions.toString())
    }



    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            "Analysis Management",
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(20.dp))




        Spacer(modifier = Modifier.height(30.dp))



        questions.forEachIndexed { index, questionData ->
            AnalysisQuestionItem(
                questionData = questionData,
                onUpdateQuestion = { updatedQuestion ->
                    questions[index].question = updatedQuestion
                },
                onUpdateOptions = { updatedOptions ->
                    questions[index].options = updatedOptions
                },
                apiviewmodel
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
            onClick = { isAddQuestionDialogOpen = true },
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = Constants.PrimaryColor,
                contentColor = Constants.SecondaryColor
            ),
            border = BorderStroke(3.dp, Constants.SecondaryColor)
        ) {
            Text("Add Question")
        }


        // Add Question Dialog
        if (isAddQuestionDialogOpen) {
            AddQuestionDialog(
                onDismiss = { isAddQuestionDialogOpen = false },
                onSave = { questionText, inputtype, options ->
                    if (questionText.isNotBlank()) {

                        apiviewmodel.CreateAnalysisQuestions(

                            QuestionDetail(
                                question = questionText,
                                inputtype = inputtype,
                                options = options
                            )
                        )
                        {
                            apiviewmodel.GetAnalysisQuestions()
                        }
//                        categories[selectedCategoryIndex].questions.add(
//                            QuestionDetail(questionText, options.toMutableList())
//                        )
                        isAddQuestionDialogOpen = false
                    }
                }
            )
        }
    }
}


@Composable
fun AnalysisQuestionItem(
    questionData: QuestionDetail,
    onUpdateQuestion: (String) -> Unit,
    onUpdateOptions: (MutableList<String>) -> Unit,
    apiviewmodel: API_ViewModel,

) {
    var isEditDialogOpen by remember { mutableStateOf(false) }
    var isDeleteDialogOpen by remember { mutableStateOf(false) }


    Card(
        modifier = Modifier.fillMaxWidth(),
//        elevation = 4.dp,
//        backgroundColor = MaterialTheme.colorScheme.surfaceVariant
        colors = CardDefaults.cardColors(containerColor = Constants.PrimaryColor),
        border = BorderStroke(3.dp,Constants.TertiaryColor)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = questionData.question,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            questionData.options.forEach { option ->
                Text("• $option", style = MaterialTheme.typography.bodyMedium)
            }

            Spacer(modifier = Modifier.height(20.dp))
            Row( modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(), verticalAlignment = Alignment.CenterVertically , horizontalArrangement = Arrangement.SpaceBetween) {
                Button(onClick = { isEditDialogOpen = true }, colors = ButtonDefaults.buttonColors(containerColor = Constants.TertiaryColor)) {
                    Text("Edit")
                }
                Icon(imageVector = Icons.Filled.Delete, contentDescription = "", tint = Constants.TertiaryColor, modifier = Modifier.clickable { isDeleteDialogOpen = true })
            }

        }
    }

    if (isEditDialogOpen) {
        EditQuestionDialog(
            questionText = questionData.question,
            options = questionData.options,
            input_type = questionData.inputtype,
            onDismiss = { isEditDialogOpen = false },
            onSave = { newQuestion,newinputtype, newOptions ->
                var question = QuestionDetail(id =questionData.id!!  , question = newQuestion , inputtype = newinputtype, options = newOptions)
                apiviewmodel.UpdateAnalysisQuestions(question.id!!,question)
                {
//                    apiviewmodel.GetAnalysisQuestions()
                }
                isEditDialogOpen = false
            }
        )
    }

    if(isDeleteDialogOpen)
    {
        ConfirmQuestionDelete(questionData = questionData, onDismiss = { isDeleteDialogOpen = false }) {
            apiviewmodel.DeleteAnalysisQuestions(questionData.id!!)
            {

                isDeleteDialogOpen = false
            }
        }
    }
}


