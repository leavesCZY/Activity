package github.leavesc.activity

import android.content.DialogInterface
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
import github.leavesc.activity.adapter.AppRecyclerAdapter
import github.leavesc.activity.extend.*
import github.leavesc.activity.holder.AppInfoHolder
import github.leavesc.activity.model.ApplicationLocal
import github.leavesc.activity.service.ActivityService
import github.leavesc.activity.widget.AppDialogFragment
import github.leavesc.activity.widget.CommonItemDecoration
import github.leavesc.activity.widget.LoadingDialog
import github.leavesc.activity.widget.MessageDialogFragment
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

/**
 * 作者：leavesC
 * 时间：2019/1/20 20:41
 * 描述：
 * GitHub：https://github.com/leavesC
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
            if (this.isDestroyed || this.isFinishing) {
                return@Callback true
            }
            runOnUiThread {
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
        rv_appList.layoutManager = LinearLayoutManager(this)
        rv_appList.adapter = appRecyclerAdapter
        rv_appList.addItemDecoration(
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
        menuInflater.inflate(R.menu.main, menu)
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
                    value?.let {
                        if (it.isNotEmpty()) {
                            val find = AppInfoHolder.getAllApplication(this@MainActivity).find {
                                it.name.toLowerCase(Locale.CHINA)
                                    .contains(value.toLowerCase(Locale.CHINA))
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
                R.id.menu_allApp -> {
                    appList.clear()
                    appList.addAll(AppInfoHolder.getAllApplication(this@MainActivity))
                }
                R.id.menu_systemApp -> {
                    appList.clear()
                    appList.addAll(AppInfoHolder.getAllSystemApplication(this@MainActivity))
                }
                R.id.menu_nonSystemApp -> {
                    appList.clear()
                    appList.addAll(AppInfoHolder.getAllNonSystemApplication(this@MainActivity))
                }
                R.id.menu_currentActivity -> {
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
            DialogInterface.OnClickListener { _, _ ->
                navToAccessibilityServiceSettingPage()
            })
        showDialog(messageDialogFragment)
    }

    private fun showOverlayConfirmDialog() {
        val messageDialogFragment = MessageDialogFragment()
        messageDialogFragment.init("", "检测到应用似乎还未被授予悬浮窗权限，是否前往开启权限？",
            DialogInterface.OnClickListener { _, _ ->
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