package com.ottistech.indespensa.ui.fragment

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ottistech.indespensa.R
import com.ottistech.indespensa.data.repository.PantryRepository
import com.ottistech.indespensa.databinding.FragmentPantryBinding
import com.ottistech.indespensa.ui.UiConstants
import com.ottistech.indespensa.ui.helpers.getCurrentUser
import com.ottistech.indespensa.ui.helpers.makeSpanText
import com.ottistech.indespensa.ui.helpers.showToast
import com.ottistech.indespensa.ui.recyclerview.adapter.PantryAdapter
import com.ottistech.indespensa.ui.viewmodel.PantryViewModel
import kotlinx.coroutines.launch

class PantryFragment : Fragment() {

    private lateinit var binding : FragmentPantryBinding
    private lateinit var adapter : PantryAdapter
    private lateinit var viewModel : PantryViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPantryBinding.inflate(inflater, container, false)
        adapter = setupAdapter()
        viewModel = PantryViewModel(
            PantryRepository(requireContext())
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupObservers()

        val currentUserName = requireContext().getCurrentUser().name
        binding.pantryText.text = makeSpanText(
            getString(R.string.pantry_text, currentUserName),
            currentUserName,
            ContextCompat.getColor(requireContext(), R.color.secondary)
        )

        binding.pantryFab.setOnClickListener {
            navigateToPantryForm()
        }
    }

    override fun onResume() {
        super.onResume()
        binding.pantryProgressbar.visibility = View.VISIBLE
        viewModel.fetchPantry()
    }

    override fun onStop() {
        super.onStop()
        viewModel.syncChanges()
    }

    private fun setupObservers() {
        viewModel.pantryState.observe(viewLifecycleOwner) { pantryItems ->
            binding.pantryProgressbar.visibility = View.GONE
            if(pantryItems != null) {
                adapter.updateState(pantryItems)
                binding.pantryItemsList.visibility = View.VISIBLE
            }
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            binding.pantryProgressbar.visibility = View.GONE
            when(error) {
                UiConstants.ERROR_NOT_FOUND -> {
                    binding.pantryItemsList.visibility = View.GONE
                    binding.pantryMessage.text = getString(R.string.pantry_message_empty)
                    binding.pantryMessage.visibility = View.VISIBLE
                }
                null -> {
                    binding.pantryMessage.visibility = View.GONE
                }
            }
        }
    }

    private fun setupAdapter() : PantryAdapter {
        val adapter = PantryAdapter(
            context = requireContext(),
            onChangeAmount = { itemId, amount ->
                viewModel.registerItemChange(itemId, amount)
            }
        )
        return adapter
    }

    private fun setupRecyclerView() {
        val recyclerView : RecyclerView = binding.pantryItemsList
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun navigateToPantryForm() {
        val action = PantryFragmentDirections.pantryToPantryForm()
        findNavController().navigate(action)
    }
}