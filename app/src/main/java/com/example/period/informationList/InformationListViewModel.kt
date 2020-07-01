package com.example.period.informationList

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.period.Day

class InformationListViewModel  (application: Application) : AndroidViewModel (application) {
    var informationList = ArrayList<InformationCardDataHolder>()
    var daysList = ArrayList<Day>()
    init {

    }
}