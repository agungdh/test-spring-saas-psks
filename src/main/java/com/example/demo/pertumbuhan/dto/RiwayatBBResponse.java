package com.example.demo.pertumbuhan.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record RiwayatBBResponse(
        Long id,
        Long userId,
        LocalDate tanggalUkur,
        BigDecimal beratBadanKg,
        BigDecimal tinggiBadanCm,
        Integer usiaKehamilanMinggu,
        String keterangan,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
