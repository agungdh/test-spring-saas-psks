package com.example.demo.pertumbuhan.web;

import com.example.demo.common.web.PageResponse;
import com.example.demo.pertumbuhan.dto.RiwayatBBRequest;
import com.example.demo.pertumbuhan.dto.RiwayatBBResponse;
import com.example.demo.pertumbuhan.service.PertumbuhanService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/pertumbuhan")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Pertumbuhan")
public class PertumbuhanController {

    private final PertumbuhanService pertumbuhanService;

    @PostMapping
    @PreAuthorize("hasAnyRole('BUMIL','PETUGAS','ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public RiwayatBBResponse create(@Valid @RequestBody RiwayatBBRequest request) {
        return pertumbuhanService.create(request);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('BUMIL','PETUGAS','ADMIN')")
    public PageResponse<RiwayatBBResponse> findAll(Pageable pageable) {
        return pertumbuhanService.findAll(pageable);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('BUMIL','PETUGAS','ADMIN')")
    public RiwayatBBResponse findById(@PathVariable Long id) {
        return pertumbuhanService.findById(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('BUMIL','PETUGAS','ADMIN')")
    public RiwayatBBResponse update(@PathVariable Long id, @Valid @RequestBody RiwayatBBRequest request) {
        return pertumbuhanService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('BUMIL','PETUGAS','ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        pertumbuhanService.delete(id);
    }
}
