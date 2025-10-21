package com.davcm.spring_jwt.Service;

import com.davcm.spring_jwt.Jwt.JwtService;
import com.davcm.spring_jwt.Model.User;
import com.davcm.spring_jwt.Projection.UserProjection;
import com.davcm.spring_jwt.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final JwtService jwtService;
    public UserProjection findProfileByUserId(String token) {
        String usernameFromToken = jwtService.getUsernameFromToken(token);
        User user = userRepository.findByUsername(usernameFromToken)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Long userId = user.getId();

        return userRepository.findBy_Id(userId)
            .orElseThrow(() -> new RuntimeException("El usuario con el id: " + userId + " no ha sido encontrado"));
    }

    /*public User updateUser(User user) {
        return userRepository.save(user);
    }*/
}
