package com.decorpuf.dao;

import com.decorpuf.model.Funcionario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FuncionarioDAO {

    public void inserir(Funcionario f) throws SQLException {
        String sql = "INSERT INTO Funcionario (nome, cargo, username, senha) VALUES (?, ?, ?, ?)";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, f.getNome());
            ps.setString(2, f.getCargo());
            ps.setString(3, f.getUsername());
            ps.setString(4, f.getSenha());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) f.setId(rs.getInt(1));
            }
        }
    }

    public void atualizar(Funcionario f) throws SQLException {
        String sql = "UPDATE Funcionario SET nome=?, cargo=?, username=?, senha=? WHERE id=?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, f.getNome());
            ps.setString(2, f.getCargo());
            ps.setString(3, f.getUsername());
            ps.setString(4, f.getSenha());
            ps.setInt(5, f.getId());
            ps.executeUpdate();
        }
    }

    public void deletar(int id) throws SQLException {
        String sql = "DELETE FROM Funcionario WHERE id=?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public Funcionario buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM Funcionario WHERE id=?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        }
        return null;
    }

    public Funcionario autenticar(String username, String senha) throws SQLException {
        String sql = "SELECT * FROM Funcionario WHERE username=? AND senha=?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, senha);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        }
        return null;
    }

    public List<Funcionario> listarTodos() throws SQLException {
        List<Funcionario> lista = new ArrayList<>();
        String sql = "SELECT * FROM Funcionario ORDER BY nome";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    private Funcionario mapear(ResultSet rs) throws SQLException {
        return new Funcionario(
            rs.getInt("id"),
            rs.getString("nome"),
            rs.getString("cargo"),
            rs.getString("username"),
            rs.getString("senha")
        );
    }
}