package com.ottistech.indespensa.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.ottistech.indespensa.R
import com.ottistech.indespensa.data.repository.ShopListRepository
import com.ottistech.indespensa.data.repository.UserRepository
import com.ottistech.indespensa.databinding.FragmentShoplistBinding
import com.ottistech.indespensa.ui.recyclerview.adapter.ShopListAdapter
import kotlinx.coroutines.launch

class ShopListFragment : Fragment() {

    private lateinit var binding: FragmentShoplistBinding
    private lateinit var shopListAdapter: ShopListAdapter
    private lateinit var shopListRepository: ShopListRepository
    private lateinit var userRepository: UserRepository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentShoplistBinding.inflate(inflater, container, false)
        shopListRepository = ShopListRepository()
        userRepository = UserRepository(requireContext())
        setupRecyclerView()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadData()
    }

    private fun setupRecyclerView() {
        shopListAdapter = ShopListAdapter(emptyList())

        binding.shoplistRecyclerview.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = shopListAdapter
        }
    }

    private fun loadData() {
        lifecycleScope.launch {
            val currentUser = userRepository.getUserCredentials()
            val shopItems = shopListRepository.listShopItems(currentUser.userId)

            binding.shoplistQuantityIngredients.text = getString(R.string.shoplist_ingredients_count, shopItems.size)

            shopListAdapter = ShopListAdapter(shopItems)
            binding.shoplistRecyclerview.adapter = shopListAdapter
        }
    }
}

