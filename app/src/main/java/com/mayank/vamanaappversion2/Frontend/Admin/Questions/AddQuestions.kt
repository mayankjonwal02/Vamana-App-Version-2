package com.mayank.vamanaapp.Frontend.Admin.Questions

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
import androidx.compose.foundation.layout.fillMaxHeight
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
import com.mayank.vamanaappversion2.Backend.API_ViewModel
import com.mayank.vamanaappversion2.Constants
import com.mayank.vamanaappversion2.Modals.Question
import com.mayank.vamanaappversion2.Modals.QuestionDetail
import com.mayank.vamanaappversion2.Modals.User


@Composable
fun QuestionsScreen(apiviewmodel: API_ViewModel) {

    val categories by apiviewmodel.all_questions.collectAsState()
    var context = LocalContext.current
    LaunchedEffect(Unit) {
        apiviewmodel.GetAllQuestions()
    }


    LaunchedEffect(categories) {
        Log.i("NetworkCall","cat" + categories.toString())
    }

    var selectedCategoryIndex by remember { mutableStateOf(0) }
    val selectedCategory = categories.getOrNull(selectedCategoryIndex)

    var isAddCategoryDialogOpen by remember { mutableStateOf(false) }
    var isAddQuestionDialogOpen by remember { mutableStateOf(false) }
    var isDeleteCategoryDialogOpen by remember { mutableStateOf(false) }
    var isEditCategoryDialogOpen by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            "Questions Management",
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(20.dp))

        Text("Select Category", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(18.dp))

        CategoryDropdown(
            categories = categories.map { it.category },
            selectedCategoryIndex = selectedCategoryIndex,
            onCategorySelected = { selectedCategoryIndex = it }
        )

        Spacer(modifier = Modifier.height(18.dp))

        OutlinedButton(
            onClick = { isAddCategoryDialogOpen = true },
            colors = ButtonDefaults.outlinedButtonColors(containerColor = Constants.PrimaryColor, contentColor = Constants.SecondaryColor),
            border = BorderStroke(3.dp, Constants.SecondaryColor)
        ) {
            Text("Add Category")
        }

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedButton(
            onClick = { isDeleteCategoryDialogOpen = true },
            colors = ButtonDefaults.outlinedButtonColors(containerColor = Constants.PrimaryColor, contentColor = Constants.SecondaryColor),
            border = BorderStroke(3.dp, Constants.SecondaryColor)
        ) {
            Text("Delete Category")
        }

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedButton(
            onClick = { isEditCategoryDialogOpen = true },
            colors = ButtonDefaults.outlinedButtonColors(containerColor = Constants.PrimaryColor, contentColor = Constants.SecondaryColor),
            border = BorderStroke(3.dp, Constants.SecondaryColor)
        ) {
            Text("Edit Category")
        }

        Spacer(modifier = Modifier.height(30.dp))

        selectedCategory?.let { category ->
            Text("Questions for ${category.category}", style = MaterialTheme.typography.titleMedium)

            Spacer(modifier = Modifier.height(16.dp))

            category.questions.forEachIndexed { index, questionData ->
                QuestionItem(
                    questionData = questionData,
                    onUpdateQuestion = { updatedQuestion ->
                        categories[selectedCategoryIndex].questions[index].question = updatedQuestion
                    },
                    onUpdateOptions = { updatedOptions ->
                        categories[selectedCategoryIndex].questions[index].options = updatedOptions
                    },
                    apiviewmodel,
                    categoryid = categories[selectedCategoryIndex].id
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = { isAddQuestionDialogOpen = true },
                colors = ButtonDefaults.outlinedButtonColors(containerColor = Constants.PrimaryColor, contentColor = Constants.SecondaryColor),
                border = BorderStroke(3.dp, Constants.SecondaryColor)
            ) {
                Text("Add Question")
            }
        }

        // Add Category Dialog
        if (isAddCategoryDialogOpen) {
            AddCategoryDialog(
                onDismiss = { isAddCategoryDialogOpen = false },
                onSave = { categoryName ->
                    if (categoryName.isNotBlank()) {
//                        categories.add(CategoryData(categoryName, mutableListOf()))
                        apiviewmodel.CreateCategory(categoryName){ responce ->
                            Toast.makeText(context ,responce,Toast.LENGTH_SHORT ).show()
                            apiviewmodel.GetAllQuestions()
                        }
                        isAddCategoryDialogOpen = false
                    }
                }
            )
        }

        // Delete Category Dialog
        if (isDeleteCategoryDialogOpen) {
            DeleteCategoryDialog(
                categories = categories,
                onDismiss = { isDeleteCategoryDialogOpen = false },
                onConfirmDelete = { categoryIndex ->
//                    categories.removeAt(categoryIndex)
                    apiviewmodel.DeleteCategory(categories[categoryIndex].id ){ responce ->
                        Toast.makeText(context ,responce,Toast.LENGTH_SHORT ).show()
                        isDeleteCategoryDialogOpen = false
                        selectedCategoryIndex = 0
                        apiviewmodel.GetAllQuestions()
                    }

                }
            )
        }

        // Edit Category Dialog
        if (isEditCategoryDialogOpen) {
            EditCategoryDialog(
                categories = categories,
                onDismiss = { isEditCategoryDialogOpen = false },
                onConfirmEdit = { categoryIndex, newCategoryName ->
                    if (newCategoryName.isNotBlank()) {
                        apiviewmodel.UpdateCategory(categories[categoryIndex].id , newCategoryName){ responce ->
                            Toast.makeText(context ,responce,Toast.LENGTH_SHORT ).show()
                            apiviewmodel.GetAllQuestions()
                        }

                        isEditCategoryDialogOpen = false
                    }
                }
            )
        }

        // Add Question Dialog
        if (isAddQuestionDialogOpen) {
            AddQuestionDialog(
                onDismiss = { isAddQuestionDialogOpen = false },
                onSave = { questionText,inputtype, options ->
                    if (questionText.isNotBlank() ) {

                        apiviewmodel.AddQuestion(categories[selectedCategoryIndex].id , QuestionDetail( question = questionText, inputtype = inputtype , options = options))
                        {
                            apiviewmodel.GetAllQuestions()
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
fun DeleteCategoryDialog(
    categories: List<Question>,
    onDismiss: () -> Unit,
    onConfirmDelete: (Int) -> Unit
) {
    var selectedCategoryIndex by remember { mutableStateOf(0) }
    var isDeleteDialogOpen by remember {
        mutableStateOf(false)
    }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Delete Category") },
        text = {
            Column {
                Text("Select a category to delete:")
                Spacer(modifier = Modifier.height(8.dp))
                CategoryDropdown(
                    categories = categories.map { it.category },
                    selectedCategoryIndex = selectedCategoryIndex,
                    onCategorySelected = { selectedCategoryIndex = it }
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { isDeleteDialogOpen = true }) {
                Text("Delete")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )

    if(isDeleteDialogOpen)
    {
        ConfirmCategoryDelete(category = categories[selectedCategoryIndex], onDismiss = { isDeleteDialogOpen = false }) {
            onConfirmDelete(selectedCategoryIndex)
            isDeleteDialogOpen = false
        }
    }
}

@Composable
fun EditCategoryDialog(
    categories: List<Question>,
    onDismiss: () -> Unit,
    onConfirmEdit: (Int, String) -> Unit
) {
    var selectedCategoryIndex by remember { mutableStateOf(0) }
    var newCategoryName by remember { mutableStateOf(categories[selectedCategoryIndex].category) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Category") },
        text = {
            Column {
                Text("Select a category to edit:")
                Spacer(modifier = Modifier.height(8.dp))
                CategoryDropdown(
                    categories = categories.map { it.category },
                    selectedCategoryIndex = selectedCategoryIndex,
                    onCategorySelected = {
                        selectedCategoryIndex = it
                        newCategoryName = categories[it].category
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = newCategoryName,
                    onValueChange = { newCategoryName = it },
                    label = { Text("New Category Name") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirmEdit(selectedCategoryIndex, newCategoryName) }) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )


}



@Composable
fun QuestionItem(
    questionData: QuestionDetail,
    onUpdateQuestion: (String) -> Unit,
    onUpdateOptions: (MutableList<String>) -> Unit,
    apiviewmodel: API_ViewModel,
    categoryid: String
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
                apiviewmodel.UpdateQuestion(categoryid , question)
                {
                    apiviewmodel.GetAllQuestions()
                }
                isEditDialogOpen = false
            }
        )
    }

    if(isDeleteDialogOpen)
    {
        ConfirmQuestionDelete(questionData = questionData, onDismiss = { isDeleteDialogOpen = false }) {
            apiviewmodel.DeleteQuestion(categoryid,questionData.id!!)
            {
                apiviewmodel.GetAllQuestions()
                isDeleteDialogOpen = false
            }
        }
    }
}


@Composable
fun ConfirmCategoryDelete(category: Question,
                          onDismiss: () -> Unit,
                          onConfirm: () -> Unit) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        confirmButton = { TextButton(onClick = { onConfirm() })
        {
            Text(text = "Confirm")
        } },
        dismissButton = { TextButton(onClick = { onDismiss() })
        {
            Text(text = "Cancel")
        } } ,
        title = { Text("Delete Category") },
        text =  { Text("Are you sure to delete Category : ${category.category}") })


}


@Composable
fun ConfirmQuestionDelete(questionData: QuestionDetail,
                  onDismiss: () -> Unit,
                  onConfirm: () -> Unit) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        confirmButton = { TextButton(onClick = { onConfirm() })
        {
            Text(text = "Confirm")
        } },
        dismissButton = { TextButton(onClick = { onDismiss() })
        {
            Text(text = "Cancel")
        } } ,
        title = { Text("Delete Question") },
        text =  { Text("Are you sure to delete Question : ${questionData.question}") })


}

@Composable
fun EditQuestionDialog(
    questionText: String,
    options: List<String>,
    input_type : String,
    onDismiss: () -> Unit,
    onSave: (String,String, MutableList<String>) -> Unit
) {
    var newQuestionText by remember { mutableStateOf(questionText) }
    val newOptions = remember { mutableStateListOf(*options.toTypedArray()) } // Use mutableStateListOf
    var newOptionText by remember { mutableStateOf("") } // Tracks the new option text
    val inputTypesList = listOf(
        "text", "textarea", "number", "radio", "checkbox", "dropdown",
        "date", "datetime",  "time"
    )
    var inputtype by remember {
        mutableStateOf(input_type.toString())
    }
    var expanded by remember { mutableStateOf(false) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Question") },
        containerColor = Constants.PrimaryColor,
        text = {
            Column(modifier = Modifier
                .height(400.dp)
                .verticalScroll(rememberScrollState())) {
                OutlinedTextField(
                    value = newQuestionText,
                    onValueChange = { newQuestionText = it },
                    label = { Text("Question Text") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))
                Box(modifier = Modifier.fillMaxWidth()) {
                    Button(
                        onClick = { expanded = true },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Constants.TertiaryColor)
                    ) {
                        Text(text = if(inputtype == "") "Select Input-Type" else "Input Type : "+inputtype)
                    }

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.background(Color.White).height(200.dp)
                    ) {
                        inputTypesList.forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type) },
                                onClick = {
                                    inputtype = type
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                if (listOf("radio", "checkbox", "dropdown").contains(inputtype))
                {
                    Text("Options:", style = MaterialTheme.typography.titleMedium)

                    newOptions.forEachIndexed { index, option ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedTextField(
                                value = option,
                                onValueChange = { newOptions[index] = it }, // Update value
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(3.dp)
                            )
                            IconButton(onClick = {
                                newOptions.removeAt(index) // Trigger recomposition
                            }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete Option")
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }


                    // Add new option input
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            value = newOptionText,
                            onValueChange = { newOptionText = it },
                            label = { Text("New Option") },
                            modifier = Modifier
                                .weight(1f)
                                .padding(3.dp)
                        )
                        Button(onClick = {
                            if (newOptionText.isNotBlank()) {
                                newOptions.add(newOptionText) // Trigger recomposition
                                newOptionText = "" // Clear input after adding
                            }
                        },
                            colors = ButtonDefaults.buttonColors(containerColor = Constants.TertiaryColor)) {
                            Text("Add Option")
                        }
                    }
                }


            }
        },
        confirmButton = {
            Button(onClick = { onSave(newQuestionText,inputtype, newOptions.toMutableList()) },
                colors = ButtonDefaults.buttonColors(containerColor = Constants.TertiaryColor)) { // Pass a copy of the list
                Text("Save")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = Constants.TertiaryColor)) {
                Text("Cancel")
            }
        }
    )
}




@Composable
fun AddCategoryDialog(onDismiss: () -> Unit, onSave: (String) -> Unit) {
    var categoryName by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Category") },
        containerColor = Constants.PrimaryColor,
        text = {
            OutlinedTextField(
                value = categoryName,
                onValueChange = { categoryName = it },
                label = { Text("Category Name") },
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Button(onClick = { onSave(categoryName) },
                colors = ButtonDefaults.buttonColors(containerColor = Constants.TertiaryColor)) {
                Text("Save")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = Constants.TertiaryColor)) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun AddQuestionDialog(
    onDismiss: () -> Unit,
    onSave: (String , String, List<String>) -> Unit
) {
    var questionText by remember { mutableStateOf("") }
    val options = remember { mutableStateListOf<String>() }
    var newOptionText by remember { mutableStateOf("") } // For the new option input
    val inputTypesList = listOf(
        "text", "textarea", "number", "radio", "checkbox", "dropdown",
        "date", "datetime",  "time"
    )
    var inputtype by remember {
        mutableStateOf("")
    }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Question") },
        containerColor = Constants.PrimaryColor,
        text = {
            Column(modifier = Modifier
                .height(400.dp)
                .verticalScroll(rememberScrollState())) {
                // Input field for the question text
                OutlinedTextField(
                    value = questionText,
                    onValueChange = { questionText = it },
                    label = { Text("Question Text") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                var expanded by remember { mutableStateOf(false) }

                Box(modifier = Modifier.fillMaxWidth()) {
                    Button(
                        onClick = { expanded = true },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Constants.TertiaryColor)
                    ) {
                        Text(text = if(inputtype == "") "Select Input-Type" else "Input Type : "+inputtype)
                    }

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.background(Color.White).height(200.dp)
                    ) {
                        inputTypesList.forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type) },
                                onClick = {
                                    inputtype = type
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                if (listOf("radio", "checkbox", "dropdown").contains(inputtype))
                {
                    // Display the list of options
                    Text("Options:", style = MaterialTheme.typography.titleMedium)

                    options.forEachIndexed { index, option ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedTextField(
                                value = option,
                                onValueChange = { options[index] = it }, // Update the option
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(onClick = { options.removeAt(index) }) { // Delete the option
                                Icon(Icons.Default.Delete, contentDescription = "Delete Option")
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    // Input for adding a new option
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            value = newOptionText,
                            onValueChange = { newOptionText = it },
                            label = { Text("New Option") },
                            modifier = Modifier
                                .weight(1f)
                                .padding(4.dp)
                        )
                        Button(onClick = {
                            if (newOptionText.isNotBlank()) {
                                options.add(newOptionText)
                                newOptionText = "" // Clear the input field
                            }
                        },
                            colors = ButtonDefaults.buttonColors(containerColor = Constants.TertiaryColor)) {
                            Text("Add")
                        }
                    }
                }



            }
        },
        confirmButton = {
            Button(onClick = {
                if (questionText.isNotBlank() && inputtype.isNotBlank()) {
                    onSave(questionText,inputtype, options.toList()) // Pass the question and options
                }
            },
                colors = ButtonDefaults.buttonColors(containerColor = Constants.TertiaryColor)) {
                Text("Save")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = Constants.TertiaryColor)) {
                Text("Cancel")
            }
        }
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryDropdown(
    categories: List<String>,
    selectedCategoryIndex: Int,
    onCategorySelected: (Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        OutlinedTextField(
            value = if (categories.isNotEmpty()) categories[selectedCategoryIndex] else "No Categories",
            onValueChange = {},
            readOnly = true,
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    Modifier.clickable { expanded = true }
                )
            },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Category") },
            colors = TextFieldDefaults.outlinedTextFieldColors(containerColor = Constants.PrimaryColor),

            )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.height(300.dp)
        ) {
            categories.forEachIndexed { index, category ->
                DropdownMenuItem(
                    onClick = {
                        expanded = false
                        onCategorySelected(index)
                    },
                    text = { Text(category) }
                )
            }
        }
    }
}


