/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view;

import controller.LapakController;
import model.Lapak;
import model.Pelanggan;
import utils.ValidatorUtil;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.List;

public class CheckinDialog extends JDialog {

    private LapakController lapakController;
    private Lapak lapak;
    private boolean checkinBerhasil = false;

    // Komponen form
    private JTextField txtNama;
    private JTextField txtNoHp;
    private JTextField txtEmail;
    private JTextField txtAlamat;
    private JTextField txtDurasi;
    private JComboBox<String> cmbMetodeBayar;
    private JComboBox<Integer> cmbPosisi;
    private JLabel lblInfoMember;
    private JLabel lblInfoLapak;
    private JLabel lblPosisiInfo;
    private JButton btnCheckin;
    private JButton btnBatal;

    private static final Color BIRU_TUA = new Color(26, 82, 118);
    private static final Color HIJAU    = new Color(39, 174, 96);
    private static final Color MERAH    = new Color(192, 57, 43);
    private static final Color BG_PANEL = new Color(245, 248, 250);

    public CheckinDialog(Frame parent, Lapak lapak, LapakController lapakController) {
        super(parent, "Check-in Pelanggan", true);
        this.lapak = lapak;
        this.lapakController = lapakController;

        initUI();
        muatPosisiTersedia();
        pack();
        setMinimumSize(new Dimension(460, 600));
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

        lblInfoLapak = new JLabel(lapak.getJenisKolam()
                + "  •  Kapasitas: " + lapak.getKapasitas());
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
        panel.setBorder(BorderFactory.createEmptyBorder(14, 20, 8, 20));

        panel.add(buatSectionPelanggan());
        panel.add(Box.createVerticalStrut(12));
        panel.add(buatSectionSesi());

        return panel;
    }

    // =========================================================
    // SECTION DATA PELANGGAN
    // =========================================================

    private JPanel buatSectionPelanggan() {
        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setBackground(Color.WHITE);
        section.setOpaque(true);
        section.setBorder(BorderFactory.createCompoundBorder(
                buatTitledBorder("Data Pelanggan"),
                BorderFactory.createEmptyBorder(10,10,10,10)
        ));

        // Nama
        txtNama = new JTextField();
        section.add(buatFieldRow("Nama Pelanggan *", txtNama));
        section.add(Box.createVerticalStrut(6));

        // No HP — auto lookup member saat fokus hilang
        txtNoHp = new JTextField();
        txtNoHp.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                cekMember();
            }
        });
        section.add(buatFieldRow("No. HP *", txtNoHp));
        section.add(Box.createVerticalStrut(4));

        // Info member
        lblInfoMember = new JLabel(" ");
        lblInfoMember.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblInfoMember.setForeground(HIJAU);
        lblInfoMember.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 0));
        section.add(lblInfoMember);
        section.add(Box.createVerticalStrut(6));

        // Email
        txtEmail = new JTextField();
        section.add(buatFieldRow("Email", txtEmail));
        section.add(Box.createVerticalStrut(6));

        // Alamat
        txtAlamat = new JTextField();
        section.add(buatFieldRow("Alamat", txtAlamat));

        return section;
    }

    // =========================================================
    // SECTION DETAIL SESI
    // =========================================================

    private JPanel buatSectionSesi() {
        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setBackground(Color.WHITE);
        section.setOpaque(true);
        section.setBorder(BorderFactory.createCompoundBorder(
                buatTitledBorder("Detail Sesi"),
                BorderFactory.createEmptyBorder(10,10,10,10)
        ));

        // Pilih posisi
        cmbPosisi = new JComboBox<>();
        cmbPosisi.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        section.add(buatFieldRow("Posisi *", cmbPosisi));
        section.add(Box.createVerticalStrut(4));

        // Info posisi tersedia
        lblPosisiInfo = new JLabel(" ");
        lblPosisiInfo.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblPosisiInfo.setForeground(new Color(100, 100, 100));
        lblPosisiInfo.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 0));
        section.add(lblPosisiInfo);
        section.add(Box.createVerticalStrut(6));

        // Durasi pesan
        txtDurasi = new JTextField("0");
        String labelDurasi = lapak.getJenisKolam().equals("HARIAN")
                ? "Durasi Pesan (menit) *" : "Durasi Pesan (menit, opsional)";
        section.add(buatFieldRow(labelDurasi, txtDurasi));
        section.add(Box.createVerticalStrut(6));

        // Metode bayar
        cmbMetodeBayar = new JComboBox<>(
                new String[]{"TUNAI", "TRANSFER", "QRIS", "MEMBER_POIN"});
        cmbMetodeBayar.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        section.add(buatFieldRow("Metode Bayar", cmbMetodeBayar));

        return section;
    }

    // =========================================================
    // TOMBOL PANEL
    // =========================================================

    private JPanel buatTombolPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 12));
        panel.setBackground(BG_PANEL);
        panel.setBorder(BorderFactory.createMatteBorder(
                1, 0, 0, 0, new Color(220, 220, 220)));

        btnBatal = new JButton("Batal");
        btnBatal.setBackground(new Color(236,240,241));
        btnBatal.setForeground(new Color(52,73,94));
        btnBatal.setBorder(BorderFactory.createLineBorder(
                new Color(189,195,199)
        ));
        btnBatal.setOpaque(true);
        btnBatal.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnBatal.setPreferredSize(new Dimension(90, 34));
        btnBatal.setFocusPainted(false);
        btnBatal.addActionListener(e -> dispose());

        btnCheckin = new JButton("Check-in");
        btnCheckin.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnCheckin.setBackground(new Color(34,197,94));
        btnCheckin.setForeground(Color.WHITE);
        btnCheckin.setOpaque(true);
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
    // HELPER UI
    // =========================================================

    private JPanel buatFieldRow(String labelTeks, JComponent field) {
        JPanel row = new JPanel(new BorderLayout(0, 4));
        row.setBackground(BG_PANEL);
        row.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        field.setPreferredSize(new Dimension(0, 36));

        JLabel label = new JLabel(labelTeks);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        label.setForeground(new Color(80, 80, 80));

        if (field instanceof JTextField txt) {
        txt.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txt.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210,210,210)),
                BorderFactory.createEmptyBorder(5,10,5,10)
        ));
        txt.setPreferredSize(new Dimension(0,36));
        }
        
        if (field instanceof JComboBox<?> combo) {
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        combo.setPreferredSize(new Dimension(0,36));
        }

        row.add(label, BorderLayout.NORTH);
        row.add(field, BorderLayout.CENTER);

        return row;
    }

    private TitledBorder buatTitledBorder(String judul) {
        TitledBorder border = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(220,220,220),1),
                judul
        );

        border.setTitleFont(new Font("Segoe UI", Font.BOLD, 13));
        border.setTitleColor(BIRU_TUA);

        return border;
    }

    // =========================================================
    // LOGIKA
    // =========================================================

    /**
     * Muat posisi yang masih tersedia ke ComboBox.
     */
    private void muatPosisiTersedia() {
        cmbPosisi.removeAllItems();

        List<Integer> terpakai = lapakController.getPosisiTerpakai(lapak.getId());
        int tersedia = 0;

        for (int i = 1; i <= lapak.getKapasitas(); i++) {
            if (!terpakai.contains(i)) {
                cmbPosisi.addItem(i);
                tersedia++;
            }
        }

        // Tampilkan info posisi
        if (tersedia == 0) {
            lblPosisiInfo.setText("Semua posisi penuh!");
            lblPosisiInfo.setForeground(MERAH);
            btnCheckin.setEnabled(false);
        } else {
            lblPosisiInfo.setText(tersedia + " posisi tersedia dari "
                    + lapak.getKapasitas());
            lblPosisiInfo.setForeground(new Color(100, 100, 100));
            btnCheckin.setEnabled(true);
        }
    }

    /**
     * Auto lookup member berdasarkan no HP.
     */
    private void cekMember() {
        String noHp = txtNoHp.getText().trim();
        if (!ValidatorUtil.isNomorHpValid(noHp)) {
            lblInfoMember.setText(" ");
            txtNama.setEditable(true);
            txtNama.setBackground(Color.WHITE);
            return;
        }

        Pelanggan pelanggan = lapakController.cariPelangganByNoHp(noHp);
        if (pelanggan != null) {
            // Auto-fill data pelanggan
            if (txtNama.getText().trim().isEmpty()) {
                txtNama.setText(pelanggan.getNama());
            }
            if (txtEmail.getText().trim().isEmpty() && pelanggan.getEmail() != null) {
                txtEmail.setText(pelanggan.getEmail());
            }
            if (txtAlamat.getText().trim().isEmpty() && pelanggan.getAlamat() != null) {
                txtAlamat.setText(pelanggan.getAlamat());
            }

            lblInfoMember.setText(pelanggan.getNama()
                    + "  [" + pelanggan.getTipeMember() + "]"
                    + "  Poin: " + pelanggan.getPoin()
                    + "  Kunjungan: " + pelanggan.getTotalKunjungan());
            lblInfoMember.setForeground(HIJAU);
        } else {
            lblInfoMember.setText("Pelanggan baru — akan didaftarkan otomatis.");
            lblInfoMember.setForeground(new Color(150, 150, 150));
        }
    }

    private void prosesCheckin() {
        String nama    = txtNama.getText().trim();
        String noHp    = txtNoHp.getText().trim();
        String durasi  = txtDurasi.getText().trim();
        String metode  = (String) cmbMetodeBayar.getSelectedItem();

        // Validasi nama
        if (!ValidatorUtil.isNotNullOrEmpty(nama)) {
            tampilkanError("Nama pelanggan tidak boleh kosong!");
            txtNama.requestFocus();
            return;
        }
        //validasi no hp wajib isi
        if (!ValidatorUtil.isNotNullOrEmpty(noHp)) {
            tampilkanError("Nomor HP wajib diisi!");
            txtNoHp.requestFocus();
            return;
        }
        // Validasi no HP jika diisi
        if (ValidatorUtil.isNotNullOrEmpty(noHp) && !ValidatorUtil.isNomorHpValid(noHp)) {
            tampilkanError("Format nomor HP tidak valid!");
            txtNoHp.requestFocus();
            return;
        }

        // Validasi durasi
        if (!ValidatorUtil.isNumeric(durasi)) {
            tampilkanError("Durasi harus berupa angka bulat!");
            txtDurasi.requestFocus();
            return;
        }

        // Validasi posisi
        if (cmbPosisi.getItemCount() == 0 || cmbPosisi.getSelectedItem() == null) {
            tampilkanError("Tidak ada posisi tersedia!");
            return;
        }

        int posisi = (Integer) cmbPosisi.getSelectedItem();

        // Kirim ke controller
        String hasil = lapakController.prosesCheckin(
                lapak, nama, noHp, durasi, metode, posisi, "admin");

        if (hasil.startsWith("Error")) {
            tampilkanError(hasil);
        } else {
            JOptionPane.showMessageDialog(this, hasil,
                    "Check-in Berhasil", JOptionPane.INFORMATION_MESSAGE);
            checkinBerhasil = true;
            dispose();
        }
    }

    private void tampilkanError(String pesan) {
        JOptionPane.showMessageDialog(this, pesan,
                "Validasi Gagal", JOptionPane.ERROR_MESSAGE);
    }

    // =========================================================
    // GETTER
    // =========================================================

    public boolean isCheckinBerhasil() {
        return checkinBerhasil;
    }
}
