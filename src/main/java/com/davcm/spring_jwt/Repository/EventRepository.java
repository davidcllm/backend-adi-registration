package com.davcm.spring_jwt.Repository;

import com.davcm.spring_jwt.Model.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EventRepository extends JpaRepository<Event, Long> {
    Page<Event> findAll(Pageable pageable);
    @Query("""
        SELECT e FROM Event e 
        WHERE CAST(e.id AS string) LIKE CONCAT('%', :searchKey, '%')
        OR LOWER(e.eventName) LIKE LOWER(CONCAT('%', :searchKey, '%'))
    """)
    Page<Event> findBySearchKey(@Param("searchKey") String searchKey, Pageable pageable);
}

