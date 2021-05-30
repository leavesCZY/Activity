package github.leavesc.activity.widget

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDialogFragment
import github.leavesc.activity.R
import github.leavesc.activity.extend.clipboardCopy
import github.leavesc.activity.extend.showToast
import github.leavesc.activity.model.ApplicationLocal
import kotlinx.android.synthetic.main.dialog_app_share.*
import java.io.File

/**
 * 作者：leavesC
 * 时间：2019/1/18 0:07
 * 描述：
 * GitHub：https://github.com/leavesC
 */
class AppDialogFragment : AppCompatDialogFragment() {

    lateinit var applicationInfo: ApplicationLocal

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_app_share, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isCancelable = true
        iv_icon.setImageDrawable(applicationInfo.icon)
        tv_appName.text = applicationInfo.name
        tv_appPackage.text = applicationInfo.packageName
        tv_appVersionName.text = applicationInfo.versionNameFormat
        tv_appTargetSdkVersion.text = applicationInfo.targetSdkVersionFormat
        tv_minSdkVersion.text = applicationInfo.minSdkVersionFormat
        tv_appLongVersionCode.text = applicationInfo.longVersionCodeFormat
        tv_sigMd5.text = applicationInfo.sigMd5Format
        tv_appSize.text = applicationInfo.apkSizeFormat
        tv_appFirstInstallTime.text = applicationInfo.firstInstallTimeFormat
        tv_appLastUpdateTime.text = applicationInfo.lastInstallTimeFormat
        tv_sourceDir.text = applicationInfo.sourceDirFormat
        iv_appSettings.setOnClickListener {
            context?.let {
                openAppSettings(it, applicationInfo.packageName)
            }
        }
        iv_appCopy.setOnClickListener {
            val context = activity
            context?.let {
                it.clipboardCopy(applicationInfo.toString())
                it.showToast("已复制应用信息")
            }
        }
    }

    private fun shareApp(sourceDir: String) {
        val apkFile = File(sourceDir)
        val intent = Intent()
        intent.action = Intent.ACTION_SEND
        intent.type = "*/*"
        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(apkFile))
        try {
            startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun openAppSettings(context: Context, packageName: String) {
        val intent = Intent()
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        intent.data = Uri.fromParts("package", packageName, null)
        context.startActivity(intent)
    }

}