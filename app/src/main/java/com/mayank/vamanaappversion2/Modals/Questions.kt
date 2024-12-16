package com.mayank.vamanaappversion2.Modals

import com.google.gson.annotations.SerializedName

data class Question(
    @SerializedName("_id")
    val id: String,

    @SerializedName("category")
    var category: String,

    @SerializedName("questions")
    val questions: List<QuestionDetail>
)

data class QuestionDetail(
    @SerializedName("_id")
    val id: String? = "",

    @SerializedName("question")
    var question: String,

    @SerializedName("options")
    var options: List<String>
)
