package tech.xiaoniu.fest.util

/**
 * @author littlefogcat
 * @email littlefogcat@foxmail.com
 */
object Utils {
    fun Map<*, *>.prettify(indent: Int = 2): String {
        if (isEmpty()) return "{}"
        return entries.joinToString(",\n", "{\n", "\n${" ".repeat(indent - 2)}}") {
            val key = it.key.let { k -> if (k is Map<*, *>) k.prettify(indent + 2) else k }
            val value = it.value.let { v -> if (v is Map<*, *>) v.prettify(indent + 2) else v }
            "${" ".repeat(indent)}${key}=${value}"
        }
    }

}