package github.leavesczy.activity.model

import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.text.format.Formatter
import github.leavesczy.activity.holder.ContextHolder
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 * @Author: leavesCZY
 * @Date: 2019/1/2 20:45
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
data class AppInfo(
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
                "$minSdkVersionFormat\n$versionCodeFormat\n$sigMd5Format\n$apkSizeFormat" +
                "$firstInstallTimeFormat\n$lastInstallTimeFormat\n$sourceDirFormat"
    }

    val versionCodeFormat: String
        get() = "versionCode：$longVersionCode"

    val versionNameFormat: String
        get() = "versionName：$versionName"

    val targetSdkVersionFormat: String
        get() = "targetSdkVersion：$targetSdkVersion"

    val minSdkVersionFormat: String
        get() = "minSdkVersion：$minSdkVersion"

    val sigMd5Format: String
        get() = "md5：$sigMd5"

    val sourceDirFormat: String
        get() = "sourceDir：$sourceDir"

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