package tech.xiaoniu.fest.exception

/**
 * @author littlefogcat
 * @email littlefogcat@foxmail.com
 */
class NoSuchBeanException(
    beanName: String
) : BeanException(
    "Cannot find bean named `$beanName`"
)