package com.ottistech.indespensa.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import com.ottistech.indespensa.ui.helpers.FieldVisibilitySwitcher
import com.ottistech.indespensa.ui.helpers.showToast
import com.ottistech.indespensa.ui.model.feedback.Feedback
import com.ottistech.indespensa.ui.model.feedback.FeedbackCode
import com.ottistech.indespensa.ui.model.feedback.FeedbackId
import com.ottistech.indespensa.ui.viewmodel.UpdateProfileViewModel

class UpdateProfileFragment : Fragment() {

    private lateinit var binding: FragmentUpdateProfileBinding
    private lateinit var visibilitySwitcher : FieldVisibilitySwitcher
    private lateinit var dialogCreator : ConfirmationDialogCreator
    private lateinit var viewModel: UpdateProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        visibilitySwitcher = FieldVisibilitySwitcher(requireContext())
        dialogCreator = ConfirmationDialogCreator(requireContext())
        binding = FragmentUpdateProfileBinding.inflate(inflater, container, false)
        viewModel = UpdateProfileViewModel(
            UserRepository(requireContext())
        )
        binding.model = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupForAccountType(viewModel.userType)
        setupValidationListeners()
        setupDeactivateButton()
        setupAppTermsButton()
        setupStatesSelect()
        setupLogoutButton()
        setupObservers()
        setupBackButton()

        var passwordVisibility = false
        binding.updateProfilePasswordLayout.setEndIconOnClickListener {
            passwordVisibility = visibilitySwitcher.switch(passwordVisibility,
                binding.updateProfilePasswordField, binding.updateProfilePasswordLayout)
        }
    }

    private fun setupBackButton() {
        binding.updateProfileBack.setOnClickListener {
            popBackStack()
        }
    }

    private fun popBackStack() {
        findNavController().popBackStack(R.id.update_profile_dest, true)
    }

    private fun setupDeactivateButton() {
        binding.updateProfileFormButtonRemoveAccount.setOnClickListener {
            Log.d("DEBUG", "PASSOU AQUI 2")
            dialogCreator.showDialog(
                title = getString(R.string.update_profile_deactivate_confirmation_message),
                actionText = getString(R.string.cta_remove),
            ) {
                viewModel.deactivate()
                navigateToAuth()
            }
        }
    }

    private fun setupLogoutButton() {
        binding.updateProfileButtonLogout.setOnClickListener {
            Log.d("DEBUG", "PASSOU AQUI")
            dialogCreator.showDialog(
                title = getString(R.string.update_profile_logout_confirmation_message),
                actionText = getString(R.string.cta_exit)
            ) {
                viewModel.logout()
                navigateToAuth()
            }
        }
    }

    private fun setupStatesSelect() {
        val brazilStatesAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_list_item_1,
            AppConstants.BRAZIL_STATES
        )
        binding.updateProfileStateSelectField.setAdapter(brazilStatesAdapter)
    }

    private fun setupForAccountType(type: AppAccountType) {
        if (type != AppAccountType.BUSINESS) {
            binding.updateProfileEnterpriseTypeLayout.visibility = View.GONE
        }
    }

    private fun setupAppTermsButton() {
        binding.updateProfileFormAppTermsLink.setOnClickListener {
            navigateToTermsAndConditions()
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.fetchUserInfo()
    }

    private fun setupObservers() {
        viewModel.feedback.observe(viewLifecycleOwner) { feedback ->
            feedback?.let {
                handleFeedback(it)
            }
        }
    }

    private fun handleFeedback(feedback: Feedback) {
        when(feedback.feedbackId) {
            FeedbackId.DEACTIVATE_PROFILE -> {
                showToast(feedback.message)
            }
            FeedbackId.UPDATE_PROFILE -> {
                showToast(feedback.message)
            }
            FeedbackId.GET_PROFILE_DATA -> {
                if(feedback.code != FeedbackCode.SUCCESS) {
                    showToast(feedback.message)
                    findNavController().popBackStack(R.id.nav_profile, false)
                }
            }
        }
    }

    private fun setupValidationListeners() {
        binding.updateProfileNameField.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) { viewModel.validName() }
        }
        binding.updateProfileEnterpriseTypeLayout.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) { viewModel.validEnterpriseType() }
        }
        binding.updateProfileEmailField.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) { viewModel.validEmail() }
        }
        binding.updateProfilePasswordLayout.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) { viewModel.validPassword() }
        }
        binding.updateProfileCepField.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) { viewModel.validCep() }
        }
        binding.updateProfileAddressNumberField.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) { viewModel.validAddressNumber() }
        }
        binding.updateProfileStreetField.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) { viewModel.validStreet() }
        }
        binding.updateProfileCityField.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) { viewModel.validCity() }
        }
        binding.updateProfileStateSelectField.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) { viewModel.validState() }
        }
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
}