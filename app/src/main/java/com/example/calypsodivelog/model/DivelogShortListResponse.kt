package com.example.calypsodivelog.model

import java.util.*

class DivelogShortListResponse(){
    // TODO: ¿Convertir en data class?
    var avgDepth: Double = 0.0
    var buddiesDivingCollection: List<BuddiesDivingResponse> = emptyList()
    var buddyName: String = ""
    var city: String = ""
    var diveLength: Int = 0
    var idDivelog: Int = 0
    var location: String = ""
    var maxDepth: Double = 0.0
    var numDive: Int = 0
    var startDateTime: Date = Date()
    var temperature: Double = 0.0
}