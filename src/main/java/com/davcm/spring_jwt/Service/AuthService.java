package com.davcm.spring_jwt.Service;

import com.davcm.spring_jwt.Auth.AuthResponse;
import com.davcm.spring_jwt.Auth.LoginRequest;
import com.davcm.spring_jwt.Auth.RegisterRequest;
import com.davcm.spring_jwt.Jwt.JwtService;
import com.davcm.spring_jwt.Model.Role;
import com.davcm.spring_jwt.Model.Total;
import com.davcm.spring_jwt.Model.User;
import com.davcm.spring_jwt.Repository.TotalRepository;
import com.davcm.spring_jwt.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final TotalRepository totalRepository;

    public AuthResponse login(LoginRequest loginRequest) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        //UserDetails userDetails = userRepository.findByUsername(loginRequest.getUsername()).orElseThrow();
        User user = userRepository.findByUsername(loginRequest.getUsername()).orElseThrow();
        String token = jwtService.getToken(user);

        return AuthResponse.builder()
            .token(token)
            .role(user.getRole())
            .build();
    }
    public AuthResponse register(RegisterRequest registerRequest) {
        String idStr = String.valueOf(registerRequest.getId());

        if(idStr.startsWith("00")) {
            idStr = idStr.replaceFirst("^00", "");
        }

        Long id = Long.valueOf(idStr);

        if(userRepository.existsById(id)) {
            throw new IllegalStateException("Usuario con el id " + id + " ya existe.");
        }

        if(!registerRequest.getUsername().endsWith("@anahuac.mx")) {
            throw new IllegalStateException("El correo del usuario debe de pertenecer a la Red de Universidades Anáhuac");
        }

        User user = User.builder()
            .id(id)
            .username(registerRequest.getUsername())
            .password(passwordEncoder.encode(registerRequest.getPassword()))
            .firstName(registerRequest.getFirstName())
            .lastName(registerRequest.getLastName())
            .carrera(registerRequest.getCarrera())
            .role(Role.ROLE_USER)
            .build();

        userRepository.save(user);

        Total total = Total.builder()
            .user(user)
            .sportEvents(0)
            .academicEvents(0)
            .culturalEvents(0)
            .sociedadEvents(0)
            .asuaEvents(0)
            .penalty(0)
            .totalEvents(0)
            .build();

        totalRepository.save(total);

        return AuthResponse.builder()
            .token(jwtService.getToken(user))
            .role(user.getRole())
            .build();
    }
}
