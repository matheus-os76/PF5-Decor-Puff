package Classes.Tabelas;

import Classes.Cargo;

public class Funcionario {
    
    public int ID;
    public String Nome;
    public String Email;
    public Cargo Cargo;
    public String Usuario;
    public String Senha;
    
    public Funcionario(int id, String nome, String email, Cargo cargo, String usuario, String senha)
    {
        this.ID = id;
        this.Nome = nome;
        this.Email = email;
        this.Cargo = cargo;
        this.Usuario = usuario;
        this.Senha = senha;
    }

    
    @Override
    public String toString()
    {
        return String.format("Funcionario(%d, %s, %s, %s, %s, %s)", 
                                this.ID,
                                this.Nome,
                                this.Email,
                                this.Cargo,
                                this.Usuario,
                                this.Senha
                                );
    }
}