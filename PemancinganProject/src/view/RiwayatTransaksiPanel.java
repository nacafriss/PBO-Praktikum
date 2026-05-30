/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import dao.TransaksiDao;
import model.Transaksi;


/**
 *
 * @author rei
 */
public class RiwayatTransaksiPanel extends JPanel {

    private JTable tabelRiwayat;
    private DefaultTableModel tableModel;

    public RiwayatTransaksiPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(245, 245, 245)); // Sesuaikan warna dengan tema kamu
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // 1. Bagian Judul (Atas)
        JLabel lblJudul = new JLabel("Riwayat Semua Transaksi");
        lblJudul.setFont(new Font("Segoe UI", Font.BOLD, 24));
        add(lblJudul, BorderLayout.NORTH);

        // 2. Setup Tabel dan Kolomnya (Tengah)
        String[] namaKolom = {
            "ID", "Pelanggan", "Kolam", "Check-In", "Check-Out", "Status", "Total Biaya"
        };
        
        // Buat model tabel agar datanya tidak bisa diedit dengan double-click
        tableModel = new DefaultTableModel(namaKolom, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; 
            }
        };
        
        tabelRiwayat = new JTable(tableModel);
        tabelRiwayat.setRowHeight(30);
        tabelRiwayat.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        // Masukkan tabel ke dalam ScrollPane agar bisa di-scroll jika datanya banyak
        JScrollPane scrollPane = new JScrollPane(tabelRiwayat);
        add(scrollPane, BorderLayout.CENTER);

        // 3. Tombol Refresh (Bawah)
        JButton btnRefresh = new JButton("Segarkan Data");
        btnRefresh.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnRefresh.addActionListener(e -> muatRiwayatTabel());
        
        JPanel panelBawah = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelBawah.setOpaque(false);
        panelBawah.add(btnRefresh);
        add(panelBawah, BorderLayout.SOUTH);

        // Load data pertama kali saat panel dibuka
        muatRiwayatTabel();
    }

    // Method untuk mengambil data dari Database dan memasukkan ke Tabel
    public void muatRiwayatTabel() {
        tableModel.setRowCount(0); // Bersihkan isi tabel lama
        
        TransaksiDao dao = new TransaksiDao();
        List<Transaksi> list = dao.getAll();

        for (Transaksi t : list) {
            Object[] baris = {
                t.getId(),
                t.getNamaPelanggan() != null ? t.getNamaPelanggan() : "Anonim",
                t.getJenisKolam(),
                t.getWaktuCheckin(),
                t.getWaktuCheckout() != null ? t.getWaktuCheckout() : "-",
                t.getStatusTransaksi(),
                t.getTotalTagihan() != null ? "Rp " + t.getTotalTagihan() : "-"
            };
            tableModel.addRow(baris);
        }
    }
}
