/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Database;

import java.sql.*;
import Database.Cliente.Cliente;


/**
 *
 * @author ender
 */
public class Database {
    
    private String Caminho;
    private Connection Conexao;
    
    public Database(String Caminho)
    {
        this.Caminho = String.format("jdbc:sqlite:%s.db", Caminho);
        
        try 
        {
            this.Conexao = DriverManager.getConnection(this.Caminho);
            Statement Estamento = this.Conexao.createStatement();          

            // Criação das Tabelas
            
            // <editor-fold defaultstate="collapsed" desc="Tabela Cliente"> 
            Estamento.addBatch("CREATE TABLE IF NOT EXISTS Cliente ("
                               + "ID INTEGER PRIMARY KEY,"
                               + "CPF TEXT NOT NULL UNIQUE,"
                               + "nome TEXT NOT NULL,"
                               + "email TEXT NOT NULL,"
                               + "telefone TEXT NOT NULL);");
            // </editor-fold>  

            // <editor-fold defaultstate="collapsed" desc="Tabela Funcionario">
            Estamento.addBatch("CREATE TABLE IF NOT EXISTS Funcionario ("
                               + "ID INT PRIMARY KEY NOT NULL,"
                               + "nome TEXT NOT NULL,"
                               + "email TEXT NOT NULL,"
                               + "cargo TEXT NOT NULL,"
                               + "usuario TEXT NOT NULL,"
                               + "senha TEXT NOT NULL);");
            // </editor-fold>
            
            // <editor-fold defaultstate="collapsed" desc="Tabela Item">
            Estamento.addBatch("CREATE TABLE IF NOT EXISTS Item ("
                               + "ID INT PRIMARY KEY NOT NULL,"
                               + "nome TEXT NOT NULL,"
                               + "quantidade INT NOT NULL,"
                               + "descricao TEXT NOT NULL,"
                               + "valor REAL NOT NULL,"
                               + "status TEXT NOT NULL);");
            // </editor-fold>
            
            // <editor-fold defaultstate="collapsed" desc="Tabela Pedido">
            Estamento.addBatch("CREATE TABLE IF NOT EXISTS Pedido ("
                               + "ID INT PRIMARY KEY NOT NULL,"
                               + "ID_cliente INT NOT NULL,"
                               + "ID_funcionario INT NOT NULL,"
                               + "Data DATETIME NOT NULL,"
                               + "valor_servico REAL NOT NULL,"
                               + "valor_frete REAL NOT NULL,"
                               + "subtotal REAL NOT NULL,"
                               + "status TEXT NOT NULL,"
                               + "FOREIGN KEY(ID_funcionario) REFERENCES Funcionario(ID),"
                               + "FOREIGN KEY(ID_cliente) REFERENCES Cliente(ID));");
            // </editor-fold>
            
            // <editor-fold defaultstate="collapsed" desc="Tabela PedidoItem">
            Estamento.addBatch("CREATE TABLE IF NOT EXISTS PedidoItem ("
                               + "ID INT PRIMARY KEY NOT NULL,"
                               + "ID_pedido INT NOT NULL,"
                               + "ID_item INT NOT NULL,"
                               + "quantidade INT NOT NULL,"
                               + "subtotal REAL NOT NULL,"
                               + "FOREIGN KEY(ID_pedido) REFERENCES Pedido(ID),"
                               + "FOREIGN KEY(ID_item) REFERENCES Item(ID));");
            // </editor-fold>
            
            // <editor-fold defaultstate="collapsed" desc="Tabela LogSistema">
            Estamento.addBatch("CREATE TABLE IF NOT EXISTS LogSistema ("
                               + "ID INT PRIMARY KEY NOT NULL,"
                               + "ID_item INT NOT NULL,"
                               + "ID_funcionario INT NOT NULL,"
                               + "acao TEXT NOT NULL,"
                               + "Data DATETIME,"
                               + "FOREIGN KEY(ID_funcionario) REFERENCES Funcionario(ID));");
            // </editor-fold>
            
            Estamento.executeBatch();
        }
        catch (SQLException e)
        {
            System.err.print(e.getMessage());
        }
                
    }
    
    public void addCliente(Cliente c)
    {
        try 
        {
            
            Statement stmt = Conexao.createStatement();
            
            var h = String.format("INSERT INTO Cliente (CPF, nome, email, telefone) VALUES ('%s', '%s', '%s', '%s')", 
                    c.CPF.toString(), 
                    c.Nome, 
                    c.Email, 
                    c.Telefone);
            
            System.out.println(h);
            stmt.execute(h);
            
            stmt.close();
        } 
        catch (SQLException e) 
        {
            e.printStackTrace();
        }
    }
    
}
