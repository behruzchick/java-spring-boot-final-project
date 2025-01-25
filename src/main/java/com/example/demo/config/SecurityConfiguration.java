package com.example.demo.config;

import com.example.demo.security.JWTConfigurer;
import com.example.demo.security.JWTFilter;
import com.example.demo.security.TokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;
@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
    private final TokenProvider tokenProvider;
    private JWTFilter jwtFilter;
    private static final Logger log = LoggerFactory.getLogger(SecurityConfiguration.class);

    public SecurityConfiguration(TokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:4040"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JWTFilter jwtFilter) throws Exception {
        try {

            http
                    .csrf(csrf -> csrf.disable())
                    .sessionManagement(session -> session
                            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    )
                    .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                    .addFilterBefore(jwtFilter , UsernamePasswordAuthenticationFilter.class)
                    .authorizeHttpRequests(auth -> auth

                            // Employee
                            .requestMatchers("/api/employee/signIn").permitAll()
                            .requestMatchers("/api/employee/getAllEmployees").hasAnyRole("DIRECTOR" , "ADMIN")
                            .requestMatchers("/api/employee/addEmployee").hasAnyRole("DIRECTOR" , "ADMIN")
                            .requestMatchers("/api/employee/findEmployee/**").hasAnyRole("DIRECTOR" , "ADMIN")
                            .requestMatchers("/api/employee/deleteEmployee/**").hasAnyRole("ADMIN")
                            .requestMatchers("/api/employee/editEmployee/**").hasAnyRole("ADMIN")

                            // Client
                            .requestMatchers("/api/client/getAllClients").hasAnyRole("DIRECTOR" , "ADMIN" , "ADMINISTRATOR")
                            .requestMatchers("/api/client/addClient").hasAnyRole("DIRECTOR" , "ADMIN" , "ADMINISTRATOR")
                            .requestMatchers("/api/client/getClient").hasAnyRole("DIRECTOR" , "ADMIN" , "ADMINISTRATOR")
                            .requestMatchers("/api/client/updateClient/**").hasAnyRole("DIRECTOR" , "ADMIN" , "ADMINISTRATOR")
                            .requestMatchers("/api/client/addArchive/**").hasAnyRole("DIRECTOR" , "ADMIN" , "ADMINISTRATOR")


                            //Advertisement
                            .requestMatchers("/api/ad/createAd").hasAnyRole("DIRECTOR" , "ADMIN" , "ADMINISTRATOR")
                            .requestMatchers("/api/ad/getAllAds").hasAnyRole("DIRECTOR" , "ADMIN" , "ADMINISTRATOR")
                            .requestMatchers("/api/ad/getAd/**").hasAnyRole("DIRECTOR" , "ADMIN" , "ADMINISTRATOR")
                            .requestMatchers("/api/ad/updateAd").hasAnyRole("DIRECTOR" , "ADMIN" , "ADMINISTRATOR")
                            .requestMatchers("/api/ad/deleteAd").hasAnyRole("DIRECTOR" , "ADMIN" , "ADMINISTRATOR")
                            .requestMatchers("/api/ad/stopAd/**").hasAnyRole("DIRECTOR" , "ADMIN" , "ADMINISTRATOR")

                            // Filters (Statistics)

                            .requestMatchers("/api/statistics/daily_registered_clients").hasAnyRole("DIRECTOR" , "ADMIN" , "ADMINISTRATOR")
                            .requestMatchers("/api/statistics/monthly_registered_clients").hasAnyRole("DIRECTOR" , "ADMIN" , "ADMINISTRATOR")
                            .requestMatchers("/api/statistics/best_employees").hasAnyRole("DIRECTOR" , "ADMIN" , "ADMINISTRATOR")
                            .requestMatchers("/api/statistics/best_employee").hasAnyRole("DIRECTOR" , "ADMIN" , "ADMINISTRATOR")
                            .requestMatchers("/api/statistics/getEmployeesByRole/**").hasAnyRole("DIRECTOR" , "ADMIN" , "ADMINISTRATOR")
                            .requestMatchers("/api/statistics/getEmployeesByAge/**").hasAnyRole("DIRECTOR" , "ADMIN" , "ADMINISTRATOR")
                            .requestMatchers("/api/statistics/getAdvertisementPriceByType/**").hasAnyRole("DIRECTOR" , "ADMIN" , "ADMINISTRATOR")
                            .requestMatchers("/api/statistics/getAdvertisementMostAddedByEmployee").hasAnyRole("DIRECTOR" , "ADMIN" , "ADMINISTRATOR")
                            .requestMatchers("/api/statistics/getMonthlyIncludedAdvertisement").hasAnyRole("DIRECTOR" , "ADMIN" , "ADMINISTRATOR")
                            .requestMatchers("/api/statistics/getMonthlyStoppedAdvertisement").hasAnyRole("DIRECTOR" , "ADMIN" , "ADMINISTRATOR")
                            .anyRequest().authenticated()

                    );
            return http.build();
        } catch (Exception e) {
            throw e;
        }
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

//    @Bean
//    public UserDetailsService demoUserDetailsService() {
//        UserDetails user =
//                User.builder()
//                        .username("user")
//                        .password(passwordEncoder().encode("1234"))
//                        .roles("CLIENT")
//                        .build();
//
//        UserDetails employee =
//                User.builder()
//                        .username("employee")
//                        .password(passwordEncoder().encode("1234"))
//                        .roles("DIRECTOR")
//                        .build();
//        return new InMemoryUserDetailsManager(user, employee);
//    }


    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


}