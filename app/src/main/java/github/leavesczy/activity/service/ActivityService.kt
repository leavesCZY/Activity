package github.leavesczy.activity.service

import android.accessibilityservice.AccessibilityService
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import github.leavesczy.activity.R
import github.leavesczy.activity.adapter.ActivityRecyclerAdapter
import github.leavesczy.activity.adapter.AppRecyclerAdapter
import github.leavesczy.activity.holder.AccessibilityUtils.canDrawOverlays
import github.leavesczy.activity.holder.AccessibilityUtils.clipboardCopy
import github.leavesczy.activity.holder.AccessibilityUtils.showToast
import github.leavesczy.activity.holder.AppInfoLoader

/**
 * @Author: leavesCZY
 * @Date: 2019/1/27 12:08
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
class ActivityService : AccessibilityService() {

    private var windowView: View? = null

    private var tvAppName: TextView? = null

    private var rvActivityList: RecyclerView? = null

    private lateinit var windowManager: WindowManager

    private lateinit var layoutParams: WindowManager.LayoutParams

    private val activityList = mutableListOf<String>()

    private val activityRecyclerAdapter = ActivityRecyclerAdapter()

    private var itemIndex = 0

    override fun onServiceConnected() {
        super.onServiceConnected()
        showFloatingWindow()
        log {
            "onServiceConnected()"
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        log {
            "onAccessibilityEvent()"
        }
        windowView?.let {
            event?.let {
                val eventType = event.eventType
                if (eventType == AccessibilityEvent.TYPE_WINDOWS_CHANGED || eventType == AccessibilityEvent.CONTENT_CHANGE_TYPE_PANE_DISAPPEARED) {
                    val packageName = event.packageName?.toString() ?: ""
                    val appName = if (packageName.isBlank()) {
                        ""
                    } else {
                        AppInfoLoader.getAppName(packageName)
                    }
                    tvAppName?.text = buildString {
                        append(appName)
                        append(" : ")
                        append(packageName)
                    }
                    event.className?.let {
                        activityList.add("${++itemIndex}" + " : " + event.className.toString())
                        rvActivityList?.scrollToPosition(
                            activityRecyclerAdapter.itemCount - 1
                        )
                        activityRecyclerAdapter.notifyDataSetChanged()
                    }
                }
            }
        }
    }

    override fun onInterrupt() {
        log {
            "onInterrupt()"
        }
        removeWindow()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        log {
            "onStartCommand()"
        }
        showFloatingWindow()
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        removeWindow()
        log {
            "onDestroy()"
        }
    }

    private fun removeWindow() {
        windowView?.let {
            windowManager.removeView(windowView)
            windowView = null
            tvAppName = null
            rvActivityList = null
        }
    }

    private fun showFloatingWindow() {
        if (canDrawOverlays) {
            if (windowView == null) {
                initView()
            }
        }
    }

    private fun initView() {
        val layoutView = LayoutInflater.from(this).inflate(R.layout.layout_activity_window, null)
        val ivExtendsWindow = layoutView.findViewById<ImageView>(R.id.ivExtendsWindow)
        val ivRemoveWindow = layoutView.findViewById<ImageView>(R.id.ivRemoveWindow)
        val tvAppName = layoutView.findViewById<TextView>(R.id.tvAppName)
        val rvActivityList = layoutView.findViewById<RecyclerView>(R.id.rvActivityList)
        layoutView.setOnTouchListener(FloatingOnTouchListener())
        ivExtendsWindow.setOnClickListener {
            if (rvActivityList.visibility == View.GONE) {
                rvActivityList.visibility = View.VISIBLE
            } else {
                rvActivityList.visibility = View.GONE
            }
        }
        ivRemoveWindow.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                disableSelf()
                removeWindow()
            } else {
                removeWindow()
            }
        }
        activityRecyclerAdapter.activityList = activityList
        activityRecyclerAdapter.setOnItemClickListener(object :
            AppRecyclerAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                val text = tvAppName.text.toString() + "\n" + activityList[position]
                clipboardCopy(msg = text)
                showToast(msg = "已复制进程&页面信息")
            }
        })
        rvActivityList.adapter = activityRecyclerAdapter
        rvActivityList.layoutManager = LinearLayoutManager(this)
        layoutParams = WindowManager.LayoutParams()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            layoutParams.type = WindowManager.LayoutParams.TYPE_PHONE
        }
        layoutParams.gravity = Gravity.START or Gravity.TOP
        layoutParams.flags =
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager.addView(layoutView, layoutParams)
        this.windowView = layoutView
        this.tvAppName = tvAppName
        this.rvActivityList = rvActivityList
    }

    private inner class FloatingOnTouchListener : View.OnTouchListener {

        private var x = 0

        private var y = 0

        @SuppressLint("ClickableViewAccessibility")
        override fun onTouch(view: View, event: MotionEvent): Boolean {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    x = event.rawX.toInt()
                    y = event.rawY.toInt()
                }

                MotionEvent.ACTION_MOVE -> {
                    val nowX = event.rawX.toInt()
                    val nowY = event.rawY.toInt()
                    val movedX = nowX - x
                    val movedY = nowY - y
                    x = nowX
                    y = nowY
                    layoutParams.x += movedX
                    layoutParams.y += movedY
                    windowManager.updateViewLayout(view, layoutParams)
                }
            }
            return false
        }
    }

    private fun log(log: () -> Any) {
        Log.e("ActivityService", log().toString())
    }

}