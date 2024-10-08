package com.ottistech.indespensa.ui.recyclerview.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ottistech.indespensa.databinding.CardProductSearchBinding
import com.ottistech.indespensa.ui.helpers.loadImage
import com.ottistech.indespensa.webclient.dto.product.ProductSearchResponseDTO

class ProductSearchAdapter (
    private val context: Context,
    products: List<ProductSearchResponseDTO> = ArrayList(),
    private val onItemClick: (product: ProductSearchResponseDTO) -> Unit
) : RecyclerView.Adapter<ProductSearchAdapter.ProductSearchViewHolder>() {

    private var productsList : MutableList<ProductSearchResponseDTO> = products.toMutableList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductSearchViewHolder {
        val itemView = CardProductSearchBinding.inflate(
            LayoutInflater.from(context),
            parent,
            false
        )

        return ProductSearchViewHolder(itemView)
    }

    override fun getItemCount(): Int = productsList.size

    override fun onBindViewHolder(holder: ProductSearchViewHolder, position: Int) {
        val product = productsList[position]
        holder.bind(product)
    }

    fun updateState(products: List<ProductSearchResponseDTO>) {
        productsList.clear()
        productsList.addAll(products)
        notifyDataSetChanged()
    }

    fun clear() {
        productsList.clear()
        notifyDataSetChanged()
    }

    inner class ProductSearchViewHolder(
        private val binding: CardProductSearchBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        private lateinit var product: ProductSearchResponseDTO

        init {
            itemView.setOnClickListener {
                onItemClick(product)
            }
        }

        fun bind(product: ProductSearchResponseDTO) {
            this.product = product
            with(binding) {
                cardProductSearchImage.loadImage(product.imageUrl)
                cardProductSearchName.text = product.productName
            }
        }
    }
}