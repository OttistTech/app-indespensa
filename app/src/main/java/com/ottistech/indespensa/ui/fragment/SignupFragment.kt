package com.ottistech.indespensa.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.datepicker.MaterialDatePicker
import com.ottistech.indespensa.R
import com.ottistech.indespensa.data.repository.UserRepository
import com.ottistech.indespensa.databinding.FragmentSignupBinding
import com.ottistech.indespensa.shared.AppAccountType
import com.ottistech.indespensa.shared.AppConstants
import com.ottistech.indespensa.ui.activity.MainActivity
import com.ottistech.indespensa.ui.dialog.DatePickerCreator
import com.ottistech.indespensa.ui.helpers.FieldVisibilitySwitcher
import com.ottistech.indespensa.ui.helpers.showToast
import com.ottistech.indespensa.ui.model.feedback.Feedback
import com.ottistech.indespensa.ui.model.feedback.FeedbackCode
import com.ottistech.indespensa.ui.viewmodel.SignupViewModel

class SignupFragment : Fragment() {

    private val args: SignupFragmentArgs by navArgs<SignupFragmentArgs>()

    private lateinit var binding: FragmentSignupBinding
    private lateinit var viewModel: SignupViewModel

    private lateinit var datePicker: MaterialDatePicker<Long>
    private lateinit var visibilitySwitcher: FieldVisibilitySwitcher


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        visibilitySwitcher = FieldVisibilitySwitcher(requireContext())
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_signup, container, false)
        viewModel = SignupViewModel(
            args.signupType,
            UserRepository(requireContext())
        )
        binding.model = viewModel
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        handleFormAccountType()
        setupStatesSelect()
        setupTermsLink()
        setupObservers()
        setupValidationListeners()
        setupBackButton()

        var passwordVisibility = false
        binding.signupPasswordLayout.setEndIconOnClickListener {
            passwordVisibility = visibilitySwitcher.switch(passwordVisibility, binding.signupPasswordField, binding.signupPasswordLayout)
        }

        var passwordConfirmationVisibility = false
        binding.signupPasswordConfirmationLayout.setEndIconOnClickListener {
            passwordConfirmationVisibility = visibilitySwitcher.switch(passwordConfirmationVisibility, binding.signupPasswordConfirmationField, binding.signupPasswordConfirmationLayout)
        }
    }

    private fun setupBackButton() {
        binding.signupBack.setOnClickListener {
            popBackStack()
        }
    }

    private fun popBackStack() {
        findNavController().popBackStack(R.id.signup_dest, true)
    }

    private fun setupTermsLink() {
        binding.signupTermsLink.setOnClickListener {
            navigateToTermsAndConditions()
        }
    }

    private fun setupStatesSelect() {
        val brazilStatesAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, AppConstants.BRAZIL_STATES)
        binding.signupStatesSelectField.setAdapter(brazilStatesAdapter)
    }

    private fun handleFormAccountType() {
        when(args.signupType) {
            AppAccountType.PERSONAL -> {
                binding.signupEnterpriseTypeLayout.visibility = View.GONE
                val datePickerCreator = DatePickerCreator()
                datePicker = datePickerCreator.createDatePicker(binding.signupBirthdateField, getString(R.string.form_hint_birthdate), true)
                binding.signupBirthdateField.setOnClickListener {
                    if (!datePicker.isAdded) {
                        datePicker.show(parentFragmentManager, "DATE PICKER")
                    }
                }
            }

            AppAccountType.BUSINESS -> {
                binding.signupBirthdateLayout.visibility = View.GONE
            }

            else -> throw Exception("Not allowed account type")
        }
    }

    private fun setupObservers() {
        viewModel.feedback.observe(viewLifecycleOwner) { feedback ->
            feedback?.let {
                handleFeedback(it)
            }
        }
    }

    private fun handleFeedback(feedback: Feedback) {
        showToast(feedback.message)
        if(feedback.code == FeedbackCode.SUCCESS) {
            navigateToHome()
        }
    }

    private fun setupValidationListeners() {
        binding.signupNameField.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) { viewModel.validName() }
        }
        binding.signupEnterpriseTypeField.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) { viewModel.validEnterpriseType() }
        }
        binding.signupEmailField.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) { viewModel.validEmail() }
        }
        binding.signupBirthdateField.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) { viewModel.validBirthdate() }
        }
        binding.signupPasswordField.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) { viewModel.validPassword() }
        }
        binding.signupPasswordConfirmationLayout.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) { viewModel.validPasswordConfirmation() }
        }
        binding.signupCepField.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) { viewModel.validCep() }
        }
        binding.signupAddressNumberField.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) { viewModel.validAddressNumber() }
        }
        binding.signupStreetField.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) { viewModel.validStreet() }
        }
        binding.signupCityField.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) { viewModel.validCity() }
        }
        binding.signupStatesSelectField.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) { viewModel.validState() }
        }
        binding.signupTermsCheckbox.setOnCheckedChangeListener { _, _ ->
            viewModel.validTermsCheck()
        }
    }

    private fun navigateToTermsAndConditions() {
        val action = SignupFragmentDirections.actionSignupToTerms()
        findNavController().navigate(action)
    }

    private fun navigateToHome() {
        val currentActivity = requireActivity()
        val intent = Intent(currentActivity, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        currentActivity.startActivity(intent)
        currentActivity.finish()
    }
}
