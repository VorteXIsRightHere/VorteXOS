package com.vortexos.filemanager

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vortexos.filemanager.databinding.ItemFileBinding

class FileAdapter(
    private var files: List<FileItem>,
    private val onClick: (FileItem) -> Unit,
    private val onLongClick: (FileItem) -> Boolean
) : RecyclerView.Adapter<FileAdapter.FileViewHolder>() {

    inner class FileViewHolder(val binding: ItemFileBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: FileItem) {
            binding.tvFileName.text = item.name
            binding.ivIcon.setImageResource(
                if (item.isDirectory) android.R.drawable.ic_menu_agenda
                else android.R.drawable.ic_menu_gallery
            )
            binding.tvFileDetails.text = if (item.isDirectory) "Folder" else "${item.size / 1024} KB"
            
            binding.root.setOnClickListener { onClick(item) }
            binding.root.setOnLongClickListener { onLongClick(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileViewHolder {
        val binding = ItemFileBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FileViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FileViewHolder, position: Int) {
        holder.bind(files[position])
    }

    override fun getItemCount() = files.size

    fun updateFiles(newFiles: List<FileItem>) {
        files = newFiles
        notifyDataSetChanged()
    }
}
