package tech.xiaoniu.fest.sample.mvc

import tech.xiaoniu.fest.annotation.Autowired
import tech.xiaoniu.fest.annotation.Component
import tech.xiaoniu.fest.annotation.Controller

/**
 * @author littlefogcat
 * @email littlefogcat@foxmail.com
 */
@Controller
class MyController {
    @Autowired
    private lateinit var myService: MyService

    fun say(msg: String): String {
        return myService.sayHello(msg)
    }
}