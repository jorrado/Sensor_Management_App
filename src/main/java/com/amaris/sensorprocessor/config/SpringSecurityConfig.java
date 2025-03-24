package com.amaris.sensorprocessor.config;

import com.amaris.sensorprocessor.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SpringSecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;

    @Autowired
    public SpringSecurityConfig(CustomUserDetailsService customUserDetailsService) {
        this.customUserDetailsService = customUserDetailsService;
    }
//    private CustomUserDetailsService customUserDetailsService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http.authorizeHttpRequests(auth -> {
//            auth.requestMatchers("/login").permitAll();
//            auth.requestMatchers("/home").authenticated();
            auth.requestMatchers("/css/**").permitAll();
            auth.requestMatchers("/image/**").permitAll();
//            auth.requestMatchers("/admin/**").hasRole("ADMIN");
//            auth.requestMatchers("/superuser").hasRole("SUPERUSER");
//            auth.requestMatchers("/user").hasRole("USER");
            auth.anyRequest().hasRole("ADMIN");
        }).formLogin(form -> form.loginPage("/login")
                    .permitAll().defaultSuccessUrl("/home", true))
                    .build();
    }

    /**
     * Crée et retourne un {@link BCryptPasswordEncoder}.
     * Cet encodeur est utilisé pour hacher les mots de passe avec l'algorithme BCrypt.
     *
     * @return Une nouvelle instance de {@link BCryptPasswordEncoder}.
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Crée et configure un {@link AuthenticationManager} avec un {@link BCryptPasswordEncoder}.
     * Ce manager est utilisé pour l'authentification des utilisateurs en utilisant un service de détails utilisateur personnalisé.
     *
     * @param http L'objet HttpSecurity utilisé pour configurer la sécurité.
     * @param bCryptPasswordEncoder Le password encoder utilisé pour encoder les mots de passe.
     * @return L'instance configurée de {@link AuthenticationManager}.
     * @throws Exception Si une erreur se produit lors de la configuration.
     */
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http, BCryptPasswordEncoder bCryptPasswordEncoder) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(customUserDetailsService).passwordEncoder(bCryptPasswordEncoder);
        return authenticationManagerBuilder.build();
    }

}
