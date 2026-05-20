/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Database.Classes;

import Database.Classes.Utils.Status_Item;

/**
 *
 * @author ender
 */
public class Item {
    
    public int ID;
    public String Nome;
    public int Quantidade;
    public String Descricao;
    public double Valor;
    public Status_Item Status;
    
    public Item(int id, String nome, int quantidade, String descricao, double valor, Status_Item status) 
    {
        
        this.ID = id;
        this.Nome = nome;
        this.Quantidade = quantidade;
        this.Descricao = descricao;
        this.Valor = valor;
        this.Status = status;
    }
    
    public Item(String nome, int quantidade, String descricao, double valor, Status_Item status) 
    {
        
        this.ID = 0;
        this.Nome = nome;
        this.Quantidade = quantidade;
        this.Descricao = descricao;
        this.Valor = valor;
        this.Status = status;
    }
    
    public String toString()
    {
        return String.format("Item(%d, %s, %d, %s, %.2f, %s)", 
                                this.ID,
                                this.Nome,
                                this.Quantidade,
                                this.Descricao,
                                this.Valor,
                                this.Status
                                );
    }
}