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
import controller.TransaksiController;
import model.Lapak;
import model.Transaksi;
import thread.LapakTimerThread;
import utils.FormatterUtil;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LapakPanel extends JPanel implements LapakTimerThread.LapakTimerListener {

    private LapakController lapakController;
    private TransaksiController transaksiController;

    // Map untuk menyimpan timer per lapak (key: lapakId)
    private Map<Integer, LapakTimerThread> timerMap;

    // Map untuk menyimpan panel kartu per lapak (key: lapakId)
    private Map<Integer, JPanel> kartuMap;

    // Map untuk menyimpan label timer per lapak (key: lapakId)
    private Map<Integer, JLabel> timerLabelMap;

    // Panel utama yang menampung semua kartu lapak
    private JPanel gridPanel;

    public LapakPanel() {
        this.lapakController = new LapakController();
        this.transaksiController = new TransaksiController();
        this.timerMap = new HashMap<>();
        this.kartuMap = new HashMap<>();
        this.timerLabelMap = new HashMap<>();

        initUI();
        muatSemuaLapak();
    }

    // =========================================================
    // INISIALISASI UI
    // =========================================================

    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(new Color(240, 244, 248));

        // Header
        JPanel headerPanel = buatHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // Grid lapak (scrollable)
        gridPanel = new JPanel(new GridLayout(0, 3, 15, 15));
        gridPanel.setBackground(new Color(240, 244, 248));
        gridPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JScrollPane scrollPane = new JScrollPane(gridPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel buatHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(26, 82, 118));
        panel.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));

        JLabel titleLabel = new JLabel("Monitor Lapak");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);

        JButton refreshButton = new JButton("↺ Refresh");
        refreshButton.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        refreshButton.setBackground(new Color(52, 152, 219));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setFocusPainted(false);
        refreshButton.setBorderPainted(false);
        refreshButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        refreshButton.addActionListener(e -> refresh());

        panel.add(titleLabel, BorderLayout.WEST);
        panel.add(refreshButton, BorderLayout.EAST);

        return panel;
    }

    // =========================================================
    // LOAD DATA LAPAK
    // =========================================================

    private void muatSemuaLapak() {
        gridPanel.removeAll();
        kartuMap.clear();
        timerLabelMap.clear();

        // Hentikan semua timer yang sedang berjalan sebelum reload
        for (LapakTimerThread timer : timerMap.values()) {
            timer.hentikan();
        }
        timerMap.clear();

        List<Lapak> daftarLapak = lapakController.semuaLapak();

        if (daftarLapak == null || daftarLapak.isEmpty()) {
            JLabel kosongLabel = new JLabel("Belum ada lapak terdaftar.", SwingConstants.CENTER);
            kosongLabel.setFont(new Font("Segoe UI", Font.ITALIC, 14));
            kosongLabel.setForeground(Color.GRAY);
            gridPanel.add(kosongLabel);
        } else {
            for (Lapak lapak : daftarLapak) {
                JPanel kartu = buatKartuLapak(lapak);
                kartuMap.put(lapak.getId(), kartu);
                gridPanel.add(kartu);

                // Jika lapak sedang TERISI, cari transaksi aktif dan jalankan timer
                if (lapak.getStatus().equals("TERISI")) {
                    Transaksi transaksiAktif = lapakController.getTransaksiAktifByLapak(lapak.getId());
                    if (transaksiAktif != null) {
                        mulaiTimer(lapak, transaksiAktif);
                    }
                }
            }
        }

        gridPanel.revalidate();
        gridPanel.repaint();
    }

    // =========================================================
    // KARTU LAPAK
    // =========================================================

    private JPanel buatKartuLapak(Lapak lapak) {
        JPanel kartu = new JPanel();
        kartu.setLayout(new BoxLayout(kartu, BoxLayout.Y_AXIS));
        kartu.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(warnaBorderStatus(lapak), 2),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));
        kartu.setBackground(warnaBgStatus(lapak));
        kartu.setPreferredSize(new Dimension(200, 220));

        // Label nama lapak
        JLabel namaLabel = new JLabel(lapak.getNamaLapak());
        namaLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        namaLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Label jenis kolam
        JLabel jenisLabel = new JLabel("[" + lapak.getJenisKolam() + "]");
        jenisLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        jenisLabel.setForeground(new Color(100, 100, 100));
        jenisLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Label status
        JLabel statusLabel = new JLabel(lapak.getStatus());
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        statusLabel.setForeground(warnaTextStatus(lapak));
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Label timer (default kosong)
        JLabel timerLabel = new JLabel("--:--:--");
        timerLabel.setFont(new Font("Consolas", Font.BOLD, 18));
        timerLabel.setForeground(new Color(52, 73, 94));
        timerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        timerLabelMap.put(lapak.getId(), timerLabel);

        // Tombol aksi
        JPanel tombolPanel = buatTombolAksi(lapak);
        tombolPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        kartu.add(namaLabel);
        kartu.add(Box.createVerticalStrut(4));
        kartu.add(jenisLabel);
        kartu.add(Box.createVerticalStrut(8));
        kartu.add(statusLabel);
        kartu.add(Box.createVerticalStrut(6));
        kartu.add(timerLabel);
        kartu.add(Box.createVerticalGlue());
        kartu.add(tombolPanel);

        return kartu;
    }

    private JPanel buatTombolAksi(Lapak lapak) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 0));
        panel.setOpaque(false);

        if (!lapak.isAktif()) {
            JLabel nonaktifLabel = new JLabel("NONAKTIF");
            nonaktifLabel.setFont(new Font("Segoe UI", Font.ITALIC, 11));
            nonaktifLabel.setForeground(Color.GRAY);
            panel.add(nonaktifLabel);
            return panel;
        }

        switch (lapak.getStatus()) {
            case "KOSONG":
                JButton checkinBtn = buatTombol("Check-in", new Color(39, 174, 96));
                checkinBtn.addActionListener(e -> bukaCheckinDialog(lapak));
                panel.add(checkinBtn);
                break;

            case "TERISI":
                JButton checkoutBtn = buatTombol("Checkout", new Color(192, 57, 43));
                checkoutBtn.addActionListener(e -> bukaCheckoutDialog(lapak));
                panel.add(checkoutBtn);
                break;

            default:
                break;
        }

        return panel;
    }

    private JButton buatTombol(String teks, Color warna) {
        JButton btn = new JButton(teks);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(warna);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(100, 30));
        return btn;
    }

    // =========================================================
    // WARNA BERDASARKAN STATUS
    // =========================================================

    private Color warnaBgStatus(Lapak lapak) {
        if (!lapak.isAktif()) return new Color(220, 220, 220);
        switch (lapak.getStatus()) {
            case "KOSONG":  return new Color(232, 245, 233);
            case "TERISI":  return new Color(255, 243, 224);
            default:        return Color.WHITE;
        }
    }

    private Color warnaBorderStatus(Lapak lapak) {
        if (!lapak.isAktif()) return Color.GRAY;
        switch (lapak.getStatus()) {
            case "KOSONG":  return new Color(39, 174, 96);
            case "TERISI":  return new Color(230, 126, 34);
            default:        return Color.LIGHT_GRAY;
        }
    }

    private Color warnaTextStatus(Lapak lapak) {
        if (!lapak.isAktif()) return Color.GRAY;
        switch (lapak.getStatus()) {
            case "KOSONG":  return new Color(39, 174, 96);
            case "TERISI":  return new Color(230, 126, 34);
            default:        return Color.DARK_GRAY;
        }
    }

    // =========================================================
    // DIALOG
    // =========================================================

    private void bukaCheckinDialog(Lapak lapak) {
        CheckinDialog dialog = new CheckinDialog(
                (Frame) SwingUtilities.getWindowAncestor(this),
                lapak,
                lapakController
        );
        dialog.setVisible(true);

        // Setelah dialog ditutup, cek apakah checkin berhasil
        if (dialog.isCheckinBerhasil()) {
            Transaksi transaksiAktif = lapakController.getTransaksiAktifByLapak(lapak.getId());
            if (transaksiAktif != null) {
                lapak.setStatus("TERISI");
                mulaiTimer(lapak, transaksiAktif);
                perbaruiKartu(lapak);
            }
        }
    }

    private void bukaCheckoutDialog(Lapak lapak) {
        Transaksi transaksiAktif = lapakController.getTransaksiAktifByLapak(lapak.getId());
        if (transaksiAktif == null) {
            JOptionPane.showMessageDialog(this,
                    "Tidak ditemukan transaksi aktif untuk lapak ini.",
                    "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        CheckoutDialog dialog = new CheckoutDialog(
                (Frame) SwingUtilities.getWindowAncestor(this),
                lapak,
                transaksiAktif,
                transaksiController
        );
        dialog.setVisible(true);

        if (dialog.isCheckoutBerhasil()) {
            hentikanTimer(lapak.getId());
            lapak.setStatus("KOSONG");
            perbaruiKartu(lapak);
        }
    }

    // =========================================================
    // TIMER MANAGEMENT
    // =========================================================

    private void mulaiTimer(Lapak lapak, Transaksi transaksi) {
        // Hentikan timer lama jika ada
        hentikanTimer(lapak.getId());

        LapakTimerThread timer = new LapakTimerThread(lapak, transaksi, this);
        timerMap.put(lapak.getId(), timer);
        timer.start();
    }

    private void hentikanTimer(int lapakId) {
        LapakTimerThread timer = timerMap.get(lapakId);
        if (timer != null) {
            timer.hentikan();
            timerMap.remove(lapakId);
        }
    }

    // =========================================================
    // CALLBACK DARI TIMER THREAD
    // =========================================================

    @Override
    public void onTimerUpdate(int lapakId, String waktuBerjalan, boolean overtime) {
        // Update UI harus di EDT (Event Dispatch Thread)
        SwingUtilities.invokeLater(() -> {
            JLabel timerLabel = timerLabelMap.get(lapakId);
            if (timerLabel != null) {
                timerLabel.setText(waktuBerjalan);
                timerLabel.setForeground(overtime
                        ? new Color(192, 57, 43)   // Merah jika overtime
                        : new Color(52, 73, 94));   // Normal
            }
        });
    }

    @Override
    public void onTimerSelesai(int lapakId) {
        SwingUtilities.invokeLater(() -> {
            JLabel timerLabel = timerLabelMap.get(lapakId);
            if (timerLabel != null) {
                timerLabel.setText("--:--:--");
            }
        });
    }

    // =========================================================
    // HELPER
    // =========================================================

    /**
     * Rebuild kartu lapak tertentu setelah ada perubahan status.
     */
    private void perbaruiKartu(Lapak lapak) {
        SwingUtilities.invokeLater(() -> {
            JPanel kartuLama = kartuMap.get(lapak.getId());
            if (kartuLama != null) {
                int index = getComponentIndex(kartuLama);
                if (index >= 0) {
                    JPanel kartuBaru = buatKartuLapak(lapak);
                    kartuMap.put(lapak.getId(), kartuBaru);
                    timerLabelMap.put(lapak.getId(),
                            (JLabel) cariKomponenTimer(kartuBaru));
                    gridPanel.remove(index);
                    gridPanel.add(kartuBaru, index);
                    gridPanel.revalidate();
                    gridPanel.repaint();
                }
            }
        });
    }

    private int getComponentIndex(Component comp) {
        for (int i = 0; i < gridPanel.getComponentCount(); i++) {
            if (gridPanel.getComponent(i) == comp) return i;
        }
        return -1;
    }

    private Component cariKomponenTimer(JPanel kartu) {
        for (Component c : kartu.getComponents()) {
            if (c instanceof JLabel) {
                JLabel label = (JLabel) c;
                if (label.getFont().getName().equals("Consolas")) {
                    return label;
                }
            }
        }
        return null;
    }

    /**
     * Refresh seluruh panel (reload dari DB).
     */
    public void refresh() {
        muatSemuaLapak();
    }
}
