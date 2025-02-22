package com.mayank.vamanaappversion2

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

object Constants {
    val PrimaryColor = Color(0xffBCFFC2)
    val SecondaryColor = Color(0xff018749)
    val TertiaryColor = Color(0xff1CAC78)
    val PrimaryColor_second = Color(0xff4FFFB0)

    val BlueButtonColor = Color(0xFF00668B)

    val backgroundGradient = Brush.verticalGradient(colors = listOf(PrimaryColor_second,PrimaryColor,PrimaryColor_second))


    val AdminID = "Master"
    val AdminPassword = "Slave"
}