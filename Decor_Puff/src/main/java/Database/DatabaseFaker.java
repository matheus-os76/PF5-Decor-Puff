package Database;

import Classes.*;
import Classes.DAO.ItemDAO;
import Classes.Tabelas.*;
import Database.Database.TABELAS;
import com.github.javafaker.Faker;
import java.time.LocalDateTime;
import java.util.Locale;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.concurrent.TimeUnit;


public class DatabaseFaker {
    
    private final static Faker faker = new Faker(new Locale("pt", "BR"));
    private Database DB;
    
    public DatabaseFaker(Database DB)
    {
        this.DB = DB;
    }
    
    public static CPF CPF()
    {
        try 
        {
            return new CPF(faker.number().randomNumber(11, false));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }
    
    public static Cliente Cliente()
    {
        try
        {
            var pessoa = faker.name();
            
            CPF cpf = new CPF(faker.number().randomNumber(11, false));
            String nome = pessoa.fullName();
            String email = String.format("%s@gmail.com", pessoa.username());
            String telefone = faker.phoneNumber().phoneNumber();
            
            return new Cliente(0, cpf, nome, email, telefone);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        
        return null;
    }
    
    public static Funcionario Funcionario()
    {
        var pessoa = faker.name();

        String nome = pessoa.fullName();
        String usuario = pessoa.username();
        String senha = faker.internet().password();
        String email = String.format("%s@gmail.com", usuario);
        Cargo cargo = Cargo.values()[faker.random().nextInt(0, 1)];

        return new Funcionario(0, nome, email, cargo, usuario, senha);
    }
    
    public static Item Item() throws Exception
    {
        var produto = faker.commerce();
        String nome = produto.productName();
        Categoria categoria = Categoria.values()[faker.random().nextInt(0, Categoria.values().length-1)];
        String descricao = faker.lorem().sentence(5);
        int quantidade = faker.random().nextInt(1, 100) + 1;
        Status_Item status = Status_Item.ESTOQUE;
        
        int numero_auxiliar = faker.random().nextInt(0, 10);
        Double valor_venda = 0.0;
        Double valor_aluguel = 0.0;
        
        if (numero_auxiliar == 0)
        {
            valor_venda = Double.parseDouble(produto.price(0.05, 150.0));
            valor_aluguel = Double.parseDouble(produto.price(0.05, 150.0));
        }
        else if (numero_auxiliar % 2 == 0)
        {
            valor_venda = Double.parseDouble(produto.price(0.05, 150.0));
        }
        else 
        {
            valor_aluguel = Double.parseDouble(produto.price(0.05, 150.0));
        }
        
        return new Item(0, nome, categoria, descricao, quantidade, valor_venda, valor_aluguel, status);
    }
    
    public static LinkedHashSet<Integer> gerar_NumerosUnicos(int qntd_numeros, int maximo) throws Exception
    {
        if (maximo < qntd_numeros)
        {
            throw new Exception("Não é possível gerar uma lista sem repetições");
        }
        
        LinkedHashSet<Integer> numeros = new LinkedHashSet<Integer>();
        
        while(numeros.size() < qntd_numeros)
        {
            numeros.add(faker.random().nextInt(maximo) + 1);
        }
        
        return numeros;
    }
    
    public Pedido Pedido() throws Exception
    {
        double servico = faker.number().randomDouble(2, 1L, 100L);
        double frete = faker.number().randomDouble(2, 1L, 100L);
        Status_Pedido status = Status_Pedido.values()[faker.random().nextInt(0, Status_Pedido.values().length-1)];
        LocalDateTime data = faker.date().past(1, TimeUnit.DAYS).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        
        int qntd_clientes = DB.getTabela_Tamanho(TABELAS.Cliente);
        int qntd_funcionarios = DB.getTabela_Tamanho(TABELAS.Funcionario);
        int qntd_itens = DB.getTabela_Tamanho(TABELAS.Item);
        
        if (qntd_clientes == 0 && qntd_funcionarios == 0 && qntd_itens == 0)
        {
            throw new Exception("Não existe entradas suficientes no Banco de Dados");
        }
        
        int qntd_pedido_itens = faker.random().nextInt(1, qntd_itens);
        
        LinkedHashSet<Integer> lista_ids_itens = gerar_NumerosUnicos(qntd_pedido_itens, qntd_itens);
        
        ArrayList<ItemDAO> itens = new ArrayList<ItemDAO>(qntd_pedido_itens);
        
        for (int i = 0; i < qntd_pedido_itens; i++) {
            
            Item item = DB.getItem(lista_ids_itens.removeFirst());
            
            if (item.Quantidade == 0)
            {
                continue;
            }
            
            ItemDAO item_dao;
            
            int qntd_aleatoria = faker.random().nextInt(1, item.Quantidade);
            
            
            if (item.Valor_Aluguel > 0)
            {
                if (faker.random().nextBoolean())
                {
                    item_dao = new ItemDAO(
                            item, 
                            qntd_aleatoria,
                            data.plusDays(3)
                    );
                }
                else
                {
                    item_dao = new ItemDAO(
                            item, 
                            qntd_aleatoria
                    );
                }
            }
            else
            {                
                item_dao = new ItemDAO(
                        item, 
                        qntd_aleatoria
                );
            }
            
            itens.add(item_dao);
        }
        
        return new Pedido(
                0, 
                DB.getCliente(faker.random().nextInt(1, qntd_clientes)), 
                DB.getFuncionario(faker.random().nextInt(1, qntd_funcionarios)), 
                data, 
                servico, 
                frete, 
                status,
                itens
        );
    }
   
    public LogSistema LogSistema() throws Exception
    {
        int qntd_itens = DB.getTabela_Tamanho(TABELAS.Item);
        int qntd_funcionarios = DB.getTabela_Tamanho(TABELAS.Funcionario);
        
        if (qntd_funcionarios == 0)
        {
            throw new Exception("Não existe entradas suficientes no Banco de Dados");
        }
        
        String acao = faker.lorem().sentence(faker.random().nextInt(3, 12));
        LocalDateTime data = faker.date().past(1, TimeUnit.DAYS).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        
        if (faker.random().nextBoolean() && qntd_itens > 0)
        {
            return new LogSistema(0, DB.getItem(faker.random().nextInt(1, qntd_itens)), DB.getFuncionario(faker.random().nextInt(1, qntd_funcionarios)), acao, data);
        }
        else
        {
            return new LogSistema(0, DB.getFuncionario(faker.random().nextInt(1, qntd_funcionarios)), acao, data);
        }
    }
        
}
