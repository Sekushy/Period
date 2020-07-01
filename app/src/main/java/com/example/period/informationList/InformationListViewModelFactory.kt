package com.example.period.informationList

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class InformationListViewModelFactory (
    private val application: Application
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(InformationListViewModel::class.java)) {
            return InformationListViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown view model class")
    }
}