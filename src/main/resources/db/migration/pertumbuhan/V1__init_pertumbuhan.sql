CREATE TABLE IF NOT EXISTS riwayat_berat_badan (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    tanggal_ukur DATE NOT NULL,
    berat_badan_kg DECIMAL(5,2) NOT NULL,
    tinggi_badan_cm DECIMAL(5,2) NULL,
    usia_kehamilan_minggu INT NULL,
    keterangan TEXT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP NULL,
    created_by BIGINT NULL,
    updated_by BIGINT NULL,
    deleted_by BIGINT NULL
);

CREATE INDEX idx_riwayat_bb_user ON riwayat_berat_badan(user_id);
CREATE INDEX idx_riwayat_bb_tanggal ON riwayat_berat_badan(tanggal_ukur);
CREATE INDEX idx_riwayat_bb_deleted ON riwayat_berat_badan(deleted_at);
