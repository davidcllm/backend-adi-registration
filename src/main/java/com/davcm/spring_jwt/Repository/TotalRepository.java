package com.davcm.spring_jwt.Repository;

import com.davcm.spring_jwt.Model.Total;
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
}