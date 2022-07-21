package com.example.calypsodivelog.model

import java.util.*

class EquipmentResponse {
    // TODO(): Poner a NULL los campos que aceptan recibir null en la BBDD
    var checkRecommendedDives: Int = 0
    var checkRecommendedHours: Int = 0
    var checkRecommendedMonths: Int = 0
    var datePurchase: Date? = null
    var lastCheckDate: Date? = null
    var idEquipment: Int = 0
    var idUser: Int = 0
    var usedDives: Int = 0
    var usedDivesAfterCheck: Int = 0
    var usedHours: Double = 0.0
    var usedHoursAfterCheck: Double = 0.0
    var name: String = ""
    var type: String = ""

    fun getNextCkeckDate(): Date? {
        var dateNextCheck: Date? = null
        var m = 0
        var y = 0

        if (checkRecommendedMonths > 0) {
            if (lastCheckDate != null) {
                m = lastCheckDate!!.month
                y = lastCheckDate!!.year

                if ((m + checkRecommendedMonths) <= 11) {
                    m += checkRecommendedMonths
                } else {
                    m += (checkRecommendedMonths - 12)
                    y++
                }
                dateNextCheck = Date(y, m, lastCheckDate!!.date)
            } else if (datePurchase != null) {
                m = datePurchase!!.month
                y = datePurchase!!.year

                if ((m + checkRecommendedMonths) <= 11) {
                    m += checkRecommendedMonths
                } else {
                    m += (checkRecommendedMonths - 12)
                    y++
                }
                dateNextCheck = Date(y, m, datePurchase!!.date)
            }
        }

        return dateNextCheck
    }

}