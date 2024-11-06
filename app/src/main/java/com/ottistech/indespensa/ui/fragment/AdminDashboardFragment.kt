package com.ottistech.indespensa.ui.fragment

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import com.ottistech.indespensa.databinding.FragmentAdminDashboardBinding
import com.ottistech.indespensa.shared.AppConstants

class AdminDashboardFragment : Fragment() {

    private lateinit var binding: FragmentAdminDashboardBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAdminDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupWebView()
    }

    private fun setupWebView() {
        binding.adminDashboardWebview.apply {
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            webViewClient = object : WebViewClient() {
                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    binding.adminDashboardProgressbar.visibility = View.VISIBLE
                }
                override fun onPageFinished(view: WebView?, url: String?) {
                    binding.adminDashboardProgressbar.visibility = View.GONE
                }
            }
            loadUrl(AppConstants.EXPOTECH_DASHBOARD_URL)
        }
    }

}