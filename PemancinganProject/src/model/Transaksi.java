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
    private Integer pelangganId; // Menggunakan Integer (bukan int) karena di DB bisa NULL (jika non-member)
    private LocalDateTime waktuCheckin;
    private LocalDateTime waktuCheckout;
    private int durasiPesanMenit;
    private int durasiAktualMenit;
    private BigDecimal totalTagihan;
    private String statusTransaksi; // 'AKTIF', 'SELESAI', 'BATAL'

    // Constructor Kosong
    public Transaksi() {
    }

    // Constructor Penuh
    public Transaksi(int id, int lapakId, Integer pelangganId, LocalDateTime waktuCheckin, 
                     LocalDateTime waktuCheckout, int durasiPesanMenit, int durasiAktualMenit, 
                     BigDecimal totalTagihan, String statusTransaksi) {
        this.id = id;
        this.lapakId = lapakId;
        this.pelangganId = pelangganId;
        this.waktuCheckin = waktuCheckin;
        this.waktuCheckout = waktuCheckout;
        this.durasiPesanMenit = durasiPesanMenit;
        this.durasiAktualMenit = durasiAktualMenit;
        this.totalTagihan = totalTagihan;
        this.statusTransaksi = statusTransaksi;
    }

    // Getter dan Setter
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLapakId() {
        return lapakId;
    }

    public void setLapakId(int lapakId) {
        this.lapakId = lapakId;
    }

    public Integer getPelangganId() {
        return pelangganId;
    }

    public void setPelangganId(Integer pelangganId) {
        this.pelangganId = pelangganId;
    }

    public LocalDateTime getWaktuCheckin() {
        return waktuCheckin;
    }

    public void setWaktuCheckin(LocalDateTime waktuCheckin) {
        this.waktuCheckin = waktuCheckin;
    }

    public LocalDateTime getWaktuCheckout() {
        return waktuCheckout;
    }

    public void setWaktuCheckout(LocalDateTime waktuCheckout) {
        this.waktuCheckout = waktuCheckout;
    }

    public int getDurasiPesanMenit() {
        return durasiPesanMenit;
    }

    public void setDurasiPesanMenit(int durasiPesanMenit) {
        this.durasiPesanMenit = durasiPesanMenit;
    }

    public int getDurasiAktualMenit() {
        return durasiAktualMenit;
    }

    public void setDurasiAktualMenit(int durasiAktualMenit) {
        this.durasiAktualMenit = durasiAktualMenit;
    }

    public BigDecimal getTotalTagihan() {
        return totalTagihan;
    }

    public void setTotalTagihan(BigDecimal totalTagihan) {
        this.totalTagihan = totalTagihan;
    }

    public String getStatusTransaksi() {
        return statusTransaksi;
    }

    public void setStatusTransaksi(String statusTransaksi) {
        this.statusTransaksi = statusTransaksi;
    }
}
