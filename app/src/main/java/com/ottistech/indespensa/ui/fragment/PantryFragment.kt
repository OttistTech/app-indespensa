package com.ottistech.indespensa.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ottistech.indespensa.R
import com.ottistech.indespensa.data.repository.PantryRepository
import com.ottistech.indespensa.databinding.FragmentPantryBinding
import com.ottistech.indespensa.shared.ProductItemType
import com.ottistech.indespensa.shared.getCurrentUser
import com.ottistech.indespensa.shared.showToast
import com.ottistech.indespensa.ui.helpers.makeSpanText
import com.ottistech.indespensa.ui.model.feedback.Feedback
import com.ottistech.indespensa.ui.model.feedback.FeedbackCode
import com.ottistech.indespensa.ui.model.feedback.FeedbackId
import com.ottistech.indespensa.ui.recyclerview.adapter.PantryAdapter
import com.ottistech.indespensa.ui.viewmodel.PantryViewModel

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
        setupBackButton()
        setupObservers()
        showUserMessage()
        setupFab()
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

    private fun setupFab() {
        binding.pantryFab.setOnClickListener {
            navigateToPantryForm()
        }
    }

    private fun showUserMessage() {
        val currentUserName = requireContext().getCurrentUser().name
        binding.pantryText.text = makeSpanText(
            getString(R.string.pantry_text, currentUserName),
            currentUserName,
            ContextCompat.getColor(requireContext(), R.color.secondary)
        )
    }

    private fun setupBackButton() {
        binding.pantryBack.setOnClickListener {
            popBackStack()
        }
    }

    private fun popBackStack() {
        findNavController().popBackStack(R.id.pantry_dest, true)
    }

    private fun setupObservers() {
        viewModel.pantryState.observe(viewLifecycleOwner) { pantryItems ->
            binding.pantryProgressbar.visibility = View.GONE
            if(pantryItems != null) {
                adapter.updateState(pantryItems)
                binding.pantryItemsList.visibility = View.VISIBLE
            }
        }

        viewModel.feedback.observe(viewLifecycleOwner) { feedback ->
            binding.pantryProgressbar.visibility = View.GONE
            feedback?.let {
                handleFeedback(it)
            }
        }
    }

    private fun handleFeedback(feedback: Feedback) {
        if (
            feedback.feedbackId == FeedbackId.PANTRY_LIST &&
            feedback.code == FeedbackCode.NOT_FOUND
        ) {
            binding.pantryItemsList.visibility = View.GONE
            binding.pantryMessage.text = feedback.message
            binding.pantryMessage.visibility = View.VISIBLE
        } else {
            showToast(feedback.message)
        }
    }

    private fun setupAdapter() : PantryAdapter {
        val adapter = PantryAdapter(
            context = requireContext(),
            onChangeAmount = { itemId, amount ->
                viewModel.registerItemChange(itemId, amount)
            },
            onItemClick = { itemId ->
                navigateToProductDetails(itemId)
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

    private fun navigateToProductDetails(itemId: Long) {
        val productItemType = ProductItemType.PANTRY_ITEM
        val action = PantryFragmentDirections.pantryToProductDetails(itemId, productItemType)
        findNavController().navigate(action)
    }
}