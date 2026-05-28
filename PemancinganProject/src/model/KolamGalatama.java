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
public class KolamGalatama extends LayananKolam {

    public KolamGalatama() {
        super("GALATAMA");
    }

    @Override
    public BigDecimal hitungTagihan(int durasiMenit, BigDecimal hargaTiket, BigDecimal totalBeratKg) {
        
        if (hargaTiket == null) {
            return BigDecimal.ZERO;
        }
        
        return hargaTiket;
    }
}
