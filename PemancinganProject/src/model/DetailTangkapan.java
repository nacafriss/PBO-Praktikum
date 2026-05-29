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

public class DetailTangkapan {
    private int id;
    private int transaksiId;
    private String namaIkan;          // Nama jenis ikan yang ditangkap
    private BigDecimal beratKg;       // Berat ikan dalam kg
    private BigDecimal hargaPerKg;    // Harga per kg saat transaksi (snapshot)
    private BigDecimal subtotal;      // beratKg * hargaPerKg
    private String catatan;
    private LocalDateTime createdAt;

    public DetailTangkapan() {}

    public DetailTangkapan(int id, int transaksiId, String namaIkan,
                            BigDecimal beratKg, BigDecimal hargaPerKg,
                            BigDecimal subtotal, String catatan,
                            LocalDateTime createdAt) {
        this.id = id;
        this.transaksiId = transaksiId;
        this.namaIkan = namaIkan;
        this.beratKg = beratKg;
        this.hargaPerKg = hargaPerKg;
        this.subtotal = subtotal;
        this.catatan = catatan;
        this.createdAt = createdAt;
    }

    // =========================================================
    // GETTER & SETTER
    // =========================================================

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getTransaksiId() { return transaksiId; }
    public void setTransaksiId(int transaksiId) { this.transaksiId = transaksiId; }

    public String getNamaIkan() { return namaIkan; }
    public void setNamaIkan(String namaIkan) { this.namaIkan = namaIkan; }

    public BigDecimal getBeratKg() { return beratKg; }
    public void setBeratKg(BigDecimal beratKg) { this.beratKg = beratKg; }

    public BigDecimal getHargaPerKg() { return hargaPerKg; }
    public void setHargaPerKg(BigDecimal hargaPerKg) { this.hargaPerKg = hargaPerKg; }

    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }

    public String getCatatan() { return catatan; }
    public void setCatatan(String catatan) { this.catatan = catatan; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    /**
     * Hitung subtotal otomatis dari berat dan harga per kg.
     * Dipanggil sebelum insert ke DB.
     */
    public void hitungSubtotal() {
        if (beratKg != null && hargaPerKg != null) {
            this.subtotal = beratKg.multiply(hargaPerKg);
        } else {
            this.subtotal = BigDecimal.ZERO;
        }
    }

    @Override
    public String toString() {
        return namaIkan + " - " + beratKg + " kg";
    }
}
