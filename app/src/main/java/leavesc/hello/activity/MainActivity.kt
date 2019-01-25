package leavesc.hello.activity

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.databinding.DataBindingUtil
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.provider.Settings
import android.support.v4.content.ContextCompat
import android.support.v4.view.MenuItemCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SearchView
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import leavesc.hello.activity.adapter.AppRecyclerAdapter
import leavesc.hello.activity.databinding.ActivityMainBinding
import leavesc.hello.activity.holder.AppInfoHolder
import leavesc.hello.activity.model.ApplicationLocal
import leavesc.hello.activity.service.ActivityService
import leavesc.hello.activity.utils.AccessibilityServiceUtils
import leavesc.hello.activity.utils.SoftKeyboardUtils
import leavesc.hello.activity.widget.AppDialogFragment
import leavesc.hello.activity.widget.CommonItemDecoration

/**
 * 作者：leavesC
 * 时间：2019/1/20 20:41
 * 描述：
 * GitHub：https://github.com/leavesC
 * Blog：https://www.jianshu.com/u/9df45b87cfdf
 */
class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"

    private lateinit var appList: MutableList<ApplicationLocal>

    private lateinit var appRecyclerAdapter: AppRecyclerAdapter

    private lateinit var activityMainBinding: ActivityMainBinding

    private val REQUEST_CODE_OVERLAYS = 10

    private inner class InitAppAsyncTask : AsyncTask<Context, Void, MutableList<ApplicationLocal>>() {

        override fun doInBackground(vararg params: Context): MutableList<ApplicationLocal> {
            AppInfoHolder.init(params[0])
            return AppInfoHolder.getAllApplication(params[0]).toMutableList()
        }

        override fun onPostExecute(result: MutableList<ApplicationLocal>) {
            appList = result
            initView()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        InitAppAsyncTask().execute(this)
    }

    private fun initView() {
        appRecyclerAdapter = AppRecyclerAdapter(appList)
        activityMainBinding.rvAppList.layoutManager = LinearLayoutManager(this)
        activityMainBinding.rvAppList.adapter = appRecyclerAdapter
        activityMainBinding.rvAppList.addItemDecoration(
            CommonItemDecoration(
                ContextCompat.getDrawable(this@MainActivity, R.drawable.layout_divider)!!,
                LinearLayoutManager.VERTICAL
            )
        )
        appRecyclerAdapter.setOnItemClickListener(object : AppRecyclerAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                SoftKeyboardUtils.hideSoftKeyboard(this@MainActivity)
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
                                Toast.makeText(this@MainActivity, "没有找到应用", Toast.LENGTH_SHORT).show()
                            } else {
                                searchView.isIconified = true
                                SoftKeyboardUtils.hideSoftKeyboard(this@MainActivity)
                                val fragment = AppDialogFragment()
                                fragment.applicationInfo = find
                                fragment.show(supportFragmentManager, "AppDialogFragment")
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

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        item?.apply {
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
                    if (AccessibilityServiceUtils.isEnabled(this@MainActivity, ActivityService::class.java)) {
                        Toast.makeText(this@MainActivity, "已启用", Toast.LENGTH_SHORT).show()
                    } else {
                        jumpToSettingPage(this@MainActivity)
                    }
//                    showWindow()
                }
            }
            appRecyclerAdapter.notifyDataSetChanged()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showWindow() {
        if (appCanDrawOverlays(this)) {
            startWindowService()
        } else {
            startActivityForResult(
                Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:${BuildConfig.APPLICATION_ID}")),
                REQUEST_CODE_OVERLAYS
            )
        }
    }

    private fun startWindowService() {
        startService(Intent(this, ActivityService::class.java))
    }

    private fun appCanDrawOverlays(context: Context) = Settings.canDrawOverlays(context)

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_CODE_OVERLAYS -> {
                if (appCanDrawOverlays(this)) {
                    startWindowService()
                } else {
                    Toast.makeText(this, "请授予弹出悬浮窗的权限", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    //跳转到设置页面无障碍服务开启自定义辅助功能服务
    fun jumpToSettingPage(context: Context) {
        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
    }

}