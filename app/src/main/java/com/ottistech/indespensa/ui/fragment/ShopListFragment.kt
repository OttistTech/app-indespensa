package com.ottistech.indespensa.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.ottistech.indespensa.databinding.FragmentShoplistBinding
import com.ottistech.indespensa.ui.recyclerview.adapter.ShopListAdapter
import com.ottistech.indespensa.webclient.dto.ShopListIngredientResponseDTO

class ShopListFragment : Fragment() {

    private lateinit var binding : FragmentShoplistBinding
    private lateinit var shopListAdapter: ShopListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentShoplistBinding.inflate(inflater, container, false)

        setupRecyclerView()

        loadData()

        return binding.root
    }

    private fun setupRecyclerView() {
        shopListAdapter = ShopListAdapter(emptyList())

        binding.shoplistRecyclerview.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = shopListAdapter
        }
    }

    private fun loadData() {
        val mockData = listOf(
            ShopListIngredientResponseDTO(
                listItemId = 31,
                userId = 11,
                productName = "Arroz",
                imageUrl = "https://example.com/arroz.jpg",
                amount = 12,
                productAmount = 1.00,
                productUnit = "kg"
            ),
            ShopListIngredientResponseDTO(
                listItemId = 34,
                userId = 11,
                productName = "Feijão",
                imageUrl = "https://example.com/feijao.jpg",
                amount = 10,
                productAmount = 1.00,
                productUnit = "kg"
            ),
            ShopListIngredientResponseDTO(
                listItemId = 34,
                userId = 11,
                productName = "Feijão",
                imageUrl = "https://example.com/feijao.jpg",
                amount = 10,
                productAmount = 1.00,
                productUnit = "kg"
            ),
            ShopListIngredientResponseDTO(
                listItemId = 34,
                userId = 11,
                productName = "Feijão",
                imageUrl = "https://example.com/feijao.jpg",
                amount = 10,
                productAmount = 1.00,
                productUnit = "kg"
            ),
            ShopListIngredientResponseDTO(
                listItemId = 34,
                userId = 11,
                productName = "Feijão",
                imageUrl = "https://example.com/feijao.jpg",
                amount = 10,
                productAmount = 1.00,
                productUnit = "kg"
            ),
            ShopListIngredientResponseDTO(
                listItemId = 34,
                userId = 11,
                productName = "Feijão",
                imageUrl = "https://example.com/feijao.jpg",
                amount = 10,
                productAmount = 1.00,
                productUnit = "kg"
            ),
            ShopListIngredientResponseDTO(
                listItemId = 34,
                userId = 11,
                productName = "Feijão",
                imageUrl = "https://example.com/feijao.jpg",
                amount = 10,
                productAmount = 1.00,
                productUnit = "kg"
            ),
            ShopListIngredientResponseDTO(
                listItemId = 34,
                userId = 11,
                productName = "Feijão",
                imageUrl = "https://example.com/feijao.jpg",
                amount = 10,
                productAmount = 1.00,
                productUnit = "kg"
            ),
            ShopListIngredientResponseDTO(
                listItemId = 34,
                userId = 11,
                productName = "Feijão",
                imageUrl = "https://example.com/feijao.jpg",
                amount = 10,
                productAmount = 1.00,
                productUnit = "kg"
            ),
            ShopListIngredientResponseDTO(
                listItemId = 34,
                userId = 11,
                productName = "Feijão",
                imageUrl = "https://example.com/feijao.jpg",
                amount = 10,
                productAmount = 1.00,
                productUnit = "kg"
            )
        )

        shopListAdapter = ShopListAdapter(mockData)
        binding.shoplistRecyclerview.adapter = shopListAdapter
    }
}