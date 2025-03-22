package tech.xiaoniu.fest.annotation

/**
 * @author littlefogcat
 * @email littlefogcat@foxmail.com
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class Bean(
    val name: String = ""
)
