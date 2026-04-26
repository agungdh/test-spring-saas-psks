package com.example.demo.nutrisi.repository;

import com.example.demo.nutrisi.entity.RiwayatAsupanNutrisi;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RiwayatAsupanNutrisiRepository extends JpaRepository<RiwayatAsupanNutrisi, Long> {

    @Query("SELECT r FROM RiwayatAsupanNutrisi r WHERE r.deletedAt IS NULL ORDER BY r.tanggal DESC")
    Page<RiwayatAsupanNutrisi> findAllActive(Pageable pageable);

    @Query("SELECT r FROM RiwayatAsupanNutrisi r WHERE r.userId = :userId AND r.deletedAt IS NULL ORDER BY r.tanggal DESC")
    Page<RiwayatAsupanNutrisi> findAllByUserIdAndDeletedAtIsNull(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT r FROM RiwayatAsupanNutrisi r WHERE r.id = :id AND r.deletedAt IS NULL")
    Optional<RiwayatAsupanNutrisi> findByIdAndDeletedAtIsNull(@Param("id") Long id);
}
