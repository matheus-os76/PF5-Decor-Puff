package Classes.Tabelas;

import Classes.CPF;

public class Cliente {
    
    public int ID;
    public CPF CPF;
    public String Nome;
    public String Email;
    public String Telefone;
    
    public Cliente(int id, CPF cpf, String nome, String email, String telefone)
    {
        
        this.ID = id;
        this.CPF = cpf;
        this.Nome = nome;
        this.Email = email;
        this.Telefone = telefone;
    }
   
    
    @Override
    public String toString()
    {
        return String.format("Cliente(%d, %s, %s, %s, %s)", 
                                this.ID,
                                this.CPF,
                                this.Nome,
                                this.Email,
                                this.Telefone
                                );
    }
}