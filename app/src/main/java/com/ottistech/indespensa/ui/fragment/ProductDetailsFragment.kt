package com.ottistech.indespensa.ui.fragment

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.ottistech.indespensa.R
import com.ottistech.indespensa.data.repository.PantryRepository
import com.ottistech.indespensa.data.repository.ShopRepository
import com.ottistech.indespensa.databinding.FragmentProductDetailsBinding
import com.ottistech.indespensa.shared.ProductItemType
import com.ottistech.indespensa.ui.dialog.DatePickerCreator
import com.ottistech.indespensa.shared.loadImage
import com.ottistech.indespensa.shared.renderAmount
import com.ottistech.indespensa.shared.renderValidityDate
import com.ottistech.indespensa.shared.showToast
import com.ottistech.indespensa.ui.model.feedback.Feedback
import com.ottistech.indespensa.ui.model.feedback.FeedbackCode
import com.ottistech.indespensa.ui.model.feedback.FeedbackId
import com.ottistech.indespensa.ui.viewmodel.PantryItemDetailsViewModel
import com.ottistech.indespensa.ui.viewmodel.ProductItemDetailsViewModel
import com.ottistech.indespensa.ui.viewmodel.ShopItemDetailsViewModel
import com.ottistech.indespensa.webclient.dto.pantry.PantryItemDetailsDTO
import com.ottistech.indespensa.webclient.dto.product.ProductItemDetailsDTO
import com.ottistech.indespensa.webclient.dto.shoplist.ShopItemDetailsDTO
import java.util.Date

class ProductDetailsFragment : Fragment() {

    private lateinit var binding : FragmentProductDetailsBinding
    private val args : ProductDetailsFragmentArgs by navArgs<ProductDetailsFragmentArgs>()
    private lateinit var viewModel : ProductItemDetailsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProductDetailsBinding.inflate(inflater, container, false)
        setupForProductType()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObservers()
        setupUiInteractions()
        setupBackButton()
    }

    override fun onResume() {
        super.onResume()
        loadProductItem()
    }

    override fun onStop() {
        super.onStop()
        viewModel.syncChanges()
    }

    private fun setupUiInteractions() {
        binding.productDetailsDescription.movementMethod = ScrollingMovementMethod()

        binding.productDetailsAddButton.setOnClickListener {
            viewModel.registerAmountChange(1)
        }
        binding.productDetailsRemoveButton.setOnClickListener {
            viewModel.registerAmountChange(-1)
        }
        binding.productDetailsAddToButton.setOnClickListener {
            when(viewModel) {
                is PantryItemDetailsViewModel -> {
                    (viewModel as PantryItemDetailsViewModel).addToShopList()
                }
                is ShopItemDetailsViewModel -> {
                    val datePicker = DatePickerCreator().createDatePicker(label = "Selecione a data de validade", dateBackWards = false)

                    datePicker.show(parentFragmentManager, "date_picker")

                    datePicker.addOnPositiveButtonClickListener {
                        val selectedDate = Date(it)
                        (viewModel as ShopItemDetailsViewModel).addToPantry(selectedDate)
                    }
                }
            }
        }
    }

    private fun showLoad() {
        binding.productDetailsContent.visibility = View.GONE
        binding.pantryDetailsProgressbar.visibility = View.VISIBLE
    }

    private fun showContent() {
        binding.productDetailsContent.visibility = View.VISIBLE
        binding.pantryDetailsProgressbar.visibility = View.GONE
    }

    private fun renderProductItem(itemDetails: ProductItemDetailsDTO) {
        with(binding) {
            productDetailsImage.loadImage(itemDetails.productImageUrl)
            productDetailsFood.text = itemDetails.productFood
            productDetailsTitle.text = itemDetails.productName
            productDetailsBrand.text = itemDetails.productBrand
            productDetailsDescription.text = itemDetails.productDescription
            productDetailsAmount.text = itemDetails.amount.toString()
            productDetailsProductAmount.renderAmount(
                itemDetails.productAmount,
                itemDetails.productUnit
            )
            if(itemDetails is PantryItemDetailsDTO) {
                if (itemDetails.validityDate != null) {
                    productDetailsValidityDate.renderValidityDate(itemDetails.validityDate)
                } else {
                    productDetailsValidityDateContainer.visibility = View.GONE
                }

                if (itemDetails.wasOpened) productDetailsWasOpenedContainer.visibility = View.VISIBLE
            }
        }
        showContent()
    }

    private fun setupObservers() {
        viewModel.itemDetails.observe(viewLifecycleOwner) { itemDetails ->
            if (itemDetails is PantryItemDetailsDTO) {
                renderProductItem(itemDetails)
            } else if (itemDetails is ShopItemDetailsDTO) {
                renderProductItem(itemDetails)
            }
        }
        viewModel.feedback.observe(viewLifecycleOwner) { feedback ->
            feedback?.let {
                handleFeedback(it)
            }
        }
        viewModel.itemAmount.observe(viewLifecycleOwner) { amount ->
            binding.productDetailsAmount.text = amount.toString()
            binding.productDetailsRemoveButton.isEnabled = amount > 0
        }
    }

    private fun loadProductItem() {
        viewModel.getItemDetails(args.itemId)
        showLoad()
    }

    private fun handleFeedback(feedback: Feedback) {
        showToast(feedback.message)
        when(feedback.feedbackId) {
            FeedbackId.ADD_TO_SHOPLIST -> {
                if(feedback.code == FeedbackCode.SUCCESS) { popBackStack() }
            }
            FeedbackId.ADD_TO_PANTRY -> {
                if(feedback.code == FeedbackCode.SUCCESS) { popBackStack() }
            }
            FeedbackId.GET_ITEM_DETAILS -> popBackStack()
        }
    }

    private fun setupBackButton() {
        binding.productDetailsBack.setOnClickListener {
            popBackStack()
        }
    }

    private fun popBackStack() {
        findNavController().popBackStack(R.id.product_details_dest, true)
    }

    private fun setupForProductType() {
        when(args.itemType) {
            ProductItemType.PANTRY_ITEM -> {
                binding.productDetailsAddToButton.text = getString(R.string.add_to_shop_list)
                binding.productDetailsAmountLabel.text = getString(R.string.label_amount_pantry)
                viewModel = PantryItemDetailsViewModel(
                    PantryRepository(requireContext()),
                    ShopRepository(requireContext())
                )
            }
            ProductItemType.SHOP_LIST_ITEM -> {
                binding.productDetailsAddToButton.text = getString(R.string.add_to_pantry)
                binding.productDetailsAmountLabel.text = getString(R.string.label_amount_shoplist)
                binding.productDetailsValidityDateContainer.visibility = View.GONE
                viewModel = ShopItemDetailsViewModel(
                    ShopRepository(requireContext()),
                    PantryRepository(requireContext())
                )
            }
        }
    }
}