/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Database;

import Cliente.*;
import Funcionario.*;
import java.sql.*;


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
    
    // <editor-fold defaultstate="collapsed" desc="Metodos Cliente">
    public void addCliente(Cliente c)
    {
        try 
        {
            
            PreparedStatement stmt = Conexao.prepareStatement("INSERT INTO Cliente (CPF, nome, email, telefone) VALUES (?, ?, ?, ?)");
            
            stmt.setString(1, c.CPF.toString());
            stmt.setString(2, c.Nome);
            stmt.setString(3, c.Email);
            stmt.setString(4, c.Telefone);
            
            stmt.execute();
            stmt.close();
        } 
        catch (SQLException e) 
        {
            e.printStackTrace();
        }
    }
    
    public Cliente getCliente(int id)
    {
        try 
        {
            PreparedStatement stmt = Conexao.prepareStatement("SELECT * FROM Cliente WHERE ID = ?");
            
            stmt.setInt(1, id);
            ResultSet resultado = stmt.executeQuery();
            
            if (resultado.next())
            {      
                try
                {
                    return new Cliente(
                            id, 
                            new CPF(resultado.getString("CPF")), 
                            resultado.getString("nome"), 
                            resultado.getString("email"), 
                            resultado.getString("telefone")
                    );
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
            resultado.close();
            stmt.close();
            
            return null;
        } 
        catch (SQLException e) 
        {
            e.printStackTrace();
        }
        
        return null;
    }
    
    public Cliente getCliente(CPF cpf)
    {
        try 
        {
            PreparedStatement stmt = Conexao.prepareStatement("SELECT * FROM Cliente WHERE CPF = ?");
            
            stmt.setString(1, cpf.toString());
            ResultSet resultado = stmt.executeQuery();
            
            if (resultado.next())
            {      
                try
                {
                    return new Cliente(
                            resultado.getInt("ID"), 
                            cpf, 
                            resultado.getString("nome"), 
                            resultado.getString("email"), 
                            resultado.getString("telefone")
                    );
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
            resultado.close();
            stmt.close();
            
            return null;
        } 
        catch (SQLException e) 
        {
            e.printStackTrace();
        }
        
        return null;
    }
    
    public Cliente updateCliente(Cliente c)
    {
        try 
        {
            PreparedStatement stmt = Conexao.prepareStatement("UPDATE Cliente SET CPF = ?, nome = ?, email = ?, telefone = ? WHERE ID = ?");
            
            stmt.setString(1, c.CPF.toString());
            stmt.setString(2, c.Nome);
            stmt.setString(3, c.Email);
            stmt.setString(4, c.Telefone);
            
            stmt.setInt(5, c.ID);
            
            stmt.executeUpdate();
            stmt.close();
            
            return c;
        } 
        catch (SQLException e) 
        {
            e.printStackTrace();
        }
        
        return null;
    }
    
    public boolean delCliente(int id)
    {
        try 
        {
            PreparedStatement stmt = Conexao.prepareStatement("DELETE FROM Cliente WHERE ID = ?");
            
            stmt.setInt(1, id);
            stmt.executeUpdate();
            
            stmt.close();
            return true;
        } 
        catch (SQLException e) 
        {
            e.printStackTrace();
        }
        
        return false;
    }

    public boolean delCliente(Cliente c)
    {
        try 
        {
            PreparedStatement stmt = Conexao.prepareStatement("DELETE FROM Cliente CPF = ?");
            
            stmt.setString(1, c.CPF.toString());
            stmt.executeUpdate();
            
            stmt.close();
            return true;
        } 
        catch (SQLException e) 
        {
            e.printStackTrace();
        }
        
        return false;
    }
    // </editor-fold> 
    
    // <editor-fold defaultstate="collapsed" desc="Metodos Funcionario">
    public void addFuncionario(Funcionario f)
    {
        try 
        {
            
            PreparedStatement stmt = Conexao.prepareStatement("INSERT INTO Funcionario (nome, email, cargo, username, password) VALUES (?, ?, ?, ?, ?)");
            
            stmt.setString(1, f.Nome);
            stmt.setString(2, f.Email);
            stmt.setString(3, f.Cargo);
            stmt.setString(4, f.Usuario);
            stmt.setString(5, f.Senha);
            
            stmt.execute();
            stmt.close();
        } 
        catch (SQLException e) 
        {
            e.printStackTrace();
        }
    }
    
    public Funcionario getFuncionario(int id)
    {
        try 
        {
            PreparedStatement stmt = Conexao.prepareStatement("SELECT * FROM Funcionario WHERE ID = ?");
            
            stmt.setInt(1, id);
            ResultSet resultado = stmt.executeQuery();
            
            if (resultado.next())
            {      
                try
                {
                    return new Funcionario(
                            id,
                            resultado.getString("nome"),
                            resultado.getString("email"),
                            resultado.getString("cargo"),
                            resultado.getString("usuario"),
                            resultado.getString("senha")
                    );
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
            resultado.close();
            stmt.close();
            
            return null;
        } 
        catch (SQLException e) 
        {
            e.printStackTrace();
        }
        
        return null;
    }
    
    public Funcionario updateFuncionario(Funcionario f)
    {
        try 
        {
            PreparedStatement stmt = Conexao.prepareStatement("UPDATE Funcionario SET nome = ?, email = ?, cargo = ?, usuario = ?, senha = ? WHERE ID = ?");
            
            stmt.setString(1, f.Nome);
            stmt.setString(2, f.Email);
            stmt.setString(3, f.Cargo);
            stmt.setString(4, f.Usuario);
            stmt.setString(5, f.Senha);
            
            stmt.setInt(6, f.ID);
            
            stmt.executeUpdate();
            stmt.close();
            
            return f;
        } 
        catch (SQLException e) 
        {
            e.printStackTrace();
        }
        
        return null;
    }
    
    public boolean delFuncionario(int id)
    {
        try 
        {
            PreparedStatement stmt = Conexao.prepareStatement("DELETE FROM Funcionario WHERE ID = ?");
            
            stmt.setInt(1, id);
            stmt.executeUpdate();
            
            stmt.close();
            return true;
        } 
        catch (SQLException e) 
        {
            e.printStackTrace();
        }
        
        return false;
    }
    // </editor-fold> 
    
}
