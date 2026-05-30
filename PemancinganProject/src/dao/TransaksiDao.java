/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import database.DBConnection;
import model.Transaksi;

public class TransaksiDao implements TransaksiDaoInterface {

    @Override
    public void insert(Transaksi transaksi) {
        String query = "INSERT INTO transaksi " +
                "(lapak_id, pelanggan_id, jenis_kolam, nama_pelanggan, no_hp_pelanggan, " +
                "posisi, waktu_checkin, waktu_checkout, durasi_aktual_menit, durasi_pesan_menit, " +
                "subtotal_ikan, biaya_sewa, diskon, poin_digunakan, total_tagihan, " +
                "metode_bayar, status_transaksi, catatan, created_by) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.Connect()) {
            if (conn == null) {
                System.err.println("insert Transaksi gagal: koneksi null.");
                return;
            }

            try (PreparedStatement stmt = conn.prepareStatement(query)) {

                stmt.setInt(1, transaksi.getLapakId());

                if (transaksi.getPelangganId() != null) stmt.setInt(2, transaksi.getPelangganId());
                else stmt.setNull(2, Types.INTEGER);

                stmt.setString(3, transaksi.getJenisKolam());
                stmt.setString(4, transaksi.getNamaPelanggan());
                stmt.setString(5, transaksi.getNoHpPelanggan());

                // Posisi baru
                if (transaksi.getPosisi() != null) stmt.setInt(6, transaksi.getPosisi());
                else stmt.setNull(6, Types.INTEGER);

                if (transaksi.getWaktuCheckin() != null)
                    stmt.setTimestamp(7, Timestamp.valueOf(transaksi.getWaktuCheckin()));
                else stmt.setNull(7, Types.TIMESTAMP);

                if (transaksi.getWaktuCheckout() != null)
                    stmt.setTimestamp(8, Timestamp.valueOf(transaksi.getWaktuCheckout()));
                else stmt.setNull(8, Types.TIMESTAMP);

                if (transaksi.getDurasiAktualMenit() != null) stmt.setInt(9, transaksi.getDurasiAktualMenit());
                else stmt.setNull(9, Types.INTEGER);

                if (transaksi.getDurasiPesanMenit() != null) stmt.setInt(10, transaksi.getDurasiPesanMenit());
                else stmt.setNull(10, Types.INTEGER);

                stmt.setBigDecimal(11, transaksi.getSubtotalIkan());
                stmt.setBigDecimal(12, transaksi.getBiayaSewa());
                stmt.setBigDecimal(13, transaksi.getDiskon());
                stmt.setInt(14, transaksi.getPoinDigunakan());
                stmt.setBigDecimal(15, transaksi.getTotalTagihan());
                stmt.setString(16, transaksi.getMetodeBayar());
                stmt.setString(17, transaksi.getStatusTransaksi());
                stmt.setString(18, transaksi.getCatatan());
                stmt.setString(19, transaksi.getCreatedBy());

                stmt.executeUpdate();
                System.out.println("Transaksi berhasil diinsert!");
            }

        } catch (SQLException e) {
            System.err.println("insert Transaksi gagal: " + e.getMessage());
        }
    }

    @Override
    public void update(Transaksi transaksi) {
        String query = "UPDATE transaksi SET " +
                "lapak_id=?, pelanggan_id=?, jenis_kolam=?, nama_pelanggan=?, no_hp_pelanggan=?, " +
                "posisi=?, waktu_checkin=?, waktu_checkout=?, durasi_aktual_menit=?, durasi_pesan_menit=?, " +
                "subtotal_ikan=?, biaya_sewa=?, diskon=?, poin_digunakan=?, total_tagihan=?, " +
                "metode_bayar=?, status_transaksi=?, catatan=?, created_by=? " +
                "WHERE id=?";

        try (Connection conn = DBConnection.Connect()) {
            if (conn == null) {
                System.err.println("update Transaksi gagal: koneksi null.");
                return;
            }

            try (PreparedStatement stmt = conn.prepareStatement(query)) {

                stmt.setInt(1, transaksi.getLapakId());

                if (transaksi.getPelangganId() != null) stmt.setInt(2, transaksi.getPelangganId());
                else stmt.setNull(2, Types.INTEGER);

                stmt.setString(3, transaksi.getJenisKolam());
                stmt.setString(4, transaksi.getNamaPelanggan());
                stmt.setString(5, transaksi.getNoHpPelanggan());

                if (transaksi.getPosisi() != null) stmt.setInt(6, transaksi.getPosisi());
                else stmt.setNull(6, Types.INTEGER);

                if (transaksi.getWaktuCheckin() != null)
                    stmt.setTimestamp(7, Timestamp.valueOf(transaksi.getWaktuCheckin()));
                else stmt.setNull(7, Types.TIMESTAMP);

                if (transaksi.getWaktuCheckout() != null)
                    stmt.setTimestamp(8, Timestamp.valueOf(transaksi.getWaktuCheckout()));
                else stmt.setNull(8, Types.TIMESTAMP);

                if (transaksi.getDurasiAktualMenit() != null) stmt.setInt(9, transaksi.getDurasiAktualMenit());
                else stmt.setNull(9, Types.INTEGER);

                if (transaksi.getDurasiPesanMenit() != null) stmt.setInt(10, transaksi.getDurasiPesanMenit());
                else stmt.setNull(10, Types.INTEGER);

                stmt.setBigDecimal(11, transaksi.getSubtotalIkan());
                stmt.setBigDecimal(12, transaksi.getBiayaSewa());
                stmt.setBigDecimal(13, transaksi.getDiskon());
                stmt.setInt(14, transaksi.getPoinDigunakan());
                stmt.setBigDecimal(15, transaksi.getTotalTagihan());
                stmt.setString(16, transaksi.getMetodeBayar());
                stmt.setString(17, transaksi.getStatusTransaksi());
                stmt.setString(18, transaksi.getCatatan());
                stmt.setString(19, transaksi.getCreatedBy());

                stmt.setInt(20, transaksi.getId());

                stmt.executeUpdate();
            }

        } catch (SQLException e) {
            System.err.println("update Transaksi gagal: " + e.getMessage());
        }
    }

    @Override
    public void delete(int id) {
        String query = "DELETE FROM transaksi WHERE id=?";

        try (Connection conn = DBConnection.Connect()) {
            if (conn == null) {
                System.err.println("delete Transaksi gagal: koneksi null.");
                return;
            }

            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, id);
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            System.err.println("delete Transaksi gagal: " + e.getMessage());
        }
    }

    @Override
    public List<Transaksi> getAll() {
        List<Transaksi> list = new ArrayList<>();
        String query = "SELECT * FROM transaksi ORDER BY waktu_checkin DESC";

        try (Connection conn = DBConnection.Connect()) {
            if (conn == null) {
                System.err.println("getAll Transaksi gagal: koneksi null.");
                return list;
            }

            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {

                while (rs.next()) {
                    list.add(mapResultSet(rs));
                }
            }

        } catch (SQLException e) {
            System.err.println("getAll Transaksi gagal: " + e.getMessage());
        }

        return list;
    }

    // =========================================================
    // METHOD BARU
    // =========================================================

    /**
     * Ambil semua transaksi AKTIF berdasarkan lapak ID.
     * Dipakai LapakPanel untuk tampilkan posisi yang terisi.
     */
    public List<Transaksi> getAktifByLapakId(int lapakId) {
        List<Transaksi> list = new ArrayList<>();
        String query = "SELECT * FROM transaksi " +
                "WHERE lapak_id = ? AND status_transaksi = 'AKTIF' " +
                "ORDER BY posisi ASC";

        try (Connection conn = DBConnection.Connect()) {
            if (conn == null) {
                System.err.println("getAktifByLapakId gagal: koneksi null.");
                return list;
            }

            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, lapakId);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        list.add(mapResultSet(rs));
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("getAktifByLapakId gagal: " + e.getMessage());
        }

        return list;
    }

    /**
     * Ambil semua transaksi AKTIF dari semua lapak.
     * Dipakai tab Pelanggan Aktif di DashboardView.
     */
    public List<Transaksi> getAllAktif() {
        List<Transaksi> list = new ArrayList<>();
        String query = "SELECT * FROM transaksi " +
                "WHERE status_transaksi = 'AKTIF' " +
                "ORDER BY waktu_checkin ASC";

        try (Connection conn = DBConnection.Connect()) {
            if (conn == null) {
                System.err.println("getAllAktif gagal: koneksi null.");
                return list;
            }

            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {

                while (rs.next()) {
                    list.add(mapResultSet(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("getAllAktif gagal: " + e.getMessage());
        }

        return list;
    }

    /**
     * Ambil transaksi AKTIF berdasarkan lapak ID dan posisi.
     * Dipakai saat checkout pelanggan tertentu.
     */
    public Transaksi getAktifByLapakIdDanPosisi(int lapakId, int posisi) {
        String query = "SELECT * FROM transaksi " +
                "WHERE lapak_id = ? AND posisi = ? AND status_transaksi = 'AKTIF' " +
                "LIMIT 1";

        try (Connection conn = DBConnection.Connect()) {
            if (conn == null) {
                System.err.println("getAktifByLapakIdDanPosisi gagal: koneksi null.");
                return null;
            }

            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, lapakId);
                stmt.setInt(2, posisi);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return mapResultSet(rs);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("getAktifByLapakIdDanPosisi gagal: " + e.getMessage());
        }

        return null;
    }

    /**
     * Ambil posisi yang sudah terpakai di lapak tertentu.
     * Dipakai CheckinDialog untuk disable posisi yang sudah terisi.
     */
    public List<Integer> getPosisiTerpakaiByLapakId(int lapakId) {
        List<Integer> listPosisi = new ArrayList<>();
        String query = "SELECT posisi FROM transaksi " +
                "WHERE lapak_id = ? AND status_transaksi = 'AKTIF'";

        try (Connection conn = DBConnection.Connect()) {
            if (conn == null) {
                System.err.println("getPosisiTerpakai gagal: koneksi null.");
                return listPosisi;
            }

            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, lapakId);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        listPosisi.add(rs.getInt("posisi"));
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("getPosisiTerpakai gagal: " + e.getMessage());
        }

        return listPosisi;
    }

    /**
     * Cek apakah lapak masih punya posisi kosong.
     * Dipakai LapakPanel untuk enable/disable tombol Check-in.
     */
    public boolean adaPosisiKosong(int lapakId, int kapasitas) {
        List<Integer> terpakai = getPosisiTerpakaiByLapakId(lapakId);
        return terpakai.size() < kapasitas;
    }

    // =========================================================
    // PRIVATE HELPER
    // =========================================================

    /**
     * Mapping ResultSet ke objek Transaksi.
     */
    private Transaksi mapResultSet(ResultSet rs) throws SQLException {
        Transaksi t = new Transaksi();
        t.setId(rs.getInt("id"));
        t.setLapakId(rs.getInt("lapak_id"));

        int pelangganId = rs.getInt("pelanggan_id");
        t.setPelangganId(rs.wasNull() ? null : pelangganId);

        t.setJenisKolam(rs.getString("jenis_kolam"));
        t.setNamaPelanggan(rs.getString("nama_pelanggan"));
        t.setNoHpPelanggan(rs.getString("no_hp_pelanggan"));

        // Posisi baru
        int posisi = rs.getInt("posisi");
        t.setPosisi(rs.wasNull() ? null : posisi);

        if (rs.getTimestamp("waktu_checkin") != null)
            t.setWaktuCheckin(rs.getTimestamp("waktu_checkin").toLocalDateTime());
        if (rs.getTimestamp("waktu_checkout") != null)
            t.setWaktuCheckout(rs.getTimestamp("waktu_checkout").toLocalDateTime());

        int aktualMenit = rs.getInt("durasi_aktual_menit");
        t.setDurasiAktualMenit(rs.wasNull() ? null : aktualMenit);

        int pesanMenit = rs.getInt("durasi_pesan_menit");
        t.setDurasiPesanMenit(rs.wasNull() ? null : pesanMenit);

        t.setSubtotalIkan(rs.getBigDecimal("subtotal_ikan"));
        t.setBiayaSewa(rs.getBigDecimal("biaya_sewa"));
        t.setDiskon(rs.getBigDecimal("diskon"));
        t.setPoinDigunakan(rs.getInt("poin_digunakan"));
        t.setTotalTagihan(rs.getBigDecimal("total_tagihan"));
        t.setMetodeBayar(rs.getString("metode_bayar"));
        t.setStatusTransaksi(rs.getString("status_transaksi"));
        t.setCatatan(rs.getString("catatan"));
        t.setCreatedBy(rs.getString("created_by"));

        return t;
    }
}