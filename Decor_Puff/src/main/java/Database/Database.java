package Database;

import Classes.*;
import Classes.DAO.ItemDAO;
import Classes.DAO.PedidoDAO;
import Classes.Tabelas.*;
import java.sql.*;
import java.util.ArrayList;

// <editor-fold desc="Explicação"> 
/*

    Explicação
    
        A classe 'Database' funciona da seguinte forma: quando ela é instância com o "new"
        ela criará um Banco de Dados SQLite dentro da pasta 'Decor_Puff' com o nome que você
        passar como parâmetro, caso esse arquivo já exista na pasta ele só ira conectar-se 
        a esse Banco de Dados existente
    
        Com o objeto criado, ele ira permitir você chamar metódos para manusear qualquer tabela
        presente no banco

        A variável 'Conexao' é a responsável por manter a ligação com o Banco de Dados,
        através dela podemos executar qualquer comando relacionado ao banco, você pode
        obter ela através do metódo 'getConn' para casos de EMERGÊNCIA em que 
        não existe AINDA um metódo para tal ação.
    
*/
// </editor-fold>

public class Database {
    
    private String Caminho;
    private Connection Conexao;
    
    public static enum TABELAS {
        Cliente, Funcionario, Categoria, Item, Pedido, PedidoItem, LogSistema
    }
    
    public Database(String Caminho)
    {
        this.Caminho = String.format("jdbc:sqlite:%s.db", Caminho);
        
        try 
        {
            this.Conexao = DriverManager.getConnection(this.Caminho);
            Statement Estamento = this.Conexao.createStatement();
            
            Estamento.addBatch("PRAGMA foreign_keys = ON;");
            Estamento.addBatch("PRAGMA ignore_check_constraints = OFF;");
            

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
                               + "usuario TEXT NOT NULL UNIQUE,"
                               + "senha TEXT NOT NULL);");
            // </editor-fold>
            
            // <editor-fold defaultstate="collapsed" desc="Tabela Categoria">
            Estamento.addBatch("CREATE TABLE IF NOT EXISTS Categoria ("
                               + "ID INTEGER PRIMARY KEY,"
                               + "nome TEXT UNIQUE NOT NULL);");
            
            String insert_categorias = "INSERT OR IGNORE INTO Categoria (nome) VALUES ";
            int qntd_categorias = Categoria.values().length;
            
            for (int i = 0; i < qntd_categorias-1; i++) {
                
                insert_categorias += String.format("(\"%s\"), ", Categoria.values()[i]);
            }
            
            insert_categorias += String.format("(\"%s\");", Categoria.values()[qntd_categorias-1]);
            
            Estamento.addBatch(insert_categorias);
            // </editor-fold>
            
            // <editor-fold defaultstate="collapsed" desc="Tabela Item">
            Estamento.addBatch("CREATE TABLE IF NOT EXISTS Item ("
                               + "ID INTEGER PRIMARY KEY,"
                               + "ID_categoria INTEGER NOT NULL,"
                               + "nome TEXT NOT NULL,"
                               + "descricao TEXT,"
                               + "quantidade INT NOT NULL,"
                               + "valor_venda REAL DEFAULT 0.0,"
                               + "valor_aluguel REAL DEFAULT 0.0,"
                               + "status TEXT NOT NULL,"
                               + "FOREIGN KEY(ID_categoria) REFERENCES Categoria(ID));");
            // </editor-fold>
            
            // <editor-fold defaultstate="collapsed" desc="Tabela Pedido">
            Estamento.addBatch("CREATE TABLE IF NOT EXISTS Pedido ("
                               + "ID INTEGER PRIMARY KEY,"
                               + "ID_cliente INT NOT NULL,"
                               + "ID_funcionario INT NOT NULL,"
                               + "data DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,"
                               + "valor_frete REAL NOT NULL DEFAULT 0.0,"
                               + "valor_servico REAL NOT NULL DEFAULT 0.0,"
                               + "subtotal REAL NOT NULL DEFAULT 0.0,"
                               + "total REAL GENERATED ALWAYS AS (valor_frete+valor_servico+subtotal) STORED,"
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
                               + "subtotal REAL DEFAULT 0.0,"
                               + "data_devolucao DATETIME,"
                               + "FOREIGN KEY(ID_pedido) REFERENCES Pedido(ID),"
                               + "FOREIGN KEY(ID_item) REFERENCES Item(ID));");
            // </editor-fold>
            
            // <editor-fold defaultstate="collapsed" desc="Tabela LogSistema">
            Estamento.addBatch("CREATE TABLE IF NOT EXISTS LogSistema ("
                               + "ID INTEGER PRIMARY KEY,"
                               + "ID_funcionario INT NOT NULL,"
                               + "ID_item INT,"
                               + "acao TEXT NOT NULL,"
                               + "data DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,"
                               + "FOREIGN KEY(ID_funcionario) REFERENCES Funcionario(ID),"
                               + "FOREIGN KEY(ID_item) REFERENCES Item(ID));");
            // </editor-fold>
            
            // <editor-fold defaultstate="collapsed" desc="Trigger Pedido">
            Estamento.addBatch("CREATE TRIGGER IF NOT EXISTS Pedido_Subtotal_DELETE "
                               + "AFTER DELETE ON PedidoItem "
                               + "BEGIN "
                                    + "UPDATE Pedido "
                                        + "SET subtotal = subtotal - OLD.subtotal "
                                    + "WHERE Pedido.ID = OLD.ID_pedido; "
                    
                                    + "UPDATE Item "
                                        + "SET quantidade = quantidade + OLD.quantidade "
                                    + "WHERE Item.ID = OLD.ID_item; "
                               + "END;");
            
            Estamento.addBatch("CREATE TRIGGER IF NOT EXISTS Pedido_Subtotal_INSERT "
                               + "AFTER INSERT ON PedidoItem "
                               + "BEGIN "
                                    + "UPDATE PedidoItem "
                                        + "SET subtotal = NEW.quantidade * IIF(NEW.data_devolucao IS NULL, Item.valor_venda, Item.valor_aluguel) "
                                    + "FROM Item "
                                    + "WHERE PedidoItem.ID = NEW.ID AND Item.ID = NEW.ID_item; "
                    
                                    + "UPDATE Pedido "
                                        + "SET subtotal = Pedido.subtotal + PedidoItem.subtotal "
                                    + "FROM PedidoItem "
                                    + "WHERE PedidoItem.ID = NEW.ID AND Pedido.ID = NEW.ID_pedido; "
                    
                                    + "UPDATE Item "
                                        + "SET quantidade = quantidade - NEW.quantidade "
                                    + "WHERE Item.ID = NEW.ID_item; "
                               + "END;");
            
            Estamento.addBatch("CREATE TRIGGER IF NOT EXISTS Pedido_Subtotal_UPDATE "
                               + "AFTER UPDATE OF ID_item, quantidade ON PedidoItem "
                               + "BEGIN "
                                    + "UPDATE PedidoItem "
                                        + "SET subtotal = NEW.quantidade * IIF(NEW.data_devolucao IS NULL, Item.valor_venda, Item.valor_aluguel) "
                                    + "FROM Item "
                                    + "WHERE PedidoItem.ID = NEW.ID AND Item.ID = NEW.ID_item; "
                    
                                    + "UPDATE Pedido "
                                        + "SET subtotal = (SELECT SUM(subtotal) FROM PedidoItem WHERE ID_pedido = NEW.ID_pedido) "
                                    + "FROM PedidoItem "
                                    + "WHERE Pedido.ID = NEW.ID_pedido; "
                    
                                    + "UPDATE Item "
                                        + "SET quantidade = quantidade + (OLD.quantidade - NEW.quantidade) "
                                    + "WHERE Item.ID = NEW.ID_item; "
                               + "END;");
            
            //</editor-fold>
            
            // <editor-fold defaultstate="collapsed" desc="Trigger Item">
            
            Estamento.addBatch(""
                    + "CREATE TRIGGER IF NOT EXISTS Item_Status_ESTOQUE "
                    + "AFTER UPDATE OF quantidade ON Item "
                        + "WHEN OLD.quantidade = 0 AND NEW.quantidade > 0 "
                    + "BEGIN "
                        + "UPDATE Item SET status = 'ESTOQUE' WHERE Item.ID = NEW.ID; "
                    + "END;");
            
            Estamento.addBatch(""
                    + "CREATE TRIGGER IF NOT EXISTS Item_Status_FALTA "
                    + "AFTER UPDATE OF quantidade ON Item "
                        + "WHEN NEW.quantidade = 0 "
                    + "BEGIN "
                        + "UPDATE Item SET status = 'FALTA' WHERE Item.ID = NEW.ID; "
                    + "END;");
            
            Estamento.addBatch(""
                    + "CREATE TRIGGER IF NOT EXISTS Item_Valor_UPDATE "
                    + "AFTER UPDATE OF valor_venda, valor_aluguel ON Item "
                    + "BEGIN "
                        + "UPDATE PedidoItem "
                            + "SET subtotal = PedidoItem.quantidade * IIF(PedidoItem.data_devolucao IS NULL, NEW.valor_venda, NEW.valor_aluguel) "
                        + "FROM Item "
                        + "WHERE PedidoItem.ID_item = NEW.ID; " 
                    
                        + "UPDATE Pedido " 
                            + "SET subtotal = (SELECT SUM(subtotal) FROM PedidoItem WHERE ID_pedido = Pedido.ID); "  
                    + "END;");
            
            //</editor-fold>

            Estamento.executeBatch();
        }
        catch (SQLException e)
        {
            System.err.print(e.getMessage());
        }
        
    }
    
    public Connection getConn()
    {
        return Conexao;
    }
    
    public int getTabela_Tamanho(TABELAS nome_tabela)
    {
        
        try {
            ResultSet resultado = Conexao.createStatement().executeQuery(String.format("SELECT COUNT(ID) FROM %s", nome_tabela.name()));
            
            if (resultado.next())
            {
                return resultado.getInt("COUNT(ID)");
            }
            
        } catch (SQLException ex) {
            System.getLogger(Database.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
        
        return 0;
    }
    
    // <editor-fold defaultstate="collapsed" desc="Metodos Tabelas">
    
    public void addLog(LogSistema log)
    {
        try 
        {
            
            PreparedStatement stmt;
            if (log.Item == null)
            {
                stmt = Conexao.prepareStatement("INSERT INTO "
                        + "LogSistema (ID_funcionario, acao) "
                        + "VALUES (?, ?) "
                        + "RETURNING ID");
                
                stmt.setInt(1, log.Funcionario.ID);
                stmt.setString(2, log.Acao);
            }
            else
            {
                stmt = Conexao.prepareStatement("INSERT INTO "
                        + "LogSistema (ID_funcionario, ID_item, acao) "
                        + "VALUES (?, ?, ?) "
                        + "RETURNING ID");    
                
                stmt.setInt(1, log.Funcionario.ID);
                stmt.setInt(2, log.Item.ID);
                stmt.setString(3, log.Acao);
            }
            
            ResultSet resultado = stmt.executeQuery();
            
            if (resultado.next())
            {            
                log.ID = resultado.getInt("ID");
            }
            stmt.close();
        } 
        catch (SQLException e) 
        {
            e.printStackTrace();
        }
    }
    
    // <editor-fold defaultstate="collapsed" desc="Cliente">
    public void addCliente(Cliente c)
    {
        try 
        {
            
            PreparedStatement stmt = Conexao.prepareStatement("INSERT INTO Cliente (CPF, nome, email, telefone) VALUES (?, ?, ?, ?) RETURNING ID");
            
            stmt.setString(1, c.CPF.toString());
            stmt.setString(2, c.Nome);
            stmt.setString(3, c.Email);
            stmt.setString(4, c.Telefone);
            
            ResultSet resultado = stmt.executeQuery();
            
            if (resultado.next())
            {            
                c.ID = resultado.getInt("ID");
            }
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

                return new Cliente(
                        id, 
                        new CPF(resultado.getString("CPF")), 
                        resultado.getString("nome"), 
                        resultado.getString("email"), 
                        resultado.getString("telefone")
                );

            }
            resultado.close();
            stmt.close();
            
            return null;
        } 
        catch (SQLException e) 
        {
            e.printStackTrace();
        } catch (Exception ex) {
            System.getLogger(Database.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
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
                return new Cliente(
                        resultado.getInt("ID"), 
                        cpf, 
                        resultado.getString("nome"), 
                        resultado.getString("email"), 
                        resultado.getString("telefone")
                );
       
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

    public boolean delCliente(CPF cpf)
    {
        try 
        {
            PreparedStatement stmt = Conexao.prepareStatement("DELETE FROM Cliente CPF = ?");
            
            stmt.setString(1, cpf.toString());
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
    
    // <editor-fold defaultstate="collapsed" desc="Funcionario">
    public void addFuncionario(Funcionario f)
    {
        try 
        {
            
            PreparedStatement stmt = Conexao.prepareStatement("INSERT INTO Funcionario "
                                                            + "(nome, email, cargo, usuario, senha) VALUES (?, ?, ?, ?, ?) "
                                                            + "RETURNING ID;");
            
            stmt.setString(1, f.Nome);
            stmt.setString(2, f.Email);
            stmt.setString(3, f.Cargo.toString());
            stmt.setString(4, f.Usuario);
            stmt.setString(5, f.Senha);
            
            ResultSet resultado = stmt.executeQuery();
            
            if (resultado.next())
            {
                f.ID = resultado.getInt("ID");
            }
            
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
                return new Funcionario(
                        id,
                        resultado.getString("nome"),
                        resultado.getString("email"),
                        Cargo.valueOf(resultado.getString("cargo")),
                        resultado.getString("usuario"),
                        resultado.getString("senha")
                );

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
    
    public Funcionario getFuncionario(String usuario)
    {
        try 
        {
            PreparedStatement stmt = Conexao.prepareStatement("SELECT * FROM Funcionario WHERE usuario = ?");
            
            stmt.setString(1, usuario);
            ResultSet resultado = stmt.executeQuery();
            
            if (resultado.next())
            {      
   
                return new Funcionario(
                        resultado.getInt("ID"),
                        resultado.getString("nome"),
                        resultado.getString("email"),
                        Cargo.valueOf(resultado.getString("cargo")),
                        usuario,
                        resultado.getString("senha")
                );

            }
            resultado.close();
            stmt.close();
            
            return null;
        } 
        catch (SQLException e) 
        {
            e.printStackTrace();
        }
        catch (Exception e)
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
    
    // <editor-fold defaultstate="collapsed" desc="Item">    
    public void addItem(Item i)
    {
        try 
        {

            PreparedStatement stmt = Conexao.prepareStatement("INSERT INTO Item "
                    + "(nome, ID_categoria, descricao, quantidade, valor_venda, valor_aluguel, status) VALUES "
                    + "(?, (SELECT ID FROM Categoria WHERE nome = ?), ?, ?, ?, ?, ?)"
                    + "RETURNING ID");

            stmt.setString(1, i.Nome);
            stmt.setString(2, i.Categoria.name());
            stmt.setString(3, i.Descricao);
            stmt.setInt(4, i.Quantidade);
            stmt.setDouble(5, i.Valor_Venda);
            stmt.setDouble(6, i.Valor_Aluguel);
            stmt.setString(7, i.Status.name());

            ResultSet resultado = stmt.executeQuery();
            
            if (resultado.next())
            {
                i.ID = resultado.getInt("ID");
            }
            
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
            PreparedStatement stmt = Conexao.prepareStatement("SELECT "
                    + "Item.ID, Categoria.nome AS 'Categoria', Item.nome, Item.descricao, Item.quantidade, Item.valor_venda, Item.valor_aluguel, Item.status "
                    + "FROM Item INNER JOIN Categoria ON Categoria.ID = Item.ID_categoria "
                    + "WHERE Item.ID = ?;");
            
            stmt.setInt(1, id);
            
            ResultSet resultado = stmt.executeQuery();
            
            if (resultado.next())
            {
                return new Item(
                        resultado.getInt("ID"), 
                        resultado.getString("nome"), 
                        Categoria.valueOf(resultado.getString("Categoria")), 
                        resultado.getString("descricao"), 
                        resultado.getInt("quantidade"), 
                        resultado.getDouble("valor_venda"), 
                        resultado.getDouble("valor_aluguel"), 
                        Status_Item.valueOf(resultado.getString("status"))
                );
            }
            
            stmt.close();
            
            return null;
        } 
        catch (SQLException e) 
        {
            e.printStackTrace();
        } catch (Exception ex) {
            System.getLogger(Database.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
        
        return null;
    }
    
    public Item updateItem(Item i)
    {
        try 
        {
            PreparedStatement stmt = Conexao.prepareStatement("UPDATE Item SET "
                    + "nome = ?, ID_categoria = (SELECT ID From Categoria WHERE nome = ?), quantidade = ?, descricao = ?, valor_venda = ?, valor_aluguel = ?, status = ? "
                    + "WHERE ID = ?");
            
            stmt.setString(1, i.Nome);
            stmt.setString(2, i.Categoria.name());
            stmt.setInt(3, i.Quantidade);
            stmt.setString(4, i.Descricao);
            stmt.setDouble(5, i.Valor_Venda);
            stmt.setDouble(6, i.Valor_Aluguel);
            stmt.setString(7, i.Status.name());
            stmt.setInt(8, i.ID);
            
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
    
    // <editor-fold defaultstate="collapsed" desc="Pedido">
    public void addPedido(Pedido p)
    {
        try 
        {

            PreparedStatement stmt = Conexao.prepareStatement("INSERT INTO "
                    + "Pedido (ID_cliente, ID_funcionario, data, valor_servico, valor_frete, status) "
                    + "VALUES (?, ?, ?, ?, ?, ?) "
                    + "RETURNING ID;");
                
            stmt.setInt(1, p.Cliente.ID);
            stmt.setInt(2, p.Funcionario.ID);
            stmt.setTimestamp(3, p.getDateSQL());
            stmt.setDouble(4, p.Valor_Servico);
            stmt.setDouble(5, p.Valor_Frete);
            stmt.setString(6, p.Status.name());

            ResultSet resultado = stmt.executeQuery();

            if (resultado.next())
            {
                p.ID = resultado.getInt("ID");
                stmt.close();
                
                if (!p.Itens.isEmpty())
                {
                    
                    Conexao.setAutoCommit(false);
                    
                    PreparedStatement stmt_Itens = Conexao.prepareStatement("INSERT INTO "
                            + "PedidoItem (ID_pedido, ID_item, quantidade, data_devolucao) "
                            + "VALUES (?, ?, ?, ?)");
                                       
                    
                    for (int j = 0; j < p.Itens.size(); j++)
                    {
                        stmt_Itens.setInt(1, p.ID);
                        stmt_Itens.setInt(2, p.Itens.get(j).Item.ID);
                        stmt_Itens.setInt(3, p.Itens.get(j).Quantidade);
                        stmt_Itens.setTimestamp(4, p.Itens.get(j).getDateSQL());
                        stmt_Itens.addBatch();
                    }
                    
                    stmt_Itens.executeBatch();
                    stmt_Itens.close();
                    
                    Conexao.commit();
                    Conexao.setAutoCommit(true);
                }
            }
            else
            {
                stmt.close();
            }
            
        } 
        catch (SQLException e) 
        {
            e.printStackTrace();
        }
    }
    
    public void addPedidoItem(int id_pedido, ItemDAO i)
    {
        try 
        {

            PreparedStatement stmt = Conexao.prepareStatement("INSERT INTO "
                    + "PedidoItem (ID_pedido, ID_item, quantidade, data_devolucao) "
                    + "VALUES (?, ?, ?, ?);");
                
            stmt.setInt(1, id_pedido);
            stmt.setInt(2, i.Item.ID);
            stmt.setInt(3, i.Quantidade);
            stmt.setTimestamp(4, i.getDateSQL());

            ResultSet resultado = stmt.executeQuery();

            if (!resultado.next())
            {
                throw new Exception("Erro ao Inserir Item");
            }
            
        } 
        catch (SQLException e) 
        {
            e.printStackTrace();
        } catch (Exception ex) {
            System.getLogger(Database.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
    }
    
    public void addPedidoItem(PedidoItem pi)
    {
        try 
        {

            PreparedStatement stmt = Conexao.prepareStatement("INSERT INTO "
                    + "PedidoItem (ID_pedido, ID_item, quantidade, data_devolucao) "
                    + "VALUES (?, ?, ?, ?) "
                    + "RETURNING ID;");
                
            stmt.setInt(1, pi.pedido.ID);
            stmt.setInt(2, pi.item.ID);
            stmt.setInt(3, pi.Quantidade);
            stmt.setTimestamp(4, pi.getDateSQL());

            ResultSet resultado = stmt.executeQuery();

            if (resultado.next())
            {
                pi.ID = resultado.getInt("ID");
            }
            
        } 
        catch (SQLException e) 
        {
            e.printStackTrace();
        } catch (Exception ex) {
            System.getLogger(Database.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
    }
    
    public Pedido getPedido(int id)
    {
        try 
        {
            PreparedStatement stmt = Conexao.prepareStatement("SELECT "
                    + "ID, ID_cliente, ID_funcionario, data, valor_servico, valor_frete, subtotal, total, status "
                    + "FROM Pedido "
                    + "WHERE Pedido.ID = ?; ");
            
            stmt.setInt(1, id);

            ResultSet resultado = stmt.executeQuery();

            if (resultado.next())
            {
                
                Cliente c = this.getCliente(resultado.getInt("ID_cliente"));
                Funcionario f = this.getFuncionario(resultado.getInt("ID_funcionario"));
                
                
                PreparedStatement stmt_itens = Conexao.prepareStatement(
                        "SELECT ID_item, quantidade, subtotal, data_devolucao "
                        + "FROM PedidoItem WHERE ID_pedido = ?;");

                stmt_itens.setInt(1, id);

                ResultSet resultado_itens = stmt_itens.executeQuery();
                
                ArrayList<ItemDAO> lista_itens = new ArrayList<ItemDAO>();

                while(resultado_itens.next())
                {    
                    
                    ItemDAO i = new ItemDAO(
                            this.getItem(resultado_itens.getInt("ID_item")),
                            resultado_itens.getInt("quantidade"),
                            resultado_itens.getTimestamp("data_devolucao")
                    );

                    lista_itens.add(i);
                }

                return new Pedido(
                                id,
                                c,
                                f,
                                resultado.getTimestamp("data"),
                                resultado.getDouble("valor_servico"),
                                resultado.getDouble("valor_frete"),
                                Status_Pedido.valueOf(resultado.getString("status")),
                                lista_itens

                );

            }
        } 
        catch (SQLException e) 
        {
            e.printStackTrace();
        } catch (Exception ex) {
            System.getLogger(Database.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }

        return null;
    }
    
    public ArrayList<ItemDAO> getPedidoItens(int id)
    {
        try 
        {
            PreparedStatement stmt = Conexao.prepareStatement("SELECT ID_item, quantidade, data_devolucao FROM PedidoItem WHERE ID_pedido = ?;");
            
            stmt.setInt(1, id);

            ResultSet resultado = stmt.executeQuery();
            ArrayList<ItemDAO> PedidoItens = new ArrayList<ItemDAO>();
            
            while (resultado.next())
            {
                PedidoItens.add(new ItemDAO(
                            this.getItem(resultado.getInt("ID_item")),
                            resultado.getInt("quantidade"),
                            resultado.getTimestamp("data_devolucao")
                        )
                );
            }

            return PedidoItens;
        } 
        catch (SQLException e) 
        {
            e.printStackTrace();
        } catch (Exception ex) {
            System.getLogger(Database.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }

        return null;
    }
    
    public PedidoDAO getPedidoDAO(int id)
    {
        try 
        {
            PreparedStatement stmt = Conexao.prepareStatement("SELECT "
                    + "ID, ID_cliente, ID_funcionario, data, valor_servico, valor_frete, subtotal, total, status "
                    + "FROM Pedido "
                    + "WHERE Pedido.ID = ?; ");
            
            stmt.setInt(1, id);

            ResultSet resultado = stmt.executeQuery();

            if (resultado.next())
            {
                
                Cliente c = this.getCliente(resultado.getInt("ID_cliente"));
                Funcionario f = this.getFuncionario(resultado.getInt("ID_funcionario"));

                return new PedidoDAO(
                                id,
                                c,
                                f,
                                resultado.getTimestamp("data"),
                                resultado.getDouble("valor_servico"),
                                resultado.getDouble("valor_frete"),
                                resultado.getDouble("subtotal"),
                                resultado.getDouble("total"),
                                Status_Pedido.valueOf(resultado.getString("status"))
                );

            }
        } 
        catch (SQLException e) 
        {
            e.printStackTrace();
        } catch (Exception ex) {
            System.getLogger(Database.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }

        return null;
    }
    
    public PedidoItem getPedidoItem(int id)
    {
        try 
        {
            PreparedStatement stmt = Conexao.prepareStatement("SELECT "
                    + "SELECT ID, ID_pedido, ID_item, quantidade, subtotal, data_devolucao "
                    + "FROM PedidoItem "
                    + "WHERE ID = ?;");
            
            stmt.setInt(1, id);

            ResultSet resultado = stmt.executeQuery();
            
            if (resultado.next())
            {
                return new PedidoItem(
                        resultado.getInt("ID"),
                        this.getPedidoDAO(resultado.getInt("ID_pedido")),
                        this.getItem(resultado.getInt("ID_item")),
                        resultado.getInt("quantidade"),
                        resultado.getTimestamp("data_devolucao")
                );
            }

        } 
        catch (SQLException e) 
        {
            e.printStackTrace();
        } catch (Exception ex) {
            System.getLogger(Database.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }

        return null;
    }

    public Pedido updatePedido(Pedido p)
    {
        try 
        {
            Conexao.setAutoCommit(false);
            PreparedStatement stmt = Conexao.prepareStatement("UPDATE Pedido "
                    + "SET ID_cliente = ?, ID_funcionario = ?, data = ?, valor_servico = ?, valor_frete = ?, status = ? "
                    + "WHERE ID = ?; ");

            stmt.setInt(1, p.Cliente.ID);
            stmt.setInt(2, p.Funcionario.ID);
            stmt.setTimestamp(3, p.getDateSQL());
            stmt.setDouble(4, p.Valor_Servico);
            stmt.setDouble(5, p.Valor_Frete);
            stmt.setString(6, p.Status.name());
            stmt.setInt(7, p.ID);

            stmt.executeUpdate();
            stmt.close();
            
            PreparedStatement stmt_delete = Conexao.prepareStatement("DELETE FROM PedidoItem WHERE ID_pedido = ?");
            stmt_delete.setInt(1, p.ID);
            stmt_delete.execute();
            stmt_delete.close();
            
            if (!p.Itens.isEmpty())
            {
                PreparedStatement stmt_Itens = Conexao.prepareStatement("INSERT INTO "
                        + "PedidoItem (ID_pedido, ID_item, quantidade, data_devolucao) "
                        + "VALUES (?, ?, ?, ?)");


                for (int j = 0; j < p.Itens.size(); j++)
                {
                    stmt_Itens.setInt(1, p.ID);
                    stmt_Itens.setInt(2, p.Itens.get(j).Item.ID);
                    stmt_Itens.setInt(3, p.Itens.get(j).Quantidade);
                    stmt_Itens.setTimestamp(4, p.Itens.get(j).getDateSQL());
                    stmt_Itens.addBatch();
                }

                stmt_Itens.executeBatch();
                stmt_Itens.close();
            }

            Conexao.commit();
            Conexao.setAutoCommit(true);
            return p;
        } 
        catch (SQLException e) 
        {
            e.printStackTrace();
        }

        return null;
    }
    
    public Pedido updatePedidoItem(PedidoItem p)
    {
        try 
        {
            PreparedStatement stmt = Conexao.prepareStatement("UPDATE PedidoItem "
                    + "SET ID_pedido = ?, ID_item = ?, quantidade = ?, subtotal = ?, data_devolucao = ? "
                    + "WHERE ID = ?");
            
            stmt.setInt(1, p.pedido.ID);
            stmt.setInt(2, p.item.ID);
            stmt.setInt(3, p.Quantidade);
            stmt.setDouble(4, p.Subtotal);
            stmt.setTimestamp(5, p.getDateSQL());
            stmt.setInt(6, p.ID);
            
            stmt.executeUpdate();
            stmt.close();
        } 
        catch (SQLException e) 
        {
            e.printStackTrace();
        }

        return null;
    }

    public boolean delPedido(int id)
    {
        try 
        {
            PreparedStatement stmt = Conexao.prepareStatement("DELETE FROM PedidoItem WHERE ID_pedido = ?; "
                                                            + "DELETE FROM Pedido WHERE ID = ?");

            stmt.setInt(1, id);
            stmt.setInt(2, id);
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
    
    public boolean delPedidoItem(int id)
    {
        try 
        {
            PreparedStatement stmt = Conexao.prepareStatement("DELETE FROM PedidoItem WHERE ID = ?;");

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
    
    public boolean cleanPedido(int id)
    {
        try 
        {
            PreparedStatement stmt = Conexao.prepareStatement("DELETE FROM PedidoItem WHERE ID_pedido = ?");

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
    
    public LogSistema getLog(int id)
    {
        try 
        {
            PreparedStatement stmt = Conexao.prepareStatement("SELECT * FROM LogSistema WHERE ID = ?");
            
            stmt.setInt(1, id);
            ResultSet resultado = stmt.executeQuery();
            
            if (resultado.next())
            {      

                return new LogSistema(
                        resultado.getInt("ID"), 
                        this.getItem(resultado.getInt("ID_item")), 
                        this.getFuncionario(resultado.getInt("ID_funcionario")), 
                        resultado.getString("acao"), 
                        resultado.getTimestamp("data")
                );

            }
            resultado.close();
            stmt.close();
            
            return null;
        } 
        catch (SQLException e) 
        {
            e.printStackTrace();
        } catch (Exception ex) {
            System.getLogger(Database.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
        
        return null;
    }
    
    public LogSistema updateLog(LogSistema log)
    {
        try 
        {
            PreparedStatement stmt = Conexao.prepareStatement("UPDATE LogSistema "
                    + "SET ID_funcionario = ?, ID_item = ?, acao = ? "
                    + "WHERE ID = ?");
            
            stmt.setInt(1, log.Funcionario.ID);
            stmt.setInt(2, (log.Item == null) ? null : log.Item.ID);
            stmt.setString(3, log.Acao);
            stmt.setInt(4, log.ID);
            
            stmt.executeUpdate();
            stmt.close();
            
            return log;
        } 
        catch (SQLException e) 
        {
            e.printStackTrace();
        }
        
        return null;
    }
    
    public boolean delLog(int id)
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
    
    //</editor-fold> 
}
