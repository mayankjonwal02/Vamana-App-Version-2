package com.mayank.vamanaappversion2.Backend

import com.google.gson.annotations.SerializedName
import com.mayank.vamanaappversion2.Modals.Patient
import com.mayank.vamanaappversion2.Modals.Question
import com.mayank.vamanaappversion2.Modals.QuestionDetail
import com.mayank.vamanaappversion2.Modals.User
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import java.util.Locale.Category

interface API_Service {

    @GET("test")
    suspend fun testConnection() : GeneralResponce

    @POST("user/create")
    suspend fun createUser(@Body request : SignUpRequest) : GeneralResponce

    @POST("user/signin")
    suspend fun loginUser(@Body request : SignInRequest) : GeneralResponce

    @GET("user/all")
    suspend fun fetchAllUsers() : GetAllUserResponce

    @GET("user/{id}")
    suspend fun getUserByID(@Path("id") id : String) : GetUserByIDResponce

    @PUT("user/update/{id}")
    suspend fun updateUserByID(@Path("id") id : String,@Body request: User) : GeneralResponce

    @DELETE("user/delete/{id}")
    suspend fun deleteUserByID(@Path("id") id : String) : GeneralResponce



    @GET("questions/questions")
    suspend fun getAllQuestions() : GetQuestionsResponce


    @POST("questions/categories")
    suspend fun createCategory(@Body category: CategoryRequest) : GeneralResponce

    @PUT("questions/categories/{categoryUID}")
    suspend fun updateCategory(@Path("categoryUID") UID : String , @Body category : CategoryRequest) : GeneralResponce

    @DELETE("questions/categories/{categoryUID}")
    suspend fun deleteCategory(@Path("categoryUID") UID : String ) : GeneralResponce


    @POST("questions/categories/{categoryUID}/questions")
    suspend fun addQuestion(@Path("categoryUID") UID : String , @Body question: QuestionDetail ) : GeneralResponce


    @PUT("questions/categories/{categoryUID}/questions/{questionUID}")
    suspend fun updateQuestions(@Path("categoryUID") categoryUID : String, @Path("questionUID") questionUID : String  , @Body question: QuestionDetail ) : GeneralResponce

    @DELETE("questions/categories/{categoryUID}/questions/{questionUID}")
    suspend fun deleteQuestions(@Path("categoryUID") categoryUID : String, @Path("questionUID") questionUID : String   ) : GeneralResponce


    @POST("patient/patients")
    suspend fun createPatient(@Body patient: Patient) : GeneralResponce

    @GET("patient/patients")
    suspend fun getPatients() : GetPatientsResponce


    @PUT("patient/patients/{uhid}")
    suspend fun updatePatients(@Path("uhid") uhid : String, @Body update: UpdatePatientsRequest) : GeneralResponce

    @PUT("patient/patients/{uhid}/questions")
    suspend fun updatePatientsQuestions(@Path("uhid") uhid : String, @Body questions: UpdatePatientsQuestionsRequest) : GeneralResponce
    @DELETE("patient/patients/{uhid}")
    suspend fun deletePatients(@Path("uhid") uhid : String) : GeneralResponce



    @GET("analysis/analysisquestions")
    suspend fun getAnalysisQuestions() : GetAnalysisQuestionsResponce

    @POST("analysis/analysisQuestions")
    suspend fun createAnalysisQuestion(@Body question: QuestionDetail) : GeneralResponce
    @PUT("analysis/analysisQuestions/{id}")
    suspend fun updateAnalysisQuestion(@Path("id") id : String, @Body update: QuestionDetail) : GeneralResponce

    @DELETE("analysis/analysisQuestions/{id}")
    suspend fun deleteAnalysisQuestion(@Path("id") id : String) : GeneralResponce


    @GET("analysis/analysisStatistics")
    suspend fun getOverallAnalysis() : OverAllAnalysisResponse

}