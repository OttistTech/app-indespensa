package com.ottistech.indespensa.ui.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.ottistech.indespensa.R
import com.ottistech.indespensa.data.exception.FieldConflictException
import com.ottistech.indespensa.data.repository.DashboardRepository
import com.ottistech.indespensa.data.repository.UserRepository
import com.ottistech.indespensa.databinding.FragmentProfileBinding
import com.ottistech.indespensa.ui.helpers.showToast
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {

    private val TAG = "PROFILE FRAGMENT"
    private lateinit var binding : FragmentProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            try {
                val result = DashboardRepository(requireContext()).getProfileData()

                binding.profileBigNumberData1.text = result.itemsInPantryCount.toString()
                binding.profileBigNumberLabel1.text = getString(R.string.profile_items_in_pantry)

                binding.profileBigNumberData2.text = result.purchasesMadeCount.toString()
                binding.profileBigNumberLabel2.text = getString(R.string.profile_purchases_made)

                binding.profileBigNumberData3.text = result.productsAlreadyExpiredCount.toString()
                binding.profileBigNumberLabel3.text = getString(R.string.profile_products_already_expired)

                binding.profileBigNumberData4.text = result.recipesMadeCount.toString()
                binding.profileBigNumberLabel4.text = getString(R.string.profile_recipes_made)

            } catch (e: Exception) {
                showToast("Failed to get dash profile info")
            }
        }

        lifecycleScope.launch {
            try {
                val result = DashboardRepository(requireContext()).getProfileData()

                binding.profileBigNumberData1.text = result.itemsInPantryCount.toString()
                binding.profileBigNumberLabel1.text = getString(R.string.profile_items_in_pantry)

                binding.profileBigNumberData2.text = result.purchasesMadeCount.toString()
                binding.profileBigNumberLabel2.text = getString(R.string.profile_purchases_made)

                binding.profileBigNumberData3.text = result.productsAlreadyExpiredCount.toString()
                binding.profileBigNumberLabel3.text = getString(R.string.profile_products_already_expired)

                binding.profileBigNumberData4.text = result.recipesMadeCount.toString()
                binding.profileBigNumberLabel4.text = getString(R.string.profile_recipes_made)

            } catch (e: Exception) {
                showToast("Failed to get recipes made by you")
            }
        }
    }

}