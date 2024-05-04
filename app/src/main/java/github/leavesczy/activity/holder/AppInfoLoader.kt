package github.leavesczy.activity.holder

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import github.leavesczy.activity.model.ApplicationDetail
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.security.MessageDigest

/**
 * @Author: leavesCZY
 * @Date: 2024/5/4 0:55
 * @Desc:
 */
enum class ApplicationType {
    AllApplication,
    NonSystemApplication,
    SystemApplication
}

object AppInfoLoader {

    private val appCache = mutableMapOf<String, ApplicationDetail>()

    suspend fun init(context: Context) {
        withContext(context = Dispatchers.Default) {
            val map = mutableMapOf<String, ApplicationDetail>()
            val packageInfoList =
                context.packageManager.getInstalledPackages(PackageManager.GET_SIGNATURES)
            for (packageInfo in packageInfoList) {
                val applicationInfo = packageInfo.applicationInfo
                val applicationDetail = ApplicationDetail(
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
                map[applicationDetail.name] = applicationDetail
            }
            synchronized(lock = this@AppInfoLoader) {
                appCache.clear()
                appCache.putAll(map)
            }
        }
    }

    private suspend fun getSignValidString(signatures: ByteArray): String {
        return withContext(context = Dispatchers.Default) {
            val messageDigest = MessageDigest.getInstance("MD5")
            messageDigest.update(signatures)
            return@withContext toHexString(messageDigest.digest())
        }
    }

    private suspend fun toHexString(keyData: ByteArray): String {
        return withContext(context = Dispatchers.Default) {
            val strBuilder = StringBuilder(keyData.size * 2)
            for (keyDatum in keyData) {
                var hexStr = (keyDatum.toInt() and 255).toString(16)
                if (hexStr.length == 1) {
                    hexStr = "0$hexStr"
                }
                strBuilder.append(hexStr)
            }
            return@withContext strBuilder.toString()
        }
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

    fun filterApplications(applicationType: ApplicationType): List<ApplicationDetail> {
        return when (applicationType) {
            ApplicationType.AllApplication -> {
                appCache.values.toList()
            }

            ApplicationType.SystemApplication -> {
                appCache.values.filter {
                    it.isSystemApp
                }
            }

            ApplicationType.NonSystemApplication -> {
                appCache.values.filter {
                    !it.isSystemApp
                }
            }
        }
    }

}