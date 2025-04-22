package com.davcm.spring_jwt.Controller;

import com.davcm.spring_jwt.Model.Total;
import com.davcm.spring_jwt.Projection.RegistrationProjection;
import com.davcm.spring_jwt.Service.RegistrationService;
import com.davcm.spring_jwt.Service.TotalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/total")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
public class UserTotalController {
    private final RegistrationService registrationService;
    private final TotalService totalService;
    @GetMapping("/all/aprobado")
    public ResponseEntity<List<RegistrationProjection>> getRegistrationsByUserId(@RequestHeader("Authorization") String token) {
        try {
            String subToken = token.substring(7);
            List<RegistrationProjection> registrations = registrationService.findRegistrationsByUserId(subToken);
            return new ResponseEntity<>(registrations, HttpStatus.OK);
        }
        catch(RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }
    @GetMapping("/my")
    public ResponseEntity<Total> getTotalByUserId(@RequestHeader("Authorization") String token) {
        try {
            String subToken = token.substring(7);
            Total totals = totalService.findTotalByUserId(subToken);
            return new ResponseEntity<>(totals, HttpStatus.OK);
        }
        catch(RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

}
