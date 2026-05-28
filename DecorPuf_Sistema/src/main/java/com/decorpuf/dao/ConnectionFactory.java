package com.decorpuf.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Fábrica de conexões com o banco de dados SQLite.
 * Gerencia a criação das tabelas e o fornecimento de conexões.
 */
public class ConnectionFactory {

    private static final String URL = "jdbc:sqlite:decorpuf.db";
    private static Connection instance;

    private ConnectionFactory() {}

    /**
     * Retorna a conexão singleton com o banco de dados.
     */
    public static Connection getConnection() throws SQLException {
        if (instance == null || instance.isClosed()) {
            instance = DriverManager.getConnection(URL);
            instance.setAutoCommit(true);
        }
        return instance;
    }

    /**
     * Inicializa o banco de dados criando todas as tabelas necessárias.
     */
    public static void initializeDatabase() {
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement()) {

            // Habilitar Foreign Keys no SQLite
            stmt.execute("PRAGMA foreign_keys = ON");

            // Tabela Funcionario
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS Funcionario (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    nome TEXT NOT NULL,
                    cargo TEXT NOT NULL CHECK(cargo IN ('Gerente', 'Atendente')),
                    username TEXT NOT NULL UNIQUE,
                    senha TEXT NOT NULL
                )
            """);

            // Tabela Cliente
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS Cliente (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    nome TEXT NOT NULL,
                    email TEXT,
                    cpf TEXT NOT NULL UNIQUE,
                    telefone TEXT
                )
            """);

            // Tabela Produto
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS Produto (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    nome TEXT NOT NULL,
                    categoria TEXT,
                    descricao TEXT,
                    estoque_inicial INTEGER DEFAULT 0,
                    permite_venda INTEGER NOT NULL DEFAULT 0,
                    permite_aluguel INTEGER NOT NULL DEFAULT 0,
                    valor_venda REAL DEFAULT 0.0,
                    valor_aluguel REAL DEFAULT 0.0,
                    path_foto TEXT
                )
            """);

            // Tabela Pedido
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS Pedido (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    cliente_id INTEGER NOT NULL,
                    funcionario_id INTEGER NOT NULL,
                    tipo_pedido TEXT NOT NULL CHECK(tipo_pedido IN ('Venda', 'Aluguel')),
                    data_criacao TEXT NOT NULL,
                    data_devolucao_prevista TEXT,
                    status TEXT NOT NULL DEFAULT 'Em Andamento' CHECK(status IN ('Em Andamento', 'Concluído', 'Atrasado')),
                    valor_total REAL DEFAULT 0.0,
                    FOREIGN KEY (cliente_id) REFERENCES Cliente(id) ON DELETE RESTRICT,
                    FOREIGN KEY (funcionario_id) REFERENCES Funcionario(id) ON DELETE RESTRICT
                )
            """);

            // Tabela ItemPedido
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS ItemPedido (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    pedido_id INTEGER NOT NULL,
                    produto_id INTEGER NOT NULL,
                    quantidade INTEGER NOT NULL DEFAULT 1,
                    subtotal REAL DEFAULT 0.0,
                    FOREIGN KEY (pedido_id) REFERENCES Pedido(id) ON DELETE CASCADE,
                    FOREIGN KEY (produto_id) REFERENCES Produto(id) ON DELETE RESTRICT
                )
            """);

            // Criar usuário Gerente padrão se não existir
            stmt.execute("""
                INSERT OR IGNORE INTO Funcionario (nome, cargo, username, senha)
                VALUES ('Administrador', 'Gerente', 'admin', 'admin123')
            """);

            System.out.println("[DB] Banco de dados inicializado com sucesso.");

        } catch (SQLException e) {
            System.err.println("[DB] Erro ao inicializar banco de dados: " + e.getMessage());
            throw new RuntimeException("Falha crítica ao inicializar o banco de dados.", e);
        }
    }

    /**
     * Fecha a conexão com o banco de dados.
     */
    public static void closeConnection() {
        try {
            if (instance != null && !instance.isClosed()) {
                instance.close();
            }
        } catch (SQLException e) {
            System.err.println("[DB] Erro ao fechar conexão: " + e.getMessage());
        }
    }
}