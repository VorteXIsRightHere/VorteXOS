package com.vortexos.notepad

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.vortexos.notepad.databinding.ActivityNoteEditBinding
import android.text.Editable
import android.text.TextWatcher

class NoteEditActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNoteEditBinding
    private lateinit var dbHelper: NoteDatabaseHelper
    private var noteId: Long = -1

    private val themeReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            recreate()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNoteEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = NoteDatabaseHelper(this)
        
        noteId = intent.getLongExtra("note_id", -1)
        if (noteId != -1L) {
            loadNote()
        }

        binding.btnSave.setOnClickListener {
            saveNote()
        }

        binding.btnShare.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, binding.etTitle.text.toString())
                putExtra(Intent.EXTRA_TEXT, binding.etContent.text.toString())
            }
            startActivity(Intent.createChooser(shareIntent, getString(R.string.share)))
        }

        binding.etContent.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                binding.tvCharCount.text = "${s?.length ?: 0} chars"
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        val filter = IntentFilter().apply {
            addAction("com.vortexos.common.VorteXConstants.ACTION_WEATHER_CHANGED")
            addAction("com.vortexos.common.VorteXConstants.ACTION_EMOTION_CHANGED")
        }
        registerReceiver(themeReceiver, filter)
    }

    private fun loadNote() {
        val note = dbHelper.getAllNotes().find { it.id == noteId }
        note?.let {
            binding.etTitle.setText(it.title)
            binding.etContent.setText(it.content)
            binding.etCategory.setText(it.category)
        }
    }

    private fun saveNote() {
        val title = binding.etTitle.text.toString()
        val content = binding.etContent.text.toString()
        val category = binding.etCategory.text.toString()
        
        if (title.isBlank() && content.isBlank()) return

        val note = Note(
            id = if (noteId == -1L) 0 else noteId,
            title = title,
            content = content,
            category = category,
            timestamp = System.currentTimeMillis(),
            colorTag = 0
        )

        if (noteId == -1L) {
            dbHelper.addNote(note)
        } else {
            dbHelper.updateNote(note)
        }
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(themeReceiver)
    }
}
