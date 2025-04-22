package com.davcm.spring_jwt.Repository;

import com.davcm.spring_jwt.Model.Event;
import com.davcm.spring_jwt.Model.Registration;
import com.davcm.spring_jwt.Model.User;
import com.davcm.spring_jwt.Projection.RegistrationProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RegistrationRepository extends JpaRepository<Registration, Long> {
    Optional<Registration> findByUserAndEvent(User user, Event event);

    @Query("SELECT r FROM Registration r WHERE r.user.id = :userId AND r.aprobado = :aprobado")
    Optional<List<RegistrationProjection>> findByUser_Id(@Param("userId") Long userId, @Param("aprobado") String aprobado);

    @Query("SELECT r FROM Registration r WHERE r.user.id = :userId AND r.aprobado IN ('APROBADO', 'PENALIZADO')")
    Optional<List<RegistrationProjection>> findRegistrationsByUser_Id(@Param("userId") Long userId);

    @Query("SELECT r FROM Registration r")
    Page<RegistrationProjection> findAllProjectedBy(Pageable pageable);
    //Page<Registration> findAll(Pageable pageable);

    @Query("""
        SELECT r FROM Registration r
        WHERE
            LOWER(r.event.eventName) LIKE LOWER(CONCAT('%', :searchKey, '%')) 
            OR LOWER(r.aprobado) LIKE LOWER(CONCAT('%', :searchKey, '%'))
            OR CAST(r.user.id AS string) LIKE CONCAT('%', :searchKey, '%')
    """)
    Page<RegistrationProjection> findBySearchKey(@Param("searchKey") String searchKey, Pageable pageable);

    @Query("SELECT r.photos FROM Registration r WHERE r.id = :id")
    Optional<String> findPhotoBy_Id(@Param("id") Long id);

    //Optional<Registration> findByEvent_IdAndUser_Id(Long eventId, Long userId);

    boolean existsByEventId(Long id);
}