package com.ottistech.indespensa.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.ottistech.indespensa.R
import com.ottistech.indespensa.data.repository.UserRepository
import com.ottistech.indespensa.databinding.FragmentUpdateProfileBinding
import com.ottistech.indespensa.shared.AppAccountType
import com.ottistech.indespensa.shared.AppConstants
import com.ottistech.indespensa.ui.activity.AuthActivity
import com.ottistech.indespensa.ui.dialog.ConfirmationDialogCreator
import com.ottistech.indespensa.ui.helpers.FieldValidations
import com.ottistech.indespensa.ui.helpers.FieldVisibilitySwitcher
import com.ottistech.indespensa.ui.helpers.showToast
import com.ottistech.indespensa.ui.model.FieldError
import com.ottistech.indespensa.ui.viewmodel.UpdateProfileViewModel
import com.ottistech.indespensa.webclient.dto.user.UserFullIDTO
import com.ottistech.indespensa.webclient.dto.user.UserUpdateDTO

class UpdateProfileFragment : Fragment() {

    private lateinit var binding: FragmentUpdateProfileBinding
    private lateinit var validator : FieldValidations
    private lateinit var visibilitySwitcher : FieldVisibilitySwitcher
    private lateinit var dialogCreator : ConfirmationDialogCreator
    private lateinit var viewModel: UpdateProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUpdateProfileBinding.inflate(inflater, container, false)
        validator = FieldValidations(requireContext())
        visibilitySwitcher = FieldVisibilitySwitcher(requireContext())
        dialogCreator = ConfirmationDialogCreator(requireContext())
        viewModel = UpdateProfileViewModel(
            UserRepository(requireContext())
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObservers()
        viewModel.fetchUserInfo()

        if(viewModel.getUserType() != AppAccountType.BUSINESS) {
            binding.updateProfileFormInputEnterpriseTypeContainer.visibility = View.GONE
        }

        var passwordVisibility = false
        binding.updateProfileFormInputPasswordContainer.setEndIconOnClickListener {
            passwordVisibility = visibilitySwitcher.switch(passwordVisibility,
                binding.updateProfileFormInputPassword, binding.updateProfileFormInputPasswordContainer)
        }

        val brazilStatesAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, AppConstants.BRAZIL_STATES)
        binding.updateProfileFormInputStateSelect.setAdapter(brazilStatesAdapter)

        binding.updateProfileFormAppTermsLink.setOnClickListener {
            navigateToTermsAndConditions()
        }

        binding.updateProfileFormButtonSave.setOnClickListener {
            if(validForm()) {
                val user = generateUpdatedUser()
                viewModel.updateUser(user)
            }
        }

        binding.updateProfileFormAppTermsLink.setOnClickListener {
            navigateToTermsAndConditions()
        }

        binding.updateProfileFormButtonLogout.setOnClickListener {
            dialogCreator.showDialog(
                getString(R.string.update_profile_logout_confirmation_message),
                getString(R.string.cta_exit)
            ) {
                viewModel.logoutUser()
                navigateToAuth()
            }
        }

        binding.updateProfileFormButtonRemoveAccount.setOnClickListener {
            dialogCreator.showDialog(
                getString(R.string.update_profile_deactivate_confirmation_message),
                getString(R.string.cta_remove),
            ) {
                viewModel.deactivateUser()
                navigateToAuth()
            }
        }
    }

    private fun setupObservers() {
        viewModel.userInfo.observe(viewLifecycleOwner) { userInfo ->
            userInfo?.let { fillForm(it) }
        }
        viewModel.updateResult.observe(viewLifecycleOwner) { success ->
            if (success) {
                showToast("Atualizado com sucesso!")
            }
        }
        viewModel.userMessage.observe(viewLifecycleOwner) { message ->
            message?.let { showToast(it) }
        }
        viewModel.fieldError.observe(viewLifecycleOwner) { error ->
            error?.let { showFieldError(error) }
        }
    }

    private fun showFieldError(error: FieldError) {
        when(error.fieldName) {
            "email" -> {
                validator.setFieldError(
                    binding.updateProfileFormInputEmailContainer, binding.updateProfileFormInputEmailError,
                    getString(R.string.form_error_conflict, "E-mail")
                )
            }
        }
    }

    private fun generateUpdatedUser(): UserUpdateDTO {
        return UserUpdateDTO(
            name = binding.updateProfileFormInputName.text.toString(),
            enterpriseType = binding.updateProfileFormInputEnterpriseType.text.toString(),
            email = binding.updateProfileFormInputEmail.text.toString(),
            password = binding.updateProfileFormInputPassword.text.toString(),
            cep = binding.updateProfileFormInputCep.text.toString(),
            addressNumber = binding.updateProfileFormInputAddressNumber.text.toString().toInt(),
            street = binding.updateProfileFormInputStreet.text.toString(),
            city = binding.updateProfileFormInputCity.text.toString(),
            state = binding.updateProfileFormInputStateSelect.text.toString(),
        )
    }

    private fun fillForm(userInfo: UserFullIDTO) {
        binding.updateProfileFormInputName.setText(userInfo.name)
        binding.updateProfileFormInputEnterpriseType.setText(userInfo.enterpriseType)
        binding.updateProfileFormInputEmail.setText(userInfo.email)
        binding.updateProfileFormInputPassword.setText(userInfo.password)
        binding.updateProfileFormInputCep.setText(userInfo.cep)
        binding.updateProfileFormInputAddressNumber.setText(userInfo.addressNumber.toString())
        binding.updateProfileFormInputStreet.setText(userInfo.street)
        binding.updateProfileFormInputCity.setText(userInfo.city)
        binding.updateProfileFormInputStateSelect.setText(userInfo.state, false)
    }

    private fun navigateToTermsAndConditions() {
        val action = UpdateProfileFragmentDirections.actionUpdateProfileToTermsAndConditions()
        findNavController().navigate(action)
    }

    private fun navigateToAuth() {
        val currentActivity = requireActivity()
        val intent = Intent(currentActivity, AuthActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        currentActivity.startActivity(intent)
        currentActivity.finish()
    }

    private fun validForm() : Boolean {
        val isNameValid = validator.validMinLength(binding.updateProfileFormInputName, binding.updateProfileFormInputNameContainer, binding.updateProfileFormInputNameError, 4) &&
                validator.validMaxLength(binding.updateProfileFormInputName, binding.updateProfileFormInputNameContainer, binding.updateProfileFormInputNameError, 40)
        val isEnterpriseTypeValid = if(viewModel.getUserType() == AppAccountType.BUSINESS) {
            validator.validNotNull(binding.updateProfileFormInputEnterpriseType, binding.updateProfileFormInputEnterpriseTypeContainer, binding.updateProfileFormInputEnterpriseTypeError)
        } else {
            true
        }
        val isEmailValid = validator.validIsEmail(binding.updateProfileFormInputEmail, binding.updateProfileFormInputEmailContainer, binding.updateProfileFormInputEmailError)
        val isPasswordValid = validator.validPassword(binding.updateProfileFormInputPassword, binding.updateProfileFormInputPasswordContainer, binding.updateProfileFormInputPasswordError)
        val isCepValid = validator.validCep(binding.updateProfileFormInputCep, binding.updateProfileFormInputCepContainer, binding.updateProfileFormInputCepError)
        val isAddressNumberValid = validator.validMaxLength(binding.updateProfileFormInputAddressNumber, binding.updateProfileFormInputAddressNumberContainer, binding.updateProfileFormInputAddressNumberError, 4) &&
                validator.validNotNull(binding.updateProfileFormInputAddressNumber, binding.updateProfileFormInputAddressNumberContainer, binding.updateProfileFormInputAddressNumberError)
        val isStreetValid = validator.validNotNull(binding.updateProfileFormInputStreet, binding.updateProfileFormInputStreetContainer, binding.updateProfileFormInputStreetError)
        val isCityValid = validator.validNotNull(binding.updateProfileFormInputCity, binding.updateProfileFormInputCityContainer, binding.updateProfileFormInputCityError)
        val isStateValid = validator.validNotNull(binding.updateProfileFormInputStateSelect, binding.updateProfileFormInputStateSelectContainer, binding.updateProfileFormInputStateSelectError)
        return isNameValid && isEnterpriseTypeValid && isEmailValid &&
                isPasswordValid && isCepValid && isAddressNumberValid &&
                isCityValid && isStreetValid && isStateValid
    }
}