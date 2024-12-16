package com.mayank.vamanaappversion2.Frontend.Navigation

import SignInScreen
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mayank.vamanaapp.Frontend.SplashScreen
import com.mayank.vamanaappversion2.Backend.API_ViewModel
import com.mayank.vamanaappversion2.Frontend.AdminCommonScreen
import com.mayank.vamanaappversion2.Frontend.DoctorCommonScreen

@Composable
fun MyNavigator(apiviewmodel : API_ViewModel = viewModel())
{
    var navController = rememberNavController()

    NavHost(navController = navController, startDestination = "splash" )
    {
        composable("splash")
        {
            SplashScreen(navController)
        }

        composable("signin")
        {
            SignInScreen(navController ,apiviewmodel)
        }

        composable("adminpanel")
        {
            AdminCommonScreen(navController , apiviewmodel)
        }

        composable("vamanapanel")
        {
            DoctorCommonScreen(navController,apiviewmodel)
        }

    }

}