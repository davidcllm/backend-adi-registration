package com.davcm.spring_jwt.Controller;

import com.davcm.spring_jwt.Model.Event;
import com.davcm.spring_jwt.Model.Total;
import com.davcm.spring_jwt.Service.TotalService;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin/total")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class AdminTotalController {
    private final TotalService totalService;
    private final PagedResourcesAssembler<Total> pagedResourcesAssembler;

    @GetMapping("/all")
    public ResponseEntity<PagedModel<EntityModel<Total>>> getAllTotals(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String searchKey) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Total> totals = totalService.findAllTotals(searchKey, pageable);
        PagedModel<EntityModel<Total>> pagedModel = pagedResourcesAssembler.toModel(totals);

        return new ResponseEntity<>(pagedModel, HttpStatus.OK);
    }
}
