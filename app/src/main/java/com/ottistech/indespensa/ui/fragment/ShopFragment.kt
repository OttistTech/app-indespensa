package com.ottistech.indespensa.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ottistech.indespensa.R
import com.ottistech.indespensa.data.repository.PantryRepository
import com.ottistech.indespensa.data.repository.ShopRepository
import com.ottistech.indespensa.databinding.FragmentShoplistBinding
import com.ottistech.indespensa.shared.ProductItemType
import com.ottistech.indespensa.ui.UiConstants
import com.ottistech.indespensa.ui.helpers.makeSpanText
import com.ottistech.indespensa.ui.recyclerview.adapter.ShopAdapter
import com.ottistech.indespensa.ui.viewmodel.ShopViewModel

class ShopFragment : Fragment() {

    private lateinit var binding: FragmentShoplistBinding
    private lateinit var adapter: ShopAdapter
    private lateinit var viewModel: ShopViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentShoplistBinding.inflate(inflater, container, false)
        adapter = setupAdapter()
        viewModel = ShopViewModel(
            ShopRepository(requireContext()),
            PantryRepository(requireContext())
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.shoplistAddAllToPantryButton.isEnabled = false

        setupRecyclerView()
        setupObservers()
        setupUiInteractions()
    }

    override fun onResume() {
        super.onResume()
        binding.shopProgressbar.visibility = View.VISIBLE
        viewModel.fetchShop()
    }

    override fun onStop() {
        super.onStop()
        viewModel.syncChanges()
    }

    private fun setupAdapter(): ShopAdapter {
        val adapter = ShopAdapter(
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
        val recyclerView : RecyclerView = binding.shopRecyclerview
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun setupObservers() {
        viewModel.shopState.observe(viewLifecycleOwner) { shopItems ->
            binding.shopProgressbar.visibility = View.GONE

            if (!shopItems.isNullOrEmpty()) {
                adapter.updateState(shopItems)
                binding.shopRecyclerview.visibility = View.VISIBLE

                binding.shoplistAddAllToPantryButton.isEnabled = true

                val itemsInShopListText = shopItems.size.toString() + " ingredientes"
                binding.shoplistQuantityIngredients.text = makeSpanText(
                    getString(R.string.shoplist_ingredients_count, itemsInShopListText),
                    itemsInShopListText,
                    ContextCompat.getColor(requireContext(), R.color.secondary)
                )
            }
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            binding.shopProgressbar.visibility = View.GONE
            when (error) {
                UiConstants.ERROR_NOT_FOUND -> {
                    binding.shopRecyclerview.visibility = View.GONE
                    binding.shopMessage.text = getString(R.string.shoplist_message_empty)
                    binding.shopMessage.visibility = View.VISIBLE
                    binding.shoplistAddAllToPantryButton.isEnabled = false
                    binding.shoplistQuantityIngredients.text = makeSpanText(
                        getString(R.string.shoplist_ingredients_count, "0 ingredientes"),
                        "0 ingredientes",
                        ContextCompat.getColor(requireContext(), R.color.secondary)
                    )
                }
                null -> {
                    binding.shopMessage.visibility = View.GONE
                }
            }
        }

        viewModel.message.observe(viewLifecycleOwner) { message ->
            message?.let {
                if (it == UiConstants.OK) {
                    Toast.makeText(requireContext(), "Todos os itens foram adicionados à despensa!", Toast.LENGTH_SHORT).show()
                } else if (it == UiConstants.FAIL) {
                    Toast.makeText(requireContext(), "Erro ao adicionar itens à despensa", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setupUiInteractions() {
        binding.shoplistAddAllToPantryButton.setOnClickListener {
            viewModel.addAllItemsFromShopToPantry()
        }
    }

    private fun navigateToProductDetails(itemId: Long) {
        val productItemType = ProductItemType.SHOP_LIST_ITEM
        val action = ShopFragmentDirections.actionShoplistToProductDetails(itemId, productItemType)
        findNavController().navigate(action)
    }
}

