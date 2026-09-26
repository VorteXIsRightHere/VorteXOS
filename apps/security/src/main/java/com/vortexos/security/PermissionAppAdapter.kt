package com.vortexos.security

import android.content.pm.PackageManager
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.vortexos.security.databinding.ItemAppPermissionBinding

class PermissionAppAdapter(
    private val items: List<AppPermissionData>,
    private val packageManager: PackageManager
) : RecyclerView.Adapter<PermissionAppAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemAppPermissionBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAppPermissionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.binding.textAppName.text = item.appInfo.loadLabel(packageManager)
        holder.binding.imageAppIcon.setImageDrawable(item.appInfo.loadIcon(packageManager))

        holder.binding.chipGroupPermissions.removeAllViews()
        for (perm in item.permissions) {
            val chip = Chip(holder.itemView.context).apply {
                text = perm.substringAfterLast(".")
                isClickable = false
            }
            holder.binding.chipGroupPermissions.addView(chip)
        }
    }

    override fun getItemCount() = items.size
}
