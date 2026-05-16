/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Database.Cliente;

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
    
    public static Cliente gerarAleatorio()
    {
        try {
            Faker f = new Faker(new Locale("pt", "BR"));
            
            CPF cpf_faker = new CPF(f.number().numberBetween(0, 99999999999L));
            
            return new Cliente(cpf_faker, f.name().fullName(), f.internet().emailAddress(), f.phoneNumber().cellPhone());
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}