package com.vortexos.calculator

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.vortexos.calculator.databinding.ActivityCalculatorBinding

class CalculatorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCalculatorBinding
    private val engine = CalculatorEngine()
    private var isScientific = false
    private lateinit var prefs: SharedPreferences

    private val themeReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            // Recreate to apply new theme color if ACTION_WEATHER_CHANGED or ACTION_EMOTION_CHANGED is received
            recreate()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCalculatorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefs = getSharedPreferences("calc_history", Context.MODE_PRIVATE)

        setupButtons()
        
        val filter = IntentFilter().apply {
            addAction("com.vortexos.common.VorteXConstants.ACTION_WEATHER_CHANGED")
            addAction("com.vortexos.common.VorteXConstants.ACTION_EMOTION_CHANGED")
        }
        registerReceiver(themeReceiver, filter)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(themeReceiver)
    }

    private fun setupButtons() {
        val buttons = listOf(
            binding.btn0, binding.btn1, binding.btn2, binding.btn3, binding.btn4,
            binding.btn5, binding.btn6, binding.btn7, binding.btn8, binding.btn9,
            binding.btnDot, binding.btnAdd, binding.btnSub, binding.btnMul, binding.btnDiv,
            binding.btnOpenParen, binding.btnCloseParen, binding.btnSin, binding.btnCos,
            binding.btnTan, binding.btnLog, binding.btnLn, binding.btnSqrt, binding.btnPow
        )

        for (btn in buttons) {
            btn?.setOnClickListener {
                val b = it as Button
                binding.tvExpression.append(b.text)
                evaluateRealTime()
            }
        }

        binding.btnClear.setOnClickListener {
            binding.tvExpression.text = ""
            binding.tvResult.text = ""
        }

        binding.btnDel.setOnClickListener {
            val expr = binding.tvExpression.text.toString()
            if (expr.isNotEmpty()) {
                binding.tvExpression.text = expr.dropLast(1)
                evaluateRealTime()
            }
        }

        binding.btnEqual.setOnClickListener {
            val expr = binding.tvExpression.text.toString()
            if (expr.isNotEmpty()) {
                val result = engine.evaluate(expr)
                binding.tvResult.text = result
                saveHistory(expr, result)
            }
        }
        
        binding.btnHistory?.setOnClickListener {
            // Show history dialog (simplified for this generation)
        }
    }

    private fun evaluateRealTime() {
        val expr = binding.tvExpression.text.toString()
        if (expr.isNotEmpty()) {
            val res = engine.evaluate(expr)
            if (res != "Error") {
                binding.tvResult.text = res
            } else {
                binding.tvResult.text = ""
            }
        } else {
            binding.tvResult.text = ""
        }
    }

    private fun saveHistory(expr: String, res: String) {
        val historyCount = prefs.getInt("count", 0)
        prefs.edit()
            .putString("expr_$historyCount", expr)
            .putString("res_$historyCount", res)
            .putInt("count", historyCount + 1)
            .apply()
    }
}
