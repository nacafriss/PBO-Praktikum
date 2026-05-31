/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view;

/**
 *
 * @author user
 */
import dao.LaporanDao;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class LaporanPanel extends JPanel {

    private LaporanDao laporanDao;

    // Komponen filter
    private JSpinner spinnerMulai;
    private JSpinner spinnerSelesai;
    private JButton btnTampilkan;

    // Ringkasan
    private JLabel lblTotalPengunjung;
    private JLabel lblRataPerHari;
    private JLabel lblHariTersibuk;
    private JLabel lblPuncakHari;

    // Grafik
    private GrafikPanel grafikPanel;

    // Data
    private Map<LocalDate, Integer> dataGrafik;

    // Warna dark theme
    private static final Color BG         = new Color(15, 23, 42);
    private static final Color BG_CARD    = new Color(30, 41, 59);
    private static final Color BORDER     = new Color(51, 65, 85);
    private static final Color TEXT_UTAMA = Color.WHITE;
    private static final Color TEXT_SUB   = new Color(148, 163, 184);
    private static final Color BIRU       = new Color(37, 99, 235);
    private static final Color HIJAU      = new Color(34, 197, 94);
    private static final Color ORANYE     = new Color(251, 146, 60);
    private static final Color MERAH      = new Color(239, 68, 68);

    public LaporanPanel() {
        this.laporanDao = new LaporanDao();
        initUI();
        // Default load 7 hari terakhir
        muatData(LocalDate.now().minusDays(6), LocalDate.now());
    }

    // =========================================================
    // INISIALISASI UI
    // =========================================================

    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(BG);

        add(buatHeaderPanel(), BorderLayout.NORTH);
        add(buatBodyPanel(), BorderLayout.CENTER);
    }

    private JPanel buatHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG);
        panel.setBorder(new EmptyBorder(16, 20, 12, 20));

        // Judul
        JPanel judulPanel = new JPanel();
        judulPanel.setLayout(new BoxLayout(judulPanel, BoxLayout.Y_AXIS));
        judulPanel.setOpaque(false);

        JLabel title = new JLabel("Laporan");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(TEXT_UTAMA);

        JLabel sub = new JLabel("Statistik pengunjung per hari");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        sub.setForeground(TEXT_SUB);

        judulPanel.add(title);
        judulPanel.add(Box.createVerticalStrut(2));
        judulPanel.add(sub);

        // Filter tanggal
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        filterPanel.setOpaque(false);

        // Spinner tanggal mulai
        SpinnerDateModel modelMulai = new SpinnerDateModel();
        spinnerMulai = new JSpinner(modelMulai);
        spinnerMulai.setEditor(new JSpinner.DateEditor(spinnerMulai, "dd/MM/yyyy"));
        spinnerMulai.setValue(java.util.Date.from(
                LocalDate.now().minusDays(6)
                        .atStartOfDay(java.time.ZoneId.systemDefault()).toInstant()));
        styleSpinner(spinnerMulai);

        // Spinner tanggal selesai
        SpinnerDateModel modelSelesai = new SpinnerDateModel();
        spinnerSelesai = new JSpinner(modelSelesai);
        spinnerSelesai.setEditor(new JSpinner.DateEditor(spinnerSelesai, "dd/MM/yyyy"));
        spinnerSelesai.setValue(java.util.Date.from(
                LocalDate.now()
                        .atStartOfDay(java.time.ZoneId.systemDefault()).toInstant()));
        styleSpinner(spinnerSelesai);

        // Tombol tampilkan
        btnTampilkan = new JButton("Tampilkan");
        btnTampilkan.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnTampilkan.setBackground(BIRU);
        btnTampilkan.setForeground(Color.WHITE);
        btnTampilkan.setFocusPainted(false);
        btnTampilkan.setBorderPainted(false);
        btnTampilkan.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnTampilkan.setBorder(new EmptyBorder(7, 16, 7, 16));
        btnTampilkan.addActionListener(e -> onTampilkan());

        JLabel lblDari = new JLabel("Dari");
        lblDari.setForeground(TEXT_SUB);
        lblDari.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        JLabel lblSampai = new JLabel("sampai");
        lblSampai.setForeground(TEXT_SUB);
        lblSampai.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        filterPanel.add(lblDari);
        filterPanel.add(spinnerMulai);
        filterPanel.add(lblSampai);
        filterPanel.add(spinnerSelesai);
        filterPanel.add(btnTampilkan);

        // Shortcut range
        JPanel shortcutPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 0));
        shortcutPanel.setOpaque(false);

        String[] shortcuts = {"7 Hari", "30 Hari", "Bulan Ini"};
        for (String s : shortcuts) {
            JButton btn = buatTombolShortcut(s);
            shortcutPanel.add(btn);
        }

        JPanel kananPanel = new JPanel(new BorderLayout(0, 6));
        kananPanel.setOpaque(false);
        kananPanel.add(filterPanel, BorderLayout.NORTH);
        kananPanel.add(shortcutPanel, BorderLayout.SOUTH);

        panel.add(judulPanel, BorderLayout.WEST);
        panel.add(kananPanel, BorderLayout.EAST);

        return panel;
    }

    private JButton buatTombolShortcut(String teks) {
        JButton btn = new JButton(teks);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        btn.setForeground(TEXT_SUB);
        btn.setBackground(BG_CARD);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(4, 10, 4, 10));

        btn.addActionListener(e -> {
            LocalDate selesai = LocalDate.now();
            LocalDate mulai;
            switch (teks) {
                case "7 Hari":   mulai = selesai.minusDays(6); break;
                case "30 Hari":  mulai = selesai.minusDays(29); break;
                case "Bulan Ini": mulai = selesai.withDayOfMonth(1); break;
                default: mulai = selesai.minusDays(6);
            }
            // Update spinner
            spinnerMulai.setValue(java.util.Date.from(
                    mulai.atStartOfDay(java.time.ZoneId.systemDefault()).toInstant()));
            spinnerSelesai.setValue(java.util.Date.from(
                    selesai.atStartOfDay(java.time.ZoneId.systemDefault()).toInstant()));
            muatData(mulai, selesai);
        });

        return btn;
    }

    private JPanel buatBodyPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setBackground(BG);
        panel.setBorder(new EmptyBorder(0, 20, 20, 20));

        // Ringkasan cards
        panel.add(buatRingkasanPanel(), BorderLayout.NORTH);

        // Grafik
        grafikPanel = new GrafikPanel();
        grafikPanel.setPreferredSize(new Dimension(0, 340));
        grafikPanel.setBackground(BG_CARD);
        grafikPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                new EmptyBorder(20, 20, 20, 20)
        ));

        panel.add(grafikPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel buatRingkasanPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 4, 10, 0));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(0, 0, 4, 0));

        lblTotalPengunjung = buatSummaryCard("Total Pengunjung", "0", BIRU);
        lblRataPerHari     = buatSummaryCard("Rata-rata/Hari",   "0", HIJAU);
        lblHariTersibuk    = buatSummaryCard("Hari Tersibuk",    "-", ORANYE);
        lblPuncakHari      = buatSummaryCard("Puncak Hari",      "0 org", MERAH);

        panel.add(lblTotalPengunjung.getParent());
        panel.add(lblRataPerHari.getParent());
        panel.add(lblHariTersibuk.getParent());
        panel.add(lblPuncakHari.getParent());

        return panel;
    }

    private JLabel buatSummaryCard(String labelTeks, String nilai, Color warna) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                new EmptyBorder(14, 16, 14, 16)
        ));

        JLabel lblLabel = new JLabel(labelTeks);
        lblLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblLabel.setForeground(TEXT_SUB);

        JLabel lblNilai = new JLabel(nilai);
        lblNilai.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblNilai.setForeground(warna);

        card.add(lblLabel, BorderLayout.NORTH);
        card.add(lblNilai, BorderLayout.CENTER);

        return lblNilai;
    }

    // =========================================================
    // LOGIKA
    // =========================================================

    private void onTampilkan() {
        java.util.Date dateMulai   = (java.util.Date) spinnerMulai.getValue();
        java.util.Date dateSelesai = (java.util.Date) spinnerSelesai.getValue();

        LocalDate mulai = dateMulai.toInstant()
                .atZone(java.time.ZoneId.systemDefault()).toLocalDate();
        LocalDate selesai = dateSelesai.toInstant()
                .atZone(java.time.ZoneId.systemDefault()).toLocalDate();

        if (mulai.isAfter(selesai)) {
            JOptionPane.showMessageDialog(this,
                    "Tanggal mulai tidak boleh setelah tanggal selesai!",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        muatData(mulai, selesai);
    }

    private void muatData(LocalDate mulai, LocalDate selesai) {
        // Ambil data dari DB
        dataGrafik = laporanDao.getPengunjungPerHari(mulai, selesai);

        int total        = laporanDao.getTotalPengunjung(mulai, selesai);
        double rata      = laporanDao.getRataPengunjungPerHari(mulai, selesai);
        LocalDate sibuk  = laporanDao.getHariTersibuk(mulai, selesai);
        int puncak       = laporanDao.getJumlahHariTersibuk(mulai, selesai);

        // Update ringkasan
        lblTotalPengunjung.setText(String.valueOf(total));
        lblRataPerHari.setText(String.format("%.1f", rata));
        lblHariTersibuk.setText(sibuk != null
                ? sibuk.format(DateTimeFormatter.ofPattern("dd MMM",
                        new Locale("id", "ID")))
                : "-");
        lblPuncakHari.setText(puncak + " org");

        // Update grafik
        grafikPanel.setData(dataGrafik);
        grafikPanel.repaint();
    }

    private void styleSpinner(JSpinner spinner) {
        spinner.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        spinner.setBackground(BG_CARD);
        spinner.setForeground(TEXT_UTAMA);
        spinner.setPreferredSize(new Dimension(110, 30));
        JComponent editor = spinner.getEditor();
        if (editor instanceof JSpinner.DateEditor) {
            JTextField tf = ((JSpinner.DateEditor) editor).getTextField();
            tf.setBackground(BG_CARD);
            tf.setForeground(TEXT_UTAMA);
            tf.setCaretColor(TEXT_UTAMA);
            tf.setBorder(new EmptyBorder(4, 8, 4, 8));
            tf.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        }
    }

    // =========================================================
    // INNER CLASS — GRAFIK CUSTOM
    // =========================================================

    class GrafikPanel extends JPanel {

        private Map<LocalDate, Integer> data;
        private final DateTimeFormatter fmtLabel =
                DateTimeFormatter.ofPattern("dd/MM");

        public void setData(Map<LocalDate, Integer> data) {
            this.data = data;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (data == null || data.isEmpty()) {
                g.setColor(TEXT_SUB);
                g.setFont(new Font("Segoe UI", Font.ITALIC, 14));
                String msg = "Tidak ada data untuk ditampilkan";
                FontMetrics fm = g.getFontMetrics();
                g.drawString(msg,
                        (getWidth() - fm.stringWidth(msg)) / 2,
                        getHeight() / 2);
                return;
            }

            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                    RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();
            int padLeft   = 50;
            int padRight  = 20;
            int padTop    = 20;
            int padBottom = 50;

            int grafikW = w - padLeft - padRight;
            int grafikH = h - padTop - padBottom;

            // Nilai max
            int maxVal = data.values().stream()
                    .mapToInt(Integer::intValue).max().orElse(1);
            if (maxVal == 0) maxVal = 1;

            List<LocalDate> keys = new ArrayList<>(data.keySet());
            int n = keys.size();
            if (n == 0) return;

            // Lebar per bar
            int gap     = Math.max(4, grafikW / (n * 3));
            int barW    = Math.max(8, (grafikW - gap * (n + 1)) / n);

            // Garis grid horizontal
            g2.setStroke(new BasicStroke(0.5f));
            int gridCount = 5;
            for (int i = 0; i <= gridCount; i++) {
                int y = padTop + grafikH - (grafikH * i / gridCount);
                g2.setColor(BORDER);
                g2.drawLine(padLeft, y, padLeft + grafikW, y);

                // Label nilai Y
                int nilaiY = maxVal * i / gridCount;
                g2.setColor(TEXT_SUB);
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
                FontMetrics fm = g2.getFontMetrics();
                String lblY = String.valueOf(nilaiY);
                g2.drawString(lblY,
                        padLeft - fm.stringWidth(lblY) - 6,
                        y + fm.getAscent() / 2);
            }

            // Bar chart
            for (int i = 0; i < n; i++) {
                LocalDate tgl = keys.get(i);
                int val = data.get(tgl);

                int barH = (int) ((double) val / maxVal * grafikH);
                int x = padLeft + gap + i * (barW + gap);
                int y = padTop + grafikH - barH;

                // Bar dengan rounded top
                Color barColor = val == 0 ? new Color(51, 65, 85) : BIRU;
                g2.setColor(barColor);

                if (barH > 8) {
                    // Rounded top
                    g2.fill(new RoundRectangle2D.Float(x, y, barW, barH, 6, 6));
                    // Kotak bawah untuk tutup rounded bawah
                    g2.fillRect(x, y + barH / 2, barW, barH / 2);
                } else if (barH > 0) {
                    g2.fillRect(x, y, barW, barH);
                }

                // Nilai di atas bar
                if (val > 0) {
                    g2.setColor(TEXT_UTAMA);
                    g2.setFont(new Font("Segoe UI", Font.BOLD, 10));
                    FontMetrics fm = g2.getFontMetrics();
                    String valStr = String.valueOf(val);
                    g2.drawString(valStr,
                            x + (barW - fm.stringWidth(valStr)) / 2,
                            y - 4);
                }

                // Label tanggal X — tampilkan setiap N hari jika banyak
                int step = n <= 14 ? 1 : n <= 30 ? 2 : 5;
                if (i % step == 0) {
                    g2.setColor(TEXT_SUB);
                    g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
                    FontMetrics fm = g2.getFontMetrics();
                    String lblX = tgl.format(fmtLabel);
                    g2.drawString(lblX,
                            x + (barW - fm.stringWidth(lblX)) / 2,
                            padTop + grafikH + 16);
                }
            }

            // Garis sumbu X
            g2.setColor(BORDER);
            g2.setStroke(new BasicStroke(1f));
            g2.drawLine(padLeft, padTop + grafikH,
                    padLeft + grafikW, padTop + grafikH);
        }
    }
}
