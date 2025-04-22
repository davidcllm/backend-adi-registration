package com.davcm.spring_jwt.Service;

import com.davcm.spring_jwt.Jwt.JwtService;
import com.davcm.spring_jwt.Model.QrModel;
import com.davcm.spring_jwt.Model.Registration;
import com.davcm.spring_jwt.Model.User;
import com.davcm.spring_jwt.Repository.RegistrationRepository;
import com.davcm.spring_jwt.Repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import jakarta.servlet.ServletOutputStream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class QrService {
    private final ObjectMapper objectMapper;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final RegistrationRepository registrationRepository;
    public void generateQr(Long registrationId, ServletOutputStream outputStream, String token) throws
            WriterException, IOException {

        String usernameFromToken = jwtService.getUsernameFromToken(token);
        User user = userRepository.findByUsername(usernameFromToken)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Registration registration = registrationRepository.findById(registrationId)
            .orElseThrow(() -> new RuntimeException("Registro no encontrado"));

        if(!registration.getUser().getUsername().equals(usernameFromToken)){
            throw new RuntimeException("No tienes permiso para acceder a esta información");
        }

        String jsonData = objectMapper.writeValueAsString(registrationId);
        BitMatrix bitMatrix = new QRCodeWriter().encode(jsonData, BarcodeFormat.QR_CODE, 250,250);
        MatrixToImageWriter.writeToStream(bitMatrix, "jpeg", outputStream);
    }
}
