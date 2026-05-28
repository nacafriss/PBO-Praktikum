/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.math.BigDecimal;

/**
 *
 * @author rei
 */
public class KolamKiloan extends LayananKolam {

    public KolamKiloan() {
        super("KILOAN");
    }
    
    @Override
    public BigDecimal hitungTagihan(int durasiMenit, BigDecimal tarifPerKg, BigDecimal totalBeratKg) {
        
        if (totalBeratKg == null || totalBeratKg.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        return totalBeratKg.multiply(tarifPerKg);
    }
}
