package pl.coderslab.carrental.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import pl.coderslab.carrental.model.CurrentUser;
import pl.coderslab.carrental.repository.UserRepository;

import java.util.stream.Collectors;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/", "/index", "/api/cars", "/api/reviews", "/login", "/error").permitAll()
                        .anyRequest().authenticated()

                )
                .formLogin(form -> form
                        .loginProcessingUrl("/perform_login")
                        .defaultSuccessUrl("/logged", true)
                        .permitAll()
                )
                .logout(logout -> logout.logoutUrl("/logout").permitAll());

        return http.build();
    }


    @Bean
    public UserDetailsService combinedUsers(UserRepository repo, PasswordEncoder encoder) {
        return username -> {

            if ("admin".equalsIgnoreCase(username)) {
                return org.springframework.security.core.userdetails.User.builder()
                        .username("admin")
                        .password(encoder.encode("password"))
                        .roles("ADMIN")
                        .build();
            }

            return repo.findUserByEmail(username)
                    .map(user -> new CurrentUser(
                            user.getEmail(),
                            user.getPassword(),
                            user.getRoles().stream()
                                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName().toUpperCase()))
                                    .collect(Collectors.toSet()),
                            user
                    ))
                    .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        };
    }

    @Bean
    public AuthenticationManager authManager(PasswordEncoder encoder,
                                             UserDetailsService combinedUsers) {

        var provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(combinedUsers);
        provider.setPasswordEncoder(encoder);

        return new ProviderManager(provider);
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
