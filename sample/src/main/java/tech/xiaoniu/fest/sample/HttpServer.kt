package tech.xiaoniu.fest.sample

import android.content.Context
import android.widget.Toast
import tech.xiaoniu.fest.annotation.Autowired
import tech.xiaoniu.fest.annotation.Component
import tech.xiaoniu.fest.sample.mvc.MyController

/**
 * @author littlefogcat
 * @email littlefogcat@foxmail.com
 */
@Component
class HttpServer {
    @Autowired
    lateinit var myController: MyController

    fun start() {
    }

    fun sayHi(context: Context) {
        val response = myController.say("hi")
        Toast.makeText(context, response, Toast.LENGTH_SHORT).show()
    }
}