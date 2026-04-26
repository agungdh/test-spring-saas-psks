package com.example.demo.nutrisi.dto;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

public record RiwayatNutrisiRequest(
        @NotNull LocalDate tanggal,
        String jenisMakanan,
        Integer kalori,
        BigDecimal proteinG,
        BigDecimal karbohidratG,
        BigDecimal lemakG,
        String vitamin,
        String keterangan,
        Long userId
) {
}
