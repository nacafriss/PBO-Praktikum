/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.time.LocalDateTime;
import java.math.BigDecimal;

/**
 *
 * @author rei
 */
public class Pelanggan {
    private int id;
    private String nama;
    private String noHp; // Sesuai dengan kolom no_hp
    private String email;
    private String alamat;
    private String tipeMember; // 'UMUM', 'MEMBER', 'VIP'
    private int poin;
    private int totalKunjungan;
    private BigDecimal totalBelanja;
    private boolean aktif;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Pelanggan() {
    }

    public Pelanggan(int id, String nama, String noHp, String email, String alamat, 
                     String tipeMember, int poin, int totalKunjungan, BigDecimal totalBelanja, 
                     boolean aktif, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.nama = nama;
        this.noHp = noHp;
        this.email = email;
        this.alamat = alamat;
        this.tipeMember = tipeMember;
        this.poin = poin;
        this.totalKunjungan = totalKunjungan;
        this.totalBelanja = totalBelanja;
        this.aktif = aktif;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNama() { return nama; }
    public void setNama(String nama) { this.nama = nama; }

    public String getNoHp() { return noHp; }
    public void setNoHp(String noHp) { this.noHp = noHp; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getAlamat() { return alamat; }
    public void setAlamat(String alamat) { this.alamat = alamat; }

    public String getTipeMember() { return tipeMember; }
    public void setTipeMember(String tipeMember) { this.tipeMember = tipeMember; }

    public int getPoin() { return poin; }
    public void setPoin(int poin) { this.poin = poin; }

    public int getTotalKunjungan() { return totalKunjungan; }
    public void setTotalKunjungan(int totalKunjungan) { this.totalKunjungan = totalKunjungan; }

    public BigDecimal getTotalBelanja() { return totalBelanja; }
    public void setTotalBelanja(BigDecimal totalBelanja) { this.totalBelanja = totalBelanja; }

    public boolean isAktif() { return aktif; }
    public void setAktif(boolean aktif) { this.aktif = aktif; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
