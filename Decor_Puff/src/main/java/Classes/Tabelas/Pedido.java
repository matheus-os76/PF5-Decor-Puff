package Classes.Tabelas;


import Classes.DAO.ItemDAO;
import Classes.Status_Pedido;

import java.time.LocalDateTime;
import java.sql.Timestamp;
import java.util.ArrayList;

import Utils.Conversor_Datetime;

public class Pedido {
    
    public int ID;
    public Cliente Cliente;
    public Funcionario Funcionario;
    public LocalDateTime Data;
    public double Valor_Servico;
    public double Valor_Frete;
    public double Subtotal;
    public double Total;
    public Status_Pedido Status;
    public ArrayList<ItemDAO> Itens;
    
    private Timestamp Data_SQL;
    
    
    // LocalDateTime            
    public Pedido(int id, Cliente c, Funcionario f, LocalDateTime data, double valor_servico, double valor_frete, Status_Pedido status, ArrayList<ItemDAO> itens) throws Exception
    {
        this.ID = id;
        this.Cliente = c;
        this.Funcionario = f;
        
        if (data.isAfter(LocalDateTime.now()))
        {
            throw new Exception("Data inválida");
        }
        
        this.Data = data;
        this.Valor_Servico = valor_servico;
        this.Valor_Frete = valor_frete;
        
        if (valor_servico <= 0 || valor_frete <= 0)
        {
            throw new Exception("Valores negativos");
        }
        
        this.Subtotal = 0;
        
        for (int i = 0; i < itens.size(); i++)
        {
            this.Subtotal += itens.get(i).Subtotal;
        }
        
        this.Total = this.Subtotal + valor_servico + valor_frete;
        this.Status = status;
        this.Itens = itens;
        
        this.Data_SQL = Conversor_Datetime.to_SQLTimestamp(data);
    }
    
    public Pedido(int id, Cliente c, Funcionario f, LocalDateTime data, double valor_servico, double valor_frete, Status_Pedido status) throws Exception
    {
        this.ID = id;
        this.Cliente = c;
        this.Funcionario = f;
        this.Data = data;
        
        if (data.isAfter(LocalDateTime.now()))
        {
            throw new Exception("Data inválida");
        }
        
        this.Valor_Servico = valor_servico;
        this.Valor_Frete = valor_frete;
        
        if (valor_servico <= 0 || valor_frete <= 0)
        {
            throw new Exception("Valores negativos");
        }
        
        this.Subtotal = 0;
        this.Total = valor_servico + valor_frete;
        this.Status = status;
        this.Itens = new ArrayList<ItemDAO>();
        
        this.Data_SQL = Conversor_Datetime.to_SQLTimestamp(data);
    }
    
    // Timestamp
    public Pedido(int id, Cliente c, Funcionario f, Timestamp data, double valor_servico, double valor_frete, Status_Pedido status, ArrayList<ItemDAO> itens) throws Exception
    {
        this.Data_SQL = data;
        
        this.ID = id;
        this.Cliente = c;
        this.Funcionario = f;
        this.Data = Conversor_Datetime.to_LocalDateTime(this.Data_SQL);
        
        if (this.Data.isAfter(LocalDateTime.now()))
        {
            throw new Exception("Data inválida");
        }
        
        this.Valor_Servico = valor_servico;
        this.Valor_Frete = valor_frete;
        
        if (valor_servico <= 0 || valor_frete <= 0)
        {
            throw new Exception("Valores negativos");
        }
        
        this.Subtotal = 0;
        
        for (int i = 0; i < itens.size(); i++)
        {
            this.Subtotal += itens.get(i).Subtotal;
        }
        
        this.Total = this.Subtotal + valor_servico + valor_frete;
        this.Status = status;
        this.Itens = itens;
    }
    
    public Pedido(int id, Cliente c, Funcionario f, Timestamp data, double valor_servico, double valor_frete, Status_Pedido status) throws Exception
    {
        this.Data_SQL = data;
        
        this.ID = id;
        this.Cliente = c;
        this.Funcionario = f;
        this.Data = Conversor_Datetime.to_LocalDateTime(this.Data_SQL);
        
        if (this.Data.isAfter(LocalDateTime.now()))
        {
            throw new Exception("Data inválida");
        }
        
        this.Valor_Servico = valor_servico;
        this.Valor_Frete = valor_frete;
        
        if (valor_servico <= 0 || valor_frete <= 0)
        {
            throw new Exception("Valores negativos");
        }
        
        this.Subtotal = 0;
        this.Total = valor_servico + valor_frete;
        this.Status = status;
        this.Itens = new ArrayList<ItemDAO>();
    }
    
    
    public Timestamp getDateSQL()
    {
        return this.Data_SQL;
    }
    
    @Override
    public String toString()
    {
        return String.format("Pedido(%d, %s, %s, %s, %.2f, %.2f, %.2f, %.2f, %s, Itens[%d])", 
                                this.ID,
                                this.Cliente.Nome,
                                this.Funcionario.Nome,
                                this.Data.toString(),
                                this.Valor_Servico,
                                this.Valor_Frete,
                                this.Subtotal,
                                this.Total,
                                this.Status,
                                this.Itens.size()
                                );
    }
}