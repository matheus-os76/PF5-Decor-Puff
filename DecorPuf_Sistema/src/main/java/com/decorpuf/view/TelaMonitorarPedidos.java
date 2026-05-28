package com.decorpuf.view;

import com.decorpuf.dao.ConnectionFactory;
import com.decorpuf.model.Funcionario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TelaMonitorarPedidos extends JFrame {

    private Funcionario usuarioLogado;
    private JTable tabelaPedidos;
    private DefaultTableModel modeloTabela;
    private JComboBox<String> cbFiltroStatus;

    public TelaMonitorarPedidos(Funcionario usuarioLogado) {
        this.usuarioLogado = usuarioLogado;

        UIUtils.configurarJanela(this, "Decor Puf - Monitorar Pedidos");
        setLayout(new BorderLayout());
        setBackground(UIUtils.COR_FUNDO);

        // 1. Cabeçalho
        add(UIUtils.criarPainelHeader("Gestão de Pedidos", "Acompanhe as vendas e controle as devoluções de locações"), BorderLayout.NORTH);

        // 2. Centro (A Tabela)
        JPanel painelCentral = new JPanel(new BorderLayout(10, 10));
        painelCentral.setBackground(UIUtils.COR_FUNDO);
        painelCentral.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        // --- FILTRO DE STATUS NO TOPO ---
        JPanel painelFiltro = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        painelFiltro.setOpaque(false);
        painelFiltro.add(UIUtils.criarLabel("Filtrar por Status:"));
        
        // ADICIONADO: O status "Cancelado" no filtro
        cbFiltroStatus = UIUtils.criarComboBox(new String[]{"Todos", "Em Andamento", "Concluído", "Atrasado", "Cancelado"});
        cbFiltroStatus.setPreferredSize(new Dimension(200, 35));
        cbFiltroStatus.addActionListener(e -> carregarPedidos());
        painelFiltro.add(cbFiltroStatus);
        
        painelCentral.add(painelFiltro, BorderLayout.NORTH);

        // --- TABELA ---
        String[] colunas = {"Nº Pedido", "Cliente", "Tipo", "Data Criação", "Prazo Devolução", "Status", "Valor Total"};
        
        modeloTabela = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        tabelaPedidos = new JTable(modeloTabela);
        UIUtils.estilizarTabela(tabelaPedidos);
        tabelaPedidos.getTableHeader().setForeground(Color.BLACK);
        tabelaPedidos.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        tabelaPedidos.getTableHeader().setBackground(new Color(230, 230, 250));
        tabelaPedidos.getColumnModel().getColumn(0).setMaxWidth(80);

        JScrollPane scrollPane = new JScrollPane(tabelaPedidos);
        scrollPane.getViewport().setBackground(Color.WHITE);
        painelCentral.add(scrollPane, BorderLayout.CENTER);
        
        add(painelCentral, BorderLayout.CENTER);

        // 3. Rodapé (Botões de Ação com textos curtos para não cortar na resolução)
        JPanel rodape = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        rodape.setBackground(UIUtils.COR_FUNDO);

        JButton btnVoltar = UIUtils.criarBotaoPerigo("Voltar");
        btnVoltar.addActionListener(e -> voltarAoMenu());

        // NOVO BOTÃO: Cancelar Pedido
        JButton btnCancelar = UIUtils.criarBotaoPerigo("🚫 Cancelar");
        btnCancelar.setBackground(new Color(220, 53, 69)); // Um vermelho mais forte para alerta
        btnCancelar.addActionListener(e -> cancelarPedidoSelecionado());

        JButton btnVerItens = UIUtils.criarBotaoAcento("📦 Ver Itens");
        btnVerItens.addActionListener(e -> verItensPedidoSelecionado());

        JButton btnConcluir = UIUtils.criarBotaoPrimario("✅ Dar Baixa");
        btnConcluir.addActionListener(e -> concluirPedidoSelecionado());

        // Ordem dos botões no rodapé
        rodape.add(btnVoltar);
        rodape.add(btnCancelar);
        rodape.add(btnVerItens);
        rodape.add(btnConcluir);

        add(rodape, BorderLayout.SOUTH);

        // Carrega os dados na abertura da tela
        carregarPedidos();
    }

    private void carregarPedidos() {
        modeloTabela.setRowCount(0); 
        
        String statusSelecionado = (String) cbFiltroStatus.getSelectedItem();
        
        String sql = "SELECT p.id, c.nome AS cliente_nome, p.tipo_pedido, p.data_criacao, " +
                     "p.data_devolucao_prevista, p.status, p.valor_total " +
                     "FROM Pedido p " +
                     "INNER JOIN Cliente c ON p.cliente_id = c.id ";
                     
        if (!statusSelecionado.equals("Todos")) {
            sql += "WHERE p.status = ? ";
        }
        
        sql += "ORDER BY p.id DESC"; 

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            if (!statusSelecionado.equals("Todos")) {
                ps.setString(1, statusSelecionado);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String prazo = rs.getString("data_devolucao_prevista");
                    modeloTabela.addRow(new Object[]{
                        "#" + String.format("%04d", rs.getInt("id")),
                        rs.getString("cliente_nome"),
                        rs.getString("tipo_pedido"),
                        rs.getString("data_criacao"),
                        (prazo == null || prazo.isEmpty()) ? "-" : prazo,
                        rs.getString("status"),
                        UIUtils.formatarMoeda(rs.getDouble("valor_total"))
                    });
                }
            }
        } catch (SQLException ex) {
            UIUtils.mostrarErro(this, "Erro ao carregar pedidos:\n" + ex.getMessage());
        }
    }

    // --- NOVA LÓGICA DE CANCELAMENTO ---
    private void cancelarPedidoSelecionado() {
        int linha = tabelaPedidos.getSelectedRow();
        if (linha == -1) {
            UIUtils.mostrarErro(this, "Selecione um pedido na tabela para cancelar.");
            return;
        }

        String statusAtual = (String) tabelaPedidos.getValueAt(linha, 5);
        
        // Blindagem: Impede cancelar o que já acabou ou já foi cancelado
        if (statusAtual.equals("Concluído")) {
            UIUtils.mostrarErro(this, "Operação bloqueada: Não é possível cancelar um pedido que já foi concluído e pago.");
            return;
        }
        if (statusAtual.equals("Cancelado")) {
            UIUtils.mostrarErro(this, "Este pedido já está com o status de cancelado!");
            return;
        }

        String idFormatado = (String) tabelaPedidos.getValueAt(linha, 0);
        int idPedido = Integer.parseInt(idFormatado.replace("#", ""));
        String nomeCliente = (String) tabelaPedidos.getValueAt(linha, 1);

        if (UIUtils.confirmar(this, "ATENÇÃO: Deseja realmente CANCELAR o pedido de " + nomeCliente + "?\nO pedido continuará no histórico, mas o status mudará para 'Cancelado'.")) {
            String sql = "UPDATE Pedido SET status = 'Cancelado' WHERE id = ?";
            
            try (Connection conn = ConnectionFactory.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                 
                ps.setInt(1, idPedido);
                ps.executeUpdate();
                
                UIUtils.mostrarSucesso(this, "Pedido cancelado com sucesso no sistema!");
                carregarPedidos(); // Atualiza a tabela na mesma hora
                
            } catch (SQLException ex) {
                UIUtils.mostrarErro(this, "Erro ao atualizar status:\n" + ex.getMessage());
            }
        }
    }
    // -----------------------------------

    private void concluirPedidoSelecionado() {
        int linha = tabelaPedidos.getSelectedRow();
        if (linha == -1) {
            UIUtils.mostrarErro(this, "Selecione um pedido na tabela para dar baixa.");
            return;
        }

        String statusAtual = (String) tabelaPedidos.getValueAt(linha, 5);
        if (statusAtual.equals("Concluído")) {
            UIUtils.mostrarErro(this, "Este pedido já está concluído!");
            return;
        }
        if (statusAtual.equals("Cancelado")) {
            UIUtils.mostrarErro(this, "Não é possível dar baixa em um pedido que foi cancelado!");
            return;
        }

        String idFormatado = (String) tabelaPedidos.getValueAt(linha, 0);
        int idPedido = Integer.parseInt(idFormatado.replace("#", ""));
        String nomeCliente = (String) tabelaPedidos.getValueAt(linha, 1);

        if (UIUtils.confirmar(this, "Confirmar devolução/conclusão do pedido de " + nomeCliente + "?")) {
            String sql = "UPDATE Pedido SET status = 'Concluído' WHERE id = ?";
            
            try (Connection conn = ConnectionFactory.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                 
                ps.setInt(1, idPedido);
                ps.executeUpdate();
                
                UIUtils.mostrarSucesso(this, "Pedido concluído com sucesso!");
                carregarPedidos(); 
                
            } catch (SQLException ex) {
                UIUtils.mostrarErro(this, "Erro ao atualizar status:\n" + ex.getMessage());
            }
        }
    }

    private void verItensPedidoSelecionado() {
        int linha = tabelaPedidos.getSelectedRow();
        if (linha == -1) {
            UIUtils.mostrarErro(this, "Selecione um pedido para ver os itens.");
            return;
        }

        String idFormatado = (String) tabelaPedidos.getValueAt(linha, 0);
        int idPedido = Integer.parseInt(idFormatado.replace("#", ""));

        String sql = "SELECT p.nome, i.quantidade, i.subtotal " +
                     "FROM ItemPedido i " +
                     "INNER JOIN Produto p ON i.produto_id = p.id " +
                     "WHERE i.pedido_id = ?";

        StringBuilder itensMsg = new StringBuilder("Itens do Pedido " + idFormatado + ":\n\n");

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
             
            ps.setInt(1, idPedido);
            try (ResultSet rs = ps.executeQuery()) {
                boolean temItens = false;
                while (rs.next()) {
                    temItens = true;
                    itensMsg.append(rs.getInt("quantidade")).append("x ")
                            .append(rs.getString("nome")).append(" (")
                            .append(UIUtils.formatarMoeda(rs.getDouble("subtotal"))).append(")\n");
                }
                
                if (temItens) {
                    JOptionPane.showMessageDialog(this, itensMsg.toString(), "Detalhes do Pedido", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    UIUtils.mostrarErro(this, "Nenhum item encontrado para este pedido.");
                }
            }
        } catch (SQLException ex) {
            UIUtils.mostrarErro(this, "Erro ao buscar itens:\n" + ex.getMessage());
        }
    }

    private void voltarAoMenu() {
        new TelaMenuPrincipal(this.usuarioLogado).setVisible(true);
        this.dispose();
    }
}