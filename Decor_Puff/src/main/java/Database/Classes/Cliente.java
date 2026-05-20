/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Database.Classes;

import Database.Classes.Utils.CPF;
import com.github.javafaker.Faker;
import java.util.Locale;

/**
 *
 * @author ender
 */
public class Cliente {
    
    public int ID;
    public CPF CPF;
    public String Nome;
    public String Email;
    public String Telefone;
    
    public Cliente(int id, CPF cpf, String nome, String email, String telefone) throws Exception
    {
        
        this.ID = id;
        this.CPF = cpf;
        this.Nome = nome;
        this.Email = email;
        this.Telefone = telefone;
    }
    
    public Cliente(CPF cpf, String nome, String email, String telefone)
    {
        this.ID = 0;
        this.CPF = cpf;
        this.Nome = nome;
        this.Email = email;
        this.Telefone = telefone;
    }
    
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