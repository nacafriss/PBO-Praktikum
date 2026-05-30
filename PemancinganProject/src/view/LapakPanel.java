/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view;

import controller.LapakController;
import controller.TransaksiController;
import model.Lapak;
import model.Transaksi;
import thread.LapakTimerThread;
import utils.FormatterUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LapakPanel extends JPanel implements LapakTimerThread.LapakTimerListener {

    private LapakController lapakController;
    private TransaksiController transaksiController;

    // Timer per transaksi (key: transaksiId)
    private Map<Integer, LapakTimerThread> timerMap;

    // Panel kartu per lapak (key: lapakId)
    private Map<Integer, JPanel> kartuMap;

    // Komponen tab
    private JPanel tabKartu;
    private JPanel tabAktif;
    private JButton btnTabKartu;
    private JButton btnTabAktif;

    // Tabel pelanggan aktif
    private JTable tabelAktif;
    private DefaultTableModel modelTabelAktif;

    // Grid kartu lapak
    private JPanel gridPanel;

    // Summary metrics
    private JLabel lblTotalAktif;
    private JLabel lblLapakTerisi;
    private JLabel lblOvertime;
    private JLabel lblPosisiKosong;

    private static final Color BIRU_TUA   = new Color(26, 82, 118);
    private static final Color BG_CONTENT = new Color(240, 244, 248);
    private static final Color HIJAU      = new Color(39, 174, 96);
    private static final Color ORANYE     = new Color(230, 126, 34);
    private static final Color MERAH      = new Color(192, 57, 43);

    public LapakPanel() {
        this.lapakController    = new LapakController();
        this.transaksiController = new TransaksiController();
        this.timerMap  = new HashMap<>();
        this.kartuMap  = new HashMap<>();

        initUI();
        muatSemuaData();
    }

    // =========================================================
    // INISIALISASI UI
    // =========================================================

    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(BG_CONTENT);

        add(buatHeaderPanel(), BorderLayout.NORTH);

        // Container dua tab
        JPanel container = new JPanel(new CardLayout());
        container.setBackground(BG_CONTENT);

        tabKartu = buatTabKartu();
        tabAktif = buatTabAktif();

        container.add(tabKartu, "KARTU");
        container.add(tabAktif, "AKTIF");

        add(container, BorderLayout.CENTER);
    }

    private JPanel buatHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BIRU_TUA);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 16, 0, 16));

        // Judul
        JLabel title = new JLabel("Monitor Lapak");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(Color.WHITE);

        // Tab buttons
        JPanel tabPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        tabPanel.setOpaque(false);

        btnTabKartu = buatTabButton("Kartu Lapak", true);
        btnTabAktif = buatTabButton("Pelanggan Aktif", false);

        btnTabKartu.addActionListener(e -> pindahTab("KARTU"));
        btnTabAktif.addActionListener(e -> pindahTab("AKTIF"));

        tabPanel.add(btnTabKartu);
        tabPanel.add(btnTabAktif);

        // Refresh
        JButton btnRefresh = new JButton("Refresh");
        btnRefresh.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnRefresh.setBackground(new Color(52, 152, 219));
        btnRefresh.setForeground(Color.WHITE);
        btnRefresh.setFocusPainted(false);
        btnRefresh.setBorderPainted(false);
        btnRefresh.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRefresh.addActionListener(e -> refresh());

        JPanel kanan = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 8));
        kanan.setOpaque(false);
        kanan.add(btnRefresh);

        panel.add(title, BorderLayout.WEST);
        panel.add(tabPanel, BorderLayout.SOUTH);
        panel.add(kanan, BorderLayout.EAST);

        return panel;
    }

    private JButton buatTabButton(String teks, boolean aktif) {
        JButton btn = new JButton(teks);
        btn.setFont(new Font("Segoe UI", aktif ? Font.BOLD : Font.PLAIN, 13));
        btn.setForeground(aktif ? Color.WHITE : new Color(174, 214, 241));
        btn.setBackground(BIRU_TUA);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 16, 0, 16));

        // Garis bawah sebagai indikator tab aktif
        if (aktif) {
            btn.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 3, 0, Color.WHITE),
                    BorderFactory.createEmptyBorder(8, 16, 0, 16)
            ));
        }

        return btn;
    }

    // =========================================================
    // TAB KARTU LAPAK
    // =========================================================

    private JPanel buatTabKartu() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_CONTENT);

        gridPanel = new JPanel(new GridLayout(0, 3, 12, 12));
        gridPanel.setBackground(BG_CONTENT);
        gridPanel.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));

        JScrollPane scroll = new JScrollPane(gridPanel);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    // =========================================================
    // TAB PELANGGAN AKTIF
    // =========================================================

    private JPanel buatTabAktif() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_CONTENT);
        panel.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));

        // Summary metrics
        panel.add(buatSummaryPanel(), BorderLayout.NORTH);

        // Tabel
        String[] kolom = {"Pelanggan", "No. HP", "Lapak", "Jenis", "Posisi",
                           "Check-in", "Durasi", "Status", "Aksi"};
        modelTabelAktif = new DefaultTableModel(kolom, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return col == 8; // Hanya kolom Aksi yang bisa diklik
            }
        };

        tabelAktif = new JTable(modelTabelAktif);
        tabelAktif.setRowHeight(36);
        tabelAktif.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tabelAktif.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tabelAktif.getTableHeader().setBackground(new Color(235, 241, 245));
        tabelAktif.getTableHeader().setForeground(new Color(80, 80, 80));
        tabelAktif.setShowVerticalLines(false);
        tabelAktif.setGridColor(new Color(230, 230, 230));
        tabelAktif.setSelectionBackground(new Color(235, 245, 255));
        tabelAktif.setIntercellSpacing(new Dimension(0, 1));

        // Atur lebar kolom
        int[] lebarKolom = {140, 110, 90, 80, 55, 80, 90, 75, 85};
        for (int i = 0; i < lebarKolom.length; i++) {
            tabelAktif.getColumnModel().getColumn(i).setPreferredWidth(lebarKolom[i]);
        }

        // Renderer kolom Status
        tabelAktif.getColumnModel().getColumn(7).setCellRenderer(new StatusBadgeRenderer());

        // Renderer kolom Durasi
        tabelAktif.getColumnModel().getColumn(6).setCellRenderer(new DurasiRenderer());

        // Renderer kolom Aksi (tombol Checkout)
        tabelAktif.getColumnModel().getColumn(8).setCellRenderer(new TombolRenderer("Checkout"));
        tabelAktif.getColumnModel().getColumn(8).setCellEditor(
                new TombolEditor(new JCheckBox(), this));

        JScrollPane scrollTabel = new JScrollPane(tabelAktif);
        scrollTabel.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        scrollTabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(10, 0, 0, 0),
                BorderFactory.createLineBorder(new Color(220, 220, 220))
        ));

        panel.add(scrollTabel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel buatSummaryPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 4, 10, 0));
        panel.setOpaque(false);

        lblTotalAktif   = buatMetricCard("Total Aktif", "0", ORANYE);
        lblLapakTerisi  = buatMetricCard("Lapak Terisi", "0", HIJAU);
        lblOvertime     = buatMetricCard("Overtime", "0", MERAH);
        lblPosisiKosong = buatMetricCard("Posisi Kosong", "0", new Color(100, 100, 100));

        panel.add(lblTotalAktif.getParent());
        panel.add(lblLapakTerisi.getParent());
        panel.add(lblOvertime.getParent());
        panel.add(lblPosisiKosong.getParent());

        return panel;
    }

    private JLabel buatMetricCard(String labelTeks, String nilai, Color warnaAngka) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(10, 14, 10, 14)
        ));

        JLabel lblLabel = new JLabel(labelTeks);
        lblLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblLabel.setForeground(new Color(120, 120, 120));

        JLabel lblNilai = new JLabel(nilai);
        lblNilai.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblNilai.setForeground(warnaAngka);

        card.add(lblLabel, BorderLayout.NORTH);
        card.add(lblNilai, BorderLayout.CENTER);

        return lblNilai;
    }

    // =========================================================
    // LOAD DATA
    // =========================================================

    private void muatSemuaData() {
        muatKartuLapak();
        muatTabelAktif();
    }

    private void muatKartuLapak() {
        gridPanel.removeAll();
        kartuMap.clear();

        // Hentikan semua timer
        for (LapakTimerThread t : timerMap.values()) t.hentikan();
        timerMap.clear();

        List<Lapak> daftarLapak = lapakController.semuaLapak();

        if (daftarLapak == null || daftarLapak.isEmpty()) {
            JLabel kosong = new JLabel("Belum ada lapak terdaftar.", SwingConstants.CENTER);
            kosong.setFont(new Font("Segoe UI", Font.ITALIC, 14));
            kosong.setForeground(Color.GRAY);
            gridPanel.add(kosong);
        } else {
            for (Lapak lapak : daftarLapak) {
                JPanel kartu = buatKartuLapak(lapak);
                kartuMap.put(lapak.getId(), kartu);
                gridPanel.add(kartu);

                // Mulai timer untuk setiap transaksi aktif di lapak ini
                List<Transaksi> aktifList =
                        lapakController.getSemuaTransaksiAktifByLapak(lapak.getId());
                for (Transaksi t : aktifList) {
                    mulaiTimer(t);
                }
            }
        }

        gridPanel.revalidate();
        gridPanel.repaint();
    }

    private void muatTabelAktif() {
        modelTabelAktif.setRowCount(0);

        List<Transaksi> aktifList = lapakController.getSemuaTransaksiAktif();
        List<Lapak> semuaLapak   = lapakController.semuaLapak();

        int totalAktif    = aktifList.size();
        int overtime      = 0;
        int posisiKosong  = 0;

        // Hitung posisi kosong
        for (Lapak l : semuaLapak) {
            List<Transaksi> isiLapak =
                    lapakController.getSemuaTransaksiAktifByLapak(l.getId());
            posisiKosong += (l.getKapasitas() - isiLapak.size());
        }

        for (Transaksi t : aktifList) {
            // Cari nama lapak
            String namaLapak = "-";
            for (Lapak l : semuaLapak) {
                if (l.getId() == t.getLapakId()) {
                    namaLapak = l.getNamaLapak();
                    break;
                }
            }

            // Hitung durasi
            long menitBerjalan = 0;
            if (t.getWaktuCheckin() != null) {
                menitBerjalan = Duration.between(
                        t.getWaktuCheckin(), LocalDateTime.now()).toMinutes();
            }
            String durasiStr = formatDurasiMenit(menitBerjalan);

            // Cek overtime
            boolean isOvertime = false;
            if (t.getDurasiPesanMenit() != null && t.getDurasiPesanMenit() > 0) {
                isOvertime = menitBerjalan >= t.getDurasiPesanMenit();
            }
            if (isOvertime) overtime++;

            String statusStr  = isOvertime ? "OVERTIME" : "AKTIF";
            String waktuCI    = t.getWaktuCheckin() != null
                    ? t.getWaktuCheckin().format(
                            java.time.format.DateTimeFormatter.ofPattern("HH:mm")) : "-";

            modelTabelAktif.addRow(new Object[]{
                t.getNamaPelanggan(),
                t.getNoHpPelanggan() != null ? t.getNoHpPelanggan() : "-",
                namaLapak,
                t.getJenisKolam(),
                t.getPosisi() != null ? t.getPosisi() : "-",
                waktuCI,
                durasiStr,
                statusStr,
                "Checkout"
            });
        }

        // Update metric cards
        lblTotalAktif.setText(String.valueOf(totalAktif));
        lblLapakTerisi.setText(String.valueOf(
                semuaLapak.stream().filter(l -> l.getStatus().equals("TERISI")).count()));
        lblOvertime.setText(String.valueOf(overtime));
        lblPosisiKosong.setText(String.valueOf(posisiKosong));
    }

    // =========================================================
    // KARTU LAPAK
    // =========================================================

    private JPanel buatKartuLapak(Lapak lapak) {
        List<Transaksi> aktifList =
                lapakController.getSemuaTransaksiAktifByLapak(lapak.getId());

        JPanel kartu = new JPanel(new BorderLayout());
        kartu.setBackground(Color.WHITE);
        kartu.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(warnaBorder(lapak), 2),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));

        // -- Header kartu --
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(BorderFactory.createEmptyBorder(10, 12, 8, 12));

        JLabel lblNama = new JLabel(lapak.getNamaLapak());
        lblNama.setFont(new Font("Segoe UI", Font.BOLD, 13));

        JLabel lblBadge = new JLabel(lapak.getStatus());
        lblBadge.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblBadge.setOpaque(true);
        lblBadge.setBorder(BorderFactory.createEmptyBorder(2, 8, 2, 8));
        lblBadge.setBackground(warnaBgBadge(lapak));
        lblBadge.setForeground(warnaTextBadge(lapak));

        header.add(lblNama, BorderLayout.WEST);
        header.add(lblBadge, BorderLayout.EAST);

        // -- Jenis + kapasitas --
        JPanel subHeader = new JPanel(new BorderLayout());
        subHeader.setBackground(new Color(248, 249, 250));
        subHeader.setBorder(BorderFactory.createEmptyBorder(4, 12, 4, 12));

        JLabel lblJenis = new JLabel(lapak.getJenisKolam());
        lblJenis.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblJenis.setForeground(new Color(100, 100, 100));

        JLabel lblKapasitas = new JLabel(aktifList.size() + "/" + lapak.getKapasitas() + " terisi");
        lblKapasitas.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblKapasitas.setForeground(new Color(100, 100, 100));

        subHeader.add(lblJenis, BorderLayout.WEST);
        subHeader.add(lblKapasitas, BorderLayout.EAST);

        // -- List posisi --
        JPanel posisiPanel = new JPanel();
        posisiPanel.setLayout(new BoxLayout(posisiPanel, BoxLayout.Y_AXIS));
        posisiPanel.setBackground(Color.WHITE);
        posisiPanel.setBorder(BorderFactory.createEmptyBorder(4, 12, 4, 12));

        // Buat map posisi terisi
        Map<Integer, Transaksi> mapPosisi = new HashMap<>();
        for (Transaksi t : aktifList) {
            if (t.getPosisi() != null) mapPosisi.put(t.getPosisi(), t);
        }

        for (int i = 1; i <= lapak.getKapasitas(); i++) {
            posisiPanel.add(buatPosisiRow(i, mapPosisi.get(i)));
            if (i < lapak.getKapasitas()) {
                JSeparator sep = new JSeparator();
                sep.setForeground(new Color(240, 240, 240));
                posisiPanel.add(sep);
            }
        }

        // -- Tombol footer --
        JPanel footer = buatFooterKartu(lapak, aktifList);

        // Susun kartu
        JPanel topSection = new JPanel(new BorderLayout());
        topSection.setBackground(Color.WHITE);
        topSection.add(header, BorderLayout.NORTH);
        topSection.add(subHeader, BorderLayout.SOUTH);

        kartu.add(topSection, BorderLayout.NORTH);
        kartu.add(posisiPanel, BorderLayout.CENTER);
        kartu.add(footer, BorderLayout.SOUTH);

        return kartu;
    }

    private JPanel buatPosisiRow(int noPosisi, Transaksi transaksi) {
        JPanel row = new JPanel(new BorderLayout(8, 0));
        row.setBackground(Color.WHITE);
        row.setBorder(BorderFactory.createEmptyBorder(6, 0, 6, 0));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));

        // Nomor posisi
        JLabel lblNo = new JLabel(String.valueOf(noPosisi), SwingConstants.CENTER);
        lblNo.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblNo.setPreferredSize(new Dimension(24, 24));
        lblNo.setOpaque(true);

        if (transaksi != null) {
            lblNo.setBackground(new Color(250, 238, 218));
            lblNo.setForeground(new Color(133, 79, 11));
        } else {
            lblNo.setBackground(new Color(234, 243, 222));
            lblNo.setForeground(new Color(59, 109, 17));
        }

        // Konten tengah
        JPanel tengah = new JPanel(new BorderLayout());
        tengah.setOpaque(false);

        if (transaksi != null) {
            JLabel lblNama = new JLabel(transaksi.getNamaPelanggan());
            lblNama.setFont(new Font("Segoe UI", Font.PLAIN, 12));

            // Timer label — pakai nama unik berdasarkan transaksiId
            JLabel lblTimer = new JLabel("--:--:--");
            lblTimer.setName("timer_" + transaksi.getId());
            lblTimer.setFont(new Font("Consolas", Font.PLAIN, 11));
            lblTimer.setForeground(new Color(100, 100, 100));

            tengah.add(lblNama, BorderLayout.WEST);
            tengah.add(lblTimer, BorderLayout.EAST);
        } else {
            JLabel lblKosong = new JLabel("— kosong");
            lblKosong.setFont(new Font("Segoe UI", Font.ITALIC, 11));
            lblKosong.setForeground(new Color(160, 160, 160));
            tengah.add(lblKosong, BorderLayout.WEST);
        }

        row.add(lblNo, BorderLayout.WEST);
        row.add(tengah, BorderLayout.CENTER);

        return row;
    }

    private JPanel buatFooterKartu(Lapak lapak, List<Transaksi> aktifList) {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 8));
        footer.setBackground(new Color(248, 249, 250));
        footer.setBorder(BorderFactory.createMatteBorder(
                1, 0, 0, 0, new Color(235, 235, 235)));

        boolean bisaCheckin = lapakController.bisaCheckin(lapak);
        boolean adaAktif    = !aktifList.isEmpty();

        if (!lapak.isAktif()) {
            JLabel lbl = new JLabel("Nonaktif");
            lbl.setFont(new Font("Segoe UI", Font.ITALIC, 11));
            lbl.setForeground(Color.GRAY);
            footer.add(lbl);
            return footer;
        }

        if (bisaCheckin) {
            JButton btnCI = buatTombolKecil("+ Check-in", HIJAU);
            btnCI.addActionListener(e -> bukaCheckinDialog(lapak));
            footer.add(btnCI);
        }

        if (adaAktif) {
            JButton btnCO = buatTombolKecil("Checkout", MERAH);
            btnCO.addActionListener(e -> bukaCheckoutDialog(lapak));
            footer.add(btnCO);
        }

        return footer;
    }

    private JButton buatTombolKecil(String teks, Color warna) {
        JButton btn = new JButton(teks);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btn.setBackground(warna.brighter().brighter());
        btn.setForeground(warna.darker().darker());
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(90, 26));
        return btn;
    }

    // =========================================================
    // WARNA HELPER
    // =========================================================

    private Color warnaBorder(Lapak lapak) {
        if (!lapak.isAktif()) return new Color(200, 200, 200);
        switch (lapak.getStatus()) {
            case "KOSONG": return new Color(39, 174, 96);
            case "TERISI": return new Color(230, 126, 34);
            default:       return new Color(200, 200, 200);
        }
    }

    private Color warnaBgBadge(Lapak lapak) {
        if (!lapak.isAktif()) return new Color(220, 220, 220);
        switch (lapak.getStatus()) {
            case "KOSONG": return new Color(234, 243, 222);
            case "TERISI": return new Color(250, 238, 218);
            default:       return new Color(220, 220, 220);
        }
    }

    private Color warnaTextBadge(Lapak lapak) {
        if (!lapak.isAktif()) return Color.GRAY;
        switch (lapak.getStatus()) {
            case "KOSONG": return new Color(59, 109, 17);
            case "TERISI": return new Color(133, 79, 11);
            default:       return Color.GRAY;
        }
    }

    // =========================================================
    // DIALOG
    // =========================================================

    private void bukaCheckinDialog(Lapak lapak) {
        CheckinDialog dialog = new CheckinDialog(
                (Frame) SwingUtilities.getWindowAncestor(this),
                lapak, lapakController);
        dialog.setVisible(true);

        if (dialog.isCheckinBerhasil()) {
            refresh();
        }
    }

    public void bukaCheckoutDialog(Lapak lapak) {
        List<Transaksi> aktifList =
                lapakController.getSemuaTransaksiAktifByLapak(lapak.getId());
        if (aktifList.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Tidak ada pelanggan aktif di lapak ini.",
                    "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        CheckoutDialog dialog = new CheckoutDialog(
                (Frame) SwingUtilities.getWindowAncestor(this),
                lapak, aktifList, transaksiController, lapakController);
        dialog.setVisible(true);

        if (dialog.isCheckoutBerhasil()) {
            refresh();
        }
    }

    /**
     * Checkout langsung dari baris tabel pelanggan aktif.
     */
    public void checkoutDariTabel(int baris) {
        List<Transaksi> aktifList = lapakController.getSemuaTransaksiAktif();
        if (baris < 0 || baris >= aktifList.size()) return;

        Transaksi transaksi = aktifList.get(baris);

        // Cari lapak
        Lapak lapak = null;
        for (Lapak l : lapakController.semuaLapak()) {
            if (l.getId() == transaksi.getLapakId()) {
                lapak = l;
                break;
            }
        }
        if (lapak == null) return;

        List<Transaksi> aktifDiLapak =
                lapakController.getSemuaTransaksiAktifByLapak(lapak.getId());

        CheckoutDialog dialog = new CheckoutDialog(
                (Frame) SwingUtilities.getWindowAncestor(this),
                lapak, aktifDiLapak, transaksiController, lapakController);
        dialog.setVisible(true);

        if (dialog.isCheckoutBerhasil()) {
            refresh();
        }
    }

    // =========================================================
    // TIMER
    // =========================================================

    private void mulaiTimer(Transaksi transaksi) {
        hentikanTimer(transaksi.getId());

        // Cari lapak untuk transaksi ini
        Lapak lapak = null;
        for (Lapak l : lapakController.semuaLapak()) {
            if (l.getId() == transaksi.getLapakId()) {
                lapak = l;
                break;
            }
        }
        if (lapak == null) return;

        LapakTimerThread timer = new LapakTimerThread(lapak, transaksi, this);
        timerMap.put(transaksi.getId(), timer);
        timer.start();
    }

    private void hentikanTimer(int transaksiId) {
        LapakTimerThread timer = timerMap.get(transaksiId);
        if (timer != null) {
            timer.hentikan();
            timerMap.remove(transaksiId);
        }
    }

    // =========================================================
    // CALLBACK TIMER
    // =========================================================

    @Override
    public void onTimerUpdate(int lapakId, String waktuBerjalan, boolean overtime) {
        // Cari label timer di semua kartu berdasarkan nama komponen
        SwingUtilities.invokeLater(() -> {
            for (JPanel kartu : kartuMap.values()) {
                cariDanUpdateTimerLabel(kartu, lapakId, waktuBerjalan, overtime);
            }
            // Update tabel juga
            muatTabelAktif();
        });
    }

    @Override
    public void onTimerSelesai(int lapakId) {
        SwingUtilities.invokeLater(this::muatTabelAktif);
    }

    private void cariDanUpdateTimerLabel(Container container,
                                          int transaksiId,
                                          String waktu,
                                          boolean overtime) {
        for (Component c : container.getComponents()) {
            if (c instanceof JLabel) {
                JLabel lbl = (JLabel) c;
                if (("timer_" + transaksiId).equals(lbl.getName())) {
                    lbl.setText(waktu);
                    lbl.setForeground(overtime ? MERAH : new Color(100, 100, 100));
                }
            } else if (c instanceof Container) {
                cariDanUpdateTimerLabel((Container) c, transaksiId, waktu, overtime);
            }
        }
    }

    // =========================================================
    // TAB NAVIGATION
    // =========================================================

    private void pindahTab(String key) {
        CardLayout cl = (CardLayout) ((JPanel) tabKartu.getParent()).getLayout();
        cl.show(tabKartu.getParent(), key);

        boolean kartuAktif = key.equals("KARTU");
        btnTabKartu.setFont(new Font("Segoe UI",
                kartuAktif ? Font.BOLD : Font.PLAIN, 13));
        btnTabKartu.setForeground(kartuAktif ? Color.WHITE : new Color(174, 214, 241));
        btnTabKartu.setBorder(kartuAktif
                ? BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 3, 0, Color.WHITE),
                        BorderFactory.createEmptyBorder(8, 16, 0, 16))
                : BorderFactory.createEmptyBorder(8, 16, 0, 16));

        btnTabAktif.setFont(new Font("Segoe UI",
                !kartuAktif ? Font.BOLD : Font.PLAIN, 13));
        btnTabAktif.setForeground(!kartuAktif ? Color.WHITE : new Color(174, 214, 241));
        btnTabAktif.setBorder(!kartuAktif
                ? BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 3, 0, Color.WHITE),
                        BorderFactory.createEmptyBorder(8, 16, 0, 16))
                : BorderFactory.createEmptyBorder(8, 16, 0, 16));

        if (!kartuAktif) muatTabelAktif();
    }

    public void refresh() {
        muatSemuaData();
    }

    // =========================================================
    // HELPER FORMAT
    // =========================================================

    private String formatDurasiMenit(long totalMenit) {
        long jam   = totalMenit / 60;
        long menit = totalMenit % 60;
        return String.format("%02d:%02d", jam, menit);
    }

    // =========================================================
    // INNER CLASS RENDERER & EDITOR TABEL
    // =========================================================

    // Renderer badge status
    static class StatusBadgeRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean selected, boolean focus, int row, int col) {
            JLabel lbl = new JLabel(String.valueOf(value));
            lbl.setOpaque(true);
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
            lbl.setHorizontalAlignment(SwingConstants.CENTER);
            lbl.setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 6));

            if ("OVERTIME".equals(value)) {
                lbl.setBackground(new Color(252, 235, 235));
                lbl.setForeground(new Color(163, 45, 45));
            } else {
                lbl.setBackground(new Color(234, 243, 222));
                lbl.setForeground(new Color(59, 109, 17));
            }
            return lbl;
        }
    }

    // Renderer durasi — merah jika overtime
    static class DurasiRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean selected, boolean focus, int row, int col) {
            JLabel lbl = new JLabel(String.valueOf(value));
            lbl.setFont(new Font("Consolas", Font.PLAIN, 12));
            lbl.setOpaque(true);
            lbl.setBackground(selected
                    ? table.getSelectionBackground() : table.getBackground());

            String status = (String) table.getValueAt(row, 7);
            lbl.setForeground("OVERTIME".equals(status)
                    ? new Color(163, 45, 45) : table.getForeground());
            return lbl;
        }
    }

    // Renderer tombol di tabel
    static class TombolRenderer extends DefaultTableCellRenderer {
        private final String teks;
        TombolRenderer(String teks) { this.teks = teks; }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean selected, boolean focus, int row, int col) {
            JButton btn = new JButton(teks);
            btn.setFont(new Font("Segoe UI", Font.BOLD, 11));
            btn.setBackground(new Color(250, 238, 218));
            btn.setForeground(new Color(133, 79, 11));
            btn.setFocusPainted(false);
            btn.setBorderPainted(false);
            return btn;
        }
    }

    // Editor tombol di tabel — trigger checkout saat diklik
    static class TombolEditor extends DefaultCellEditor {
        private final LapakPanel panel;
        private int barisTerpilih;

        TombolEditor(JCheckBox cb, LapakPanel panel) {
            super(cb);
            this.panel = panel;
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean selected, int row, int col) {
            barisTerpilih = row;
            JButton btn = new JButton("Checkout");
            btn.setFont(new Font("Segoe UI", Font.BOLD, 11));
            btn.setBackground(new Color(250, 238, 218));
            btn.setForeground(new Color(133, 79, 11));
            btn.setFocusPainted(false);
            btn.setBorderPainted(false);
            btn.addActionListener(e -> {
                fireEditingStopped();
                panel.checkoutDariTabel(barisTerpilih);
            });
            return btn;
        }

        @Override
        public Object getCellEditorValue() { return "Checkout"; }
    }
}
