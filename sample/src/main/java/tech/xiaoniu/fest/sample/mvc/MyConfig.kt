package tech.xiaoniu.fest.sample.mvc

import tech.xiaoniu.fest.annotation.Bean
import tech.xiaoniu.fest.annotation.Configuration
import tech.xiaoniu.fest.sample.bean.User

/**
 * @author littlefogcat
 * @email littlefogcat@foxmail.com
 */
@Configuration
class MyConfig {

    @Bean
    fun user(): User {
        return User("xiaoniu", 18)
    }
}