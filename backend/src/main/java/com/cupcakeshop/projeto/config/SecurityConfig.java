package com.cupcakeshop.projeto.config;

import com.cupcakeshop.projeto.service.AutenticacaoService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, DaoAuthenticationProvider authenticationProvider) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authenticationProvider(authenticationProvider) // <-- PONTO CRÍTICO: Registra o provedor
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/cadastro/**", "/login", "/vitrine", "/css/**", "/js/**").permitAll()
                        .requestMatchers("/admin/**").hasAuthority("ADMIN") // <-- MELHORIA: Acesso Admin por permissão
                        .anyRequest().authenticated()
                )
                .formLogin(login -> login
                        .loginPage("/login")
                        .loginProcessingUrl("/login") // Garante que a rota POST seja /login
                        .usernameParameter("username") // Campo do formulário
                        .passwordParameter("password") // Campo do formulário
                        .defaultSuccessUrl("/admin/produtos", true) // Redireciona para o admin após sucesso
                        .failureUrl("/login?error") // Redireciona para login com erro
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout") // Redireciona para login após logout
                        .permitAll());

        return http.build();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(AutenticacaoService autenticacaoService, BCryptPasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(autenticacaoService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }
}
