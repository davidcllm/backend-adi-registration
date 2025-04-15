package com.davcm.spring_jwt.Repository;

import com.davcm.spring_jwt.Model.Total;
import com.davcm.spring_jwt.Projection.RegistrationProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TotalRepository extends JpaRepository<Total, Long> {
    Optional<Total> findByUserId(Long userId);

    Page<Total> findAll(Pageable pageable);
    @Query("""
        SELECT t FROM Total t
        WHERE 
            CAST(t.user.id AS string) LIKE CONCAT('%', :searchKey, '%')
            OR LOWER(t.user.carrera) LIKE LOWER(CONCAT('%', :searchKey, '%'))
            OR LOWER(t.user.username) LIKE LOWER(CONCAT('%', :searchKey, '%'))
    """)
    Page<Total> findBySearchKey(@Param("searchKey") String searchKey, Pageable pageable);
}
