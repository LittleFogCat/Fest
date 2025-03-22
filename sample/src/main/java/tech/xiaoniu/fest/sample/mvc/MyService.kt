package tech.xiaoniu.fest.sample.mvc

import tech.xiaoniu.fest.annotation.Autowired
import tech.xiaoniu.fest.annotation.Component

/**
 * @author littlefogcat
 * @email littlefogcat@foxmail.com
 */
@Component
class MyService {
    @Autowired
    lateinit var myRepository: MyRepository

    fun sayHello(msg: String): String {
        val user = myRepository.getUser()
        return "I received your message '$msg', ${user.name}."
    }
}