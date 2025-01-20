package com.davcm.spring_jwt.Controller;

import com.davcm.spring_jwt.Auth.AuthResponse;
import com.davcm.spring_jwt.Service.AuthService;
import com.davcm.spring_jwt.Auth.LoginRequest;
import com.davcm.spring_jwt.Auth.RegisterRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping(value = "login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest loginRequest) { //accedemos a las credenciales del usuario
        return ResponseEntity.ok(authService.login(loginRequest));
    }
    @PostMapping(value = "register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest registerRequest) {
        return ResponseEntity.ok(authService.register(registerRequest));
    }
}

//@RequiredArgsConstructor de Lombok es una manera conveniente de generar un constructor en tu clase.

//Un endpoint, en el contexto de las APIs y los servicios web, es una URL específica a la que se puede hacer
// una solicitud para interactuar con un recurso del servidor.

//ResponseEntity: tipo de objeto que va a representar toda la respuesta http, que incluira los codigos de estado,
// encabezados y el cuerpo de respuesta
