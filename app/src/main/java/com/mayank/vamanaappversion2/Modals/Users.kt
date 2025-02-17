package com.mayank.vamanaappversion2.Modals
import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("userID")
    val userID: String,

    @SerializedName("contact")
    val contact: String,

    @SerializedName("password")
    val password: String,

    @SerializedName("role")
    val role: String,

    @SerializedName("powers")
    val powers: List<String> = emptyList(),

    @SerializedName("instituteID")
    val instituteID : String ,

    @SerializedName("createdAt")
    val createdAt: String? = null,

    @SerializedName("updatedAt")
    val updatedAt: String? = null
)

enum class Role {
    @SerializedName("Admin")
    ADMIN,

    @SerializedName("Staff")
    STAFF
}

