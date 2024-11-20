package com.example.contactformapp.ui.result

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.contactformapp.databinding.FragmentResultBinding
import com.example.contactformapp.ui.MainActivity
import com.example.contactformapp.ui.MainViewModel

class ResultFragment : Fragment() {

    // Private variable for binding the views in this fragment
    private var _binding: FragmentResultBinding? = null
    private val binding get() = _binding!! // Access the binding object safely

    // Reference to the ViewModel
    private lateinit var viewModel: MainViewModel

    // This method is called when the fragment's view is being created
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment and get access to the views via binding
        _binding = FragmentResultBinding.inflate(inflater, container, false)

        // Initialize ViewModel by getting it from the activity
        viewModel = ViewModelProvider(activity as MainActivity)[MainViewModel::class.java]

        // Observe changes in ViewModel data and update UI accordingly

        // Observing answer for question 1 and updating the corresponding TextView
        viewModel.answerQ1.observe(viewLifecycleOwner) { answer ->
            binding.tvQ1Answer.text = answer
        }

        // Observing answer for question 2 and updating the corresponding TextView
        viewModel.answerQ2.observe(viewLifecycleOwner) { answer ->
            binding.tvQ2Answer.text = answer
        }

        // Observing the selfie path and displaying it in the TextView
        viewModel.selfiePath.observe(viewLifecycleOwner) { path ->
            binding.tvQ3ImagePath.text = path
        }

        // Observing the audio file path and displaying it in the TextView
        viewModel.audioFilePath.observe(viewLifecycleOwner) { path ->
            binding.tvRecordingPath.text = path
        }

        // Observing GPS location and displaying it in the TextView
        viewModel.gpsLocation.observe(viewLifecycleOwner) { location ->
            binding.tvGpsCoordinates.text = location
        }

        // Observing the timestamp of submission and displaying it in the TextView
        viewModel.submitTimestamp.observe(viewLifecycleOwner) { timestamp ->
            binding.tvSubmitTime.text = timestamp
        }

        // Setting up the back button click listener to navigate back
        binding.btnBack.setOnClickListener {
            // Using safe cast to access the activity and call onBackPressed
            (activity as? MainActivity)?.onBackPressedDispatcher?.onBackPressed()
        }

        // Returning the root view of this fragment
        return binding.root
    }

    // This method is called when the fragment's view is being destroyed
    override fun onDestroyView() {
        super.onDestroyView()
        // Nullify the binding object to prevent memory leaks
        _binding = null
    }

    // Companion object to create a new instance of the fragment
    companion object {
        @JvmStatic
        fun newInstance() = ResultFragment() // Return a new instance of ResultFragment
    }
}
