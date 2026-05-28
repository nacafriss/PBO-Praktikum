/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package dao;

import java.util.List;
import model.Transaksi;
/**
 *
 * @author rei
 */
public interface TransaksiDaoInterface {
    
    void insert(Transaksi transaksi);
    
    void update(Transaksi transaksi);
    
    void delete(int id);
    
    List<Transaksi> getAll();
    
    
}
