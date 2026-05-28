/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package dao;

import java.util.List;
import model.Pelanggan;

/**
 *
 * @author rei
 */
public interface PelangganDaoInterface {
    
    void insert(Pelanggan pelanggan);
    
    void update(Pelanggan pelanggan);
    
    void delete(int id);
    
    List<Pelanggan> getAll();
    
}
