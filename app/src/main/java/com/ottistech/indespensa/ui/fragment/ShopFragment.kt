package com.ottistech.indespensa.ui.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.ottistech.indespensa.R
import com.ottistech.indespensa.data.repository.PantryRepository
import com.ottistech.indespensa.data.repository.ProductRepository
import com.ottistech.indespensa.data.repository.RecommendationRepository
import com.ottistech.indespensa.data.repository.ShopRepository
import com.ottistech.indespensa.databinding.FragmentShoplistBinding
import com.ottistech.indespensa.shared.ProductItemType
import com.ottistech.indespensa.ui.dialog.ConfirmationDialogCreator
import com.ottistech.indespensa.ui.helpers.makeSpanText
import com.ottistech.indespensa.ui.helpers.showToast
import com.ottistech.indespensa.ui.model.feedback.Feedback
import com.ottistech.indespensa.ui.model.feedback.FeedbackCode
import com.ottistech.indespensa.ui.model.feedback.FeedbackId
import com.ottistech.indespensa.ui.recyclerview.adapter.ProductSearchAdapter
import com.ottistech.indespensa.ui.recyclerview.adapter.RecommendationAdapter
import com.ottistech.indespensa.ui.recyclerview.adapter.ShopAdapter
import com.ottistech.indespensa.ui.viewmodel.ShopViewModel
import com.ottistech.indespensa.webclient.dto.product.ProductDTO
import com.ottistech.indespensa.webclient.dto.shoplist.ShopItemPartialDTO

class ShopFragment : Fragment() {

    private lateinit var binding: FragmentShoplistBinding
    private lateinit var shopListAdapter: ShopAdapter
    private lateinit var recommendationAdapter: RecommendationAdapter
    private lateinit var searchAdapter: ProductSearchAdapter
    private lateinit var viewModel: ShopViewModel
    private lateinit var confirmationDialogCreator: ConfirmationDialogCreator

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        confirmationDialogCreator = ConfirmationDialogCreator(requireContext())
        binding = FragmentShoplistBinding.inflate(inflater, container, false)
        viewModel = ShopViewModel(
            ShopRepository(requireContext()),
            PantryRepository(requireContext()),
            ProductRepository(),
            RecommendationRepository(requireContext())
        )
        binding.model = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.shoplistAddAllToPantryButton.isEnabled = false

        setupAdapter()
        setupSearchRecyclerView()
        setupObservers()
        setupUiInteractions()
        setupSearchBar()
    }

    override fun onResume() {
        super.onResume()
        viewModel.fetchShop()
    }

    override fun onStop() {
        super.onStop()
        viewModel.syncChanges()
        viewModel.clearCallbacks()
    }

    private fun setupAdapter() {
        shopListAdapter = ShopAdapter(
            context = requireContext(),
            onChangeAmount = { itemId, amount ->
                viewModel.registerItemChange(itemId, amount)
            },
            onItemClick = { itemId ->
                navigateToProductDetails(itemId)
            }
        )
        recommendationAdapter = RecommendationAdapter(
            context = requireContext(),
            onAdd = { product ->
                product.productId?.let {
                    viewModel.addItemToShopList(it)
                }
            }
        )
        searchAdapter = ProductSearchAdapter(
            requireContext()
        ) { product ->
            viewModel.clearCallbacks()
            confirmationDialogCreator.showDialog(
                "+ Lista de compras",
                "Deseja adicionar ${product.productName} à lista de compras?",
                product.imageUrl,
                "Adicionar"
            ) {
                viewModel.addItemToShopList(product.productId)
            }
        }
    }

    private fun setupSearchRecyclerView() {
        with(binding.shoplistSearchResultList) {
            adapter = searchAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun setupShopRecyclerView(recyclerViewAdapter: Adapter<*>) {
        with(binding.shoplistProductList) {
            adapter = recyclerViewAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun setupObservers() {
        viewModel.shopItems.observe(viewLifecycleOwner) { shopItems ->
            if (!shopItems.isNullOrEmpty()) {
                setupShopRecyclerView(shopListAdapter)
                handleShopListResult(shopItems)
            }
        }

        viewModel.recommendations.observe(viewLifecycleOwner) { products ->
            if (!products.isNullOrEmpty()) {
                setupShopRecyclerView(recommendationAdapter)
                handleRecommendationsResult(products)
            }
        }

        viewModel.searchResult.observe(viewLifecycleOwner) { result ->
            result?.let {
                binding.shoplistSearchResultList.visibility = View.VISIBLE
                searchAdapter.updateState(it)
            }
        }

        viewModel.feedback.observe(viewLifecycleOwner) { feedback ->
            feedback?.let {
                handleFeedback(it)
            }
        }
    }

    private fun handleRecommendationsResult(products: List<ProductDTO>) {
        recommendationAdapter.updateState(products)
        binding.shoplistMessage.visibility = View.GONE
        binding.shoplistProductList.visibility = View.VISIBLE
        binding.shoplistRecommendationsSubtitle.visibility = View.VISIBLE
    }

    private fun handleShopListResult(shopItems: List<ShopItemPartialDTO>) {
        shopListAdapter.updateState(shopItems)
        val itemsInShopListText = shopItems.size.toString() + " ingredientes"
        binding.shoplistIngredientsCount.text = makeSpanText(
            getString(R.string.shoplist_ingredients_count, itemsInShopListText),
            itemsInShopListText,
            ContextCompat.getColor(requireContext(), R.color.secondary)
        )
        binding.shoplistMessage.visibility = View.GONE
        binding.shoplistRecommendationsSubtitle.visibility = View.GONE
        binding.shoplistAddAllToPantryButton.isEnabled = true
        binding.shoplistProductList.visibility = View.VISIBLE
    }

    private fun handleFeedback(feedback: Feedback) {
        when(feedback.feedbackId) {
            FeedbackId.PRODUCT_SEARCH -> {
                handleSearchFeedback(feedback)
            }
            FeedbackId.ADD_ALL_TO_PANTRY -> {
                handleAddAllToPantryFeedback(feedback)
            }
            FeedbackId.SHOPLIST -> {
                handleShopListFeedback(feedback)
            }
            FeedbackId.ADD_TO_SHOPLIST -> {
                handleAddItemFeedback(feedback)
            }
            FeedbackId.RECOMMENDATIONS -> {
                handleRecommendationsFeedback(feedback)
            }
        }
    }

    private fun handleRecommendationsFeedback(feedback: Feedback) {
        binding.shoplistMessage.visibility = View.VISIBLE
        binding.shoplistMessage.text = feedback.message
        binding.shoplistAddAllToPantryButton.isEnabled = false
    }

    private fun handleAddItemFeedback(feedback: Feedback) {
        showToast(feedback.message)
    }

    private fun handleShopListFeedback(feedback: Feedback) {
        binding.shoplistAddAllToPantryButton.isEnabled = false
        when(feedback.code) {
            FeedbackCode.NOT_FOUND -> {
                binding.shoplistIngredientsCount.text = makeSpanText(
                    getString(R.string.shoplist_ingredients_count, "0 ingredientes"),
                    "0 ingredientes",
                    ContextCompat.getColor(requireContext(), R.color.secondary)
                )
            }
            FeedbackCode.UNHANDLED -> {
                showToast(feedback.message)
            }
        }
    }

    private fun handleAddAllToPantryFeedback(feedback: Feedback) {
        showToast(feedback.message)
    }

    private fun handleSearchFeedback(feedback: Feedback) {
        when(feedback.code) {
            FeedbackCode.NOT_FOUND -> {
                binding.shoplistSearchResultList.visibility = View.GONE
            }
            FeedbackCode.UNHANDLED -> {
                showToast(feedback.message)
            }
        }
    }

    private fun setupUiInteractions() {
        binding.shoplistAddAllToPantryButton.setOnClickListener {
            viewModel.addAllItemsFromShopToPantry()
        }
        binding.shopPurchaseHistoryButton.setOnClickListener {
            navigateToShopHistory()
        }
    }

    private fun setupSearchBar() {
        binding.shoplistSearchbarInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.searchProducts(s.toString())
            }
        })
        binding.shoplistSearchbarInput.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                Handler(Looper.getMainLooper()).postDelayed({
                    binding.shoplistSearchResultList.visibility = View.GONE
                    binding.shoplistBlur.visibility = View.GONE
                    binding.shoplistSearchbarInput.text = null
                    searchAdapter.clear()
                }, 50)
            } else {
                binding.shoplistSearchResultList.visibility = View.VISIBLE
                binding.shoplistBlur.visibility = View.VISIBLE
            }
        }
    }

    private fun navigateToProductDetails(itemId: Long) {
        val productItemType = ProductItemType.SHOP_LIST_ITEM
        val action = ShopFragmentDirections.actionShoplistToProductDetails(itemId, productItemType)
        findNavController().navigate(action)
    }

    private fun navigateToShopHistory() {
        val action = ShopFragmentDirections.shoplistToShopHistory()
        findNavController().navigate(action)
    }
}

