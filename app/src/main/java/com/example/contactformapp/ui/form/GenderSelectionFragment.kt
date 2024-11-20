package com.example.contactformapp.ui.form

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.contactformapp.R
import com.example.contactformapp.databinding.FragmentGenderSelectionBinding
import com.example.contactformapp.ui.MainActivity
import com.example.contactformapp.ui.MainViewModel
import com.example.contactformapp.utils.SnackbarUtil

class GenderSelectionFragment : Fragment() {

    private var _binding: FragmentGenderSelectionBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGenderSelectionBinding.inflate(inflater, container, false)
        val activity = activity as? MainActivity
            ?: throw IllegalStateException("Activity must be MainActivity.")

        viewModel = ViewModelProvider(activity)[MainViewModel::class.java]

        setupObservers()
        setupListeners(activity)

        return binding.root
    }

    private fun setupObservers() {
        viewModel.answerQ1.observe(viewLifecycleOwner) { id_ ->
            val id = id_?.toIntOrNull() ?: -1
            if (id != -1) {
                binding.spinnerGender.setSelection(id - 1)
            }
        }
    }

    private fun setupListeners(activity: MainActivity) {
        binding.btnNextGender.setOnClickListener {
            val selectedPosition = binding.spinnerGender.selectedItemPosition
            if (selectedPosition != -1) {
                viewModel.answerQ1.value = (selectedPosition + 1).toString()
                activity.onGenderSelected()
            } else {
                SnackbarUtil.showSnackbar(binding.root, getString(R.string.select_gender_error))
            }
        }

        binding.btnBack.setOnClickListener {
            activity.onBackPressedDispatcher.onBackPressed()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance() = GenderSelectionFragment()
    }
}
