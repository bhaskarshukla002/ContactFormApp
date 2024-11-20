// MainViewModel.kt
package com.example.contactformapp.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.contactformapp.data.FormRepository

/**
 * ViewModel class for managing form data and application state.
 */
class MainViewModel(application: Application) : AndroidViewModel(application) {

    // Repository instance for handling data persistence
    private val repository = FormRepository()

    // LiveData properties to manage form state and input values
    val isRecording: MutableLiveData<Boolean> = MutableLiveData(false) // Tracks recording status
    val answerQ1: MutableLiveData<String> = MutableLiveData()   // Stores answer for question 1
    val answerQ2: MutableLiveData<String> = MutableLiveData()   // Stores answer for question 2
    val selfiePath: MutableLiveData<String> = MutableLiveData()    // Path to the captured selfie
    val audioFilePath: MutableLiveData<String> = MutableLiveData()     // Path to the recorded audio file
    val gpsLocation: MutableLiveData<String> = MutableLiveData()    // GPS location in latitude,longitude format
    val submitTimestamp: MutableLiveData<String> = MutableLiveData() // Timestamp of form submission

    /**
     * Saves form data to local storage using the repository.
     */
    fun saveData() {
        repository.saveData(
            answerQ1.value ?: "",
            answerQ2.value ?: "",
            selfiePath.value ?: "",
            audioFilePath.value ?: "",
            gpsLocation.value ?:"",
            getApplication<Application>().applicationContext
        )
    }

    /**
     * Loads form data from local storage and updates LiveData values.
     */
    fun loadData() {
        val jsonData = repository.getData(getApplication<Application>().applicationContext)

        answerQ1.value = jsonData.optString("Q1", "No data")
        answerQ2.value = jsonData.optString("Q2", "No data")
        selfiePath.value = jsonData.optString("Q3", "No data")
        audioFilePath.value = jsonData.optString("recording", "No data")
        gpsLocation.value = jsonData.optString("gps", "No data")
        submitTimestamp.value = jsonData.optString("submit_time", "No data")
    }
}
