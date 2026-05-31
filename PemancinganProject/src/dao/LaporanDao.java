/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

/**
 *
 * @author user
 */
import database.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

public class LaporanDao {

    /**
     * Ambil jumlah pengunjung per hari dalam range tanggal.
     * Key: tanggal (LocalDate), Value: jumlah pengunjung
     */
    public Map<LocalDate, Integer> getPengunjungPerHari(
            LocalDate tanggalMulai, LocalDate tanggalSelesai) {

        Map<LocalDate, Integer> result = new LinkedHashMap<>();

        // Pre-fill semua tanggal dengan 0 supaya grafik tidak bolong
        LocalDate cursor = tanggalMulai;
        while (!cursor.isAfter(tanggalSelesai)) {
            result.put(cursor, 0);
            cursor = cursor.plusDays(1);
        }

        String query = "SELECT DATE(waktu_checkin) as tgl, COUNT(*) as jumlah " +
                "FROM transaksi " +
                "WHERE DATE(waktu_checkin) BETWEEN ? AND ? " +
                "AND status_transaksi IN ('SELESAI', 'AKTIF') " +
                "GROUP BY DATE(waktu_checkin) " +
                "ORDER BY tgl ASC";

        Connection conn = DBConnection.Connect();
        if (conn == null) return result;

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setDate(1, java.sql.Date.valueOf(tanggalMulai));
            stmt.setDate(2, java.sql.Date.valueOf(tanggalSelesai));

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    LocalDate tgl = rs.getDate("tgl").toLocalDate();
                    int jumlah = rs.getInt("jumlah");
                    result.put(tgl, jumlah);
                }
            }
        } catch (SQLException e) {
            System.err.println("getPengunjungPerHari gagal: " + e.getMessage());
        }

        return result;
    }

    /**
     * Total pengunjung dalam range tanggal.
     */
    public int getTotalPengunjung(LocalDate mulai, LocalDate selesai) {
        String query = "SELECT COUNT(*) FROM transaksi " +
                "WHERE DATE(waktu_checkin) BETWEEN ? AND ? " +
                "AND status_transaksi IN ('SELESAI', 'AKTIF')";

        Connection conn = DBConnection.Connect();
        if (conn == null) return 0;

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setDate(1, java.sql.Date.valueOf(mulai));
            stmt.setDate(2, java.sql.Date.valueOf(selesai));
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("getTotalPengunjung gagal: " + e.getMessage());
        }
        return 0;
    }

    /**
     * Rata-rata pengunjung per hari.
     */
    public double getRataPengunjungPerHari(LocalDate mulai, LocalDate selesai) {
        long jumlahHari = mulai.until(selesai).getDays() + 1;
        if (jumlahHari == 0) return 0;
        return (double) getTotalPengunjung(mulai, selesai) / jumlahHari;
    }

    /**
     * Hari tersibuk dalam range tanggal.
     */
    public LocalDate getHariTersibuk(LocalDate mulai, LocalDate selesai) {
        String query = "SELECT DATE(waktu_checkin) as tgl, COUNT(*) as jumlah " +
                "FROM transaksi " +
                "WHERE DATE(waktu_checkin) BETWEEN ? AND ? " +
                "AND status_transaksi IN ('SELESAI', 'AKTIF') " +
                "GROUP BY DATE(waktu_checkin) " +
                "ORDER BY jumlah DESC LIMIT 1";

        Connection conn = DBConnection.Connect();
        if (conn == null) return null;

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setDate(1, java.sql.Date.valueOf(mulai));
            stmt.setDate(2, java.sql.Date.valueOf(selesai));
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDate("tgl").toLocalDate();
                }
            }
        } catch (SQLException e) {
            System.err.println("getHariTersibuk gagal: " + e.getMessage());
        }
        return null;
    }

    /**
     * Jumlah pengunjung di hari tersibuk.
     */
    public int getJumlahHariTersibuk(LocalDate mulai, LocalDate selesai) {
        String query = "SELECT COUNT(*) as jumlah " +
                "FROM transaksi " +
                "WHERE DATE(waktu_checkin) BETWEEN ? AND ? " +
                "AND status_transaksi IN ('SELESAI', 'AKTIF') " +
                "GROUP BY DATE(waktu_checkin) " +
                "ORDER BY jumlah DESC LIMIT 1";

        Connection conn = DBConnection.Connect();
        if (conn == null) return 0;

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setDate(1, java.sql.Date.valueOf(mulai));
            stmt.setDate(2, java.sql.Date.valueOf(selesai));
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt("jumlah");
            }
        } catch (SQLException e) {
            System.err.println("getJumlahHariTersibuk gagal: " + e.getMessage());
        }
        return 0;
    }
}