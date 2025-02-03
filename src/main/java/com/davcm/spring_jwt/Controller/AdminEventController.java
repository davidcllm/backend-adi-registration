package com.davcm.spring_jwt.Controller;

import com.davcm.spring_jwt.Model.Event;
import com.davcm.spring_jwt.Model.User;
import com.davcm.spring_jwt.Projection.RegistrationProjection;
import com.davcm.spring_jwt.Service.EventService;
import com.davcm.spring_jwt.Service.UserService;
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
@RequestMapping("/admin/event")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class AdminEventController {
    private final EventService eventService;
    private final UserService userService;
    private final PagedResourcesAssembler<Event> pagedResourcesAssembler;

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
    @PostMapping("/add")
    public ResponseEntity<Event> addEvent(@RequestBody Event event) {
        Event newEvent = eventService.addEvent(event);
        return new ResponseEntity<>(newEvent, HttpStatus.CREATED);
    }
    @PutMapping("/update")
    public ResponseEntity<Event> updateEvent(@RequestBody Event event) {
        Event updatedEvent = eventService.updateEvent(event);
        return new ResponseEntity<>(updatedEvent, HttpStatus.OK);
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteById(@PathVariable("id") Long id) {
        try {
            eventService.deleteById(id);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        catch(RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /*@PutMapping("/user")
    public ResponseEntity<User> updateEvent(@RequestBody User user) {
        User updatedUser = userService.updateUser(user);
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }*/
}
