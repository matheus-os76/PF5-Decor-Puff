package com.decorpuf.dao;

import com.decorpuf.model.Produto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProdutoDAO {

    public void inserir(Produto p) throws SQLException {
        String sql = """
            INSERT INTO Produto (nome, categoria, descricao, estoque_inicial,
                permite_venda, permite_aluguel, valor_venda, valor_aluguel, path_foto)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, p.getNome());
            ps.setString(2, p.getCategoria());
            ps.setString(3, p.getDescricao());
            ps.setInt(4, p.getEstoqueInicial());
            ps.setInt(5, p.isPermiteVenda() ? 1 : 0);
            ps.setInt(6, p.isPermiteAluguel() ? 1 : 0);
            ps.setDouble(7, p.getValorVenda());
            ps.setDouble(8, p.getValorAluguel());
            ps.setString(9, p.getPathFoto());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) p.setId(rs.getInt(1));
            }
        }
    }

    public void atualizar(Produto p) throws SQLException {
        String sql = """
            UPDATE Produto SET nome=?, categoria=?, descricao=?, estoque_inicial=?,
                permite_venda=?, permite_aluguel=?, valor_venda=?, valor_aluguel=?, path_foto=?
            WHERE id=?
        """;
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getNome());
            ps.setString(2, p.getCategoria());
            ps.setString(3, p.getDescricao());
            ps.setInt(4, p.getEstoqueInicial());
            ps.setInt(5, p.isPermiteVenda() ? 1 : 0);
            ps.setInt(6, p.isPermiteAluguel() ? 1 : 0);
            ps.setDouble(7, p.getValorVenda());
            ps.setDouble(8, p.getValorAluguel());
            ps.setString(9, p.getPathFoto());
            ps.setInt(10, p.getId());
            ps.executeUpdate();
        }
    }

    public void deletar(int id) throws SQLException {
        String sql = "DELETE FROM Produto WHERE id=?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public Produto buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM Produto WHERE id=?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        }
        return null;
    }

    public List<Produto> listarTodos() throws SQLException {
        List<Produto> lista = new ArrayList<>();
        String sql = "SELECT * FROM Produto ORDER BY nome";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    public List<Produto> listarPorCategoria(String categoria) throws SQLException {
        List<Produto> lista = new ArrayList<>();
        String sql = "SELECT * FROM Produto WHERE categoria=? ORDER BY nome";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, categoria);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        }
        return lista;
    }

    public List<String> listarCategorias() throws SQLException {
        List<String> cats = new ArrayList<>();
        String sql = "SELECT DISTINCT categoria FROM Produto WHERE categoria IS NOT NULL ORDER BY categoria";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) cats.add(rs.getString("categoria"));
        }
        return cats;
    }

    private Produto mapear(ResultSet rs) throws SQLException {
        return new Produto(
            rs.getInt("id"),
            rs.getString("nome"),
            rs.getString("categoria"),
            rs.getString("descricao"),
            rs.getInt("estoque_inicial"),
            rs.getInt("permite_venda") == 1,
            rs.getInt("permite_aluguel") == 1,
            rs.getDouble("valor_venda"),
            rs.getDouble("valor_aluguel"),
            rs.getString("path_foto")
        );
    }
}