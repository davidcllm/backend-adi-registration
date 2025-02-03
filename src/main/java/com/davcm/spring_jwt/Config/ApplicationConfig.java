package com.davcm.spring_jwt.Config;

import com.davcm.spring_jwt.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {
    private final UserRepository userRepository;
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailService());
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    public UserDetailsService userDetailService() {
        return username -> userRepository.findByUsername(username).orElseThrow(
            () -> new UsernameNotFoundException("User not found")
        );
    }
}

//DaoAuthenticationProvider es una implementación de la interfaz AuthenticationProvider en Spring Security
// que se utiliza para autenticar usuarios utilizando un UserDetailsService y un PasswordEncoder.
// Esta clase es parte del marco de trabajo de Spring Security y facilita la autenticación basada en los detalles
// del usuario almacenados en una base de datos u otro tipo de almacenamiento persistente.

//UserDetailsService:
//
//    Es una interfaz de Spring Security que carga los datos del usuario desde una base de datos u otro almacenamiento.
//    DaoAuthenticationProvider usa esta interfaz para obtener los detalles del usuario durante la autenticación.
//
//PasswordEncoder:
//
//    Es una interfaz que define cómo se deben codificar y verificar las contraseñas.
//    DaoAuthenticationProvider usa esta interfaz para comparar la contraseña proporcionada por el usuario con la
//    contraseña almacenada de manera segura.
