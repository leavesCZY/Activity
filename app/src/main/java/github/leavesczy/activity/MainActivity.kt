package github.leavesczy.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import github.leavesczy.activity.adapter.AppRecyclerAdapter
import github.leavesczy.activity.extend.*
import github.leavesczy.activity.holder.AppInfoHolder
import github.leavesczy.activity.model.AppInfo
import github.leavesczy.activity.service.ActivityService
import github.leavesczy.activity.widget.AppInfoDialog
import github.leavesczy.activity.widget.CommonItemDecoration
import github.leavesczy.activity.widget.LoadingDialog
import github.leavesczy.activity.widget.MessageDialogFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * @Author: leavesCZY
 * @Date: 2019/1/20 20:41
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
class MainActivity : AppCompatActivity() {

    private val overlayPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (canDrawOverlays) {
                showAccessibilityConfirmDialog()
            } else {
                showToast("请授予悬浮窗权限")
            }
        }

    private val rvAppList by lazy {
        findViewById<RecyclerView>(R.id.rvAppList)
    }

    private var appList = mutableListOf<AppInfo>()

    private val appRecyclerAdapter = AppRecyclerAdapter(appList)

    private var progressDialog: LoadingDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
        lifecycleScope.launch {
            loadApps()
        }
    }

    private fun initView() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        rvAppList.layoutManager = LinearLayoutManager(this)
        rvAppList.adapter = appRecyclerAdapter
        rvAppList.addItemDecoration(
            CommonItemDecoration(
                ContextCompat.getDrawable(this@MainActivity, R.drawable.layout_divider)!!,
                LinearLayoutManager.VERTICAL
            )
        )
        appRecyclerAdapter.setOnItemClickListener(object : AppRecyclerAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                val fragment = AppInfoDialog()
                fragment.applicationInfo = appList[position]
                showDialog(fragment)
            }
        })
    }

    @SuppressLint("NotifyDataSetChanged")
    private suspend fun loadApps() {
        withContext(context = Dispatchers.Main.immediate) {
            startLoading()
        }
        withContext(context = Dispatchers.IO) {
            AppInfoHolder.init(context = applicationContext)
            val list = AppInfoHolder.getAllApplication()
            appList.clear()
            appList.addAll(list)
        }
        withContext(context = Dispatchers.Main.immediate) {
            appRecyclerAdapter.notifyDataSetChanged()
            cancelLoading()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        item.apply {
            when (item.itemId) {
                R.id.menuAllApp, R.id.menuSystemApp, R.id.menuNormalApp -> {
                    val list = when (item.itemId) {
                        R.id.menuAllApp -> {
                            AppInfoHolder.getAllApplication()
                        }
                        R.id.menuSystemApp -> {
                            AppInfoHolder.getAllSystemApplication()
                        }
                        R.id.menuNormalApp -> {
                            AppInfoHolder.getAllNonSystemApplication()
                        }
                        else -> {
                            emptyList()
                        }
                    }
                    appList.clear()
                    appList.addAll(list)
                    appRecyclerAdapter.notifyDataSetChanged()
                    rvAppList.scrollToPosition(0)
                }
                R.id.menuCurrentActivity -> {
                    if (canDrawOverlays) {
                        requestAccessibilityPermission()
                    } else {
                        requestOverlayPermission()
                    }
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun requestAccessibilityPermission() {
        if (accessibilityServiceIsEnabled(ActivityService::class.java)) {
            if (canDrawOverlays) {
                startActivityService()
            } else {
                showOverlayConfirmDialog()
            }
        } else {
            showAccessibilityConfirmDialog()
        }
    }

    private fun requestOverlayPermission() {
        if (canDrawOverlays) {
            if (accessibilityServiceIsEnabled(ActivityService::class.java)) {
                startActivityService()
            } else {
                showAccessibilityConfirmDialog()
            }
        } else {
            showOverlayConfirmDialog()
        }
    }

    private fun showAccessibilityConfirmDialog() {
        val messageDialogFragment = MessageDialogFragment()
        messageDialogFragment.init("", "检测到应用似乎还未被授予无障碍服务权限，是否前往开启权限？",
            { _, _ ->
                navToAccessibilityServiceSettingPage()
            })
        showDialog(messageDialogFragment)
    }

    private fun showOverlayConfirmDialog() {
        val messageDialogFragment = MessageDialogFragment()
        messageDialogFragment.init("", "检测到应用似乎还未被授予悬浮窗权限，是否前往开启权限？",
            { _, _ ->
                overlayPermissionLauncher.launch(
                    Intent(
                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:${BuildConfig.APPLICATION_ID}")
                    )
                )
            })
        showDialog(messageDialogFragment)
    }

    private fun startActivityService() {
        startService(Intent(this, ActivityService::class.java))
    }

    private fun startLoading() {
        if (progressDialog == null) {
            progressDialog = LoadingDialog(this)
        }
        progressDialog?.start(cancelable = false, canceledOnTouchOutside = false)
    }

    private fun cancelLoading() {
        progressDialog?.dismiss()
    }

}