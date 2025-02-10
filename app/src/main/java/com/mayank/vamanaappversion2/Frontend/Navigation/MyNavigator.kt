package com.mayank.vamanaappversion2.Frontend.Navigation

import FullScreenLoader
import SignInScreen
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.*
import com.mayank.vamanaapp.Frontend.SplashScreen
import com.mayank.vamanaappversion2.Backend.API_ViewModel
import com.mayank.vamanaappversion2.Frontend.AdminCommonScreen
import com.mayank.vamanaappversion2.Frontend.DoctorCommonScreen


// Define Routes as Constants
object Routes {
    const val SPLASH = "splash"
    const val SIGN_IN = "signin"
    const val ADMIN_PANEL = "adminpanel"
    const val DOCTOR_PANEL = "vamanapanel"
}

@Composable
fun MyNavigator(
    apiviewmodel: API_ViewModel = viewModel()
) {
    val navController = rememberNavController()
    val loading by apiviewmodel.loading.collectAsState()

    Box {
        NavHost(navController = navController, startDestination = Routes.SPLASH) {
            composable(Routes.SPLASH) { SplashScreen(navController) }
            composable(Routes.SIGN_IN) { SignInScreen(navController, apiviewmodel) }
            composable(Routes.ADMIN_PANEL) { AdminCommonScreen(navController, apiviewmodel) }
            composable(Routes.DOCTOR_PANEL) { DoctorCommonScreen(navController, apiviewmodel) }
        }

        // Show Loader Overlay when loading
        if (loading) {
            FullScreenLoader()
        }
    }
}
