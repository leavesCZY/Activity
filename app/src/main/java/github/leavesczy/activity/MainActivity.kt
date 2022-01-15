package github.leavesczy.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.view.MenuItemCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import github.leavesczy.activity.adapter.AppRecyclerAdapter
import github.leavesczy.activity.extend.*
import github.leavesczy.activity.holder.AppInfoHolder
import github.leavesczy.activity.model.ApplicationLocal
import github.leavesczy.activity.service.ActivityService
import github.leavesczy.activity.widget.AppDialogFragment
import github.leavesczy.activity.widget.CommonItemDecoration
import github.leavesczy.activity.widget.LoadingDialog
import github.leavesczy.activity.widget.MessageDialogFragment
import java.util.*

/**
 * @Author: leavesCZY
 * @Date: 2019/1/20 20:41
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
class MainActivity : AppCompatActivity() {

    companion object {

        private const val REQUEST_CODE_OVERLAYS = 10

    }

    private lateinit var appList: MutableList<ApplicationLocal>

    private lateinit var appRecyclerAdapter: AppRecyclerAdapter

    private var progressDialog: LoadingDialog? = null

    private val loadAppThread = HandlerThread("loadApp")

    private val loadAppHandler by lazy {
        Handler(loadAppThread.looper, Handler.Callback {
            if (this.isDestroyed || this.isFinishing) {
                return@Callback true
            }
            runOnUiThread {
                startLoading()
            }
            AppInfoHolder.init(this@MainActivity)
            appList = AppInfoHolder.getAllApplication(this@MainActivity)
            runOnUiThread {
                if (this.isDestroyed || this.isFinishing) {
                    return@runOnUiThread
                }
                initView()
                cancelLoading()
            }
            return@Callback true
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        loadAppThread.start()
        loadAppHandler.sendEmptyMessage(1)
    }

    private fun initView() {
        appRecyclerAdapter = AppRecyclerAdapter(appList)
        val rvAppList = findViewById<RecyclerView>(R.id.rvAppList)
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
                hideSoftKeyboard()
                val fragment = AppDialogFragment()
                fragment.applicationInfo = appList[position]
                showDialog(fragment)
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        menu?.let {
            val menuItem = menu.findItem(R.id.menu_search)
            val searchView: SearchView = MenuItemCompat.getActionView(menuItem) as SearchView
            searchView.setIconifiedByDefault(false)
            searchView.queryHint = "Search App Name"
            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextChange(value: String?): Boolean {
                    return true
                }

                override fun onQueryTextSubmit(value: String?): Boolean {
                    value?.let { s ->
                        if (s.isNotEmpty()) {
                            val find = AppInfoHolder.getAllApplication(this@MainActivity).find {
                                it.name.lowercase(Locale.CHINA)
                                    .contains(value.lowercase(Locale.CHINA))
                            }
                            if (find == null) {
                                showToast("没有找到应用")
                            } else {
                                searchView.isIconified = true
                                hideSoftKeyboard()
                                showAppInfoDialog(find)
                            }
                        }
                    }
                    return true
                }
            }
            )
        }
        return super.onCreateOptionsMenu(menu)
    }

    private fun showAppInfoDialog(applicationLocal: ApplicationLocal) {
        val fragment = AppDialogFragment()
        fragment.applicationInfo = applicationLocal
        showDialog(fragment)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        item.apply {
            when (item.itemId) {
                R.id.menuAllApp -> {
                    appList.clear()
                    appList.addAll(AppInfoHolder.getAllApplication(this@MainActivity))
                }
                R.id.menuSystemApp -> {
                    appList.clear()
                    appList.addAll(AppInfoHolder.getAllSystemApplication(this@MainActivity))
                }
                R.id.menuNormalApp -> {
                    appList.clear()
                    appList.addAll(AppInfoHolder.getAllNonSystemApplication(this@MainActivity))
                }
                R.id.menuCurrentActivity -> {
                    if (canDrawOverlays) {
                        requestAccessibilityPermission()
                    } else {
                        requestOverlayPermission()
                    }
                }
            }
            appRecyclerAdapter.notifyDataSetChanged()
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
                startActivityForResult(
                    Intent(
                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:${BuildConfig.APPLICATION_ID}")
                    ),
                    REQUEST_CODE_OVERLAYS
                )
            })
        showDialog(messageDialogFragment)
    }

    private fun startActivityService() {
        startService(Intent(this, ActivityService::class.java))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CODE_OVERLAYS -> {
                if (canDrawOverlays) {
                    showAccessibilityConfirmDialog()
                } else {
                    showToast("请授予悬浮窗权限")
                }
            }
        }
    }

    private fun startLoading(cancelable: Boolean = false) {
        if (progressDialog == null) {
            progressDialog = LoadingDialog(this)
        }
        progressDialog?.start(cancelable, cancelable)
    }

    private fun cancelLoading() {
        progressDialog?.dismiss()
    }

    override fun onDestroy() {
        super.onDestroy()
        loadAppThread.quitSafely()
        loadAppHandler.removeCallbacksAndMessages(null)
    }

}