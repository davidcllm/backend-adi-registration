package com.davcm.spring_jwt.Controller;

import com.davcm.spring_jwt.Model.Registration;
import com.davcm.spring_jwt.Projection.RegistrationProjection;
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

@RestController
@RequestMapping("/admin/registration")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class AdminRegistrationController {
    private final RegistrationService registrationService;
    private final PagedResourcesAssembler<RegistrationProjection> pagedResourcesAssembler;
    private final String urlPrefix = "https://objectstorage.us-phoenix-1.oraclecloud.com";

    @GetMapping("/all")
    public ResponseEntity<PagedModel<EntityModel<RegistrationProjection>>> getRegistrations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String searchKey) {
        Pageable pageable = PageRequest.of(page, size);
        Page<RegistrationProjection> registrations = registrationService.findAllRegistrations(searchKey, pageable);
        PagedModel<EntityModel<RegistrationProjection>> pagedModel = pagedResourcesAssembler.toModel(registrations);

        return new ResponseEntity<>(pagedModel, HttpStatus.OK);
    }
    @GetMapping("/photo/{id}")
    public ResponseEntity<String> getRegistrationPhoto(@PathVariable Long id) {
        try {
            String photoUrl = registrationService.findPhotoById(id);
            return new ResponseEntity<>(urlPrefix + photoUrl, HttpStatus.OK);
        }
        catch(RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    @PatchMapping("/approve/{id}")
    public ResponseEntity<String> updateApprovalStatus(@PathVariable("id") Long id, @RequestBody String status) {
        try {
            if(status.startsWith("\"") && status.endsWith("\"")) {
                status = status.substring(1, status.length() - 1);
            }

            registrationService.updateApprovalStatus(id, status);
            return new ResponseEntity<>("Estatus de aprobación modificado exitosamente", HttpStatus.OK);
        }
        catch(RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteRegistration(@PathVariable Long id) {
        try {
            registrationService.deleteRegistration(id);
            return new ResponseEntity<>("Registro eliminado exitosamente", HttpStatus.OK);
        }
        catch(RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}