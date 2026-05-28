/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 *
 * @author rei
 */
public class KolamHarian extends LayananKolam {

    public KolamHarian() {
        super("HARIAN");
    }

    @Override
    public BigDecimal hitungTagihan(int durasiMenit, BigDecimal tarifPerJam, BigDecimal totalBeratKg) {
       
        if (durasiMenit <= 0) {
            return BigDecimal.ZERO;
        }

        
        BigDecimal durasiJam = new BigDecimal(durasiMenit)
                .divide(new BigDecimal("60"), 2, RoundingMode.HALF_UP);

        BigDecimal totalTagihan = tarifPerJam.multiply(durasiJam);

        return totalTagihan;
    }
}
