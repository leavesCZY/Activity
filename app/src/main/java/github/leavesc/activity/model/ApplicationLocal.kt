package github.leavesc.activity.model

import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.text.format.Formatter
import github.leavesc.activity.holder.ContextHolder
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 * 作者：leavesC
 * 时间：2019/1/2 20:45
 * 描述：
 * GitHub：https://github.com/leavesC
 */
data class ApplicationLocal(
    val name: String,
    val packageName: String,
    val icon: Drawable,
    val versionName: String,
    val targetSdkVersion: Int,
    val minSdkVersion: Int = 0,
    val longVersionCode: Long,
    val firstInstallTime: Long,
    val lastUpdateTime: Long,
    val sourceDir: String,
    val isSystemApp: Boolean,
    val dataDir: String?,
    val sigMd5: String
) {

    override fun toString(): String {
        return "$name\n$packageName\n$versionNameFormat\n$targetSdkVersionFormat\n" +
                "$minSdkVersionFormat\n$longVersionCodeFormat\n$sigMd5Format\n$apkSizeFormat" +
                "$firstInstallTimeFormat\n$lastInstallTimeFormat\n$sourceDirFormat"
    }

    val sourceDirFormat: String
        get() = "sourceDir：$sourceDir"

    val minSdkVersionFormat: String
        get() = "minSdkVersion：$minSdkVersion"

    val sigMd5Format: String
        get() = "md5：$sigMd5"

    val versionNameFormat: String
        get() = "versionName：$versionName"

    val longVersionCodeFormat: String
        get() = "longVersionCode：$longVersionCode"

    val targetSdkVersionFormat: String
        get() = "targetSdkVersion：$targetSdkVersion"

    val firstInstallTimeFormat: String
        get() {
            val size = if (firstInstallTime == 0L) {
                ""
            } else {
                val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA)
                simpleDateFormat.format(Date(firstInstallTime))
            }
            return "firstInstallTime：$size"
        }

    val lastInstallTimeFormat: String
        get() {
            val size = if (lastUpdateTime == 0L) {
                ""
            } else {
                val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA)
                simpleDateFormat.format(Date(lastUpdateTime))
            }
            return "lastUpdateTime：$size"
        }

    val apkSizeFormat: String
        get() {
            val size = if (TextUtils.isEmpty(sourceDir)) {
                ""
            } else {
                Formatter.formatShortFileSize(ContextHolder.context, File(sourceDir).length())
            }
            return "apkSize：$size"
        }

}