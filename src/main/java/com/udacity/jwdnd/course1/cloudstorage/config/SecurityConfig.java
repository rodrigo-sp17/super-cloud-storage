package com.udacity.jwdnd.course1.cloudstorage.config;

import com.udacity.jwdnd.course1.cloudstorage.service.AuthenticationService;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final AuthenticationService authenticationService;

    // This tells Spring to insert dependency to our AuthenticationService
    public SecurityConfig(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    // This adds our AuthenticationService to SpringSecurity
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authenticationService);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // Sets up open access pages
        http.authorizeRequests().antMatchers(
                "/css/**", "/js/**", "/signup")
                .permitAll()
                .anyRequest().authenticated();

        // Sets up login/logout pages
        http.formLogin().loginPage("/login").permitAll();
        http.formLogin().defaultSuccessUrl("/home", true);
        http.logout().permitAll();
    }
}
