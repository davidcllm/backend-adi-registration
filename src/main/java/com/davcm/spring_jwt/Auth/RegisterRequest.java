package com.davcm.spring_jwt.Auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    Long id;
    String username;
    String password;
    String firstName;
    String lastName;
    String carrera;
}
