/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view;

import controller.LapakController;
import controller.TransaksiController;
import model.DetailTangkapan;
import model.Lapak;
import model.Transaksi;
import dao.TransaksiDao;
import utils.FormatterUtil;
import utils.ValidatorUtil;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CheckoutDialog extends JDialog {

    private TransaksiController transaksiController;
    private Transaksi transaksi;
    private LapakController lapakController;
    private Lapak lapak;
    private List<Transaksi> aktifList;
    private Transaksi transaksiDipilih;
    private boolean checkoutBerhasil = false;
//    private void batalkanTransaksi() { //PENTING WAKKK
//    // Ambil transaksi urutan pertama (index 0) dari list
//        if (aktifList != null && !aktifList.isEmpty()) {
//            model.Transaksi transaksiYgDibatalkan = aktifList.get(1); // <-- INI KUNCI NYA
//
//            System.out.println("DEBUG: Membatalkan transaksi ID: " + transaksiYgDibatalkan.getId());
//
//            transaksiYgDibatalkan.setStatusTransaksi("DIBATALKAN");
//            transaksiYgDibatalkan.setWaktuCheckout(java.time.LocalDateTime.now());
//
//            dao.TransaksiDao dao = new dao.TransaksiDao();
//            dao.update(transaksiYgDibatalkan);
//
//            System.out.println("DEBUG: Update database SUKSES.");
//            dispose(); // Tutup form
//
//        } else {
//            System.err.println("DEBUG ERROR: aktifList kosong!");
//            dispose();
//        }
//    }
    

    // Komponen pemilih pelanggan
    private JList<String> listPelanggan;
    private DefaultListModel<String> modelListPelanggan;

    // Komponen info sesi
    private JLabel lblNama;
    private JLabel lblLapak;
    private JLabel lblPosisi;
    private JLabel lblCheckin;
    private JLabel lblDurasi;
    private JLabel lblJenis;

    // Komponen input checkout
    private JTextField txtDurasi;
    private JPanel panelKiloan;
    private JPanel panelInputDurasi;

    // Tabel detail tangkapan (untuk KILOAN)
    private JTable tabelTangkapan;
    private DefaultTableModel modelTabelTangkapan;
    private JTextField txtNamaIkan;
    private JTextField txtBeratIkan;
    private JTextField txtHargaPerKg;

    // Struk & total
    private JTextArea txtStruk;
    private JLabel lblTotalTagihan;
    private JButton btnHitung;
    private JButton btnCheckout;
    private JButton btnBatal;

    private static final Color BIRU_TUA = new Color(26, 82, 118);
    private static final Color HIJAU    = new Color(39, 174, 96);
    private static final Color MERAH    = new Color(192, 57, 43);
    private static final Color ORANYE   = new Color(230, 126, 34);
    private static final Color BG_PANEL = new Color(245, 248, 250);

    public CheckoutDialog(Frame parent, Lapak lapak, List<Transaksi> aktifList,
                          TransaksiController transaksiController,
                          LapakController lapakController) {
        super(parent, "Checkout Pelanggan — " + lapak.getNamaLapak(), true);
        this.lapak               = lapak;
        this.aktifList           = aktifList;
        this.transaksiController = transaksiController;
        this.lapakController     = lapakController;

        initUI();
        muatListPelanggan();
        pack();
        setMinimumSize(new Dimension(680, 620));
        setLocationRelativeTo(parent);
        setResizable(true);
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

        JLabel title = new JLabel("Checkout — " + lapak.getNamaLapak());
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setForeground(Color.WHITE);

        JLabel sub = new JLabel(lapak.getJenisKolam()
                + "  •  " + aktifList.size() + " pelanggan aktif");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        sub.setForeground(new Color(245, 183, 177));

        panel.add(title, BorderLayout.NORTH);
        panel.add(sub, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel buatBodyPanel() {
        JPanel panel = new JPanel(new BorderLayout(12, 0));
        panel.setBackground(BG_PANEL);
        panel.setBorder(BorderFactory.createEmptyBorder(14, 16, 8, 16));

        // Kiri: pilih pelanggan
        panel.add(buatPanelPilihPelanggan(), BorderLayout.WEST);

        // Kanan: detail + input + struk
        panel.add(buatPanelDetail(), BorderLayout.CENTER);

        return panel;
    }

    // =========================================================
    // PANEL KIRI — PILIH PELANGGAN
    // =========================================================

    private JPanel buatPanelPilihPelanggan() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_PANEL);
        panel.setPreferredSize(new Dimension(200, 0));
        panel.setBorder(buatTitledBorder("Pilih Pelanggan"));

        modelListPelanggan = new DefaultListModel<>();
        listPelanggan = new JList<>(modelListPelanggan);
        listPelanggan.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        listPelanggan.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listPelanggan.setFixedCellHeight(44);
        listPelanggan.setCellRenderer(new PelangganListRenderer());

        listPelanggan.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int idx = listPelanggan.getSelectedIndex();
                if (idx >= 0 && idx < aktifList.size()) {
                    pilihPelanggan(aktifList.get(idx));
                }
            }
        });

        JScrollPane scroll = new JScrollPane(listPelanggan);
        scroll.setBorder(null);
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    // =========================================================
    // PANEL KANAN — DETAIL + INPUT + STRUK
    // =========================================================

    private JPanel buatPanelDetail() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(BG_PANEL);

        // Atas: info sesi
        panel.add(buatSectionInfo(), BorderLayout.NORTH);

        // Tengah: input checkout
        JPanel tengah = new JPanel(new BorderLayout(0, 10));
        tengah.setBackground(BG_PANEL);

        panelInputDurasi = buatSectionInputDurasi();
        tengah.add(panelInputDurasi, BorderLayout.NORTH);

        // Panel kiloan (tabel tangkapan) — hanya tampil jika KILOAN
        panelKiloan = buatSectionKiloan();
        panelKiloan.setVisible(lapak.getJenisKolam().equals("KILOAN"));
        tengah.add(panelKiloan, BorderLayout.CENTER);

        panel.add(tengah, BorderLayout.CENTER);

        // Bawah: struk + total
        panel.add(buatSectionStruk(), BorderLayout.SOUTH);

        return panel;
    }

    // =========================================================
    // SECTION INFO SESI
    // =========================================================

    private JPanel buatSectionInfo() {
        JPanel section = new JPanel(new GridLayout(3, 2, 6, 4));
        section.setBackground(BG_PANEL);
        section.setBorder(buatTitledBorder("Info Sesi"));

        lblNama    = new JLabel("—");
        lblLapak   = new JLabel(lapak.getNamaLapak());
        lblPosisi  = new JLabel("—");
        lblCheckin = new JLabel("—");
        lblDurasi  = new JLabel("—");
        lblJenis   = new JLabel(lapak.getJenisKolam());

        section.add(buatInfoRow("Pelanggan", lblNama));
        section.add(buatInfoRow("Posisi", lblPosisi));
        section.add(buatInfoRow("Check-in", lblCheckin));
        section.add(buatInfoRow("Durasi", lblDurasi));
        section.add(buatInfoRow("Jenis", lblJenis));
        section.add(buatInfoRow("Lapak", lblLapak));

        return section;
    }

    // =========================================================
    // SECTION INPUT DURASI
    // =========================================================

    private JPanel buatSectionInputDurasi() {
        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setBackground(BG_PANEL);
        section.setBorder(buatTitledBorder("Input Checkout"));

        txtDurasi = new JTextField("0");

        String labelDurasi = "Durasi Aktual (menit)";
        if (lapak.getJenisKolam().equals("HARIAN")) {
            labelDurasi = "Durasi Aktual (menit) *";
        }

        section.add(buatFieldRow(labelDurasi, txtDurasi));

        // Disable durasi untuk non-HARIAN
        if (!lapak.getJenisKolam().equals("HARIAN")) {
            txtDurasi.setEnabled(false);
            txtDurasi.setText("0");
        }

        return section;
    }

    // =========================================================
    // SECTION KILOAN — TABEL TANGKAPAN
    // =========================================================

    private JPanel buatSectionKiloan() {
        JPanel section = new JPanel(new BorderLayout(0, 6));
        section.setBackground(BG_PANEL);
        section.setBorder(buatTitledBorder("Detail Tangkapan Ikan"));

        // Form tambah baris
        JPanel formTambah = new JPanel(new GridLayout(1, 4, 6, 0));
        formTambah.setBackground(BG_PANEL);
        formTambah.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

        txtNamaIkan  = new JTextField();
        txtBeratIkan = new JTextField();
        txtHargaPerKg = new JTextField();

        txtNamaIkan.setToolTipText("Nama ikan (contoh: Lele, Mas, Nila)");
        txtBeratIkan.setToolTipText("Berat dalam kg (contoh: 1.5)");
        txtHargaPerKg.setToolTipText("Harga per kg");

        JButton btnTambah = new JButton("+ Tambah");
        btnTambah.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnTambah.setBackground(BIRU_TUA);
        btnTambah.setForeground(Color.WHITE);
        btnTambah.setFocusPainted(false);
        btnTambah.setBorderPainted(false);
        btnTambah.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnTambah.addActionListener(e -> tambahBarisTangkapan());

        JLabel lblHint1 = new JLabel("Nama Ikan");
        lblHint1.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        JLabel lblHint2 = new JLabel("Berat (kg)");
        lblHint2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        JLabel lblHint3 = new JLabel("Harga/kg");
        lblHint3.setFont(new Font("Segoe UI", Font.PLAIN, 11));

        // Label + field per kolom
        JPanel col1 = buatKolomInput(lblHint1, txtNamaIkan);
        JPanel col2 = buatKolomInput(lblHint2, txtBeratIkan);
        JPanel col3 = buatKolomInput(lblHint3, txtHargaPerKg);

        JPanel col4 = new JPanel(new BorderLayout());
        col4.setOpaque(false);
        col4.setBorder(BorderFactory.createEmptyBorder(14, 0, 0, 0));
        col4.add(btnTambah, BorderLayout.CENTER);

        formTambah.add(col1);
        formTambah.add(col2);
        formTambah.add(col3);
        formTambah.add(col4);

        // Tabel tangkapan
        String[] kolom = {"Nama Ikan", "Berat (kg)", "Harga/kg", "Subtotal", ""};
        modelTabelTangkapan = new DefaultTableModel(kolom, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return col == 4; // Hanya kolom hapus
            }
        };

        tabelTangkapan = new JTable(modelTabelTangkapan);
        tabelTangkapan.setRowHeight(30);
        tabelTangkapan.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tabelTangkapan.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 11));
        tabelTangkapan.getTableHeader().setBackground(new Color(235, 241, 245));

        // Lebar kolom
        int[] lebar = {110, 80, 90, 100, 40};
        for (int i = 0; i < lebar.length; i++) {
            tabelTangkapan.getColumnModel().getColumn(i).setPreferredWidth(lebar[i]);
        }

        // Tombol hapus di kolom terakhir
        tabelTangkapan.getColumnModel().getColumn(4)
                .setCellRenderer(new HapusRenderer());
        tabelTangkapan.getColumnModel().getColumn(4)
                .setCellEditor(new HapusEditor(new JCheckBox(), this));

        JScrollPane scrollTabel = new JScrollPane(tabelTangkapan);
        scrollTabel.setPreferredSize(new Dimension(0, 120));
        scrollTabel.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));

        section.add(formTambah, BorderLayout.NORTH);
        section.add(scrollTabel, BorderLayout.CENTER);

        return section;
    }

    private JPanel buatKolomInput(JLabel label, JTextField field) {
        JPanel col = new JPanel(new BorderLayout(0, 3));
        col.setOpaque(false);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        label.setForeground(new Color(100, 100, 100));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        col.add(label, BorderLayout.NORTH);
        col.add(field, BorderLayout.CENTER);
        return col;
    }

    // =========================================================
    // SECTION STRUK & TOTAL
    // =========================================================

    private JPanel buatSectionStruk() {
        JPanel section = new JPanel(new BorderLayout(0, 6));
        section.setBackground(BG_PANEL);

        // Tombol hitung
        btnHitung = new JButton("Hitung Tagihan");
        btnHitung.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnHitung.setBackground(ORANYE);
        btnHitung.setForeground(Color.WHITE);
        btnHitung.setFocusPainted(false);
        btnHitung.setBorderPainted(false);
        btnHitung.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnHitung.setEnabled(false);
        btnHitung.addActionListener(e -> hitungPreview());

        // Struk teks
        txtStruk = new JTextArea(5, 0);
        txtStruk.setFont(new Font("Consolas", Font.PLAIN, 11));
        txtStruk.setEditable(false);
        txtStruk.setBackground(new Color(250, 250, 250));
        txtStruk.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
        txtStruk.setText("Pilih pelanggan dan klik Hitung Tagihan...");

        JScrollPane scrollStruk = new JScrollPane(txtStruk);
        scrollStruk.setPreferredSize(new Dimension(0, 110));
        scrollStruk.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));

        // Total tagihan
        JPanel panelTotal = new JPanel(new BorderLayout());
        panelTotal.setBackground(new Color(235, 245, 251));
        panelTotal.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BIRU_TUA),
                BorderFactory.createEmptyBorder(8, 14, 8, 14)
        ));

        JLabel lblJudul = new JLabel("Total Tagihan");
        lblJudul.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblJudul.setForeground(BIRU_TUA);

        lblTotalTagihan = new JLabel("Rp —");
        lblTotalTagihan.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTotalTagihan.setForeground(MERAH);
        lblTotalTagihan.setHorizontalAlignment(SwingConstants.RIGHT);

        panelTotal.add(lblJudul, BorderLayout.WEST);
        panelTotal.add(lblTotalTagihan, BorderLayout.EAST);

        section.add(btnHitung, BorderLayout.NORTH);
        section.add(scrollStruk, BorderLayout.CENTER);
        section.add(panelTotal, BorderLayout.SOUTH);

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
        btnBatal.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnBatal.setPreferredSize(new Dimension(90, 34));
        btnBatal.setFocusPainted(false);
        //btnBatal.addActionListener(e -> batalkanTransaksi());
        btnBatal.addActionListener(e -> dispose());

        btnCheckout = new JButton("✔ Checkout");
        btnCheckout.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnCheckout.setBackground(MERAH);
        btnCheckout.setForeground(Color.WHITE);
        btnCheckout.setFocusPainted(false);
        btnCheckout.setBorderPainted(false);
        btnCheckout.setPreferredSize(new Dimension(110, 34));
        btnCheckout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCheckout.setEnabled(false); // Aktif setelah hitung
        btnCheckout.addActionListener(e -> prosesCheckout());

        panel.add(btnBatal);
        panel.add(btnCheckout);

        return panel;
    }

    // =========================================================
    // HELPER UI
    // =========================================================

    private JPanel buatInfoRow(String labelTeks, JLabel valueLabel) {
        JPanel row = new JPanel(new BorderLayout(4, 0));
        row.setBackground(BG_PANEL);
        row.setBorder(BorderFactory.createEmptyBorder(2, 8, 2, 8));

        JLabel lbl = new JLabel(labelTeks + ":");
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lbl.setForeground(new Color(120, 120, 120));
        lbl.setPreferredSize(new Dimension(60, 18));

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));

        row.add(lbl, BorderLayout.WEST);
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

    private TitledBorder buatTitledBorder(String judul) {
        TitledBorder border = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)), judul);
        border.setTitleFont(new Font("Segoe UI", Font.BOLD, 12));
        return border;
    }

    // =========================================================
    // LOGIKA
    // =========================================================

    private void muatListPelanggan() {
        modelListPelanggan.clear();
        for (Transaksi t : aktifList) {
            modelListPelanggan.addElement(
                    "Pos " + t.getPosisi() + "  —  " + t.getNamaPelanggan());
        }
        // Auto-pilih pertama
        if (!aktifList.isEmpty()) {
            listPelanggan.setSelectedIndex(0);
        }
    }

    private void pilihPelanggan(Transaksi transaksi) {
        transaksiDipilih = transaksi;

        // Update info sesi
        lblNama.setText(transaksi.getNamaPelanggan());
        lblPosisi.setText(String.valueOf(transaksi.getPosisi()));
        lblJenis.setText(transaksi.getJenisKolam());
        lblCheckin.setText(FormatterUtil.formatTanggalWaktu(transaksi.getWaktuCheckin()));

        long menitBerjalan = 0;
        if (transaksi.getWaktuCheckin() != null) {
            menitBerjalan = Duration.between(
                    transaksi.getWaktuCheckin(), LocalDateTime.now()).toMinutes();
        }
        lblDurasi.setText(menitBerjalan + " menit");

        // Auto-fill durasi aktual
        if (lapak.getJenisKolam().equals("HARIAN")) {
            txtDurasi.setText(String.valueOf(menitBerjalan));
        }

        // Reset tabel tangkapan & struk
        modelTabelTangkapan.setRowCount(0);
        txtStruk.setText("Klik Hitung Tagihan untuk melihat preview...");
        lblTotalTagihan.setText("Rp —");
        btnCheckout.setEnabled(false);
        btnHitung.setEnabled(true);
    }

    /**
     * Tambah baris tangkapan ke tabel (untuk kolam KILOAN).
     */
    private void tambahBarisTangkapan() {
        String namaIkan   = txtNamaIkan.getText().trim();
        String beratStr   = txtBeratIkan.getText().trim();
        String hargaStr   = txtHargaPerKg.getText().trim();

        if (!ValidatorUtil.isNotNullOrEmpty(namaIkan)) {
            tampilkanError("Nama ikan tidak boleh kosong!");
            return;
        }
        if (!ValidatorUtil.isDecimal(beratStr)) {
            tampilkanError("Berat harus berupa angka (contoh: 1.5)!");
            return;
        }
        if (!ValidatorUtil.isDecimal(hargaStr)) {
            tampilkanError("Harga per kg harus berupa angka!");
            return;
        }

        BigDecimal berat = new BigDecimal(beratStr);
        BigDecimal harga = new BigDecimal(hargaStr);

        if (berat.compareTo(BigDecimal.ZERO) <= 0) {
            tampilkanError("Berat harus lebih dari 0!");
            return;
        }

        BigDecimal subtotal = berat.multiply(harga);

        modelTabelTangkapan.addRow(new Object[]{
            namaIkan,
            beratStr,
            FormatterUtil.formatRupiah(harga),
            FormatterUtil.formatRupiah(subtotal),
            "🗑"
        });

        // Reset form input
        txtNamaIkan.setText("");
        txtBeratIkan.setText("");
        txtHargaPerKg.setText("");
        txtNamaIkan.requestFocus();

        // Reset hitung
        btnCheckout.setEnabled(false);
        btnHitung.setEnabled(true);
    }

    /**
     * Hapus baris tangkapan dari tabel.
     */
    public void hapusBarisTangkapan(int baris) {
        if (baris >= 0 && baris < modelTabelTangkapan.getRowCount()) {
            modelTabelTangkapan.removeRow(baris);
            btnCheckout.setEnabled(false);
        }
    }

    /**
     * Kumpulkan list DetailTangkapan dari tabel.
     */
    private List<DetailTangkapan> ambilListTangkapan() {
        List<DetailTangkapan> list = new ArrayList<>();

        for (int i = 0; i < modelTabelTangkapan.getRowCount(); i++) {
            String namaIkan = (String) modelTabelTangkapan.getValueAt(i, 0);
            String beratStr = (String) modelTabelTangkapan.getValueAt(i, 1);

            // Harga disimpan dalam format Rupiah, perlu parse balik
            // Kita simpan harga asli di model terpisah
            BigDecimal berat = new BigDecimal(beratStr);

            // Ambil harga dari subtotal / berat (cara aman)
            // Kita simpan raw value saat tambahBaris via hidden column
            DetailTangkapan detail = new DetailTangkapan();
            detail.setNamaIkan(namaIkan);
            detail.setBeratKg(berat);
            detail.setCatatan("");
            list.add(detail);
        }

        return list;
    }

    private void hitungPreview() {
        if (transaksiDipilih == null) {
            tampilkanError("Pilih pelanggan terlebih dahulu!");
            return;
        }

        String durasi = txtDurasi.getText().trim();

        if (!ValidatorUtil.isNumeric(durasi)) {
            tampilkanError("Durasi harus berupa angka bulat!");
            return;
        }

        // Untuk KILOAN — wajib ada minimal 1 baris tangkapan
        List<DetailTangkapan> listTangkapan = new ArrayList<>();
        if (lapak.getJenisKolam().equals("KILOAN")) {
            listTangkapan = ambilListTangkapanLengkap();
            if (listTangkapan.isEmpty()) {
                tampilkanError("Tambahkan minimal 1 data tangkapan ikan!");
                return;
            }
        }

        String hasil = transaksiController.prosesCheckout(
                transaksiDipilih, lapak, durasi, listTangkapan, false);

        if (hasil.startsWith("Error")) {
            tampilkanError(hasil);
            btnCheckout.setEnabled(false);
        } else {
            txtStruk.setText(hasil);
            lblTotalTagihan.setText(
                    FormatterUtil.formatRupiah(transaksiDipilih.getTotalTagihan()));
            btnCheckout.setEnabled(true);
        }
    }

    /**
     * Ambil list tangkapan lengkap dengan harga dari tabel.
     * Menyimpan raw BigDecimal di hidden column index 5.
     */
    private List<DetailTangkapan> ambilListTangkapanLengkap() {
        List<DetailTangkapan> list = new ArrayList<>();

        for (int i = 0; i < modelTabelTangkapan.getRowCount(); i++) {
            String namaIkan = (String) modelTabelTangkapan.getValueAt(i, 0);
            String beratStr = (String) modelTabelTangkapan.getValueAt(i, 1);

            // Ambil harga raw dari kolom tersembunyi (index 5 jika ada)
            BigDecimal harga = BigDecimal.ZERO;
            if (modelTabelTangkapan.getColumnCount() > 5) {
                Object rawHarga = modelTabelTangkapan.getValueAt(i, 5);
                if (rawHarga instanceof BigDecimal) {
                    harga = (BigDecimal) rawHarga;
                }
            }

            DetailTangkapan detail = new DetailTangkapan();
            detail.setNamaIkan(namaIkan);
            detail.setBeratKg(new BigDecimal(beratStr));
            detail.setHargaPerKg(harga);
            detail.hitungSubtotal();
            detail.setCatatan("");
            list.add(detail);
        }

        return list;
    }

    private void prosesCheckout() {
        if (transaksiDipilih == null || transaksiDipilih.getTotalTagihan() == null) {
            tampilkanError("Harap hitung tagihan terlebih dahulu!");
            return;
        }

        int konfirmasi = JOptionPane.showConfirmDialog(
                this,
                "Checkout " + transaksiDipilih.getNamaPelanggan()
                        + " dari posisi " + transaksiDipilih.getPosisi()
                        + "?\n\nTotal: "
                        + FormatterUtil.formatRupiah(transaksiDipilih.getTotalTagihan()),
                "Konfirmasi Checkout",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (konfirmasi == JOptionPane.YES_OPTION) {
            
            // 1. Ambil ulang nilai durasi dan tangkapan dari layar UI
            String durasi = txtDurasi.getText().trim();
            List<DetailTangkapan> listTangkapan = new ArrayList<>();
            
            if (lapak.getJenisKolam().equals("KILOAN")) {
                listTangkapan = ambilListTangkapanLengkap();
            }

            // 2. EKSEKUSI DATABASE SECARA PERMANEN (isSimpanKeDB = true)
            // Method inilah yang akan mengubah status menjadi SELESAI di MySQL
            transaksiController.prosesCheckout(transaksiDipilih, lapak, durasi, listTangkapan, true);

            // 3. Refresh layar utama agar pelanggan langsung hilang dari UI
            if (lapakController != null) {
                // Gunakan method refresh yang ada di controller-mu (misal: muatData() atau semacamnya)
                lapakController.sinkronStatusLapak(lapak);
            }

            checkoutBerhasil = true;
            JOptionPane.showMessageDialog(this,
                    "Checkout berhasil!\nTerima kasih, "
                            + transaksiDipilih.getNamaPelanggan(),
                    "Berhasil", JOptionPane.INFORMATION_MESSAGE);
            
            // 4. Tutup Jendela
            dispose();
        }
    }

    private void tampilkanError(String pesan) {
        JOptionPane.showMessageDialog(this, pesan,
                "Error", JOptionPane.ERROR_MESSAGE);
    }

    // =========================================================
    // GETTER
    // =========================================================

    public boolean isCheckoutBerhasil() {
        return checkoutBerhasil;
    }

    // =========================================================
    // INNER CLASS RENDERER & EDITOR
    // =========================================================

    // Renderer list pelanggan kiri
    static class PelangganListRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value,
                int index, boolean selected, boolean cellHasFocus) {
            JPanel row = new JPanel(new BorderLayout(6, 0));
            row.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));

            // Nomor posisi
            String teks = String.valueOf(value);
            String[] parts = teks.split("  —  ");
            String posisiStr = parts.length > 0 ? parts[0] : "";
            String namaStr   = parts.length > 1 ? parts[1] : teks;

            JLabel lblPos = new JLabel(posisiStr);
            lblPos.setFont(new Font("Segoe UI", Font.BOLD, 11));
            lblPos.setForeground(new Color(133, 79, 11));
            lblPos.setOpaque(true);
            lblPos.setBackground(new Color(250, 238, 218));
            lblPos.setBorder(BorderFactory.createEmptyBorder(2, 6, 2, 6));

            JLabel lblNama = new JLabel(namaStr);
            lblNama.setFont(new Font("Segoe UI", Font.PLAIN, 13));

            row.add(lblPos, BorderLayout.WEST);
            row.add(lblNama, BorderLayout.CENTER);

            if (selected) {
                row.setBackground(new Color(235, 245, 255));
            } else {
                row.setBackground(Color.WHITE);
            }

            return row;
        }
    }

    // Renderer tombol hapus tabel tangkapan
    static class HapusRenderer extends DefaultListCellRenderer
            implements javax.swing.table.TableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean selected, boolean focus, int row, int col) {
            JButton btn = new JButton("🗑");
            btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            btn.setBackground(new Color(252, 235, 235));
            btn.setForeground(MERAH);
            btn.setFocusPainted(false);
            btn.setBorderPainted(false);
            return btn;
        }
    }

    // Editor tombol hapus
    static class HapusEditor extends DefaultCellEditor {
        private final CheckoutDialog dialog;
        private int barisTerpilih;
        private static final Color MERAH = new Color(192, 57, 43);

        HapusEditor(JCheckBox cb, CheckoutDialog dialog) {
            super(cb);
            this.dialog = dialog;
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean selected, int row, int col) {
            barisTerpilih = row;
            JButton btn = new JButton("🗑");
            btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            btn.setBackground(new Color(252, 235, 235));
            btn.setForeground(MERAH);
            btn.setFocusPainted(false);
            btn.setBorderPainted(false);
            btn.addActionListener(e -> {
                fireEditingStopped();
                dialog.hapusBarisTangkapan(barisTerpilih);
            });
            return btn;
        }

        @Override
        public Object getCellEditorValue() { return "🗑"; }
    }
}