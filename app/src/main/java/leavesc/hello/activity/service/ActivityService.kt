package leavesc.hello.activity.service

import android.accessibilityservice.AccessibilityService
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Build
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.*
import android.view.accessibility.AccessibilityEvent
import android.widget.SeekBar
import leavesc.hello.activity.R
import leavesc.hello.activity.adapter.ActivityRecyclerAdapter
import leavesc.hello.activity.databinding.LayoutActivityWindowBinding

/**
 * 作者：leavesC
 * 时间：2019/1/27 12:08
 * 描述：
 * GitHub：https://github.com/leavesC
 * Blog：https://www.jianshu.com/u/9df45b87cfdf
 */
class ActivityService : AccessibilityService() {

    private val TAG = "ActivityService"

    private lateinit var windowManager: WindowManager

    private lateinit var layoutParams: WindowManager.LayoutParams

    private var view: View? = null

    private lateinit var layoutActivityWindowBinding: LayoutActivityWindowBinding;

    private val activityList = mutableListOf<String>()

    private val activityRecyclerAdapter = ActivityRecyclerAdapter()

    override fun onServiceConnected() {
        super.onServiceConnected()
        showFloatingWindow()
        Log.e(TAG, "onServiceConnected()")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        Log.e(TAG, "onAccessibilityEvent()")
        view?.let {
            event?.let {
                val eventType = event.eventType
                if (eventType == AccessibilityEvent.TYPE_WINDOWS_CHANGED || eventType == AccessibilityEvent.CONTENT_CHANGE_TYPE_PANE_DISAPPEARED) {
                    layoutActivityWindowBinding.tvAppName.text = event.packageName
                    event.className?.let {
                        activityList.add(event.className.toString())
                        activityRecyclerAdapter.notifyDataSetChanged()
                        layoutActivityWindowBinding.rvActivityList.scrollToPosition(activityRecyclerAdapter.itemCount - 1)
                    }
                }
            }
        }
    }

    override fun onInterrupt() {
        Log.e(TAG, "onInterrupt()")
        clean()
    }

    private fun clean() {
        view?.let {
            windowManager.removeView(view)
            view = null
        }
    }

    private fun showFloatingWindow() {
        if (view == null) {
            initView()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initView() {
        layoutActivityWindowBinding =
                DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.layout_activity_window, null, false)
        layoutActivityWindowBinding.ivExtendsWindow.setOnClickListener {
            if (layoutActivityWindowBinding.rvActivityList.visibility == View.GONE) {
                layoutActivityWindowBinding.rvActivityList.visibility = View.VISIBLE
            } else {
                layoutActivityWindowBinding.rvActivityList.visibility = View.GONE
            }
        }
        layoutActivityWindowBinding.ivRemoveWindow.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                disableSelf()
                clean()
                Log.e(TAG, "disableSelf()")
            } else {
                Log.e(TAG, "view?.visibility = View.GONE")
                clean()
            }
        }
        layoutActivityWindowBinding.seekBarBg.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
//                view?.background?.alpha = progress
                Log.e(TAG, "onProgressChanged: $progress")
            }
        })
        activityRecyclerAdapter.activityList = activityList
        layoutActivityWindowBinding.rvActivityList.adapter = activityRecyclerAdapter
        layoutActivityWindowBinding.rvActivityList.layoutManager = LinearLayoutManager(this)
        layoutParams = WindowManager.LayoutParams()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            layoutParams.type = WindowManager.LayoutParams.TYPE_PHONE
        }
        layoutParams.gravity = Gravity.START or Gravity.TOP
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
        view = layoutActivityWindowBinding.root
        view?.setOnTouchListener(FloatingOnTouchListener())
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager.addView(view, layoutParams)
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
                    layoutParams.x = layoutParams.x + movedX
                    layoutParams.y = layoutParams.y + movedY
                    windowManager.updateViewLayout(view, layoutParams)
                }
            }
            return false
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        clean()
        Log.e(TAG, "onDestroy()")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.e(TAG, "onStartCommand()")
        showFloatingWindow()
        return super.onStartCommand(intent, flags, startId)
    }

}