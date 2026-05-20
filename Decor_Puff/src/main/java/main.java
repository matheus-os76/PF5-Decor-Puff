import Database.DatabaseFaker;

public static void main(String [] args)
{ 
    
    try 
    {
        var a = DatabaseFaker.CPF();
        var b = DatabaseFaker.Cliente();
        var c = DatabaseFaker.Funcionario();
        var d = DatabaseFaker.Item();
//        var e = DatabaseFaker.Pedido(b, c);
        
        System.out.println(a);
        System.out.println(b);
        System.out.println(c);
        System.out.println(d);
//        System.out.println(e);
        
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