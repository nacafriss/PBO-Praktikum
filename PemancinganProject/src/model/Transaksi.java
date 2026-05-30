/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 *
 * @author rei
 */
public class Transaksi {
    private int id;
    private int lapakId;
    private Integer pelangganId; // Menggunakan Integer karena bisa NULL
    private String jenisKolam; // 'HARIAN', 'KILOAN', 'GALATAMA'
    private String namaPelanggan; // Snapshot nama pelanggan
    private String noHpPelanggan; // Snapshot no hp pelanggan
    private Integer posisi;
    private LocalDateTime waktuCheckin;
    private LocalDateTime waktuCheckout; // Bisa NULL
    private Integer durasiAktualMenit; // Bisa NULL
    private Integer durasiPesanMenit; // Bisa NULL
    private BigDecimal subtotalIkan;
    private BigDecimal biayaSewa;
    private BigDecimal diskon;
    private int poinDigunakan;
    private BigDecimal totalTagihan; // Bisa NULL sebelum checkout
    private String metodeBayar; // 'TUNAI', 'TRANSFER', 'QRIS', 'MEMBER_POIN'
    private String statusTransaksi; // 'AKTIF', 'SELESAI', 'DIBATALKAN', 'EXPIRED'
    private String catatan;
    private String createdBy;

    public Transaksi() {
    }

    public Transaksi(int id, int lapakId, Integer pelangganId, String jenisKolam, String namaPelanggan, 
                     String noHpPelanggan, Integer posisi, LocalDateTime waktuCheckin, LocalDateTime waktuCheckout, 
                     Integer durasiAktualMenit, Integer durasiPesanMenit, BigDecimal subtotalIkan, 
                     BigDecimal biayaSewa, BigDecimal diskon, int poinDigunakan, BigDecimal totalTagihan, 
                     String metodeBayar, String statusTransaksi, String catatan, String createdBy) {
        this.id = id;
        this.lapakId = lapakId;
        this.pelangganId = pelangganId;
        this.jenisKolam = jenisKolam;
        this.namaPelanggan = namaPelanggan;
        this.noHpPelanggan = noHpPelanggan;
        this.posisi = posisi;
        this.waktuCheckin = waktuCheckin;
        this.waktuCheckout = waktuCheckout;
        this.durasiAktualMenit = durasiAktualMenit;
        this.durasiPesanMenit = durasiPesanMenit;
        this.subtotalIkan = subtotalIkan;
        this.biayaSewa = biayaSewa;
        this.diskon = diskon;
        this.poinDigunakan = poinDigunakan;
        this.totalTagihan = totalTagihan;
        this.metodeBayar = metodeBayar;
        this.statusTransaksi = statusTransaksi;
        this.catatan = catatan;
        this.createdBy = createdBy;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getLapakId() { return lapakId; }
    public void setLapakId(int lapakId) { this.lapakId = lapakId; }

    public Integer getPelangganId() { return pelangganId; }
    public void setPelangganId(Integer pelangganId) { this.pelangganId = pelangganId; }

    public String getJenisKolam() { return jenisKolam; }
    public void setJenisKolam(String jenisKolam) { this.jenisKolam = jenisKolam; }

    public String getNamaPelanggan() { return namaPelanggan; }
    public void setNamaPelanggan(String namaPelanggan) { this.namaPelanggan = namaPelanggan; }

    public String getNoHpPelanggan() { return noHpPelanggan; }
    public void setNoHpPelanggan(String noHpPelanggan) { this.noHpPelanggan = noHpPelanggan; }
    
    public Integer getPosisi() { return posisi; }
    public void setPosisi(Integer posisi) { this.posisi = posisi; }

    public LocalDateTime getWaktuCheckin() { return waktuCheckin; }
    public void setWaktuCheckin(LocalDateTime waktuCheckin) { this.waktuCheckin = waktuCheckin; }

    public LocalDateTime getWaktuCheckout() { return waktuCheckout; }
    public void setWaktuCheckout(LocalDateTime waktuCheckout) { this.waktuCheckout = waktuCheckout; }

    public Integer getDurasiAktualMenit() { return durasiAktualMenit; }
    public void setDurasiAktualMenit(Integer durasiAktualMenit) { this.durasiAktualMenit = durasiAktualMenit; }

    public Integer getDurasiPesanMenit() { return durasiPesanMenit; }
    public void setDurasiPesanMenit(Integer durasiPesanMenit) { this.durasiPesanMenit = durasiPesanMenit; }

    public BigDecimal getSubtotalIkan() { return subtotalIkan; }
    public void setSubtotalIkan(BigDecimal subtotalIkan) { this.subtotalIkan = subtotalIkan; }

    public BigDecimal getBiayaSewa() { return biayaSewa; }
    public void setBiayaSewa(BigDecimal biayaSewa) { this.biayaSewa = biayaSewa; }

    public BigDecimal getDiskon() { return diskon; }
    public void setDiskon(BigDecimal diskon) { this.diskon = diskon; }

    public int getPoinDigunakan() { return poinDigunakan; }
    public void setPoinDigunakan(int poinDigunakan) { this.poinDigunakan = poinDigunakan; }

    public BigDecimal getTotalTagihan() { return totalTagihan; }
    public void setTotalTagihan(BigDecimal totalTagihan) { this.totalTagihan = totalTagihan; }

    public String getMetodeBayar() { return metodeBayar; }
    public void setMetodeBayar(String metodeBayar) { this.metodeBayar = metodeBayar; }

    public String getStatusTransaksi() { return statusTransaksi; }
    public void setStatusTransaksi(String statusTransaksi) { this.statusTransaksi = statusTransaksi; }

    public String getCatatan() { return catatan; }
    public void setCatatan(String catatan) { this.catatan = catatan; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
}
