package tech.xiaoniu.fest.util

/**
 * @author littlefogcat
 * @email littlefogcat@foxmail.com
 */
object ReflectUtil {
    fun getFieldValue(obj: Any, fieldName: String): Any? = try {
        val clazz = obj.javaClass
        val field = clazz.getDeclaredField(fieldName)
        field.isAccessible = true
        field.get(obj)
    } catch (e: Exception) {
        null
    }
}