package leavesc.hello.activity.utils

import android.accessibilityservice.AccessibilityService
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.text.TextUtils

/**
 * Created by：CZY
 * Time：2019/1/24 23:40
 * Desc：
 */
object PermissionUtils {

    fun accessibilityServiceIsEnabled(
        context: Context,
        accessibilityService: Class<out AccessibilityService>
    ): Boolean {
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

    fun navToAccessibilityServiceSettingPage(context: Context) {
        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
    }

    fun canDrawOverlays(context: Context) = Settings.canDrawOverlays(context)

}