CREATE TABLE IF NOT EXISTS riwayat_asupan_nutrisi (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    tanggal DATE NOT NULL,
    jenis_makanan VARCHAR(255) NULL,
    kalori INT NULL,
    protein_g DECIMAL(6,2) NULL,
    karbohidrat_g DECIMAL(6,2) NULL,
    lemak_g DECIMAL(6,2) NULL,
    vitamin VARCHAR(255) NULL,
    keterangan TEXT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP NULL,
    created_by BIGINT NULL,
    updated_by BIGINT NULL,
    deleted_by BIGINT NULL
);

CREATE INDEX idx_riwayat_nutrisi_user ON riwayat_asupan_nutrisi(user_id);
CREATE INDEX idx_riwayat_nutrisi_tanggal ON riwayat_asupan_nutrisi(tanggal);
CREATE INDEX idx_riwayat_nutrisi_deleted ON riwayat_asupan_nutrisi(deleted_at);
