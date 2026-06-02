package Classes.DAO;

import Classes.Status_Pedido;
import Classes.Tabelas.Cliente;
import Classes.Tabelas.Funcionario;

import java.time.LocalDateTime;
import java.sql.Timestamp;

import Utils.Conversor_Datetime;

public class PedidoDAO {
    
    public int ID;
    public Cliente Cliente;
    public Funcionario Funcionario;
    public LocalDateTime Data;
    public double Valor_Servico;
    public double Valor_Frete;
    public double Subtotal;
    public double Total;
    public Status_Pedido Status;
    
    private Timestamp Data_SQL;
    
    
    // LocalDateTime            
    public PedidoDAO(int id, Cliente c, Funcionario f, LocalDateTime data, double valor_servico, double valor_frete, double subtotal, double total, Status_Pedido status) throws Exception
    {
        this.ID = id;
        this.Cliente = c;
        this.Funcionario = f;
        
        if (data != null && data.isAfter(LocalDateTime.now()))
        {
            throw new Exception("Data inválida");
        }
        
        if (valor_servico <= 0 || valor_frete <= 0 || subtotal < 0)
        {
            throw new Exception("Valores negativos");
        }
        
        this.Data = data;
        this.Valor_Servico = valor_servico;
        this.Valor_Frete = valor_frete;
        this.Subtotal = subtotal;
        this.Total = subtotal + valor_servico + valor_frete;
        this.Status = status;
        
        this.Data_SQL = (data == null) ? null : Conversor_Datetime.to_SQLTimestamp(data);
    }
    
    // Timestamp
    public PedidoDAO(int id, Cliente c, Funcionario f, Timestamp data, double valor_servico, double valor_frete, double subtotal, double total, Status_Pedido status) throws Exception
    {
        this.ID = id;
        this.Cliente = c;
        this.Funcionario = f;
        this.Data = (data == null) ? null : Conversor_Datetime.to_LocalDateTime(data);;
        
        if (this.Data != null && this.Data.isAfter(LocalDateTime.now()))
        {
            throw new Exception("Data inválida");
        }
        
        if (valor_servico <= 0 || valor_frete <= 0 || subtotal < 0)
        {
            throw new Exception("Valores negativos");
        }
        
        this.Valor_Servico = valor_servico;
        this.Valor_Frete = valor_frete;
        this.Subtotal = subtotal;
        this.Total = subtotal + valor_servico + valor_frete;
        this.Status = status;
        
        this.Data_SQL = data;
    }
    
    
    public Timestamp getDateSQL()
    {
        return this.Data_SQL;
    }
    
    @Override
    public String toString()
    {
        return String.format("Pedido(%d, %s, %s, %s, %.2f, %.2f, %.2f, %.2f, %s)", 
                                this.ID,
                                this.Cliente.Nome,
                                this.Funcionario.Nome,
                                this.Data.toString(),
                                this.Valor_Servico,
                                this.Valor_Frete,
                                this.Subtotal,
                                this.Total,
                                this.Status
                                );
    }
}