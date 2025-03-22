package tech.xiaoniu.fest.bean

/**
 * @author littlefogcat
 * @email littlefogcat@foxmail.com
 */
interface BeanFactory {
    fun getBean(name: String): Any
    fun <T : Any> getBean(clazz: Class<T>): T
}