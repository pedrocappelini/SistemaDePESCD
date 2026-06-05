package br.dsw.pescd.config;

import br.dsw.pescd.domain.Usuario;
import br.dsw.pescd.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/css/**", "/js/**").permitAll() // arquivos estáticos
                .requestMatchers("/login").permitAll()            // tela de login
                .requestMatchers("/aluno/**").hasRole("ALUNO")    // só alunos
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")           // nossa tela de login
                .defaultSuccessUrl("/login/redirecionar", true)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/login")
                .permitAll()
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