package tech.xiaoniu.fest.exception

/**
 * @author littlefogcat
 * @email littlefogcat@foxmail.com
 */
open class BeanException(
    message: String?,
    cause: Throwable? = null
) : RuntimeException(message, cause)