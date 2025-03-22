package tech.xiaoniu.fest.bean

/**
 * @author littlefogcat
 * @email littlefogcat@foxmail.com
 */
class ComponentBeanDefinition(
    beanName: String,
    beanClazz: Class<*>,
    dependsOn: Array<String>,
    singleton: Boolean = true,
) : BeanDefinition(
    beanName, beanClazz, null, null, dependsOn, singleton
)