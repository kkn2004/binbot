package com.example.wastemanagementapp

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.zxing.integration.android.IntentIntegrator

class MainActivity : AppCompatActivity() {

    private lateinit var btnScan: Button
    private lateinit var txtResult: TextView

    // Sample mapping of product barcodes to waste category
    private val wasteCategoryMap = mapOf(
        "0123456789012" to "Recyclable Waste",
        "978020137962" to "Organic Waste",
        "4006381333931" to "Hazardous Waste",
        "5012345678900" to "General Waste"
        // Add more sample barcodes as needed
    )

    private val cameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            startScanning()
        } else {
            Toast.makeText(this, "Camera permission denied", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnScan = findViewById(R.id.btnScan)
        txtResult = findViewById(R.id.txtResult)

        btnScan.setOnClickListener {
            checkPermissionAndScan()
        }
    }

    private fun checkPermissionAndScan() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                startScanning()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) -> {
                Toast.makeText(
                    this,
                    "Camera permission is needed to scan barcodes",
                    Toast.LENGTH_LONG
                ).show()
                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
            else -> {
                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    private fun startScanning() {
        val integrator = IntentIntegrator(this)
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES)
        integrator.setPrompt("Scan a product barcode")
        integrator.setCameraId(0)  // Use default camera
        integrator.setBeepEnabled(true)
        integrator.setBarcodeImageEnabled(false)
        integrator.initiateScan()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: android.content.Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) {
                Toast.makeText(this, "Scan cancelled", Toast.LENGTH_LONG).show()
            } else {
                val barcode = result.contents
                val wasteType = wasteCategoryMap[barcode] ?: "Unknown Waste Category"
                txtResult.text = "Scanned Product Barcode:\n$barcode\n\nWaste Category:\n$wasteType"
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
}
