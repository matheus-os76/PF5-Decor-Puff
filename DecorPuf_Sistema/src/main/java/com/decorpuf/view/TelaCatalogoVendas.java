package com.decorpuf.view;

import com.decorpuf.dao.ClienteDAO;
import com.decorpuf.dao.ConnectionFactory;
import com.decorpuf.dao.ProdutoDAO;
import com.decorpuf.model.Cliente;
import com.decorpuf.model.Funcionario;
import com.decorpuf.model.Produto;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class TelaCatalogoVendas extends JFrame {

    private Funcionario usuarioLogado;
    
    private JTable tabelaCatalogo;
    private DefaultTableModel modeloCatalogo;
    private JComboBox<String> cbFiltroCategoria; 
    private List<Produto> todosProdutosDB = new ArrayList<>(); 
    
    private JTable tabelaCarrinho;
    private DefaultTableModel modeloCarrinho;
    
    private JComboBox<Cliente> cbClientes;
    private JRadioButton rbVenda, rbAluguel;
    private JLabel lblTotal;
    private JSpinner spinQtd;
    private double valorTotalPedido = 0.0;

    public TelaCatalogoVendas(Funcionario usuarioLogado) {
        this.usuarioLogado = usuarioLogado;
        
        UIUtils.configurarJanela(this, "Decor Puf - Frente de Caixa (PDV)");
        setLayout(new BorderLayout());
        setBackground(UIUtils.COR_FUNDO);

        add(UIUtils.criarPainelHeader("Catálogo & Vendas", "Selecione a modalidade, adicione ao carrinho e finalize"), BorderLayout.NORTH);

        JPanel painelSplit = new JPanel(new GridLayout(1, 2, 20, 0));
        painelSplit.setBackground(UIUtils.COR_FUNDO);
        painelSplit.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // ==========================================
        // LADO ESQUERDO: CATÁLOGO DE PRODUTOS
        // ==========================================
        JPanel painelCatalogo = UIUtils.criarCard();
        painelCatalogo.setLayout(new BorderLayout(10, 10));
        
        // --- CORREÇÃO DO FILTRO QUE SUMIU ---
        JPanel painelTopoCatalogo = new JPanel(new BorderLayout(10, 0));
        painelTopoCatalogo.setOpaque(false);
        painelTopoCatalogo.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        
        JLabel lblTituloCatalogo = UIUtils.criarLabel("📦 Produtos");
        lblTituloCatalogo.setFont(UIUtils.FONTE_SUBTITULO);
        painelTopoCatalogo.add(lblTituloCatalogo, BorderLayout.WEST);
        
        // Blindagem: Colocando em um FlowLayout com tamanho forçado para não ser esmagado
        JPanel painelFiltro = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        painelFiltro.setOpaque(false);
        
        JLabel lblFiltro = UIUtils.criarLabel("Filtro: ");
        lblFiltro.setFont(UIUtils.FONTE_CORPO);
        painelFiltro.add(lblFiltro);

        cbFiltroCategoria = new JComboBox<>(new String[]{"Todas as Categorias", "Kits", "Adereços", "Painéis", "Móveis"});
        cbFiltroCategoria.setFont(UIUtils.FONTE_CORPO);
        cbFiltroCategoria.setPreferredSize(new Dimension(170, 30)); // Força a largura na tela!
        cbFiltroCategoria.setBackground(Color.WHITE);
        cbFiltroCategoria.addActionListener(e -> aplicarFiltroCategoria());
        painelFiltro.add(cbFiltroCategoria);
        
        painelTopoCatalogo.add(painelFiltro, BorderLayout.EAST);
        painelCatalogo.add(painelTopoCatalogo, BorderLayout.NORTH);
        // ------------------------------------

        String[] colunasCat = {"ID", "Produto", "Venda (R$)", "Aluguel (R$)"};
        modeloCatalogo = new DefaultTableModel(colunasCat, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tabelaCatalogo = new JTable(modeloCatalogo);
        UIUtils.estilizarTabela(tabelaCatalogo);
        tabelaCatalogo.getTableHeader().setForeground(Color.BLACK);
        tabelaCatalogo.getTableHeader().setBackground(new Color(230, 230, 250));
        tabelaCatalogo.getColumnModel().getColumn(0).setMaxWidth(50); 

        painelCatalogo.add(new JScrollPane(tabelaCatalogo), BorderLayout.CENTER);

        JPanel painelAcaoRapida = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        painelAcaoRapida.setBackground(Color.WHITE);
        painelAcaoRapida.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, UIUtils.COR_BORDA));
        
        rbVenda = new JRadioButton("Venda");
        rbAluguel = new JRadioButton("Aluguel");
        rbVenda.setSelected(true);
        rbVenda.setBackground(Color.WHITE);
        rbAluguel.setBackground(Color.WHITE);
        
        ButtonGroup grupoTipo = new ButtonGroup();
        grupoTipo.add(rbVenda);
        grupoTipo.add(rbAluguel);

        painelAcaoRapida.add(rbVenda);
        painelAcaoRapida.add(rbAluguel);
        
        painelAcaoRapida.add(UIUtils.criarLabel(" | Qtd:"));
        spinQtd = new JSpinner(new SpinnerNumberModel(1, 1, 999, 1));
        spinQtd.setFont(UIUtils.FONTE_CORPO);
        painelAcaoRapida.add(spinQtd);

        JButton btnAdicionar = UIUtils.criarBotaoPrimario("Adicionar ➔");
        btnAdicionar.addActionListener(e -> adicionarItemAoCarrinho());
        painelAcaoRapida.add(btnAdicionar);

        painelCatalogo.add(painelAcaoRapida, BorderLayout.SOUTH);

        // ==========================================
        // LADO DIREITO: CARRINHO E FECHAMENTO
        // ==========================================
        JPanel painelPedido = UIUtils.criarCard();
        painelPedido.setLayout(new BorderLayout(10, 10));

        JPanel painelConfigPedido = new JPanel(new BorderLayout(5, 5));
        painelConfigPedido.setOpaque(false);
        painelConfigPedido.add(UIUtils.criarLabel("Selecione o Cliente vinculado a esta operação:"), BorderLayout.NORTH);
        
        cbClientes = new JComboBox<>();
        cbClientes.setFont(UIUtils.FONTE_CORPO);
        painelConfigPedido.add(cbClientes, BorderLayout.CENTER);

        painelPedido.add(painelConfigPedido, BorderLayout.NORTH);

        String[] colunasCar = {"ID Prod", "Produto", "Modalidade", "Qtd", "Subtotal"};
        modeloCarrinho = new DefaultTableModel(colunasCar, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tabelaCarrinho = new JTable(modeloCarrinho);
        UIUtils.estilizarTabela(tabelaCarrinho);
        tabelaCarrinho.getTableHeader().setForeground(Color.BLACK);
        tabelaCarrinho.getTableHeader().setBackground(new Color(255, 245, 200));
        tabelaCarrinho.getColumnModel().getColumn(0).setMinWidth(0);
        tabelaCarrinho.getColumnModel().getColumn(0).setMaxWidth(0); 

        painelPedido.add(new JScrollPane(tabelaCarrinho), BorderLayout.CENTER);

        JPanel painelFechamento = new JPanel(new BorderLayout());
        painelFechamento.setOpaque(false);
        
        lblTotal = UIUtils.criarLabel("Total: R$ 0,00");
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTotal.setForeground(UIUtils.COR_PRIMARIA_ESCURA);
        painelFechamento.add(lblTotal, BorderLayout.WEST);

        JButton btnFinalizar = UIUtils.criarBotaoAcento("✅ Finalizar Pedido");
        btnFinalizar.setPreferredSize(new Dimension(200, 45));
        btnFinalizar.addActionListener(e -> finalizarPedido());
        painelFechamento.add(btnFinalizar, BorderLayout.EAST);

        painelPedido.add(painelFechamento, BorderLayout.SOUTH);

        painelSplit.add(painelCatalogo);
        painelSplit.add(painelPedido);
        add(painelSplit, BorderLayout.CENTER);

        JPanel rodapeGeral = new JPanel(new FlowLayout(FlowLayout.LEFT));
        rodapeGeral.setBackground(UIUtils.COR_FUNDO);
        JButton btnVoltar = UIUtils.criarBotaoPerigo("Voltar ao Menu");
        btnVoltar.addActionListener(e -> voltarAoMenu());
        rodapeGeral.add(btnVoltar);
        add(rodapeGeral, BorderLayout.SOUTH);

        carregarClientes();
        carregarProdutosDB();
    }

    private void carregarClientes() {
        try {
            List<Cliente> clientes = new ClienteDAO().listarTodos();
            for (Cliente c : clientes) cbClientes.addItem(c);
        } catch (SQLException ex) { ex.printStackTrace(); }
    }

    private void carregarProdutosDB() {
        try {
            todosProdutosDB = new ProdutoDAO().listarTodos();
            aplicarFiltroCategoria(); 
        } catch (SQLException ex) { 
            ex.printStackTrace(); 
        }
    }

    private void aplicarFiltroCategoria() {
        modeloCatalogo.setRowCount(0); 
        String filtroSelecionado = (String) cbFiltroCategoria.getSelectedItem();

        for (Produto p : todosProdutosDB) {
            if (filtroSelecionado.equals("Todas as Categorias") || (p.getCategoria() != null && p.getCategoria().equalsIgnoreCase(filtroSelecionado))) {
                modeloCatalogo.addRow(new Object[]{p.getId(), p.getNome(), p.getValorVenda(), p.getValorAluguel()});
            }
        }
    }

    private void adicionarItemAoCarrinho() {
        int linha = tabelaCatalogo.getSelectedRow();
        if (linha == -1) {
            UIUtils.mostrarErro(this, "Selecione um produto do catálogo na tabela da esquerda!");
            return;
        }

        boolean isAluguel = rbAluguel.isSelected();
        String modalidadeAtual = isAluguel ? "Aluguel" : "Venda";

        if (modeloCarrinho.getRowCount() > 0) {
            String modalidadeNoCarrinho = (String) modeloCarrinho.getValueAt(0, 2);
            if (!modalidadeNoCarrinho.equals(modalidadeAtual)) {
                UIUtils.mostrarErro(this, "Atenção: Para garantir o controle de estoque e devoluções, não é permitido misturar Venda e Aluguel no mesmo pedido.\n\nPor favor, finalize o pedido de '" + modalidadeNoCarrinho + "' que já está no carrinho primeiro.");
                return; 
            }
        }

        int idProd = (int) tabelaCatalogo.getValueAt(linha, 0);
        String nomeProd = (String) tabelaCatalogo.getValueAt(linha, 1);
        double valorVenda = (double) tabelaCatalogo.getValueAt(linha, 2);
        double valorAluguel = (double) tabelaCatalogo.getValueAt(linha, 3);
        double valorUnitario = isAluguel ? valorAluguel : valorVenda;

        if (valorUnitario <= 0) {
            UIUtils.mostrarErro(this, "O produto '" + nomeProd + "' não está disponível para " + modalidadeAtual + ".");
            return;
        }

        int qtdParaAdicionar = (int) spinQtd.getValue();
        boolean itemJaExisteNoCarrinho = false;

        for (int i = 0; i < modeloCarrinho.getRowCount(); i++) {
            int idTabela = (int) modeloCarrinho.getValueAt(i, 0);
            if (idTabela == idProd) {
                int qtdAtual = (int) modeloCarrinho.getValueAt(i, 3);
                int novaQtd = qtdAtual + qtdParaAdicionar;
                double novoSubtotal = novaQtd * valorUnitario;

                modeloCarrinho.setValueAt(novaQtd, i, 3);
                modeloCarrinho.setValueAt(novoSubtotal, i, 4);
                itemJaExisteNoCarrinho = true;
                break;
            }
        }

        if (!itemJaExisteNoCarrinho) {
            double subtotal = qtdParaAdicionar * valorUnitario;
            modeloCarrinho.addRow(new Object[]{idProd, nomeProd, modalidadeAtual, qtdParaAdicionar, subtotal});
        }
        
        spinQtd.setValue(1); 
        atualizarTotal();
    }

    private void atualizarTotal() {
        valorTotalPedido = 0.0;
        for (int i = 0; i < modeloCarrinho.getRowCount(); i++) {
            valorTotalPedido += (double) modeloCarrinho.getValueAt(i, 4);
        }
        lblTotal.setText(UIUtils.formatarMoeda(valorTotalPedido));
    }

    private void finalizarPedido() {
        if (modeloCarrinho.getRowCount() == 0) {
            UIUtils.mostrarErro(this, "O carrinho está vazio!");
            return;
        }
        if (cbClientes.getSelectedItem() == null) {
            UIUtils.mostrarErro(this, "Selecione um cliente para vincular ao pedido.");
            return;
        }

        String tipoPedido = (String) modeloCarrinho.getValueAt(0, 2); 
        
        String statusInteligente = tipoPedido.equals("Aluguel") ? "Em Andamento" : "Concluído";
        String dataAtual = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        String dataDevolucao = tipoPedido.equals("Aluguel") ? LocalDateTime.now().plusHours(72).format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : null;

        Cliente cliente = (Cliente) cbClientes.getSelectedItem();
        String sqlPedido = "INSERT INTO Pedido (cliente_id, funcionario_id, tipo_pedido, data_criacao, data_devolucao_prevista, status, valor_total) VALUES (?, ?, ?, ?, ?, ?, ?)";
        String sqlItem = "INSERT INTO ItemPedido (pedido_id, produto_id, quantidade, subtotal) VALUES (?, ?, ?, ?)";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement psPedido = conn.prepareStatement(sqlPedido, Statement.RETURN_GENERATED_KEYS)) {

            psPedido.setInt(1, cliente.getId());
            psPedido.setInt(2, usuarioLogado.getId());
            psPedido.setString(3, tipoPedido);
            psPedido.setString(4, dataAtual);
            psPedido.setString(5, dataDevolucao);
            psPedido.setString(6, statusInteligente);
            psPedido.setDouble(7, valorTotalPedido);
            psPedido.executeUpdate();

            int idPedidoGerado = -1;
            try (ResultSet rs = psPedido.getGeneratedKeys()) {
                if (rs.next()) idPedidoGerado = rs.getInt(1);
            }

            try (PreparedStatement psItem = conn.prepareStatement(sqlItem)) {
                for (int i = 0; i < modeloCarrinho.getRowCount(); i++) {
                    psItem.setInt(1, idPedidoGerado);
                    psItem.setInt(2, (int) modeloCarrinho.getValueAt(i, 0)); 
                    psItem.setInt(3, (int) modeloCarrinho.getValueAt(i, 3)); 
                    psItem.setDouble(4, (double) modeloCarrinho.getValueAt(i, 4)); 
                    psItem.executeUpdate();
                }
            }

            String msg = "Pedido de " + tipoPedido + " finalizado com sucesso!\nStatus definido como: " + statusInteligente + ".\nValor Total: " + UIUtils.formatarMoeda(valorTotalPedido);
            UIUtils.mostrarSucesso(this, msg);
            
            modeloCarrinho.setRowCount(0);
            atualizarTotal();

        } catch (SQLException ex) {
            UIUtils.mostrarErro(this, "Erro crítico ao salvar o pedido no banco de dados:\n" + ex.getMessage());
        }
    }

    private void voltarAoMenu() {
        new TelaMenuPrincipal(this.usuarioLogado).setVisible(true);
        this.dispose();
    }
}