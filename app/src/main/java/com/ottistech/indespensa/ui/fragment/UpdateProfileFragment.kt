package com.ottistech.indespensa.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ottistech.indespensa.R
import com.ottistech.indespensa.databinding.FragmentLoginBinding
import com.ottistech.indespensa.databinding.FragmentUpdateProfileBinding
import com.ottistech.indespensa.ui.helpers.FieldValidations
import com.ottistech.indespensa.ui.helpers.FieldVisibilitySwitcher

class UpdateProfileFragment : Fragment() {

    private val TAG = "UPDATE PROFILE FRAGMENT"
    private lateinit var binding: FragmentUpdateProfileBinding
    private lateinit var validator : FieldValidations
    private lateinit var visibilitySwitcher : FieldVisibilitySwitcher

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        validator = FieldValidations(requireContext())
        binding = FragmentUpdateProfileBinding.inflate(inflater, container, false)
        visibilitySwitcher = FieldVisibilitySwitcher(requireContext())

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
}