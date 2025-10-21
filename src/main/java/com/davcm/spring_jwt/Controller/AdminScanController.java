package com.davcm.spring_jwt.Controller;

import com.davcm.spring_jwt.Service.RegistrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin/scan")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class AdminScanController {
    private final RegistrationService registrationService;

    @PutMapping("/update/{registrationId}")
    public ResponseEntity<String> updateScanStatus(@PathVariable Long registrationId) {
        try {
            registrationService.updateScanStatus(registrationId);
            return new ResponseEntity<>("Estado del registro actualizado exitosamente", HttpStatus.OK);
        }
        catch(RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}