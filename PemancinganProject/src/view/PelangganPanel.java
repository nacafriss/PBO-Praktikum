/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import dao.PelangganDao;
import model.Pelanggan;

/**
 *
 * @author rei
 */
public class PelangganPanel extends JPanel {

    private JTable tabelPelanggan;
    private DefaultTableModel tableModel;

    public PelangganPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(245, 245, 245));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // 1. Judul Panel
        JLabel lblJudul = new JLabel("Manajemen Data Pelanggan");
        lblJudul.setFont(new Font("Segoe UI", Font.BOLD, 24));
        add(lblJudul, BorderLayout.NORTH);

        // 2. Setup Tabel
        // Sesuaikan nama kolom ini dengan data yang ingin kamu tampilkan
        String[] namaKolom = {"ID", "Nama Pelanggan", "Nomor HP", "Email", "Alamat", "Tipe Member", "Poin", "Total Kunjungan", "Total Belanja"}; 
        
        tableModel = new DefaultTableModel(namaKolom, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Agar tabel tidak bisa diedit langsung
            }
        };
        
        tabelPelanggan = new JTable(tableModel);
        tabelPelanggan.setRowHeight(30);
        tabelPelanggan.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        JScrollPane scrollPane = new JScrollPane(tabelPelanggan);
        add(scrollPane, BorderLayout.CENTER);

        // 3. Tombol Refresh
        JButton btnRefresh = new JButton("Segarkan Data");
        btnRefresh.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnRefresh.addActionListener(e -> muatDataPelanggan());
        
        JPanel panelBawah = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelBawah.setOpaque(false);
        panelBawah.add(btnRefresh);
        add(panelBawah, BorderLayout.SOUTH);

        // Load data saat panel pertama kali dibuka
        muatDataPelanggan();
    }

    // Method untuk menarik data dari MySQL dan memasukkannya ke JTable
    public void muatDataPelanggan() {
        tableModel.setRowCount(0); // Hapus data lama di tabel
        
        PelangganDao dao = new PelangganDao();
        List<Pelanggan> list = dao.getAll();

        for (Pelanggan p : list) {
            Object[] baris = {
                p.getId(),
                p.getNama(),  // Sesuaikan dengan getter di class Pelanggan
                p.getNoHp(),   // Sesuaikan dengan getter di class Pelanggan
                p.getEmail(),
                p.getAlamat(),
                p.getTipeMember(),
                p.getPoin(),
                p.getTotalKunjungan(),
                p.getTotalBelanja(),
            };
            tableModel.addRow(baris);
        }
    }
}
