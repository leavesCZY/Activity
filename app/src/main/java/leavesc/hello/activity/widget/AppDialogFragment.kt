package leavesc.hello.activity.widget

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import leavesc.hello.activity.R
import leavesc.hello.activity.databinding.DialogAppShareBinding
import leavesc.hello.activity.model.ApplicationLocal
import leavesc.hello.activity.utils.SystemUtils
import java.io.File

/**
 * 作者：leavesC
 * 时间：2019/1/18 0:07
 * 描述：
 * GitHub：https://github.com/leavesC
 * Blog：https://www.jianshu.com/u/9df45b87cfdf
 */
class AppDialogFragment : DialogFragment() {

    lateinit var applicationInfo: ApplicationLocal

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(context!!)
        val bind: DialogAppShareBinding =
            DataBindingUtil.inflate(
                LayoutInflater.from(context),
                R.layout.dialog_app_share, null, false
            )
        bind.applicationLocal = applicationInfo
        bind.ivAppSettings.setOnClickListener {
            openAppSettings(context!!, applicationInfo.packageName)
            dismiss()
        }
        bind.ivAppCopy.setOnClickListener {
            val context = activity
            context?.let {
                SystemUtils.clipboardCopy(context, applicationInfo.toString())
                Toast.makeText(activity, "已复制应用信息", Toast.LENGTH_SHORT).show()
                dismiss()
            }
        }
        builder.setView(bind.root)
        return builder.create()
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