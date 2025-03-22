package tech.xiaoniu.fest.annotation

/**
 * @author littlefogcat
 * @email littlefogcat@foxmail.com
 */
@Component
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class Configuration(
    val name: String = ""
)
