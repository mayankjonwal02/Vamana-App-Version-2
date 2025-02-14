import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.with
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import com.mayank.vamanaappversion2.Backend.API_ViewModel
import com.mayank.vamanaappversion2.Backend.getSharedPreferences
import com.mayank.vamanaappversion2.Backend.saveList
import com.mayank.vamanaappversion2.Constants
import com.mayank.vamanaappversion2.Functions.isInternetConnected
import com.mayank.vamanaappversion2.Modals.Role
import com.mayank.vamanaappversion2.R

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun SignInScreen(navController: NavHostController, apiviewmodel: API_ViewModel) {


    // State to toggle between Doctor/Admin login
    var isDoctorLogin by remember { mutableStateOf(true) }
    // State for the text fields
    var id by remember { mutableStateOf(TextFieldValue("")) }
    var password by remember { mutableStateOf(TextFieldValue("")) }
    // State to toggle password visibility
    var passwordVisible by remember { mutableStateOf(false) }
    var context = LocalContext.current
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
            .background(Constants.backgroundGradient), // Replace with your gradient or solid color
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
                    .wrapContentHeight(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.6f)
                ),
                border = BorderStroke(2.dp,Color.Black)
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
                            .zIndex(1f) // Ensure it appears above the card,


                    )

                    AnimatedContent(
                        targetState = if (isDoctorLogin) "Sign In" else "Admin",
                        transitionSpec = {
                            fadeIn(animationSpec = tween(300)) with fadeOut(animationSpec = tween(300))
                        }
                    ) { targetText ->
                        Text(
                            text = targetText,
                            style = MaterialTheme.typography.headlineMedium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth(),
                            color = Constants.TertiaryColor,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }


                    OutlinedTextField(
                        value = id,
                        onValueChange = { id = it },
                        label = { Text(if (isDoctorLogin) "Staff ID" else "Admin ID") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
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

                    // Submit Button
                    Button(
                        onClick = {
                                  if(!isDoctorLogin)
                                  {
//                                      Toast.makeText(context , "id: " + id.text + " pass : " + password.text , Toast.LENGTH_SHORT).show()
                                      UserLogin(id.text , password.text , navController ,context,apiviewmodel,Role.ADMIN )
                                  }
                            else
                                  {
                                      UserLogin(id.text , password.text , navController ,context,apiviewmodel,Role.STAFF )

                                  }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor =Constants.TertiaryColor)
                    ) {
                        Text(text = "Login")
                    }

                    TextButton(
                        onClick = {

//                            isDoctorLogin = !isDoctorLogin
                            UserLogin(id.text , password.text , navController ,context,apiviewmodel,Role.ADMIN )

                        },
//
                    ) {
                        Text(
                            text =
//                            if(isDoctorLogin)
                                "Login as Admin"
//                            else
//                                "login as Staff"
                            ,
                            color = Constants.SecondaryColor,
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}



fun UserLogin(
    id: String,
    password: String,
    navController: NavHostController,
    context: Context,
    apiviewmodel: API_ViewModel,
    role: Role
)
{
    if(id == "" || password == "")
    {
        Toast.makeText(context , "Empty Credentials" , Toast.LENGTH_SHORT).show()
        return

    }
    if (isInternetConnected(context))
    {
        if ( id == Constants.AdminID && password == Constants.AdminPassword && role == Role.ADMIN)
        {
            getSharedPreferences(context).edit().putString("role","admin").apply()
            getSharedPreferences(context).edit().putString("admin_type","super_admin").apply()
            navController.navigate("adminpanel")
            Toast.makeText(context , "Login Successful" , Toast.LENGTH_SHORT).show()
        }
        else if ( id != Constants.AdminID && password != Constants.AdminPassword)
        {

            apiviewmodel.LoginUser(id , password , role.name )
            { isSuccess , user ->
                if (isSuccess)
                {
                    saveList(context,"powers",user.powers)
                    if (role == Role.ADMIN)
                    {
                        getSharedPreferences(context).edit().putString("role","admin").apply()

                        navController.navigate("adminpanel")
                        Toast.makeText(context , "Welcome Admin" , Toast.LENGTH_SHORT).show()
                    }
                    else if (role == Role.STAFF)
                    {
                        getSharedPreferences(context).edit().putString("role","staff").apply()
                        getSharedPreferences(context).edit().putString("institute_id",user.instituteID).apply()
                        navController.navigate("vamanapanel")
                        Toast.makeText(context , "Login Successful" , Toast.LENGTH_SHORT).show()
                    }

                }
                else
                {
                    Toast.makeText(context , "Invalid Credentials" , Toast.LENGTH_SHORT).show()
                }
            }


        }
        else
        {
            Toast.makeText(context , "Invalid Credentials" , Toast.LENGTH_SHORT).show()
        }
    }
    else
    {
        Toast.makeText(context , "Internet Not Connected" , Toast.LENGTH_LONG).show()
    }

}