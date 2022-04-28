package com.example.calypsodivelog.model

import java.util.*

class DivelogFullResponse() {
    /*
    * Clase Model para trabajar con la API (Imagenes == Base64)
    * */
    var idDivelog: Int = 0
    var idUser: Int = 0
    var avgDepth: Double = 0.0
    var buddyName: String = ""
    var location: String = ""
    var country: String = ""
    var decoTime: Int = 0
    var diveLength: Int = 0
    var divingCenter: String = ""
    var gasConsumption: Double = 0.0
    var site: String = ""
    var maxDepth: Double = 0.0
    var notes: String = ""
    var numDive: Int = 0
    var photo1: String = ""
    var photo2: String = ""
    var photo3: String = ""
    var photo4: String = ""
    var startDateTime: Date = Date()
    var temperature: Double = 0.0
    var buddiesDivingCollection: List<BuddiesDivingResponse> = emptyList()
}