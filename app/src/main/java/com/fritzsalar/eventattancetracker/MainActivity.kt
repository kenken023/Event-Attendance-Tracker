package com.fritzsalar.eventattancetracker

import android.Manifest
import android.content.Intent
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.android.synthetic.main.activity_main.*
import android.R.attr.data
import android.R.attr.textViewStyle
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.zxing.ResultPoint
import com.google.zxing.integration.android.IntentResult
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import android.media.RingtoneManager
import android.media.Ringtone




class MainActivity : AppCompatActivity() {
    private var code: String = ""
    private var isScanning: Boolean = false


    private lateinit var textMessage: TextView
    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                textMessage.setText(R.string.title_home)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_dashboard -> {
                textMessage.setText(R.string.title_dashboard)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_notifications -> {
                textMessage.setText(R.string.title_notifications)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        textMessage = findViewById(R.id.message)
        navView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)

        btn_scan.setOnClickListener {
//            val integrator = IntentIntegrator(this)
//            integrator.setPrompt("Scan a barcode")
//            integrator.initiateScan()
            if (isScanning) {
                dbv_barcode.barcodeView.pause()
                btn_scan.text = "Start Scanning"
            } else {
                dbv_barcode.barcodeView.resume()
                btn_scan.text = "Pause Scanning"
            }

            isScanning = !isScanning
        }

        requestPermission()

        dbv_barcode.barcodeView.decodeContinuous(object: BarcodeCallback {
            override fun barcodeResult(result: BarcodeResult) {

                if (code != result.text) {
                    message.text = result.text
                    code = result.text
                    beepSound()
                }
            }

            override fun possibleResultPoints(resultPoints: List<ResultPoint>) {

            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)

        if (result != null) {
            if (result.contents == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Scanned: " + result.contents, Toast.LENGTH_LONG).show()
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onResume() {
        super.onResume()

        dbv_barcode.barcodeView.resume()
    }

    override fun onPause() {
        super.onPause()

        dbv_barcode.barcodeView.pause()
    }

    protected fun beepSound() {
        try {
            val notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val r = RingtoneManager.getRingtone(applicationContext, notification)
            r.play()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun requestPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 0)
        }
    }
}
