package tech.xiaoniu.fest.exception

/**
 * @author littlefogcat
 * @email littlefogcat@foxmail.com
 */
class CircularDependencyException(
    first: String,
    second: String
) : BeanException("Circular dependency found for $first and $second")