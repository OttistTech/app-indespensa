package com.ottistech.indespensa.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.ottistech.indespensa.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private lateinit var binding : FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.homeFab.setOnClickListener {
            navigateToRecipeForm()
        }

        binding.homeAccessPantry.setOnClickListener {
            navigateToPantry()
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
}