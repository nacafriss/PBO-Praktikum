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
import utils.FormatterUtil;

public class RiwayatTransaksiPanel extends JPanel {

    private JTable tabelRiwayat;
    private DefaultTableModel tableModel;
    private JLabel lblJumlah;

    public RiwayatTransaksiPanel() {

        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(248, 250, 252));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // =====================================================
        // HEADER
        // =====================================================

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel lblJudul = new JLabel("Riwayat Transaksi");
        lblJudul.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblJudul.setForeground(new Color(30, 41, 59));

        JLabel lblSub = new JLabel("Daftar seluruh transaksi yang pernah terjadi");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSub.setForeground(new Color(100, 116, 139));

        JPanel teksPanel = new JPanel();
        teksPanel.setLayout(new BoxLayout(teksPanel, BoxLayout.Y_AXIS));
        teksPanel.setOpaque(false);

        teksPanel.add(lblJudul);
        teksPanel.add(Box.createVerticalStrut(4));
        teksPanel.add(lblSub);

        lblJumlah = new JLabel("0 transaksi");
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
            "Pelanggan",
            "Kolam",
            "Check-In",
            "Check-Out",
            "Status",
            "Total Biaya"
        };

        tableModel = new DefaultTableModel(namaKolom, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabelRiwayat = new JTable(tableModel);

        tabelRiwayat.setRowHeight(36);
        tabelRiwayat.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        tabelRiwayat.setShowVerticalLines(false);
        tabelRiwayat.setGridColor(new Color(230, 230, 230));

        tabelRiwayat.setSelectionBackground(
                new Color(219, 234, 254));

        tabelRiwayat.setSelectionForeground(Color.BLACK);

        tabelRiwayat.getTableHeader().setFont(
                new Font("Segoe UI", Font.BOLD, 13));

        tabelRiwayat.getTableHeader().setBackground(
                new Color(241, 245, 249));

        tabelRiwayat.getTableHeader().setForeground(
                new Color(30, 41, 59));

        tabelRiwayat.getTableHeader().setReorderingAllowed(false);

        // Sembunyikan ID
        tabelRiwayat.getColumnModel().getColumn(0).setMinWidth(0);
        tabelRiwayat.getColumnModel().getColumn(0).setMaxWidth(0);
        tabelRiwayat.getColumnModel().getColumn(0).setPreferredWidth(0);

        JScrollPane scrollPane = new JScrollPane(tabelRiwayat);
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
                e -> muatRiwayatTabel());

        JPanel panelBawah = new JPanel(
                new FlowLayout(
                        FlowLayout.RIGHT));

        panelBawah.setOpaque(false);
        panelBawah.add(btnRefresh);

        add(panelBawah, BorderLayout.SOUTH);

        // =====================================================
        // LOAD DATA
        // =====================================================

        muatRiwayatTabel();
    }

    public void muatRiwayatTabel() {

        tableModel.setRowCount(0);

        TransaksiDao dao = new TransaksiDao();
        List<Transaksi> list = dao.getAll();

        for (Transaksi t : list) {

            Object[] baris = {
                t.getId(),
                t.getNamaPelanggan() != null
                        ? t.getNamaPelanggan()
                        : "Anonim",

                t.getJenisKolam(),

                t.getWaktuCheckin(),

                t.getWaktuCheckout() != null
                        ? t.getWaktuCheckout()
                        : "-",

                t.getStatusTransaksi(),

                t.getTotalTagihan() != null
                        ? FormatterUtil.formatRupiah(
                                t.getTotalTagihan())
                        : "-"
            };

            tableModel.addRow(baris);
        }

        lblJumlah.setText(
                list.size() + " transaksi");
    }
}