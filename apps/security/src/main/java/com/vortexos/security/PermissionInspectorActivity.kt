package com.vortexos.security

import android.Manifest
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.vortexos.security.databinding.ActivityPermissionInspectorBinding

class PermissionInspectorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPermissionInspectorBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPermissionInspectorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val pm = packageManager
        val installedPackages = pm.getInstalledPackages(PackageManager.GET_PERMISSIONS)

        val sensitivePermissions = listOf(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_CONTACTS
        )

        val appsWithPermissions = mutableListOf<AppPermissionData>()

        for (pkg in installedPackages) {
            val requestedPerms = pkg.requestedPermissions
            if (requestedPerms != null) {
                val heldPerms = requestedPerms.filter { sensitivePermissions.contains(it) }
                if (heldPerms.isNotEmpty()) {
                    val appInfo = pkg.applicationInfo
                    if (appInfo != null) {
                        appsWithPermissions.add(
                            AppPermissionData(
                                appInfo,
                                heldPerms
                            )
                        )
                    }
                }
            }
        }

        binding.recyclerPermissions.layoutManager = LinearLayoutManager(this)
        binding.recyclerPermissions.adapter = PermissionAppAdapter(appsWithPermissions, pm)
    }
}

data class AppPermissionData(
    val appInfo: android.content.pm.ApplicationInfo,
    val permissions: List<String>
)
