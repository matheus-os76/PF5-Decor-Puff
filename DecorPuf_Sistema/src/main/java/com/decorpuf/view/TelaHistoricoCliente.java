package com.decorpuf.view;

import com.decorpuf.model.Cliente;
import com.decorpuf.model.Funcionario; // Importação necessária

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class TelaHistoricoCliente extends JFrame {

    private Funcionario usuarioLogado;
    private Cliente cliente;
    private JTable tabelaHistorico;
    private DefaultTableModel modeloTabela;

    public TelaHistoricoCliente(Funcionario usuarioLogado, Cliente cliente) {
        this.usuarioLogado = usuarioLogado;
        this.cliente = cliente;

        UIUtils.configurarJanela(this, "Decor Puf - Histórico do Cliente");
        setLayout(new BorderLayout());
        setBackground(UIUtils.COR_FUNDO);

        add(UIUtils.criarPainelHeader("Ficha Comercial", "Histórico de transações de: " + cliente.getNome()), BorderLayout.NORTH);

        JPanel painelCentral = new JPanel(new BorderLayout(15, 15));
        painelCentral.setBackground(UIUtils.COR_FUNDO);
        painelCentral.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JPanel cardDados = UIUtils.criarCard();
        cardDados.setLayout(new GridLayout(2, 2, 20, 10));
        cardDados.setPreferredSize(new Dimension(0, 100));

        cardDados.add(UIUtils.criarLabel("<html><b>E-mail:</b> " + cliente.getEmail() + "</html>"));
        cardDados.add(UIUtils.criarLabel("<html><b>CPF:</b> " + formatarCpfExibicao(cliente.getCpf()) + "</html>"));
        cardDados.add(UIUtils.criarLabel("<html><b>Telefone:</b> " + formatarTelExibicao(cliente.getTelefone()) + "</html>"));
        
        JLabel lblStatusFinanceiro = UIUtils.criarLabel("<html><b>Situação Financeira:</b> <font color='green'>REGULAR</font></html>");
        cardDados.add(lblStatusFinanceiro);

        painelCentral.add(cardDados, BorderLayout.NORTH);

        String[] colunas = {"Nº Pedido", "Modalidade", "Data Emissão", "Prazo Devolução (72h)", "Status", "Valor Total"};
        modeloTabela = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        tabelaHistorico = new JTable(modeloTabela);
        UIUtils.estilizarTabela(tabelaHistorico);
        tabelaHistorico.getTableHeader().setForeground(Color.BLACK);
        tabelaHistorico.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabelaHistorico.getTableHeader().setBackground(new Color(230, 230, 250));

        JScrollPane scrollPane = new JScrollPane(tabelaHistorico);
        scrollPane.getViewport().setBackground(Color.WHITE);
        
        JPanel painelTabelaComTitulo = new JPanel(new BorderLayout(5, 5));
        painelTabelaComTitulo.setOpaque(false);
        JLabel lblTabelaTitulo = UIUtils.criarLabel("Histórico de Compras e Locações:");
        lblTabelaTitulo.setFont(UIUtils.FONTE_SUBTITULO);
        painelTabelaComTitulo.add(lblTabelaTitulo, BorderLayout.NORTH);
        painelTabelaComTitulo.add(scrollPane, BorderLayout.CENTER);

        painelCentral.add(painelTabelaComTitulo, BorderLayout.CENTER);
        add(painelCentral, BorderLayout.CENTER);

        JPanel rodape = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        rodape.setBackground(UIUtils.COR_FUNDO);
        
        JButton btnVoltar = UIUtils.criarBotaoPerigo("Voltar para Clientes");
        btnVoltar.addActionListener(e -> {
            new TelaGerenciarClientes(this.usuarioLogado).setVisible(true);
            this.dispose();
        });
        rodape.add(btnVoltar);
        add(rodape, BorderLayout.SOUTH);

        carregarHistoricoReal();
    }

    private void carregarHistoricoReal() {
        modeloTabela.setRowCount(0);
        String sql = "SELECT id, tipo_pedido, data_criacao, data_devolucao_prevista, status, valor_total " +
                     "FROM Pedido WHERE cliente_id = ? ORDER BY id DESC";
                     
        try (java.sql.Connection conn = com.decorpuf.dao.ConnectionFactory.getConnection();
             java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {
             
            ps.setInt(1, cliente.getId());
            
            try (java.sql.ResultSet rs = ps.executeQuery()) {
                while(rs.next()) {
                    String prazo = rs.getString("data_devolucao_prevista");
                    modeloTabela.addRow(new Object[]{
                        "#" + String.format("%04d", rs.getInt("id")), 
                        rs.getString("tipo_pedido"),
                        rs.getString("data_criacao"),
                        (prazo == null || prazo.isEmpty()) ? "-" : prazo,
                        rs.getString("status"),
                        UIUtils.formatarMoeda(rs.getDouble("valor_total"))
                    });
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao puxar histórico: " + e.getMessage());
        }
    }

    private String formatarCpfExibicao(String cpf) {
        if (cpf == null || cpf.length() != 11) return cpf;
        return cpf.substring(0, 3) + "." + cpf.substring(3, 6) + "." + cpf.substring(6, 9) + "-" + cpf.substring(9, 11);
    }

    private String formatarTelExibicao(String tel) {
        if (tel == null || tel.length() != 11) return tel;
        return "(" + tel.substring(0, 2) + ") " + tel.substring(2, 7) + "-" + tel.substring(7, 11);
    }
}