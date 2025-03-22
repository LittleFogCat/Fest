package tech.xiaoniu.fest.exception

/**
 * @author littlefogcat
 * @email littlefogcat@foxmail.com
 */
class BeanTypeMismatchException(
    beanName: String,
    expectedType: Class<*>,
    actualType: Class<*>?
) : BeanException(
    "Bean named '$beanName' is expected to be of type '$expectedType', but was actually of type '$actualType'"
)