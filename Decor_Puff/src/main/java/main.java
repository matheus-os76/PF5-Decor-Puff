import Cliente.CPF;
import Cliente.Cliente;
import Database.*;
import com.github.javafaker.Faker;

public static void main(String [] args)
{ 
    
    Database b = new Database("Database");
    
    
    try 
    {
        var x = b.getCliente(new CPF("053.017.093-95"));
        
        System.out.println(x);
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