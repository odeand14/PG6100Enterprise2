package no.westerdals.highscore

// Created by Andreas Ødegaard on 09.12.2017.
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter

@Configuration
@EnableWebSecurity
class WebSecurityConfig: WebSecurityConfigurerAdapter() {


    override fun configure(http: HttpSecurity) {

        http.httpBasic()
                .and()
                .authorizeRequests()
                .antMatchers("/highscores/**").permitAll()
                .antMatchers("/highscoresCount").permitAll()
                .antMatchers("/highscores/**").hasRole("USER")
                .antMatchers("/**").permitAll()
                //
//                .antMatchers("/highscores/{id}/**")
//                .access("hasRole('USER') and @userSecurity.checkId(authentication, #id)")

                .antMatchers("/highscores/**").hasRole("USER")

                .anyRequest().denyAll()
                .and()
                .csrf().disable()
    }

//    @Bean
//    fun userSecurity() : UserSecurity{
//        return UserSecurity()
//    }
}


//class UserSecurity{
//
//    fun checkId(authentication: Authentication, id: String) : Boolean{
//
//        val current = (authentication.principal as UserDetails).username
//
//        return current == id
//    }
//}