package com.decorpuf.model;

// ============================================================
// ENTIDADE: Funcionario
// ============================================================
public class Funcionario {
    private int id;
    private String nome;
    private String cargo; // "Gerente" ou "Atendente"
    private String username;
    private String senha;

    public Funcionario() {}

    public Funcionario(int id, String nome, String cargo, String username, String senha) {
        this.id = id;
        this.nome = nome;
        this.cargo = cargo;
        this.username = username;
        this.senha = senha;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCargo() { return cargo; }
    public void setCargo(String cargo) { this.cargo = cargo; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }

    @Override
    public String toString() {
        return nome + " (" + cargo + ")";
    }
}