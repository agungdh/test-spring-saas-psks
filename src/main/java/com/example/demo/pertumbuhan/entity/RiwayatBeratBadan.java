package com.example.demo.pertumbuhan.entity;

import com.example.demo.common.audit.AuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "riwayat_berat_badan")
@Getter
@Setter
public class RiwayatBeratBadan extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "tanggal_ukur", nullable = false)
    private LocalDate tanggalUkur;

    @Column(name = "berat_badan_kg", nullable = false, precision = 5, scale = 2)
    private BigDecimal beratBadanKg;

    @Column(name = "tinggi_badan_cm", precision = 5, scale = 2)
    private BigDecimal tinggiBadanCm;

    @Column(name = "usia_kehamilan_minggu")
    private Integer usiaKehamilanMinggu;

    @Column(name = "keterangan", columnDefinition = "TEXT")
    private String keterangan;
}
