/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

/**
 *
 * @author rei
 */
import dao.LapakDao;
import dao.PelangganDao;
import dao.TransaksiDao;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import model.Lapak;
import model.Pelanggan;
import model.Transaksi;
import utils.ValidatorUtil;

public class LapakController {

    private LapakDao lapakDao;
    private TransaksiDao transaksiDao;
    private PelangganDao pelangganDao;

    public LapakController() {
        this.lapakDao = new LapakDao();
        this.transaksiDao = new TransaksiDao();
        this.pelangganDao = new PelangganDao();
    }

    /**
     * Mengambil semua data lapak dari database.
     */
    public List<Lapak> semuaLapak() {
        return lapakDao.getAll();
    }

    /**
     * Memproses check-in pelanggan ke lapak tertentu.
     *
     * @param lapak          Lapak yang dipilih
     * @param namaPelanggan  Nama pelanggan (wajib)
     * @param noHp           Nomor HP pelanggan
     * @param inputDurasi    Durasi pesan dalam menit (opsional, bisa "0")
     * @param metodeBayar    Metode pembayaran
     * @param createdBy      Username kasir/operator
     * @return Pesan hasil operasi
     */
    public String prosesCheckin(Lapak lapak, String namaPelanggan, String noHp,
                                 String inputDurasi, String metodeBayar, String createdBy) {

        // Validasi lapak tersedia
        if (lapak == null) {
            return "Error: Lapak tidak ditemukan!";
        }
        if (!lapak.getStatus().equals("KOSONG")) {
            return "Error: Lapak sedang tidak tersedia!";
        }
        if (!lapak.isAktif()) {
            return "Error: Lapak sedang tidak aktif!";
        }

        // Validasi input nama
        if (!ValidatorUtil.isNotNullOrEmpty(namaPelanggan)) {
            return "Error: Nama pelanggan tidak boleh kosong!";
        }

        // Validasi no HP jika diisi
        if (ValidatorUtil.isNotNullOrEmpty(noHp) && !ValidatorUtil.isNomorHpValid(noHp)) {
            return "Error: Format nomor HP tidak valid!";
        }

        // Validasi durasi
        if (!ValidatorUtil.isNumeric(inputDurasi)) {
            return "Error: Durasi harus berupa angka!";
        }

        int durasiPesan = Integer.parseInt(inputDurasi);

        // Cek apakah pelanggan terdaftar (by no HP)
        Integer pelangganId = null;
        if (ValidatorUtil.isNotNullOrEmpty(noHp)) {
            Pelanggan pelanggan = cariPelangganByNoHp(noHp);
            if (pelanggan != null) {
                pelangganId = pelanggan.getId();
            }
        }

        // Buat objek transaksi baru
        Transaksi transaksi = new Transaksi();
        transaksi.setLapakId(lapak.getId());
        transaksi.setPelangganId(pelangganId);
        transaksi.setJenisKolam(lapak.getJenisKolam());
        transaksi.setNamaPelanggan(namaPelanggan.trim());
        transaksi.setNoHpPelanggan(noHp != null ? noHp.trim() : "");
        transaksi.setWaktuCheckin(LocalDateTime.now());
        transaksi.setWaktuCheckout(null);
        transaksi.setDurasiPesanMenit(durasiPesan > 0 ? durasiPesan : null);
        transaksi.setDurasiAktualMenit(null);
        transaksi.setSubtotalIkan(BigDecimal.ZERO);
        transaksi.setBiayaSewa(BigDecimal.ZERO);
        transaksi.setDiskon(BigDecimal.ZERO);
        transaksi.setPoinDigunakan(0);
        transaksi.setTotalTagihan(BigDecimal.ZERO);
        transaksi.setMetodeBayar(metodeBayar);
        transaksi.setStatusTransaksi("AKTIF");
        transaksi.setCatatan("");
        transaksi.setCreatedBy(createdBy);

        // Simpan transaksi ke DB
        transaksiDao.insert(transaksi);

        // Update status lapak menjadi TERISI
        lapak.setStatus("TERISI");
        lapakDao.update(lapak);

        return "Check-in Berhasil!\nPelanggan: " + namaPelanggan + "\nLapak: " + lapak.getNamaLapak();
    }

    /**
     * Mengambil transaksi aktif berdasarkan lapak ID.
     */
    public Transaksi getTransaksiAktifByLapak(int lapakId) {
        List<Transaksi> semuaTransaksi = transaksiDao.getAll();
        for (Transaksi t : semuaTransaksi) {
            if (t.getLapakId() == lapakId && t.getStatusTransaksi().equals("AKTIF")) {
                return t;
            }
        }
        return null;
    }

    /**
     * Mencari pelanggan berdasarkan nomor HP.
     */
    public Pelanggan cariPelangganByNoHp(String noHp) {
        List<Pelanggan> semuaPelanggan = pelangganDao.getAll();
        for (Pelanggan p : semuaPelanggan) {
            if (p.getNoHp() != null && p.getNoHp().equals(noHp)) {
                return p;
            }
        }
        return null;
    }

    /**
     * Menambah lapak baru.
     */
    public String tambahLapak(int tarifId, String namaLapak, String jenisKolam,
                               String deskripsi, int kapasitas, int posisiX, int posisiY) {

        if (!ValidatorUtil.isNotNullOrEmpty(namaLapak)) {
            return "Error: Nama lapak tidak boleh kosong!";
        }
        if (!jenisKolam.equals("HARIAN") && !jenisKolam.equals("KILOAN") && !jenisKolam.equals("GALATAMA")) {
            return "Error: Jenis kolam tidak valid!";
        }

        Lapak lapak = new Lapak();
        lapak.setTarifId(tarifId);
        lapak.setNamaLapak(namaLapak.trim());
        lapak.setJenisKolam(jenisKolam);
        lapak.setDeskripsi(deskripsi);
        lapak.setStatus("KOSONG");
        lapak.setKapasitas(kapasitas);
        lapak.setAktif(true);
        lapak.setPosisiX(posisiX);
        lapak.setPosisiY(posisiY);

        lapakDao.insert(lapak);
        return "Lapak " + namaLapak + " berhasil ditambahkan!";
    }

    /**
     * Toggle status aktif/nonaktif lapak.
     */
    public String toggleAktifLapak(Lapak lapak) {
        if (lapak.getStatus().equals("TERISI")) {
            return "Error: Tidak bisa menonaktifkan lapak yang sedang terisi!";
        }
        lapak.setAktif(!lapak.isAktif());
        lapakDao.update(lapak);
        String statusBaru = lapak.isAktif() ? "aktif" : "nonaktif";
        return "Lapak " + lapak.getNamaLapak() + " sekarang " + statusBaru + ".";
    }
}