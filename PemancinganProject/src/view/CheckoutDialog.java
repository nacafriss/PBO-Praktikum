/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view;

/**
 *
 * @author rei
 */
import controller.TransaksiController;
import model.Lapak;
import model.Transaksi;
import utils.FormatterUtil;
import utils.ValidatorUtil;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.time.Duration;
import java.time.LocalDateTime;

public class CheckoutDialog extends JDialog {

    private TransaksiController transaksiController;
    private Lapak lapak;
    private Transaksi transaksi;
    private boolean checkoutBerhasil = false;

    // Komponen form
    private JTextField txtDurasi;
    private JTextField txtBeratIkan;
    private JLabel lblNamaPelanggan;
    private JLabel lblJenisKolam;
    private JLabel lblWaktuCheckin;
    private JLabel lblDurasiOtomatis;
    private JLabel lblTotalTagihan;
    private JButton btnHitung;
    private JButton btnCheckout;
    private JButton btnBatal;

    private static final Color BIRU_TUA  = new Color(26, 82, 118);
    private static final Color HIJAU     = new Color(39, 174, 96);
    private static final Color MERAH     = new Color(192, 57, 43);
    private static final Color ORANYE    = new Color(230, 126, 34);
    private static final Color BG_PANEL  = new Color(245, 248, 250);

    public CheckoutDialog(Frame parent, Lapak lapak, Transaksi transaksi,
                          TransaksiController transaksiController) {
        super(parent, "Checkout Pelanggan", true);
        this.lapak = lapak;
        this.transaksi = transaksi;
        this.transaksiController = transaksiController;

        initUI();
        isiDataAwal();
        pack();
        setMinimumSize(new Dimension(440, 560));
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
        add(buatBodyPanel(), BorderLayout.CENTER);
        add(buatTombolPanel(), BorderLayout.SOUTH);
    }

    private JPanel buatHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(MERAH);
        panel.setBorder(BorderFactory.createEmptyBorder(14, 20, 14, 20));

        JLabel titleLabel = new JLabel("Checkout dari " + lapak.getNamaLapak());
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(Color.WHITE);

        JLabel subLabel = new JLabel(lapak.getJenisKolam() + "  •  " + transaksi.getNamaPelanggan());
        subLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subLabel.setForeground(new Color(245, 183, 177));

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(subLabel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel buatBodyPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(BG_PANEL);
        panel.setBorder(BorderFactory.createEmptyBorder(16, 20, 8, 20));

        panel.add(buatSectionInfo());
        panel.add(Box.createVerticalStrut(12));
        panel.add(buatSectionInput());
        panel.add(Box.createVerticalStrut(12));
        panel.add(buatSectionTagihan());

        return panel;
    }

    // =========================================================
    // SECTION INFO TRANSAKSI
    // =========================================================

    private JPanel buatSectionInfo() {
        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setBackground(BG_PANEL);

        TitledBorder border = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                "Informasi Sesi"
        );
        border.setTitleFont(new Font("Segoe UI", Font.BOLD, 12));
        section.setBorder(border);

        lblNamaPelanggan  = new JLabel();
        lblJenisKolam     = new JLabel();
        lblWaktuCheckin   = new JLabel();
        lblDurasiOtomatis = new JLabel();

        section.add(buatInfoRow("Pelanggan",   lblNamaPelanggan));
        section.add(buatInfoRow("Jenis Kolam", lblJenisKolam));
        section.add(buatInfoRow("Check-in",    lblWaktuCheckin));
        section.add(buatInfoRow("Durasi Saat Ini", lblDurasiOtomatis));

        return section;
    }

    // =========================================================
    // SECTION INPUT CHECKOUT
    // =========================================================

    private JPanel buatSectionInput() {
        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setBackground(BG_PANEL);

        TitledBorder border = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                "Input Checkout"
        );
        border.setTitleFont(new Font("Segoe UI", Font.BOLD, 12));
        section.setBorder(border);

        // Durasi aktual (untuk HARIAN)
        txtDurasi = new JTextField();
        String labelDurasi = "Durasi Aktual (menit)";
        if (lapak.getJenisKolam().equals("HARIAN")) {
            labelDurasi = "Durasi Aktual (menit) *";
        }
        section.add(buatFieldRow(labelDurasi, txtDurasi));
        section.add(Box.createVerticalStrut(8));

        // Berat ikan (untuk KILOAN)
        txtBeratIkan = new JTextField("0");
        String labelBerat = "Berat Ikan (kg)";
        if (lapak.getJenisKolam().equals("KILOAN")) {
            labelBerat = "Berat Ikan (kg) *";
        }
        section.add(buatFieldRow(labelBerat, txtBeratIkan));
        section.add(Box.createVerticalStrut(10));

        // Tombol Hitung Preview
        btnHitung = new JButton("🔢 Hitung Tagihan");
        btnHitung.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnHitung.setBackground(ORANYE);
        btnHitung.setForeground(Color.WHITE);
        btnHitung.setFocusPainted(false);
        btnHitung.setBorderPainted(false);
        btnHitung.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnHitung.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnHitung.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        btnHitung.addActionListener(e -> hitungPreview());
        section.add(btnHitung);

        // Atur visibilitas field sesuai jenis kolam
        aturVisibilitasField();

        return section;
    }

    // =========================================================
    // SECTION TAGIHAN
    // =========================================================

    private JPanel buatSectionTagihan() {
        JPanel section = new JPanel(new BorderLayout());
        section.setBackground(new Color(235, 245, 251));
        section.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BIRU_TUA, 1),
                BorderFactory.createEmptyBorder(12, 16, 12, 16)
        ));

        JLabel lblJudul = new JLabel("Total Tagihan");
        lblJudul.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblJudul.setForeground(BIRU_TUA);

        lblTotalTagihan = new JLabel("Rp -");
        lblTotalTagihan.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTotalTagihan.setForeground(MERAH);
        lblTotalTagihan.setHorizontalAlignment(SwingConstants.RIGHT);

        section.add(lblJudul, BorderLayout.WEST);
        section.add(lblTotalTagihan, BorderLayout.EAST);

        return section;
    }

    // =========================================================
    // TOMBOL PANEL
    // =========================================================

    private JPanel buatTombolPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 12));
        panel.setBackground(BG_PANEL);
        panel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0,
                new Color(220, 220, 220)));

        btnBatal = new JButton("Batal");
        btnBatal.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnBatal.setPreferredSize(new Dimension(90, 34));
        btnBatal.setFocusPainted(false);
        btnBatal.addActionListener(e -> dispose());

        btnCheckout = new JButton("✔ Checkout");
        btnCheckout.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnCheckout.setBackground(MERAH);
        btnCheckout.setForeground(Color.WHITE);
        btnCheckout.setFocusPainted(false);
        btnCheckout.setBorderPainted(false);
        btnCheckout.setPreferredSize(new Dimension(110, 34));
        btnCheckout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCheckout.setEnabled(false); // Aktif setelah hitung dulu
        btnCheckout.addActionListener(e -> prosesCheckout());

        panel.add(btnBatal);
        panel.add(btnCheckout);

        return panel;
    }

    // =========================================================
    // HELPER ROW
    // =========================================================

    private JPanel buatInfoRow(String labelTeks, JLabel valueLabel) {
        JPanel row = new JPanel(new BorderLayout());
        row.setBackground(BG_PANEL);
        row.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));

        JLabel label = new JLabel(labelTeks + " : ");
        label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        label.setForeground(new Color(100, 100, 100));
        label.setPreferredSize(new Dimension(120, 20));

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        valueLabel.setForeground(new Color(40, 40, 40));

        row.add(label, BorderLayout.WEST);
        row.add(valueLabel, BorderLayout.CENTER);

        return row;
    }

    private JPanel buatFieldRow(String labelTeks, JTextField field) {
        JPanel row = new JPanel(new BorderLayout(0, 4));
        row.setBackground(BG_PANEL);
        row.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));

        JLabel label = new JLabel(labelTeks);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        label.setForeground(new Color(80, 80, 80));

        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setPreferredSize(new Dimension(0, 30));

        row.add(label, BorderLayout.NORTH);
        row.add(field, BorderLayout.CENTER);

        return row;
    }

    // =========================================================
    // LOGIKA
    // =========================================================

    /**
     * Isi label info dari data transaksi yang sedang aktif.
     */
    private void isiDataAwal() {
        lblNamaPelanggan.setText(transaksi.getNamaPelanggan());
        lblJenisKolam.setText(lapak.getJenisKolam());
        lblWaktuCheckin.setText(FormatterUtil.formatTanggalWaktu(transaksi.getWaktuCheckin()));

        // Hitung durasi berjalan otomatis
        long menitBerjalan = Duration.between(
                transaksi.getWaktuCheckin(), LocalDateTime.now()).toMinutes();
        lblDurasiOtomatis.setText(menitBerjalan + " menit");

        // Auto-fill durasi aktual dengan durasi yang sudah berjalan
        txtDurasi.setText(String.valueOf(menitBerjalan));
    }

    /**
     * Sembunyikan/tampilkan field sesuai jenis kolam.
     */
    private void aturVisibilitasField() {
        switch (lapak.getJenisKolam()) {
            case "HARIAN":
                // Durasi wajib, berat tidak perlu
                txtBeratIkan.setEnabled(false);
                txtBeratIkan.setText("0");
                break;
            case "KILOAN":
                // Berat wajib, durasi tidak perlu
                txtDurasi.setEnabled(false);
                txtDurasi.setText("0");
                break;
            case "GALATAMA":
                // Keduanya tidak relevan
                txtDurasi.setEnabled(false);
                txtBeratIkan.setEnabled(false);
                txtDurasi.setText("0");
                txtBeratIkan.setText("0");
                break;
        }
    }

    /**
     * Preview hitung tagihan tanpa simpan ke DB.
     */
    private void hitungPreview() {
        String durasi = txtDurasi.getText().trim();
        String berat  = txtBeratIkan.getText().trim();

        if (!ValidatorUtil.isNumeric(durasi)) {
            tampilkanError("Durasi harus berupa angka bulat!");
            return;
        }
        if (!ValidatorUtil.isDecimal(berat)) {
            tampilkanError("Berat ikan harus berupa angka (gunakan titik untuk desimal)!");
            return;
        }

        // Gunakan controller untuk hitung, tapi belum simpan
        // Kita buat transaksi sementara untuk preview
        String hasil = transaksiController.prosesCheckout(transaksi, lapak, durasi, berat);

        if (hasil.startsWith("Error")) {
            tampilkanError(hasil);
            btnCheckout.setEnabled(false);
        } else {
            // Ambil total tagihan dari transaksi yang sudah diupdate controller
            lblTotalTagihan.setText(
                    FormatterUtil.formatRupiah(transaksi.getTotalTagihan()));
            btnCheckout.setEnabled(true);
        }
    }

    /**
     * Finalisasi checkout — karena prosesCheckout di controller
     * sudah dipanggil saat hitungPreview, di sini kita langsung
     * konfirmasi dan tutup dialog.
     */
    private void prosesCheckout() {
        if (transaksi.getTotalTagihan() == null) {
            tampilkanError("Harap hitung tagihan terlebih dahulu!");
            return;
        }

        int konfirmasi = JOptionPane.showConfirmDialog(
                this,
                "Total tagihan: " + FormatterUtil.formatRupiah(transaksi.getTotalTagihan())
                        + "\n\nKonfirmasi checkout?",
                "Konfirmasi",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (konfirmasi == JOptionPane.YES_OPTION) {
            checkoutBerhasil = true;
            JOptionPane.showMessageDialog(this,
                    "Checkout berhasil!\nTerima kasih, " + transaksi.getNamaPelanggan(),
                    "Berhasil", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        }
    }

    private void tampilkanError(String pesan) {
        JOptionPane.showMessageDialog(this, pesan, "Error", JOptionPane.ERROR_MESSAGE);
    }

    // =========================================================
    // GETTER
    // =========================================================

    public boolean isCheckoutBerhasil() {
        return checkoutBerhasil;
    }
}
