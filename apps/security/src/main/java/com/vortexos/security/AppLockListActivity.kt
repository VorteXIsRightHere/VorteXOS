package com.vortexos.security

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.vortexos.security.databinding.ActivityAppLockListBinding

class AppLockListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAppLockListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAppLockListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        AppLockManager.init(this)

        val pm = packageManager
        val installedApps = pm.getInstalledApplications(PackageManager.GET_META_DATA)
        // Filter out system apps if you want, but for now show all user apps that have a launch intent
        val launchableApps = installedApps.filter { pm.getLaunchIntentForPackage(it.packageName) != null }

        binding.recyclerAppLock.layoutManager = LinearLayoutManager(this)
        binding.recyclerAppLock.adapter = AppLockAdapter(launchableApps, pm)
    }
}
