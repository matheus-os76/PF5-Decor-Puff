/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Database;

import Database.Classes.*;
import Database.Classes.Utils.*;
import java.sql.*;
import java.util.ArrayList;

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
                               + "ID INTEGER PRIMARY KEY,"
                               + "nome TEXT NOT NULL,"
                               + "email TEXT NOT NULL,"
                               + "cargo TEXT NOT NULL,"
                               + "usuario TEXT NOT NULL,"
                               + "senha TEXT NOT NULL);");
            // </editor-fold>
            
            // <editor-fold defaultstate="collapsed" desc="Tabela Item">
            Estamento.addBatch("CREATE TABLE IF NOT EXISTS Item ("
                               + "ID INTEGER PRIMARY KEY,"
                               + "nome TEXT NOT NULL,"
                               + "quantidade INT NOT NULL,"
                               + "descricao TEXT NOT NULL,"
                               + "valor REAL NOT NULL,"
                               + "status TEXT NOT NULL);");
            // </editor-fold>
            
            // <editor-fold defaultstate="collapsed" desc="Tabela Pedido">
            Estamento.addBatch("CREATE TABLE IF NOT EXISTS Pedido ("
                               + "ID INTEGER PRIMARY KEY,"
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
                               + "ID INTEGER PRIMARY KEY,"
                               + "ID_pedido INT NOT NULL,"
                               + "ID_item INT NOT NULL,"
                               + "quantidade INT NOT NULL,"
                               + "subtotal REAL NOT NULL,"
                               + "FOREIGN KEY(ID_pedido) REFERENCES Pedido(ID),"
                               + "FOREIGN KEY(ID_item) REFERENCES Item(ID));");
            // </editor-fold>
            
            // <editor-fold defaultstate="collapsed" desc="Tabela LogSistema">
            Estamento.addBatch("CREATE TABLE IF NOT EXISTS LogSistema ("
                               + "ID INTEGER PRIMARY KEY,"
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
            
            PreparedStatement stmt = Conexao.prepareStatement("INSERT INTO Funcionario (nome, email, cargo, usuario, senha) VALUES (?, ?, ?, ?, ?)");
            
            stmt.setString(1, f.Nome);
            stmt.setString(2, f.Email);
            stmt.setString(3, f.Cargo.toString());
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
                            Cargo.valueOf(resultado.getString("cargo")),
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
    
    public Funcionario getFuncionario(String usuario, String senha)
    {
        try 
        {
            PreparedStatement stmt = Conexao.prepareStatement("SELECT * FROM Funcionario WHERE usuario = ? AND senha = ?");
            
            stmt.setString(1, usuario);
            stmt.setString(2, senha);
            ResultSet resultado = stmt.executeQuery();
            
            if (resultado.next())
            {      
                try
                {
                    return new Funcionario(
                            resultado.getInt("ID"),
                            resultado.getString("nome"),
                            resultado.getString("email"),
                            Cargo.valueOf(resultado.getString("cargo")),
                            usuario,
                            senha
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
            stmt.setString(3, f.Cargo.toString());
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
    
    // <editor-fold defaultstate="collapsed" desc="Metodos Item">
    public void addItem(Item i)
    {
        try 
        {
            
            PreparedStatement stmt = Conexao.prepareStatement("INSERT INTO Item (nome, quantidade, descricao, valor, status) VALUES (?, ?, ?, ?, ?)");
            
            stmt.setString(1, i.Nome);
            stmt.setInt(2, i.Quantidade);
            stmt.setString(3, i.Descricao);
            stmt.setDouble(4, i.Valor);
            stmt.setString(5, i.Status.name());
            
            stmt.execute();
            stmt.close();
        } 
        catch (SQLException e) 
        {
            e.printStackTrace();
        }
    }
    
    public Item getItem(int id)
    {
        try 
        {
            PreparedStatement stmt = Conexao.prepareStatement("SELECT * FROM Item WHERE ID = ?");
            
            stmt.setInt(1, id);
            ResultSet resultado = stmt.executeQuery();
            
            if (resultado.next())
            {      
                try
                {
                    return new Item(
                            id,
                            resultado.getString("nome"),
                            resultado.getInt("quantidade"),
                            resultado.getString("descricao"),
                            resultado.getDouble("valor"),
                            Status_Item.valueOf(resultado.getString("status"))
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
    public ArrayList<Item> listartodososItens() {
        ArrayList<Item> lista = new ArrayList<>();
        try {
            PreparedStatement stmt = Conexao.prepareStatement("SELECT * FROM Item");
            ResultSet resultado = stmt.executeQuery();
        
            while (resultado.next()) {
                try {
                    Item item = new Item(
                        resultado.getInt("ID"),
                        resultado.getString("nome"),
                        resultado.getInt("quantidade"),
                        resultado.getString("descricao"),
                        resultado.getDouble("valor"),
                        Status_Item.valueOf(resultado.getString("status"))
                    );
                    lista.add(item);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            resultado.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
    
    public Item updateItem(Item i)
    {
        try 
        {
            PreparedStatement stmt = Conexao.prepareStatement("UPDATE Item SET nome = ?, quantidade = ?, descricao = ?, valor = ?, status = ? WHERE ID = ?");
            
            stmt.setString(1, i.Nome);
            stmt.setInt(2, i.Quantidade);
            stmt.setString(3, i.Descricao);
            stmt.setDouble(4, i.Valor);
            stmt.setString(5, i.Status.name());
            
            stmt.setInt(6, i.ID);
            
            stmt.executeUpdate();
            stmt.close();
            
            return i;
        } 
        catch (SQLException e) 
        {
            e.printStackTrace();
        }
        
        return null;
    }
    
    public boolean delItem(int id)
    {
        try 
        {
            PreparedStatement stmt = Conexao.prepareStatement("DELETE FROM Item WHERE ID = ?");
            
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
    
    // <editor-fold defaultstate="collapsed" desc="Metodos Pedido">
    public int addPedido(Pedido p)
{
    try
    {
        PreparedStatement stmt = Conexao.prepareStatement(
            "INSERT INTO Pedido (ID_cliente, ID_funcionario, Data, valor_servico, valor_frete, subtotal, status) VALUES (?, ?, ?, ?, ?, ?, ?)",
            Statement.RETURN_GENERATED_KEYS
        );

        stmt.setInt(1, p.cliente.ID);
        stmt.setInt(2, p.funcionario.ID);
        stmt.setDate(3, p.getDateSQL());
        stmt.setDouble(4, p.Valor_Servico);
        stmt.setDouble(5, p.Valor_Frete);
        stmt.setDouble(6, p.Subtotal);
        stmt.setString(7, p.Status.name());

        stmt.executeUpdate();

        ResultSet rs = stmt.getGeneratedKeys();

        if(rs.next()){
            return rs.getInt(1);
        }

        stmt.close();
    }
    catch(SQLException e)
    {
        e.printStackTrace();
    }

    return -1;
}

    public Pedido getPedido(int id)
    {
        try 
        {
            PreparedStatement stmt_pedido = Conexao.prepareStatement("SELECT * FROM Pedido WHERE ID = ?");

            stmt_pedido.setInt(1, id);
            ResultSet resultado_pedido = stmt_pedido.executeQuery();

            if (resultado_pedido.next())
            {      
                try
                {
                    PreparedStatement stmt_cliente = Conexao.prepareStatement("SELECT * FROM Cliente WHERE ID = ?");
                    stmt_cliente.setInt(1, resultado_pedido.getInt("ID_cliente"));

                    ResultSet resultado_cliente = stmt_cliente.executeQuery();

                    PreparedStatement stmt_funcionario = Conexao.prepareStatement("SELECT * FROM Funcionario WHERE ID = ?");
                    stmt_funcionario.setInt(1, resultado_pedido.getInt("ID_funcionario"));

                    ResultSet resultado_funcionario = stmt_funcionario.executeQuery();

                    if (resultado_cliente.next() & resultado_funcionario.next())
                    {
                        return new Pedido(
                                id,

                                new Cliente(
                                        resultado_cliente.getInt("ID"), 
                                        new CPF(resultado_cliente.getString("CPF")), 
                                        resultado_cliente.getString("nome"), 
                                        resultado_cliente.getString("email"), 
                                        resultado_cliente.getString("telefone")),

                                new Funcionario(
                                        resultado_funcionario.getInt("ID"),
                                        resultado_funcionario.getString("nome"),
                                        resultado_funcionario.getString("email"),
                                        Cargo.valueOf(resultado_funcionario.getString("cargo")),
                                        resultado_funcionario.getString("usuario"),
                                        resultado_funcionario.getString("senha")),

                                resultado_pedido.getTimestamp("Data").toLocalDateTime(),
                                resultado_pedido.getDouble("valor_servico"),
                                resultado_pedido.getDouble("valor_frete"),
                                resultado_pedido.getDouble("subtotal"),
                                Status_Pedido.valueOf(resultado_pedido.getString("status"))
                        );             
                    }

                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
            resultado_pedido.close();
            stmt_pedido.close();

            return null;
        } 
        catch (SQLException e) 
        {
            e.printStackTrace();
        }

        return null;
    }

    public Pedido updatePedido(Pedido p)
    {
        try 
        {
            PreparedStatement stmt = Conexao.prepareStatement("UPDATE Pedido SET Data = ?, valor_servico = ?, valor_frete = ?, subtotal = ?, status = ?, ID_cliente = ?, ID_funcionario = ? WHERE ID = ?");

            stmt.setDate(1, p.getDateSQL());
            stmt.setDouble(2, p.Valor_Servico);
            stmt.setDouble(3, p.Valor_Frete);
            stmt.setDouble(4, p.Subtotal);
            stmt.setString(5, p.Status.toString());
            stmt.setInt(6, p.cliente.ID);
            stmt.setInt(7, p.funcionario.ID);

            stmt.setInt(8, p.ID);

            stmt.executeUpdate();
            stmt.close();

            return p;
        } 
        catch (SQLException e) 
        {
            e.printStackTrace();
        }

        return null;
    }
    public ArrayList<Pedido> listarPedidos() {
        ArrayList<Pedido> lista = new ArrayList<>();

        try {
            PreparedStatement stmt = Conexao.prepareStatement("SELECT ID FROM Pedido");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Pedido p = getPedido(rs.getInt("ID"));
                if (p != null) {
                    lista.add(p);
                }
            }

            rs.close();
            stmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    public boolean delPedido(int id)
    {
        try 
        {
            PreparedStatement stmt = Conexao.prepareStatement("DELETE FROM Pedido WHERE ID = ?");

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
    
    // <editor-fold defaultstate="collapsed" desc="Metodos PedidoItem">
    public void addPedidoItem(PedidoItem p)
    {
        try 
        {

            PreparedStatement stmt = Conexao.prepareStatement("INSERT INTO PedidoItem (ID_pedido, ID_item, quantidade, subtotal) VALUES (?, ?, ?, ?)");

            stmt.setInt(1, p.pedido.ID);
            stmt.setInt(2, p.item.ID);
            stmt.setInt(3, p.Quantidade);
            stmt.setDouble(4, p.Subtotal);

            stmt.execute();
            stmt.close();
        } 
        catch (SQLException e) 
        {
            e.printStackTrace();
        }
    }

    public PedidoItem getPedidoItem(int id)
    {
        try 
        {
            PreparedStatement stmt = Conexao.prepareStatement("SELECT * FROM PedidoItem WHERE ID = ?");

            stmt.setInt(1, id);
            ResultSet resultado = stmt.executeQuery();

            if (resultado.next())
            {      
                try
                {
                    return new PedidoItem(
                            id, 
                            getPedido(resultado.getInt("ID_pedido")), 
                            getItem(resultado.getInt("ID_item")), 
                            resultado.getInt("quantidade")
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

    public PedidoItem updatePedidoItem(PedidoItem p)
    {
        try 
        {
            PreparedStatement stmt = Conexao.prepareStatement("UPDATE PedidoItem SET ID_pedido = ?, ID_item = ?, quantidade = ?, subtotal = ? WHERE ID = ?");

            stmt.setInt(1, p.pedido.ID);
            stmt.setInt(2, p.item.ID);
            stmt.setInt(3, p.Quantidade);
            stmt.setDouble(4, p.Subtotal);
            stmt.setInt(5, p.ID);

            stmt.executeUpdate();
            stmt.close();

            return p;
        } 
        catch (SQLException e) 
        {
            e.printStackTrace();
        }

        return null;
    }

    public boolean  delPedidoItem(int id)
    {
        try 
        {
            PreparedStatement stmt = Conexao.prepareStatement("DELETE FROM PedidoItem WHERE ID = ?");

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
    
    // <editor-fold defaultstate="collapsed" desc="Metodos LogSistema">
    public void addLog(LogSistema l)
    {
        try 
        {

            PreparedStatement stmt = Conexao.prepareStatement("INSERT INTO LogSistema (ID, ID_item, ID_funcionario, acao, Data) VALUES (?, ?, ?, ?)");

            stmt.setInt(1, l.ID);
            stmt.setInt(2, l.item.ID);
            stmt.setInt(3, l.funcionario.ID);
            stmt.setString(4, l.Acao);
            stmt.setDate(5, l.getDateSQL());

            stmt.execute();
            stmt.close();
        } 
        catch (SQLException e) 
        {
            e.printStackTrace();
        }
    }

    public LogSistema getLog(int id)
    {
        try 
        {
            PreparedStatement stmt = Conexao.prepareStatement("SELECT * FROM PedidoItem WHERE ID = ?");

            stmt.setInt(1, id);
            ResultSet resultado = stmt.executeQuery();

            if (resultado.next())
            {      
                try
                {
                    return new LogSistema(
                            id, 
                            getItem(resultado.getInt("ID_item")), 
                            getFuncionario(resultado.getInt("ID_funcionario")), 
                            resultado.getString("Acao"), 
                            resultado.getDate("Data")
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

    public LogSistema updateLog(LogSistema l)
    {
        try 
        {
            PreparedStatement stmt = Conexao.prepareStatement("UPDATE LogSistema SET ID_item = ?, ID_funcionario = ?, acao = ?, Data = ? WHERE ID = ?");

            stmt.setInt(1, l.item.ID);
            stmt.setInt(2, l.funcionario.ID);
            stmt.setString(3, l.Acao);
            stmt.setDate(4, l.getDateSQL());

            stmt.executeUpdate();
            stmt.close();

            return l;
        } 
        catch (SQLException e) 
        {
            e.printStackTrace();
        }

        return null;
    }

    public boolean  delLog(int id)
    {
        try 
        {
            PreparedStatement stmt = Conexao.prepareStatement("DELETE FROM LogSistema WHERE ID = ?");

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
