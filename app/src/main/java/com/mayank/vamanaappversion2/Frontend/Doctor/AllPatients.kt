import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.mayank.vamanaapp.Frontend.Doctor.AnswerQuestionScreen
import com.mayank.vamanaappversion2.Backend.API_ViewModel
import com.mayank.vamanaappversion2.Backend.getSharedPreferences
import com.mayank.vamanaappversion2.Constants
import com.mayank.vamanaappversion2.Frontend.Doctor.AnalysisQuestionScreen
import com.mayank.vamanaappversion2.Frontend.Doctor.SnehapanaCalculatorScreen
import com.mayank.vamanaappversion2.Frontend.Doctor.UpdatePatientFormScreen
import com.mayank.vamanaappversion2.Modals.Patient
import com.mayank.vamanaappversion2.Modals.Question
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Data class to represent patient details

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AllPatientsScreen(apiviewmodel: API_ViewModel) {
    // State to hold the search query

    val questions_with_Category by apiviewmodel.all_questions.collectAsState()
    val analysisQuestions by apiviewmodel.all_analysis_questions.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    // Sample patient list
    var context = LocalContext.current
    val patientList by apiviewmodel.all_patients.collectAsState()
    LaunchedEffect(Unit) {
        apiviewmodel.GetAllQuestions()
        apiviewmodel.GetAllPatients()
        apiviewmodel.GetAnalysisQuestions()
    }

    LaunchedEffect(patientList) {
        Log.i("Test",patientList.toString())
    }



    // Filtered list based on search query
    val filteredList = patientList.filter {
        it.uhid.contains(searchQuery, ignoreCase = true)
    }

    Box(
            modifier = Modifier.background(Color.White),
        contentAlignment = Alignment.BottomEnd
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "All Patients",
                fontWeight = FontWeight.ExtraBold,
                fontFamily = FontFamily.Default,
                fontStyle = FontStyle.Normal,
                fontSize = 30.sp,
                modifier = Modifier.padding(10.dp)
            )
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth(),
                placeholder = { Text("Search by UHID/IPD No.") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Search
                ),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    unfocusedPlaceholderColor = Color.Gray,
                    unfocusedBorderColor = Color.Gray,
                    unfocusedTextColor = Color.Gray,
                    focusedBorderColor = Constants.SecondaryColor,
                    focusedLabelColor = Constants.SecondaryColor,
                    focusedTextColor = Constants.SecondaryColor
                ),
                leadingIcon = {
                    Icon(imageVector = Icons.Filled.Search, contentDescription = "", tint = Color.DarkGray)
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

//             LazyColumn with filtered list
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                items(filteredList) { patient ->
                    ExpandablePatientCard(patient,apiviewmodel,questions_with_Category)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
        Button(onClick = {
            exportPatientsToCSV(context,patientList,questions_with_Category,analysisQuestions)
        }) {
            Text(text = "Export Data")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(query: String, onQueryChange: (String) -> Unit) {
    OutlinedTextField(
        value = query,
        onValueChange = { onQueryChange(it) },
        modifier = Modifier
            .fillMaxWidth(),
        placeholder = { Text("Search by UHID/IPD No.") },
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Search
        ),
        colors = TextFieldDefaults.outlinedTextFieldColors(
            unfocusedPlaceholderColor = Color.Gray,
            unfocusedBorderColor = Color.Gray,
            unfocusedTextColor = Color.Gray,
            focusedBorderColor = Constants.SecondaryColor,
            focusedLabelColor = Constants.SecondaryColor,
            focusedTextColor = Constants.SecondaryColor
        )
    )
}

@Composable
fun ExpandablePatientCard(patient: Patient, apiviewmodel: API_ViewModel, questions_with_categories: List<Question>) {
    var isExpanded by remember { mutableStateOf(false) }
    var showBottomSheet = remember { mutableStateOf(false) }
    var showBottomSheet1 = remember { mutableStateOf(false) }
    var showBottomSheet2 = remember { mutableStateOf(false) }
    var showUpdateBottomSheet = remember { mutableStateOf(false) }
    var context = LocalContext.current
    val role = getSharedPreferences(context).getString("role", "admin")
    val isDoctor = role == "staff"

    var isDeleteDialogOpen by remember {
        mutableStateOf(false)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded },
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "UHID/IPD No.: ${patient.uhid}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = patient.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            if (isExpanded) {
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text("Age: ${patient.age}", style = MaterialTheme.typography.bodyMedium)
                    Text("Occupation: ${patient.occupation}", style = MaterialTheme.typography.bodyMedium)
                    Text("Past Illness: ${patient.pastIllness}", style = MaterialTheme.typography.bodyMedium)
                    Text("Address: ${patient.address}", style = MaterialTheme.typography.bodyMedium)
                    Text("Medicine History: ${patient.medicineHistory}", style = MaterialTheme.typography.bodyMedium)
                    Text("Date of Admission: ${convertIsoToCustomDate(patient.dateOfAdmission)}", style = MaterialTheme.typography.bodyMedium)
                    Text("Date of Vamana: ${convertIsoToCustomDate(patient.dateOfVamana!!)}", style = MaterialTheme.typography.bodyMedium)
                    Text("Prakriti: ${patient.prakriti}", style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(20.dp))

                    Row(modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceAround)
                    {
                        Button(onClick = { showBottomSheet.value = true }) {
                            Text(text = "Show Questions")
                        }

                        Button(onClick = { showBottomSheet1.value = true }) {
                            Text(text = "Analyse Patient")
                        }


                    }

                    Button(onClick = { showBottomSheet2.value = true }) {
                        Text(text = "Snehapana Calculator")
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                    Row(modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween)
                    {
                        Icon(imageVector = Icons.Filled.Edit, contentDescription = "" , tint = Constants.BlueButtonColor , modifier = Modifier.clickable { showUpdateBottomSheet.value = true })
                        Icon(imageVector = Icons.Filled.Delete, contentDescription = "" , tint = Constants.BlueButtonColor, modifier = Modifier.clickable { isDeleteDialogOpen = true })


                    }

                    TextButton(onClick = { sharePdf(context,1) }, colors = ButtonDefaults.textButtonColors(
                        contentColor = Constants.BlueButtonColor
                    )) {
                        Icon(imageVector = Icons.Filled.Share, contentDescription = "", modifier = Modifier.padding(6.dp))
                        Text(text = "  Share Avara Samsarjana Karma PDF")
                    }

                    TextButton(onClick = { sharePdf(context,2) }, colors = ButtonDefaults.textButtonColors(
                        contentColor = Constants.BlueButtonColor
                    )) {
                        Icon(imageVector = Icons.Filled.Share, contentDescription = "", modifier = Modifier.padding(6.dp))
                        Text(text = "  Share Madhyam Samsarjana Karma PDF")
                    }

                    TextButton(onClick = { sharePdf(context,3) }, colors = ButtonDefaults.textButtonColors(
                        contentColor = Constants.BlueButtonColor
                    )) {
                        Icon(imageVector = Icons.Filled.Share, contentDescription = "", modifier = Modifier.padding(6.dp))
                        Text(text = "  Share Pravar Shuddhi Samsarjana Karma PDF")

                    }
                }
            }
        }
    }


    if(showBottomSheet.value)
    {
        QuestionBottomSheet(patient = patient, showBottomSheet,apiviewmodel,questions_with_categories,isDoctor)
    }

    if(showBottomSheet1.value)
    {

        AnalysisQuestionBottomSheet(patient = patient, showBottomSheet1,apiviewmodel,isDoctor)
    }


    if(showBottomSheet2.value)
    {
            SnehaPanaBottomSheet(patient = patient, showBottomSheet2,apiviewmodel,isDoctor)
//        AnalysisQuestionBottomSheet(patient = patient, showBottomSheet1,apiviewmodel)
    }
    if (isDeleteDialogOpen)
    {
        ConfirmPatientDelete(uhid = patient.uhid, onDismiss = { isDeleteDialogOpen = false }) {
           apiviewmodel.DeletePatient(patient.uhid)
           {
               isDeleteDialogOpen = false
               apiviewmodel.GetAllPatients()
           }
        }
    }

    if(showUpdateBottomSheet.value)
    {
        PatientDetailsBottomSheet(patient = patient, showBottomSheet = showUpdateBottomSheet, apiviewmodel = apiviewmodel)
    }


}



fun convertIsoToCustomDate(isoTimestamp: String): String {
    // Parse the ISO-8601 timestamp
    val isoFormat = isoTimestamp.replace("Z", "+00:00") // Ensure compatibility

    // Parse ISO timestamp to Date
    val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.getDefault())
    val date = inputFormat.parse(isoFormat)

    // Format Date to dd/MM/yyyy
    val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return outputFormat.format(date ?: Date())
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestionBottomSheet(
    patient: Patient,
    showBottomSheet: MutableState<Boolean>,
    apiviewmodel: API_ViewModel,
    questions_with_categories: List<Question>,
    isDoctor: Boolean
) {

    LaunchedEffect(Unit)
    {
        Log.i("Questions",patient.questions.toString())
    }
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
    )

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {



            ModalBottomSheet(
                modifier = Modifier.fillMaxHeight(),
                sheetState = sheetState,
                onDismissRequest = { showBottomSheet.value = false }
            ) {
                AnswerQuestionScreen(patient,apiviewmodel,questions_with_categories,
                    onSubmitResponse = { onSubmitted ->

                        if (isDoctor)
                        {
                            apiviewmodel.UpdatePatientQuestions(patient)
                            {
                                onSubmitted()
                            }
                        }
                        else
                        {
                            onSubmitted()
                        }

                    }
                ) {
                    if (isDoctor)
                    {
                        apiviewmodel.UpdatePatientQuestions(patient)
                        {
                            showBottomSheet.value = false
                            apiviewmodel.FetchUsers()
                        }

                    }
                    else
                    {
                        showBottomSheet.value = false
                    }

                }

            }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalysisQuestionBottomSheet(
    patient: Patient,
    showBottomSheet: MutableState<Boolean>,
    apiviewmodel: API_ViewModel,
    isDoctor: Boolean,

    ) {

    LaunchedEffect(Unit)
    {
        Log.i("Questions",patient.questions.toString())
    }
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
    )

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {



        ModalBottomSheet(
            modifier = Modifier.fillMaxHeight(),
            sheetState = sheetState,
            onDismissRequest = { showBottomSheet.value = false }
        ) {
            AnalysisQuestionScreen(patient,apiviewmodel,
                onSubmitResponse = { onSubmitted ->

                }
            ) {
                if(isDoctor)
                {
                    apiviewmodel.UpdatePatient(patient.uhid!!,patient)

                    {

                        apiviewmodel.FetchUsers()
                    }
                }
                showBottomSheet.value = false

            }

        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SnehaPanaBottomSheet(
    patient: Patient,
    showBottomSheet: MutableState<Boolean>,
    apiviewmodel: API_ViewModel,
    isDoctor: Boolean,

    ) {

    LaunchedEffect(Unit)
    {
        Log.i("Questions",patient.questions.toString())
    }
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
    )

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {



        ModalBottomSheet(
            modifier = Modifier.fillMaxHeight(),
            sheetState = sheetState,
            onDismissRequest = { showBottomSheet.value = false }
        ) {
            SnehapanaCalculatorScreen(patient,apiviewmodel)
            {
                if(isDoctor)
                {
                    apiviewmodel.UpdatePatient(patient.uhid,patient)
                    {
                        showBottomSheet.value = false
                    }
                }
                else
                {
                    showBottomSheet.value = false
                }


            }
//            AnalysisQuestionScreen(patient,apiviewmodel,
//                onSubmitResponse = { onSubmitted ->
//
//                }
//            ) {
//                apiviewmodel.UpdatePatient(patient.uhid!!,patient)
//
//                {
//                    showBottomSheet.value = false
//                    apiviewmodel.FetchUsers()
//                }
//            }

        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientDetailsBottomSheet(
    patient: Patient,
    showBottomSheet: MutableState<Boolean>,
    apiviewmodel: API_ViewModel
) {

    LaunchedEffect(Unit)
    {
        Log.i("Questions",patient.questions.toString())
    }
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
    )

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {



        ModalBottomSheet(
            modifier = Modifier.fillMaxHeight(),
            sheetState = sheetState,
            onDismissRequest = { showBottomSheet.value = false },
            containerColor = Color.Transparent,
//            scrimColor = Constants.BlueButtonColor
        ) {
            UpdatePatientFormScreen(apiviewmodel = apiviewmodel, patient = patient) {
                showBottomSheet.value = false
                apiviewmodel.GetAllPatients()
            }
        }
    }
}


@Composable
fun ConfirmPatientDelete(uhid:String,
                  onDismiss: () -> Unit,
                  onConfirm: () -> Unit)
{
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
        title = { Text("Delete User") },
        text =  { Text("Are you sure to delete User (UHID = ${uhid})") })


}