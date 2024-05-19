package com.pposong.pposongoauth2.config;

import com.pposong.pposongoauth2.Service.Oauth2MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {
    private final Oauth2MemberService oauth2MemberService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception{
        return httpSecurity
                .httpBasic().disable()
                .csrf().disable()
                .cors().and()
                .authorizeRequests()
                .requestMatchers("/private/**").authenticated()
                .anyRequest().permitAll()
                .and().oauth2Login()
                .loginPage("/")
                .defaultSuccessUrl("/private")
                .userInfoEndpoint()
                .userService(oauth2MemberService).and().and().build();
    }
}
