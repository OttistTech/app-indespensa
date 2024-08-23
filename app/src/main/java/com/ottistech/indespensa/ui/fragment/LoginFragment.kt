package com.ottistech.indespensa.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.ottistech.indespensa.R
import com.ottistech.indespensa.data.exception.FieldConflictException
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
                    } catch (e: FieldConflictException) {
                        validator.setFieldError(
                            binding.loginFormInputEmailContainer, binding.loginFormInputEmailError,
                            getString(R.string.form_error_conflict, "E-mail")
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

    private fun validForm() : Boolean {
        // email
        val isEmailValid = validator.validIsEmail(binding.loginFormInputEmail, binding.loginFormInputEmailContainer, binding.loginFormInputEmailError)

        // password
        val isPasswordValid = validator.validPassword(binding.loginFormInputPassword, binding.loginFormInputPasswordContainer, binding.loginFormInputPasswordError)

        return isEmailValid && isPasswordValid
    }

}