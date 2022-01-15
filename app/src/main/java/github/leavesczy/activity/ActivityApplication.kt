package github.leavesczy.activity

import android.app.Application
import github.leavesczy.activity.holder.ContextHolder

/**
 * @Author: leavesCZY
 * @Date: 2019/1/16 23:19
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
class ActivityApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        ContextHolder.init(context = this)
    }

}