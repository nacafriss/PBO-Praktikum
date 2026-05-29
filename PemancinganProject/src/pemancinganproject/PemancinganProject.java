/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package pemancinganproject;

/**
 *
 * @author rei
 */
import javax.swing.*;
import java.awt.*;
import view.DashboardView;

public class PemancinganProject {

    public static void main(String[] args) {

        // Set Look and Feel ke sistem OS agar tampilan lebih native
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Fallback ke default Swing jika gagal
            System.err.println("Gagal set LookAndFeel: " + e.getMessage());
        }

        // Kustomisasi global komponen Swing
        terapkanTemaGlobal();

        // Jalankan UI di Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            DashboardView dashboard = new DashboardView();
            dashboard.setVisible(true);
        });
    }

    /**
     * Kustomisasi tampilan global semua komponen Swing
     * sebelum frame pertama dibuat.
     */
    private static void terapkanTemaGlobal() {

        // Font default semua komponen
        Font fontDefault = new Font("Segoe UI", Font.PLAIN, 13);

        UIManager.put("Button.font",            fontDefault);
        UIManager.put("Label.font",             fontDefault);
        UIManager.put("TextField.font",         fontDefault);
        UIManager.put("ComboBox.font",          fontDefault);
        UIManager.put("Table.font",             fontDefault);
        UIManager.put("TableHeader.font",       new Font("Segoe UI", Font.BOLD, 13));
        UIManager.put("TitledBorder.font",      new Font("Segoe UI", Font.BOLD, 12));
        UIManager.put("OptionPane.messageFont", fontDefault);

        // Warna tombol default
        UIManager.put("Button.background",      new Color(52, 152, 219));
        UIManager.put("Button.foreground",      Color.WHITE);

        // Warna fokus field (hilangkan border fokus yang jelek)
        UIManager.put("TextField.caretForeground", new Color(26, 82, 118));

        // Warna selection tabel
        UIManager.put("Table.selectionBackground", new Color(52, 152, 219));
        UIManager.put("Table.selectionForeground", Color.WHITE);

        // Warna scrollbar
        UIManager.put("ScrollBar.thumb",        new Color(180, 180, 180));
        UIManager.put("ScrollBar.track",        new Color(230, 230, 230));

        // JOptionPane tombol
        UIManager.put("OptionPane.yesButtonText",    "Ya");
        UIManager.put("OptionPane.noButtonText",     "Tidak");
        UIManager.put("OptionPane.cancelButtonText", "Batal");
        UIManager.put("OptionPane.okButtonText",     "OK");
    }
}
