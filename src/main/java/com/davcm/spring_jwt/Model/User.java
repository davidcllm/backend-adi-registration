package com.davcm.spring_jwt.Model;

import com.davcm.spring_jwt.Model.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "app_user", uniqueConstraints = {@UniqueConstraint(columnNames = {"username"})})
public class User implements UserDetails {
    @Id
    @Column(nullable = false)
    private Long id;

    @Column(nullable = false)
    private String username;

    private String password;
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    private String carrera;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Role role;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonIgnore //para no crear un bucle
    private Total total;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name())); //la autoridad que se le dara al usuario, rol
    }
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    @Override
    public boolean isEnabled() {
        return true;
    }
}

//@Enumerated se utiliza para mapear una entidad JPA (Java Persistence API) a un tipo enumerado (enum).
// Esta anotación especifica cómo un atributo enum de una entidad debe ser almacenado y recuperado en la base de datos.

//UserDetails es una interfaz en Spring Security que representa la información de un usuario que se autentica en una
// aplicación. Proporciona detalles sobre el usuario, como su nombre de usuario, contraseña (encriptada),
// roles y privilegios.

//@Data es una combinación de varias otras anotaciones de Lombok que generan automáticamente
// métodos comúnmente utilizados. Esencialmente, @Data incluye:
//
//    @Getter: Genera los métodos getter para todos los campos.
//    @Setter: Genera los métodos setter para todos los campos.
//    @ToString: Genera el método toString().
//    @EqualsAndHashCode: Genera los métodos equals(Object other) y hashCode().
//    @RequiredArgsConstructor: Genera un constructor para los campos finales (final).

//@Builder proporciona un patrón de diseño de Builder para la clase en la que se aplica.
// Este patrón es útil para crear instancias de clases con muchos parámetros de manera clara y legible.

//@AllArgsConstructor genera un constructor con un parámetro para cada campo en la clase. Es útil cuando
// necesitas un constructor que inicialice todos los campos de una vez.

//@NoArgsConstructor genera un constructor sin parámetros. Es útil cuando necesitas un constructor predeterminado,
// especialmente en casos donde la clase necesita ser serializada o deserializada (por ejemplo, en JPA o JSON).
