package com.vortexos.filemanager

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.GridLayoutManager
import com.vortexos.filemanager.databinding.ActivityFileManagerBinding
import java.io.File
import android.widget.EditText

class FileManagerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFileManagerBinding
    private lateinit var adapter: FileAdapter
    private var currentDirectory: File = Environment.getExternalStorageDirectory()
    private var isGridLayout = false

    private val themeReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            recreate()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFileManagerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (checkPermission()) {
            setupRecyclerView()
            loadFiles()
        } else {
            requestPermission()
        }

        binding.btnToggleView.setOnClickListener {
            isGridLayout = !isGridLayout
            setupRecyclerView()
        }

        binding.btnNewFolder.setOnClickListener {
            showNewFolderDialog()
        }

        val filter = IntentFilter().apply {
            addAction("com.vortexos.common.VorteXConstants.ACTION_WEATHER_CHANGED")
            addAction("com.vortexos.common.VorteXConstants.ACTION_EMOTION_CHANGED")
        }
        registerReceiver(themeReceiver, filter)
    }

    private fun setupRecyclerView() {
        adapter = FileAdapter(emptyList(), { item ->
            if (item.isDirectory) {
                currentDirectory = item.file
                loadFiles()
            } else {
                Toast.makeText(this, "Opening file: ${item.name}", Toast.LENGTH_SHORT).show()
                // Implementation for open with intent could be here
            }
        }, { item ->
            showFileOptionsDialog(item)
            true
        })
        binding.recyclerView.layoutManager = if (isGridLayout) GridLayoutManager(this, 3) else LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
    }

    private fun loadFiles() {
        binding.tvBreadcrumb.text = currentDirectory.absolutePath
        val files = currentDirectory.listFiles()?.map { FileItem(it) }?.sortedWith(
            compareBy({ !it.isDirectory }, { it.name.lowercase() })
        ) ?: emptyList()
        adapter.updateFiles(files)
    }

    private fun showFileOptionsDialog(item: FileItem) {
        val options = arrayOf(getString(R.string.rename), getString(R.string.delete))
        AlertDialog.Builder(this)
            .setTitle(item.name)
            .setItems(options) { _, which ->
                when (which) {
                    0 -> showRenameDialog(item)
                    1 -> {
                        if (FileOperations.deleteFile(item.file)) loadFiles()
                    }
                }
            }.show()
    }
    
    private fun showRenameDialog(item: FileItem) {
        val input = EditText(this)
        input.setText(item.name)
        AlertDialog.Builder(this)
            .setTitle(R.string.rename)
            .setView(input)
            .setPositiveButton("OK") { _, _ ->
                val newName = input.text.toString()
                if (newName.isNotBlank()) {
                    FileOperations.renameFile(item.file, newName)
                    loadFiles()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showNewFolderDialog() {
        val input = EditText(this)
        AlertDialog.Builder(this)
            .setTitle(R.string.new_folder)
            .setView(input)
            .setPositiveButton("OK") { _, _ ->
                val name = input.text.toString()
                if (name.isNotBlank()) {
                    FileOperations.createFolder(currentDirectory, name)
                    loadFiles()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onBackPressed() {
        if (currentDirectory.absolutePath != Environment.getExternalStorageDirectory().absolutePath) {
            currentDirectory = currentDirectory.parentFile ?: Environment.getExternalStorageDirectory()
            loadFiles()
        } else {
            super.onBackPressed()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(themeReceiver)
    }

    private fun checkPermission(): Boolean {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE), 101)
    }
}
