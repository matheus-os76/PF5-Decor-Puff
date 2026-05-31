/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Database.Classes;

import java.sql.Date;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 *
 * @author ender
 */
public class LogSistema {
    
    public int ID;
    public String Acao;
    public LocalDateTime Data;
    private java.sql.Date Data_SQL;
    
    public Item item;
    public Funcionario funcionario;
    
    public LogSistema(int id, Item i, Funcionario f, String acao, LocalDateTime data)
    {
        this.ID = id;
        this.item = i;
        this.funcionario = f;
        this.Acao = acao;
        this.Data = data;
        
        this.Data_SQL = Date.valueOf(this.Data.toLocalDate());
    }
    
    public LogSistema(Item i, Funcionario f, String acao, LocalDateTime data)
    {
        this.ID = 0;
        this.item = i;
        this.funcionario = f;
        this.Acao = acao;
        this.Data = data;
        
        this.Data_SQL = Date.valueOf(this.Data.toLocalDate());
    }
    
    public LogSistema(int id, Item i, Funcionario f, String acao, java.sql.Date data)
    {
        this.ID = id;
        this.item = i;
        this.funcionario = f;
        this.Acao = acao;
        this.Data = data.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        
        this.Data_SQL = data;
    }
    
    public LogSistema(Item i, Funcionario f, String acao, java.sql.Date data)
    {
        this.ID = 0;
        this.item = i;
        this.funcionario = f;
        this.Acao = acao;
        this.Data = data.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        
        this.Data_SQL = data;
    }
    
    public Date getDateSQL()
    {
        return this.Data_SQL;
    }
    
    public String toString()
    {
        return String.format("LogSistema(%d, %s, %s, %s, %s)", 
                                this.ID,
                                this.Acao,
                                this.Data.toString(),
                                this.item.Nome,
                                this.funcionario.Nome
                                );
    }
}
