package tech.xiaoniu.fest.bean

/**
 * @author littlefogcat
 * @email littlefogcat@foxmail.com
 */
class FactoryMethodBeanDefinition(
    beanName: String,
    beanClazz: Class<*>,
    factoryBeanName: String,
    factoryMethodName: String?,
    dependsOn: Array<String>,
    singleton: Boolean = true
) : BeanDefinition(
    beanName, beanClazz, factoryBeanName, factoryMethodName, dependsOn, singleton
)