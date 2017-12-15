package no.westerdals.userservice

// Created by Andreas Ødegaard on 10.12.2017.

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
                /*
                     ?  matches one character
                     *  matches zero or more characters
                     ** matches zero or more directories in a path
                     {foo:[a-z]+} matches the regexp [a-z]+ as a path variable named "foo"
                 */
                .antMatchers("/usersInfoCount").permitAll()
                .antMatchers("/usersInfo").hasRole("ADMIN")
                //
                .antMatchers("/api/**/v2/api-docs","webjars/springfox-swagger-ui/**","/v2/api-docs", "/configuration/ui", "/swagger-resources", "/configuration/security", "/swagger-ui.html", "/webjars/**","/swagger-resources/configuration/ui","/swagge‌​r-ui.html").permitAll()
                //
                .antMatchers("/usersInfo/{id}/**")
                .access("hasRole('USER') and @userSecurity.checkId(authentication, #id)")
                //
                .anyRequest().denyAll()
                .and()
                .csrf().disable()
    }

    @Bean
    fun userSecurity() : UserSecurity {
        return UserSecurity()
    }
}

/**
 * Custom check. Not only we need a userservice authenticated, but we also
 * need to make sure that a userservice can only access his/her data, and not the
 * one of the other users
 */
class UserSecurity{

    fun checkId(authentication: Authentication, id: String) : Boolean{

        val current = (authentication.principal as UserDetails).username

        return current == id
    }
}


