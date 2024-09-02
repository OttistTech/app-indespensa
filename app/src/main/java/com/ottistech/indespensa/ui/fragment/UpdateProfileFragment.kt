package com.ottistech.indespensa.ui.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.ottistech.indespensa.R
import com.ottistech.indespensa.data.datasource.UserFirebaseDataSource
import com.ottistech.indespensa.data.exception.BadRequestException
import com.ottistech.indespensa.data.exception.FieldConflictException
import com.ottistech.indespensa.data.exception.ResourceNotFoundException
import com.ottistech.indespensa.data.exception.ResourceUnauthorizedException
import com.ottistech.indespensa.data.repository.UserRepository
import com.ottistech.indespensa.databinding.FragmentLoginBinding
import com.ottistech.indespensa.databinding.FragmentUpdateProfileBinding
import com.ottistech.indespensa.shared.AppConstants
import com.ottistech.indespensa.ui.helpers.FieldValidations
import com.ottistech.indespensa.ui.helpers.FieldVisibilitySwitcher
import com.ottistech.indespensa.ui.helpers.toDate
import com.ottistech.indespensa.webclient.dto.UserCreateDTO
import com.ottistech.indespensa.webclient.dto.UserFullCredentialsDTO
import com.ottistech.indespensa.webclient.dto.UserLoginDTO
import com.ottistech.indespensa.webclient.dto.UserUpdateDTO
import kotlinx.coroutines.launch

class UpdateProfileFragment : Fragment() {

    private val TAG = "UPDATE PROFILE FRAGMENT"
    private lateinit var binding: FragmentUpdateProfileBinding
    private lateinit var validator : FieldValidations
    private lateinit var visibilitySwitcher : FieldVisibilitySwitcher
    private lateinit var userFirebaseDataSource: UserFirebaseDataSource

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

        var passwordVisibility = false
        binding.updateProfileFormInputPasswordContainer.setEndIconOnClickListener {
            passwordVisibility = visibilitySwitcher.switch(passwordVisibility,
                binding.updateProfileFormInputPassword, binding.updateProfileFormInputPasswordContainer)
        }

        val brazilStatesAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, AppConstants.BRAZIL_STATES)
        binding.updateProfileFormInputStateSelect.setAdapter(brazilStatesAdapter)

        val userId = 7L // TODO: get the userId from authentication or something like this

        fetchUserDetails(userId)

        // TODO: navigation to terms and conditions
        binding.updateProfileFormAppTermsLink.setOnClickListener {
            navigateToTermsAndConditions()
        }

        binding.updateProfileFormButtonSave.setOnClickListener {

            if(validForm()) {
                val user = generateUpdatedUser()

                lifecycleScope.launch {
                    Log.d(TAG, "Trying to update user. Called UserRepository.updateUser with $user")

                    try {
                        val result = UserRepository(requireContext()).updateUser(userId, user)

                        if(result != null) {
                            fetchUserDetails(userId)
                        }
                    }
                    catch (e: FieldConflictException) {
                        validator.setFieldError(
                            binding.updateProfileFormInputEmailContainer, binding.updateProfileFormInputEmailError,
                            getString(R.string.form_error_conflict, "E-mail")
                        )
                    } catch (e: ResourceNotFoundException) {
                        validator.setFieldError(
                            null, binding.updateProfileFormInputUnauthorizedNotfoundError,
                            getString(R.string.form_login_notfound_error)
                        )
                    } catch (e: BadRequestException) {
                        Log.d(TAG, "Bad Request for data: $user, $userId")
                    }

                }
            }
        }

        binding.updateProfileFormButtonLogout.setOnClickListener {
            userFirebaseDataSource.logoutUser()
            navigateToLandingFragment()
        }
    }

    private fun generateUpdatedUser(): UserUpdateDTO {
        return UserUpdateDTO(
            name = binding.updateProfileFormInputName.text.toString(),
            email = binding.updateProfileFormInputEmail.text.toString(),
            password = binding.updateProfileFormInputPassword.text.toString(),
            cep = binding.updateProfileFormInputCep.text.toString(),
            addressNumber = binding.updateProfileFormInputAddressNumber.text.toString().toInt(),
            street = binding.updateProfileFormInputStreet.text.toString(),
            city = binding.updateProfileFormInputCity.text.toString(),
            state = binding.updateProfileFormInputStateSelect.text.toString(),
        )
    }

    private fun fetchUserDetails(userId: Long) {
        lifecycleScope.launch {
            try {
                val userDetails = UserRepository(requireContext()).infoUserDetails(userId, true)

                if (userDetails != null) {
                    updateUIWithUserDetails(userDetails)
                } else {
                    Log.d(TAG, "Failed to fetch user details")
                }

            } catch (e: ResourceNotFoundException) {
                Log.e(TAG, "User not found")
            }
        }
    }

    private fun updateUIWithUserDetails(userDetails: UserFullCredentialsDTO) {
        binding.updateProfileFormInputName.setText(userDetails.name)
        binding.updateProfileFormInputEmail.setText(userDetails.email)
        binding.updateProfileFormInputPassword.setText(userDetails.password)
        binding.updateProfileFormInputCep.setText(userDetails.cep)
        binding.updateProfileFormInputAddressNumber.setText(userDetails.addressNumber.toString())
        binding.updateProfileFormInputStreet.setText(userDetails.street)
        binding.updateProfileFormInputCity.setText(userDetails.city)
        binding.updateProfileFormInputStateSelect.setText(userDetails.state, false)
    }

    private fun navigateToTermsAndConditions() {
        TODO("Not yet implemented")
    }

    private fun navigateToLandingFragment() {
        val action = ProfileFragmentDirections.actionNavProfileToLandingFragment()

        findNavController().navigate(action)
    }

    private fun validForm() : Boolean {
        val isNameValid = validator.validMinLength(binding.updateProfileFormInputName, binding.updateProfileFormInputNameContainer, binding.updateProfileFormInputNameError, 4) &&
                validator.validMaxLength(binding.updateProfileFormInputName, binding.updateProfileFormInputNameContainer, binding.updateProfileFormInputNameError, 40)

        val isEmailValid = validator.validIsEmail(binding.updateProfileFormInputEmail, binding.updateProfileFormInputEmailContainer, binding.updateProfileFormInputEmailError)

        val isPasswordValid = validator.validPassword(binding.updateProfileFormInputPassword, binding.updateProfileFormInputPasswordContainer, binding.updateProfileFormInputPasswordError)

        val isCepValid = validator.validCep(binding.updateProfileFormInputCep, binding.updateProfileFormInputCepContainer, binding.updateProfileFormInputCepError)

        val isAddressNumberValid = validator.validMaxLength(binding.updateProfileFormInputAddressNumber, binding.updateProfileFormInputAddressNumberContainer, binding.updateProfileFormInputAddressNumberError, 4) &&
                validator.validNotNull(binding.updateProfileFormInputAddressNumber, binding.updateProfileFormInputAddressNumberContainer, binding.updateProfileFormInputAddressNumberError)

        val isStreetValid = validator.validNotNull(binding.updateProfileFormInputStreet, binding.updateProfileFormInputStreetContainer, binding.updateProfileFormInputStreetError)

        val isCityValid = validator.validNotNull(binding.updateProfileFormInputCity, binding.updateProfileFormInputCityContainer, binding.updateProfileFormInputCityError)

        val isStateValid = validator.validNotNull(binding.updateProfileFormInputStateSelect, binding.updateProfileFormInputStateSelectContainer, binding.updateProfileFormInputStateSelectError)

        return isNameValid && isEmailValid &&
                isPasswordValid && isCepValid && isAddressNumberValid &&
                isCityValid && isStreetValid && isStateValid
    }

}