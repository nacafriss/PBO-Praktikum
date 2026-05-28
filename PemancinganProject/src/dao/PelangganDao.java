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
import java.util.ArrayList;
import java.util.List;
import database.DBConnection;
import model.Pelanggan;

/**
 *
 * @author rei
 */
public class PelangganDao implements PelangganDaoInterface {

    @Override
    public void insert(Pelanggan pelanggan) {
        String query = "INSERT INTO pelanggan " +
                "(nama, no_hp, email, alamat, tipe_member, poin, total_kunjungan, total_belanja, aktif) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.Connect();
             PreparedStatement statement = conn.prepareStatement(query)) {

            statement.setString(1, pelanggan.getNama());
            statement.setString(2, pelanggan.getNoHp());
            statement.setString(3, pelanggan.getEmail());
            statement.setString(4, pelanggan.getAlamat());
            statement.setString(5, pelanggan.getTipeMember());
            statement.setInt(6, pelanggan.getPoin());
            statement.setInt(7, pelanggan.getTotalKunjungan());
            statement.setBigDecimal(8, pelanggan.getTotalBelanja());
            statement.setBoolean(9, pelanggan.isAktif());

            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("Data pelanggan berhasil ditambahkan!");
            }

        } catch (SQLException e) {
            System.err.println("Insert Pelanggan Failed: " + e.getMessage());
        }
    }

    @Override
    public void update(Pelanggan pelanggan) {
        String query = "UPDATE pelanggan SET " +
                "nama=?, no_hp=?, email=?, alamat=?, tipe_member=?, " +
                "poin=?, total_kunjungan=?, total_belanja=?, aktif=? " +
                "WHERE id=?";

        try (Connection conn = DBConnection.Connect();
             PreparedStatement statement = conn.prepareStatement(query)) {

            statement.setString(1, pelanggan.getNama());
            statement.setString(2, pelanggan.getNoHp());
            statement.setString(3, pelanggan.getEmail());
            statement.setString(4, pelanggan.getAlamat());
            statement.setString(5, pelanggan.getTipeMember());
            statement.setInt(6, pelanggan.getPoin());
            statement.setInt(7, pelanggan.getTotalKunjungan());
            statement.setBigDecimal(8, pelanggan.getTotalBelanja());
            statement.setBoolean(9, pelanggan.isAktif());
            
            // Parameter untuk WHERE id=?
            statement.setInt(10, pelanggan.getId());

            statement.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Update Pelanggan Failed: " + e.getMessage());
        }
    }

    @Override
    public void delete(int id) {
        String query = "DELETE FROM pelanggan WHERE id=?";

        try (Connection conn = DBConnection.Connect();
             PreparedStatement statement = conn.prepareStatement(query)) {

            statement.setInt(1, id);
            statement.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Delete Pelanggan Failed: " + e.getLocalizedMessage());
        }
    }

    @Override
    public List<Pelanggan> getAll() {
        List<Pelanggan> listPelanggan = new ArrayList<>();
        String query = "SELECT * FROM pelanggan";

        try (Connection conn = DBConnection.Connect();
             Statement statement = conn.createStatement();
             ResultSet rs = statement.executeQuery(query)) {

            while (rs.next()) {
                Pelanggan p = new Pelanggan();
                p.setId(rs.getInt("id"));
                p.setNama(rs.getString("nama"));
                p.setNoHp(rs.getString("no_hp"));
                p.setEmail(rs.getString("email"));
                p.setAlamat(rs.getString("alamat"));
                p.setTipeMember(rs.getString("tipe_member"));
                p.setPoin(rs.getInt("poin"));
                p.setTotalKunjungan(rs.getInt("total_kunjungan"));
                p.setTotalBelanja(rs.getBigDecimal("total_belanja"));
                p.setAktif(rs.getBoolean("aktif"));

                if (rs.getTimestamp("created_at") != null) {
                    p.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                }
                if (rs.getTimestamp("updated_at") != null) {
                    p.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
                }

                listPelanggan.add(p);
            }

        } catch (SQLException e) {
            System.err.println("Error get all Pelanggan: " + e.getLocalizedMessage());
        }

        return listPelanggan;
    }
}
