package com.example.demo.nutrisi.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record RiwayatNutrisiResponse(
        Long id,
        Long userId,
        LocalDate tanggal,
        String jenisMakanan,
        Integer kalori,
        BigDecimal proteinG,
        BigDecimal karbohidratG,
        BigDecimal lemakG,
        String vitamin,
        String keterangan,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
