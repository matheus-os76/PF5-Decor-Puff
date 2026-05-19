/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Cliente;

import java.util.Random;

/**
 *
 * @author ender
 */
public class CPF {
    
    public String Digitos_unicos;
    public char Digito_origem;
    public char[] Digitos_verificadores;
    public static final long MAXIMO_CPF = 99999999999L;
    
    public CPF(String cpf) throws Exception
    {        
        cpf = cpf.strip().replaceAll("\\.|-", "");
        
        if (cpf.length() != 11)
        {
            throw new Exception("CPF inválido");
        }
        
        this.Digitos_unicos = cpf.substring(0, 8);
        this.Digito_origem = cpf.charAt(8);
        this.Digitos_verificadores = cpf.substring(9, 11).toCharArray();
    }
    
    public CPF(long cpf) throws Exception
    {
        if (cpf < 0 || cpf > this.MAXIMO_CPF)
        {
            throw new Exception("CPF inválido");
        }
        
        String cpf_string = String.format("%011d", cpf);
        
        this.Digitos_unicos = cpf_string.substring(0, 7);
        this.Digito_origem = cpf_string.charAt(8);
        this.Digitos_verificadores = cpf_string.substring(9, 11).toCharArray();
    }
    
    public String toString()
    {
        return String.format("%s.%s.%s%s-%s",
                        this.Digitos_unicos.substring(0, 3),
                        this.Digitos_unicos.substring(3, 6),
                        this.Digitos_unicos.substring(6, 8),
                        this.Digito_origem,
                        String.valueOf(this.Digitos_verificadores)
        );
    }

    public static CPF gerarAleatorio()
    {
        try {
            Random rand = new Random();
            return new CPF(rand.nextLong(0, MAXIMO_CPF));
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
        
    }
}
