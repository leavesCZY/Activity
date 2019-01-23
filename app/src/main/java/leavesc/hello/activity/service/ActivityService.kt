package leavesc.hello.activity.service

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Build
import android.os.IBinder
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.*
import android.widget.SeekBar
import leavesc.hello.activity.R
import leavesc.hello.activity.adapter.ActivityRecyclerAdapter
import leavesc.hello.activity.databinding.LayoutActivityWindowBinding

class ActivityService : Service() {

    private val TAG = "ActivityService"

    private lateinit var windowManager: WindowManager

    private lateinit var layoutParams: WindowManager.LayoutParams

    private var view: View? = null

    override fun onCreate() {
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        showFloatingWindow()
        return START_NOT_STICKY
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun showFloatingWindow() {
        if (view == null) {
            initView()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initView() {
        val layoutActivityWindowBinding: LayoutActivityWindowBinding =
            DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.layout_activity_window, null, false)
        layoutActivityWindowBinding.ivExtendsWindow.setOnClickListener {
            if (layoutActivityWindowBinding.rvActivityList.visibility == View.GONE) {
                layoutActivityWindowBinding.rvActivityList.visibility = View.VISIBLE
            } else {
                layoutActivityWindowBinding.rvActivityList.visibility = View.GONE
            }
        }
        layoutActivityWindowBinding.ivRemoveWindow.setOnClickListener {
            stopSelf()
        }
        layoutActivityWindowBinding.seekBarBg.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
//                view?.background?.alpha = progress
                Log.e(TAG, "onProgressChanged: " + progress)
            }
        })

        val activityList = MutableList(30) {
            it.toString()
        }
        val activityRecyclerAdapter = ActivityRecyclerAdapter()
        activityRecyclerAdapter.activityList = activityList
        layoutActivityWindowBinding.rvActivityList.adapter = activityRecyclerAdapter
        layoutActivityWindowBinding.rvActivityList.layoutManager = LinearLayoutManager(this)

        view = layoutActivityWindowBinding.root
        view?.setOnTouchListener(FloatingOnTouchListener())

        layoutParams = WindowManager.LayoutParams()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            layoutParams.type = WindowManager.LayoutParams.TYPE_PHONE
        }
//        layoutParams.format = PixelFormat.RGBA_8888
        layoutParams.gravity = Gravity.START or Gravity.TOP
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT

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
        view?.let {
            windowManager.removeView(view)
            view = null
        }
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

}