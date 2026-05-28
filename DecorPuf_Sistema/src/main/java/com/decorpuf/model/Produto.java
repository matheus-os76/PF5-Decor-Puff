package com.decorpuf.model;

public class Produto {
    private int id;
    private String nome;
    private String categoria;
    private String descricao;
    private int estoqueInicial;
    private boolean permiteVenda;
    private boolean permiteAluguel;
    private double valorVenda;
    private double valorAluguel;
    private String pathFoto;

    public Produto() {}

    public Produto(int id, String nome, String categoria, String descricao,
                   int estoqueInicial, boolean permiteVenda, boolean permiteAluguel,
                   double valorVenda, double valorAluguel, String pathFoto) {
        this.id = id;
        this.nome = nome;
        this.categoria = categoria;
        this.descricao = descricao;
        this.estoqueInicial = estoqueInicial;
        this.permiteVenda = permiteVenda;
        this.permiteAluguel = permiteAluguel;
        this.valorVenda = valorVenda;
        this.valorAluguel = valorAluguel;
        this.pathFoto = pathFoto;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public int getEstoqueInicial() { return estoqueInicial; }
    public void setEstoqueInicial(int estoqueInicial) { this.estoqueInicial = estoqueInicial; }

    public boolean isPermiteVenda() { return permiteVenda; }
    public void setPermiteVenda(boolean permiteVenda) { this.permiteVenda = permiteVenda; }

    public boolean isPermiteAluguel() { return permiteAluguel; }
    public void setPermiteAluguel(boolean permiteAluguel) { this.permiteAluguel = permiteAluguel; }

    public double getValorVenda() { return valorVenda; }
    public void setValorVenda(double valorVenda) { this.valorVenda = valorVenda; }

    public double getValorAluguel() { return valorAluguel; }
    public void setValorAluguel(double valorAluguel) { this.valorAluguel = valorAluguel; }

    public String getPathFoto() { return pathFoto; }
    public void setPathFoto(String pathFoto) { this.pathFoto = pathFoto; }

    @Override
    public String toString() {
        return nome + " [" + categoria + "]";
    }
}