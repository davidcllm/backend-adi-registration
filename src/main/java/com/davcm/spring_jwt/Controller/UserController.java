package com.davcm.spring_jwt.Controller;

import com.davcm.spring_jwt.Jwt.JwtService;
import com.davcm.spring_jwt.Projection.UserProjection;
import com.davcm.spring_jwt.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
public class UserController {
    private final UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<UserProjection> getUserProfileByUserId(@RequestHeader("Authorization") String token) {

        try {
            String subToken = token.substring(7);
            UserProjection profile = userService.findProfileByUserId(subToken);
            return new ResponseEntity<>(profile, HttpStatus.OK);
        }
        catch(RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }
}
