package com.davcm.spring_jwt.Jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String token = getTokenFromRequest(request);
        final String username;

        if(token == null) {
            filterChain.doFilter(request, response);
            return;
        }

        username = jwtService.getUsernameFromToken(token);

        if(username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            if(jwtService.isTokenValid(token, userDetails)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if(StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }
}

//@Component en Spring se utiliza para indicar que una clase es un componente gestionado por el contenedor de Spring.
// Esto significa que Spring creará una instancia de esa clase y la gestionará durante el ciclo de vida de la
// aplicación, permitiendo que esa clase se inyecte como una dependencia en otros componentes.

//OncePerRequestFilter es una clase abstracta en el framework Spring que se utiliza para asegurar que un filtro se
// ejecute una sola vez por cada solicitud HTTP entrante. Tambien se utiliza para crear filtros personalizados.

//Clase JwtAuthenticationFilter: Extiende OncePerRequestFilter, lo que asegura que el filtro se ejecuta una sola vez
// por solicitud.
//Método doFilterInternal:
//
//Extrae el token: Llama al método getTokenFromRequest para extraer el token JWT de la cabecera de autorización.
//Verifica si el token es nulo: Si no hay token (es nulo), continúa con el siguiente filtro en la cadena
// (filterChain.doFilter(request, response)).
//
//Método getTokenFromRequest:
//
//Obtiene la cabecera de autorización: Utiliza request.getHeader(HttpHeaders.AUTHORIZATION) para obtener
// la cabecera de autorización.
//Verifica y extrae el token: Comprueba si la cabecera tiene texto y comienza con "Bearer ". Si es así,
// extrae el token JWT eliminando el prefijo "Bearer ".

