package com.example.contactformapp.ui

import android.Manifest.permission.*
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.contactformapp.databinding.ActivityMainBinding
import com.example.contactformapp.ui.form.*
import com.example.contactformapp.ui.result.ResultFragment
import com.example.contactformapp.utils.AudioRecorder
import com.example.contactformapp.utils.SnackbarUtil
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    // View binding for accessing UI elements
    private lateinit var binding: ActivityMainBinding

    // Audio recorder for recording audio input
    private lateinit var audioRecorder: AudioRecorder

    // Fragments used in the application flow
    private lateinit var startFragment: Fragment
    private lateinit var genderSelectionFragment: Fragment
    private lateinit var ageInputFragment: Fragment
    private lateinit var selfieCaptureFragment: Fragment
    private lateinit var submitFragment: Fragment
    private lateinit var resultFragment: Fragment

    // ViewModel for managing UI-related data
    private lateinit var viewModel: MainViewModel

    // Location client for accessing current location
    private val locationClient by lazy { LocationServices.getFusedLocationProviderClient(this) }

    companion object {
        // Required permissions for the application
        private val REQUIRED_PERMISSIONS = arrayOf(
            RECORD_AUDIO,
            CAMERA,
            ACCESS_FINE_LOCATION,
            ACCESS_COARSE_LOCATION
        )
    }

    // Permission launcher for handling runtime permission requests
    private val requestPermissionsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.all { it.value }) {
            // Proceed if all permissions are granted
            onStartClicked()
        } else {
            // Show error if permissions are denied
            SnackbarUtil.showSnackbar(
                binding.root,
                "All permissions (Camera, Audio, Location) are required to proceed",
                Snackbar.LENGTH_LONG
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Set up view binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Adjust padding to avoid overlapping with system bars
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize ViewModel and AudioRecorder
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        val audioFilePath = "${getExternalFilesDir(null)?.absolutePath}/recording.wav"
        audioRecorder = AudioRecorder(audioFilePath)

        // Initialize fragments
        initializeFragments()

        // Load the start fragment on the first launch
        if (savedInstanceState == null) {
            loadFragment(startFragment)
        }
    }

    /**
     * Initialize all fragments used in the application.
     */
    private fun initializeFragments() {
        startFragment = StartFragment()
        genderSelectionFragment = GenderSelectionFragment()
        ageInputFragment = AgeInputFragment()
        selfieCaptureFragment = SelfieCaptureFragment()
        submitFragment = SubmitFragment()
        resultFragment = ResultFragment()
    }

    /**
     * Check if all required permissions are granted.
     */
    private fun hasAllPermissions(): Boolean {
        return REQUIRED_PERMISSIONS.all {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    /**
     * Get the current location and execute the callback with latitude and longitude.
     */
    private fun getCurrentLocation(onLocationReceived: (latitude: Double, longitude: Double) -> Unit) {
        if (ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        ) {
            locationClient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let {
                    onLocationReceived(it.latitude, it.longitude)
                }
            }
        }
    }

    /**
     * Load a fragment into the container.
     */
    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(binding.container.id, fragment)
            .addToBackStack(null)
            .commit()
    }

    /**
     * Request all necessary permissions.
     */
    private fun requestAllPermissions() {
        requestPermissionsLauncher.launch(REQUIRED_PERMISSIONS)
    }

    /**
     * Handle the "Start" button click event.
     */
    fun onStartClicked() {
        if (hasAllPermissions()) {
            // Start audio recording if not already recording
            if (!viewModel.isRecording.value!!) {
                audioRecorder.startRecording()
                viewModel.isRecording.value = true
            }
            // Load the next fragment
            loadFragment(genderSelectionFragment)
        } else {
            requestAllPermissions()
        }
    }

    // Navigation callbacks for user interactions
    fun onGenderSelected() {
        loadFragment(ageInputFragment)
    }

    fun onAgeSelected() {
        loadFragment(selfieCaptureFragment)
    }

    fun onSelfieCaptured() {
        loadFragment(submitFragment)
    }

    /**
     * Handle form submission.
     */
    fun onSubmitted() {
        val audioPath = audioRecorder.stopRecording()
        viewModel.isRecording.value = false
        viewModel.audioFilePath.value = audioPath

        // Get current location and save form data
        getCurrentLocation { latitude, longitude ->
            viewModel.gpsLocation.value = "$latitude,$longitude"
            viewModel.saveData()
            viewModel.loadData()
            loadFragment(resultFragment)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Clean up resources, if any
    }

    override fun onStop() {
        super.onStop()
        // Stop recording if necessary
        // audioRecorder.stopRecording()
    }
}
