package com.ottistech.indespensa.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.ottistech.indespensa.R
import com.ottistech.indespensa.data.exception.ResourceNotFoundException
import com.ottistech.indespensa.data.exception.ResourceUnauthorizedException
import com.ottistech.indespensa.data.repository.UserRepository
import com.ottistech.indespensa.databinding.FragmentLoginBinding
import com.ottistech.indespensa.ui.helpers.FieldValidations
import com.ottistech.indespensa.ui.helpers.FieldVisibilitySwitcher
import com.ottistech.indespensa.webclient.dto.UserLoginDTO
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {

    private val TAG = "LOGIN FRAGMENT"
    private lateinit var binding: FragmentLoginBinding
    private lateinit var validator : FieldValidations
    private lateinit var visibilitySwitcher : FieldVisibilitySwitcher

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        validator = FieldValidations(requireContext())
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        visibilitySwitcher = FieldVisibilitySwitcher(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var passwordVisibility = false

        binding.loginFormInputPasswordContainer.setEndIconOnClickListener {
            passwordVisibility = visibilitySwitcher.switch(passwordVisibility,
                binding.loginFormInputPassword, binding.loginFormInputPasswordContainer)
        }

        binding.loginFormButton.setOnClickListener {
            if (validForm()) {
                val user = generateLoginUser()

                lifecycleScope.launch {
                    try {
                        UserRepository(requireContext()).loginUser(user)
                    } catch (e: ResourceNotFoundException) {
                        validator.setFieldError(
                            null, binding.loginFormInputUnauthorizedNotfoundError,
                            getString(R.string.form_login_unauthorizated_error)
                        )
                    } catch (e: ResourceUnauthorizedException) {
                        validator.setFieldError(
                            null, binding.loginFormInputUnauthorizedNotfoundError,
                            getString(R.string.form_login_notfound_error)
                        )
                    }
                }
            }
        }

    }

    private fun generateLoginUser(): UserLoginDTO {
        return UserLoginDTO(
            email = binding.loginFormInputEmail.text.toString(),
            password = binding.loginFormInputPassword.text.toString(),
        )
    }

    private fun validForm(): Boolean {
        var isFormValid = true

        val isEmailNotNull = validator.validNotNull(
            binding.loginFormInputEmail,
            binding.loginFormInputEmailContainer,
            binding.loginFormInputEmailError
        )
        if (!isEmailNotNull) {
            isFormValid = false
        }

        val isPasswordNotNull = validator.validNotNull(
            binding.loginFormInputPassword,
            binding.loginFormInputPasswordContainer,
            binding.loginFormInputPasswordError
        )
        if (!isPasswordNotNull) {
            isFormValid = false
        }

        if (isEmailNotNull) {
            val isEmailValid = validator.validIsEmailDetailed(
                binding.loginFormInputEmail,
                binding.loginFormInputEmailContainer,
                binding.loginFormInputEmailError
            )
            if (!isEmailValid) {
                isFormValid = false
            }
        }

//        if (isPasswordNotNull) {
//            val isPasswordValid = validator.validPassword(
//                binding.loginFormInputPassword,
//                binding.loginFormInputPasswordContainer,
//                binding.loginFormInputPasswordError
//            )
//            if (!isPasswordValid) {
//                isFormValid = false
//            }
//        }

        return isFormValid
    }

}