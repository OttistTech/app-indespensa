package com.ottistech.indespensa.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ottistech.indespensa.R
import com.ottistech.indespensa.data.repository.ShopRepository
import com.ottistech.indespensa.databinding.FragmentShopHistoryBinding
import com.ottistech.indespensa.ui.model.feedback.Feedback
import com.ottistech.indespensa.ui.model.feedback.FeedbackCode
import com.ottistech.indespensa.ui.recyclerview.adapter.PurchaseHistoryAdapter
import com.ottistech.indespensa.ui.viewmodel.ShopHistoryViewModel
import com.ottistech.indespensa.webclient.dto.shoplist.PurchaseDTO

class ShopHistoryFragment : Fragment() {

    private lateinit var binding: FragmentShopHistoryBinding
    private lateinit var adapter: PurchaseHistoryAdapter
    private lateinit var viewModel: ShopHistoryViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        adapter = setupAdapter()
        binding = FragmentShopHistoryBinding.inflate(inflater, container, false)
        viewModel = ShopHistoryViewModel(
            ShopRepository(requireContext())
        )
        binding.model = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupObservers()
        setupBackButton()
    }

    override fun onResume() {
        super.onResume()
        viewModel.fetchHistory()
    }

    private fun setupAdapter() : PurchaseHistoryAdapter {
        return PurchaseHistoryAdapter(
            context = requireContext()
        )
    }

    private fun setupBackButton() {
        binding.shopHistoryBack.setOnClickListener {
            popBackStack()
        }
    }

    private fun popBackStack() {
        findNavController().popBackStack(R.id.shop_history_dest, true)
    }

    private fun setupRecyclerView() {
        val recyclerView = binding.shopHistoryList
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun setupObservers() {
        viewModel.history.observe(viewLifecycleOwner) { history ->
            if(history != null) {
                binding.shopHistoryPurchases.text = getString(R.string.purchases_number, history.size)
                binding.shopHistoryIngredients.text = getString(R.string.ingredients_number, history.sumOf { it.dailyAmount })
                adapter.updateState(
                    prepareDataForAdapter(history)
                )
            }
        }

        viewModel.feedback.observe(viewLifecycleOwner) { feedback ->
            feedback?.let {
                handleFeedback(it)
            }
        }
    }

    private fun handleFeedback(feedback: Feedback) {
        if(feedback.code == FeedbackCode.NOT_FOUND) {
            binding.shopHistoryList.visibility = View.GONE
            binding.shopHistoryMessage.text = getString(R.string.shop_history_message_empty)
            binding.shopHistoryMessage.visibility = View.VISIBLE
        }
    }

    private fun prepareDataForAdapter(data: List<PurchaseDTO>): List<Any> {
        val items = mutableListOf<Any>()
        for (purchase in data) {
            items.add(PurchaseHistoryAdapter.PurchaseHeader(
                purchase.purchaseDate,
                purchase.dailyAmount
            ))
            items.addAll(purchase.historyDataItems)
        }
        return items
    }
}

