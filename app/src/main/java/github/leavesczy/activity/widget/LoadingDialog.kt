package github.leavesczy.activity.widget

import android.app.Dialog
import android.content.Context
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.widget.ImageView
import github.leavesczy.activity.R

/**
 * @Author: leavesCZY
 * @Date: 2019/6/21 10:51
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
class LoadingDialog(context: Context) : Dialog(context, R.style.LoadingDialogTheme) {

    private val ivLoading: ImageView

    private val rotateAnimation: RotateAnimation

    init {
        setContentView(R.layout.dialog_loading)
        ivLoading = findViewById(R.id.ivLoading)
        rotateAnimation = RotateAnimation(
            0f,
            360f,
            Animation.RELATIVE_TO_SELF,
            0.5f,
            Animation.RELATIVE_TO_SELF,
            0.5f
        )
        rotateAnimation.duration = 1000
        rotateAnimation.repeatCount = -1
        rotateAnimation.interpolator = LinearInterpolator()
        rotateAnimation.repeatMode = Animation.RESTART

    }

    fun start(cancelable: Boolean = false, canceledOnTouchOutside: Boolean = false) {
        setCancelable(cancelable)
        setCanceledOnTouchOutside(canceledOnTouchOutside)
        ivLoading.clearAnimation()
        ivLoading.startAnimation(rotateAnimation)
        show()
    }

    override fun cancel() {
        super.cancel()
        rotateAnimation.cancel()
        ivLoading.clearAnimation()
    }

    override fun dismiss() {
        super.dismiss()
        rotateAnimation.cancel()
        ivLoading.clearAnimation()
    }

}