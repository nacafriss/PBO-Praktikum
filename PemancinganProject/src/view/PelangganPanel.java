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
import utils.FormatterUtil;

public class PelangganPanel extends JPanel {

    private JTable tabelPelanggan;
    private DefaultTableModel tableModel;
    private JLabel lblJumlah;

    public PelangganPanel() {

        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(248, 250, 252));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // =====================================================
        // HEADER
        // =====================================================

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel lblJudul = new JLabel("Data Pelanggan");
        lblJudul.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblJudul.setForeground(new Color(30, 41, 59));

        JLabel lblSub = new JLabel("Daftar seluruh pelanggan yang terdaftar");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSub.setForeground(new Color(100, 116, 139));

        JPanel teksPanel = new JPanel();
        teksPanel.setLayout(new BoxLayout(teksPanel, BoxLayout.Y_AXIS));
        teksPanel.setOpaque(false);

        teksPanel.add(lblJudul);
        teksPanel.add(Box.createVerticalStrut(4));
        teksPanel.add(lblSub);

        lblJumlah = new JLabel("0 pelanggan");
        lblJumlah.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblJumlah.setForeground(new Color(37, 99, 235));

        headerPanel.add(teksPanel, BorderLayout.WEST);
        headerPanel.add(lblJumlah, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        // =====================================================
        // TABEL
        // =====================================================

        String[] namaKolom = {
            "ID",
            "Nama Pelanggan",
            "Nomor HP",
            "Email",
            "Alamat",
            "Tipe Member",
            "Poin",
            "Total Kunjungan",
            "Total Belanja"
        };

        tableModel = new DefaultTableModel(namaKolom, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabelPelanggan = new JTable(tableModel);

        tabelPelanggan.setRowHeight(36);
        tabelPelanggan.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        tabelPelanggan.setShowVerticalLines(false);
        tabelPelanggan.setGridColor(new Color(230, 230, 230));

        tabelPelanggan.setSelectionBackground(
                new Color(219, 234, 254));

        tabelPelanggan.setSelectionForeground(Color.BLACK);

        tabelPelanggan.getTableHeader().setFont(
                new Font("Segoe UI", Font.BOLD, 13));

        tabelPelanggan.getTableHeader().setBackground(
                new Color(241, 245, 249));

        tabelPelanggan.getTableHeader().setForeground(
                new Color(30, 41, 59));

        tabelPelanggan.getTableHeader().setReorderingAllowed(false);

        // Sembunyikan ID
        tabelPelanggan.getColumnModel().getColumn(0).setMinWidth(0);
        tabelPelanggan.getColumnModel().getColumn(0).setMaxWidth(0);
        tabelPelanggan.getColumnModel().getColumn(0).setPreferredWidth(0);

        JScrollPane scrollPane = new JScrollPane(tabelPelanggan);
        scrollPane.setBorder(null);

        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);

        card.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                new Color(226, 232, 240)),
                        BorderFactory.createEmptyBorder(
                                10, 10, 10, 10)
                )
        );

        card.add(scrollPane, BorderLayout.CENTER);

        add(card, BorderLayout.CENTER);

        // =====================================================
        // FOOTER
        // =====================================================

        JButton btnRefresh = new JButton("↻ Refresh Data");

        btnRefresh.setFont(new Font("Segoe UI", Font.BOLD, 13));

        btnRefresh.setBackground(
                new Color(37, 99, 235));

        btnRefresh.setForeground(Color.WHITE);

        btnRefresh.setFocusPainted(false);
        btnRefresh.setBorderPainted(false);

        btnRefresh.setCursor(
                new Cursor(Cursor.HAND_CURSOR));

        btnRefresh.setPreferredSize(
                new Dimension(160, 40));

        btnRefresh.addActionListener(
                e -> muatDataPelanggan());

        JPanel panelBawah = new JPanel(
                new FlowLayout(
                        FlowLayout.RIGHT));

        panelBawah.setOpaque(false);
        panelBawah.add(btnRefresh);

        add(panelBawah, BorderLayout.SOUTH);

        // =====================================================
        // LOAD DATA
        // =====================================================

        muatDataPelanggan();
    }

    public void muatDataPelanggan() {

        tableModel.setRowCount(0);

        PelangganDao dao = new PelangganDao();
        List<Pelanggan> list = dao.getAll();

        for (Pelanggan p : list) {

            Object[] baris = {
                p.getId(),
                p.getNama(),
                p.getNoHp(),
                p.getEmail(),
                p.getAlamat(),
                p.getTipeMember(),
                p.getPoin(),
                p.getTotalKunjungan(),
                FormatterUtil.formatRupiah(
                        p.getTotalBelanja())
            };

            tableModel.addRow(baris);
        }

        lblJumlah.setText(
                list.size() + " pelanggan");
    }
}
