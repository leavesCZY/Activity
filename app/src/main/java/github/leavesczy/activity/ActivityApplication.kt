package github.leavesczy.activity

import android.app.Application

/**
 * @Author: leavesCZY
 * @Date: 2019/1/16 23:19
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
class ActivityApplication : Application() {

    companion object {

        lateinit var context: Application

    }

    override fun onCreate() {
        super.onCreate()
        context = this
    }

}