package com.ottistech.indespensa.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.datepicker.MaterialDatePicker
import com.ottistech.indespensa.R
import com.ottistech.indespensa.data.exception.FieldConflictException
import com.ottistech.indespensa.data.repository.UserRepository
import com.ottistech.indespensa.databinding.FragmentSignupBinding
import com.ottistech.indespensa.shared.AppAccountType
import com.ottistech.indespensa.shared.AppConstants
import com.ottistech.indespensa.ui.activity.MainActivity
import com.ottistech.indespensa.ui.helpers.DatePickerCreator
import com.ottistech.indespensa.ui.helpers.FieldValidations
import com.ottistech.indespensa.ui.helpers.FieldVisibilitySwitcher
import com.ottistech.indespensa.ui.helpers.toDate
import com.ottistech.indespensa.webclient.dto.UserCreateDTO
import kotlinx.coroutines.launch

class SignupFragment : Fragment() {

    private val TAG = "SIGNUP FRAGMENT"
    private lateinit var binding : FragmentSignupBinding
    private val args : SignupFragmentArgs by navArgs<SignupFragmentArgs>()
    private lateinit var validator : FieldValidations
    private lateinit var datePicker : MaterialDatePicker<Long>
    private lateinit var visibilitySwitcher : FieldVisibilitySwitcher

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        validator = FieldValidations(requireContext())
        visibilitySwitcher = FieldVisibilitySwitcher(requireContext())
        binding = FragmentSignupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        when(args.signupType) {
            AppAccountType.PERSONAL -> {
                binding.signupFormInputEnterpriseTypeContainer.visibility = View.GONE

                val datePickerCreator = DatePickerCreator()
                datePicker = datePickerCreator.createDatePicker(binding.signupFormInputBirthdate, getString(R.string.form_hint_birthdate), true)
                binding.signupFormInputBirthdate.setOnClickListener {
                    datePicker.show(parentFragmentManager, "DATE PICKER")
                }
            }
            AppAccountType.BUSINESS -> {
               binding.signupFormInputBirthdateContainer.visibility = View.GONE
            }
        }

        var passwordVisibility = false
        binding.signupFormInputPasswordContainer.setEndIconOnClickListener {
            passwordVisibility = visibilitySwitcher.switch(passwordVisibility,
                binding.signupFormInputPassword, binding.signupFormInputPasswordContainer)
        }
        var passwordConfirmationVisibility = false
        binding.signupFormInputPasswordConfirmationContainer.setEndIconOnClickListener {
            passwordConfirmationVisibility = visibilitySwitcher.switch(passwordConfirmationVisibility,
                binding.signupFormInputPasswordConfirmation, binding.signupFormInputPasswordConfirmationContainer)
        }

        val brazilStatesAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, AppConstants.BRAZIL_STATES)
        binding.signupFormInputStateSelect.setAdapter(brazilStatesAdapter)

        binding.signupFormAppTermsLink.setOnClickListener {
            navigateToTermsAndConditions()
        }

        binding.signupFormButton.setOnClickListener {
            if(validForm()) {
                val newUser = generateFormUser()
                lifecycleScope.launch {
                    Log.d(TAG, "Trying to Signup. Called UserRepository.signupUser wit $newUser")
                    try {
                        val result = UserRepository(requireContext()).signupUser(newUser)
                        if(result) {
                            navigateToHome()
                        }
                    } catch (e: FieldConflictException) {
                        validator.setFieldError(
                            binding.signupFormInputEmailContainer, binding.signupFormInputEmailError,
                            getString(R.string.form_error_conflict, "E-mail")
                        )
                    }
                }
            }
        }
    }

    private fun generateFormUser(): UserCreateDTO {
        return UserCreateDTO(
            type = args.signupType,
            name = binding.signupFormInputName.text.toString(),
            enterpriseType = binding.signupFormInputEnterpriseType.text.toString(),
            birthDate = binding.signupFormInputBirthdate.text.toString().toDate(),
            email = binding.signupFormInputEmail.text.toString(),
            password = binding.signupFormInputPassword.text.toString(),
            cep = binding.signupFormInputCep.text.toString(),
            addressNumber = binding.signupFormInputAddressNumber.text.toString().toInt(),
            street = binding.signupFormInputStreet.text.toString(),
            city = binding.signupFormInputCity.text.toString(),
            state = binding.signupFormInputStateSelect.text.toString(),
        )
    }

    private fun validForm() : Boolean {
        // name
        val isNameValid = validator.validMinLength(binding.signupFormInputName, binding.signupFormInputNameContainer, binding.signupFormInputNameError, 4)
        // enterprise type
        val isEnterpriseTypeValid = if(binding.signupFormInputEnterpriseTypeContainer.visibility == View.VISIBLE) {
            validator.validNotNull(binding.signupFormInputEnterpriseType, binding.signupFormInputEnterpriseTypeContainer, binding.signupFormInputEnterpriseTypeError)
        } else {
            true
        }
        // email
        val isEmailValid = validator.validIsEmail(binding.signupFormInputEmail, binding.signupFormInputEmailContainer, binding.signupFormInputEmailError)
        // birthDate
        val isBirthDateValid = if(binding.signupFormInputBirthdateContainer.visibility == View.VISIBLE) {
            validator.validNotNull(binding.signupFormInputBirthdate, binding.signupFormInputBirthdateContainer, binding.signupFormInputBirthdateError)
        } else {
            true
        }
        // password
        val isPasswordValid = validator.validPassword(binding.signupFormInputPassword, binding.signupFormInputPasswordContainer, binding.signupFormInputPasswordError)
        // password confirmation
        val isPasswordConfirmationValid = validator.validConfirmation(binding.signupFormInputPasswordConfirmation, binding.signupFormInputPasswordConfirmationContainer, binding.signupFormInputPasswordConfirmationError, binding.signupFormInputPassword.text.toString())
        // cep
        val isCepValid = validator.validCep(binding.signupFormInputCep, binding.signupFormInputCepContainer, binding.signupFormInputCepError)
        // address number
        val isAddressNumberValid = validator.validMaxLength(binding.signupFormInputAddressNumber, binding.signupFormInputAddressNumberContainer, binding.signupFormInputAddressNumberError, 4) &&
            validator.validNotNull(binding.signupFormInputAddressNumber, binding.signupFormInputAddressNumberContainer, binding.signupFormInputAddressNumberError)
        // street
        val isStreetValid = validator.validNotNull(binding.signupFormInputStreet, binding.signupFormInputStreetContainer, binding.signupFormInputStreetError)
        // city
        val isCityValid = validator.validNotNull(binding.signupFormInputCity, binding.signupFormInputCityContainer, binding.signupFormInputCityError)
        // state
        val isStateValid = validator.validNotNull(binding.signupFormInputStateSelect, binding.signupFormInputStateSelectContainer, binding.signupFormInputStateSelectError)
        // terms
        val isTermsValid = validator.validCheckBox(binding.signupFormAppTermsCheckbox, binding.signupFormAppTermsCheckboxText)

        return isNameValid && isEnterpriseTypeValid && isEmailValid && isBirthDateValid &&
                isPasswordValid && isPasswordConfirmationValid && isCepValid && isAddressNumberValid &&
                isCityValid && isStreetValid && isStateValid && isTermsValid
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
