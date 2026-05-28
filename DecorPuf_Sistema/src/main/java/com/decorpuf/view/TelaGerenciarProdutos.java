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

public class TelaGerenciarProdutos extends JFrame {

    private Funcionario usuarioLogado;
    private JTable tabelaProdutos;
    private DefaultTableModel modeloTabela;

    public TelaGerenciarProdutos(Funcionario usuarioLogado) {
        this.usuarioLogado = usuarioLogado;

        UIUtils.configurarJanela(this, "Decor Puf - Gerenciar Produtos");
        setLayout(new BorderLayout());
        setBackground(UIUtils.COR_FUNDO);

        // 1. Cabeçalho
        add(UIUtils.criarPainelHeader("Estoque e Catálogo", "Gerencie os produtos disponíveis para venda e locação"), BorderLayout.NORTH);

        // 2. Centro (A Tabela)
        JPanel painelCentral = new JPanel(new BorderLayout(10, 10));
        painelCentral.setBackground(UIUtils.COR_FUNDO);
        painelCentral.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        String[] colunas = {"ID", "Nome do Produto", "Categoria", "Valor Venda", "Valor Aluguel"};
        modeloTabela = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        tabelaProdutos = new JTable(modeloTabela);
        UIUtils.estilizarTabela(tabelaProdutos);
        tabelaProdutos.getTableHeader().setForeground(Color.BLACK);
        tabelaProdutos.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        tabelaProdutos.getTableHeader().setBackground(new Color(230, 230, 250));
        tabelaProdutos.getColumnModel().getColumn(0).setMaxWidth(60);

        JScrollPane scrollPane = new JScrollPane(tabelaProdutos);
        scrollPane.getViewport().setBackground(Color.WHITE);
        painelCentral.add(scrollPane, BorderLayout.CENTER);
        add(painelCentral, BorderLayout.CENTER);

        // 3. Rodapé (Botões de Ação)
        JPanel rodape = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        rodape.setBackground(UIUtils.COR_FUNDO);

        JButton btnVoltar = UIUtils.criarBotaoPerigo("Voltar");
        btnVoltar.addActionListener(e -> voltarAoMenu());

        JButton btnExcluir = UIUtils.criarBotaoPerigo("🗑️ Excluir Produto");
        btnExcluir.addActionListener(e -> excluirProdutoSelecionado());

        JButton btnNovo = UIUtils.criarBotaoPrimario("+ Novo Produto");
        btnNovo.addActionListener(e -> {
            new TelaCadastrarProduto(this.usuarioLogado).setVisible(true);
            this.dispose();
        });

        rodape.add(btnVoltar);
        rodape.add(btnExcluir);
        rodape.add(btnNovo);

        add(rodape, BorderLayout.SOUTH);

        // 4. Carrega os dados na abertura da tela
        carregarTabela();
    }

    private void carregarTabela() {
        modeloTabela.setRowCount(0);
        String sql = "SELECT id, nome, categoria, valor_venda, valor_aluguel FROM Produto ORDER BY nome ASC";
        
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
             
            while (rs.next()) {
                modeloTabela.addRow(new Object[]{
                    rs.getInt("id"), 
                    rs.getString("nome"), 
                    rs.getString("categoria"),
                    UIUtils.formatarMoeda(rs.getDouble("valor_venda")),
                    UIUtils.formatarMoeda(rs.getDouble("valor_aluguel"))
                });
            }
        } catch (SQLException ex) {
            UIUtils.mostrarErro(this, "Erro ao carregar os produtos:\n" + ex.getMessage());
        }
    }

    private void excluirProdutoSelecionado() {
        int linha = tabelaProdutos.getSelectedRow();
        if (linha == -1) {
            UIUtils.mostrarErro(this, "Por favor, selecione um produto na tabela para excluir.");
            return;
        }

        int idProduto = (int) tabelaProdutos.getValueAt(linha, 0);
        String nomeProduto = (String) tabelaProdutos.getValueAt(linha, 1);

        if (UIUtils.confirmar(this, "ATENÇÃO: Deseja realmente excluir o produto '" + nomeProduto + "' do sistema?\n\nEle não aparecerá mais no catálogo de vendas.")) {
            try (Connection conn = ConnectionFactory.getConnection();
                 PreparedStatement ps = conn.prepareStatement("DELETE FROM Produto WHERE id = ?")) {
                 
                ps.setInt(1, idProduto);
                ps.executeUpdate();
                
                UIUtils.mostrarSucesso(this, "Produto excluído com sucesso!");
                carregarTabela(); // Atualiza a tabela na mesma hora
                
            } catch (SQLException ex) {
                // Blindagem de Banco de Dados: Se o produto já foi vendido/alugado no passado, 
                // o banco de dados não deixa apagar para não quebrar o recibo antigo do cliente!
                UIUtils.mostrarErro(this, "Operação Bloqueada: Não é possível excluir este produto porque ele já faz parte de pedidos registrados no histórico do sistema.\n\nPara segurança financeira da loja, produtos com histórico de vendas não podem ser apagados.");
            }
        }
    }

    private void voltarAoMenu() {
        new TelaMenuPrincipal(this.usuarioLogado).setVisible(true);
        this.dispose();
    }
}
