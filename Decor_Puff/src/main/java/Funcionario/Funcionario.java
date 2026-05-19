/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Funcionario;

/**
 *
 * @author ender
 */
public class Funcionario {
    
    public int ID;
    public String Nome;
    public String Email;
    public String Cargo;
    public String Usuario;
    public String Senha;
    
    public Funcionario(int id, String nome, String email, String cargo, String usuario, String senha) throws Exception
    {
        this.ID = id;
        this.Nome = nome;
        this.Email = email;
        this.Cargo = cargo;
        this.Usuario = usuario;
        this.Senha = senha;
    }
    
    public Funcionario(String nome, String email, String cargo, String usuario, String senha) throws Exception
    {
        this.ID = 0;
        this.Nome = nome;
        this.Email = email;
        this.Cargo = cargo;
        this.Usuario = usuario;
        this.Senha = senha;
    }

    
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