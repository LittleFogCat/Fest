package tech.xiaoniu.fest.bean

import java.lang.reflect.Executable

/**
 * The definition of a bean.
 *
 * @author littlefogcat
 * @email littlefogcat@foxmail.com
 */
open class BeanDefinition(
    val beanName: String,
    val beanClazz: Class<*>,
    val factoryBeanName: String?,
    val factoryMethodName: String?,
    val dependsOn: Array<String>,
    val singleton: Boolean = true
) {
    override fun toString(): String {
        return "BeanDefinition(beanName='$beanName', " +
                "beanClazz=$beanClazz, " +
                (factoryBeanName?.let { "factoryBeanName=$it, " } ?: "") +
                (factoryMethodName?.let { "factoryMethodName=$it, " } ?: "") +
                (if (dependsOn.isEmpty()) "" else "dependsOn=${dependsOn.contentToString()}, ") +
                (if (singleton) "S)" else "N)")
    }
}