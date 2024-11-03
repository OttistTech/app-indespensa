package com.ottistech.indespensa.ui.recyclerview.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ottistech.indespensa.databinding.CardRecommendationBinding
import com.ottistech.indespensa.ui.helpers.loadImage
import com.ottistech.indespensa.ui.helpers.renderAmount
import com.ottistech.indespensa.webclient.dto.product.ProductDTO

class RecommendationAdapter (
    private val context: Context,
    products: List<ProductDTO> = ArrayList(),
    private val onAdd: (product: ProductDTO) -> Unit
) : RecyclerView.Adapter<RecommendationAdapter.RecommendationViewHolder>() {

    private var productsList : MutableList<ProductDTO> = products.toMutableList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecommendationViewHolder {
        val itemView = CardRecommendationBinding.inflate(
            LayoutInflater.from(context),
            parent,
            false
        )
        return RecommendationViewHolder(itemView)
    }

    override fun getItemCount(): Int = productsList.size

    override fun onBindViewHolder(holder: RecommendationViewHolder, position: Int) {
        val product = productsList[position]
        holder.bind(product)
    }

    fun updateState(products: List<ProductDTO>) {
        productsList.clear()
        productsList.addAll(products)
        notifyDataSetChanged()
    }

    inner class RecommendationViewHolder(
        private val binding: CardRecommendationBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        private lateinit var product: ProductDTO

        init {
            binding.cardRecommendationButton.setOnClickListener {
                onAdd(product)
            }
        }

        fun bind(product: ProductDTO) {
            this.product = product
            with(binding) {
                cardRecommendationImage.loadImage(product.imageUrl)
                cardRecommendationName.text = product.name
                cardRecommendationBrand.text = product.brandName
                if(product.amount != null && product.unit != null) {
                    cardRecommendationAmount.renderAmount(product.amount, product.unit)
                }
                cardRecommendationDescription.text = product.description
            }
        }
    }
}