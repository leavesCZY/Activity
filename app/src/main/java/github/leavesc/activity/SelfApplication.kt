package github.leavesc.activity

import android.app.Application

/**
 * 作者：leavesC
 * 时间：2019/1/16 23:19
 * 描述：
 * GitHub：https://github.com/leavesC
 */
class SelfApplication : Application() {

    companion object {
        lateinit var appContext: Application
            private set
    }

    override fun onCreate() {
        super.onCreate()
        appContext = this
    }

}