package com.mayank.vamanaappversion2.Frontend

import AllPatientsScreen
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.QuestionAnswer
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import com.example.analysis.AnalysisScreen
import com.mayank.vamanaapp.Frontend.Admin.Questions.QuestionsScreen
import com.mayank.vamanaapp.Frontend.Admin.Users.AllUsersScreen
import com.mayank.vamanaapp.Frontend.Admin.Users.CreateUserScreen
import com.mayank.vamanaappversion2.Backend.API_ViewModel
import com.mayank.vamanaappversion2.Backend.getSharedPreferences
import com.mayank.vamanaappversion2.Constants
import com.mayank.vamanaappversion2.Frontend.Admin.Questions.AnalysisQuestions
import com.mayank.vamanaappversion2.R
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminCommonScreen(navController: NavHostController, apiviewmodel: API_ViewModel)
{
 
        val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
        val scope = rememberCoroutineScope()
    var context = LocalContext.current
    var role = getSharedPreferences(context).getString("role","Not Defined")
        var content_state = remember {
            mutableStateOf(0)
        }
        ModalNavigationDrawer(
            drawerContent = {
                ModalDrawerSheet {
                    Column(
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(Modifier.height(12.dp))
//                        Text("Doctor's Panel", modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.titleLarge)
//                        HorizontalDivider()
                        Image(
                            painter = painterResource(id = R.drawable.aiia_logo_png),
                            contentDescription = null,
                            modifier = Modifier
                                .zIndex(1f)
                                .size(100.dp), // Ensure it appears above the card,


                        )

                            Divider(modifier = Modifier.padding(vertical = 8.dp))
                        NavigationDrawerItem(
                            label = { Text("Questions") },
                            selected = false,
                            icon = { Icon(Icons.Outlined.QuestionAnswer, contentDescription = null) },
                            onClick = { content_state.value = 0
                                scope.launch {
                                    drawerState.close()
                                }
                            }
                        )
                        NavigationDrawerItem(
                            label = { Text("Analysis Questions") },
                            selected = false,
                            icon = { Icon(Icons.Outlined.QuestionAnswer, contentDescription = null) },
                            onClick = { content_state.value = 4
                                scope.launch {
                                    drawerState.close()
                                }
                            }
                        )
                        NavigationDrawerItem(
                            label = { Text("Create Users") },
                            selected = false,
                            icon = { Icon(Icons.Default.Person, contentDescription = null) },
                            onClick = { content_state.value = 1
                                scope.launch {
                                    drawerState.close()
                                }},
                        )
                        NavigationDrawerItem(
                            label = { Text("View Users") },
                            selected = false,
                            icon = { Icon(Icons.Default.Groups, contentDescription = null) },
                            onClick = { content_state.value = 2
                                scope.launch {
                                    drawerState.close()
                                }},
                        )
                        NavigationDrawerItem(
                            label = { Text("View Patient Data") },
                            selected = false,
                            icon = { Icon(Icons.Default.Groups, contentDescription = null) },
                            onClick = { content_state.value = 3
                                scope.launch {
                                    drawerState.close()
                                }},
                        )
                        NavigationDrawerItem(
                            label = { Text("OverAll Analysis") },
                            selected = false,
                            icon = { Icon(Icons.Default.Analytics, contentDescription = null) },
                            onClick = { content_state.value = 5
                                scope.launch {
                                    drawerState.close()
                                }},
                        )
                        NavigationDrawerItem(
                            label = { Text("Logout") },
                            selected = false,
                            icon = { Icon(Icons.Default.Logout, contentDescription = null) },
                            onClick = { navController.navigate("signin") {
                                popUpTo(navController.graph.startDestinationId) { inclusive = true }
                                launchSingleTop = true
                            }
                            },
                        )


                        Spacer(Modifier.height(12.dp))
                    }
                }
            },
            drawerState = drawerState
        ) {
            Scaffold(
                containerColor = Constants.PrimaryColor_second,
                topBar = {
                    TopAppBar(
                        title = { Text("Admin Panel", fontWeight = FontWeight.SemiBold) },
                        navigationIcon = {
                            IconButton(onClick = {
                                scope.launch {
                                    if (drawerState.isClosed) {
                                        drawerState.open()
                                    } else {
                                        drawerState.close()
                                    }
                                }
                            }) {
                                Icon(Icons.Default.Menu, contentDescription = "Menu")
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(containerColor = Constants.PrimaryColor)
                    )
                }
            ) { padding ->
                Crossfade(targetState = content_state, modifier = Modifier.padding(padding)) {
                    when(it.value)
                    {
                        0 -> {
                            QuestionsScreen(apiviewmodel)
                        }

                        1 -> {
                            CreateUserScreen(apiviewmodel)
                        }

                        2 -> {
                            AllUsersScreen(apiviewmodel)
                        }
                        3 -> {
                            AllPatientsScreen(apiviewmodel)
                        }

                        4 -> {
                            AnalysisQuestions(apiviewmodel)
                        }
                        5 -> {
                            AnalysisScreen(apiviewmodel)
                        }

                    }
                }
            }
        }
    }