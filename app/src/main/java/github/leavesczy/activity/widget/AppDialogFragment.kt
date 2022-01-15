package github.leavesczy.activity.widget

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatDialogFragment
import github.leavesczy.activity.R
import github.leavesczy.activity.extend.clipboardCopy
import github.leavesczy.activity.extend.showToast
import github.leavesczy.activity.model.ApplicationLocal
import java.io.File

/**
 * @Author: leavesCZY
 * @Date: 2019/1/18 0:07
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
class AppDialogFragment : AppCompatDialogFragment() {

    lateinit var applicationInfo: ApplicationLocal

    init {
        setStyle(STYLE_NO_TITLE, 0)
        isCancelable = true
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_app_share, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<ImageView>(R.id.ivAppIcon).setImageDrawable(applicationInfo.icon)
        view.findViewById<TextView>(R.id.tvAppName).text = applicationInfo.name
        view.findViewById<TextView>(R.id.tvAppPackage).text = applicationInfo.packageName
        view.findViewById<TextView>(R.id.tvAppVersionName).text = applicationInfo.versionNameFormat
        view.findViewById<TextView>(R.id.tvAppTargetSdkVersion).text =
            applicationInfo.targetSdkVersionFormat
        view.findViewById<TextView>(R.id.tvAppMinSdkVersion).text =
            applicationInfo.minSdkVersionFormat
        view.findViewById<TextView>(R.id.tvAppVersionCode).text =
            applicationInfo.versionCodeFormat
        view.findViewById<TextView>(R.id.tvAppSigMd5).text = applicationInfo.sigMd5Format
        view.findViewById<TextView>(R.id.tvAppSize).text = applicationInfo.apkSizeFormat
        view.findViewById<TextView>(R.id.tvAppFirstInstallTime).text =
            applicationInfo.firstInstallTimeFormat
        view.findViewById<TextView>(R.id.tvAppLastUpdateTime).text =
            applicationInfo.lastInstallTimeFormat
        view.findViewById<TextView>(R.id.tvAppSourceDir).text = applicationInfo.sourceDirFormat
        view.findViewById<View>(R.id.ivAppSettings).setOnClickListener {
            context?.let {
                openAppSettings(it, applicationInfo.packageName)
            }
        }
        view.findViewById<View>(R.id.ivAppCopy).setOnClickListener {
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