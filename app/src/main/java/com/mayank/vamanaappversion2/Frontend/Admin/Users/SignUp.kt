package com.mayank.vamanaapp.Frontend.Admin.Users

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.mayank.vamanaappversion2.Backend.API_ViewModel
import com.mayank.vamanaappversion2.Constants
import com.mayank.vamanaappversion2.Modals.Role
import com.mayank.vamanaappversion2.Modals.User
import com.mayank.vamanaappversion2.R

@Composable
fun CreateUserScreen(apiviewmodel: API_ViewModel) {
    var id by remember { mutableStateOf(TextFieldValue("")) }
    var contact by remember { mutableStateOf(TextFieldValue("")) }
    var password by remember { mutableStateOf(TextFieldValue("")) }
    var confirmPassword by remember { mutableStateOf(TextFieldValue("")) }
    var instituteID by remember { mutableStateOf(TextFieldValue("")) }
    var passwordVisible by remember { mutableStateOf(false) }
    var selectedRole by remember { mutableStateOf(Role.STAFF.name) }
    val roles = listOf(Role.STAFF.name, Role.ADMIN.name)
    var expanded by remember { mutableStateOf(false) }
    val context = LocalContext.current

    // Powers management
    val availablePowers = if (selectedRole == Role.ADMIN.name ) listOf("Edit Questions", "View Data","Edit Users")  else listOf("Export Data")
    val selectedPowers = remember { mutableStateListOf<String>() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
            .background(Constants.backgroundGradient),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .wrapContentHeight(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .wrapContentHeight(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.6f)
                ),
                border = BorderStroke(2.dp, Color.Black)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.aiia_logo_png),
                        contentDescription = null,
                        modifier = Modifier
                            .zIndex(1f)
                            .size(100.dp)
                    )

                    Text(
                        text = "Create User",
                        style = MaterialTheme.typography.headlineMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                        color = Constants.TertiaryColor,
                        fontWeight = FontWeight.ExtraBold
                    )

                    OutlinedTextField(
                        value = id,
                        onValueChange = { id = it },
                        label = { Text("ID") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = contact,
                        onValueChange = { contact = it },
                        label = { Text("Contact") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                    )

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = if (passwordVisible) "Hide Password" else "Show Password"
                                )
                            }
                        }
                    )

                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        label = { Text("Confirm Password") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = if (passwordVisible) "Hide Password" else "Show Password"
                                )
                            }
                        }
                    )

                    // Dropdown for role selection
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = selectedRole,
                            onValueChange = {},
                            label = { Text("Role") },
                            modifier = Modifier.fillMaxWidth(),
                            readOnly = true,
                            trailingIcon = {
                                IconButton(onClick = { expanded = !expanded }) {
                                    Icon(
                                        imageVector = Icons.Default.ArrowDropDown,
                                        contentDescription = null
                                    )
                                }
                            }
                        )

                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            roles.forEach { role ->
                                DropdownMenuItem(
                                    onClick = {
                                        selectedRole = role
                                        expanded = false
                                    },
                                    text = {
                                        Text(role)
                                    }
                                )
                            }
                        }
                    }
                    Text("Choose Powers", style = MaterialTheme.typography.bodyLarge , fontWeight = FontWeight.Bold)
                    availablePowers.forEach { power ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = selectedPowers.contains(power),
                                onCheckedChange = { isChecked ->
                                    if (isChecked) {
                                        selectedPowers.add(power)
                                    } else {
                                        selectedPowers.remove(power)
                                    }
                                }
                            )
                            Text(text = power)
                        }
                    }
                    // Powers Section: Only visible if role is "ADMIN"
                    if (selectedRole == Role.STAFF.name) {

                        OutlinedTextField(
                            value = instituteID,
                            onValueChange = { instituteID = it },
                            label = { Text("Institute ID") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }



                    // Submit Button
                    Button(
                        onClick = {
                            if (password.text == confirmPassword.text) {
                                val user = User(
                                    userID = id.text,
                                    contact = contact.text,
                                    password = password.text,
                                    role = selectedRole,
                                    powers = if (selectedRole == Role.ADMIN.name) selectedPowers else emptyList(),
                                    createdAt = null,
                                    updatedAt = null
                                )
                                apiviewmodel.CreateUser(user) {
                                    id = TextFieldValue("")
                                    contact = TextFieldValue("")
                                    password = TextFieldValue("")
                                    confirmPassword = TextFieldValue("")
                                    selectedPowers.clear()
                                }
                            } else {
                                Toast.makeText(context, "Password Mis-Match", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Constants.TertiaryColor)
                    ) {
                        Text(text = "Create User")
                    }
                }
            }
        }
    }
}









