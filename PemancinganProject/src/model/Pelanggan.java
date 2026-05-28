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
public class Pelanggan {
    private int id;
    private String nama;
    private String nomorTelepon;
    private LocalDateTime createdAt;

    // Constructor Kosong
    public Pelanggan() {
    }

    // Constructor Penuh
    public Pelanggan(int id, String nama, String nomorTelepon, LocalDateTime createdAt) {
        this.id = id;
        this.nama = nama;
        this.nomorTelepon = nomorTelepon;
        this.createdAt = createdAt;
    }

    // Getter dan Setter
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getNomorTelepon() {
        return nomorTelepon;
    }

    public void setNomorTelepon(String nomorTelepon) {
        this.nomorTelepon = nomorTelepon;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
