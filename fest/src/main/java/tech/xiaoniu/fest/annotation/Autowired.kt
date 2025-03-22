package tech.xiaoniu.fest.annotation

/**
 * @author littlefogcat
 * @email littlefogcat@foxmail.com
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD)
annotation class Autowired(
    val name: String = "",
)
