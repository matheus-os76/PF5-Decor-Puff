package Classes.Tabelas;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import Utils.Conversor_Datetime;

public class LogSistema {
    
    public int ID;
    public Item Item;
    public Funcionario Funcionario;
    public String Acao;
    public LocalDateTime Data;
    
    private Timestamp Data_SQL;
   
    
    // LocalDateTime
    public LogSistema(int id, Item i, Funcionario f, String acao, LocalDateTime data)
    {
        this.ID = id;
        this.Item = i;
        this.Funcionario = f;
        this.Acao = acao;
        this.Data = data;
        
        this.Data_SQL = Conversor_Datetime.to_SQLTimestamp(data);
    }
    
    public LogSistema(int id, Funcionario f, String acao, LocalDateTime data)
    {
        this.ID = id;
        this.Item = null;
        this.Funcionario = f;
        this.Acao = acao;
        this.Data = data;
        
        this.Data_SQL = Conversor_Datetime.to_SQLTimestamp(data);
    }
    
    
    // Timestamp
    public LogSistema(int id, Item i, Funcionario f, String acao, java.sql.Timestamp data)
    {
        this.ID = id;
        this.Item = i;
        this.Funcionario = f;
        this.Acao = acao;
        this.Data = Conversor_Datetime.to_LocalDateTime(data);
        
        this.Data_SQL = data;
    }
    
    public LogSistema(int id, Funcionario f, String acao, java.sql.Timestamp data)
    {
        this.ID = id;
        this.Item = null;
        this.Funcionario = f;
        this.Acao = acao;
        this.Data = Conversor_Datetime.to_LocalDateTime(data);
        
        this.Data_SQL = data;
    }
    
    
    public Timestamp getDateSQL()
    {
        return this.Data_SQL;
    }
    
    public String toString()
    {
        return String.format("LogSistema(%d, %s, %s, %s, %s)", 
                                this.ID,
                                this.Acao,
                                this.Data.toString(),
                                this.Item.Nome,
                                this.Funcionario.Nome
                                );
    }
}
