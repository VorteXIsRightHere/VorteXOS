package com.vortexos.security

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vortexos.security.databinding.ItemAppLockBinding

class AppLockAdapter(
    private val apps: List<ApplicationInfo>,
    private val packageManager: PackageManager
) : RecyclerView.Adapter<AppLockAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemAppLockBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAppLockBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val appInfo = apps[position]
        holder.binding.textAppName.text = appInfo.loadLabel(packageManager)
        holder.binding.imageAppIcon.setImageDrawable(appInfo.loadIcon(packageManager))

        val packageName = appInfo.packageName
        holder.binding.switchLock.setOnCheckedChangeListener(null)
        holder.binding.switchLock.isChecked = AppLockManager.isLocked(packageName)

        holder.binding.switchLock.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                AppLockManager.lockApp(packageName)
            } else {
                AppLockManager.unlockApp(packageName)
            }
        }
    }

    override fun getItemCount() = apps.size
}
