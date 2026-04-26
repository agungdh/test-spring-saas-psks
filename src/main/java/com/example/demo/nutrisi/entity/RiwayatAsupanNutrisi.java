package com.example.demo.nutrisi.entity;

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
@Table(name = "riwayat_asupan_nutrisi")
@Getter
@Setter
public class RiwayatAsupanNutrisi extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "tanggal", nullable = false)
    private LocalDate tanggal;

    @Column(name = "jenis_makanan")
    private String jenisMakanan;

    @Column(name = "kalori")
    private Integer kalori;

    @Column(name = "protein_g", precision = 6, scale = 2)
    private BigDecimal proteinG;

    @Column(name = "karbohidrat_g", precision = 6, scale = 2)
    private BigDecimal karbohidratG;

    @Column(name = "lemak_g", precision = 6, scale = 2)
    private BigDecimal lemakG;

    @Column(name = "vitamin")
    private String vitamin;

    @Column(name = "keterangan", columnDefinition = "TEXT")
    private String keterangan;
}
