package com.ottistech.indespensa.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.OptIn
import androidx.camera.core.AspectRatio
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import com.ottistech.indespensa.R
import com.ottistech.indespensa.data.exception.ResourceNotFoundException
import com.ottistech.indespensa.data.repository.ProductRepository
import com.ottistech.indespensa.databinding.FragmentScannerBinding
import com.ottistech.indespensa.ui.helpers.PermissionManager
import com.ottistech.indespensa.ui.helpers.showToast
import com.ottistech.indespensa.ui.UiConstants
import com.ottistech.indespensa.webclient.dto.ProductResponseDTO
import kotlinx.coroutines.launch
import java.util.concurrent.Executor

class ScannerFragment : Fragment() {

    private val TAG = "SCANNER FRAGMENT"
    private lateinit var binding : FragmentScannerBinding
    private lateinit var barcodeScanner : BarcodeScanner
    private lateinit var permissionManager: PermissionManager
    private val permissions = arrayOf(
        android.Manifest.permission.CAMERA
    )
    private var barcodeFound : String? = null
    private val repository = ProductRepository()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        permissionManager = PermissionManager(
            fragment = this,
            permissions = permissions,
            onRequestSuccess = { startScan() },
            onRequestFailure = { showToast("Permission is required") }
        )

        binding = FragmentScannerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(permissionManager.checkPermissions()) {
            startScan()
        } else {
            permissionManager.requestPermissions()
        }

        binding.scannerCancel.setOnClickListener {
            giveFragmentResultBack(null)
        }
    }

    private fun startScan() {
        val context = requireContext()
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        val executor = ContextCompat.getMainExecutor(context)
        cameraProviderFuture.addListener ({
            try {
                barcodeScanner = BarcodeScanning.getClient()
                val cameraProvider = cameraProviderFuture.get()
                val activity = requireActivity()
                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                val preview = configureImagePreview()
                val imageAnalysis = configureImageAnalysis(executor)

                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    activity,
                    cameraSelector,
                    preview,
                    imageAnalysis
                )
            } catch(e: Exception) {
                Log.e(TAG, "[startScan] Error while starting camera:", e)
            }
        }, executor)
    }

    @OptIn(ExperimentalGetImage::class)
    private fun analyseImage(image: ImageProxy) {
        val inputImage = InputImage.fromMediaImage(
            image.image!!,
            image.imageInfo.rotationDegrees
        )

        barcodeScanner.process(inputImage).addOnSuccessListener { barcodeResult ->
            for (barcode in barcodeResult) {
                barcode.rawValue?.let {
                    if(barcodeFound == null) {
                        barcodeFound = it
                        lifecycleScope.launch {
                            val product = searchProduct(barcodeFound!!)
                            giveFragmentResultBack(product)
                        }
                    }
                }
            }
            image.close()
        }
        .addOnFailureListener { e ->
            Log.e(TAG, "[analyseImage] Error processing barcode: ", e)
        }
    }

    private fun giveFragmentResultBack(product: ProductResponseDTO?) {
        val resultData = Bundle().apply {
            putSerializable(UiConstants.SCANNER_RESULT_KEY, product)
        }
        setFragmentResult(UiConstants.SCANNER_REQUEST_CODE, resultData)
        findNavController().popBackStack(R.id.nav_home, true)
    }

    private suspend fun searchProduct(barcode: String) : ProductResponseDTO? {
        return try {
            val product = repository.findByBarcode(barcode)
            Log.d(TAG, "[analyseImage] Product found: $product")
            product
        } catch (e: ResourceNotFoundException) {
            Log.e(TAG, "[analyseImage] Product not found", e)
            showToast(getString(R.string.result_not_found, "Produto"))
            null
        } catch (e: Exception) {
            Log.e(TAG, "[analyseImage] Error while looking for product by barcode", e)
            showToast(getString(R.string.result_not_found, "Produto"))
            null
        }
    }

    private fun configureImageAnalysis(executor: Executor): ImageAnalysis {
        return ImageAnalysis.Builder()
            .setTargetAspectRatio(AspectRatio.RATIO_16_9)
            .setOutputImageRotationEnabled(true)
            .build()
            .also {
                it.setAnalyzer(executor) { image ->
                    analyseImage(image)
                }
            }
    }

    private fun configureImagePreview(): Preview {
        return Preview.Builder()
            .build()
            .also {
                it.setSurfaceProvider(binding.scannerPreview.surfaceProvider)
            }
    }
}