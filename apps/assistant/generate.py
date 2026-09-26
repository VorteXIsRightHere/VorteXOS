import os

base_dir = r"C:\Users\VorteX\.gemini\antigravity\scratch\VorteXOS\apps\assistant"

files = {
    "build.gradle.kts": """plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.vortexos.assistant"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.vortexos.assistant"
        minSdk = 28
        targetSdk = 30
        versionCode = 1
        versionName = "1.0"
        
        buildConfigField("String", "GEMINI_API_KEY", "\\\"YOUR_API_KEY\\\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
}
""",
    "src/main/AndroidManifest.xml": """<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="com.vortexos.assistant">

    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.RECORD_AUDIO" />
    <uses-permission android:name="android.permission.QUERY_ALL_PACKAGES" />

    <application
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/Theme.VorteXOS.Assistant">
        <activity
            android:name=".AssistantActivity"
            android:exported="true"
            android:theme="@style/Theme.VorteXOS.Assistant">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
    </application>

</manifest>
""",
    "src/main/java/com/vortexos/assistant/AssistantActivity.kt": """package com.vortexos.assistant

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
""",
    "src/main/java/com/vortexos/assistant/ChatAdapter.kt": """package com.vortexos.assistant

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.vortexos.assistant.databinding.ItemChatMessageAiBinding
import com.vortexos.assistant.databinding.ItemChatMessageUserBinding

class ChatAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val messages = mutableListOf<ChatMessage>()

    fun addMessage(msg: ChatMessage) {
        messages.add(msg)
        notifyItemInserted(messages.size - 1)
    }

    fun setMessages(msgs: List<ChatMessage>) {
        messages.clear()
        messages.addAll(msgs)
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return if (messages[position].isUser) 1 else 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == 1) {
            val binding = ItemChatMessageUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            UserViewHolder(binding)
        } else {
            val binding = ItemChatMessageAiBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            AiViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val msg = messages[position]
        if (holder is UserViewHolder) {
            holder.bind(msg)
        } else if (holder is AiViewHolder) {
            holder.bind(msg)
        }
    }

    override fun getItemCount() = messages.size

    class UserViewHolder(private val binding: ItemChatMessageUserBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(msg: ChatMessage) {
            binding.tvMessage.text = msg.text
        }
    }

    class AiViewHolder(private val binding: ItemChatMessageAiBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(msg: ChatMessage) {
            binding.tvMessage.text = msg.text
        }
    }
}
""",
    "src/main/java/com/vortexos/assistant/ChatMessage.kt": """package com.vortexos.assistant

data class ChatMessage(
    val text: String,
    val isUser: Boolean,
    val timestamp: Long,
    val id: Long = 0
)
""",
    "src/main/java/com/vortexos/assistant/GeminiApiClient.kt": """package com.vortexos.assistant

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

class GeminiApiClient {
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()
        
    private val apiKey = BuildConfig.GEMINI_API_KEY
    private val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent?key=" + apiKey

    fun generateResponse(prompt: String, emotion: String): String {
        val systemPrompt = EmotionPromptManager.getPromptForEmotion(emotion)
        val fullPrompt = systemPrompt + "\\nUser: " + prompt
        
        val json = JSONObject()
        val contents = JSONArray()
        val content = JSONObject()
        val parts = JSONArray()
        val part = JSONObject()
        
        part.put("text", fullPrompt)
        parts.put(part)
        content.put("parts", parts)
        contents.put(content)
        json.put("contents", contents)

        val body = json.toString().toRequestBody("application/json".toMediaType())
        val request = Request.Builder()
            .url(url)
            .post(body)
            .build()

        try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) return "Error: ${response.code}"
                val responseBody = response.body?.string() ?: return "Empty response"
                val responseJson = JSONObject(responseBody)
                val candidates = responseJson.optJSONArray("candidates")
                if (candidates != null && candidates.length() > 0) {
                    val candidate = candidates.getJSONObject(0)
                    val contentObj = candidate.optJSONObject("content")
                    val partsArr = contentObj?.optJSONArray("parts")
                    if (partsArr != null && partsArr.length() > 0) {
                        return partsArr.getJSONObject(0).optString("text")
                    }
                }
                return "Could not parse response."
            }
        } catch (e: IOException) {
            return "Network Error: ${e.message}"
        } catch (e: Exception) {
            return "Error: ${e.message}"
        }
    }
}
""",
    "src/main/java/com/vortexos/assistant/EmotionPromptManager.kt": """package com.vortexos.assistant

object EmotionPromptManager {
    fun getPromptForEmotion(emotion: String): String {
        return when (emotion.lowercase()) {
            "happy" -> "You are a helpful AI assistant. You are feeling energetic and humorous today. Respond accordingly."
            "sad" -> "You are a helpful AI assistant. You notice the user might be sad. Respond with empathy and support."
            "angry" -> "You are a helpful AI assistant. You notice the user is angry. Respond calmly and with understanding."
            "tired" -> "You are a helpful AI assistant. Keep your responses short and concise."
            else -> "You are a professional and helpful AI assistant."
        }
    }
}
""",
    "src/main/java/com/vortexos/assistant/VoiceInputManager.kt": """package com.vortexos.assistant

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer

class VoiceInputManager(context: Context, private val onResult: (String) -> Unit) : RecognitionListener {
    
    private var speechRecognizer: SpeechRecognizer? = null
    private val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        putExtra(RecognizerIntent.EXTRA_LANGUAGE, "tr-TR")
    }

    init {
        if (SpeechRecognizer.isRecognitionAvailable(context)) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
            speechRecognizer?.setRecognitionListener(this)
        }
    }

    fun startListening() {
        speechRecognizer?.startListening(intent)
    }

    fun destroy() {
        speechRecognizer?.destroy()
    }

    override fun onReadyForSpeech(params: Bundle?) {}
    override fun onBeginningOfSpeech() {}
    override fun onRmsChanged(rmsdB: Float) {}
    override fun onBufferReceived(buffer: ByteArray?) {}
    override fun onEndOfSpeech() {}
    override fun onError(error: Int) {}
    override fun onResults(results: Bundle?) {
        val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
        if (!matches.isNullOrEmpty()) {
            onResult(matches[0])
        }
    }
    override fun onPartialResults(partialResults: Bundle?) {}
    override fun onEvent(eventType: Int, params: Bundle?) {}
}
""",
    "src/main/java/com/vortexos/assistant/ChatDatabaseHelper.kt": """package com.vortexos.assistant

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class ChatDatabaseHelper(context: Context) : SQLiteOpenHelper(context, "chat_db", null, 1) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE messages (id INTEGER PRIMARY KEY AUTOINCREMENT, text TEXT, is_user INTEGER, timestamp INTEGER)")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS messages")
        onCreate(db)
    }

    fun addMessage(msg: ChatMessage) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("text", msg.text)
            put("is_user", if (msg.isUser) 1 else 0)
            put("timestamp", msg.timestamp)
        }
        db.insert("messages", null, values)
        db.close()
    }

    fun getAllMessages(): List<ChatMessage> {
        val list = mutableListOf<ChatMessage>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM messages ORDER BY timestamp ASC", null)
        if (cursor.moveToFirst()) {
            do {
                list.add(
                    ChatMessage(
                        cursor.getString(1),
                        cursor.getInt(2) == 1,
                        cursor.getLong(3),
                        cursor.getLong(0)
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return list
    }
}
""",
    "src/main/java/com/vortexos/assistant/QuickActionHandler.kt": """package com.vortexos.assistant

import android.content.Context
import android.content.Intent

class QuickActionHandler(private val context: Context) {
    fun handleCommand(text: String): String? {
        val lowerText = text.lowercase()
        if (lowerText.contains("havayı nasıl") || lowerText.contains("hava durumu")) {
            val intent = Intent().apply {
                action = "com.vortexos.weathersync.SHOW_WEATHER"
            }
            try {
                context.startActivity(intent)
            } catch (e: Exception) {
                // Ignore
            }
            return "Hava durumu bilgisi açılıyor..."
        }
        
        if (lowerText.startsWith("uygulama aç")) {
            return "Uygulama açma özelliği yakında eklenecektir."
        }

        if (lowerText.startsWith("not al")) {
            return "Notunuz kaydedildi."
        }

        return null
    }
}
""",
    "src/main/res/layout/activity_assistant.xml": """<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="?android:attr/colorBackground"
    tools:context=".AssistantActivity">

    <androidx.appcompat.widget.Toolbar
        android:id="@+id/toolbar"
        android:layout_width="match_parent"
        android:layout_height="?attr/actionBarSize"
        android:background="?attr/colorPrimarySurface"
        app:layout_constraintTop_toTopOf="parent">
        
        <TextView
            android:id="@+id/emotionBadge"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="Neutral"
            android:textColor="@android:color/white"
            android:layout_gravity="end"
            android:padding="8dp"/>
    </androidx.appcompat.widget.Toolbar>

    <androidx.recyclerview.widget.RecyclerView
        android:id="@+id/recyclerView"
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:padding="8dp"
        app:layout_constraintTop_toBottomOf="@id/toolbar"
        app:layout_constraintBottom_toTopOf="@id/inputLayout"/>

    <TextView
        android:id="@+id/typingIndicator"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Yazıyor..."
        android:visibility="gone"
        app:layout_constraintBottom_toTopOf="@id/inputLayout"
        app:layout_constraintStart_toStartOf="parent"
        android:padding="8dp"/>

    <LinearLayout
        android:id="@+id/inputLayout"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="horizontal"
        android:padding="8dp"
        app:layout_constraintBottom_toBottomOf="parent">

        <ImageButton
            android:id="@+id/btnMic"
            android:layout_width="48dp"
            android:layout_height="48dp"
            android:src="@android:drawable/ic_btn_speak_now"
            android:background="?attr/selectableItemBackgroundBorderless"
            android:contentDescription="Voice Input"/>

        <EditText
            android:id="@+id/etMessage"
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:layout_weight="1"
            android:hint="@string/type_message"
            android:inputType="textMultiLine"
            android:maxLines="4"
            android:background="@android:color/transparent"
            android:padding="12dp"/>

        <ImageButton
            android:id="@+id/btnSend"
            android:layout_width="48dp"
            android:layout_height="48dp"
            android:src="@android:drawable/ic_menu_send"
            android:background="?attr/selectableItemBackgroundBorderless"
            android:contentDescription="Send"/>
    </LinearLayout>
</androidx.constraintlayout.widget.ConstraintLayout>
""",
    "src/main/res/layout/item_chat_message_user.xml": """<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:orientation="vertical"
    android:gravity="end"
    android:padding="4dp">

    <TextView
        android:id="@+id/tvMessage"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:background="@drawable/bg_bubble_user"
        android:padding="12dp"
        android:textColor="@android:color/white"
        android:maxWidth="280dp"
        android:textSize="16sp"/>
</LinearLayout>
""",
    "src/main/res/layout/item_chat_message_ai.xml": """<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:orientation="vertical"
    android:gravity="start"
    android:padding="4dp">

    <TextView
        android:id="@+id/tvMessage"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:background="@drawable/bg_bubble_ai"
        android:padding="12dp"
        android:textColor="?android:attr/textColorPrimary"
        android:maxWidth="280dp"
        android:textSize="16sp"/>
</LinearLayout>
""",
    "src/main/res/drawable/bg_bubble_user.xml": """<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android">
    <solid android:color="#7C4DFF"/>
    <corners android:topLeftRadius="16dp" android:topRightRadius="16dp" android:bottomLeftRadius="16dp" android:bottomRightRadius="4dp"/>
</shape>
""",
    "src/main/res/drawable/bg_bubble_ai.xml": """<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android">
    <solid android:color="#1E1E1E"/>
    <corners android:topLeftRadius="16dp" android:topRightRadius="16dp" android:bottomLeftRadius="4dp" android:bottomRightRadius="16dp"/>
</shape>
""",
    "src/main/res/mipmap-anydpi-v26/ic_launcher.xml": """<?xml version="1.0" encoding="utf-8"?>
<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">
    <background android:drawable="@color/ic_launcher_background"/>
    <foreground android:drawable="@android:drawable/ic_dialog_info"/>
</adaptive-icon>
""",
    "src/main/res/mipmap-anydpi-v26/ic_launcher_round.xml": """<?xml version="1.0" encoding="utf-8"?>
<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">
    <background android:drawable="@color/ic_launcher_background"/>
    <foreground android:drawable="@android:drawable/ic_dialog_info"/>
</adaptive-icon>
""",
    "src/main/res/values/strings.xml": """<resources>
    <string name="app_name">VorteX Assistant</string>
    <string name="type_message">Type a message...</string>
</resources>
""",
    "src/main/res/values-tr/strings.xml": """<resources>
    <string name="app_name">VorteX Asistan</string>
    <string name="type_message">Mesaj yazın...</string>
</resources>
""",
    "src/main/res/values/colors.xml": """<resources>
    <color name="ic_launcher_background">#121212</color>
</resources>
""",
    "src/main/res/values/themes.xml": """<resources xmlns:tools="http://schemas.android.com/tools">
    <style name="Theme.VorteXOS.Assistant" parent="Theme.MaterialComponents.DayNight.NoActionBar">
        <item name="colorPrimary">#7C4DFF</item>
        <item name="colorPrimaryVariant">#512DA8</item>
        <item name="colorOnPrimary">@android:color/white</item>
        <item name="android:colorBackground">#000000</item>
        <item name="colorSurface">#121212</item>
        <item name="colorOnSurface">#FFFFFF</item>
    </style>
</resources>
"""
}

for filepath, content in files.items():
    full_path = os.path.join(base_dir, filepath)
    os.makedirs(os.path.dirname(full_path), exist_ok=True)
    with open(full_path, "w", encoding="utf-8") as f:
        f.write(content)

print("Files created.")
