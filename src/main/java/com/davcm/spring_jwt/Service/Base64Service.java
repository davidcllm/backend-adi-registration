package com.davcm.spring_jwt.Service;

import org.springframework.stereotype.Service;

import java.util.Base64;

@Service
public class Base64Service {
    public String encode(byte[] data) {
        return Base64.getEncoder().encodeToString(data);
    }
    public byte[] decode(String base64String) {
        return Base64.getDecoder().decode(base64String);
    }
}
