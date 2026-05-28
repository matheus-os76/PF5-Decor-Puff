package com.decorpuf.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Pedido {
    private int id;
    private int clienteId;
    private int funcionarioId;
    private String tipoPedido;         // "Venda" ou "Aluguel"
    private LocalDateTime dataCriacao;
    private LocalDateTime dataDevolucaoPrevista; // null se for Venda
    private String status;             // "Em Andamento", "Concluído", "Atrasado"
    private double valorTotal;

    // Objetos relacionados (para exibição)
    private Cliente cliente;
    private Funcionario funcionario;
    private List<ItemPedido> itens = new ArrayList<>();

    public Pedido() {
        this.dataCriacao = LocalDateTime.now();
        this.status = "Em Andamento";
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getClienteId() { return clienteId; }
    public void setClienteId(int clienteId) { this.clienteId = clienteId; }

    public int getFuncionarioId() { return funcionarioId; }
    public void setFuncionarioId(int funcionarioId) { this.funcionarioId = funcionarioId; }

    public String getTipoPedido() { return tipoPedido; }
    public void setTipoPedido(String tipoPedido) { this.tipoPedido = tipoPedido; }

    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }

    public LocalDateTime getDataDevolucaoPrevista() { return dataDevolucaoPrevista; }
    public void setDataDevolucaoPrevista(LocalDateTime dataDevolucaoPrevista) {
        this.dataDevolucaoPrevista = dataDevolucaoPrevista;
    }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public double getValorTotal() { return valorTotal; }
    public void setValorTotal(double valorTotal) { this.valorTotal = valorTotal; }

    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }

    public Funcionario getFuncionario() { return funcionario; }
    public void setFuncionario(Funcionario funcionario) { this.funcionario = funcionario; }

    public List<ItemPedido> getItens() { return itens; }
    public void setItens(List<ItemPedido> itens) { this.itens = itens; }

    public void addItem(ItemPedido item) { this.itens.add(item); }

    /**
     * Verifica se o pedido de aluguel está atrasado.
     */
    public boolean isAtrasado() {
        if ("Aluguel".equals(tipoPedido) && dataDevolucaoPrevista != null) {
            return LocalDateTime.now().isAfter(dataDevolucaoPrevista)
                    && !"Concluído".equals(status);
        }
        return false;
    }

    @Override
    public String toString() {
        return "Pedido #" + id + " [" + tipoPedido + "] - " + status;
    }
}