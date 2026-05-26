/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Database;

import Database.Classes.Utils.CPF;
import com.github.javafaker.Faker;
import java.util.Locale;
import Database.Classes.*;
import Database.Classes.Utils.*;
import java.sql.Date;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.concurrent.TimeUnit;
/**
 *
 * @author ender
 */
public class DatabaseFaker {
    
    private final static Faker faker = new Faker(new Locale("pt", "BR"));
    
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
            
            return new Cliente(cpf, nome, email, telefone);
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

        return new Funcionario(nome, email, cargo, usuario, senha);
    }
    
    public static Item Item()
    {
        var produto = faker.commerce();
        int quantidade = faker.random().nextInt(0, 100);
        
        Status_Item status = (quantidade == 0) ? Status_Item.FALTA : Status_Item.ESTOQUE;
        
        return new Item(
                produto.productName(), 
                quantidade, 
                faker.lorem().sentence(4), 
                Double.parseDouble(produto.price()), 
                status
        );
    }
    
    public static Pedido Pedido(Cliente c, Funcionario f)
    {
        double servico = faker.number().randomDouble(2, 1L, 100L);
        double frete = faker.number().randomDouble(2, 1L, 100L);
        
        return new Pedido(
                c, 
                f, 
                faker.date().past(1, TimeUnit.DAYS).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime(),
                servico, 
                frete, 
                servico + frete, 
                Status_Pedido.values()[faker.random().nextInt(0, (Status_Pedido.values().length)-1)]
        );
    }
    
    public static PedidoItem PedidoItem(Pedido p, Item i)
    {
        return new PedidoItem(p, i, faker.random().nextInt(1, 100));
    }
    
    public static LogSistema LogSistema(Funcionario f, Item i)
    {
        return new LogSistema(
                i, 
                f, 
                faker.lorem().sentence(faker.random().nextInt(3, 12)), 
                faker.date().past(1, TimeUnit.DAYS).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
        );
    }
        
}
