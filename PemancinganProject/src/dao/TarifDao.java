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
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import model.Tarif;

public class TarifDao {

    /**
     * Ambil semua tarif dari database.
     */
    public List<Tarif> getAll() {
        List<Tarif> listTarif = new ArrayList<>();
        String query = "SELECT * FROM tarif";

        // Connection masuk ke try-with-resources pertama
        try (Connection conn = DBConnection.Connect()) {
            if (conn == null) {
                System.err.println("getAll Tarif gagal: koneksi null.");
                return listTarif;
            }

            // Statement dan ResultSet masuk ke try-with-resources kedua
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {

                while (rs.next()) {
                    listTarif.add(mapResultSet(rs));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error getAll Tarif: " + e.getMessage());
        }

        return listTarif;
    }

    /**
     * Ambil tarif berdasarkan ID.
     */
    public Tarif getById(int id) {
        String query = "SELECT * FROM tarif WHERE id = ?";

        try (Connection conn = DBConnection.Connect()) {
            if (conn == null) {
                System.err.println("getById Tarif gagal: koneksi null.");
                return null;
            }

            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, id);
                
                // ResultSet dibungkus try agar tertutup otomatis
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return mapResultSet(rs);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getById Tarif: " + e.getMessage());
        }

        return null;
    }

    /**
     * Ambil tarif berdasarkan jenis kolam (aktif saja).
     */
    public Tarif getByJenisKolam(String jenisKolam) {
        String query = "SELECT * FROM tarif WHERE jenis_kolam = ? AND aktif = true LIMIT 1";

        try (Connection conn = DBConnection.Connect()) {
            if (conn == null) {
                System.err.println("getByJenisKolam Tarif gagal: koneksi null.");
                return null;
            }

            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, jenisKolam);
                
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return mapResultSet(rs);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getByJenisKolam Tarif: " + e.getMessage());
        }

        return null;
    }

    /**
     * Insert tarif baru.
     */
    public void insert(Tarif tarif) {
        String query = "INSERT INTO tarif " +
                "(nama_tarif, jenis_kolam, harga_per_jam, biaya_masuk_kiloan, " +
                "harga_tiket_galatama, aktif) " + // Hapus diskon di sini
                "VALUES (?, ?, ?, ?, ?, ?)"; // Tanda tanya dikurangi jadi 6

        try (Connection conn = DBConnection.Connect()) {
            if (conn == null) return;
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, tarif.getNamaTarif());
                stmt.setString(2, tarif.getJenisKolam());
                stmt.setBigDecimal(3, tarif.getHargaPerJam());
                stmt.setBigDecimal(4, tarif.getBiayaMasukKiloan());
                stmt.setBigDecimal(5, tarif.getHargaTiketGalatama());
                stmt.setBoolean(6, tarif.isAktif()); // Ini jadi nomor 6

                stmt.executeUpdate();
                System.out.println("Tarif berhasil ditambahkan!");
            }
        } catch (SQLException e) {
            System.err.println("insert Tarif gagal: " + e.getMessage());
        }
    }

    /**
     * Update tarif.
     */
    public void update(Tarif tarif) {
        String query = "UPDATE tarif SET " +
                "nama_tarif=?, jenis_kolam=?, harga_per_jam=?, biaya_masuk_kiloan=?, " +
                "harga_tiket_galatama=?, aktif=? " + // Hapus diskon di sini
                "WHERE id=?"; // id jadi nomor 7

        try (Connection conn = DBConnection.Connect()) {
            if (conn == null) return;
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, tarif.getNamaTarif());
                stmt.setString(2, tarif.getJenisKolam());
                stmt.setBigDecimal(3, tarif.getHargaPerJam());
                stmt.setBigDecimal(4, tarif.getBiayaMasukKiloan());
                stmt.setBigDecimal(5, tarif.getHargaTiketGalatama());
                stmt.setBoolean(6, tarif.isAktif());
                stmt.setInt(7, tarif.getId()); // Ini jadi nomor 7

                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            System.err.println("update Tarif gagal: " + e.getMessage());
        }
    }

    /**
     * Mapping ResultSet ke objek Tarif.
     */
    private Tarif mapResultSet(ResultSet rs) throws SQLException {
        Tarif tarif = new Tarif();
        tarif.setId(rs.getInt("id"));
        tarif.setNamaTarif(rs.getString("nama_tarif"));
        tarif.setJenisKolam(rs.getString("jenis_kolam"));
        tarif.setHargaPerJam(rs.getBigDecimal("harga_per_jam"));
        tarif.setBiayaMasukKiloan(rs.getBigDecimal("biaya_masuk_kiloan"));
        tarif.setHargaTiketGalatama(rs.getBigDecimal("harga_tiket_galatama"));
      
        tarif.setAktif(rs.getBoolean("aktif"));

        if (rs.getTimestamp("created_at") != null) {
            tarif.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        }
//        if (rs.getTimestamp("updated_at") != null) {
//            tarif.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
//        }

        return tarif;
    }
}