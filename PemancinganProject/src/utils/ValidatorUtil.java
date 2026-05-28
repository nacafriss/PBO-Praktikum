/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package utils;

import java.math.BigDecimal;

/**
 *
 * @author rei
 */
public class ValidatorUtil {

    public static boolean isNotNullOrEmpty(String str) {
        return str != null && !str.trim().isEmpty();
    }

    public static boolean isNumeric(String str) {
        if (!isNotNullOrEmpty(str)) {
            return false;
        }
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isDecimal(String str) {
        if (!isNotNullOrEmpty(str)) {
            return false;
        }
        try {
            new BigDecimal(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    public static boolean isNomorHpValid(String noHp) {
        if (!isNotNullOrEmpty(noHp)) {
            return false;
        }

        return noHp.matches("^[0-9]{10,13}$");
    }
}
