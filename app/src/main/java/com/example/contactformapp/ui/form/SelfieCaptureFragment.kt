package com.example.contactformapp.ui.form

import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.contactformapp.R
import com.example.contactformapp.databinding.FragmentSelfieCaptureBinding
import com.example.contactformapp.ui.MainActivity
import com.example.contactformapp.ui.MainViewModel
import com.example.contactformapp.utils.SnackbarUtil
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SelfieCaptureFragment : Fragment() {

    // Constant for the image capture request code
    private val IMAGE_CAPTURE_CODE = 1002

    // Binding variable for the fragment's views
    private var _binding: FragmentSelfieCaptureBinding? = null
    private val binding get() = _binding!!

    // ViewModel to observe selfie path and other data
    private lateinit var viewModel: MainViewModel

    // onCreateView is called when the fragment's view is being created
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment and get a reference to the root view
        _binding = FragmentSelfieCaptureBinding.inflate(inflater, container, false)
        val view = binding.root

        // Set an onClickListener on the capture selfie button
        binding.btnCaptureSelfie.setOnClickListener {
            // Create an intent to open the camera and capture an image
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent, IMAGE_CAPTURE_CODE)
        }

        // Initialize the ViewModel to observe data from the MainActivity
        viewModel = ViewModelProvider(activity as MainActivity)[MainViewModel::class.java]

        // Observe the selfie path LiveData to display the captured image in the preview
        viewModel.selfiePath.observe(viewLifecycleOwner) { path ->
            path?.let {
                // Decode the image from the file path and display it in the ImageView
                val bitmap = BitmapFactory.decodeFile(it)
                binding.imgPhotoPreview.setImageBitmap(bitmap)
            }
        }

        // Set an onClickListener for the Next button to proceed if a selfie is captured
        binding.btnNextPhoto.setOnClickListener {
            if (viewModel.selfiePath.value != null) {
                // Notify the MainActivity that the selfie was captured and proceed
                (activity as MainActivity).onSelfieCaptured()
            } else {
                // Show a snack bar message if no selfie has been captured yet
                SnackbarUtil.showSnackbar(binding.root, getString(R.string.capture_selfie_message))
            }
        }

        // Set an onClickListener for the back button to navigate back
        binding.btnBack.setOnClickListener {
            (activity as MainActivity).onBackPressedDispatcher.onBackPressed()
        }

        // Return the root view for the fragment
        return view
    }

    // onActivityResult handles the result from the camera activity
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_CAPTURE_CODE) {
            when (resultCode) {
                // If the image was successfully captured, process the image
                RESULT_OK -> {
                    // Get the bitmap from the intent's extras
                    val bitmap = data?.extras?.get("data") as? Bitmap
                    bitmap?.let {
                        // Save the captured image to storage
                        val filePath = saveImageToAppStorage(it)
                        filePath?.let { path ->
                            // Update the ViewModel with the saved image path
                            viewModel.selfiePath.value = path
                            // Set the captured image in the preview ImageView
                            binding.imgPhotoPreview.setImageBitmap(it)
                            // Show a Toast with the path where the image is saved
                            Toast.makeText(context, getString(R.string.image_saved_message, path), Toast.LENGTH_SHORT).show()
                        } ?: run {
                            // Show a Toast if saving the image failed
                            Toast.makeText(context, getString(R.string.failed_to_save_image), Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                // If the capture was canceled, show a Toast message
                RESULT_CANCELED -> {
                    Toast.makeText(context, getString(R.string.capture_cancelled), Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    // saveImageToAppStorage saves the bitmap image to the app's storage and returns the file path
    private fun saveImageToAppStorage(bitmap: Bitmap): String? {
        return try {
            // Create a timestamped file name for the image
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val fileName = "IMG_$timestamp.jpg"
            // Get the app's external storage directory
            val filePath = context?.getExternalFilesDir(null)?.absolutePath

            // Create a File object in the specified directory
            val file = File(filePath, fileName)

            // Save the bitmap to the file as a JPEG image
            file.outputStream().use { fos ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
            }

            // Return the absolute file path
            file.absolutePath
        } catch (e: IOException) {
            // Print the error stack trace if saving the image fails
            e.printStackTrace()
            null
        }
    }

    // Companion object to create a new instance of SelfieCaptureFragment
    companion object {
        @JvmStatic
        fun newInstance() = SelfieCaptureFragment()
    }
}
