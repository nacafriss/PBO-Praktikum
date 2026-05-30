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
import model.DetailTangkapan;

public class DetailTangkapanDao {

    /**
     * Insert satu detail tangkapan ke database.
     */
    public void insert(DetailTangkapan detail) {
        String query = "INSERT INTO detail_tangkapan " +
                "(transaksi_id, nama_ikan, berat_kg, harga_per_kg, subtotal, catatan) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        Connection conn = DBConnection.Connect();
        if (conn == null) {
            System.err.println("insert DetailTangkapan gagal: koneksi null.");
            return;
        }

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, detail.getTransaksiId());
            stmt.setString(2, detail.getNamaIkan());
            stmt.setBigDecimal(3, detail.getBeratKg());
            stmt.setBigDecimal(4, detail.getHargaPerKg());
            stmt.setBigDecimal(5, detail.getSubtotal());
            stmt.setString(6, detail.getCatatan());

            stmt.executeUpdate();
            System.out.println("Detail tangkapan berhasil disimpan!");

        } catch (SQLException e) {
            System.err.println("insert DetailTangkapan gagal: " + e.getMessage());
        }
    }

    /**
     * Insert banyak detail tangkapan sekaligus (batch insert).
     * Lebih efisien daripada insert satu per satu.
     */
    public void insertBatch(List<DetailTangkapan> listDetail) {
        if (listDetail == null || listDetail.isEmpty()) return;

        String query = "INSERT INTO detail_tangkapan " +
                "(transaksi_id, nama_ikan, berat_kg, harga_per_kg, subtotal, catatan) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        Connection conn = DBConnection.Connect();
        if (conn == null) {
            System.err.println("insertBatch DetailTangkapan gagal: koneksi null.");
            return;
        }

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            for (DetailTangkapan detail : listDetail) {
                stmt.setInt(1, detail.getTransaksiId());
                stmt.setString(2, detail.getNamaIkan());
                stmt.setBigDecimal(3, detail.getBeratKg());
                stmt.setBigDecimal(4, detail.getHargaPerKg());
                stmt.setBigDecimal(5, detail.getSubtotal());
                stmt.setString(6, detail.getCatatan());
                stmt.addBatch();
            }

            int[] hasil = stmt.executeBatch();
            System.out.println("Batch insert detail tangkapan: " + hasil.length + " baris.");

        } catch (SQLException e) {
            System.err.println("insertBatch DetailTangkapan gagal: " + e.getMessage());
        }
    }

    /**
     * Ambil semua detail tangkapan berdasarkan transaksi ID.
     */
    public List<DetailTangkapan> getByTransaksiId(int transaksiId) {
        List<DetailTangkapan> list = new ArrayList<>();
        String query = "SELECT * FROM detail_tangkapan WHERE transaksi_id = ?";

        Connection conn = DBConnection.Connect();
        if (conn == null) {
            System.err.println("getByTransaksiId DetailTangkapan gagal: koneksi null.");
            return list;
        }

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, transaksiId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                list.add(mapResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("getByTransaksiId DetailTangkapan gagal: " + e.getMessage());
        }

        return list;
    }

    /**
     * Ambil semua detail tangkapan (untuk laporan).
     */
    public List<DetailTangkapan> getAll() {
        List<DetailTangkapan> list = new ArrayList<>();
        String query = "SELECT * FROM detail_tangkapan ORDER BY created_at DESC";

        Connection conn = DBConnection.Connect();
        if (conn == null) {
            System.err.println("getAll DetailTangkapan gagal: koneksi null.");
            return list;
        }

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                list.add(mapResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("getAll DetailTangkapan gagal: " + e.getMessage());
        }

        return list;
    }

    /**
     * Hapus semua detail tangkapan berdasarkan transaksi ID.
     * Dipakai jika transaksi dibatalkan.
     */
    public void deleteByTransaksiId(int transaksiId) {
        String query = "DELETE FROM detail_tangkapan WHERE transaksi_id = ?";

        Connection conn = DBConnection.Connect();
        if (conn == null) {
            System.err.println("deleteByTransaksiId DetailTangkapan gagal: koneksi null.");
            return;
        }

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, transaksiId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("deleteByTransaksiId DetailTangkapan gagal: " + e.getMessage());
        }
    }

    /**
     * Hitung total berat tangkapan per transaksi.
     */
    public java.math.BigDecimal getTotalBeratByTransaksiId(int transaksiId) {
        String query = "SELECT SUM(berat_kg) as total_berat " +
                "FROM detail_tangkapan WHERE transaksi_id = ?";

        Connection conn = DBConnection.Connect();
        if (conn == null) return java.math.BigDecimal.ZERO;

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, transaksiId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next() && rs.getBigDecimal("total_berat") != null) {
                return rs.getBigDecimal("total_berat");
            }
        } catch (SQLException e) {
            System.err.println("getTotalBerat gagal: " + e.getMessage());
        }

        return java.math.BigDecimal.ZERO;
    }

    /**
     * Mapping ResultSet ke objek DetailTangkapan.
     */
    private DetailTangkapan mapResultSet(ResultSet rs) throws SQLException {
        DetailTangkapan detail = new DetailTangkapan();
        detail.setId(rs.getInt("id"));
        detail.setTransaksiId(rs.getInt("transaksi_id"));
        detail.setNamaIkan(rs.getString("nama_ikan"));
        detail.setBeratKg(rs.getBigDecimal("berat_kg"));
        detail.setHargaPerKg(rs.getBigDecimal("harga_per_kg"));
        detail.setSubtotal(rs.getBigDecimal("subtotal"));
        detail.setCatatan(rs.getString("catatan"));

        if (rs.getTimestamp("created_at") != null) {
            detail.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        }

        return detail;
    }
}
