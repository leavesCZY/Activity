package github.leavesczy.activity.holder

import android.accessibilityservice.AccessibilityService
import android.content.ClipData
import android.content.ClipboardManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.text.TextUtils
import android.widget.Toast

/**
 * @Author: leavesCZY
 * @Date: 2024/5/4 0:49
 * @Desc:
 */
object AccessibilityUtils {

    val Context.canDrawOverlays
        get() = Settings.canDrawOverlays(this)

    fun Context.clipboardCopy(msg: String) {
        val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboardManager.setPrimaryClip(ClipData.newPlainText("Activity", msg))
    }

    fun Context.showToast(msg: String) {
        if (msg.isNotBlank()) {
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        }
    }

    fun accessibilityServiceIsEnabled(
        context: Context,
        accessibilityService: Class<out AccessibilityService>
    ): Boolean {
        val enabledServicesSetting = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        ) ?: return false
        val colonSplitter = TextUtils.SimpleStringSplitter(':')
        colonSplitter.setString(enabledServicesSetting)
        while (colonSplitter.hasNext()) {
            val enabledService = ComponentName.unflattenFromString(colonSplitter.next())
            if (enabledService != null && enabledService == ComponentName(
                    context,
                    accessibilityService
                )
            ) {
                return true
            }
        }
        return false
    }

    fun navToAccessibilityServiceSettingPage(context: Context) {
        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
    }

}