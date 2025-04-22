package com.davcm.spring_jwt.Controller;

import com.davcm.spring_jwt.Jwt.JwtService;
import com.davcm.spring_jwt.Model.QrModel;
import com.davcm.spring_jwt.Model.Registration;
import com.davcm.spring_jwt.Model.User;
import com.davcm.spring_jwt.Projection.RegistrationProjection;
import com.davcm.spring_jwt.Repository.UserRepository;
import com.davcm.spring_jwt.Service.Base64Service;
import com.davcm.spring_jwt.Service.QrService;
import com.davcm.spring_jwt.Service.RegistrationService;
import com.google.zxing.WriterException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.MissingRequestValueException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/user/registration")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
public class UserRegistrationController {
    private final RegistrationService registrationService;
    private final QrService qrService;
    private final Base64Service base64Service;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    @GetMapping("/all/espera")
    public ResponseEntity<List<RegistrationProjection>> getPreRegistrationsByUserId(@RequestHeader("Authorization") String token) {
        try {
            String subToken = token.substring(7);
            List<RegistrationProjection> preRegistrations = registrationService.findPreRegistrationsByUserId(subToken);
            return new ResponseEntity<>(preRegistrations, HttpStatus.OK);
        }
        catch(RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    @PostMapping(value = "/qr/{registrationId}", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<?> generateQr(
            @PathVariable Long registrationId,
            @RequestHeader("Authorization") String token,
            HttpServletResponse response) throws
            MissingRequestValueException, WriterException, IOException {
        try {
            String subToken = token.substring(7);
            qrService.generateQr(registrationId, response.getOutputStream(), subToken);
            response.getOutputStream().flush();

            return ResponseEntity.ok().build();
        }
        catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        }
        catch (IOException | WriterException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/upload/{registrationId}")
    public ResponseEntity<Object> uploadPhoto(
            @PathVariable("registrationId") Long registrationId,
            @RequestParam("file") MultipartFile file,
            @RequestHeader("Authorization") String token) throws IOException {
        if(file.isEmpty()) {
            return new ResponseEntity<>("Archivo no subido", HttpStatus.BAD_REQUEST);
        }
        if(file.getSize() > 1048576) {
            return new ResponseEntity<>("El archivo excede el límite de 350 KB", HttpStatus.BAD_REQUEST);
        }

        try {
            String subToken = token.substring(7);
            registrationService.savePhoto(registrationId, file, subToken);
            return new ResponseEntity<>("Archivo subido exitosamente", HttpStatus.OK);
        }
        catch(RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        catch(Exception e) {
            throw new RuntimeException(e);
        }
    }
}
