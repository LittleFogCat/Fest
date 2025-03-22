package tech.xiaoniu.fest.bean

import android.content.Context
import android.util.Log
import tech.xiaoniu.fest.annotation.Autowired
import tech.xiaoniu.fest.annotation.Bean
import tech.xiaoniu.fest.annotation.Component
import tech.xiaoniu.fest.annotation.Configuration
import tech.xiaoniu.fest.exception.BeanException
import tech.xiaoniu.fest.exception.BeanTypeMismatchException
import tech.xiaoniu.fest.exception.CircularDependencyException
import tech.xiaoniu.fest.exception.DuplicatedBeanDefinitionException
import tech.xiaoniu.fest.exception.NoSuchBeanException
import tech.xiaoniu.fest.util.ClassUtils
import tech.xiaoniu.fest.util.ReflectUtil
import tech.xiaoniu.fest.util.Utils.prettify
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.util.concurrent.ConcurrentHashMap

/**
 * @author littlefogcat
 * @email littlefogcat@foxmail.com
 */
@Suppress("FoldInitializerAndIfToElvis")
class SingletonBeanFactory : BeanFactory {
    private val TAG = "BeanContainer"

    private val beanDefinitionMap = ConcurrentHashMap<String, BeanDefinition>()
    private val beans = ConcurrentHashMap<String, Any>()

    private val beanDependenciesMap = ConcurrentHashMap<String, Set<String>>()
    private val beanNamesByType = ConcurrentHashMap<Class<*>, String>()

    // Initialize the bean factory.
    // Generate bean definitions and instantiate singletons.
    fun initialize(context: Context) {
        val classes = ClassUtils.getFileNameByPackageName(context, context.packageName)

        registerBeanDefinitions(classes)
        Log.i(TAG, "initialize: ${beanDefinitionMap.prettify()}")
        doInstantiation()
    }

    override fun getBean(name: String): Any {
        val bean = beans[name]
        if (bean != null) {
            return bean
        }
        return instantiateBean(name, getBeanDefinition(name))
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : Any> getBean(clazz: Class<T>): T {
        return getBean(getBeanNameForClass(clazz)) as T
    }

    private fun registerBeanDefinitions(classNames: MutableSet<String>) {
        for (className in classNames) {
            val clazz = Class.forName(className)
            registerBeanDefinition(clazz)
        }
    }

    // A bean has 2 ways to be registered:
    // 1. @Component or its derived annotation on a class
    // 2. @Bean annotation on a method in a @Configuration class
    private fun registerBeanDefinition(clazz: Class<*>) {

        registerBeanDefinitionForComponent(clazz)

        if (clazz.isAnnotationPresent(Configuration::class.java)) {
            val configuration = clazz.getAnnotation(Configuration::class.java)
            if (configuration != null) {
                val methods = clazz.declaredMethods
                val factoryBeanName = configuration.name.ifEmpty { getDefaultBeanNameForClass(clazz) }
                for (method in methods) {
                    if (method.isAnnotationPresent(Bean::class.java)) {
                        registerBeanDefinitionForFactoryMethod(method, factoryBeanName)
                    }
                }
            }
        }
    }

    private fun registerBeanDefinitionForComponent(clazz: Class<*>) {

        val beanName = getBeanNameSpecifiedByAnnotationForClass(clazz)
        if (beanName == null) {
            // It's not a component
            return
        }
        val depends = getDepends(beanName, clazz)
        val bd = ComponentBeanDefinition(beanName, clazz, depends)
        val prev = beanDefinitionMap[beanName]
        if (prev != null) {
            if (prev is ComponentBeanDefinition) {
                // Two classes annotated with @Component have the same name?
                throw DuplicatedBeanDefinitionException(beanName, bd, prev)
            }
            // Once a FactoryMethodBeanDefinition is set, me is ignored.
            return
        }
        // Just do it
        beanDefinitionMap[beanName] = bd
    }

    /**
     * Gets and returns the bean name specified by @Component or its derived annotation.
     * If no name specified in the annotation, returns the default bean name for the class
     * by [getDefaultBeanNameForClass].
     * If the class is not annotated by the annotations above, returns null.
     */
    private fun getBeanNameSpecifiedByAnnotationForClass(clazz: Class<*>): String? {
//        Log.i(TAG, "getBeanNameSpecifiedByAnnotationForClass: $clazz")
        if (clazz.simpleName == "MyConfig") {
            println("Do")
        }
        // Check if this class is annotated by @Component.
        // If so, use the name specified in @Component annotation.
        val component = clazz.getAnnotation(Component::class.java)
        if (component != null) {
//            Log.d(TAG, "getBeanNameSpecifiedByAnnotationForClass: It's a Component")
            return component.name.ifEmpty { getDefaultBeanNameForClass(clazz) }
        }

        // Check if this class is annotated by an stereotype annotation.
        // If so, get the `name` or `value` property as the bean name if exists.
        val annotations = clazz.declaredAnnotations.filter {
            it.annotationClass.java.isAnnotationPresent(Component::class.java)
        }
        if (annotations.isEmpty()) {
            return null
        }

        // We just handle the first annotation
        // Accept `value` or `name` property as the bean name
        val annotation = annotations[0]
//        Log.d(TAG, "getBeanNameSpecifiedByAnnotationForClass: It's a ${annotation.javaClass.simpleName}")
        val value = ReflectUtil.getFieldValue(annotation, "value")
        if (value is String && value.isNotEmpty()) {
            return value
        }
        val name = ReflectUtil.getFieldValue(annotation, "name")
        if (name is String && name.isNotEmpty()) {
            return name
        }
        return getDefaultBeanNameForClass(clazz)
    }

    private fun registerBeanDefinitionForFactoryMethod(method: Method, factoryBeanName: String) {
        val beanAnnotation = method.getAnnotation(Bean::class.java)
        if (beanAnnotation == null) {
            throw BeanException("The method ${method.name} has no @Bean annotation.")
        }

        // Use method name as bean name if not specified
        val beanName = beanAnnotation.name.ifEmpty { method.name }
        val depends = getDepends(beanName, method.returnType)
        val bd = FactoryMethodBeanDefinition(
            beanName,
            method.returnType,
            factoryBeanName,
            method.name,
            depends
        )
        val prev = beanDefinitionMap[beanName]
        if (prev is FactoryMethodBeanDefinition) {
            // We just allow one bean definition
            throw DuplicatedBeanDefinitionException(beanName, bd, prev)
        }
        beanDefinitionMap[beanName] = bd
    }

    private fun getDepends(beanName: String, clazz: Class<*>): Array<String> {
        val fields = clazz.declaredFields
        val dependencies = fields.mapNotNull { field ->
            // Filter fields with @Autowired annotation and map to bean name.
            field.isAccessible = true
            if (!field.isAnnotationPresent(Autowired::class.java)) {
//                Log.d(TAG, "getDepends: ${field.name} is not annotated by @Autowired")
                return@mapNotNull null
            }
            val fieldBeanName: String = field.getAnnotation(Autowired::class.java)!!.name.ifEmpty { field.name }
            val fieldDependencies = beanDependenciesMap[fieldBeanName]
            if (fieldDependencies != null && fieldDependencies.contains(beanName)) {
                throw CircularDependencyException(beanName, fieldBeanName)
            }
            return@mapNotNull fieldBeanName
        }.toTypedArray()
        beanDependenciesMap[beanName] = dependencies.toSet()
        return dependencies
    }

    private fun doInstantiation() {
        for ((beanName, bd) in beanDefinitionMap) {
            // We just instantiate singleton beans
            if (!bd.singleton) {
                continue
            }
//            Log.i(TAG, "doInstantiation: $beanName")
            getBean(beanName)
//            Log.d(TAG, "doInstantiation: ${getBean(beanName)}")
        }
    }

    private fun instantiateBean(beanName: String, bd: BeanDefinition): Any {
        // We first ensure all dependencies is initialized
        val depends = bd.dependsOn
        for (depend in depends) {
            getBean(depend)
        }
        val result = when (bd) {
            is ComponentBeanDefinition -> instantiateComponent(beanName, bd)
            is FactoryMethodBeanDefinition -> instantiateUsingFactoryMethod(beanName, bd)
            else -> throw BeanException("Cannot instantiate bean `$beanName`. Unexpected BeanDefinition type: ${bd.javaClass}")
        }
        applyDependencies(result, bd.beanClazz, bd)
        beanNamesByType[bd.beanClazz] = beanName
        return result
    }

    private fun instantiateComponent(beanName: String, bd: ComponentBeanDefinition): Any {
        val clazz = bd.beanClazz
        val result = try {
            clazz.getDeclaredConstructor().newInstance()
        } catch (e: Exception) {
            throw BeanException("Cannot instantiate bean `$beanName`", e)
        }
        return result
    }

    private fun instantiateUsingFactoryMethod(beanName: String, bd: FactoryMethodBeanDefinition): Any {
        val factoryBeanName = bd.factoryBeanName ?: throw BeanException("factoryBeanName == null for $bd")
        val factoryMethodName = bd.factoryMethodName ?: throw BeanException("factoryMethodName == null for $bd")
        val factoryBean = getBean(factoryBeanName)
        val factoryMethod = factoryBean.javaClass.getDeclaredMethod(factoryMethodName)
        val result = factoryMethod.invoke(factoryBean)
        if (result == null || !bd.beanClazz.isInstance(result)) {
            throw BeanTypeMismatchException(beanName, bd.beanClazz, result?.javaClass)
        }
        return result
    }

    private fun applyDependencies(obj: Any, clazz: Class<*>, bd: BeanDefinition) {
        for (field in clazz.declaredFields) {
            val autowired = field.getAnnotation(Autowired::class.java)
            if (autowired == null) {
                continue
            }
            field.isAccessible = true
            val beanName = getBeanNameForAutowiredField(field, autowired)
            val bean = getBean(beanName)
            if (!field.type.isInstance(bean)) {
                throw BeanTypeMismatchException(beanName, field.type, bean.javaClass)
            }
            field.set(obj, getBean(beanName))
        }
    }

    private fun getDefaultBeanNameForClass(clazz: Class<*>): String {


        val clazzName = clazz.simpleName
        if (clazzName.isEmpty()) {
            return clazzName
        }
        if (clazzName.length == 1) {
            return clazzName.lowercase()
        }
        return clazzName[0].lowercase() + clazzName.substring(1)
    }

    private fun getBeanNameForClass(clazz: Class<*>): String {
        val name = beanNamesByType[clazz]
        return name ?: getDefaultBeanNameForClass(clazz)
    }

    private fun getBeanNameForAutowiredField(field: Field, autowired: Autowired): String {
        return autowired.name.ifEmpty { field.name } ?: field.name
    }

    private fun getBeanDefinition(beanName: String): BeanDefinition {
        val bd = beanDefinitionMap[beanName]
        if (bd != null) {
            return bd
        }
        throw NoSuchBeanException(beanName)
    }

}