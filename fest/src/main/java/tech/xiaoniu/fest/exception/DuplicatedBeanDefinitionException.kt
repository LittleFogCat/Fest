package tech.xiaoniu.fest.exception

import tech.xiaoniu.fest.bean.BeanDefinition

/**
 * @author littlefogcat
 * @email littlefogcat@foxmail.com
 */
class DuplicatedBeanDefinitionException(
    beanName: String,
    bd: BeanDefinition,
    prev: BeanDefinition
) : BeanException("Can't register bean `$bd` with name '$beanName' cause it's already registered as `$prev`.")