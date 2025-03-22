package tech.xiaoniu.fest.sample

import android.app.Application
import android.content.Context
import tech.xiaoniu.fest.Fest

/**
 * @author littlefogcat
 * @email littlefogcat@foxmail.com
 */
class App : Application() {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        Fest.install(this)
    }
}