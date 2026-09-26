package com.vortexos.assistant

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.vortexos.assistant.databinding.ActivityAssistantBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AssistantActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAssistantBinding
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var dbHelper: ChatDatabaseHelper
    private lateinit var apiClient: GeminiApiClient
    private lateinit var voiceManager: VoiceInputManager
    private lateinit var quickActionHandler: QuickActionHandler
    private var currentEmotion: String = "Neutral"

    private val themeReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == "com.vortexos.weathersync.UPDATE_THEME") {
                val colorStr = intent.getStringExtra("color") ?: "#7C4DFF"
                applyThemeColor(colorStr)
            } else if (intent?.action == "com.vortexos.emotion.ACTION_EMOTION_CHANGED") {
                currentEmotion = intent.getStringExtra("emotion") ?: "Neutral"
                binding.emotionBadge.text = currentEmotion
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAssistantBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = ChatDatabaseHelper(this)
        apiClient = GeminiApiClient()
        quickActionHandler = QuickActionHandler(this)
        
        setupRecyclerView()
        setupVoiceInput()

        binding.btnSend.setOnClickListener {
            val text = binding.etMessage.text.toString()
            if (text.isNotBlank()) {
                sendMessage(text)
                binding.etMessage.text?.clear()
            }
        }

        loadHistory()
        
        val filter = IntentFilter().apply {
            addAction("com.vortexos.weathersync.UPDATE_THEME")
            addAction("com.vortexos.emotion.ACTION_EMOTION_CHANGED")
        }
        registerReceiver(themeReceiver, filter, Context.RECEIVER_NOT_EXPORTED)
    }

    private fun setupRecyclerView() {
        chatAdapter = ChatAdapter()
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@AssistantActivity).apply {
                stackFromEnd = true
            }
            adapter = chatAdapter
        }
    }

    private fun setupVoiceInput() {
        voiceManager = VoiceInputManager(this) { text ->
            sendMessage(text)
        }
        
        binding.btnMic.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), 1)
            } else {
                voiceManager.startListening()
            }
        }
    }

    private fun sendMessage(text: String) {
        val userMsg = ChatMessage(text, true, System.currentTimeMillis())
        chatAdapter.addMessage(userMsg)
        dbHelper.addMessage(userMsg)
        binding.recyclerView.scrollToPosition(chatAdapter.itemCount - 1)

        val handled = quickActionHandler.handleCommand(text)
        if (handled != null) {
            val aiMsg = ChatMessage(handled, false, System.currentTimeMillis())
            chatAdapter.addMessage(aiMsg)
            dbHelper.addMessage(aiMsg)
            binding.recyclerView.scrollToPosition(chatAdapter.itemCount - 1)
            return
        }

        binding.typingIndicator.visibility = View.VISIBLE
        
        CoroutineScope(Dispatchers.IO).launch {
            val response = apiClient.generateResponse(text, currentEmotion)
            withContext(Dispatchers.Main) {
                binding.typingIndicator.visibility = View.GONE
                val aiMsg = ChatMessage(response, false, System.currentTimeMillis())
                chatAdapter.addMessage(aiMsg)
                dbHelper.addMessage(aiMsg)
                binding.recyclerView.scrollToPosition(chatAdapter.itemCount - 1)
            }
        }
    }

    private fun loadHistory() {
        val msgs = dbHelper.getAllMessages()
        chatAdapter.setMessages(msgs)
        if (msgs.isNotEmpty()) {
            binding.recyclerView.scrollToPosition(msgs.size - 1)
        }
    }

    private fun applyThemeColor(colorString: String) {
        try {
            val color = Color.parseColor(colorString)
            binding.btnSend.setColorFilter(color)
            binding.btnMic.setColorFilter(color)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(themeReceiver)
        voiceManager.destroy()
    }
}
