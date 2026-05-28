package com.decorpuf.dao;

import com.decorpuf.model.Cliente;
import com.decorpuf.model.Funcionario;
import com.decorpuf.model.ItemPedido;
import com.decorpuf.model.Pedido;
import com.decorpuf.model.Produto;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class PedidoDAO {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // -------------------------------------------------------
    // PEDIDO - CRUD
    // -------------------------------------------------------

    public void inserir(Pedido p) throws SQLException {
        String sql = """
            INSERT INTO Pedido (cliente_id, funcionario_id, tipo_pedido,
                data_criacao, data_devolucao_prevista, status, valor_total)
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """;
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, p.getClienteId());
            ps.setInt(2, p.getFuncionarioId());
            ps.setString(3, p.getTipoPedido());
            ps.setString(4, p.getDataCriacao().format(FMT));
            ps.setString(5, p.getDataDevolucaoPrevista() != null
                    ? p.getDataDevolucaoPrevista().format(FMT) : null);
            ps.setString(6, p.getStatus());
            ps.setDouble(7, p.getValorTotal());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    p.setId(rs.getInt(1));
                }
            }
        }
    }

    public void atualizarStatus(int pedidoId, String novoStatus) throws SQLException {
        String sql = "UPDATE Pedido SET status=? WHERE id=?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, novoStatus);
            ps.setInt(2, pedidoId);
            ps.executeUpdate();
        }
    }

    public void deletar(int id) throws SQLException {
        String sql = "DELETE FROM Pedido WHERE id=?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public Pedido buscarPorId(int id) throws SQLException {
        String sql = """
            SELECT p.*, c.nome AS cliente_nome, c.cpf AS cliente_cpf,
                   f.nome AS func_nome, f.cargo AS func_cargo
            FROM Pedido p
            JOIN Cliente c ON p.cliente_id = c.id
            JOIN Funcionario f ON p.funcionario_id = f.id
            WHERE p.id = ?
        """;
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapearCompleto(rs);
            }
        }
        return null;
    }

    public List<Pedido> listarPorTipo(String tipo) throws SQLException {
        List<Pedido> lista = new ArrayList<>();
        String sql = """
            SELECT p.*, c.nome AS cliente_nome, c.cpf AS cliente_cpf,
                   f.nome AS func_nome, f.cargo AS func_cargo
            FROM Pedido p
            JOIN Cliente c ON p.cliente_id = c.id
            JOIN Funcionario f ON p.funcionario_id = f.id
            WHERE p.tipo_pedido = ?
            ORDER BY p.data_criacao DESC
        """;
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tipo);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapearCompleto(rs));
            }
        }
        return lista;
    }

    public List<Pedido> listarTodos() throws SQLException {
        List<Pedido> lista = new ArrayList<>();
        String sql = """
            SELECT p.*, c.nome AS cliente_nome, c.cpf AS cliente_cpf,
                   f.nome AS func_nome, f.cargo AS func_cargo
            FROM Pedido p
            JOIN Cliente c ON p.cliente_id = c.id
            JOIN Funcionario f ON p.funcionario_id = f.id
            ORDER BY p.data_criacao DESC
        """;
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapearCompleto(rs));
        }
        return lista;
    }

    /**
     * Busca aluguéis atrasados: data atual > data_devolucao_prevista e status <> 'Concluído'.
     */
    public List<Pedido> buscarAlugueisAtrasados() throws SQLException {
        List<Pedido> lista = new ArrayList<>();
        String agora = LocalDateTime.now().format(FMT);
        String sql = """
            SELECT p.*, c.nome AS cliente_nome, c.cpf AS cliente_cpf,
                   f.nome AS func_nome, f.cargo AS func_cargo
            FROM Pedido p
            JOIN Cliente c ON p.cliente_id = c.id
            JOIN Funcionario f ON p.funcionario_id = f.id
            WHERE p.tipo_pedido = 'Aluguel'
              AND p.data_devolucao_prevista IS NOT NULL
              AND p.data_devolucao_prevista < ?
              AND p.status <> 'Concluído'
            ORDER BY p.data_devolucao_prevista ASC
        """;
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, agora);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapearCompleto(rs));
            }
        }
        // Atualiza status para 'Atrasado'
        for (Pedido p : lista) {
            if (!"Atrasado".equals(p.getStatus())) {
                atualizarStatus(p.getId(), "Atrasado");
                p.setStatus("Atrasado");
            }
        }
        return lista;
    }

    // -------------------------------------------------------
    // RELATÓRIOS
    // -------------------------------------------------------

    /**
     * Total de vendas no mês atual.
     */
    public double totalVendasMesAtual() throws SQLException {
        String sql = """
            SELECT COALESCE(SUM(valor_total), 0) AS total
            FROM Pedido
            WHERE tipo_pedido = 'Venda'
              AND strftime('%Y-%m', data_criacao) = strftime('%Y-%m', 'now')
        """;
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getDouble("total");
        }
        return 0;
    }

    /**
     * Total de aluguéis no mês atual.
     */
    public double totalAluguelMesAtual() throws SQLException {
        String sql = """
            SELECT COALESCE(SUM(valor_total), 0) AS total
            FROM Pedido
            WHERE tipo_pedido = 'Aluguel'
              AND strftime('%Y-%m', data_criacao) = strftime('%Y-%m', 'now')
        """;
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getDouble("total");
        }
        return 0;
    }

    /**
     * Quantidade de pedidos por status.
     */
    public int contarPorStatus(String status) throws SQLException {
        String sql = "SELECT COUNT(*) AS qtd FROM Pedido WHERE status=?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("qtd");
            }
        }
        return 0;
    }

    // -------------------------------------------------------
    // ITEM PEDIDO
    // -------------------------------------------------------

    public void inserirItem(ItemPedido item) throws SQLException {
        String sql = "INSERT INTO ItemPedido (pedido_id, produto_id, quantidade, subtotal) VALUES (?, ?, ?, ?)";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, item.getPedidoId());
            ps.setInt(2, item.getProdutoId());
            ps.setInt(3, item.getQuantidade());
            ps.setDouble(4, item.getSubtotal());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) item.setId(rs.getInt(1));
            }
        }
    }

    public List<ItemPedido> listarItensPorPedido(int pedidoId) throws SQLException {
        List<ItemPedido> lista = new ArrayList<>();
        String sql = """
            SELECT ip.*, pr.nome AS prod_nome, pr.categoria, pr.valor_venda, pr.valor_aluguel
            FROM ItemPedido ip
            JOIN Produto pr ON ip.produto_id = pr.id
            WHERE ip.pedido_id = ?
        """;
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, pedidoId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ItemPedido item = new ItemPedido();
                    item.setId(rs.getInt("id"));
                    item.setPedidoId(rs.getInt("pedido_id"));
                    item.setProdutoId(rs.getInt("produto_id"));
                    item.setQuantidade(rs.getInt("quantidade"));
                    item.setSubtotal(rs.getDouble("subtotal"));
                    Produto p = new Produto();
                    p.setId(rs.getInt("produto_id"));
                    p.setNome(rs.getString("prod_nome"));
                    p.setCategoria(rs.getString("categoria"));
                    p.setValorVenda(rs.getDouble("valor_venda"));
                    p.setValorAluguel(rs.getDouble("valor_aluguel"));
                    item.setProduto(p);
                    lista.add(item);
                }
            }
        }
        return lista;
    }

    // -------------------------------------------------------
    // MAPEAMENTO
    // -------------------------------------------------------

    private Pedido mapearCompleto(ResultSet rs) throws SQLException {
        Pedido p = new Pedido();
        p.setId(rs.getInt("id"));
        p.setClienteId(rs.getInt("cliente_id"));
        p.setFuncionarioId(rs.getInt("funcionario_id"));
        p.setTipoPedido(rs.getString("tipo_pedido"));
        p.setStatus(rs.getString("status"));
        p.setValorTotal(rs.getDouble("valor_total"));

        String dc = rs.getString("data_criacao");
        if (dc != null) p.setDataCriacao(LocalDateTime.parse(dc, FMT));

        String dd = rs.getString("data_devolucao_prevista");
        if (dd != null) p.setDataDevolucaoPrevista(LocalDateTime.parse(dd, FMT));

        // Dados do cliente
        Cliente c = new Cliente();
        c.setId(rs.getInt("cliente_id"));
        c.setNome(rs.getString("cliente_nome"));
        c.setCpf(rs.getString("cliente_cpf"));
        p.setCliente(c);

        // Dados do funcionário
        Funcionario f = new Funcionario();
        f.setId(rs.getInt("funcionario_id"));
        f.setNome(rs.getString("func_nome"));
        f.setCargo(rs.getString("func_cargo"));
        p.setFuncionario(f);

        return p;
    }
}