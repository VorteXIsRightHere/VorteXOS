package com.vortexos.notepad

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.vortexos.notepad.databinding.ActivityNoteListBinding

class NoteListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNoteListBinding
    private lateinit var dbHelper: NoteDatabaseHelper
    private lateinit var adapter: NoteAdapter

    private val themeReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            recreate()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNoteListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = NoteDatabaseHelper(this)
        
        adapter = NoteAdapter(dbHelper.getAllNotes()) { note ->
            val intent = Intent(this, NoteEditActivity::class.java).apply {
                putExtra("note_id", note.id)
            }
            startActivity(intent)
        }
        
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        binding.fabAddNote.setOnClickListener {
            startActivity(Intent(this, NoteEditActivity::class.java))
        }

        setupSwipeToDelete()

        val filter = IntentFilter().apply {
            addAction("com.vortexos.common.VorteXConstants.ACTION_WEATHER_CHANGED")
            addAction("com.vortexos.common.VorteXConstants.ACTION_EMOTION_CHANGED")
        }
        registerReceiver(themeReceiver, filter)
    }

    override fun onResume() {
        super.onResume()
        adapter.updateNotes(dbHelper.getAllNotes())
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(themeReceiver)
    }

    private fun setupSwipeToDelete() {
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val note = dbHelper.getAllNotes()[position]
                dbHelper.deleteNote(note.id)
                adapter.updateNotes(dbHelper.getAllNotes())
            }
        }
        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(binding.recyclerView)
    }
}
