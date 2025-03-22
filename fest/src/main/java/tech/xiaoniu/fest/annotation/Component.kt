package tech.xiaoniu.fest.annotation

/**
 * @author littlefogcat
 * @email littlefogcat@foxmail.com
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
@MustBeDocumented
annotation class Component(
    val name: String = ""
)
