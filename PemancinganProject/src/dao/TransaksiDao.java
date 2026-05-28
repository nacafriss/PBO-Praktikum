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

/**
 *
 * @author rei
 */
public class TransaksiDao implements TransaksiDaoInterface {

    @Override
    public void insert(Transaksi transaksi) {
        String query = "INSERT INTO transaksi " +
                "(lapak_id, pelanggan_id, jenis_kolam, nama_pelanggan, no_hp_pelanggan, " +
                "waktu_checkin, waktu_checkout, durasi_aktual_menit, durasi_pesan_menit, " +
                "subtotal_ikan, biaya_sewa, diskon, poin_digunakan, total_tagihan, " +
                "metode_bayar, status_transaksi, catatan, created_by) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.Connect();
             PreparedStatement statement = conn.prepareStatement(query)) {

            statement.setInt(1, transaksi.getLapakId());

            // Handle Nullable Pelanggan ID
            if (transaksi.getPelangganId() != null) {
                statement.setInt(2, transaksi.getPelangganId());
            } else {
                statement.setNull(2, Types.INTEGER);
            }

            statement.setString(3, transaksi.getJenisKolam());
            statement.setString(4, transaksi.getNamaPelanggan());
            statement.setString(5, transaksi.getNoHpPelanggan());

            // Handle Waktu Checkin
            if (transaksi.getWaktuCheckin() != null) {
                statement.setTimestamp(6, Timestamp.valueOf(transaksi.getWaktuCheckin()));
            } else {
                statement.setNull(6, Types.TIMESTAMP);
            }

            // Handle Waktu Checkout (Bisa null saat baru check-in)
            if (transaksi.getWaktuCheckout() != null) {
                statement.setTimestamp(7, Timestamp.valueOf(transaksi.getWaktuCheckout()));
            } else {
                statement.setNull(7, Types.TIMESTAMP);
            }

            // Handle Nullable Integers
            if (transaksi.getDurasiAktualMenit() != null) statement.setInt(8, transaksi.getDurasiAktualMenit());
            else statement.setNull(8, Types.INTEGER);

            if (transaksi.getDurasiPesanMenit() != null) statement.setInt(9, transaksi.getDurasiPesanMenit());
            else statement.setNull(9, Types.INTEGER);

            // Handle BigDecimals
            statement.setBigDecimal(10, transaksi.getSubtotalIkan());
            statement.setBigDecimal(11, transaksi.getBiayaSewa());
            statement.setBigDecimal(12, transaksi.getDiskon());
            statement.setInt(13, transaksi.getPoinDigunakan());
            statement.setBigDecimal(14, transaksi.getTotalTagihan());

            statement.setString(15, transaksi.getMetodeBayar());
            statement.setString(16, transaksi.getStatusTransaksi());
            statement.setString(17, transaksi.getCatatan());
            statement.setString(18, transaksi.getCreatedBy());

            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("Data transaksi berhasil ditambahkan!");
            }

        } catch (SQLException e) {
            System.err.println("Insert Transaksi Failed: " + e.getMessage());
        }
    }

    @Override
    public void update(Transaksi transaksi) {
        String query = "UPDATE transaksi SET " +
                "lapak_id=?, pelanggan_id=?, jenis_kolam=?, nama_pelanggan=?, no_hp_pelanggan=?, " +
                "waktu_checkin=?, waktu_checkout=?, durasi_aktual_menit=?, durasi_pesan_menit=?, " +
                "subtotal_ikan=?, biaya_sewa=?, diskon=?, poin_digunakan=?, total_tagihan=?, " +
                "metode_bayar=?, status_transaksi=?, catatan=?, created_by=? " +
                "WHERE id=?";

        try (Connection conn = DBConnection.Connect();
             PreparedStatement statement = conn.prepareStatement(query)) {

            statement.setInt(1, transaksi.getLapakId());

            if (transaksi.getPelangganId() != null) statement.setInt(2, transaksi.getPelangganId());
            else statement.setNull(2, Types.INTEGER);

            statement.setString(3, transaksi.getJenisKolam());
            statement.setString(4, transaksi.getNamaPelanggan());
            statement.setString(5, transaksi.getNoHpPelanggan());

            if (transaksi.getWaktuCheckin() != null) statement.setTimestamp(6, Timestamp.valueOf(transaksi.getWaktuCheckin()));
            else statement.setNull(6, Types.TIMESTAMP);

            if (transaksi.getWaktuCheckout() != null) statement.setTimestamp(7, Timestamp.valueOf(transaksi.getWaktuCheckout()));
            else statement.setNull(7, Types.TIMESTAMP);

            if (transaksi.getDurasiAktualMenit() != null) statement.setInt(8, transaksi.getDurasiAktualMenit());
            else statement.setNull(8, Types.INTEGER);

            if (transaksi.getDurasiPesanMenit() != null) statement.setInt(9, transaksi.getDurasiPesanMenit());
            else statement.setNull(9, Types.INTEGER);

            statement.setBigDecimal(10, transaksi.getSubtotalIkan());
            statement.setBigDecimal(11, transaksi.getBiayaSewa());
            statement.setBigDecimal(12, transaksi.getDiskon());
            statement.setInt(13, transaksi.getPoinDigunakan());
            statement.setBigDecimal(14, transaksi.getTotalTagihan());
            statement.setString(15, transaksi.getMetodeBayar());
            statement.setString(16, transaksi.getStatusTransaksi());
            statement.setString(17, transaksi.getCatatan());
            statement.setString(18, transaksi.getCreatedBy());

            // Parameter terakhir untuk WHERE id=?
            statement.setInt(19, transaksi.getId());

            statement.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Update Transaksi Failed: " + e.getMessage());
        }
    }

    @Override
    public void delete(int id) {
        String query = "DELETE FROM transaksi WHERE id=?";
        try (Connection conn = DBConnection.Connect();
             PreparedStatement statement = conn.prepareStatement(query)) {

            statement.setInt(1, id);
            statement.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Delete Transaksi Failed: " + e.getLocalizedMessage());
        }
    }

    @Override
    public List<Transaksi> getAll() {
        List<Transaksi> listTransaksi = new ArrayList<>();
        String query = "SELECT * FROM transaksi";

        try (Connection conn = DBConnection.Connect();
             Statement statement = conn.createStatement();
             ResultSet rs = statement.executeQuery(query)) {

            while (rs.next()) {
                Transaksi t = new Transaksi();
                t.setId(rs.getInt("id"));
                t.setLapakId(rs.getInt("lapak_id"));
                
                // Menangani nilai Integer null
                int pelangganId = rs.getInt("pelanggan_id");
                t.setPelangganId(rs.wasNull() ? null : pelangganId);

                t.setJenisKolam(rs.getString("jenis_kolam"));
                t.setNamaPelanggan(rs.getString("nama_pelanggan"));
                t.setNoHpPelanggan(rs.getString("no_hp_pelanggan"));

                if (rs.getTimestamp("waktu_checkin") != null) {
                    t.setWaktuCheckin(rs.getTimestamp("waktu_checkin").toLocalDateTime());
                }
                if (rs.getTimestamp("waktu_checkout") != null) {
                    t.setWaktuCheckout(rs.getTimestamp("waktu_checkout").toLocalDateTime());
                }

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

                listTransaksi.add(t);
            }
        } catch (SQLException e) {
            System.err.println("Error get all Transaksi: " + e.getLocalizedMessage());
        }

        return listTransaksi;
    }
}
