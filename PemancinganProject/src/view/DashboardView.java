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

    private static final Color SIDEBAR_BG    = new Color(15, 23, 42);
    private static final Color SIDEBAR_HOVER = new Color(30, 41, 59);
    private static final Color SIDEBAR_AKTIF = new Color(37, 99, 235);
    private static final Color CONTENT_BG    = new Color(15, 23, 42);

    public DashboardView() {
        initFrame();
        initSidebar();
        initContent();
        tampilkanPanel("LAPAK");
    }

    private void initFrame() {
        setTitle("Sistem Manajemen Pemancingan");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 700);
        setMinimumSize(new Dimension(900, 600));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        // Warna background frame
        getContentPane().setBackground(CONTENT_BG);
    }

    private void initSidebar() {
        sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setBackground(SIDEBAR_BG);
        sidebarPanel.setPreferredSize(new Dimension(190, getHeight()));

        // Logo
        JPanel logoPanel = new JPanel(new BorderLayout());
        logoPanel.setBackground(SIDEBAR_BG);
        logoPanel.setMaximumSize(new Dimension(190, 80));
        logoPanel.setPreferredSize(new Dimension(190, 80));
        logoPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 12, 20));

        JLabel logoNama = new JLabel("Pemancingan");
        logoNama.setFont(new Font("Segoe UI", Font.BOLD, 16));
        logoNama.setForeground(Color.WHITE);

        JLabel logoSub = new JLabel("sistem admin");
        logoSub.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        logoSub.setForeground(new Color(148, 163, 184));

        JPanel logoTeks = new JPanel();
        logoTeks.setLayout(new BoxLayout(logoTeks, BoxLayout.Y_AXIS));
        logoTeks.setOpaque(false);
        logoTeks.add(logoNama);
        logoTeks.add(logoSub);

        logoPanel.add(logoTeks, BorderLayout.CENTER);

        // Garis pemisah bawah logo
        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(30, 41, 59));
        sep.setMaximumSize(new Dimension(190, 1));

        sidebarPanel.add(logoPanel);
        sidebarPanel.add(sep);
        sidebarPanel.add(Box.createVerticalStrut(8));

        // Menu items dengan ikon
        sidebarPanel.add(buatMenuButton("Monitor Lapak", "LAPAK"));
        sidebarPanel.add(buatMenuButton("Pelanggan", "PELANGGAN"));
        sidebarPanel.add(buatMenuButton("Transaksi", "TRANSAKSI"));
        sidebarPanel.add(buatMenuButton("Laporan", "LAPORAN"));

        sidebarPanel.add(Box.createVerticalGlue());

        // Garis sebelum keluar
        JSeparator sep2 = new JSeparator();
        sep2.setForeground(new Color(30, 41, 59));
        sep2.setMaximumSize(new Dimension(190, 1));
        sidebarPanel.add(sep2);
        sidebarPanel.add(Box.createVerticalStrut(4));

        JButton keluarBtn = buatMenuButton("Keluar", "KELUAR");
        keluarBtn.setForeground(new Color(248, 113, 113));
        sidebarPanel.add(keluarBtn);
        sidebarPanel.add(Box.createVerticalStrut(12));

        add(sidebarPanel, BorderLayout.WEST);
    }

    private JButton buatMenuButton(String teks, String panelKey) {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 10));

        JButton btn = new JButton();
        btn.setLayout(new BorderLayout(10, 0));
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btn.setForeground(new Color(148, 163, 184)); // Selalu terlihat
        btn.setBackground(SIDEBAR_BG);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 14));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        btn.setPreferredSize(new Dimension(190, 42));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));


        // Teks
        JLabel lblTeks = new JLabel(teks);
        lblTeks.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblTeks.setForeground(new Color(148, 163, 184));

        btn.add(lblTeks, BorderLayout.CENTER);

        // Hover
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (!btn.getBackground().equals(SIDEBAR_AKTIF)) {
                    btn.setBackground(SIDEBAR_HOVER);
                    lblTeks.setForeground(Color.WHITE);
                }
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                if (!btn.getBackground().equals(SIDEBAR_AKTIF)) {
                    btn.setBackground(SIDEBAR_BG);
                    lblTeks.setForeground(new Color(148, 163, 184));
                }
            }
        });

        btn.addActionListener(e -> {
            if (panelKey.equals("KELUAR")) {
                konfirmasiKeluar();
            } else {
                resetWarnaSemuaTombol();
                btn.setBackground(SIDEBAR_AKTIF);
                lblTeks.setForeground(Color.WHITE);
                tampilkanPanel(panelKey);
            }
        });

        return btn;
    }

    private void resetWarnaSemuaTombol() {
        for (Component c : sidebarPanel.getComponents()) {
            if (c instanceof JButton) {
                JButton btn = (JButton) c;
                // Cari label di dalam button
                for (Component child : btn.getComponents()) {
                    if (child instanceof JLabel) {
                        ((JLabel) child).setForeground(new Color(148, 163, 184));
                    }
                }
                if (!btn.getForeground().equals(new Color(248, 113, 113))) {
                    btn.setBackground(SIDEBAR_BG);
                }
            }
        }
    }

    private void initContent() {
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(CONTENT_BG);

        lapakPanel = new LapakPanel();
        contentPanel.add(lapakPanel, "LAPAK");

        PelangganPanel pelangganPanel = new PelangganPanel();
        contentPanel.add(pelangganPanel, "PELANGGAN");

        RiwayatTransaksiPanel riwayatPanel = new RiwayatTransaksiPanel();
        contentPanel.add(riwayatPanel, "TRANSAKSI");

        LaporanPanel laporanPanel =  new LaporanPanel();
        contentPanel.add(laporanPanel, "LAPORAN");

        add(contentPanel, BorderLayout.CENTER);
    }

    private JPanel buatPlaceholder(String pesan) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(CONTENT_BG);

        JLabel label = new JLabel("<html><center>" +
                pesan.replace("\n", "<br>") +
                "</center></html>");
        label.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        label.setForeground(new Color(100, 116, 139));
        label.setHorizontalAlignment(SwingConstants.CENTER);

        panel.add(label);
        return panel;
    }

    private void tampilkanPanel(String key) {
        cardLayout.show(contentPanel, key);
        if (key.equals("LAPAK") && lapakPanel != null) {
            lapakPanel.refresh();
        }
    }

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