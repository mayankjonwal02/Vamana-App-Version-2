package com.mayank.vamanaappversion2

import AllPatientsScreen
import SignInScreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.mayank.vamanaapp.Frontend.Admin.Questions.QuestionsScreen
import com.mayank.vamanaapp.Frontend.Admin.Users.AllUsersScreen
import com.mayank.vamanaapp.Frontend.Admin.Users.CreateUserScreen
import com.mayank.vamanaapp.Frontend.Doctor.AddPatientFormScreen
import com.mayank.vamanaapp.Frontend.Doctor.AnswerQuestionScreen
import com.mayank.vamanaapp.Frontend.SplashScreen
import com.mayank.vamanaappversion2.Frontend.Navigation.MyNavigator
import com.mayank.vamanaappversion2.ui.theme.VamanaAppVersion2Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VamanaAppVersion2Theme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Box(modifier = Modifier
                        .fillMaxSize()
                        .background(Constants.backgroundGradient))
                    {
                        MyNavigator()
                    }

                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    VamanaAppVersion2Theme {
        Greeting("Android")
    }
}