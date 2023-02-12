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
import github.leavesczy.activity.model.AppInfo

/**
 * @Author: leavesCZY
 * @Date: 2019/1/18 0:07
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
class AppInfoDialog : AppCompatDialogFragment() {

    var applicationInfo: AppInfo? = null

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
        val mApplicationInfo = applicationInfo
        if (mApplicationInfo == null) {
            dismiss()
            return
        }
        view.findViewById<ImageView>(R.id.ivAppIcon).setImageDrawable(mApplicationInfo.icon)
        view.findViewById<TextView>(R.id.tvAppName).text = mApplicationInfo.name
        view.findViewById<TextView>(R.id.tvAppPackage).text = mApplicationInfo.packageName
        view.findViewById<TextView>(R.id.tvAppVersionName).text = mApplicationInfo.versionNameFormat
        view.findViewById<TextView>(R.id.tvAppTargetSdkVersion).text =
            mApplicationInfo.targetSdkVersionFormat
        view.findViewById<TextView>(R.id.tvAppMinSdkVersion).text =
            mApplicationInfo.minSdkVersionFormat
        view.findViewById<TextView>(R.id.tvAppVersionCode).text =
            mApplicationInfo.versionCodeFormat
        view.findViewById<TextView>(R.id.tvAppSigMd5).text = mApplicationInfo.sigMd5Format
        view.findViewById<TextView>(R.id.tvAppSize).text = mApplicationInfo.apkSizeFormat
        view.findViewById<TextView>(R.id.tvAppFirstInstallTime).text =
            mApplicationInfo.firstInstallTimeFormat
        view.findViewById<TextView>(R.id.tvAppLastUpdateTime).text =
            mApplicationInfo.lastInstallTimeFormat
        view.findViewById<TextView>(R.id.tvAppSourceDir).text = mApplicationInfo.sourceDirFormat
        view.findViewById<View>(R.id.ivAppSettings).setOnClickListener {
            val context = activity
            context?.let {
                openAppSettings(it, mApplicationInfo.packageName)
            }
        }
        view.findViewById<View>(R.id.ivAppCopy).setOnClickListener {
            val context = activity
            context?.let {
                it.clipboardCopy(mApplicationInfo.toString())
                it.showToast("已复制应用信息")
            }
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