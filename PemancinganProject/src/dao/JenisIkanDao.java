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
import model.JenisIkan;

public class JenisIkanDao {

    /**
     * Ambil semua jenis ikan yang aktif.
     */
    public List<JenisIkan> getAllAktif() {
        List<JenisIkan> list = new ArrayList<>();
        String query = "SELECT * FROM jenis_ikan WHERE aktif = true ORDER BY nama_ikan ASC";

        try (Connection conn = DBConnection.Connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                list.add(mapResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("getAllAktif JenisIkan gagal: " + e.getMessage());
        }

        return list;
    }

    /**
     * Ambil jenis ikan berdasarkan ID.
     */
    public JenisIkan getById(int id) {
        String query = "SELECT * FROM jenis_ikan WHERE id = ?";

        try (Connection conn = DBConnection.Connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapResultSet(rs);
            }

        } catch (SQLException e) {
            System.err.println("getById JenisIkan gagal: " + e.getMessage());
        }

        return null;
    }

    private JenisIkan mapResultSet(ResultSet rs) throws SQLException {
        JenisIkan ikan = new JenisIkan();
        ikan.setId(rs.getInt("id"));
        ikan.setNamaIkan(rs.getString("nama_ikan"));
        ikan.setNamaLatin(rs.getString("nama_latin"));
        ikan.setHargaPerKg(rs.getBigDecimal("harga_per_kg"));
        ikan.setHargaPerEkor(rs.getBigDecimal("harga_per_ekor"));
        ikan.setSatuanHitung(rs.getString("satuan_hitung"));

        int berat = rs.getInt("berat_rata_gram");
        ikan.setBeratRataGram(rs.wasNull() ? null : berat);

        ikan.setStokTotalKg(rs.getBigDecimal("stok_total_kg"));
        ikan.setStokMinimumKg(rs.getBigDecimal("stok_minimum_kg"));
        ikan.setDeskripsi(rs.getString("deskripsi"));
        ikan.setAktif(rs.getBoolean("aktif"));
        return ikan;
    }
}