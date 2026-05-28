/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.time.LocalDateTime;

/**
 *
 * @author rei
 */
public class Lapak {
    private int id;
    private int tarifId;
    private String namaLapak; // Sesuai dengan kolom nama_lapak
    private String jenisKolam; // 'HARIAN', 'KILOAN', 'GALATAMA'
    private String deskripsi;
    private String status; // 'KOSONG', 'TERISI', 'NONAKTIF'
    private int kapasitas;
    private boolean aktif; // Tipe tinyint(1) di DB
    private int posisiX;
    private int posisiY;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Lapak() {
    }

    public Lapak(int id, int tarifId, String namaLapak, String jenisKolam, String deskripsi, 
                 String status, int kapasitas, boolean aktif, int posisiX, int posisiY, 
                 LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.tarifId = tarifId;
        this.namaLapak = namaLapak;
        this.jenisKolam = jenisKolam;
        this.deskripsi = deskripsi;
        this.status = status;
        this.kapasitas = kapasitas;
        this.aktif = aktif;
        this.posisiX = posisiX;
        this.posisiY = posisiY;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getTarifId() { return tarifId; }
    public void setTarifId(int tarifId) { this.tarifId = tarifId; }

    public String getNamaLapak() { return namaLapak; }
    public void setNamaLapak(String namaLapak) { this.namaLapak = namaLapak; }

    public String getJenisKolam() { return jenisKolam; }
    public void setJenisKolam(String jenisKolam) { this.jenisKolam = jenisKolam; }

    public String getDeskripsi() { return deskripsi; }
    public void setDeskripsi(String deskripsi) { this.deskripsi = deskripsi; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getKapasitas() { return kapasitas; }
    public void setKapasitas(int kapasitas) { this.kapasitas = kapasitas; }

    public boolean isAktif() { return aktif; }
    public void setAktif(boolean aktif) { this.aktif = aktif; }

    public int getPosisiX() { return posisiX; }
    public void setPosisiX(int posisiX) { this.posisiX = posisiX; }

    public int getPosisiY() { return posisiY; }
    public void setPosisiY(int posisiY) { this.posisiY = posisiY; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}