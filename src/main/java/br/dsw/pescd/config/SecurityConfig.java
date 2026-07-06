package br.dsw.pescd.config;

import br.dsw.pescd.domain.Usuario;
import br.dsw.pescd.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/ofertas-publicas").permitAll()
                        .requestMatchers("/api/aluno/**").hasRole("ALUNO")
                        .requestMatchers("/api/secretario/**").hasRole("SECRETARIO")
                        .requestMatchers("/api/admin/**").hasRole("ADMINISTRADOR")
                        .requestMatchers("/api/professor/**").hasRole("PROFESSOR")
                        .requestMatchers(HttpMethod.GET, "/api/professores").hasAnyRole("ALUNO", "SECRETARIO", "PROFESSOR")
                        .requestMatchers(HttpMethod.GET, "/api/me").authenticated()
                        .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults())
                .formLogin(form -> form.disable())
                .logout(logout -> logout.disable())
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(HttpStatus.UNAUTHORIZED.value());
                            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
                            response.getWriter().write("""
                                    {"status":401,"error":"Unauthorized","message":"Autenticacao obrigatoria.","path":"%s"}
                                    """.formatted(request.getRequestURI()).trim());
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setStatus(HttpStatus.FORBIDDEN.value());
                            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
                            response.getWriter().write("""
                                    {"status":403,"error":"Forbidden","message":"Acesso negado para este perfil.","path":"%s"}
                                    """.formatted(request.getRequestURI()).trim());
                        })
                );

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            Usuario usuario = usuarioRepository.findByUsername(username);
            if (usuario == null) {
                throw new UsernameNotFoundException("Usuário não encontrado: " + username);
            }

            String role = "ROLE_" + usuario.getClass().getSimpleName().toUpperCase();

            return new org.springframework.security.core.userdetails.User(
                    usuario.getUsername(),
                    usuario.getSenha(),
                    List.of(new SimpleGrantedAuthority(role))
            );
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }
}
