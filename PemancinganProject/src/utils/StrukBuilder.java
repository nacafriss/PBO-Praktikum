/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package utils;

/**
 *
 * @author user
 */
import model.DetailTangkapan;
import model.Lapak;
import model.Transaksi;

import java.math.BigDecimal;
import java.util.List;

public class StrukBuilder {

    // Private constructor — semua method static, tidak perlu instantiate
    private StrukBuilder() {}

    /**
     * Build struk checkout standar dalam format teks.
     * Ditampilkan di CheckoutDialog dan bisa dipakai cetak struk nanti.
     */
    public static String buildStrukCheckout(Transaksi transaksi,
                                             Lapak lapak,
                                             List<DetailTangkapan> listTangkapan,
                                             BigDecimal biayaSewa,
                                             BigDecimal subtotalIkan,
                                             BigDecimal diskon,
                                             BigDecimal totalBiaya) {

        StringBuilder sb = new StringBuilder();
        sb.append("===== STRUK CHECKOUT =====\n");
        sb.append("Lapak    : ").append(lapak.getNamaLapak()).append("\n");
        sb.append("Pelanggan: ").append(transaksi.getNamaPelanggan()).append("\n");
        sb.append("Posisi   : ").append(transaksi.getPosisi()).append("\n");
        sb.append("Check-in : ").append(
                FormatterUtil.formatTanggalWaktu(transaksi.getWaktuCheckin())).append("\n");
        sb.append("Check-out: ").append(
                FormatterUtil.formatTanggalWaktu(transaksi.getWaktuCheckout())).append("\n");
        sb.append("--------------------------\n");

        // Detail tangkapan — hanya muncul untuk kolam KILOAN
        if (listTangkapan != null && !listTangkapan.isEmpty()) {
            sb.append("Detail Tangkapan:\n");
            for (DetailTangkapan d : listTangkapan) {
                sb.append("  ").append(d.getNamaIkan())
                        .append(" ").append(d.getBeratKg()).append(" kg")
                        .append(" x ").append(FormatterUtil.formatRupiah(d.getHargaPerKg()))
                        .append(" = ").append(FormatterUtil.formatRupiah(d.getSubtotal()))
                        .append("\n");
            }
            sb.append("--------------------------\n");
        }

        if (biayaSewa.compareTo(BigDecimal.ZERO) > 0) {
            sb.append("Biaya Sewa  : ")
              .append(FormatterUtil.formatRupiah(biayaSewa)).append("\n");
        }
        if (subtotalIkan.compareTo(BigDecimal.ZERO) > 0) {
            sb.append("Subtotal Ikan: ")
              .append(FormatterUtil.formatRupiah(subtotalIkan)).append("\n");
        }
        if (diskon.compareTo(BigDecimal.ZERO) > 0) {
            sb.append("Diskon      : -")
              .append(FormatterUtil.formatRupiah(diskon)).append("\n");
        }

        sb.append("==========================\n");
        sb.append("TOTAL : ").append(FormatterUtil.formatRupiah(totalBiaya)).append("\n");
        sb.append("==========================");

        return sb.toString();
    }

    /**
     * Build struk ringkas untuk notifikasi atau log.
     * Contoh penggunaan berbeda format dari method di atas.
     */
    public static String buildStrukRingkas(Transaksi transaksi, BigDecimal totalBiaya) {
        return String.format(
                "Transaksi #%d | %s | Total: %s",
                transaksi.getId(),
                transaksi.getNamaPelanggan(),
                FormatterUtil.formatRupiah(totalBiaya)
        );
    }
}
