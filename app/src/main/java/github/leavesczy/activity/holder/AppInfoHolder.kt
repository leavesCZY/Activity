package github.leavesczy.activity.holder

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import github.leavesczy.activity.model.AppInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.security.MessageDigest

/**
 * @Author: leavesCZY
 * @Date: 2019/1/2 20:42
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
object AppInfoHolder {

    private enum class ApplicationType {
        AllApplication, NonSystemApplication, SystemApplication
    }

    private val lock = Any()

    private val appCache = mutableMapOf<String, AppInfo>()

    suspend fun init(context: Context) {
        withContext(context = Dispatchers.IO) {
            val map = mutableMapOf<String, AppInfo>()
            val packageInfoList =
                context.packageManager.getInstalledPackages(PackageManager.GET_SIGNATURES)
            for (packageInfo in packageInfoList) {
                val applicationInfo = packageInfo.applicationInfo
                val application = AppInfo(
                    packageName = packageInfo.packageName,
                    versionName = packageInfo.versionName ?: "",
                    targetSdkVersion = applicationInfo.targetSdkVersion,
                    minSdkVersion = if (Build.VERSION.SDK_INT > 23) applicationInfo.minSdkVersion else 0,
                    longVersionCode = packageInfo.versionCode.toLong(),
                    firstInstallTime = packageInfo.firstInstallTime,
                    lastUpdateTime = packageInfo.lastUpdateTime,
                    isSystemApp = isSystemApplication(packageInfo),
                    icon = applicationInfo.loadIcon(context.packageManager),
                    name = applicationInfo.loadLabel(context.packageManager).toString(),
                    sourceDir = applicationInfo.sourceDir,
                    dataDir = applicationInfo.dataDir,
                    sigMd5 = packageInfo.signatures?.let {
                        if (packageInfo.signatures.isNotEmpty())
                            getSignValidString(packageInfo.signatures[0].toByteArray())
                        else
                            ""
                    } ?: ""
                )
                map[application.name] = application
            }
            synchronized(lock = lock) {
                appCache.clear()
                appCache.putAll(map)
            }
        }
    }

    private fun getSignValidString(signatures: ByteArray): String {
        val messageDigest = MessageDigest.getInstance("MD5")
        messageDigest.update(signatures)
        return toHexString(messageDigest.digest())
    }

    private fun toHexString(keyData: ByteArray): String {
        val strBuilder = StringBuilder(keyData.size * 2)
        for (keyDatum in keyData) {
            var hexStr = (keyDatum.toInt() and 255).toString(16)
            if (hexStr.length == 1) {
                hexStr = "0$hexStr"
            }
            strBuilder.append(hexStr)
        }
        return strBuilder.toString()
    }

    private fun isSystemApplication(packageInfo: PackageInfo): Boolean {
        return packageInfo.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM != 0
    }

    fun getAppName(packageName: String): String {
        for (value in appCache.values) {
            if (value.packageName == packageName) {
                return value.name
            }
        }
        return ""
    }

    private fun getApplicationInfo(applicationType: ApplicationType): List<AppInfo> {
        val applicationList = mutableListOf<AppInfo>()
        when (applicationType) {
            ApplicationType.AllApplication -> {
                applicationList.addAll(appCache.values)
            }
            ApplicationType.SystemApplication -> {
                applicationList.addAll(appCache.filter { entry -> entry.value.isSystemApp }.values)
            }
            ApplicationType.NonSystemApplication -> {
                applicationList.addAll(appCache.filter { entry -> !entry.value.isSystemApp }.values)
            }
        }
        return applicationList
    }

    fun getAllApplication(): List<AppInfo> {
        return getApplicationInfo(ApplicationType.AllApplication)
    }

    fun getAllSystemApplication(): List<AppInfo> {
        return getApplicationInfo(ApplicationType.SystemApplication)
    }

    fun getAllNonSystemApplication(): List<AppInfo> {
        return getApplicationInfo(ApplicationType.NonSystemApplication)
    }

}