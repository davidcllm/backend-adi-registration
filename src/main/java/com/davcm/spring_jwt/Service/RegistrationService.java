package com.davcm.spring_jwt.Service;

import com.davcm.spring_jwt.Config.OsClientConfiguration;
import com.davcm.spring_jwt.Jwt.JwtService;
import com.davcm.spring_jwt.Model.Event;
import com.davcm.spring_jwt.Model.Registration;
import com.davcm.spring_jwt.Model.Total;
import com.davcm.spring_jwt.Model.User;
import com.davcm.spring_jwt.Projection.RegistrationProjection;
import com.davcm.spring_jwt.Repository.EventRepository;
import com.davcm.spring_jwt.Repository.RegistrationRepository;
import com.davcm.spring_jwt.Repository.TotalRepository;
import com.davcm.spring_jwt.Repository.UserRepository;
//import com.oracle.bmc.objectstorage.ObjectStorageClient;
import com.oracle.bmc.objectstorage.requests.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cglib.core.Local;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RegistrationService {
    private final RegistrationRepository registrationRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final TotalRepository totalRepository;
    private final JwtService jwtService;
    private final FileUploadService fileUploadService;

    public Registration registerToEvent(Long eventId, String token) {
        String usernameFromToken = jwtService.getUsernameFromToken(token);
        User user = userRepository.findByUsername(usernameFromToken)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Long userId = user.getId();

        Event event = eventRepository.findById(eventId)
            .orElseThrow(() -> new RuntimeException("Evento no encontrado"));

        //LocalDate today = LocalDate.now();
        LocalDateTime eventDateTime = LocalDateTime.of(event.getFecha(), event.getHora());
        LocalDateTime now = LocalDateTime.now();
        if(now.isAfter(eventDateTime.minusMinutes(40))) {
            throw new RuntimeException("Ya no se puede registrar, el evento empieza en 40 minutos");
        }

        Optional<Registration> existingRegistration = registrationRepository.findByUserAndEvent(user, event);
        if(existingRegistration.isPresent()) {
            throw new RuntimeException("El usuario ya está registrado a este evento");
        }

        if(event.getCupo() <= 0) {
            throw new RuntimeException("No hay cupo disponible para este evento");
        }

        Total total = totalRepository.findByUserId(userId)
            .orElseThrow(() -> new RuntimeException("Total no encontrado con el id de usuario: " + userId));

        /*if(total.getTotalEvents() >= 5) {
            throw new RuntimeException("Ya tienes tu 5 eventos registrados");
        }*/

        Registration registration = Registration.builder()
            .user(user)
            .event(event)
            .scan(false)
            .aprobado("ESPERA")
            .build();

        event.setCupo(event.getCupo() - 1);
        eventRepository.save(event);

        return registrationRepository.save(registration);
    }

    @Transactional(readOnly = true)
    public List<RegistrationProjection> findPreRegistrationsByUserId(String token) {
        String usernameFromToken = jwtService.getUsernameFromToken(token);
        User user = userRepository.findByUsername(usernameFromToken)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Long userId = user.getId();

        return registrationRepository.findByUser_Id(userId, "ESPERA")
            .orElseThrow(() -> new RuntimeException("Los preregistros con el id: " + userId + " no ha sido encontrado"));
    }

    @Transactional(readOnly = true)
    public List<RegistrationProjection> findRegistrationsByUserId(String token) {
        String usernameFromToken = jwtService.getUsernameFromToken(token);
        User user = userRepository.findByUsername(usernameFromToken)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Long userId = user.getId();

        return registrationRepository.findRegistrationsByUser_Id(userId)
            .orElseThrow(() -> new RuntimeException("Los registros con el id: " + userId + " no ha sido encontrado"));
    }

    public void savePhoto(Long registrationId, MultipartFile file, String token) throws Exception {
        Registration registration = registrationRepository.findById(registrationId)
            .orElseThrow(() -> new RuntimeException("Registro no encontrado"));

        String usernameFromToken = jwtService.getUsernameFromToken(token);
        User user = registration.getUser();

        if(!user.getUsername().equals(usernameFromToken)) {
            throw new RuntimeException("No tienes permiso para acceder a esta información");
        }

        if(!"ESPERA".equals(registration.getAprobado())) {
            throw new RuntimeException("El regsitro no se encuentra en estado de ESPERA");
        }

        /*if(!registration.getScan()) {
            throw new RuntimeException("El código QR aún no ha sido escaneado");
        }*/

        if(registration.getPhotos() != null && !registration.getPhotos().isEmpty()) {
            throw new RuntimeException("Ya hay una foto subida para este registro");
        }

        //System.out.println("cumple con validaciones");

        String objectName = fileUploadService.upload(file);
        String photoUrl = fileUploadService.getFileObject(objectName).getPreauthenticatedRequest().getAccessUri();

        registration.setPhotos(photoUrl);
        registrationRepository.save(registration);
    }

    @Transactional(readOnly = true)
    public Page<RegistrationProjection> findAllRegistrations(String searchKey, Pageable pageable) {
        Sort sort = Sort.by(Sort.Order.asc("user"));
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        if (searchKey != null && !searchKey.trim().isEmpty()) {
            return registrationRepository.findBySearchKey(searchKey, sortedPageable);
        }
        else {
            return registrationRepository.findAllProjectedBy(sortedPageable);
        }
    }

    @Transactional(readOnly = true)
    public String findPhotoById(Long id) {
        return registrationRepository.findPhotoBy_Id(id)
            .orElseThrow(() -> new RuntimeException("Foto no encontrada con id: " + id));
    }

    @Transactional
    public void updateApprovalStatus(Long id, String status) {
        List<String> validStatus = Arrays.asList("APROBADO", "PENALIZADO");
        if(!validStatus.contains(status)) {
            throw new RuntimeException("Estado de aprobación inválido " + status);
        }

        Registration registration = registrationRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Registro no encontrado con id:" + id));

        /*if(!"ESPERA".equals(registration.getAprobado())) {
            throw new RuntimeException("El registro ya no se encuentra en estado de ESPERA");
        }*/

        /*if(!registration.getScan()) {
            throw new RuntimeException("El código qr aún no ha sido escaneado");
        }*/

        if("ESPERA".equals(registration.getAprobado()) && "APROBADO".equals(status)) {
            //registration.setPhotos(null);
            String eventType = registration.getEvent().getTipo();
            Long userId = registration.getUser().getId();
            Total total = totalRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Total no encontrado para el usuario con id: " + id));

            switch(eventType) {
                case "ACADÉMICA":
                    total.setAcademicEvents(total.getAcademicEvents() + 1);
                    break;
                case "ASUA":
                    total.setAsuaEvents(total.getAsuaEvents() + 1);
                    break;
                case "CULTURAL":
                    total.setCulturalEvents(total.getCulturalEvents() + 1);
                    break;
                case "SOCIEDAD":
                    total.setSociedadEvents(total.getSociedadEvents() + 1);
                    break;
                case "DEPORTIVA":
                    total.setSportEvents(total.getSportEvents() + 1);
                    break;
            }
            total.setTotalEvents(total.getTotalEvents() + 1);
            registration.setAprobado(status);
            totalRepository.save(total);
        }
        else if("ESPERA".equals(registration.getAprobado()) && "PENALIZADO".equals(status)) {
            //registration.setPhotos(null);
            Long userId = registration.getUser().getId();
            Total total = totalRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Total no encontrado para el usuario con id: " + id));

            total.setTotalEvents(total.getTotalEvents() - 1);
            total.setPenalty(total.getPenalty() + 1);
            registration.setAprobado(status);
            totalRepository.save(total);
        }
        // Quitar penalizado
        else if("PENALIZADO".equals(registration.getAprobado()) && "APROBADO".equals(status)) {
            Long userId = registration.getUser().getId();
            Total total = totalRepository.findByUserId(userId)
                    .orElseThrow(() -> new RuntimeException("Total no encontrado para el usuario con id: " + id));

            registration.setPhotos(null);
            registration.setAprobado("ESPERA");
            total.setTotalEvents(total.getTotalEvents() + 1);
            total.setPenalty(total.getPenalty() - 1);
            totalRepository.save(total);
        }
        //Quitar aprobado
        else if("APROBADO".equals(registration.getAprobado()) && "PENALIZADO".equals(status)) {
            String eventType = registration.getEvent().getTipo();
            Long userId = registration.getUser().getId();
            Total total = totalRepository.findByUserId(userId)
                    .orElseThrow(() -> new RuntimeException("Total no encontrado para el usuario con id: " + id));

            switch(eventType) {
                case "ACADÉMICA":
                    total.setAcademicEvents(total.getAcademicEvents() - 1);
                    break;
                case "ASUA":
                    total.setAsuaEvents(total.getAsuaEvents() - 1);
                    break;
                case "CULTURAL":
                    total.setCulturalEvents(total.getCulturalEvents() - 1);
                    break;
                case "SOCIEDAD":
                    total.setSociedadEvents(total.getSociedadEvents() - 1);
                    break;
                case "DEPORTIVA":
                    total.setSportEvents(total.getSportEvents() - 1);
                    break;
            }

            registration.setPhotos(null);
            registration.setAprobado("ESPERA");
            total.setTotalEvents(total.getTotalEvents() - 1);
            totalRepository.save(total);
        }
        else {
            throw new RuntimeException("Error al intentar cambiar el estado de aprobación.");
        }

        registrationRepository.save(registration);
    }

    public void deleteRegistration(Long id) {
        Registration registration = registrationRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Registro no encontrado con id: " + id));

        if(!"ESPERA".equals(registration.getAprobado())) {
            throw new RuntimeException("El registro no se puede eliminar porque no está en estado de ESPERA");
        }

        if(registration.getScan()) {
            throw new RuntimeException("El registro no se puede eliminar porque el qr ya fue escaneado");
        }

        Event event = registration.getEvent();
        event.setCupo(event.getCupo() + 1);
        eventRepository.save(event);

        registrationRepository.delete(registration);
    }

    @Transactional
    public void updateScanStatus(Long registrationId) {
        Registration registration = registrationRepository.findById(registrationId)
            .orElseThrow(() -> new RuntimeException("No se encontró el registro con el id: " + registrationId));

        if(!"ESPERA".equals(registration.getAprobado())) {
            throw new RuntimeException("El regsitro no se encuentra en estado de ESPERA");
        }

        if(registration.getScan()) {
            throw new RuntimeException("El registro ya fue escaneado anteriormente");
        }

        registration.setScan(true);
        registrationRepository.save(registration);
    }
}

//La anotación @Transactional en Spring se utiliza para gestionar transacciones de manera declarativa
// en una aplicación. Una transacción es una secuencia de operaciones que se ejecutan como una sola unidad
// lógica de trabajo, que debe ser completada en su totalidad o no ser ejecutada en absoluto. La anotación
// @Transactional permite controlar el comportamiento de la transacción para métodos individuales o clases enteras.