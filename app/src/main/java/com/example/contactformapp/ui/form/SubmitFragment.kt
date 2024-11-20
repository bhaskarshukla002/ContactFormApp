package com.example.contactformapp.ui.form

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.contactformapp.databinding.FragmentSubmitBinding
import com.example.contactformapp.ui.MainActivity
import com.example.contactformapp.utils.SnackbarUtil

class SubmitFragment : Fragment() {

    private var _binding: FragmentSubmitBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSubmitBinding.inflate(inflater, container, false)

        binding.btnSubmit.setOnClickListener {
            (activity as? MainActivity)?.onSubmitted()
        }

        binding.btnBack.setOnClickListener {
            SnackbarUtil.showSnackbar(binding.root, "Submitted successfully.")
            (activity as? MainActivity)?.onBackPressedDispatcher?.onBackPressed()
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Clean up the binding when the view is destroyed
    }

    companion object {
        @JvmStatic
        fun newInstance() = SubmitFragment()
    }
}
