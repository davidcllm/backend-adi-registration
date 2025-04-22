package com.davcm.spring_jwt.Service;

import com.davcm.spring_jwt.Model.Event;
import com.davcm.spring_jwt.Repository.EventRepository;
import com.davcm.spring_jwt.Repository.RegistrationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final RegistrationRepository registrationRepository;

    @Transactional(readOnly = true)
    public Page<Event> findAllEvents(String searchKey, Pageable pageable) {
        Sort sort = Sort.by(Sort.Order.desc("fecha"));
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        if (searchKey != null && !searchKey.trim().isEmpty()) {
            return eventRepository.findBySearchKey(searchKey, sortedPageable);
        }
        else {
            return eventRepository.findAll(sortedPageable);
        }
    }
    public Event addEvent(Event event) {
        validateDate(event);
        return eventRepository.save(event);
    }
    public Event updateEvent(Event event) {
        validateDate(event);
        return eventRepository.save(event);
    }
    public void deleteById(Long id) {
        if(registrationRepository.existsByEventId(id)) {
            throw new RuntimeException("No se puede eliminar el evento, porque hay registros asociados");
        }

        if (eventRepository.existsById(id)) {
            eventRepository.deleteById(id);
        } else {
            throw new RuntimeException("Evento no encontrado");
        }
    }
    private void validateDate(Event event) {
        LocalDate date = LocalDate.now();
        if(event.getFecha().isBefore(date)) {
            throw new IllegalArgumentException("No puedes agregar un evento con una fecha pasada.");
        }
    }
}
