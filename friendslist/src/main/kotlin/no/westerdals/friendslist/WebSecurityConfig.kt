package no.westerdals.friendslist

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UserDetails

@Configuration
@EnableWebSecurity
class WebSecurityConfig: WebSecurityConfigurerAdapter() {

    override fun configure(http: HttpSecurity) {

        http.httpBasic()
                .and()
                .authorizeRequests()
                .antMatchers("/**")
                .access("hasRole('USER') and @userSecurity.checkId(authentication, #id)")
//                .hasRole("USER")
                .antMatchers("/api/**/v2/api-docs","webjars/springfox-swagger-ui/**","/v2/api-docs", "/configuration/ui", "/swagger-resources", "/configuration/security", "/swagger-ui.html", "/webjars/**","/swagger-resources/configuration/ui","/swagge‌​r-ui.html")
                .permitAll()
                .anyRequest().denyAll()
                .and()
                .csrf().disable()
    }
    @Bean
    fun userSecurity() : UserSecurity {
        return UserSecurity()
    }
}

class UserSecurity {

    fun checkId(authentication: Authentication, id: String) : Boolean {

        val current = (authentication.principal as UserDetails).username

        return current == id
    }
}