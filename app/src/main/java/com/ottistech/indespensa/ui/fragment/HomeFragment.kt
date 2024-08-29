package com.ottistech.indespensa.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ottistech.indespensa.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private lateinit var binding : FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // TODO: Transfer this code to the pantry item form screen
//        parentFragmentManager.setFragmentResultListener(UiConstants.SCANNER_REQUEST_CODE, this) { _, bundle ->
//            val result : ProductResponseDTO? =
//                bundle.getSerializable(UiConstants.SCANNER_RESULT_KEY) as ProductResponseDTO?
//
//            // Do what you have to do with response
//            binding.resultText.text = result.toString()
//        }
//
//        binding.butao.setOnClickListener {
//            val action = HomeFragmentDirections.actionHomeToScanner()
//            findNavController().navigate(action)
//        }
    }
}