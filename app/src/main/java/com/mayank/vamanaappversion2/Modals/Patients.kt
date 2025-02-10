package com.mayank.vamanaappversion2.Modals

import java.util.Date

import com.google.gson.annotations.SerializedName

data class Patient(
    @SerializedName("_id")
    val id: String? = null,

    @SerializedName("name")
    var name: String,

    @SerializedName("uhid")
    val uhid: String,

    @SerializedName("age")
    val age: Int,

    @SerializedName("occupation")
    val occupation: String? = null,

    @SerializedName("past_illness")
    val pastIllness: String? = null,

    @SerializedName("address")
    val address: String? = null,

    @SerializedName("medicine_history")
    val medicineHistory: String? = null,

    @SerializedName("date_of_admission")
    val dateOfAdmission: String,

    @SerializedName("date_of_vamana")
    val dateOfVamana: String? = null,

    @SerializedName("prakriti")
    val prakriti: String? = null,

    @SerializedName("questions")
    var questions: List<PatientQuestion>? = null,

    @SerializedName("Analysis")
    var Analysis: List<PatientQuestion>? = null,

    @SerializedName("createdAt")
    val createdAt: String? = null,

    @SerializedName("updatedAt")
    val updatedAt: String? = null,

    @SerializedName("SnehaPana")
    val SnehaPana: List<SnehaPanaItem>? = null,


    @SerializedName("results")
    val results: PatientResult? = null,
)

data class SnehaPanaItem(
    @SerializedName("day")
    val day: Number,

    @SerializedName("dose")
    val dose: Number,

    @SerializedName("digestivehours")
    val digestiveHours : Number
)

data class PatientQuestion(
    @SerializedName("question_uid")
    val questionUID: String,

    @SerializedName("question")
    val question: String,

    @SerializedName("answers")
    var answers: List<String> = emptyList()
)

data class PatientResult(

    @SerializedName("antiki_shuddhi")
    val antiki_shuddhi: List<String>,

    @SerializedName("vaigiki_shuddhi")
    val vaigiki_shuddhi: List<String>,

    @SerializedName("laingiki_shuddhi")
    val laingiki_shuddhi: List<String>,

    @SerializedName("maniki_shuddhi")
    val maniki_shuddhi: List<String>,

)
