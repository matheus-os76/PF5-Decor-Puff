package com.decorpuf.dao;

import com.decorpuf.model.Cliente;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO {

    public void inserir(Cliente c) throws SQLException {
        String sql = "INSERT INTO Cliente (nome, email, cpf, telefone) VALUES (?, ?, ?, ?)";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, c.getNome());
            ps.setString(2, c.getEmail());
            ps.setString(3, c.getCpf());
            ps.setString(4, c.getTelefone());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) c.setId(rs.getInt(1));
            }
        }
    }

    public void atualizar(Cliente c) throws SQLException {
        String sql = "UPDATE Cliente SET nome=?, email=?, cpf=?, telefone=? WHERE id=?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.getNome());
            ps.setString(2, c.getEmail());
            ps.setString(3, c.getCpf());
            ps.setString(4, c.getTelefone());
            ps.setInt(5, c.getId());
            ps.executeUpdate();
        }
    }

    public void deletar(int id) throws SQLException {
        String sql = "DELETE FROM Cliente WHERE id=?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public Cliente buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM Cliente WHERE id=?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        }
        return null;
    }

    public List<Cliente> listarTodos() throws SQLException {
        List<Cliente> lista = new ArrayList<>();
        String sql = "SELECT * FROM Cliente ORDER BY nome";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    public List<Cliente> buscarPorNome(String nome) throws SQLException {
        List<Cliente> lista = new ArrayList<>();
        String sql = "SELECT * FROM Cliente WHERE nome LIKE ? ORDER BY nome";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + nome + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        }
        return lista;
    }

    private Cliente mapear(ResultSet rs) throws SQLException {
        return new Cliente(
            rs.getInt("id"),
            rs.getString("nome"),
            rs.getString("email"),
            rs.getString("cpf"),
            rs.getString("telefone")
        );
    }
}