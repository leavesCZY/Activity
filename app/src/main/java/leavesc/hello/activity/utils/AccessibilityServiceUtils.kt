package leavesc.hello.activity.utils

import android.accessibilityservice.AccessibilityService
import android.content.ComponentName
import android.content.Context
import android.provider.Settings
import android.text.TextUtils

/**
 * Created by：CZY
 * Time：2019/1/24 23:40
 * Desc：
 */
object AccessibilityServiceUtils {

    fun isEnabled(context: Context, accessibilityService: Class<out AccessibilityService>): Boolean {
        val enabledServicesSetting =
            Settings.Secure.getString(context.contentResolver, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES)
                ?: return false
        val colonSplitter = TextUtils.SimpleStringSplitter(':')
        colonSplitter.setString(enabledServicesSetting)
        while (colonSplitter.hasNext()) {
            val enabledService = ComponentName.unflattenFromString(colonSplitter.next())
            if (enabledService != null && enabledService == ComponentName(context, accessibilityService))
                return true
        }
        return false
    }

}