package com.example.period.informationList

class InformationCardDataHolder () {
    var imageResource : Int = 0
    var titleResource : String = ""
    var descriptionResource : String = ""
    var startDate = String()
    var endDate = String()
    var durationOfCycle = 0

    constructor(startDate : String, endDate : String, durationOfCycle : Int, titleResource : String, descriptionResource : String, imageResource : Int): this() {
        this.startDate = startDate
        this.endDate = endDate
        this.durationOfCycle = durationOfCycle
        this.titleResource = titleResource
        this.descriptionResource = descriptionResource
        this.imageResource = imageResource
    }

    override fun equals(other: Any?): Boolean {
        super.equals(other)
        val ot = other as InformationCardDataHolder
        return this.startDate.equals(ot.startDate) && this.endDate.equals(ot.endDate)
    }
}