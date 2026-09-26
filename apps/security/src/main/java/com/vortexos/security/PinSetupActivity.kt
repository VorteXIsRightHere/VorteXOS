package com.vortexos.security

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.vortexos.security.databinding.ActivityPinSetupBinding
import java.security.MessageDigest

class PinSetupActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPinSetupBinding
    private var currentPin = ""
    private var firstPin = ""
    private var isConfirming = false
    
    private lateinit var encryptedPrefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPinSetupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
        encryptedPrefs = EncryptedSharedPreferences.create(
            "secure_pin_prefs",
            masterKeyAlias,
            this,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

        setupKeypad()
        updateUI()
    }

    private fun setupKeypad() {
        val buttons = listOf(
            binding.btn0, binding.btn1, binding.btn2, binding.btn3,
            binding.btn4, binding.btn5, binding.btn6, binding.btn7,
            binding.btn8, binding.btn9
        )

        buttons.forEachIndexed { index, button ->
            button.setOnClickListener {
                if (currentPin.length < 4) {
                    currentPin += if (index == 0) "0" else index.toString()
                    updateDots()
                }
            }
        }

        binding.btnBack.setOnClickListener {
            if (currentPin.isNotEmpty()) {
                currentPin = currentPin.dropLast(1)
                updateDots()
            }
        }

        binding.btnConfirm.setOnClickListener {
            if (currentPin.length == 4) {
                if (!isConfirming) {
                    firstPin = currentPin
                    currentPin = ""
                    isConfirming = true
                    updateUI()
                    updateDots()
                } else {
                    if (currentPin == firstPin) {
                        savePin(currentPin)
                        Toast.makeText(this, R.string.pin_saved, Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this, R.string.pin_mismatch, Toast.LENGTH_SHORT).show()
                        currentPin = ""
                        updateDots()
                    }
                }
            }
        }
    }

    private fun updateUI() {
        binding.textTitle.setText(if (isConfirming) R.string.confirm_pin else R.string.enter_pin)
    }

    private fun updateDots() {
        val dots = listOf(binding.dot1, binding.dot2, binding.dot3, binding.dot4)
        for (i in dots.indices) {
            dots[i].isSelected = i < currentPin.length
        }
    }

    private fun savePin(pin: String) {
        val hashedPin = hashPin(pin)
        encryptedPrefs.edit().putString("pin_hash", hashedPin).apply()
    }

    private fun hashPin(pin: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(pin.toByteArray())
        return digest.fold("") { str, it -> str + "%02x".format(it) }
    }
}
