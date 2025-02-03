package com.davcm.spring_jwt.Service;

import com.davcm.spring_jwt.Jwt.JwtService;
import com.davcm.spring_jwt.Model.Total;
import com.davcm.spring_jwt.Model.User;
import com.davcm.spring_jwt.Repository.TotalRepository;
import com.davcm.spring_jwt.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TotalService {
    private final TotalRepository totalRepository;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    public Page<Total> findAllTotals(String searchKey, Pageable pageable) {
        Sort sort = Sort.by(Sort.Order.asc("user"));
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        if (searchKey != null && !searchKey.trim().isEmpty()) {
            return totalRepository.findBySearchKey(searchKey, sortedPageable);
        }
        else {
            return totalRepository.findAll(sortedPageable);
        }
    }
    public Total findTotalByUserId(String token) {
        String usernameFromToken = jwtService.getUsernameFromToken(token);
        User user = userRepository.findByUsername(usernameFromToken)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Long userId = user.getId();

        return totalRepository.findByUserId(userId)
            .orElseThrow(() -> new RuntimeException("Los totales del usuario con el id: " + userId + " no han sido encontrados"));
    }
}
