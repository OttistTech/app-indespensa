package com.ottistech.indespensa.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.ottistech.indespensa.R
import com.ottistech.indespensa.data.repository.UserRepository
import com.ottistech.indespensa.databinding.FragmentHomeBinding
import com.ottistech.indespensa.ui.helpers.showToast
import com.ottistech.indespensa.ui.viewmodel.HomeViewModel

class HomeFragment : Fragment() {

    private lateinit var binding : FragmentHomeBinding
    private lateinit var viewModel : HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        viewModel = HomeViewModel(
            UserRepository(requireContext())
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupAccountTypeRestrictions()

        binding.homeFab.setOnClickListener {
            navigateToRecipeForm()
        }

        binding.homeAccessPantry.setOnClickListener {
            navigateToPantry()
        }
    }

    private fun setupAccountTypeRestrictions() {
        if(viewModel.isUserPremium()) {
            binding.homeRecipeSearch.setOnClickListener {
                navigateToRecipeSearch()
            }
        } else {
            binding.homeRecipeSearchContainer.setEndIconDrawable(R.drawable.ic_locked)
            binding.homeRecipeSearch.setOnClickListener {
                showToast("Seja Premium para buscar receitas")
            }
        }

    }

    private fun navigateToPantry() {
        val action = HomeFragmentDirections.homeToPantry()
        findNavController().navigate(action)
    }

    private fun navigateToRecipeForm() {
        val action = HomeFragmentDirections.homeToRecipeForm()
        findNavController().navigate(action)
    }

    private fun navigateToRecipeSearch() {
        val action = HomeFragmentDirections.homeToRecipeSearch()
        findNavController().navigate(action)
    }
}