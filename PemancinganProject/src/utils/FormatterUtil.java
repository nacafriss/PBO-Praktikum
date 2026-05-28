/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package utils;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 *
 * @author rei
 */
public class FormatterUtil {

    public static String formatRupiah(BigDecimal nominal) {
        if (nominal == null) {
            return "Rp 0";
        }
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        
        return formatRupiah.format(nominal).replace(",00", "");
    }

    public static String formatTanggalWaktu(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "-";
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        return dateTime.format(formatter);
    }
}
