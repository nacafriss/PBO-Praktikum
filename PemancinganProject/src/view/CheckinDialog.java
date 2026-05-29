/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view;

/**
 *
 * @author rei
 */
import controller.LapakController;
import model.Lapak;
import model.Pelanggan;
import utils.ValidatorUtil;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class CheckinDialog extends JDialog {

    private LapakController lapakController;
    private Lapak lapak;
    private boolean checkinBerhasil = false;

    // Komponen form
    private JTextField txtNama;
    private JTextField txtNoHp;
    private JTextField txtDurasi;
    private JComboBox<String> cmbMetodeBayar;
    private JLabel lblInfoMember;
    private JLabel lblInfoLapak;
    private JButton btnCheckin;
    private JButton btnBatal;

    private static final Color BIRU_TUA  = new Color(26, 82, 118);
    private static final Color HIJAU     = new Color(39, 174, 96);
    private static final Color MERAH     = new Color(192, 57, 43);
    private static final Color BG_PANEL  = new Color(245, 248, 250);

    public CheckinDialog(Frame parent, Lapak lapak, LapakController lapakController) {
        super(parent, "Check-in Pelanggan", true); // modal
        this.lapak = lapak;
        this.lapakController = lapakController;

        initUI();
        pack();
        setMinimumSize(new Dimension(420, 480));
        setLocationRelativeTo(parent);
        setResizable(false);
    }

    // =========================================================
    // INISIALISASI UI
    // =========================================================

    private void initUI() {
        setLayout(new BorderLayout());
        getContentPane().setBackground(BG_PANEL);

        add(buatHeaderPanel(), BorderLayout.NORTH);
        add(buatFormPanel(), BorderLayout.CENTER);
        add(buatTombolPanel(), BorderLayout.SOUTH);
    }

    private JPanel buatHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BIRU_TUA);
        panel.setBorder(BorderFactory.createEmptyBorder(14, 20, 14, 20));

        JLabel titleLabel = new JLabel("Check-in ke " + lapak.getNamaLapak());
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(Color.WHITE);

        lblInfoLapak = new JLabel(lapak.getJenisKolam() + "  •  Kapasitas: " + lapak.getKapasitas());
        lblInfoLapak.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblInfoLapak.setForeground(new Color(174, 214, 241));

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(lblInfoLapak, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel buatFormPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(BG_PANEL);
        panel.setBorder(BorderFactory.createEmptyBorder(16, 20, 8, 20));

        // --- Bagian Data Pelanggan ---
        TitledBorder borderPelanggan = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                "Data Pelanggan"
        );
        borderPelanggan.setTitleFont(new Font("Segoe UI", Font.BOLD, 12));

        JPanel sectionPelanggan = new JPanel();
        sectionPelanggan.setLayout(new BoxLayout(sectionPelanggan, BoxLayout.Y_AXIS));
        sectionPelanggan.setBackground(BG_PANEL);
        sectionPelanggan.setBorder(borderPelanggan);

        // Nama
        txtNama = new JTextField();
        sectionPelanggan.add(buatFieldRow("Nama Pelanggan *", txtNama));
        sectionPelanggan.add(Box.createVerticalStrut(8));

        // No HP + auto lookup member
        txtNoHp = new JTextField();
        txtNoHp.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                cekMember();
            }
        });
        sectionPelanggan.add(buatFieldRow("No. HP", txtNoHp));
        sectionPelanggan.add(Box.createVerticalStrut(6));

        // Info member (muncul jika HP terdaftar)
        lblInfoMember = new JLabel(" ");
        lblInfoMember.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblInfoMember.setForeground(HIJAU);
        lblInfoMember.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 0));
        sectionPelanggan.add(lblInfoMember);

        panel.add(sectionPelanggan);
        panel.add(Box.createVerticalStrut(12));

        // --- Bagian Detail Sesi ---
        TitledBorder borderSesi = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                "Detail Sesi"
        );
        borderSesi.setTitleFont(new Font("Segoe UI", Font.BOLD, 12));

        JPanel sectionSesi = new JPanel();
        sectionSesi.setLayout(new BoxLayout(sectionSesi, BoxLayout.Y_AXIS));
        sectionSesi.setBackground(BG_PANEL);
        sectionSesi.setBorder(borderSesi);

        // Durasi pesan
        txtDurasi = new JTextField("0");
        String labelDurasi = lapak.getJenisKolam().equals("HARIAN")
                ? "Durasi Pesan (menit)" : "Durasi Pesan (menit, opsional)";
        sectionSesi.add(buatFieldRow(labelDurasi, txtDurasi));
        sectionSesi.add(Box.createVerticalStrut(8));

        // Metode bayar
        cmbMetodeBayar = new JComboBox<>(new String[]{"TUNAI", "TRANSFER", "QRIS", "MEMBER_POIN"});
        cmbMetodeBayar.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        sectionSesi.add(buatFieldRow("Metode Bayar", cmbMetodeBayar));

        panel.add(sectionSesi);

        return panel;
    }

    private JPanel buatTombolPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 12));
        panel.setBackground(BG_PANEL);
        panel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 220, 220)));

        btnBatal = new JButton("Batal");
        btnBatal.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnBatal.setPreferredSize(new Dimension(90, 34));
        btnBatal.setFocusPainted(false);
        btnBatal.addActionListener(e -> dispose());

        btnCheckin = new JButton("✔ Check-in");
        btnCheckin.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnCheckin.setBackground(HIJAU);
        btnCheckin.setForeground(Color.WHITE);
        btnCheckin.setFocusPainted(false);
        btnCheckin.setBorderPainted(false);
        btnCheckin.setPreferredSize(new Dimension(110, 34));
        btnCheckin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCheckin.addActionListener(e -> prosesCheckin());

        panel.add(btnBatal);
        panel.add(btnCheckin);

        return panel;
    }

    // =========================================================
    // HELPER BUAT BARIS FORM
    // =========================================================

    private JPanel buatFieldRow(String labelTeks, JComponent field) {
        JPanel row = new JPanel(new BorderLayout(0, 4));
        row.setBackground(BG_PANEL);
        row.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));

        JLabel label = new JLabel(labelTeks);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        label.setForeground(new Color(80, 80, 80));

        if (field instanceof JTextField) {
            ((JTextField) field).setFont(new Font("Segoe UI", Font.PLAIN, 13));
            ((JTextField) field).setPreferredSize(new Dimension(0, 30));
        }

        row.add(label, BorderLayout.NORTH);
        row.add(field, BorderLayout.CENTER);

        return row;
    }

    // =========================================================
    // LOGIKA
    // =========================================================

    /**
     * Auto lookup member berdasarkan no HP saat field HP kehilangan fokus.
     */
    private void cekMember() {
        String noHp = txtNoHp.getText().trim();
        if (!ValidatorUtil.isNomorHpValid(noHp)) {
            lblInfoMember.setText(" ");
            return;
        }

        Pelanggan pelanggan = lapakController.cariPelangganByNoHp(noHp);
        if (pelanggan != null) {
            // Auto-fill nama jika field nama masih kosong
            if (txtNama.getText().trim().isEmpty()) {
                txtNama.setText(pelanggan.getNama());
            }
            lblInfoMember.setText("✔ Member ditemukan: " + pelanggan.getNama()
                    + " [" + pelanggan.getTipeMember() + "]"
                    + "  Poin: " + pelanggan.getPoin());
            lblInfoMember.setForeground(HIJAU);
        } else {
            lblInfoMember.setText("Bukan member terdaftar.");
            lblInfoMember.setForeground(new Color(150, 150, 150));
        }
    }

    private void prosesCheckin() {
        String nama      = txtNama.getText().trim();
        String noHp      = txtNoHp.getText().trim();
        String durasi    = txtDurasi.getText().trim();
        String metode    = (String) cmbMetodeBayar.getSelectedItem();

        // Validasi nama wajib diisi
        if (!ValidatorUtil.isNotNullOrEmpty(nama)) {
            tampilkanError("Nama pelanggan tidak boleh kosong!");
            txtNama.requestFocus();
            return;
        }

        // Validasi durasi
        if (!ValidatorUtil.isNumeric(durasi)) {
            tampilkanError("Durasi harus berupa angka bulat!");
            txtDurasi.requestFocus();
            return;
        }

        // Kirim ke controller
        String hasil = lapakController.prosesCheckin(lapak, nama, noHp, durasi, metode, "kasir");

        if (hasil.startsWith("Error")) {
            tampilkanError(hasil);
        } else {
            JOptionPane.showMessageDialog(this, hasil, "Berhasil", JOptionPane.INFORMATION_MESSAGE);
            checkinBerhasil = true;
            dispose();
        }
    }

    private void tampilkanError(String pesan) {
        JOptionPane.showMessageDialog(this, pesan, "Validasi Gagal", JOptionPane.ERROR_MESSAGE);
    }

    // =========================================================
    // GETTER
    // =========================================================

    public boolean isCheckinBerhasil() {
        return checkinBerhasil;
    }
}
