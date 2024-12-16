package com.mayank.vamanaapp.Frontend.Admin.Users

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.mayank.vamanaappversion2.Backend.API_ViewModel
import com.mayank.vamanaappversion2.Constants
import com.mayank.vamanaappversion2.Modals.Role
import com.mayank.vamanaappversion2.Modals.User

@Composable
fun AllUsersScreen(apiviewmodel: API_ViewModel) {
    // Collecting current users from ViewModel
    val allusers by apiviewmodel.current_users.collectAsState()

    // State for search query
    var searchQuery by remember { mutableStateOf("") }

    // State for selected user (editing)
    var selectedUser by remember { mutableStateOf<User?>(null) }

    // Filtered users based on search query
    val filteredUsers = allusers.filter { user ->
        user.userID.contains(searchQuery, ignoreCase = true)
    }

    // Trigger data fetch when the screen is loaded
    LaunchedEffect(Unit) {
        apiviewmodel.FetchUsers()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Title
        Text(
            text = "All Users",
            fontWeight = FontWeight.ExtraBold,
            color = Color.Black,
            fontSize = 30.sp
        )

        // Search Field
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Search by ID") },
            leadingIcon = {
                Icon(imageVector = Icons.Default.Search, contentDescription = "Search Icon")
            },
            modifier = Modifier.fillMaxWidth()
        )

        // User List
        filteredUsers.forEach { user ->
            UserCard(
                user = user,
                onEditUser = { selectedUser = it },
                onDeleteUser = {
                    apiviewmodel.DeleteUser(user.userID){
                        apiviewmodel.FetchUsers()
                    }
                }
            )
        }
    }

    // Edit User Dialog
    if (selectedUser != null) {
        EditUserDialog(
            user = selectedUser!!,
            onDismiss = { selectedUser = null },
            onSave = { updatedUser ->
                apiviewmodel.UpdateUser(updatedUser){
                    apiviewmodel.FetchUsers()
                }
//                apiviewmodel.updateUser(updatedUser) // Update via ViewModel
                selectedUser = null
            }
        )
    }
}

@Composable
fun UserCard(
    user: User,
    onEditUser: (User) -> Unit,
    onDeleteUser: (User) -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }
    var isDelete by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { isExpanded = !isExpanded },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Constants.PrimaryColor),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = user.userID,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                )

                IconButton(onClick = { isExpanded = !isExpanded }) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = if (isExpanded) "Collapse" else "Expand"
                    )
                }
            }

            AnimatedVisibility(
                visible = isExpanded,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Contact:  ${user.contact}")
                    Text("Role:  ${user.role}")
                    Text("Password:  ${user.password}")
                    if (! user.powers.isNullOrEmpty() && user.role == Role.ADMIN.name){
                        Row {
                            Text(text = "Powers:  ")
                            Column {
                                user.powers.forEach { power ->
                                    Text(text = power)
                                }
                            }

                        }

                    }
                    Spacer(modifier = Modifier.height(30.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Button(
                            onClick = { onEditUser(user) },
                            colors = ButtonDefaults.buttonColors(containerColor = Constants.TertiaryColor)
                        ) {
                            Icon(imageVector = Icons.Filled.Edit, contentDescription = "", tint = Color.DarkGray)
                        }

                        Button(
                            onClick = {
                                isDelete = true

                                      },
                            colors = ButtonDefaults.buttonColors(containerColor = Constants.TertiaryColor)
                        ) {
                            Icon(imageVector = Icons.Filled.Delete, contentDescription = "", tint = Color.DarkGray)
                        }
                    }
                }
            }
        }
    }

    if(isDelete)
    {
        ConfirmDelete(user = user, onDismiss = { isDelete = false }) {
            isDelete = false
            onDeleteUser(user)
        }
    }
}

@Composable
fun ConfirmDelete(user: User,
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
        text =  { Text("Are you sure to delete UserID: ${user.userID}") })


}
@Composable
fun EditUserDialog(
    user: User,
    onDismiss: () -> Unit,
    onSave: (User) -> Unit
) {
    var contact by remember { mutableStateOf(user.contact) }
    var role by remember { mutableStateOf(user.role) }
    var password by remember { mutableStateOf(user.password) }
    val availablePowers = listOf( "Edit Questions", "View Data")
    val selectedPowers = remember { mutableStateListOf(*user.powers.toTypedArray()) }
    val roles = listOf(Role.STAFF.name, Role.ADMIN.name)
    var expanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onSave(
                    user.copy(
                        contact = contact,
                        role = role,
                        password = password,
                        powers = selectedPowers
                    )
                )
            }) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        title = { Text("Edit User Details") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                // Contact Field
                OutlinedTextField(
                    value = contact,
                    onValueChange = { contact = it },
                    label = { Text("Contact") }
                )

                // Role Field
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = role,
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
                        roles.forEach { check_role ->
                            DropdownMenuItem(
                                onClick = {
                                    role = check_role
                                    expanded = false
                                },
                                text = {
                                    Text(check_role)
                                }
                            )
                        }
                    }
                }

                // Password Field
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") }
                )

                // Powers Section
                Text("Powers")
                availablePowers.forEach { power ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = power in selectedPowers,
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
            }
        }
    )
}



// Sample User Data Class
//data class User(
//    val id: String,
//    val contact: String,
//    val role: String,
//    val password: String
//)
