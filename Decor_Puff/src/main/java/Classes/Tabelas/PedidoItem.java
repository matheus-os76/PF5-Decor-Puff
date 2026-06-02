package Classes.Tabelas;

import Classes.DAO.PedidoDAO;
import Classes.Status_Item;
import Classes.Tabelas.Item;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import Utils.Conversor_Datetime;


public class PedidoItem {
 
    public int ID;
    public PedidoDAO pedido;
    public Item item;
    public int Quantidade;
    public double Subtotal;
    public LocalDateTime Data_Devolucao;
    
    private Timestamp Data_SQL;
    
    public PedidoItem(int id, PedidoDAO p, Item i, int quantidade, LocalDateTime data_devolucao) throws Exception
    {
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
        
        this.ID = 0;
        this.pedido = p;
        this.item = i;
        this.Quantidade = quantidade;
        this.Subtotal = quantidade * ((data_devolucao == null) ? i.Valor_Venda : i.Valor_Aluguel);
        this.Data_Devolucao = data_devolucao;
        
        this.Data_SQL = (data_devolucao == null) ? null : Conversor_Datetime.to_SQLTimestamp(data_devolucao);
    }
    
    public PedidoItem(int id, PedidoDAO p, Item i, int quantidade, Timestamp data_devolucao) throws Exception
    {
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
        
        this.ID = 0;
        this.pedido = p;
        this.item = i;
        this.Quantidade = quantidade;
        this.Subtotal = quantidade * ((data_devolucao == null) ? i.Valor_Venda : i.Valor_Aluguel);
        this.Data_Devolucao = (data_devolucao == null) ? null : Conversor_Datetime.to_LocalDateTime(data_devolucao);
        
        this.Data_SQL = data_devolucao;
    }

    public String toString()
    {
        return String.format("PedidoItem(%d, %d, %s, %d, %.2f)", 
                                this.ID,
                                this.pedido.ID,
                                this.item.Nome,
                                this.Quantidade,
                                this.Subtotal
                );
    }
    
    public Timestamp getDateSQL()
    {
        return this.Data_SQL;
    }
}
