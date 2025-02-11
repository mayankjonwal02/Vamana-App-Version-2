package com.mayank.vamanaappversion2.Frontend.Doctor



import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import com.mayank.vamanaappversion2.Backend.API_ViewModel
import com.mayank.vamanaappversion2.Backend.getSharedPreferences
import com.mayank.vamanaappversion2.Modals.Patient


@Composable
fun SnehapanaCalculatorScreen(patient: Patient, apiViewModel: API_ViewModel, onSave: () -> Unit) {

    var context = LocalContext.current
    val role = getSharedPreferences(context).getString("role", "admin")
    val isDoctor = role == "staff"
    val days = (2..6).toList()
    val hours = listOf(6, 9, 12, 15, 18)

    // Store doses as MutableStateList to handle recomposition correctly
    val doses = remember { mutableStateListOf(*Array(days.size) { "" }) }
    val digestivehours = remember { mutableStateListOf(*Array(days.size) { "" }) }

    // Ensure data is loaded when patient data changes



    LaunchedEffect(Unit) {
        patient.SnehaPana?.let { snehaPanaList ->
            days.forEachIndexed { index, day ->
                val storedDose = snehaPanaList.find { it.day.toFloat() == day.toFloat() }?.dose?.toString() ?: ""
                val storedHours = snehaPanaList.find { it.day.toFloat() == day.toFloat() }?.digestiveHours?.toString() ?: ""
                doses[index] = storedDose
                digestivehours[index] = storedHours
            }
        }
        Log.i("NetworkCall",patient.SnehaPana.toString())
    }

    // Corrected total dose calculation

    val totalDoses = doses.mapIndexed { index, dose ->
        val doseValue = dose.toFloatOrNull() ?: 0f
        val digestiveHourValue = digestivehours[index].toFloatOrNull() ?: 1f
        val hourFactor = if (index < hours.size) hours[index] else 1
        (doseValue /digestiveHourValue) * hourFactor
    }


//    val totalDoses = doses.mapIndexed { index, dose ->
//        val doseValue = dose.toFloatOrNull() ?: 0f
//        val hourValue = digestivehours[index].toFloatOrNull() ?: 0f // Default to 1 if empty
//        doseValue * hourValue
//    }

    val scrollState = rememberScrollState()
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .verticalScroll(scrollState)
            .imePadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Snehapana Calculator",
            style = MaterialTheme.typography.headlineMedium.copy(fontSize = 24.sp, fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(12.dp))

        days.forEachIndexed { index, day ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Day $day",
                        style = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp, fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
//                    val doseValue = patient.SnehaPana?.find { it.day == day }?.dose?.toString() ?: ""
                    val doseValue by remember {
                        mutableStateOf(patient.SnehaPana?.find { it.day == day }?.dose?.toString() ?: "")
                    }
                    Log.i("NetworkCall","${day} ${doseValue}")
                    OutlinedTextField(
                        value = doses[index], // Use stored state instead of fetching every recomposition
                        readOnly = !isDoctor,
                        onValueChange = { newValue ->
                            if (newValue != ""){
                                doses[index] = newValue
                                val doseNumber = newValue.toFloatOrNull()?:0f
                                apiViewModel.updatePatientSnehapanaResponse(
                                    patient.uhid,
                                    day,
                                    doseNumber,
                                    digestivehours[index].toFloatOrNull()?:0f
                                )

                                Log.d("Testsneha",doseNumber.toString())
                            }
                            else
                            {
                                doses[index] = ""
                                val doseNumber = 0f
                                apiViewModel.updatePatientSnehapanaResponse(
                                    patient.uhid,
                                    day,
                                    doseNumber,
                                    digestivehours[index].toFloatOrNull()?:0f
                                )

                                Log.d("Testsneha",doseNumber.toString())
                            }

                            Log.d("Testsneha",patient.SnehaPana.toString())
                        },
                        label = { Text("Dose") },
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Number
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = digestivehours[index], // Use stored state instead of fetching every recomposition
                        readOnly = !isDoctor,
                        onValueChange = { newValue ->
                            if (newValue != ""){
                                digestivehours[index] = newValue
                                val hourNumber = newValue.toFloatOrNull()?:0f
                                apiViewModel.updatePatientSnehapanaResponse(
                                    patient.uhid,
                                    day,
                                    doses[index].toFloatOrNull()?:0f,
                                    hourNumber
                                )

                                Log.d("Testsneha",hourNumber.toString())
                            }
                            else
                            {
                                digestivehours[index] = ""
                                val hourNumber = 0f
                                apiViewModel.updatePatientSnehapanaResponse(
                                    patient.uhid,
                                    day,
                                    doses[index].toFloatOrNull()?:0f,
                                    hourNumber
                                )

                                Log.d("Testsneha",hourNumber.toString())
                            }

                            Log.d("Testsneha",patient.SnehaPana.toString())
                        },
                        label = { Text("Digestive Hours") },
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Number
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Total Dose: %.2f ml".format(totalDoses[index]),
                        style = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp),
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {
                keyboardController?.hide()
                focusManager.clearFocus()
                if (isDoctor)
                {
                    onSave()

                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text(text = "Done", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}



