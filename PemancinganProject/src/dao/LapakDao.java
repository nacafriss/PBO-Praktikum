/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import database.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.Lapak;

/**
 *
 * @author rei
 */
public class LapakDao implements LapakDaoInterface {
    
    @Override
    public void insert(Lapak lapak) {
        String query = "INSERT INTO lapak " +
                       "(tarif_id, nama_lapak, jenis_kolam, deskripsi, status, kapasitas, aktif, posisi_x, posisi_y) " +
                       "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        // Try-with-resources (Connection dan PreparedStatement akan otomatis ditutup)
        try (Connection conn = DBConnection.Connect();
             PreparedStatement statement = conn.prepareStatement(query)) {

            statement.setInt(1, lapak.getTarifId());
            statement.setString(2, lapak.getNamaLapak()); 
            statement.setString(3, lapak.getJenisKolam()); 
            statement.setString(4, lapak.getDeskripsi());
            statement.setString(5, lapak.getStatus());
            statement.setInt(6, lapak.getKapasitas());
            statement.setBoolean(7, lapak.isAktif()); 
            statement.setInt(8, lapak.getPosisiX());
            statement.setInt(9, lapak.getPosisiY());

            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("Data lapak berhasil dimasukkan ke database!");
            }

        } catch (SQLException e) {
            System.err.println("Gagal memasukkan data lapak: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @Override
    public void update(Lapak lapak) {
        String query = "UPDATE lapak SET " +
                       "tarif_id=?, nama_lapak=?, jenis_kolam=?, deskripsi=?, status=?, " +
                       "kapasitas=?, aktif=?, posisi_x=?, posisi_y=? WHERE id=?";


        try (Connection conn = DBConnection.Connect();
             PreparedStatement statement = conn.prepareStatement(query)) {

            statement.setInt(1, lapak.getTarifId());
            statement.setString(2, lapak.getNamaLapak());
            statement.setString(3, lapak.getJenisKolam());
            statement.setString(4, lapak.getDeskripsi());
            statement.setString(5, lapak.getStatus());
            statement.setInt(6, lapak.getKapasitas());
            statement.setBoolean(7, lapak.isAktif());
            statement.setInt(8, lapak.getPosisiX());
            statement.setInt(9, lapak.getPosisiY());
            statement.setInt(10, lapak.getId());

            statement.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Update Failed: " + e.getMessage());
        }
    }

    @Override
    public void delete(int id) {
        String query = "DELETE FROM lapak WHERE id=?";


        try (Connection conn = DBConnection.Connect();
             PreparedStatement statement = conn.prepareStatement(query)) {

            statement.setInt(1, id);
            statement.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Delete Failed: " + e.getMessage());
        }
    }

    @Override
    public List<Lapak> getAll() {
        List<Lapak> listLapak = new ArrayList<>();
        String query = "SELECT * FROM lapak";


        try (Connection conn = DBConnection.Connect();
             Statement statement = conn.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                Lapak lapak = new Lapak();
                lapak.setId(resultSet.getInt("id"));
                lapak.setTarifId(resultSet.getInt("tarif_id"));
                lapak.setNamaLapak(resultSet.getString("nama_lapak"));
                lapak.setJenisKolam(resultSet.getString("jenis_kolam"));
                lapak.setDeskripsi(resultSet.getString("deskripsi"));
                lapak.setStatus(resultSet.getString("status"));
                lapak.setKapasitas(resultSet.getInt("kapasitas"));
                lapak.setAktif(resultSet.getBoolean("aktif"));
                lapak.setPosisiX(resultSet.getInt("posisi_x"));
                lapak.setPosisiY(resultSet.getInt("posisi_y"));
                
                if (resultSet.getTimestamp("created_at") != null) {
                    lapak.setCreatedAt(resultSet.getTimestamp("created_at").toLocalDateTime());
                }
                if (resultSet.getTimestamp("updated_at") != null) {
                    lapak.setUpdatedAt(resultSet.getTimestamp("updated_at").toLocalDateTime());
                }

                listLapak.add(lapak);
            }

        } catch (SQLException e) {
            System.err.println("Error get all Lapak: " + e.getMessage());
        }

        return listLapak;
    }
}
