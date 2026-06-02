package Classes.Tabelas;

import Classes.Categoria;
import Classes.Status_Item;

public class Item {
    
    public int ID;
    public String Nome;
    public Categoria Categoria;
    public String Descricao;
    public int Quantidade;
    public double Valor_Venda;
    public double Valor_Aluguel;
    public Status_Item Status;
    
    public Item(int id, String nome, Categoria categoria, String descricao, int quantidade, double valor_venda, double valor_aluguel, Status_Item status) throws Exception 
    {
        
        this.ID = id;
        this.Nome = nome;
        this.Categoria = categoria;
        this.Descricao = descricao;
        
        if ((status == Status_Item.ESTOQUE && quantidade == 0) || (status == Status_Item.FALTA && quantidade > 0))
        {
            throw new Exception("Status do Item incompativel com a Quantidade");
        }
        
        if (quantidade < 0)
        {
            throw new Exception("Quantidade negativa");
        }
        
        if (valor_venda < 0 || valor_aluguel < 0)
        {
            throw new Exception("Valores negativa");
        }
        
        this.Quantidade = quantidade;
        this.Valor_Venda = valor_venda;
        this.Valor_Aluguel = valor_aluguel;
        this.Status = status;
    }
    
    
    public String toString()
    {
        return String.format("Item(%d, %s, %s, %d, \"%s\", %.2f, %.2f, %s)", 
                                this.ID,
                                this.Nome,
                                this.Categoria,
                                this.Quantidade,
                                this.Descricao,
                                this.Valor_Venda,
                                this.Valor_Aluguel,
                                this.Status
                                );
    }
}