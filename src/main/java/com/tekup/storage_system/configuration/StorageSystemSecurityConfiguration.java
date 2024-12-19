package com.tekup.storage_system.configuration;

import java.util.Collections;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.password.CompromisedPasswordChecker;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.password.HaveIBeenPwnedRestApiPasswordChecker;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import com.tekup.storage_system.filter.CsrfCookieFilter;

import jakarta.servlet.http.HttpServletRequest;

@Configuration
public class StorageSystemSecurityConfiguration {

        @Bean
        public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
                // Définir un handler pour la gestion des tokens CSRF
                CsrfTokenRequestAttributeHandler csrfTokenRequestAttributeHandler = new CsrfTokenRequestAttributeHandler();

                http.securityContext(contextConfig -> contextConfig.requireExplicitSave(false))
                                .sessionManagement(sessionConfig -> sessionConfig
                                                .sessionCreationPolicy(SessionCreationPolicy.ALWAYS))
                                .cors(corsConfig -> corsConfig.configurationSource(new CorsConfigurationSource() {
                                        @Override
                                        public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                                                CorsConfiguration config = new CorsConfiguration();
                                                config.setAllowedOrigins(Collections.singletonList("*"));
                                                config.setAllowedMethods(Collections.singletonList("*"));
                                                config.setAllowCredentials(true);
                                                config.setAllowedHeaders(Collections.singletonList("*"));
                                                config.setMaxAge(3600L);
                                                return config;
                                        }
                                }))
                                .csrf(csrfConfig -> csrfConfig.csrfTokenRequestHandler(csrfTokenRequestAttributeHandler)
                                                .ignoringRequestMatchers("/auth/login", "/auth/register", "/auth/**",
                                                                "/me")
                                                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))
                                .addFilterAfter(new CsrfCookieFilter(), BasicAuthenticationFilter.class)
                                .requiresChannel(rcc -> rcc.anyRequest().requiresInsecure()) // Assurez-vous que les //
                                                                                             // requêtes utilisent


                        .authorizeHttpRequests(requests -> requests
                                .requestMatchers("/home", "/me", "/settings", "/cred", "/avatar", "/delete", "/notes",
                                        "/my-notes", "/notes/**","/accounts/**","accountpassrest/**").hasRole("USER")
                                .requestMatchers("/error", "/invalidSession", "/auth/**", "/css/**", "/js/**",
                                        "/pictures/**", "/favicon.ico", "/images/**", "/webfonts/**").permitAll()
                                        
                                );


                // Configurer le formulaire de connexion
                http.formLogin(form -> form
                                .loginPage("/auth/login")
                                .loginProcessingUrl("/auth/login")
                                .defaultSuccessUrl("/home", true)
                                .failureUrl("/auth/login?error=true")
                                .permitAll());

                // Configurer la déconnexion
                http.logout(logout -> logout
                                .logoutUrl("/auth/logout")
                                .logoutSuccessUrl("/auth/login?logout=true")
                                .permitAll());

                // JWTStateless

                return http.build();
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
                return PasswordEncoderFactories.createDelegatingPasswordEncoder();
        }

        /**
         * From Spring Security 6.3 version
         *
         * @return CompromisedPasswordChecker
         */
        @Bean
        public CompromisedPasswordChecker compromisedPasswordChecker() {
                return new HaveIBeenPwnedRestApiPasswordChecker(); // Vérifier les mots de passe compromis via le
                                                                   // service
                                                                   // HaveIBeenPwned
        }
}
