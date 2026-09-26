package com.vortexos.launcher

import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.vortexos.launcher.databinding.FragmentAppDrawerBinding
import java.util.Locale

class AppDrawerFragment : Fragment() {

    private var _binding: FragmentAppDrawerBinding? = null
    private val binding get() = _binding!!
    private lateinit var appAdapter: AppAdapter
    private var allApps: List<AppModel> = emptyList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAppDrawerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        allApps = loadApps()
        appAdapter = AppAdapter(allApps) { app ->
            startActivity(app.intent)
        }
        
        binding.appGrid.layoutManager = GridLayoutManager(context, 4)
        binding.appGrid.adapter = appAdapter

        binding.drawerSearchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                filterApps(s.toString())
            }
        })
    }

    private fun loadApps(): List<AppModel> {
        val pm = requireContext().packageManager
        val intent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }
        val resolveInfoList: List<ResolveInfo> = pm.queryIntentActivities(intent, 0)
        
        return resolveInfoList.map {
            AppModel(
                name = it.loadLabel(pm).toString(),
                packageName = it.activityInfo.packageName,
                icon = it.activityInfo.loadIcon(pm),
                intent = pm.getLaunchIntentForPackage(it.activityInfo.packageName) ?: Intent()
            )
        }.sortedBy { it.name.lowercase(Locale.getDefault()) }
    }

    private fun filterApps(query: String) {
        val filtered = if (query.isEmpty()) {
            allApps
        } else {
            allApps.filter { it.name.contains(query, ignoreCase = true) }
        }
        appAdapter.updateApps(filtered)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
