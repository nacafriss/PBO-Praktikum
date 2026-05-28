/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import dao.LapakDao;
import dao.TransaksiDao;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import model.KolamGalatama;
import model.KolamHarian;
import model.KolamKiloan;
import model.Lapak;
import model.LayananKolam;
import model.Transaksi;
import utils.FormatterUtil;
import utils.ValidatorUtil;

/**
 *
 * @author rei
 */
public class TransaksiController {
    
    private TransaksiDao transaksiDao;
    private LapakDao lapakDao;

    public TransaksiController() {
        this.transaksiDao = new TransaksiDao();
        this.lapakDao = new LapakDao();
    }

    /**
     * Memproses checkout pelanggan dari lapak dan menghitung total tagihan.
     * * @param transaksi Objek transaksi yang saat ini sedang aktif
     * @param lapak Objek lapak yang sedang disewa
     * @param inputDurasi String input durasi (relevan untuk harian)
     * @param inputBeratIkan String input berat timbangan (relevan untuk kiloan)
     * @return String pesan hasil checkout untuk ditampilkan di UI
     */
    public String prosesCheckout(Transaksi transaksi, Lapak lapak, String inputDurasi, String inputBeratIkan) {
        
        // 1. Proses Validasi Input dari UI
        if (!ValidatorUtil.isNumeric(inputDurasi)) {
            return "Error: Durasi harus berupa angka bulat!";
        }
        if (!ValidatorUtil.isDecimal(inputBeratIkan)) {
            return "Error: Berat ikan harus berupa angka (gunakan titik untuk desimal)!";
        }

        // Parsing String ke tipe data operasional
        int durasiAktual = Integer.parseInt(inputDurasi);
        BigDecimal beratIkan = new BigDecimal(inputBeratIkan);

        // 2. Terapkan Polymorphism untuk Kalkulasi Tagihan
        LayananKolam layanan = null;
        BigDecimal tarifDasar = BigDecimal.ZERO; 
        
        // Menentukan aturan bisnis berdasarkan jenis kolam di lapak tersebut
        switch (lapak.getJenisKolam()) {
            case "HARIAN":
                layanan = new KolamHarian();
                // Idealnya tarif ini ditarik dari TarifDao berdasarkan lapak.getTarifId()
                tarifDasar = new BigDecimal("15000"); // Contoh: Rp 15.000 per jam
                break;
            case "KILOAN":
                layanan = new KolamKiloan();
                tarifDasar = new BigDecimal("45000"); // Contoh: Harga ikan Rp 45.000 per kg
                break;
            case "GALATAMA":
                layanan = new KolamGalatama();
                tarifDasar = new BigDecimal("150000"); // Contoh: Harga tiket turnamen
                break;
            default:
                return "Error: Jenis kolam tidak terdaftar!";
        }

        // Method polymorphism dipanggil secara universal tanpa perlu if-else perhitungan lagi
        BigDecimal totalBiaya = layanan.hitungTagihan(durasiAktual, tarifDasar, beratIkan);

        // 3. Update State pada Objek Transaksi
        transaksi.setDurasiAktualMenit(durasiAktual);
        transaksi.setSubtotalIkan(lapak.getJenisKolam().equals("KILOAN") ? totalBiaya : BigDecimal.ZERO);
        transaksi.setBiayaSewa(lapak.getJenisKolam().equals("KILOAN") ? BigDecimal.ZERO : totalBiaya);
        transaksi.setTotalTagihan(totalBiaya);
        transaksi.setWaktuCheckout(LocalDateTime.now());
        transaksi.setStatusTransaksi("SELESAI");

        // 4. Proses Persistensi ke Database via DAO
        transaksiDao.update(transaksi);

        // Bebaskan lapak
        lapak.setStatus("KOSONG");
        lapakDao.update(lapak);

        // 5. Kembalikan output terformat untuk UI
        String tagihanRupiah = FormatterUtil.formatRupiah(totalBiaya);
        return "Checkout Berhasil!\nTotal Tagihan: " + tagihanRupiah;
    }
}
