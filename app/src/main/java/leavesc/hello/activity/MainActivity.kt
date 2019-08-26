package leavesc.hello.activity

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.view.MenuItemCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import leavesc.hello.activity.adapter.AppRecyclerAdapter
import leavesc.hello.activity.databinding.ActivityMainBinding
import leavesc.hello.activity.extend.accessibilityServiceIsEnabled
import leavesc.hello.activity.extend.canDrawOverlays
import leavesc.hello.activity.extend.hideSoftKeyboard
import leavesc.hello.activity.extend.navToAccessibilityServiceSettingPage
import leavesc.hello.activity.holder.AppInfoHolder
import leavesc.hello.activity.model.ApplicationLocal
import leavesc.hello.activity.service.ActivityService
import leavesc.hello.activity.widget.AppDialogFragment
import leavesc.hello.activity.widget.CommonItemDecoration
import leavesc.hello.activity.widget.MessageDialogFragment

/**
 * 作者：leavesC
 * 时间：2019/1/20 20:41
 * 描述：
 * GitHub：https://github.com/leavesC
 * Blog：https://www.jianshu.com/u/9df45b87cfdf
 */
class MainActivity : AppCompatActivity() {

    private lateinit var appList: MutableList<ApplicationLocal>

    private lateinit var appRecyclerAdapter: AppRecyclerAdapter

    private lateinit var activityMainBinding: ActivityMainBinding

    private val REQUEST_CODE_OVERLAYS = 10

    private var progressDialog: ProgressDialog? = null

    private fun startLoading(cancelable: Boolean = false) {
        if (progressDialog == null) {
            progressDialog = ProgressDialog(this)
        }
        progressDialog?.apply {
            setCancelable(cancelable)
            show()
        }
    }

    private fun cancelLoading() {
        progressDialog?.dismiss()
    }

    @SuppressLint("CheckResult")
    private fun loadAppList() {
        Observable.create(ObservableOnSubscribe<MutableList<ApplicationLocal>> {
            AppInfoHolder.init(this@MainActivity)
            val toMutableList = AppInfoHolder.getAllApplication(this@MainActivity)
            it.onNext(toMutableList)
            it.onComplete()
        }).subscribeOn(Schedulers.io()).doOnSubscribe {
            startLoading()
        }.doFinally {
            cancelLoading()
        }.observeOn(AndroidSchedulers.mainThread()).subscribe {
            appList = it
            initView()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        loadAppList()
    }

    private fun initView() {
        appRecyclerAdapter = AppRecyclerAdapter(appList)
        activityMainBinding.rvAppList.layoutManager =
            LinearLayoutManager(this)
        activityMainBinding.rvAppList.adapter = appRecyclerAdapter
        activityMainBinding.rvAppList.addItemDecoration(
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
                fragment.show(supportFragmentManager, "AppDialogFragment")
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
                                it.name.toLowerCase().contains(value.toLowerCase())
                            }
                            if (find == null) {
                                Toast.makeText(this@MainActivity, "没有找到应用", Toast.LENGTH_SHORT)
                                    .show()
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
        fragment.show(supportFragmentManager, "AppDialogFragment")
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
        messageDialogFragment.show(supportFragmentManager, "showAccessibilityConfirmDialog")
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
        messageDialogFragment.show(supportFragmentManager, "showOverlayConfirmDialog")
    }

    private fun startActivityService() {
        startService(Intent(this, ActivityService::class.java))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_CODE_OVERLAYS -> {
                if (canDrawOverlays) {
                    showAccessibilityConfirmDialog()
                } else {
                    Toast.makeText(this, "请授予悬浮窗权限", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

}