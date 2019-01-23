package leavesc.hello.activity.service

import android.accessibilityservice.AccessibilityService
import android.util.Log
import android.view.accessibility.AccessibilityEvent

class ActivityAccessibilityService : AccessibilityService() {

    private val TAG = "AccessibilityService"

    override fun onInterrupt() {
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        event?.let {
            val eventType = event.eventType
//        if (eventType == AccessibilityEvent.TYPE_WINDOWS_CHANGED || eventType == AccessibilityEvent.CONTENT_CHANGE_TYPE_PANE_DISAPPEARED) {
            if (eventType == AccessibilityEvent.CONTENT_CHANGE_TYPE_PANE_DISAPPEARED) {
                val className = event.className
                if (className != null) {
                    Log.e(TAG, "className: " + className.toString())
                }
                Log.e(TAG, "getPackageName: " + event.getPackageName())
            }
        }
    }

}
