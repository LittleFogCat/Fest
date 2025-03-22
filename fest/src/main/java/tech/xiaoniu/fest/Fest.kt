package tech.xiaoniu.fest

import android.content.Context
import tech.xiaoniu.fest.bean.BeanFactory
import tech.xiaoniu.fest.bean.SingletonBeanFactory

/**
 * @author littlefogcat
 * @email littlefogcat@foxmail.com
 */
object Fest : BeanFactory {
    private val TAG = javaClass.simpleName

    private lateinit var beanFactory: BeanFactory

    fun install(context: Context) {
        beanFactory = SingletonBeanFactory().apply {
            initialize(context)
        }
    }

    override fun getBean(name: String): Any {
        return beanFactory.getBean(name)
    }

    override fun <T : Any> getBean(clazz: Class<T>): T {
        return beanFactory.getBean(clazz)
    }
}