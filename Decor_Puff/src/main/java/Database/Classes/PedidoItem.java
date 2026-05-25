/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Database.Classes;

/**
 *
 * @author ender
 */
public class PedidoItem {
 
    public int ID;
    public int Quantidade;
    public double Subtotal;
    
    public Pedido pedido;
    public Item item;
    
    public PedidoItem(int id, Pedido p, Item i, int quantidade)
    {
        this.ID = id;
        this.pedido = p;
        this.item = i;
        
        this.Quantidade = quantidade;
        this.Subtotal = quantidade * item.Valor;
    }
    
    public PedidoItem(Pedido p, Item i, int quantidade)
    {
        this.ID = 0;
        this.pedido = p;
        this.item = i;
        
        this.Quantidade = quantidade;
        this.Subtotal = quantidade * item.Valor;
    }

    public String toString()
    {
        return String.format("PedidoItem(%d, %d, %s, %d, %.2f)", 
                                this.ID,
                                this.pedido.ID,
                                this.item.Nome,
                                this.Quantidade,
                                this.Subtotal
                );
    }
}
