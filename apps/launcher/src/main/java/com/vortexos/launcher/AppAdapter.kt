package com.vortexos.launcher

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.vortexos.launcher.databinding.ItemAppBinding

class AppAdapter(
    private var apps: List<AppModel>,
    private val onClick: (AppModel) -> Unit
) : RecyclerView.Adapter<AppAdapter.AppViewHolder>() {

    fun updateApps(newApps: List<AppModel>) {
        apps = newApps
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppViewHolder {
        val binding = ItemAppBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AppViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AppViewHolder, position: Int) {
        holder.bind(apps[position])
    }

    override fun getItemCount() = apps.size

    inner class AppViewHolder(private val binding: ItemAppBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(app: AppModel) {
            binding.appIcon.setImageDrawable(app.icon)
            binding.appName.text = app.name
            binding.root.setOnClickListener { onClick(app) }
        }
    }
}
