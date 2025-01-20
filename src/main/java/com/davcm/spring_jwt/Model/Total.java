package com.davcm.spring_jwt.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "app_total")
public class Total implements Serializable {
    @Id
    @SequenceGenerator(
            name = "total_sequence",
            sequenceName = "total_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "total_sequence"
    )
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", unique = true, nullable = false)
    private User user;

    private Integer sportEvents = 0;
    private Integer academicEvents = 0;
    private Integer culturalEvents = 0;
    private Integer sociedadEvents = 0;
    private Integer asuaEvents = 0;
    private Integer penalty = 0;
    private Integer totalEvents = 0;
}
