package leavesc.hello.activity.utils

import android.content.ClipboardManager
import android.content.Context

/**
 * 作者：leavesC
 * 时间：2019/3/5 1:09
 * 描述：
 * GitHub：https://github.com/leavesC
 * Blog：https://www.jianshu.com/u/9df45b87cfdf
 */
object SystemUtils {

    fun clipboardCopy(context: Context, msg: String) {
        val clipboardManager: ClipboardManager =
            context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
//        clipboardManager.primaryClip = ClipData.newPlainText("leavesC", msg)
    }

}