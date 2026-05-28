package com.decorpuf.model;

public class ItemPedido {
    private int id;
    private int pedidoId;
    private int produtoId;
    private int quantidade;
    private double subtotal;

    // Objeto relacionado para exibição
    private Produto produto;

    public ItemPedido() {}

    public ItemPedido(int pedidoId, int produtoId, int quantidade, double subtotal) {
        this.pedidoId = pedidoId;
        this.produtoId = produtoId;
        this.quantidade = quantidade;
        this.subtotal = subtotal;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getPedidoId() { return pedidoId; }
    public void setPedidoId(int pedidoId) { this.pedidoId = pedidoId; }

    public int getProdutoId() { return produtoId; }
    public void setProdutoId(int produtoId) { this.produtoId = produtoId; }

    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }

    public double getSubtotal() { return subtotal; }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }

    public Produto getProduto() { return produto; }
    public void setProduto(Produto produto) { this.produto = produto; }
}