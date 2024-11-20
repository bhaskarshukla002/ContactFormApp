package com.example.contactformapp.data

import android.content.Context
import android.text.format.DateFormat
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.util.Date

/**
 * Repository class for handling data persistence for the contact form.
 */
class FormRepository {

    /**
     * Saves form data as a JSON object into a file named `user.txt` in external storage.
     *
     * @param question1 Answer to the first question.
     * @param question2 Answer to the second question.
     * @param selfiePath Path to the selfie file.
     * @param recordingPath Path to the audio recording file.
     * @param gpsCoordinates GPS coordinates as a string (latitude,longitude).
     * @param context Application context for accessing external storage directory.
     */
    fun saveData(
        question1: String,
        question2: String,
        selfiePath: String,
        recordingPath: String,
        gpsCoordinates: String,
        context: Context
    ) {
        try {
            // Create a JSON object with the provided data
            val jsonObject = JSONObject().apply {
                put("Q1", question1)
                put("Q2", question2)
                put("Q3", selfiePath)
                put("recording", recordingPath)
                put("gps", gpsCoordinates)
                put("submit_time", getCurrentTimestamp())
            }

            // Define the file path for saving data
            val filePath = "${context.getExternalFilesDir(null)?.absolutePath}"
            val file = File(filePath, "user.txt")

            // Write JSON content to the file
            file.writeText(jsonObject.toString())
        } catch (e: JSONException) {
            e.printStackTrace() // Handle JSON-specific exceptions
        } catch (e: IOException) {
            e.printStackTrace() // Handle file I/O exceptions
        }
    }

    /**
     * Retrieves form data from the `user.txt` file as a JSON object.
     *
     * @param context Application context for accessing external storage directory.
     * @return A [JSONObject] containing the saved form data, or an empty JSON object if the file doesn't exist.
     */
    fun getData(context: Context): JSONObject {
        val filePath = "${context.getExternalFilesDir(null)?.absolutePath}"
        val file = File(filePath, "user.txt")

        return if (file.exists()) {
            // Read the file content and parse it into a JSONObject
            JSONObject(file.readText())
        } else {
            // Return an empty JSONObject if the file doesn't exist
            JSONObject()
        }
    }

    /**
     * Generates the current timestamp in the format `yyyy-MM-dd HH:mm:ss`.
     *
     * @return A string representing the current timestamp.
     */
    private fun getCurrentTimestamp(): String {
        val currentTime = Date()
        return DateFormat.format("yyyy-MM-dd HH:mm:ss", currentTime).toString()
    }
}
