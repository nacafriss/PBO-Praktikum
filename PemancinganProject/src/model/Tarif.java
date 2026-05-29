/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author user
 */
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Tarif {
    private int id;
    private String namaTarif;
    private String jenisKolam;         // 'HARIAN', 'KILOAN', 'GALATAMA'
    private BigDecimal hargaPerJam;    // untuk HARIAN
    private BigDecimal biayaMasukKiloan; // biaya masuk untuk KILOAN
    private BigDecimal hargaTiketGalatama; // untuk GALATAMA
    private BigDecimal diskonMember;   // persen diskon untuk member
    private BigDecimal diskonVip;      // persen diskon untuk VIP
    private boolean aktif;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Tarif() {}

    public Tarif(int id, String namaTarif, String jenisKolam,
                 BigDecimal hargaPerJam, BigDecimal biayaMasukKiloan,
                 BigDecimal hargaTiketGalatama, BigDecimal diskonMember,
                 BigDecimal diskonVip, boolean aktif,
                 LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.namaTarif = namaTarif;
        this.jenisKolam = jenisKolam;
        this.hargaPerJam = hargaPerJam;
        this.biayaMasukKiloan = biayaMasukKiloan;
        this.hargaTiketGalatama = hargaTiketGalatama;
        this.diskonMember = diskonMember;
        this.diskonVip = diskonVip;
        this.aktif = aktif;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNamaTarif() { return namaTarif; }
    public void setNamaTarif(String namaTarif) { this.namaTarif = namaTarif; }

    public String getJenisKolam() { return jenisKolam; }
    public void setJenisKolam(String jenisKolam) { this.jenisKolam = jenisKolam; }

    public BigDecimal getHargaPerJam() { return hargaPerJam; }
    public void setHargaPerJam(BigDecimal hargaPerJam) { this.hargaPerJam = hargaPerJam; }

    public BigDecimal getBiayaMasukKiloan() { return biayaMasukKiloan; }
    public void setBiayaMasukKiloan(BigDecimal biayaMasukKiloan) { this.biayaMasukKiloan = biayaMasukKiloan; }

    public BigDecimal getHargaTiketGalatama() { return hargaTiketGalatama; }
    public void setHargaTiketGalatama(BigDecimal hargaTiketGalatama) { this.hargaTiketGalatama = hargaTiketGalatama; }

    public BigDecimal getDiskonMember() { return diskonMember; }
    public void setDiskonMember(BigDecimal diskonMember) { this.diskonMember = diskonMember; }

    public BigDecimal getDiskonVip() { return diskonVip; }
    public void setDiskonVip(BigDecimal diskonVip) { this.diskonVip = diskonVip; }

    public boolean isAktif() { return aktif; }
    public void setAktif(boolean aktif) { this.aktif = aktif; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}