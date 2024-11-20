package com.example.contactformapp.utils

import android.view.View
import com.google.android.material.snackbar.Snackbar

/**
 * Utility object for showing Snackbar messages throughout the app.
 */
object SnackbarUtil {

    /**
     * Displays a Snackbar with the specified message and duration.
     *
     * @param view The view to find a parent from, typically the root view of the current layout.
     * @param message The message to display in the Snackbar.
     * @param duration Duration for which the Snackbar should be shown. Defaults to [Snackbar.LENGTH_SHORT].
     */
    fun showSnackbar(view: View, message: String, duration: Int = Snackbar.LENGTH_SHORT) {
        Snackbar.make(view, message, duration).show()
    }
}
