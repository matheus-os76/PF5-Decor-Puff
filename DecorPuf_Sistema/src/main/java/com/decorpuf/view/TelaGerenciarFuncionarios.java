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

public class TelaGerenciarFuncionarios extends JFrame {

    private Funcionario usuarioLogado;
    private JTable tabelaFuncionarios;
    private DefaultTableModel modeloTabela;

    public TelaGerenciarFuncionarios(Funcionario usuarioLogado) {
        this.usuarioLogado = usuarioLogado;

        UIUtils.configurarJanela(this, "Decor Puf - Gerenciar Funcionários");
        setLayout(new BorderLayout());
        setBackground(UIUtils.COR_FUNDO);

        add(UIUtils.criarPainelHeader("Equipe e Acessos", "Gerencie os funcionários e permissões do sistema"), BorderLayout.NORTH);

        JPanel painelCentral = new JPanel(new BorderLayout(10, 10));
        painelCentral.setBackground(UIUtils.COR_FUNDO);
        painelCentral.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        String[] colunas = {"ID", "Nome Completo", "Cargo", "Nome de Usuário (Login)"};
        modeloTabela = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        tabelaFuncionarios = new JTable(modeloTabela);
        UIUtils.estilizarTabela(tabelaFuncionarios);
        tabelaFuncionarios.getTableHeader().setForeground(Color.BLACK);
        tabelaFuncionarios.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        tabelaFuncionarios.getTableHeader().setBackground(new Color(230, 230, 250));
        tabelaFuncionarios.getColumnModel().getColumn(0).setMaxWidth(60);

        JScrollPane scrollPane = new JScrollPane(tabelaFuncionarios);
        scrollPane.getViewport().setBackground(Color.WHITE);
        painelCentral.add(scrollPane, BorderLayout.CENTER);
        add(painelCentral, BorderLayout.CENTER);

        // Rodapé (Botões)
        JPanel rodape = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        rodape.setBackground(UIUtils.COR_FUNDO);

        JButton btnVoltar = UIUtils.criarBotaoPerigo("Voltar ao Menu");
        btnVoltar.addActionListener(e -> voltarAoMenu());

        JButton btnExcluir = UIUtils.criarBotaoPerigo("🗑️ Excluir");
        btnExcluir.addActionListener(e -> excluirFuncionario());

        JButton btnEditar = UIUtils.criarBotaoAcento("✏️ Editar");
        btnEditar.addActionListener(e -> editarFuncionario());

        JButton btnNovo = UIUtils.criarBotaoPrimario("+ Novo Funcionário");
        btnNovo.addActionListener(e -> {
            new TelaCadastrarFuncionario(this.usuarioLogado).setVisible(true);
            this.dispose();
        });

        rodape.add(btnVoltar);
        rodape.add(btnExcluir);
        rodape.add(btnEditar);
        rodape.add(btnNovo);
        add(rodape, BorderLayout.SOUTH);

        carregarTabela();
    }

    private void carregarTabela() {
        modeloTabela.setRowCount(0);
        String sql = "SELECT id, nome, cargo, username FROM Funcionario ORDER BY nome ASC";
        
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
             
            while (rs.next()) {
                modeloTabela.addRow(new Object[]{
                    rs.getInt("id"), rs.getString("nome"), rs.getString("cargo"), rs.getString("username")
                });
            }
        } catch (SQLException ex) {
            UIUtils.mostrarErro(this, "Erro ao carregar equipe:\n" + ex.getMessage());
        }
    }

    private void excluirFuncionario() {
        int linha = tabelaFuncionarios.getSelectedRow();
        if (linha == -1) {
            UIUtils.mostrarErro(this, "Selecione um funcionário para excluir.");
            return;
        }

        int idSelecionado = (int) tabelaFuncionarios.getValueAt(linha, 0);
        String nomeSelecionado = (String) tabelaFuncionarios.getValueAt(linha, 1);

        if (idSelecionado == usuarioLogado.getId()) {
            UIUtils.mostrarErro(this, "Você não pode excluir o seu próprio usuário enquanto está logado!");
            return;
        }

        if (UIUtils.confirmar(this, "Deseja realmente demitir/excluir o acesso de " + nomeSelecionado + "?")) {
            try (Connection conn = ConnectionFactory.getConnection();
                 PreparedStatement ps = conn.prepareStatement("DELETE FROM Funcionario WHERE id = ?")) {
                ps.setInt(1, idSelecionado);
                ps.executeUpdate();
                UIUtils.mostrarSucesso(this, "Funcionário removido com sucesso!");
                carregarTabela();
            } catch (SQLException ex) {
                UIUtils.mostrarErro(this, "Erro: Este funcionário está vinculado a pedidos e não pode ser apagado.");
            }
        }
    }

    private void editarFuncionario() {
        int linha = tabelaFuncionarios.getSelectedRow();
        if (linha == -1) {
            UIUtils.mostrarErro(this, "Selecione um funcionário para editar.");
            return;
        }

        Funcionario funcEdit = new Funcionario();
        funcEdit.setId((int) tabelaFuncionarios.getValueAt(linha, 0));
        funcEdit.setNome((String) tabelaFuncionarios.getValueAt(linha, 1));
        funcEdit.setCargo((String) tabelaFuncionarios.getValueAt(linha, 2));
        funcEdit.setUsername((String) tabelaFuncionarios.getValueAt(linha, 3));

        new TelaCadastrarFuncionario(this.usuarioLogado, funcEdit).setVisible(true);
        this.dispose();
    }

    private void voltarAoMenu() {
        new TelaMenuPrincipal(this.usuarioLogado).setVisible(true);
        this.dispose();
    }
}