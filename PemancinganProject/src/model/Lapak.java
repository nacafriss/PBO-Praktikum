/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author rei
 */
public class Lapak {
    private int id;
    private String nomorLapak;
    private int posisiX;
    private int posisiY;
    private String status; // 'KOSONG', 'TERISI', 'PERBAIKAN'
    private int tarifId;

    // Constructor Kosong (Default)
    public Lapak() {
    }

    // Constructor Penuh
    public Lapak(int id, String nomorLapak, int posisiX, int posisiY, String status, int tarifId) {
        this.id = id;
        this.nomorLapak = nomorLapak;
        this.posisiX = posisiX;
        this.posisiY = posisiY;
        this.status = status;
        this.tarifId = tarifId;
    }

    // Getter dan Setter
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNomorLapak() {
        return nomorLapak;
    }

    public void setNomorLapak(String nomorLapak) {
        this.nomorLapak = nomorLapak;
    }

    public int getPosisiX() {
        return posisiX;
    }

    public void setPosisiX(int posisiX) {
        this.posisiX = posisiX;
    }

    public int getPosisiY() {
        return posisiY;
    }

    public void setPosisiY(int posisiY) {
        this.posisiY = posisiY;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getTarifId() {
        return tarifId;
    }

    public void setTarifId(int tarifId) {
        this.tarifId = tarifId;
    }
}