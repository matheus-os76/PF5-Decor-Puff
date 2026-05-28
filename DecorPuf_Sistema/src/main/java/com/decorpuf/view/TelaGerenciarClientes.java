package com.decorpuf.view;

import com.decorpuf.dao.ClienteDAO;
import com.decorpuf.model.Cliente;
import com.decorpuf.model.Funcionario; // Importação necessária

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class TelaGerenciarClientes extends JFrame {

    private Funcionario usuarioLogado; // Guardando a sessão
    private JTable tabelaClientes;
    private DefaultTableModel modeloTabela;

    public TelaGerenciarClientes(Funcionario usuarioLogado) {
        this.usuarioLogado = usuarioLogado;
        
        UIUtils.configurarJanela(this, "Decor Puf - Gerenciar Clientes");
        setLayout(new BorderLayout());
        setBackground(UIUtils.COR_FUNDO);

        add(UIUtils.criarPainelHeader("Gerenciar Clientes", "Consulte, edite, exclua ou veja o histórico dos clientes"), BorderLayout.NORTH);

        JPanel painelCentral = new JPanel(new BorderLayout(10, 10));
        painelCentral.setBackground(UIUtils.COR_FUNDO);
        painelCentral.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        String[] colunas = {"ID", "Nome", "E-mail", "CPF", "Telefone"};
        modeloTabela = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        tabelaClientes = new JTable(modeloTabela);
        UIUtils.estilizarTabela(tabelaClientes);
        tabelaClientes.getTableHeader().setForeground(Color.BLACK);
        tabelaClientes.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        tabelaClientes.getTableHeader().setBackground(new Color(230, 230, 250));
        tabelaClientes.getColumnModel().getColumn(0).setMaxWidth(60);

        JScrollPane scrollPane = new JScrollPane(tabelaClientes);
        scrollPane.getViewport().setBackground(Color.WHITE);
        painelCentral.add(scrollPane, BorderLayout.CENTER);
        
        add(painelCentral, BorderLayout.CENTER);

        JPanel rodape = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        rodape.setBackground(UIUtils.COR_FUNDO);

        JButton btnVoltar = UIUtils.criarBotaoPerigo("Voltar");
        btnVoltar.addActionListener(e -> voltarAoMenu());

        JButton btnNovo = UIUtils.criarBotaoPrimario("+ Novo Cliente");
        btnNovo.addActionListener(e -> abrirCadastroNovo());

        JButton btnEditar = UIUtils.criarBotaoAcento("✏️ Editar");
        btnEditar.addActionListener(e -> editarClienteSelecionado());

        JButton btnHistorico = UIUtils.criarBotaoPrimario("📋 Histórico");
        btnHistorico.setBackground(UIUtils.COR_PRIMARIA_ESCURA);
        btnHistorico.addActionListener(e -> verHistoricoClienteSelecionado());

        JButton btnExcluir = UIUtils.criarBotaoPerigo("🗑️ Excluir");
        btnExcluir.addActionListener(e -> excluirClienteSelecionado());

        rodape.add(btnVoltar);
        rodape.add(btnExcluir);
        rodape.add(btnEditar);
        rodape.add(btnHistorico);
        rodape.add(btnNovo);

        add(rodape, BorderLayout.SOUTH);
        carregarTabela();
    }

    private void carregarTabela() {
        modeloTabela.setRowCount(0);
        try {
            ClienteDAO dao = new ClienteDAO();
            List<Cliente> clientes = dao.listarTodos(); 
            for (Cliente c : clientes) {
                modeloTabela.addRow(new Object[]{
                    c.getId(), c.getNome(), c.getEmail(), c.getCpf(), c.getTelefone()
                });
            }
        } catch (SQLException ex) {
            UIUtils.mostrarErro(this, "Erro ao buscar clientes: " + ex.getMessage());
        }
    }

    private void verHistoricoClienteSelecionado() {
        int linhaSelecionada = tabelaClientes.getSelectedRow();
        if (linhaSelecionada == -1) {
            UIUtils.mostrarErro(this, "Por favor, selecione um cliente na tabela para ver o histórico.");
            return;
        }

        Cliente c = new Cliente();
        c.setId((int) tabelaClientes.getValueAt(linhaSelecionada, 0));
        c.setNome((String) tabelaClientes.getValueAt(linhaSelecionada, 1));
        c.setEmail((String) tabelaClientes.getValueAt(linhaSelecionada, 2));
        c.setCpf((String) tabelaClientes.getValueAt(linhaSelecionada, 3));
        c.setTelefone((String) tabelaClientes.getValueAt(linhaSelecionada, 4));

        new TelaHistoricoCliente(this.usuarioLogado, c).setVisible(true);
        this.dispose();
    }

    private void excluirClienteSelecionado() {
        int linhaSelecionada = tabelaClientes.getSelectedRow();
        if (linhaSelecionada == -1) {
            UIUtils.mostrarErro(this, "Por favor, clique em um cliente na tabela primeiro.");
            return;
        }

        int idCliente = (int) tabelaClientes.getValueAt(linhaSelecionada, 0);
        String nomeCliente = (String) tabelaClientes.getValueAt(linhaSelecionada, 1);

        if (UIUtils.confirmar(this, "ATENÇÃO: Deseja realmente excluir o cliente '" + nomeCliente + "' permanentemente?")) {
            try {
                ClienteDAO dao = new ClienteDAO();
                dao.deletar(idCliente);
                UIUtils.mostrarSucesso(this, "Cliente excluído com sucesso!");
                carregarTabela();
            } catch (SQLException ex) {
                UIUtils.mostrarErro(this, "Erro ao excluir: " + ex.getMessage());
            }
        }
    }

    private void editarClienteSelecionado() {
        int linhaSelecionada = tabelaClientes.getSelectedRow();
        if (linhaSelecionada == -1) {
            UIUtils.mostrarErro(this, "Por favor, clique em um cliente na tabela para editar.");
            return;
        }
        
        Cliente c = new Cliente();
        c.setId((int) tabelaClientes.getValueAt(linhaSelecionada, 0));
        c.setNome((String) tabelaClientes.getValueAt(linhaSelecionada, 1));
        c.setEmail((String) tabelaClientes.getValueAt(linhaSelecionada, 2));
        c.setCpf((String) tabelaClientes.getValueAt(linhaSelecionada, 3));
        c.setTelefone((String) tabelaClientes.getValueAt(linhaSelecionada, 4));

        new TelaCadastrarCliente(this.usuarioLogado, c).setVisible(true);
        this.dispose();
    }

    private void abrirCadastroNovo() {
        new TelaCadastrarCliente(this.usuarioLogado).setVisible(true);
        this.dispose();
    }

    private void voltarAoMenu() {
        new TelaMenuPrincipal(this.usuarioLogado).setVisible(true);
        this.dispose();
    }
}