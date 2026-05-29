/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package thread;

/**
 *
 * @author rei
 */

import model.Lapak;
import model.Transaksi;
import java.time.LocalDateTime;
import java.time.Duration;

public class LapakTimerThread extends Thread {

    private Lapak lapak;
    private Transaksi transaksi;
    private LapakTimerListener listener;
    private volatile boolean berjalan;

    /**
     * Interface callback ke UI (LapakPanel) agar update bisa
     * dilakukan dari thread ini tanpa LapakPanel perlu polling manual.
     */
    public interface LapakTimerListener {
        void onTimerUpdate(int lapakId, String waktuBerjalan, boolean overtime);
        void onTimerSelesai(int lapakId);
    }

    public LapakTimerThread(Lapak lapak, Transaksi transaksi, LapakTimerListener listener) {
        this.lapak = lapak;
        this.transaksi = transaksi;
        this.listener = listener;
        this.berjalan = true;
        // Jadikan daemon thread agar otomatis mati saat aplikasi ditutup
        this.setDaemon(true);
        this.setName("TimerLapak-" + lapak.getId());
    }

    @Override
    public void run() {
        while (berjalan) {
            try {
                Thread.sleep(1000); // Update setiap 1 detik

                if (!berjalan) break;

                LocalDateTime checkin = transaksi.getWaktuCheckin();
                LocalDateTime sekarang = LocalDateTime.now();

                // Hitung durasi berjalan sejak check-in
                long totalDetikBerjalan = Duration.between(checkin, sekarang).getSeconds();

                String waktuFormatted = formatDurasi(totalDetikBerjalan);

                // Cek apakah sudah overtime (durasi pesan terlewati)
                boolean overtime = false;
                if (transaksi.getDurasiPesanMenit() != null && transaksi.getDurasiPesanMenit() > 0) {
                    long batasMenit = transaksi.getDurasiPesanMenit();
                    long menitBerjalan = totalDetikBerjalan / 60;
                    overtime = menitBerjalan >= batasMenit;
                }

                // Kirim update ke listener (LapakPanel)
                if (listener != null) {
                    listener.onTimerUpdate(lapak.getId(), waktuFormatted, overtime);
                }

                // Jika overtime, beri notifikasi sekali lalu tetap jalan
                // (kasir tetap harus checkout manual)

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                berjalan = false;
            }
        }

        // Beritahu listener bahwa timer ini sudah dihentikan
        if (listener != null) {
            listener.onTimerSelesai(lapak.getId());
        }
    }

    /**
     * Hentikan timer ini (dipanggil saat checkout atau lapak dikosongkan).
     */
    public void hentikan() {
        this.berjalan = false;
        this.interrupt();
    }

    /**
     * Cek apakah timer sedang aktif berjalan.
     */
    public boolean isBerjalan() {
        return berjalan;
    }

    public int getLapakId() {
        return lapak.getId();
    }

    /**
     * Format total detik menjadi string HH:MM:SS.
     */
    private String formatDurasi(long totalDetik) {
        long jam = totalDetik / 3600;
        long menit = (totalDetik % 3600) / 60;
        long detik = totalDetik % 60;
        return String.format("%02d:%02d:%02d", jam, menit, detik);
    }
}
