package github.leavesczy.activity.holder

import android.annotation.SuppressLint
import android.content.Context

/**
 * @Author: leavesCZY
 * @Date: 2019/1/17 23:36
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
@SuppressLint("StaticFieldLeak")
object ContextHolder {

    lateinit var context: Context
        private set

    fun init(context: Context) {
        this.context = context
    }

}