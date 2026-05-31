/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import dao.LapakDao;
import dao.PelangganDao;
import dao.TarifDao;
import dao.TransaksiDao;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import model.Lapak;
import model.Pelanggan;
import model.Tarif;
import model.Transaksi;
import utils.ValidatorUtil;

public class LapakController {

    private LapakDao lapakDao;
    private TransaksiDao transaksiDao;
    private PelangganDao pelangganDao;
    private TarifDao tarifDao;

    public LapakController() {
        this.lapakDao = new LapakDao();
        this.transaksiDao = new TransaksiDao();
        this.pelangganDao = new PelangganDao();
        this.tarifDao = new TarifDao();
    }

    // =========================================================
    // LAPAK
    // =========================================================

    /**
     * Ambil semua lapak.
     */
    public List<Lapak> semuaLapak() {
        return lapakDao.getAll();
    }

    /**
     * Tambah lapak baru.
     */
    public String tambahLapak(int tarifId, String namaLapak, String jenisKolam,
                               String deskripsi, int kapasitas, int posisiX, int posisiY) {
        if (!ValidatorUtil.isNotNullOrEmpty(namaLapak)) {
            return "Error: Nama lapak tidak boleh kosong!";
        }
        if (!jenisKolam.equals("HARIAN") && !jenisKolam.equals("KILOAN")
                && !jenisKolam.equals("GALATAMA")) {
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
     * Toggle aktif/nonaktif lapak.
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

    // =========================================================
    // CHECKIN
    // =========================================================

    /**
     * Proses check-in pelanggan ke posisi tertentu di lapak.
     * Data pelanggan otomatis disimpan/diupdate di tabel pelanggan.
     */
    public String prosesCheckin(Lapak lapak, String namaPelanggan, String noHp,
                                 String inputDurasi, String metodeBayar,
                                 int posisi, String createdBy) {

        // --- Validasi Lapak ---
        if (lapak == null) return "Error: Lapak tidak ditemukan!";
        if (!lapak.isAktif()) return "Error: Lapak sedang tidak aktif!";

        // Lapak boleh TERISI asal masih ada posisi kosong
        if (lapak.getStatus().equals("KOSONG") == false
                && lapak.getStatus().equals("TERISI") == false) {
            return "Error: Status lapak tidak valid!";
        }

        // --- Validasi Input ---
        if (!ValidatorUtil.isNotNullOrEmpty(namaPelanggan)) {
            return "Error: Nama pelanggan tidak boleh kosong!";
        }
        if (ValidatorUtil.isNotNullOrEmpty(noHp) && !ValidatorUtil.isNomorHpValid(noHp)) {
            return "Error: Format nomor HP tidak valid!";
        }
        if (!ValidatorUtil.isNumeric(inputDurasi)) {
            return "Error: Durasi harus berupa angka!";
        }

        // --- Validasi Posisi ---
        if (posisi < 1 || posisi > lapak.getKapasitas()) {
            return "Error: Posisi tidak valid! (1 - " + lapak.getKapasitas() + ")";
        }

        // Cek posisi sudah terpakai atau belum
        List<Integer> posisiTerpakai = transaksiDao.getPosisiTerpakaiByLapakId(lapak.getId());
        if (posisiTerpakai.contains(posisi)) {
            return "Error: Posisi " + posisi + " sudah terisi!";
        }

        // Cek kapasitas penuh
        if (posisiTerpakai.size() >= lapak.getKapasitas()) {
            return "Error: Lapak sudah penuh! Kapasitas: " + lapak.getKapasitas();
        }

        int durasiPesan = Integer.parseInt(inputDurasi);

        // --- Simpan/Update Data Pelanggan ---
        Integer pelangganId = null;
        if (ValidatorUtil.isNotNullOrEmpty(noHp)) {
            Pelanggan pelanggan = cariPelangganByNoHp(noHp);
            if (pelanggan != null) {
                // Pelanggan sudah ada — JANGAN update nama
                // Hanya update total kunjungan
                pelanggan.setTotalKunjungan(pelanggan.getTotalKunjungan() + 1);
                pelangganDao.update(pelanggan);
                pelangganId = pelanggan.getId();
            } else {
                // Pelanggan baru — daftarkan
                Pelanggan pelangganBaru = new Pelanggan();
                pelangganBaru.setNama(namaPelanggan.trim());
                pelangganBaru.setNoHp(noHp.trim());
                pelangganBaru.setEmail("");
                pelangganBaru.setAlamat("");
                pelangganBaru.setTipeMember("UMUM");
                pelangganBaru.setPoin(0);
                pelangganBaru.setTotalKunjungan(1);
                pelangganBaru.setTotalBelanja(java.math.BigDecimal.ZERO);
                pelangganBaru.setAktif(true);
                pelangganDao.insert(pelangganBaru);

                Pelanggan tersimpan = cariPelangganByNoHp(noHp.trim());
                if (tersimpan != null) pelangganId = tersimpan.getId();
            }
        }

        // --- Ambil Tarif dari DB ---
        Tarif tarif = tarifDao.getById(lapak.getTarifId());
        if (tarif == null) {
            // Fallback: ambil tarif by jenis kolam
            tarif = tarifDao.getByJenisKolam(lapak.getJenisKolam());
        }

        // --- Buat Transaksi Baru ---
        Transaksi transaksi = new Transaksi();
        transaksi.setLapakId(lapak.getId());
        transaksi.setPelangganId(pelangganId);
        transaksi.setJenisKolam(lapak.getJenisKolam());
        transaksi.setNamaPelanggan(namaPelanggan.trim());
        transaksi.setNoHpPelanggan(noHp != null ? noHp.trim() : "");
        transaksi.setPosisi(posisi);
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

        transaksiDao.insert(transaksi);

        // --- Update Status Lapak jadi TERISI ---
        if (lapak.getStatus().equals("KOSONG")) {
            lapak.setStatus("TERISI");
            lapakDao.update(lapak);
        }

        return "Check-in Berhasil!\nPelanggan : " + namaPelanggan
                + "\nLapak     : " + lapak.getNamaLapak()
                + "\nPosisi    : " + posisi;
    }

    // =========================================================
    // QUERY HELPER
    // =========================================================

    /**
     * Ambil transaksi aktif pertama di lapak (untuk timer).
     */
    public Transaksi getTransaksiAktifByLapak(int lapakId) {
        List<Transaksi> list = transaksiDao.getAktifByLapakId(lapakId);
        return list.isEmpty() ? null : list.get(0);
    }

    /**
     * Ambil semua transaksi aktif di lapak.
     */
    public List<Transaksi> getSemuaTransaksiAktifByLapak(int lapakId) {
        return transaksiDao.getAktifByLapakId(lapakId);
    }

    /**
     * Ambil semua transaksi aktif dari semua lapak.
     * Untuk tab Pelanggan Aktif.
     */
    public List<Transaksi> getSemuaTransaksiAktif() {
        return transaksiDao.getAllAktif();
    }

    /**
     * Ambil posisi yang sudah terpakai di lapak.
     */
    public List<Integer> getPosisiTerpakai(int lapakId) {
        return transaksiDao.getPosisiTerpakaiByLapakId(lapakId);
    }

    /**
     * Cek apakah lapak masih bisa menerima checkin.
     */
    public boolean bisaCheckin(Lapak lapak) {
        if (!lapak.isAktif()) return false;
        if (lapak.getStatus().equals("NONAKTIF")) return false;
        return transaksiDao.adaPosisiKosong(lapak.getId(), lapak.getKapasitas());
    }

    /**
     * Update status lapak — jadi KOSONG jika tidak ada transaksi aktif.
     */
    public void sinkronStatusLapak(Lapak lapak) {
        List<Transaksi> aktif = transaksiDao.getAktifByLapakId(lapak.getId());
        String statusBaru = aktif.isEmpty() ? "KOSONG" : "TERISI";
        if (!lapak.getStatus().equals(statusBaru)) {
            lapak.setStatus(statusBaru);
            lapakDao.update(lapak);
        }
    }

    /**
     * Cari pelanggan berdasarkan nomor HP.
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
}