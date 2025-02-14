package com.mayank.vamanaapp.Frontend.Doctor

import android.app.AlertDialog
import android.widget.Toast
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mayank.vamanaappversion2.Backend.API_ViewModel
import com.mayank.vamanaappversion2.Backend.getSharedPreferences
import com.mayank.vamanaappversion2.Constants
import com.mayank.vamanaappversion2.Modals.Patient
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

@Composable
fun AddPatientFormScreen(apiviewmodel: API_ViewModel) {
    // State for input fields
    var name by remember { mutableStateOf(TextFieldValue("")) }
    var uhidIpdNo by remember { mutableStateOf(TextFieldValue("")) }
    var age by remember {mutableStateOf(TextFieldValue("0")) }
    var occupation by remember { mutableStateOf(TextFieldValue("")) }
    var pastIllness by remember { mutableStateOf(TextFieldValue("")) }
    var address by remember { mutableStateOf(TextFieldValue("")) }
    var medicineHistory by remember { mutableStateOf(TextFieldValue("")) }
    var dateOfAdmission by remember { mutableStateOf(TextFieldValue("")) }
    var dateOfVamana by remember { mutableStateOf(TextFieldValue("")) }
    var prakriti by remember { mutableStateOf(TextFieldValue("")) }

    var showDatePicker_admission by remember { mutableStateOf(false) }
    var showDatePicker_vamana by remember { mutableStateOf(false) }


    var context = LocalContext.current
//    var context = LocalContext.current
    var instituteId = getSharedPreferences(context).getString("institute_id","")

    var patient = Patient(
        id = null ,
        name = name.text  ,
        uhid = uhidIpdNo.text,
        age = age.text.toInt(),
        occupation = occupation.text,
        pastIllness = pastIllness.text,
        address = address.text,
        medicineHistory = medicineHistory.text,
        dateOfAdmission = dateOfAdmission.text,
        dateOfVamana = dateOfVamana.text,
        prakriti = prakriti.text,
        instituteID = instituteId)


    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
            .background(Constants.backgroundGradient),
        contentAlignment = Alignment.TopCenter
    ) {

            // Page Title


            // Patient Form Card
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .padding(10.dp)
                    .wrapContentHeight(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.5f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.Start
                ) {

                    Text(
                        text = "Add Patient",
                        style = MaterialTheme.typography.headlineLarge,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .padding(bottom = 20.dp)
                            .fillMaxWidth(),
                        fontWeight = FontWeight.ExtraBold
                    )
                    // Input Fields
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Name") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Constants.SecondaryColor,
                            focusedLabelColor = Constants.TertiaryColor,
                            focusedBorderColor = Constants.TertiaryColor,
                            unfocusedTextColor = Color.Black,
                            unfocusedLabelColor = Color.Gray,
                            unfocusedBorderColor = Color.Gray
                        )
                    )
                    OutlinedTextField(
                        value = uhidIpdNo,
                        onValueChange = { uhidIpdNo = it },
                        label = { Text("UHID/IPD No.") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Constants.SecondaryColor,
                            focusedLabelColor = Constants.TertiaryColor,
                            focusedBorderColor = Constants.TertiaryColor,
                            unfocusedTextColor = Color.Black,
                            unfocusedLabelColor = Color.Gray,
                            unfocusedBorderColor = Color.Gray
                        )
                    )
                    OutlinedTextField(
                        value = age,
                        onValueChange = {
                            if(it.text != "")
                            {
                                age = it
                            }
                            else
                            {
                                age = TextFieldValue("0")
                            }
                             },
                        label = { Text("Age (in years)") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Constants.SecondaryColor,
                            focusedLabelColor = Constants.TertiaryColor,
                            focusedBorderColor = Constants.TertiaryColor,
                            unfocusedTextColor = Color.Black,
                            unfocusedLabelColor = Color.Gray,
                            unfocusedBorderColor = Color.Gray
                        )
                    )
                    OutlinedTextField(
                        value = occupation,
                        onValueChange = { occupation = it },
                        label = { Text("Occupation") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Constants.SecondaryColor,
                            focusedLabelColor = Constants.TertiaryColor,
                            focusedBorderColor = Constants.TertiaryColor,
                            unfocusedTextColor = Color.Black,
                            unfocusedLabelColor = Color.Gray,
                            unfocusedBorderColor = Color.Gray
                        )
                    )
                    OutlinedTextField(
                        value = pastIllness,
                        onValueChange = { pastIllness = it },
                        label = { Text("Past Illness") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Constants.SecondaryColor,
                            focusedLabelColor = Constants.TertiaryColor,
                            focusedBorderColor = Constants.TertiaryColor,
                            unfocusedTextColor = Color.Black,
                            unfocusedLabelColor = Color.Gray,
                            unfocusedBorderColor = Color.Gray
                        )
                    )
                    OutlinedTextField(
                        value = address,
                        onValueChange = { address = it },
                        label = { Text("Address") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Constants.SecondaryColor,
                            focusedLabelColor = Constants.TertiaryColor,
                            focusedBorderColor = Constants.TertiaryColor,
                            unfocusedTextColor = Color.Black,
                            unfocusedLabelColor = Color.Gray,
                            unfocusedBorderColor = Color.Gray
                        )
                    )
                    OutlinedTextField(
                        value = medicineHistory,
                        onValueChange = { medicineHistory = it },
                        label = { Text("Medicine History") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Constants.SecondaryColor,
                            focusedLabelColor = Constants.TertiaryColor,
                            focusedBorderColor = Constants.TertiaryColor,
                            unfocusedTextColor = Color.Black,
                            unfocusedLabelColor = Color.Gray,
                            unfocusedBorderColor = Color.Gray
                        )
                    )
                    OutlinedTextField(
                        value = dateOfAdmission,
                        onValueChange = { dateOfAdmission = it },
                        label = { Text("Date of Admission") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        trailingIcon = { Icon(imageVector = Icons.Filled.DateRange, contentDescription = "" , tint = Color.Black , modifier = Modifier.clickable { showDatePicker_admission = true })},
                        readOnly = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Constants.SecondaryColor,
                            focusedLabelColor = Constants.TertiaryColor,
                            focusedBorderColor = Constants.TertiaryColor,
                            unfocusedTextColor = Color.Black,
                            unfocusedLabelColor = Color.Gray,
                            unfocusedBorderColor = Color.Gray
                        )

                    )
                    OutlinedTextField(
                        value = dateOfVamana,
                        onValueChange = { dateOfVamana = it },
                        label = { Text("Date of Vamana") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        trailingIcon = { Icon(imageVector = Icons.Filled.DateRange, contentDescription = "" , tint = Color.Black , modifier = Modifier.clickable { showDatePicker_vamana = true })},
                        readOnly = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Constants.SecondaryColor,
                            focusedLabelColor = Constants.TertiaryColor,
                            focusedBorderColor = Constants.TertiaryColor,
                            unfocusedTextColor = Color.Black,
                            unfocusedLabelColor = Color.Gray,
                            unfocusedBorderColor = Color.Gray
                        )
                    )
                    OutlinedTextField(
                        value = prakriti,
                        onValueChange = { prakriti = it },
                        label = { Text("Prakriti - (Optional)") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Constants.SecondaryColor,
                            focusedLabelColor = Constants.TertiaryColor,
                            focusedBorderColor = Constants.TertiaryColor,
                            unfocusedTextColor = Color.Black,
                            unfocusedLabelColor = Color.Gray,
                            unfocusedBorderColor = Color.Gray
                        )
                    )

                    OutlinedTextField(
                        value = instituteId?:"",
                        onValueChange = { instituteId = it },
                        label = { Text("Institute ID") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Constants.SecondaryColor,
                            focusedLabelColor = Constants.TertiaryColor,
                            focusedBorderColor = Constants.TertiaryColor,
                            unfocusedTextColor = Color.Black,
                            unfocusedLabelColor = Color.Gray,
                            unfocusedBorderColor = Color.Gray
                        ),
                        readOnly = true
                    )
                    // Submit Button
                    Button(
                        onClick = {
                            if (instituteId.isNullOrEmpty())
                            {
                                Toast.makeText(context,"Institute ID not Available",Toast.LENGTH_SHORT).show()
                            }
                            else
                            {
                                if (name.text.isNotEmpty() &&
                                    uhidIpdNo.text.isNotEmpty() &&
                                    age.text.isNotEmpty() && age.text != "0" &&
                                    occupation.text.isNotEmpty() &&
                                    pastIllness.text.isNotEmpty() &&
                                    address.text.isNotEmpty() &&
                                    medicineHistory.text.isNotEmpty() &&
                                    dateOfAdmission.text.isNotEmpty() &&
                                    dateOfVamana.text.isNotEmpty() )
                                {
                                    apiviewmodel.CreatePatient(patient)
                                    {
                                        name = TextFieldValue("")
                                        uhidIpdNo = TextFieldValue("")
                                        age = TextFieldValue("0")
                                        occupation = TextFieldValue("")
                                        pastIllness = TextFieldValue("")
                                        address = TextFieldValue("")
                                        medicineHistory = TextFieldValue("")
                                        dateOfAdmission = TextFieldValue("")
                                        dateOfVamana = TextFieldValue("")
                                        prakriti = TextFieldValue("")
                                    }

                                } else {
                                    // One or more fields are empty
                                    Toast.makeText(context , "Fields Empty", Toast.LENGTH_SHORT).show()
                                }
                            }



                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Constants.TertiaryColor)
                    ) {
                        Text(
                            text = "Submit",
                            color = Color.White,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }


        if(showDatePicker_admission)
        {
            DatePickerDialog {
                dateOfAdmission = TextFieldValue(it)
                showDatePicker_admission = false
            }
        }

        if(showDatePicker_vamana)
        {
            DatePickerDialog {
                dateOfVamana = TextFieldValue(it)
                showDatePicker_vamana = false
            }
        }

    }
}


@Composable
fun DatePickerDialog(


    onDateSelected: (String) -> Unit,

) {

    var context = LocalContext.current
    val calendar = Calendar.getInstance()
     calendar.time = Date()

    val datePickerDialog = AlertDialog.Builder(context).create()
    val datePicker = android.widget.DatePicker(context).apply {
        init(
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH),
        ) { _, year, month, day ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, day)
        }
    }

    datePickerDialog.setView(datePicker)
    datePickerDialog.setButton(android.app.AlertDialog.BUTTON_POSITIVE, "OK") { _, _ ->
        val dateFormat = SimpleDateFormat("dd/MM/yyyy")
        var datestring = dateFormat.format(calendar.time)
        onDateSelected(datestring)
        datePickerDialog.dismiss()
    }
    datePickerDialog.setButton(android.app.AlertDialog.BUTTON_NEGATIVE, "Cancel") { _, _ ->

        datePickerDialog.dismiss()
    }

    datePickerDialog.show()
}
