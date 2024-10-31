package com.ottistech.indespensa.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.ottistech.indespensa.R
import com.ottistech.indespensa.data.repository.ShopRepository
import com.ottistech.indespensa.databinding.FragmentShopHistoryBinding
import com.ottistech.indespensa.ui.UiConstants
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

        viewModel.error.observe(viewLifecycleOwner) { error ->
            when(error) {
                UiConstants.ERROR_NOT_FOUND -> {
                    binding.shopHistoryList.visibility = View.GONE
                    binding.shopHistoryMessage.text = getString(R.string.shop_history_message_empty)
                    binding.shopHistoryMessage.visibility = View.VISIBLE
                }
                null -> {
                    binding.shopHistoryMessage.visibility = View.GONE
                }
            }
        }
    }

    private fun prepareDataForAdapter(data: List<PurchaseDTO>): List<Any> {
        val items = mutableListOf<Any>()
        for (purchase in data) {
            Log.d("TAG", purchase.toString())
            items.add(PurchaseHistoryAdapter.PurchaseHeader(
                purchase.purchaseDate,
                purchase.dailyAmount
            ))
            items.addAll(purchase.historyDataItems)
        }
        return items
    }
}

