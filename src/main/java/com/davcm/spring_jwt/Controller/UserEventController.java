package com.davcm.spring_jwt.Controller;

import com.davcm.spring_jwt.Jwt.JwtService;
import com.davcm.spring_jwt.Model.Event;
import com.davcm.spring_jwt.Model.Registration;
import com.davcm.spring_jwt.Model.User;
import com.davcm.spring_jwt.Repository.UserRepository;
import com.davcm.spring_jwt.Service.EventService;
import com.davcm.spring_jwt.Service.RegistrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/event")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
public class UserEventController {
    private final EventService eventService;
    private final RegistrationService registrationService;
    private final PagedResourcesAssembler pagedResourcesAssembler;

    /*@GetMapping("/all")
    public ResponseEntity<List<Event>> getAllEvents() {
        List<Event> events = eventService.findAllEvents();
        return new ResponseEntity<>(events, HttpStatus.OK);
    }*/
    @GetMapping("/all")
    public ResponseEntity<PagedModel<EntityModel<Event>>> getAllEvents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String searchKey) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Event> events = eventService.findAllEvents(searchKey, pageable);
        PagedModel<EntityModel<Event>> pagedModel = pagedResourcesAssembler.toModel(events);

        return new ResponseEntity<>(pagedModel, HttpStatus.OK);
    }
    @PostMapping("/register/{eventId}")
    public ResponseEntity<?> registerToEvent(
            @PathVariable("eventId") Long eventId,
            @RequestHeader("Authorization") String token) {

        try {
            String subToken = token.substring(7);
            Registration registration = registrationService.registerToEvent(eventId, subToken);
            return new ResponseEntity<>(registration, HttpStatus.OK);
        }
        catch(RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        }
    }
}