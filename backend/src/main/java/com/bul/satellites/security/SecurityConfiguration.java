package com.bul.satellites.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@RequiredArgsConstructor
public class SecurityConfiguration {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf().disable().cors().and().authorizeHttpRequests((authz) -> authz
//                        .requestMatchers(new AntPathRequestMatcher("/**")).permitAll()
//                        .antMatchers("/navigation/**").hasRole("CAPTAIN")
                .requestMatchers("**").permitAll()
                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**").permitAll()
                .anyRequest().authenticated()).httpBasic(Customizer.withDefaults());
        return http.build();
    }
}
