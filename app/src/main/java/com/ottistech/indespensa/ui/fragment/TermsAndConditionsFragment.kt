package com.ottistech.indespensa.ui.fragment

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.ottistech.indespensa.R
import com.ottistech.indespensa.databinding.FragmentTermsAndConditionsBinding
import com.ottistech.indespensa.shared.AppConstants

class TermsAndConditionsFragment : Fragment() {

    private lateinit var binding : FragmentTermsAndConditionsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTermsAndConditionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupBackButton()
        loadTermsAndConditions()
    }

    private fun setupBackButton() {
        binding.appTermsBack.setOnClickListener {
            popupBackStack()
        }
    }

    private fun popupBackStack() {
        findNavController().popBackStack(R.id.terms_dest, true)
    }

    private fun loadTermsAndConditions() {
        binding.appTermsWebview.apply {
            webViewClient = object : WebViewClient() {
                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    binding.appTermsProgressbar.visibility = View.VISIBLE
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    binding.appTermsProgressbar.visibility = View.GONE
                }
            }
            loadUrl(AppConstants.TERMS_AND_CONDITIONS_URL)
        }
    }
}