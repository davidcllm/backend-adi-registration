package com.davcm.spring_jwt.Model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.EnableMBeanExport;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Base64;



//FetchType.EAGER
//
//    Descripción: Cuando una entidad se carga, todas las asociaciones marcadas con FetchType.EAGER se cargan inmediatamente junto con la entidad principal.
//
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "app_registration")
public class Registration implements Serializable {
    @Id
    @SequenceGenerator(
            name = "registration_sequence",
            sequenceName = "registration_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "registration_sequence"
    )
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;


    @Column(name = "photos")
    private String photos;

    private Boolean scan = false;
    private String aprobado = "ESPERA";
}
//    Uso: Útil cuando necesitas que las asociaciones estén disponibles de inmediato y no quieres realizar consultas adicionales.

//FetchType.LAZY
//
//    Descripción: Las asociaciones marcadas con FetchType.LAZY se cargan bajo demanda, es decir, solo cuando se accede a ellas por primera vez.
//
//    Uso: Útil cuando no siempre necesitas las asociaciones y quieres evitar cargar datos innecesarios.