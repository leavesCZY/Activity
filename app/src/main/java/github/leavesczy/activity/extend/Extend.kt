package github.leavesczy.activity.extend

import android.accessibilityservice.AccessibilityService
import android.app.Activity
import android.content.*
import android.provider.Settings
import android.text.TextUtils
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity

/**
 * @Author: leavesCZY
 * @Date: 2019/8/26 12:06
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
fun Context.accessibilityServiceIsEnabled(accessibilityService: Class<out AccessibilityService>): Boolean {
    val enabledServicesSetting =
        Settings.Secure.getString(contentResolver, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES)
            ?: return false
    val colonSplitter = TextUtils.SimpleStringSplitter(':')
    colonSplitter.setString(enabledServicesSetting)
    while (colonSplitter.hasNext()) {
        val enabledService = ComponentName.unflattenFromString(colonSplitter.next())
        if (enabledService != null && enabledService == ComponentName(this, accessibilityService)) {
            return true
        }
    }
    return false
}

fun Context.navToAccessibilityServiceSettingPage() {
    val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    startActivity(intent)
}

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

fun Activity.hideSoftKeyboard() {
    val view = currentFocus
    if (view != null) {
        val inputMethodManager =
            getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(
            view.windowToken,
            InputMethodManager.HIDE_NOT_ALWAYS
        )
    }
}

fun FragmentActivity.showDialog(dialogFragment: DialogFragment) {
    dialogFragment.show(supportFragmentManager, dialogFragment.javaClass.name)
}