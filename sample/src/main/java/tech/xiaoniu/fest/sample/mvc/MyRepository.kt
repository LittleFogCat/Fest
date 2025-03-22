package tech.xiaoniu.fest.sample.mvc

import tech.xiaoniu.fest.annotation.Autowired
import tech.xiaoniu.fest.annotation.Component
import tech.xiaoniu.fest.sample.bean.User

/**
 * @author littlefogcat
 * @email littlefogcat@foxmail.com
 */
@Component
class MyRepository {
    @Autowired
    private lateinit var user: User

    fun getUser() = user
}
