package com.mayank.vamanaappversion2.Backend

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mayank.vamanaappversion2.Modals.Patient
import com.mayank.vamanaappversion2.Modals.PatientQuestion
import com.mayank.vamanaappversion2.Modals.Question
import com.mayank.vamanaappversion2.Modals.QuestionDetail
import com.mayank.vamanaappversion2.Modals.User
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class API_ViewModel(application: Application) : AndroidViewModel(application) {

    var RetrofitClient = RetrofitInstance.getClient()
    var context = application

    private var _current_users = MutableStateFlow<List<User>>(emptyList())
    var current_users : StateFlow<List<User>> = _current_users

    private var _all_questions = MutableStateFlow<List<Question>>(emptyList())
    var all_questions : StateFlow<List<Question>> = _all_questions

    private var _all_patients = MutableStateFlow<List<Patient>>(emptyList())
    var all_patients : StateFlow<List<Patient>> = _all_patients



    fun TestConnection()
    {
        viewModelScope.launch {
            try {
                var responce = async{ RetrofitClient.testConnection() }.await()
                withContext(Dispatchers.Main){
                    if(responce.executed)
                    {
                        Toast.makeText(context , responce.message , Toast.LENGTH_SHORT).show()
                    }
                    else
                    {
                        Toast.makeText(context , "Can't Connect" , Toast.LENGTH_SHORT).show()
                    }
                }

            }
            catch (e:Exception)
            {
                withContext(Dispatchers.Main){
                    Toast.makeText(context , "Can't Connect" , Toast.LENGTH_SHORT).show()

                }
            }

        }
    }

    fun LoginUser(
        id: String,
        password: String,
        role: String,

        onResult: (Boolean) -> Unit
    ) {
        Log.i("NetworkCall","SignIn API Called")
        viewModelScope.launch(CoroutineExceptionHandler {_,ex -> Log.i("NetworkCall",ex.message.toString())}) {
            try {
                val requestBody = SignInRequest(id, password, role)
                val response = RetrofitClient.loginUser(requestBody)

                // Check response and invoke result
                onResult(response.executed)
            } catch (e: HttpException) {
                withContext(Dispatchers.Main) {
                    when (e.code()) {
                        401 -> {
                            Toast.makeText(context, "Invalid Credentials", Toast.LENGTH_SHORT).show()
                        }
                        500 -> {
                            Toast.makeText(context, "Server Issue", Toast.LENGTH_SHORT).show()
                        }
                        else -> {
                            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Login Failed: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    fun CreateUser(user: User , onResult: () -> Unit)
    {
        Log.i("NetworkCall","SignUp API Called")

        viewModelScope.launch(CoroutineExceptionHandler {_,ex -> Log.i("NetworkCall",ex.message.toString())}) {
            try {
                var requestBody : SignUpRequest = SignUpRequest(user.userID,user.contact,user.password,user.role,user.powers)
                var responce = async { RetrofitClient.createUser(requestBody) }.await()
                if (responce.executed)
                {
                    onResult()
                }
                withContext(Dispatchers.Main)
                {
                    Toast.makeText(context , responce.message , Toast.LENGTH_SHORT).show()
                }

            }
            catch (e:HttpException)
            {
                withContext(Dispatchers.Main) {
                    when (e.code()) {
                        500 -> {
                            Toast.makeText(context, "Server Issue", Toast.LENGTH_SHORT).show()
                        }
                        else -> {
                            Toast.makeText(context, "Error While Creating User", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }

            catch (e:Exception)
            {
                withContext(Dispatchers.Main)
                {
                    Toast.makeText(context , "Error While Creating User" , Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    fun FetchUsers()
    {
        Log.i("NetworkCall","Fetch Users API Called")

        viewModelScope.launch(CoroutineExceptionHandler {_,ex -> Log.i("NetworkCall",ex.message.toString())}) {
            try {
                var responce = async { RetrofitClient.fetchAllUsers() }.await()
                if (responce.executed)
                {
                    _current_users.value = responce.users
                }
                else
                {
                    withContext(Dispatchers.Main)
                    {
                        Toast.makeText(context, responce.message, Toast.LENGTH_SHORT).show()
                    }
                }

            }
            catch (e:HttpException)
            {
                withContext(Dispatchers.Main) {
                    when (e.code()) {
                        500 -> {
                            Toast.makeText(context, "Server Issue", Toast.LENGTH_SHORT).show()
                        }
                        else -> {
                            Log.i("NetworkCall","1. " + e.localizedMessage)
                            Toast.makeText(context, "Error While Fetching Users", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }

            catch (e:Exception)
            {
                withContext(Dispatchers.Main)
                {
                    Log.i("NetworkCall","2." + e.localizedMessage)

                    Toast.makeText(context , "Error While Fetching Users" , Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    fun UpdateUser(user: User,onResult: () -> Unit)
    {
        Log.i("NetworkCall","Delete Users API Called")

        viewModelScope.launch(CoroutineExceptionHandler {_,ex -> Log.i("NetworkCall",ex.message.toString())}) {
            try {
                var responce = async { RetrofitClient.updateUserByID(user.userID,user) }.await()
                if (responce.executed)
                {
                    onResult()
                }
                withContext(Dispatchers.Main)
                {
                    Toast.makeText(context , responce.message , Toast.LENGTH_SHORT).show()
                }

            }
            catch (e:HttpException)
            {
                withContext(Dispatchers.Main) {
                    when (e.code()) {
                        500 -> {
                            Toast.makeText(context, "Server Issue", Toast.LENGTH_SHORT).show()
                        }
                        else -> {
                            Log.i("NetworkCall","1. " + e.localizedMessage)
                            Toast.makeText(context, "Error While Updating User", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }

            catch (e:Exception)
            {
                withContext(Dispatchers.Main)
                {
                    Log.i("NetworkCall","2." + e.localizedMessage)

                    Toast.makeText(context , "Error While Updating User" , Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    fun DeleteUser(id: String,onResult: () -> Unit)
    {
        Log.i("NetworkCall","Delete Users API Called")

        viewModelScope.launch(CoroutineExceptionHandler {_,ex -> Log.i("NetworkCall",ex.message.toString())}) {
            try {
                var responce = async { RetrofitClient.deleteUserByID(id) }.await()
                if (responce.executed)
                {
                   onResult()
                }
                withContext(Dispatchers.Main)
                {
                    Toast.makeText(context , responce.message , Toast.LENGTH_SHORT).show()
                }

            }
            catch (e:HttpException)
            {
                withContext(Dispatchers.Main) {
                    when (e.code()) {
                        500 -> {
                            Toast.makeText(context, "Server Issue", Toast.LENGTH_SHORT).show()
                        }
                        else -> {
                            Log.i("NetworkCall","1. " + e.localizedMessage)
                            Toast.makeText(context, "Error While Deleting User", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }

            catch (e:Exception)
            {
                withContext(Dispatchers.Main)
                {
                    Log.i("NetworkCall","2." + e.localizedMessage)

                    Toast.makeText(context , "Error While Deleting User" , Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    fun GetAllQuestions()
    {
        Log.i("NetworkCall","Fetch Users API Called")

        viewModelScope.launch(CoroutineExceptionHandler {_,ex -> Log.i("NetworkCall",ex.message.toString())}) {
            try {
                var responce = async { RetrofitClient.getAllQuestions() }.await()
                Log.i("NetworkCall",responce.questions.toString())
                if (responce.executed)
                {
                    _all_questions.value = responce.questions
                }
                else
                {
                    withContext(Dispatchers.Main)
                    {
                        Toast.makeText(context, responce.message, Toast.LENGTH_SHORT).show()
                    }
                }

            }
            catch (e:HttpException)
            {
                withContext(Dispatchers.Main) {
                    when (e.code()) {
                        500 -> {
                            Toast.makeText(context, "Server Issue", Toast.LENGTH_SHORT).show()
                        }
                        else -> {
                            Log.i("NetworkCall","1. " + e.localizedMessage)
                            Toast.makeText(context, "Error While Fetching Questions", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }

            catch (e:Exception)
            {
                withContext(Dispatchers.Main)
                {
                    Log.i("NetworkCall","2." + e.localizedMessage)

                    Toast.makeText(context , "Error While Fetching Questions" , Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun CreateCategory( category : String,onResult: (String) -> Unit)
    {
        Log.i("NetworkCall","Delete Users API Called")

        viewModelScope.launch(CoroutineExceptionHandler {_,ex -> Log.i("NetworkCall",ex.message.toString())}) {
            try {
                var requestBody = CategoryRequest(category)
                var responce = async { RetrofitClient.createCategory(requestBody) }.await()
                if (responce.executed)
                {
                    onResult(responce.message)
                }
                withContext(Dispatchers.Main)
                {
                    Toast.makeText(context , responce.message , Toast.LENGTH_SHORT).show()
                }

            }
            catch (e:HttpException)
            {
                withContext(Dispatchers.Main) {
                    when (e.code()) {
                        500 -> {
                            Toast.makeText(context, "Server Issue", Toast.LENGTH_SHORT).show()
                        }
                        else -> {
                            Log.i("NetworkCall","1. " + e.localizedMessage)
                            Toast.makeText(context, "Error While Updating User", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }

            catch (e:Exception)
            {
                withContext(Dispatchers.Main)
                {
                    Log.i("NetworkCall","2." + e.localizedMessage)

                    Toast.makeText(context , "Error While Updating User" , Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    fun UpdateCategory(uid : String ,category : String,onResult: (String) -> Unit)
    {
        Log.i("NetworkCall","Update Questions API Called")

        viewModelScope.launch(CoroutineExceptionHandler {_,ex -> Log.i("NetworkCall",ex.message.toString())}) {
            try {
                var requestBody = CategoryRequest(category)
                var responce = async { RetrofitClient.updateCategory(UID = uid,category = requestBody) }.await()
                if (responce.executed)
                {
                    onResult(responce.message)
                }
                withContext(Dispatchers.Main)
                {
                    Toast.makeText(context , responce.message , Toast.LENGTH_SHORT).show()
                }

            }
            catch (e:HttpException)
            {
                withContext(Dispatchers.Main) {
                    when (e.code()) {
                        500 -> {
                            Toast.makeText(context, "Server Issue", Toast.LENGTH_SHORT).show()
                        }
                        else -> {
                            Log.i("NetworkCall","1. " + e.localizedMessage)
                            Toast.makeText(context, "Error While Updating User", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }

            catch (e:Exception)
            {
                withContext(Dispatchers.Main)
                {
                    Log.i("NetworkCall","2." + e.localizedMessage)

                    Toast.makeText(context , "Error While Updating User" , Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    fun DeleteCategory(uid : String ,onResult: (String) -> Unit)
    {
        Log.i("NetworkCall","Update Questions API Called")

        viewModelScope.launch(CoroutineExceptionHandler {_,ex -> Log.i("NetworkCall",ex.message.toString())}) {
            try {

                var responce = async { RetrofitClient.deleteCategory(UID = uid) }.await()
                if (responce.executed)
                {
                    onResult(responce.message)
                }
                withContext(Dispatchers.Main)
                {
                    Toast.makeText(context , responce.message , Toast.LENGTH_SHORT).show()
                }

            }
            catch (e:HttpException)
            {
                withContext(Dispatchers.Main) {
                    when (e.code()) {
                        500 -> {
                            Toast.makeText(context, "Server Issue", Toast.LENGTH_SHORT).show()
                        }
                        else -> {
                            Log.i("NetworkCall","1. " + e.localizedMessage)
                            Toast.makeText(context, "Error While Updating User", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }

            catch (e:Exception)
            {
                withContext(Dispatchers.Main)
                {
                    Log.i("NetworkCall","2." + e.localizedMessage)

                    Toast.makeText(context , "Error While Updating User" , Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    fun AddQuestion(uid : String , question: QuestionDetail ,onResult: (String) -> Unit)
    {
        Log.i("NetworkCall","Update Questions API Called")

        viewModelScope.launch(CoroutineExceptionHandler {_,ex -> Log.i("NetworkCall",ex.message.toString())}) {
            try {

                var responce = async { RetrofitClient.addQuestion(uid,question) }.await()
                if (responce.executed)
                {
                    onResult(responce.message)
                }
                withContext(Dispatchers.Main)
                {
                    Toast.makeText(context , responce.message , Toast.LENGTH_SHORT).show()
                }

            }
            catch (e:HttpException)
            {
                withContext(Dispatchers.Main) {
                    when (e.code()) {
                        500 -> {
                            Toast.makeText(context, "Server Issue", Toast.LENGTH_SHORT).show()
                        }
                        else -> {
                            Log.i("NetworkCall","1. " + e.localizedMessage)
                            Toast.makeText(context, "Error While Adding Question", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }

            catch (e:Exception)
            {
                withContext(Dispatchers.Main)
                {
                    Log.i("NetworkCall","2." + e.localizedMessage)

                    Toast.makeText(context , "Error While Adding Question" , Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    fun UpdateQuestion(uid : String , question: QuestionDetail ,onResult: (String) -> Unit)
    {
        Log.i("NetworkCall","Update Questions API Called")

        viewModelScope.launch(CoroutineExceptionHandler {_,ex -> Log.i("NetworkCall",ex.message.toString())}) {
            try {

                var responce = async { RetrofitClient.updateQuestions(categoryUID = uid , questionUID = question.id!! , question) }.await()
                if (responce.executed)
                {
                    onResult(responce.message)
                }
                withContext(Dispatchers.Main)
                {
                    Toast.makeText(context , responce.message , Toast.LENGTH_SHORT).show()
                }

            }
            catch (e:HttpException)
            {
                withContext(Dispatchers.Main) {
                    when (e.code()) {
                        500 -> {
                            Toast.makeText(context, "Server Issue", Toast.LENGTH_SHORT).show()
                        }
                        else -> {
                            Log.i("NetworkCall","1. " + e.localizedMessage)
                            Toast.makeText(context, "Error While Updating Question", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }

            catch (e:Exception)
            {
                withContext(Dispatchers.Main)
                {
                    Log.i("NetworkCall","2." + e.localizedMessage)

                    Toast.makeText(context , "Error While Updating Question" , Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    fun DeleteQuestion(categoryid : String , questionid : String ,onResult: (String) -> Unit)
    {
        Log.i("NetworkCall","Update Questions API Called")

        viewModelScope.launch(CoroutineExceptionHandler {_,ex -> Log.i("NetworkCall",ex.message.toString())}) {
            try {

                var responce = async { RetrofitClient.deleteQuestions(categoryUID = categoryid , questionUID = questionid ) }.await()
                if (responce.executed)
                {
                    onResult(responce.message)
                }
                withContext(Dispatchers.Main)
                {
                    Toast.makeText(context , responce.message , Toast.LENGTH_SHORT).show()
                }

            }
            catch (e:HttpException)
            {
                withContext(Dispatchers.Main) {
                    when (e.code()) {
                        500 -> {
                            Toast.makeText(context, "Server Issue", Toast.LENGTH_SHORT).show()
                        }
                        else -> {
                            Log.i("NetworkCall","1. " + e.localizedMessage)
                            Toast.makeText(context, "Error While Deleting Question", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }

            catch (e:Exception)
            {
                withContext(Dispatchers.Main)
                {
                    Log.i("NetworkCall","2." + e.localizedMessage)

                    Toast.makeText(context , "Error While Deleting Question" , Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    fun CreatePatient(patient: Patient ,onResult: () -> Unit)
    {
        Log.i("NetworkCall","Create patient API Called")

        viewModelScope.launch(CoroutineExceptionHandler {_,ex -> Log.i("NetworkCall",ex.message.toString())}) {
            try {

                var responce = async { RetrofitClient.createPatient(patient) }.await()
                if (responce.executed)
                {
                    onResult()
                }
                withContext(Dispatchers.Main)
                {
                    Toast.makeText(context , responce.message , Toast.LENGTH_SHORT).show()
                }

            }
            catch (e:HttpException)
            {
                withContext(Dispatchers.Main) {
                    when (e.code()) {
                        500 -> {
                            Toast.makeText(context, "Server Issue", Toast.LENGTH_SHORT).show()
                        }
                        else -> {
                            Log.i("NetworkCall","1. " + e.localizedMessage)
                            Toast.makeText(context, "Error While Deleting Question", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }

            catch (e:Exception)
            {
                withContext(Dispatchers.Main)
                {
                    Log.i("NetworkCall","2." + e.localizedMessage)

                    Toast.makeText(context , "Error While Deleting Question" , Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    fun GetAllPatients()
    {
        Log.i("NetworkCall","Fetch Patients API Called")

        viewModelScope.launch(CoroutineExceptionHandler {_,ex -> Log.i("NetworkCall",ex.message.toString())}) {
            try {
                var responce = async { RetrofitClient.getPatients() }.await()
                if (responce.executed)
                {
                    _all_patients.value = responce.patients
                }
                else
                {
                    withContext(Dispatchers.Main)
                    {
                        Toast.makeText(context, responce.message, Toast.LENGTH_SHORT).show()
                    }
                }

            }
            catch (e:HttpException)
            {
                withContext(Dispatchers.Main) {
                    when (e.code()) {
                        500 -> {
                            Toast.makeText(context, "Server Issue", Toast.LENGTH_SHORT).show()
                        }
                        else -> {
                            Log.i("NetworkCall","1. " + e.localizedMessage)
                            Toast.makeText(context, "Error While Fetching Patients", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }

            catch (e:Exception)
            {
                withContext(Dispatchers.Main)
                {
                    Log.i("NetworkCall","2." + e.localizedMessage)

                    Toast.makeText(context , "Error While Fetching Patients" , Toast.LENGTH_SHORT).show()
                }
            }
        }
    }



    fun UpdatePatient(uhid : String ,patient: Patient, onResult: () -> Unit)
    {
        Log.i("NetworkCall","Update Patients API Called for ${uhid}")

        viewModelScope.launch(CoroutineExceptionHandler {_,ex -> Log.i("NetworkCall",ex.message.toString())}) {
            try {
                var request = UpdatePatientsRequest(update = patient)
                var responce = async { RetrofitClient.updatePatients(uhid,request) }.await()
                if (responce.executed)
                {
                    onResult()
                }

                withContext(Dispatchers.Main)
                {
                    Toast.makeText(context, responce.message, Toast.LENGTH_SHORT).show()
                }


            }
            catch (e:HttpException)
            {
                withContext(Dispatchers.Main) {
                    when (e.code()) {
                        500 -> {
                            Toast.makeText(context, "Server Issue", Toast.LENGTH_SHORT).show()
                        }
                        else -> {
                            Log.i("NetworkCall","1. " + e.localizedMessage)
                            Toast.makeText(context, "Error While Updating Patients", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }

            catch (e:Exception)
            {
                withContext(Dispatchers.Main)
                {
                    Log.i("NetworkCall","2." + e.localizedMessage)

                    Toast.makeText(context , "Error While Updating Patients" , Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    fun DeletePatient(uhid : String, onResult: () -> Unit)
    {
        Log.i("NetworkCall","Delete Patients API Called for ${uhid}")

        viewModelScope.launch(CoroutineExceptionHandler {_,ex -> Log.i("NetworkCall",ex.message.toString())}) {
            try {
                var responce = async { RetrofitClient.deletePatients(uhid) }.await()
                if (responce.executed)
                {
                   onResult()
                }

                    withContext(Dispatchers.Main)
                    {
                        Toast.makeText(context, responce.message, Toast.LENGTH_SHORT).show()
                    }


            }
            catch (e:HttpException)
            {
                withContext(Dispatchers.Main) {
                    when (e.code()) {
                        500 -> {
                            Toast.makeText(context, "Server Issue", Toast.LENGTH_SHORT).show()
                        }
                        else -> {
                            Log.i("NetworkCall","1. " + e.localizedMessage)
                            Toast.makeText(context, "Error While Deleting Patients", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }

            catch (e:Exception)
            {
                withContext(Dispatchers.Main)
                {
                    Log.i("NetworkCall","2." + e.localizedMessage)

                    Toast.makeText(context , "Error While Deleting Patients" , Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    fun UpdatePatientQuestions(patient: Patient, onResult: () -> Unit)
    {
        Log.i("NetworkCall","Update Patients API Called ")

        viewModelScope.launch(CoroutineExceptionHandler {_,ex -> Log.i("NetworkCall",ex.message.toString())}) {
            try {
                var requestBody = UpdatePatientsQuestionsRequest(questions = patient.questions!!)
                var responce = async { RetrofitClient.updatePatientsQuestions(patient.uhid,requestBody) }.await()
                if (responce.executed)
                {
                    onResult()
                }

                withContext(Dispatchers.Main)
                {
                    Toast.makeText(context, responce.message, Toast.LENGTH_SHORT).show()
                }


            }
            catch (e:HttpException)
            {
                withContext(Dispatchers.Main) {
                    when (e.code()) {
                        500 -> {
                            Toast.makeText(context, "Server Issue", Toast.LENGTH_SHORT).show()
                        }
                        else -> {
                            Log.i("NetworkCall","1. " + e.localizedMessage)
                            Toast.makeText(context, "Error While Updating Patients", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }

            catch (e:Exception)
            {
                withContext(Dispatchers.Main)
                {
                    Log.i("NetworkCall","2." + e.localizedMessage)

                    Toast.makeText(context , "Error While Updating Patients" , Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    fun updatePatientName(uhid: String, name: String) {
        _all_patients.value = _all_patients.value.map { patient ->
            if (patient.uhid == uhid) {
                patient.copy(name = name) // Use copy to create a new object with the updated name
            } else {
                patient // Return the unmodified patient
            }
        }
    }


    fun updatePatientResponce(uhid: String, questionid: String, option: String, question: String) {
        _all_patients.value = _all_patients.value.map { patient ->
            if (patient.uhid == uhid) {
                val updatedQuestions = patient.questions?.map { question ->
                    if (question.questionUID == questionid) {
                        // Check if the option already exists in the answers
                        val updatedAnswers = if (question.answers.contains(option)) {
                            // Remove the option if it exists
                            question.answers.filter { it != option }
                        } else {
                            // Add the option if it doesn't exist
                            question.answers + option
                        }
                        // Return the updated question with the modified answers
                        question.copy(answers = updatedAnswers)
                    } else {
                        // Return the question as is
                        question
                    }
                }?.toMutableList() ?: mutableListOf()

                // Check if the question doesn't exist, then create and add it
                if (updatedQuestions.none { it.questionUID == questionid }) {
                    updatedQuestions.add(
                        PatientQuestion(
                            questionUID = questionid,
                            question = question, // Add question text here if needed
                            answers = listOf(option)
                        )
                    )
                }

                // Return the updated patient with the modified questions
                patient.copy(questions = updatedQuestions)
            } else {
                // Return the patient as is
                patient
            }
        }
    }




}