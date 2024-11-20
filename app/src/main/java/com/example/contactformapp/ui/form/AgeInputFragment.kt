package com.example.contactformapp.ui.form

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.contactformapp.R
import com.example.contactformapp.databinding.FragmentAgeInputBinding
import com.example.contactformapp.ui.MainActivity
import com.example.contactformapp.ui.MainViewModel
import com.example.contactformapp.utils.SnackbarUtil

class AgeInputFragment : Fragment() {

    private var _binding: FragmentAgeInputBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAgeInputBinding.inflate(inflater, container, false)
        val activity = activity as? MainActivity
            ?: throw IllegalStateException("Activity must be MainActivity.")

        viewModel = ViewModelProvider(activity)[MainViewModel::class.java]

        setupObservers()
        setupListeners(activity)

        return binding.root
    }

    private fun setupObservers() {
        viewModel.answerQ2.observe(viewLifecycleOwner) { age ->
            if (!age.isNullOrEmpty() && validateAge(age)) {
                if (binding.etAge.text.toString() != age) {
                    binding.etAge.setText(age)
                }
            }
        }
    }

    private fun setupListeners(activity: MainActivity) {
        binding.btnNextAge.setOnClickListener {
            val age = binding.etAge.text.toString()
            if (validateAge(age)) {
                viewModel.answerQ2.value = age
                activity.onAgeSelected()
            } else {
                SnackbarUtil.showSnackbar(binding.root, getString(R.string.invalid_age_error))
            }
        }

        binding.btnPrevAge.setOnClickListener {
            activity.onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun validateAge(age: String): Boolean {
        val ageInt = age.toIntOrNull()
        return ageInt != null && ageInt in 0..120
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance() = AgeInputFragment()
    }
}
