package com.vortexos.security

import android.app.Activity
import android.content.SharedPreferences
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.vortexos.security.databinding.ActivityPinVerifyBinding
import java.security.MessageDigest

class PinVerifyActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPinVerifyBinding
    private var currentPin = ""
    private var attempts = 0
    private var lockoutTime = 0L

    private lateinit var encryptedPrefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPinVerifyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
        encryptedPrefs = EncryptedSharedPreferences.create(
            "secure_pin_prefs",
            masterKeyAlias,
            this,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

        lockoutTime = encryptedPrefs.getLong("lockout_time", 0L)
        if (System.currentTimeMillis() < lockoutTime) {
            startLockoutTimer(lockoutTime - System.currentTimeMillis())
        }

        setupKeypad()
    }

    private fun setupKeypad() {
        val buttons = listOf(
            binding.btn0, binding.btn1, binding.btn2, binding.btn3,
            binding.btn4, binding.btn5, binding.btn6, binding.btn7,
            binding.btn8, binding.btn9
        )

        buttons.forEachIndexed { index, button ->
            button.setOnClickListener {
                if (System.currentTimeMillis() < lockoutTime) return@setOnClickListener
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
            if (System.currentTimeMillis() < lockoutTime) return@setOnClickListener
            if (currentPin.length == 4) {
                verifyPin(currentPin)
            }
        }
    }

    private fun updateDots() {
        val dots = listOf(binding.dot1, binding.dot2, binding.dot3, binding.dot4)
        for (i in dots.indices) {
            dots[i].isSelected = i < currentPin.length
        }
    }

    private fun verifyPin(pin: String) {
        val storedHash = encryptedPrefs.getString("pin_hash", null)
        if (storedHash == null) {
            // No PIN set, allow access
            setResult(Activity.RESULT_OK)
            finish()
            return
        }

        if (hashPin(pin) == storedHash) {
            attempts = 0
            setResult(Activity.RESULT_OK)
            finish()
        } else {
            attempts++
            currentPin = ""
            updateDots()
            if (attempts >= 3) {
                lockoutTime = System.currentTimeMillis() + 30000
                encryptedPrefs.edit().putLong("lockout_time", lockoutTime).apply()
                startLockoutTimer(30000)
            } else {
                Toast.makeText(this, R.string.wrong_pin, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun startLockoutTimer(durationMillis: Long) {
        binding.textTitle.text = getString(R.string.lockout_message)
        object : CountDownTimer(durationMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                binding.textTitle.text = "Locked. Try again in ${millisUntilFinished / 1000}s"
            }

            override fun onFinish() {
                attempts = 0
                lockoutTime = 0L
                binding.textTitle.setText(R.string.verify_pin)
            }
        }.start()
    }

    private fun hashPin(pin: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(pin.toByteArray())
        return digest.fold("") { str, it -> str + "%02x".format(it) }
    }
}
