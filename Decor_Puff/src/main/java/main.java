import Database.Cliente.Cliente;
import Database.*;
import Database.Cliente.*;
import com.github.javafaker.Faker;

public static void main(String [] args)
{ 
    
    Database b = new Database("Database");
    
    
    try {

        b.addCliente(Cliente.gerarAleatorio());
    } catch (Exception e)
    {
        e.printStackTrace();
    }
//    var x = criarCliente();
//    
//    System.out.println(x.CPF);
//    System.out.println(x.Email);
//    System.out.println(x.Nome);
//    System.out.println(x.Telefone);
//    
//    b.addCliente(x);
//    
}