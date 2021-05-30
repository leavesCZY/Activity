package github.leavesc.activity.holder

import android.content.Context
import github.leavesc.activity.BaseApplication

/**
 * 作者：leavesC
 * 时间：2019/1/17 23:36
 * 描述：
 * GitHub：https://github.com/leavesC
 */
object ContextHolder {

    val context: Context by lazy { BaseApplication.appContext }

}