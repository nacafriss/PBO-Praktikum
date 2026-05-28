/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.math.BigDecimal;

/**
 *
 * @author rei
 */
public abstract class LayananKolam {
    
    protected String jenisLayanan;

    public LayananKolam(String jenisLayanan) {
        this.jenisLayanan = jenisLayanan;
    }

    public String getJenisLayanan() {
        return jenisLayanan;
    }

    public abstract BigDecimal hitungTagihan(int durasiMenit, BigDecimal tarifDasar, BigDecimal totalBeratKg);
}
