package leavesc.hello.activity.holder

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import leavesc.hello.activity.model.ApplicationLocal
import java.security.MessageDigest

/**
 * 作者：leavesC
 * 时间：2019/1/2 20:42
 * 描述：
 * GitHub：https://github.com/leavesC
 * Blog：https://www.jianshu.com/u/9df45b87cfdf
 */
object AppInfoHolder {

    private enum class ApplicationType {
        AllApplication, NonSystemApplication, SystemApplication
    }

    private val appMap = mutableMapOf<String, ApplicationLocal>()

    fun init(context: Context) {
        appMap.clear()
        val packageInfoList =
            context.packageManager.getInstalledPackages(PackageManager.GET_SIGNATURES)
        for (packageInfo in packageInfoList) {
            val applicationInfo = packageInfo.applicationInfo
            val application = ApplicationLocal(
                packageName = packageInfo.packageName,
                versionName = packageInfo.versionName,
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
            appMap[application.name] = application
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
            var hexStr = Integer.toString(keyDatum.toInt() and 255, 16)
            if (hexStr.length == 1) {
                hexStr = "0$hexStr"
            }
            strBuilder.append(hexStr)
        }
        return strBuilder.toString()
    }

    /**
     * 获取设备的应用信息
     */
    private fun getApplicationInfo(
        context: Context,
        applicationType: ApplicationType
    ): MutableList<ApplicationLocal> {
        if (appMap.isEmpty()) {
            init(context)
        }
        val applicationList = mutableListOf<ApplicationLocal>()
        when (applicationType) {
            ApplicationType.AllApplication -> {
                applicationList.addAll(appMap.values)
            }
            ApplicationType.SystemApplication -> {
                applicationList.addAll(appMap.filter { entry -> entry.value.isSystemApp }.values)
            }
            ApplicationType.NonSystemApplication -> {
                applicationList.addAll(appMap.filter { entry -> !entry.value.isSystemApp }.values)
            }
        }
        return applicationList
    }

    /**
     * 判断是否是系统应用
     */
    private fun isSystemApplication(packageInfo: PackageInfo): Boolean {
        return packageInfo.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM != 0
    }

    /**
     * 获取设备所有的应用
     */
    fun getAllApplication(context: Context): MutableList<ApplicationLocal> {
        return getApplicationInfo(
            context,
            ApplicationType.AllApplication
        )
    }

    /**
     * 获取设备所有的系统应用
     */
    fun getAllSystemApplication(context: Context): List<ApplicationLocal> {
        return getApplicationInfo(
            context,
            ApplicationType.SystemApplication
        )
    }

    /**
     * 获取设备所有的非系统应用
     */
    fun getAllNonSystemApplication(context: Context): List<ApplicationLocal> {
        return getApplicationInfo(
            context,
            ApplicationType.NonSystemApplication
        )
    }

    fun getAppName(packageName: String): String? {
        for (value in appMap.values) {
            if (value.packageName == packageName) {
                return value.name
            }
        }
        return null
    }

}