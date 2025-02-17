package com.mayank.vamanaappversion2.Backend

import com.google.gson.annotations.SerializedName
import com.mayank.vamanaappversion2.Modals.Patient
import com.mayank.vamanaappversion2.Modals.PatientQuestion
import com.mayank.vamanaappversion2.Modals.Question
import com.mayank.vamanaappversion2.Modals.QuestionDetail
import com.mayank.vamanaappversion2.Modals.User


data class GeneralResponce(

    @SerializedName("message") val message : String ,
    @SerializedName("executed") val executed : Boolean

)

data class SignInResponce(

    @SerializedName("message") val message : String ,
    @SerializedName("executed") val executed : Boolean,
    @SerializedName("user") val user : User

)

data class SignInRequest(

    @SerializedName("userID") val userID: String,
    @SerializedName("password") val password: String,
    @SerializedName("role") val role: String

)

data class SignUpRequest(

    @SerializedName("userID") val userID: String,
    @SerializedName("contact") val contact : String,
    @SerializedName("password") val password : String,
    @SerializedName("role") val role : String,
    @SerializedName("powers") val powers : List<String>,
    @SerializedName("instituteID") val instituteID : String,
)
data class GetAllUserResponce(

    @SerializedName("message") val message : String ,
    @SerializedName("executed") val executed : Boolean,
    @SerializedName("users") val users : List<User>

)


data class GetUserByIDResponce(

    @SerializedName("message") val message : String ,
    @SerializedName("executed") val executed : Boolean,
    @SerializedName("user") val user : User

)


data class GetQuestionsResponce (
    @SerializedName("message") val message: String,
    @SerializedName("executed") val executed: Boolean,
    @SerializedName("questions") val questions: List<Question>,
)


data class GetPatientsResponce (
    @SerializedName("message") val message: String,
    @SerializedName("executed") val executed: Boolean,
    @SerializedName("patients") val patients: List<Patient>,
)

data class UpdatePatientsRequest (
    @SerializedName("update") val update: Patient
)


data class UpdatePatientsQuestionsRequest (
    @SerializedName("questions") val questions: List<PatientQuestion>
)

data class CategoryRequest(
    @SerializedName("category") val category: String
)



data class GetAnalysisQuestionsResponce (
    @SerializedName("message") val message: String,
    @SerializedName("executed") val executed: Boolean,
    @SerializedName("questions") val questions: List<QuestionDetail>,
)


data class OverAllAnalysisResponse(
    @SerializedName("message") val message: String,
    @SerializedName("executed") val executed: Boolean,
    @SerializedName("questions") val questions: List<QuestionAnalysis>
)

data class QuestionAnalysis(
    @SerializedName("question") val question: String,
    @SerializedName("options") val options: List<OptionAnalysis>
)

data class OptionAnalysis(
    @SerializedName("option") val option: String,
    @SerializedName("value") val value: String
)

