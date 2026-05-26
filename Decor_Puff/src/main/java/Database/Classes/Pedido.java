/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Database.Classes;

import Database.Classes.Utils.Status_Pedido;
import java.time.*;
import java.sql.Date;

/**
 *
 * @author ender
 */
public class Pedido {
    
    public int ID;
    public LocalDateTime Data;
    private Date Data_SQL;
    public double Valor_Servico;
    public double Valor_Frete;
    public double Subtotal;
    public Status_Pedido Status;
    
    public Funcionario funcionario;
    public Cliente cliente;
    
    public Pedido(int id, Cliente c, Funcionario f, LocalDateTime data, double valor_servico, double valor_frete, double subtotal, Status_Pedido status)
    {
        this.ID = id;
        this.cliente = c;
        this.funcionario = f;
        this.Data = data;
        this.Valor_Servico = valor_servico;
        this.Valor_Frete = valor_frete;
        this.Subtotal = subtotal;
        this.Status = status;
        
        this.Data_SQL = Date.valueOf(this.Data.toLocalDate());
            
    }
    
    public Pedido(Cliente c, Funcionario f, LocalDateTime data, double valor_servico, double valor_frete, double subtotal, Status_Pedido status)
    {
        this.ID = 0;
        this.cliente = c;
        this.funcionario = f;
        this.Data = data;
        this.Valor_Servico = valor_servico;
        this.Valor_Frete = valor_frete;
        this.Subtotal = subtotal;
        this.Status = status;
        
        this.Data_SQL = Date.valueOf(this.Data.toLocalDate());
    }
    
        public Pedido(Cliente c, Funcionario f, java.sql.Date data, double valor_servico, double valor_frete, double subtotal, Status_Pedido status)
    {
        this.ID = 0;
        this.cliente = c;
        this.funcionario = f;
        this.Data = data.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        this.Valor_Servico = valor_servico;
        this.Valor_Frete = valor_frete;
        this.Subtotal = subtotal;
        this.Status = status;
        
        this.Data_SQL = data;
    }
    
    public Date getDateSQL()
    {
        return this.Data_SQL;
    }
    
    public String toString()
    {
        return String.format("Pedido(%d, %s, %s, %s, %.2f, %.2f, %.2f, %s)", 
                                this.ID,
                                this.cliente.Nome,
                                this.funcionario.Nome,
                                this.Data.toString(),
                                this.Valor_Servico,
                                this.Valor_Frete,
                                this.Subtotal,
                                this.Status
                                );
    }
}