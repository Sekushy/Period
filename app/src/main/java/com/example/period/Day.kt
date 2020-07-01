package com.example.period

import com.example.period.informationList.InformationCardDataHolder

class Day {
    var date = ""
    var bleeding = 0
    var mood = 0
    var pain = 0

    override fun equals(other: Any?): Boolean {
        super.equals(other)
        val ot = other as Day
        return this.date.equals(ot.date)
    }
}