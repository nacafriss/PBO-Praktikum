/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import dao.DetailTangkapanDao;
import dao.LapakDao;
import dao.PelangganDao;
import dao.TarifDao;
import dao.TransaksiDao;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import model.DetailTangkapan;
import model.KolamGalatama;
import model.KolamHarian;
import model.KolamKiloan;
import model.Lapak;
import model.LayananKolam;
import model.Pelanggan;
import model.Tarif;
import model.Transaksi;
import utils.FormatterUtil;
import utils.ValidatorUtil;
import utils.StrukBuilder;

public class TransaksiController {

    private TransaksiDao transaksiDao;
    private LapakDao lapakDao;
    private TarifDao tarifDao;
    private PelangganController pelangganController;
    private DetailTangkapanDao detailTangkapanDao;

    public TransaksiController() {
        this.transaksiDao = new TransaksiDao();
        this.lapakDao = new LapakDao();
        this.tarifDao = new TarifDao();
        this.pelangganController = new PelangganController();
        this.detailTangkapanDao = new DetailTangkapanDao();
    }

// =========================================================
    // CHECKOUT UTAMA
    // =========================================================

    /**
     * Proses checkout satu pelanggan berdasarkan transaksi & lapak.
     * Untuk kolam KILOAN, detail tangkapan dikirim sebagai list.
     *
     * @param transaksi        Transaksi aktif pelanggan
     * @param lapak            Lapak tempat pelanggan
     * @param inputDurasi      Durasi aktual menit (relevan HARIAN)
     * @param listTangkapan    List detail tangkapan ikan (relevan KILOAN)
     * @param isSimpanKeDB     Jika false hanya untuk preview struk, jika true simpan ke database
     * @return Pesan hasil checkout
     */
    public String prosesCheckout(Transaksi transaksi, Lapak lapak,
                                  String inputDurasi,
                                  List<DetailTangkapan> listTangkapan,
                                  boolean isSimpanKeDB) { 
        // --- Validasi Input ---
        if (!ValidatorUtil.isNumeric(inputDurasi)) {
            return "Error: Durasi harus berupa angka bulat!";
        }

        int durasiAktual = Integer.parseInt(inputDurasi);

        // --- Ambil Tarif dari DB ---
        Tarif tarif = tarifDao.getById(lapak.getTarifId());
        if (tarif == null) {
            tarif = tarifDao.getByJenisKolam(lapak.getJenisKolam());
        }
        if (tarif == null) {
            return "Error: Tarif tidak ditemukan untuk lapak ini!";
        }

        // --- Hitung Tagihan Pakai Polymorphism ---
        BigDecimal totalBiaya   = BigDecimal.ZERO;
        BigDecimal subtotalIkan = BigDecimal.ZERO;
        BigDecimal biayaSewa    = BigDecimal.ZERO;
        BigDecimal tarifDasar   = BigDecimal.ZERO;

        LayananKolam layanan;

        switch (lapak.getJenisKolam()) {
            case "HARIAN":
                layanan    = new KolamHarian();
                tarifDasar = tarif.getHargaPerJam() != null
                        ? tarif.getHargaPerJam() : BigDecimal.ZERO;
                totalBiaya = layanan.hitungTagihan(durasiAktual, tarifDasar, BigDecimal.ZERO);
                biayaSewa  = totalBiaya;
                break;

            case "KILOAN":
                layanan = new KolamKiloan();
                // Hitung subtotal per tangkapan lalu jumlahkan
                if (listTangkapan != null && !listTangkapan.isEmpty()) {
                    for (DetailTangkapan detail : listTangkapan) {
                        detail.hitungSubtotal();
                        subtotalIkan = subtotalIkan.add(detail.getSubtotal());
                    }
                }
                // Biaya masuk (sewa kolam) + subtotal ikan
                BigDecimal biayaMasuk = tarif.getBiayaMasukKiloan() != null
                        ? tarif.getBiayaMasukKiloan() : BigDecimal.ZERO;
                biayaSewa  = biayaMasuk;
                totalBiaya = biayaMasuk.add(subtotalIkan);
                break;

            case "GALATAMA":
                layanan    = new KolamGalatama();
                tarifDasar = tarif.getHargaTiketGalatama() != null
                        ? tarif.getHargaTiketGalatama() : BigDecimal.ZERO;
                totalBiaya = layanan.hitungTagihan(durasiAktual, tarifDasar, BigDecimal.ZERO);
                biayaSewa  = totalBiaya;
                break;

            default:
                return "Error: Jenis kolam tidak terdaftar!";
        }

        // --- Hitung Diskon Member/VIP ---
        BigDecimal diskon = pelangganController.hitungDiskon( transaksi.getPelangganId(), totalBiaya, tarif);

        totalBiaya = totalBiaya.subtract(diskon);
        if (totalBiaya.compareTo(BigDecimal.ZERO) < 0) {
            totalBiaya = BigDecimal.ZERO;
        }

        // --- Update State Transaksi (Di Memori untuk Struk) ---
        transaksi.setDurasiAktualMenit(durasiAktual);
        transaksi.setSubtotalIkan(subtotalIkan);
        transaksi.setBiayaSewa(biayaSewa);
        transaksi.setDiskon(diskon);
        transaksi.setTotalTagihan(totalBiaya);

        // =========================================================
        // BAGIAN DATABASE DIBUNGKUS IF (Hanya Jalan Saat Tombol Checkout Asli Diklik)
        // =========================================================
        if (isSimpanKeDB) {
            transaksi.setWaktuCheckout(LocalDateTime.now());
            transaksi.setStatusTransaksi("SELESAI");

            // --- Simpan ke DB ---
            transaksiDao.update(transaksi);

            // Simpan detail tangkapan jika ada
            if (listTangkapan != null && !listTangkapan.isEmpty()) {
                for (DetailTangkapan detail : listTangkapan) {
                    detail.setTransaksiId(transaksi.getId());
                }
                detailTangkapanDao.insertBatch(listTangkapan);
            }

            // --- Update Total Belanja Pelanggan ---
            if (transaksi.getPelangganId() != null) {
                pelangganController.tambahTotalBelanja( transaksi.getPelangganId(), totalBiaya);
            }

            // --- Sinkron Status Lapak ---
            // Lapak jadi KOSONG hanya jika tidak ada transaksi aktif lain
            List<Transaksi> sisaAktif = transaksiDao.getAktifByLapakId(lapak.getId());
            if (sisaAktif.isEmpty()) {
                lapak.setStatus("KOSONG");
                lapakDao.update(lapak);
            }
        }
        // =========================================================

        // --- Return Struk ---
        return StrukBuilder.buildStrukCheckout(transaksi, lapak, listTangkapan,
        biayaSewa, subtotalIkan, diskon, totalBiaya);
    }

//
//    /**
//     * Overload lama — tanpa list tangkapan, pakai berat tunggal.
//     * Tetap dipertahankan agar tidak breaking change.
//     */
//    public String prosesCheckout(Transaksi transaksi, Lapak lapak,
//                                  String inputDurasi, String inputBeratIkan, 
//                                  boolean isSimpanKeDB) { 
//        // Validasi
//        if (!ValidatorUtil.isNumeric(inputDurasi)) {
//            return "Error: Durasi harus berupa angka bulat!";
//        }
//        if (!ValidatorUtil.isDecimal(inputBeratIkan)) {
//            return "Error: Berat ikan harus berupa angka!";
//        }
//
//        // Untuk KILOAN buat satu DetailTangkapan generik
//        List<DetailTangkapan> listTangkapan = null;
//        if (lapak.getJenisKolam().equals("KILOAN")) {
//            BigDecimal berat = new BigDecimal(inputBeratIkan);
//            if (berat.compareTo(BigDecimal.ZERO) > 0) {
//                Tarif tarif = tarifDao.getById(lapak.getTarifId());
//                if (tarif == null) tarif = tarifDao.getByJenisKolam("KILOAN");
//
//                DetailTangkapan detail = new DetailTangkapan();
//                detail.setNamaIkan("Ikan");
//                detail.setBeratKg(berat);
//                detail.setHargaPerKg(tarif != null && tarif.getBiayaMasukKiloan() != null
//                        ? tarif.getBiayaMasukKiloan() : new BigDecimal("45000"));
//                detail.setCatatan("");
//
//                listTangkapan = new java.util.ArrayList<>();
//                listTangkapan.add(detail);
//            }
//        }
//
//        return prosesCheckout(transaksi, lapak, inputDurasi, listTangkapan, isSimpanKeDB); 
//    }
//
//    // =========================================================
//    // QUERY
//    // =========================================================
//
//    /**
//     * Ambil semua transaksi aktif dari semua lapak.
//     */
//    public List<Transaksi> getSemuaTransaksiAktif() {
//        return transaksiDao.getAllAktif();
//    }
//
//    /**
//     * Ambil detail tangkapan berdasarkan transaksi ID.
//     */
//    public List<DetailTangkapan> getDetailTangkapan(int transaksiId) {
//        return detailTangkapanDao.getByTransaksiId(transaksiId);
//    }
//
//    // =========================================================
//    // PRIVATE HELPER
//    // =========================================================
//
//    /**
//     * Hitung diskon berdasarkan tipe member pelanggan.
//     */
//    private BigDecimal hitungDiskon(Transaksi transaksi, Tarif tarif, BigDecimal totalBiaya) {
//        if (transaksi.getPelangganId() == null) return BigDecimal.ZERO;
//
//         Pelanggan pelanggan = pelangganDao.getById(transaksi.getPelangganId());
//
//        if (pelanggan == null) return BigDecimal.ZERO;
//
//        BigDecimal persenDiskon = BigDecimal.ZERO;
//
//        switch (pelanggan.getTipeMember()) {
//            case "MEMBER":
//                persenDiskon = tarif.getDiskonMember() != null
//                        ? tarif.getDiskonMember() : BigDecimal.ZERO;
//                break;
//            case "VIP":
//                persenDiskon = tarif.getDiskonVip() != null
//                        ? tarif.getDiskonVip() : BigDecimal.ZERO;
//                break;
//            default:
//                return BigDecimal.ZERO;
//        }
//
//        // Diskon dalam persen, misal 10.00 = 10%
//        return totalBiaya.multiply(persenDiskon)
//                .divide(new BigDecimal("100"), 0, RoundingMode.HALF_UP);
//    }
//
//    /**
//     * Update total belanja pelanggan setelah checkout.
//     */
//    private void updateTotalBelanjaPelanggan(int pelangganId, BigDecimal tambahan) {
//        Pelanggan pelanggan = pelangganDao.getById(pelangganId); // Query langsung
//        if (pelanggan == null) return;
//
//        BigDecimal totalLama = pelanggan.getTotalBelanja() != null
//                ? pelanggan.getTotalBelanja() : BigDecimal.ZERO;
//        pelanggan.setTotalBelanja(totalLama.add(tambahan));
//        pelangganDao.update(pelanggan);
//    }

//    /**
//     * Build struk checkout yang tampil di dialog.
//     */
//    private String buildStruk(Transaksi transaksi, Lapak lapak,
//                               List<DetailTangkapan> listTangkapan,
//                               BigDecimal biayaSewa, BigDecimal subtotalIkan,
//                               BigDecimal diskon, BigDecimal totalBiaya) {
//
//        StringBuilder sb = new StringBuilder();
//        sb.append("===== STRUK CHECKOUT =====\n");
//        sb.append("Lapak    : ").append(lapak.getNamaLapak()).append("\n");
//        sb.append("Pelanggan: ").append(transaksi.getNamaPelanggan()).append("\n");
//        sb.append("Posisi   : ").append(transaksi.getPosisi()).append("\n");
//        sb.append("Check-in : ").append(
//                FormatterUtil.formatTanggalWaktu(transaksi.getWaktuCheckin())).append("\n");
//        sb.append("Check-out: ").append(
//                FormatterUtil.formatTanggalWaktu(transaksi.getWaktuCheckout())).append("\n");
//        sb.append("--------------------------\n");
//
//        // Detail tangkapan untuk KILOAN
//        if (listTangkapan != null && !listTangkapan.isEmpty()) {
//            sb.append("Detail Tangkapan:\n");
//            for (DetailTangkapan d : listTangkapan) {
//                sb.append("  ").append(d.getNamaIkan())
//                        .append(" ").append(d.getBeratKg()).append(" kg")
//                        .append(" x ").append(FormatterUtil.formatRupiah(d.getHargaPerKg()))
//                        .append(" = ").append(FormatterUtil.formatRupiah(d.getSubtotal()))
//                        .append("\n");
//            }
//            sb.append("--------------------------\n");
//        }
//
//        if (biayaSewa.compareTo(BigDecimal.ZERO) > 0) {
//            sb.append("Biaya Sewa  : ").append(FormatterUtil.formatRupiah(biayaSewa)).append("\n");
//        }
//        if (subtotalIkan.compareTo(BigDecimal.ZERO) > 0) {
//            sb.append("Subtotal Ikan: ").append(FormatterUtil.formatRupiah(subtotalIkan)).append("\n");
//        }
//        if (diskon.compareTo(BigDecimal.ZERO) > 0) {
//            sb.append("Diskon      : -").append(FormatterUtil.formatRupiah(diskon)).append("\n");
//        }
//
//        sb.append("==========================\n");
//        sb.append("TOTAL : ").append(FormatterUtil.formatRupiah(totalBiaya)).append("\n");
//        sb.append("==========================");
//
//        return sb.toString();
//    }
}