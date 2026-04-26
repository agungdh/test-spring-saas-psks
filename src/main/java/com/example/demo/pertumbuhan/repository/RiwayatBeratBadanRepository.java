package com.example.demo.pertumbuhan.repository;

import com.example.demo.pertumbuhan.entity.RiwayatBeratBadan;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RiwayatBeratBadanRepository extends JpaRepository<RiwayatBeratBadan, Long> {

    @Query("SELECT r FROM RiwayatBeratBadan r WHERE r.deletedAt IS NULL ORDER BY r.tanggalUkur DESC")
    Page<RiwayatBeratBadan> findAllActive(Pageable pageable);

    @Query("SELECT r FROM RiwayatBeratBadan r WHERE r.userId = :userId AND r.deletedAt IS NULL ORDER BY r.tanggalUkur DESC")
    Page<RiwayatBeratBadan> findAllByUserIdAndDeletedAtIsNull(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT r FROM RiwayatBeratBadan r WHERE r.id = :id AND r.deletedAt IS NULL")
    Optional<RiwayatBeratBadan> findByIdAndDeletedAtIsNull(@Param("id") Long id);
}
