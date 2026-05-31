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

public class JenisIkan {
    private int id;
    private String namaIkan;
    private String namaLatin;
    private BigDecimal hargaPerKg;
    private BigDecimal hargaPerEkor;
    private String satuanHitung;
    private Integer beratRataGram;
    private BigDecimal stokTotalKg;
    private BigDecimal stokMinimumKg;
    private String deskripsi;
    private boolean aktif;

    public JenisIkan() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNamaIkan() { return namaIkan; }
    public void setNamaIkan(String namaIkan) { this.namaIkan = namaIkan; }

    public String getNamaLatin() { return namaLatin; }
    public void setNamaLatin(String namaLatin) { this.namaLatin = namaLatin; }

    public BigDecimal getHargaPerKg() { return hargaPerKg; }
    public void setHargaPerKg(BigDecimal hargaPerKg) { this.hargaPerKg = hargaPerKg; }

    public BigDecimal getHargaPerEkor() { return hargaPerEkor; }
    public void setHargaPerEkor(BigDecimal hargaPerEkor) { this.hargaPerEkor = hargaPerEkor; }

    public String getSatuanHitung() { return satuanHitung; }
    public void setSatuanHitung(String satuanHitung) { this.satuanHitung = satuanHitung; }

    public Integer getBeratRataGram() { return beratRataGram; }
    public void setBeratRataGram(Integer beratRataGram) { this.beratRataGram = beratRataGram; }

    public BigDecimal getStokTotalKg() { return stokTotalKg; }
    public void setStokTotalKg(BigDecimal stokTotalKg) { this.stokTotalKg = stokTotalKg; }

    public BigDecimal getStokMinimumKg() { return stokMinimumKg; }
    public void setStokMinimumKg(BigDecimal stokMinimumKg) { this.stokMinimumKg = stokMinimumKg; }

    public String getDeskripsi() { return deskripsi; }
    public void setDeskripsi(String deskripsi) { this.deskripsi = deskripsi; }

    public boolean isAktif() { return aktif; }
    public void setAktif(boolean aktif) { this.aktif = aktif; }

    // Untuk ditampilkan di ComboBox
    @Override
    public String toString() {
        return namaIkan;
    }
}
