package com.example.calypsodivelog.model

import java.util.*

class DateTimeModel(
    var day:Int = Calendar.getInstance().get(Calendar.DAY_OF_MONTH),
    var month:Int = Calendar.getInstance().get(Calendar.MONTH)+1,
    var year:Int = Calendar.getInstance().get(Calendar.YEAR),
    var hour:Int = 0,
    var minute:Int = 0
)