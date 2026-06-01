/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

/**
 *
 * @author user
 */
import dao.PelangganDao;
import model.Pelanggan;
import model.Tarif;
import utils.ValidatorUtil;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class PelangganController {

    private PelangganDao pelangganDao;

    public PelangganController() {
        this.pelangganDao = new PelangganDao();
    }

    /**
     * Cari pelanggan berdasarkan nomor HP.
     * Query langsung ke DB — tidak load semua data.
     */
    public Pelanggan cariByNoHp(String noHp) {
        if (!ValidatorUtil.isNotNullOrEmpty(noHp)) return null;
        return pelangganDao.getByNoHp(noHp.trim());
    }

    /**
     * Cari pelanggan berdasarkan ID.
     */
    public Pelanggan cariById(int id) {
        return pelangganDao.getById(id);
    }

    /**
     * Ambil semua pelanggan — untuk PelangganPanel.
     */
    public List<Pelanggan> semuaPelanggan() {
        return pelangganDao.getAll();
    }

    /**
     * Proses saat pelanggan check-in.
     * Jika HP terdaftar: increment kunjungan.
     * Jika HP baru: daftarkan otomatis.
     * Jika HP kosong: return null (tamu tanpa data).
     */
    public Integer prosesCheckinPelanggan(String namaPelanggan, String noHp) {
        if (!ValidatorUtil.isNotNullOrEmpty(noHp)) {
            return null;
        }

        Pelanggan pelanggan = pelangganDao.getByNoHp(noHp.trim());

        if (pelanggan != null) {
            pelanggan.setTotalKunjungan(pelanggan.getTotalKunjungan() + 1);
            pelangganDao.update(pelanggan);
            return pelanggan.getId();
        } else {
            return daftarkanBaru(namaPelanggan, noHp);
        }
    }

    /**
     * Daftarkan pelanggan baru dengan data minimal.
     */
    public Integer daftarkanBaru(String nama, String noHp) {
        Pelanggan baru = new Pelanggan();
        baru.setNama(nama.trim());
        baru.setNoHp(noHp.trim());
        baru.setEmail("");
        baru.setAlamat("");
        baru.setTipeMember("UMUM");
        baru.setPoin(0);
        baru.setTotalKunjungan(1);
        baru.setTotalBelanja(BigDecimal.ZERO);
        baru.setAktif(true);

        pelangganDao.insert(baru);

        // Ambil kembali dari DB untuk dapat ID yang di-generate
        Pelanggan tersimpan = pelangganDao.getByNoHp(noHp.trim());
        return tersimpan != null ? tersimpan.getId() : null;
    }

    /**
     * Tambah akumulasi total belanja setelah checkout.
     */
    public void tambahTotalBelanja(int pelangganId, BigDecimal jumlah) {
        Pelanggan pelanggan = pelangganDao.getById(pelangganId);
        if (pelanggan == null) return;

        BigDecimal totalLama = pelanggan.getTotalBelanja() != null
                ? pelanggan.getTotalBelanja() : BigDecimal.ZERO;
        pelanggan.setTotalBelanja(totalLama.add(jumlah));
        pelangganDao.update(pelanggan);
    }

    /**
     * Hitung diskon berdasarkan tipe member dan tarif.
     * Dipindahkan dari TransaksiController karena ini logika pelanggan.
     */
    public BigDecimal hitungDiskon(Integer pelangganId,
                                    BigDecimal totalBiaya,
                                    Tarif tarif) {
        if (pelangganId == null || tarif == null) return BigDecimal.ZERO;

        Pelanggan pelanggan = pelangganDao.getById(pelangganId);
        if (pelanggan == null) return BigDecimal.ZERO;

        BigDecimal persenDiskon = BigDecimal.ZERO;

        switch (pelanggan.getTipeMember()) {
            case "MEMBER":
                persenDiskon = tarif.getDiskonMember() != null
                        ? tarif.getDiskonMember() : BigDecimal.ZERO;
                break;
            case "VIP":
                persenDiskon = tarif.getDiskonVip() != null
                        ? tarif.getDiskonVip() : BigDecimal.ZERO;
                break;
            default:
                return BigDecimal.ZERO;
        }

        return totalBiaya.multiply(persenDiskon)
                .divide(new BigDecimal("100"), 0, RoundingMode.HALF_UP);
    }

    /**
     * Upgrade atau downgrade tipe member pelanggan.
     */
    public String ubahTipeMember(int pelangganId, String tipeBaru) {
        if (!tipeBaru.equals("UMUM")
                && !tipeBaru.equals("MEMBER")
                && !tipeBaru.equals("VIP")) {
            return "Error: Tipe member tidak valid!";
        }

        Pelanggan pelanggan = pelangganDao.getById(pelangganId);
        if (pelanggan == null) return "Error: Pelanggan tidak ditemukan!";

        pelanggan.setTipeMember(tipeBaru);
        pelangganDao.update(pelanggan);
        return pelanggan.getNama() + " berhasil diubah ke " + tipeBaru;
    }

    /**
     * Hapus pelanggan berdasarkan ID.
     */
    public String hapusPelanggan(int id) {
        Pelanggan pelanggan = pelangganDao.getById(id);
        if (pelanggan == null) return "Error: Pelanggan tidak ditemukan!";

        pelangganDao.delete(id);
        return "Pelanggan " + pelanggan.getNama() + " berhasil dihapus.";
    }
}
