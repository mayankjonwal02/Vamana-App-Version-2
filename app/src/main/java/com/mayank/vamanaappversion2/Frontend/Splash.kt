package com.mayank.vamanaapp.Frontend


import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.mayank.vamanaappversion2.Backend.getSharedPreferences
import com.mayank.vamanaappversion2.Constants

import com.mayank.vamanaappversion2.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavHostController)
{

    var context = LocalContext.current
    var logosize by remember {
        mutableStateOf(androidx.compose.animation.core.Animatable(0f))
    }

    var textvisiblity by remember {
        mutableStateOf(androidx.compose.animation.core.Animatable(0f))
    }
    Box(modifier = Modifier
        .fillMaxSize()
        , contentAlignment = Alignment.Center)
    {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Image(painter = painterResource(id = R.drawable.aiia_logo_png), contentDescription = "AIIA Logo" , modifier = Modifier.size(logosize.value.dp))
            Spacer(modifier = Modifier.height(30.dp))
            Text(text = "Vamana App" , fontWeight = FontWeight.ExtraBold , fontSize = 40.sp , color = Constants.SecondaryColor.copy(alpha = textvisiblity.value))
        }
    }

    LaunchedEffect(Unit) {
        logosize.animateTo(200f, animationSpec = tween(1000 , easing = FastOutLinearInEasing))
        delay(500)
        textvisiblity.animateTo(1f, animationSpec = tween(1000 , easing = FastOutLinearInEasing))
        delay(1000)
        var islogin = getSharedPreferences(context).getBoolean("login",false)
        if (islogin)
        {
            var role = getSharedPreferences(context).getString("role","na")
            if (role == "admin")
            {
                navController.navigate("adminpanel")
            }
            else if (role == "staff")
            {
                navController.navigate("vamanapanel")
            }
            else
            {
                navController.navigate("signin")
            }
        }
        else
        {
            navController.navigate("signin")
        }


    }

}