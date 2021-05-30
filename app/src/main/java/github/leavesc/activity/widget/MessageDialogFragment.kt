package github.leavesc.activity.widget

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager

/**
 * 作者：leavesC
 * 时间：2019/1/27 11:17
 * 描述：
 * GitHub：https://github.com/leavesC
 */
class MessageDialogFragment : DialogFragment() {

    private var positiveCallback: DialogInterface.OnClickListener? = null

    private var negativeCallback: DialogInterface.OnClickListener? = null

    private var title: String? = null

    private var message: String? = null

    fun init(
        title: String,
        message: String,
        positiveCallback: DialogInterface.OnClickListener,
        negativeCallback: DialogInterface.OnClickListener? = null
    ) {
        this.title = title
        this.message = message
        this.positiveCallback = positiveCallback
        this.negativeCallback = negativeCallback
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(context!!)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton("确定", positiveCallback)
        builder.setNegativeButton("取消", negativeCallback)
        return builder.create()
    }

    override fun show(manager: FragmentManager, tag: String?) {
        val ft = manager.beginTransaction()
        ft.add(this, tag)
        ft.commitAllowingStateLoss()
    }

}