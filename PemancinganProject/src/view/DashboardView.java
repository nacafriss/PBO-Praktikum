/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view;

/**
 *
 * @author rei
 */
import javax.swing.*;
import java.awt.*;

public class DashboardView extends JFrame {

    private JPanel sidebarPanel;
    private JPanel contentPanel;
    private CardLayout cardLayout;

    private LapakPanel lapakPanel;

    // Warna tema
    private static final Color SIDEBAR_BG    = new Color(26, 82, 118);
    private static final Color SIDEBAR_HOVER = new Color(52, 152, 219);
    private static final Color SIDEBAR_AKTIF = new Color(21, 67, 96);
    private static final Color CONTENT_BG    = new Color(240, 244, 248);

    public DashboardView() {
        initFrame();
        initSidebar();
        initContent();
        tampilkanPanel("LAPAK");
    }

    // =========================================================
    // INISIALISASI FRAME
    // =========================================================

    private void initFrame() {
        setTitle("Sistem Manajemen Pemancingan");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 700);
        setMinimumSize(new Dimension(900, 600));
        setLocationRelativeTo(null); // Tengah layar
        setLayout(new BorderLayout());
    }

    // =========================================================
    // SIDEBAR
    // =========================================================

    private void initSidebar() {
        sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setBackground(SIDEBAR_BG);
        sidebarPanel.setPreferredSize(new Dimension(200, getHeight()));

        // Logo / Judul Aplikasi
        JPanel logoPanel = new JPanel(new BorderLayout());
        logoPanel.setBackground(SIDEBAR_AKTIF);
        logoPanel.setMaximumSize(new Dimension(200, 70));
        logoPanel.setPreferredSize(new Dimension(200, 70));
        logoPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel logoLabel = new JLabel("<html><center><br>Pemancingan</center></html>");
        logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        logoLabel.setForeground(Color.WHITE);
        logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        logoPanel.add(logoLabel, BorderLayout.CENTER);

        sidebarPanel.add(logoPanel);
        sidebarPanel.add(Box.createVerticalStrut(10));

        // Menu Items
        sidebarPanel.add(buatMenuButton("Monitor Lapak",   "LAPAK"));
        sidebarPanel.add(buatMenuButton("Pelanggan",        "PELANGGAN"));
        sidebarPanel.add(buatMenuButton("Riwayat Transaksi","TRANSAKSI"));
        sidebarPanel.add(buatMenuButton("Laporan",          "LAPORAN"));

        // Spacer dorong tombol keluar ke bawah
        sidebarPanel.add(Box.createVerticalGlue());

        // Tombol Keluar di bagian bawah sidebar
        JButton keluarBtn = buatMenuButton("Keluar", "KELUAR");
        keluarBtn.setBackground(new Color(169, 50, 38));
        sidebarPanel.add(keluarBtn);
        sidebarPanel.add(Box.createVerticalStrut(10));

        add(sidebarPanel, BorderLayout.WEST);
    }

    private JButton buatMenuButton(String teks, String panelKey) {
        JButton btn = new JButton(teks);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btn.setForeground(Color.WHITE);
        btn.setBackground(SIDEBAR_BG);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        btn.setMaximumSize(new Dimension(200, 50));
        btn.setPreferredSize(new Dimension(200, 50));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Efek hover
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (!btn.getBackground().equals(SIDEBAR_AKTIF)) {
                    btn.setBackground(SIDEBAR_HOVER);
                }
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                if (!btn.getBackground().equals(SIDEBAR_AKTIF)) {
                    btn.setBackground(SIDEBAR_BG);
                }
            }
        });

        btn.addActionListener(e -> {
            if (panelKey.equals("KELUAR")) {
                konfirmasiKeluar();
            } else {
                resetWarnaSemuaTombol();
                btn.setBackground(SIDEBAR_AKTIF);
                tampilkanPanel(panelKey);
            }
        });

        return btn;
    }

    private void resetWarnaSemuaTombol() {
        for (Component c : sidebarPanel.getComponents()) {
            if (c instanceof JButton) {
                JButton btn = (JButton) c;
                // Jangan reset tombol keluar
                if (!btn.getBackground().equals(new Color(169, 50, 38))) {
                    btn.setBackground(SIDEBAR_BG);
                }
            }
        }
    }

    // =========================================================
    // CONTENT AREA (CardLayout)
    // =========================================================

    private void initContent() {
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(CONTENT_BG);

        // Panel Lapak
        lapakPanel = new LapakPanel();
        contentPanel.add(lapakPanel, "LAPAK");

        // Panel Pelanggan (placeholder)
        contentPanel.add(buatPlaceholder("Manajemen Pelanggan\n(Segera Hadir)"), "PELANGGAN");

        // Panel Transaksi (placeholder)
        contentPanel.add(buatPlaceholder("Riwayat Transaksi\n(Segera Hadir)"), "TRANSAKSI");

        // Panel Laporan (placeholder)
        contentPanel.add(buatPlaceholder("Laporan & Statistik\n(Segera Hadir)"), "LAPORAN");

        add(contentPanel, BorderLayout.CENTER);
    }

    private JPanel buatPlaceholder(String pesan) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(CONTENT_BG);

        JLabel label = new JLabel("<html><center>" +
                pesan.replace("\n", "<br>") +
                "</center></html>");
        label.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        label.setForeground(new Color(150, 150, 150));
        label.setHorizontalAlignment(SwingConstants.CENTER);

        panel.add(label);
        return panel;
    }

    // =========================================================
    // NAVIGASI
    // =========================================================

    private void tampilkanPanel(String key) {
        cardLayout.show(contentPanel, key);

        // Refresh LapakPanel setiap kali ditampilkan
        if (key.equals("LAPAK") && lapakPanel != null) {
            lapakPanel.refresh();
        }
    }

    // =========================================================
    // KONFIRMASI KELUAR
    // =========================================================

    private void konfirmasiKeluar() {
        int pilihan = JOptionPane.showConfirmDialog(
                this,
                "Yakin ingin keluar dari aplikasi?",
                "Konfirmasi Keluar",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );
        if (pilihan == JOptionPane.YES_OPTION) {
            dispose();
            System.exit(0);
        }
    }
}