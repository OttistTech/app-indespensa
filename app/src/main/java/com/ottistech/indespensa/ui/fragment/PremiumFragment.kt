package com.ottistech.indespensa.ui.fragment

import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.ottistech.indespensa.R
import com.ottistech.indespensa.data.repository.UserRepository
import com.ottistech.indespensa.databinding.FragmentPremiumBinding
import com.ottistech.indespensa.ui.dialog.PaymentDialogCreator
import com.ottistech.indespensa.ui.recyclerview.adapter.TextCarouselAdapter
import com.ottistech.indespensa.ui.viewmodel.PremiumViewModel

class PremiumFragment : Fragment() {

    private lateinit var binding: FragmentPremiumBinding
    private lateinit var viewModel: PremiumViewModel
    private lateinit var dialogCreator : PaymentDialogCreator

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPremiumBinding.inflate(inflater, container, false)
        dialogCreator = PaymentDialogCreator(requireContext())
        viewModel = PremiumViewModel(UserRepository(requireContext()))

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.carouselItems.observe(viewLifecycleOwner) { carouselItems ->
            val carouselAdapter = TextCarouselAdapter(carouselItems)
            binding.premiumViewPager.adapter = carouselAdapter
            binding.premiumDotsIndicator.setViewPager2(binding.premiumViewPager)
        }

        viewModel.nextCarouselItem.observe(viewLifecycleOwner) { nextItem ->
            binding.premiumViewPager.currentItem = nextItem
        }

        viewModel.currentPrice.observe(viewLifecycleOwner) { currentPrice ->
            binding.premiumPriceCurrent.text = getString(R.string.premium_price_current, currentPrice)
        }

        binding.premiumPriceOld.paintFlags = binding.premiumPriceOld.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG

        val paymentDialogCreator = PaymentDialogCreator(requireContext())
        binding.premiumGoToPaymentButton.setOnClickListener {
            paymentDialogCreator.showPaymentDialog("R$${viewModel.currentPrice.value}") {
                viewModel.handlePaymentClick(
                    onSuccess = {
                        Toast.makeText(requireContext(), "Agora você é um Premium", Toast.LENGTH_SHORT).show()
                        navigateToHome()
                    },
                    onError = { message ->
                        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                        navigateToHome()
                    }
                )
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.onCleared()
    }

    private fun navigateToHome() {
        val action = PremiumFragmentDirections.premiumToHome()
        findNavController().navigate(action)
    }
}
