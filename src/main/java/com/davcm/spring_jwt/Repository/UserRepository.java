package com.davcm.spring_jwt.Repository;

import com.davcm.spring_jwt.Model.User;
import com.davcm.spring_jwt.Projection.UserProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    @Query("SELECT r FROM User r WHERE r.id = :id")
    Optional<UserProjection> findBy_Id(@Param("id") Long id);
}

//Una interfaz en Java es un contrato que define un conjunto de métodos abstractos que una clase debe implementar.
