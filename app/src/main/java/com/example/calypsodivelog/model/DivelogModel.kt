package com.example.calypsodivelog.model

import android.graphics.Bitmap
import java.util.*

data class DivelogModel(
    /*
    * Clase Model para trabajar con los datos en memoria (Imagenes = Bitmap)
    * */
    var avgDepth: Double = 0.0,
    var buddiesDivingCollection: List<BuddiesDivingResponse> = emptyList(),
    var buddyName: String = "",
    var location: String = "",
    var country: String = "",
    var decoTime: Int = 0,
    var diveLength: Int = 0,
    var divingCenter: String = "",
    var gasConsumption: Double = 0.0,
    var idDivelog: Int = 0,
    var site: String = "",
    var maxDepth: Double = 0.0,
    var notes: String = "",
    var numDive: Int = 0,
    var listPhotos: MutableList<Bitmap> = mutableListOf(),
    var startDateTime: Date = Date(),
    var temperature: Double = 0.0

)