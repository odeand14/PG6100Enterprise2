package no.westerdals.zuul
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cloud.netflix.zuul.EnableZuulProxy
import org.springframework.context.annotation.Bean
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import springfox.documentation.swagger2.annotations.EnableSwagger2


@EnableZuulProxy
@SpringBootApplication
@EnableSwagger2
class GatewayApplication{
    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }
}



fun main(args: Array<String>) {
    SpringApplication.run(GatewayApplication::class.java, *args)
}