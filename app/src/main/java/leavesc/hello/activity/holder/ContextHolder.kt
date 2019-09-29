package leavesc.hello.activity.holder

import android.content.Context
import leavesc.hello.activity.BaseApplication

/**
 * 作者：leavesC
 * 时间：2019/1/17 23:36
 * 描述：
 * GitHub：https://github.com/leavesC
 * Blog：https://www.jianshu.com/u/9df45b87cfdf
 */
object ContextHolder {

    val context: Context by lazy { BaseApplication.appContext }

}