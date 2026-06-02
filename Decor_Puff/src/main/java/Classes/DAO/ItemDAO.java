package Classes.DAO;

import Classes.Status_Item;
import Classes.Tabelas.Item;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import Utils.Conversor_Datetime;


public class ItemDAO {
 
    public Item Item;
    public int Quantidade;
    public double Subtotal;
    public LocalDateTime Data_Devolucao;
    
    private Timestamp Data_SQL;
    
    public ItemDAO(Item i, int quantidade) throws Exception
    {
        this.Item = i;
        
        if (quantidade <= 0)
        {
            throw new Exception("Quantidade negativa");
        }
        
        if (i.Status == Status_Item.FALTA)
        {
            throw new Exception("Status do Item está em FALTA");
        }
        
        if (i.Quantidade < quantidade)
        {
            throw new Exception("Quantidade maior do que disponível em estoque");
        }
        
        this.Quantidade = quantidade;
        this.Subtotal = quantidade * i.Valor_Venda;
        this.Data_Devolucao = null;
        
        this.Data_SQL = null;
    }
    
    public ItemDAO(Item i, int quantidade, LocalDateTime data_devolucao) throws Exception
    {
        this.Item = i;
        
        if (quantidade <= 0)
        {
            throw new Exception("Quantidade negativa");
        }
        
        if (i.Status == Status_Item.FALTA)
        {
            throw new Exception("Status do Item está em FALTA");
        }
        
        if (i.Quantidade < quantidade)
        {
            throw new Exception("Quantidade maior do que disponível em estoque");
        }
        
        this.Quantidade = quantidade;
        this.Subtotal = quantidade * ((data_devolucao == null) ? i.Valor_Venda : i.Valor_Aluguel);
        this.Data_Devolucao = data_devolucao;
        
        this.Data_SQL = (data_devolucao == null) ? null : Conversor_Datetime.to_SQLTimestamp(data_devolucao);
    }
    
    public ItemDAO(Item i, int quantidade, Timestamp data_devolucao) throws Exception
    {
        this.Item = i;
        
                if (quantidade <= 0)
        {
            throw new Exception("Quantidade negativa");
        }
        
        if (i.Status == Status_Item.FALTA)
        {
            throw new Exception("Status do Item está em FALTA");
        }
        
        if (i.Quantidade < quantidade)
        {
            throw new Exception("Quantidade maior do que disponível em estoque");
        }
        
        this.Quantidade = quantidade;
        this.Subtotal = quantidade * ((data_devolucao == null) ? i.Valor_Venda : i.Valor_Aluguel);
        this.Data_Devolucao = (data_devolucao == null) ? null : Conversor_Datetime.to_LocalDateTime(data_devolucao);
        
        this.Data_SQL = data_devolucao;
    }

    public String toString()
    {
        return String.format("Itens('%s', %d, R$ %.02f)",
                                this.Item.Nome,
                                this.Quantidade,
                                this.Subtotal
                );
    }
    
    public Timestamp getDateSQL()
    {
        return this.Data_SQL;
    }
}
