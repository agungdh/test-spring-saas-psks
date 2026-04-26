package com.example.demo.nutrisi.web;

import com.example.demo.common.web.PageResponse;
import com.example.demo.nutrisi.dto.RiwayatNutrisiRequest;
import com.example.demo.nutrisi.dto.RiwayatNutrisiResponse;
import com.example.demo.nutrisi.service.NutrisiService;
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
@RequestMapping("/api/nutrisi")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Nutrisi")
public class NutrisiController {

    private final NutrisiService nutrisiService;

    @PostMapping
    @PreAuthorize("hasAnyRole('BUMIL','PETUGAS','ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public RiwayatNutrisiResponse create(@Valid @RequestBody RiwayatNutrisiRequest request) {
        return nutrisiService.create(request);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('BUMIL','PETUGAS','ADMIN')")
    public PageResponse<RiwayatNutrisiResponse> findAll(Pageable pageable) {
        return nutrisiService.findAll(pageable);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('BUMIL','PETUGAS','ADMIN')")
    public RiwayatNutrisiResponse findById(@PathVariable Long id) {
        return nutrisiService.findById(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('BUMIL','PETUGAS','ADMIN')")
    public RiwayatNutrisiResponse update(@PathVariable Long id, @Valid @RequestBody RiwayatNutrisiRequest request) {
        return nutrisiService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('BUMIL','PETUGAS','ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        nutrisiService.delete(id);
    }
}
