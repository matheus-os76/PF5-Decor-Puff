package com.decorpuf.view;

import com.decorpuf.dao.ConnectionFactory;
import com.decorpuf.model.Funcionario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TelaRelatorios extends JFrame {
    
    private Funcionario usuarioLogado;
    
    private JLabel lblFaturamento;
    private JLabel lblTotalPedidos;
    private JLabel lblPendentes;
    
    private JComboBox<String> cbFiltroStatus;
    private JComboBox<String> cbFiltroTipo;
    private JComboBox<String> cbFiltroPeriodo;
    
    private DefaultTableModel modeloTabela;

    public TelaRelatorios(Funcionario usuarioLogado) {
        this.usuarioLogado = usuarioLogado;
        
        UIUtils.configurarJanela(this, "Decor Puf - Painel Gerencial Interativo");
        setLayout(new BorderLayout());
        setBackground(UIUtils.COR_FUNDO);

        add(UIUtils.criarPainelHeader("Painel Gerencial", "Filtre e analise as métricas financeiras em tempo real"), BorderLayout.NORTH);

        JPanel painelCentral = new JPanel(new BorderLayout(20, 15));
        painelCentral.setBackground(UIUtils.COR_FUNDO);
        painelCentral.setBorder(BorderFactory.createEmptyBorder(15, 30, 20, 30));

        // ==========================================
        // 1. CARDS DE INDICADORES (KPIs)
        // ==========================================
        JPanel painelTopo = new JPanel(new BorderLayout(0, 15));
        painelTopo.setOpaque(false);

        JPanel painelKPIs = new JPanel(new GridLayout(1, 3, 20, 0));
        painelKPIs.setBackground(UIUtils.COR_FUNDO);

        JPanel cardFaturamento = criarCardKPI("Faturamento (Concluídos)", "R$ 0,00", new Color(40, 167, 69));
        lblFaturamento = (JLabel) cardFaturamento.getComponent(1); 
        
        JPanel cardPedidos = criarCardKPI("Total de Pedidos Listados", "0", new Color(0, 123, 255)); 
        lblTotalPedidos = (JLabel) cardPedidos.getComponent(1);
        
        JPanel cardPendentes = criarCardKPI("Aluguéis Pendentes (Filtro)", "0", new Color(255, 193, 7)); 
        lblPendentes = (JLabel) cardPendentes.getComponent(1);

        painelKPIs.add(cardFaturamento);
        painelKPIs.add(cardPedidos);
        painelKPIs.add(cardPendentes);
        
        painelTopo.add(painelKPIs, BorderLayout.NORTH);

        // ==========================================
        // 2. BARRA DE FILTROS INTELIGENTES (CORRIGIDA)
        // ==========================================
        JPanel painelFiltros = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        painelFiltros.setBackground(Color.WHITE);
        painelFiltros.setBorder(BorderFactory.createLineBorder(UIUtils.COR_BORDA));

        painelFiltros.add(UIUtils.criarLabel("Status:"));
        cbFiltroStatus = new JComboBox<>(new String[]{"Todos", "Concluído", "Em Andamento", "Atrasado", "Cancelado"});
        cbFiltroStatus.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cbFiltroStatus.setPreferredSize(new Dimension(140, 30)); // Força a largura para não sumir
        cbFiltroStatus.setBackground(Color.WHITE);
        painelFiltros.add(cbFiltroStatus);

        painelFiltros.add(UIUtils.criarLabel("Tipo:"));
        cbFiltroTipo = new JComboBox<>(new String[]{"Todos", "Venda", "Aluguel"});
        cbFiltroTipo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cbFiltroTipo.setPreferredSize(new Dimension(100, 30));
        cbFiltroTipo.setBackground(Color.WHITE);
        painelFiltros.add(cbFiltroTipo);

        painelFiltros.add(UIUtils.criarLabel("Período:"));
        cbFiltroPeriodo = new JComboBox<>(new String[]{"Todo o Período", "Últimos 7 Dias", "Últimos 30 Dias"});
        cbFiltroPeriodo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cbFiltroPeriodo.setPreferredSize(new Dimension(150, 30));
        cbFiltroPeriodo.setBackground(Color.WHITE);
        painelFiltros.add(cbFiltroPeriodo);

        // Removi a lupa que estava bugando e forcei o tamanho do botão
        JButton btnFiltrar = UIUtils.criarBotaoPrimario("Aplicar Filtros");
        btnFiltrar.setPreferredSize(new Dimension(150, 35));
        btnFiltrar.addActionListener(e -> carregarDadosFiltrados());
        painelFiltros.add(btnFiltrar);

        painelTopo.add(painelFiltros, BorderLayout.SOUTH);
        painelCentral.add(painelTopo, BorderLayout.NORTH);

        // ==========================================
        // 3. TABELA DE RESULTADOS
        // ==========================================
        JPanel painelTabela = new JPanel(new BorderLayout(5, 5));
        painelTabela.setOpaque(false);
        
        String[] colunas = {"Nº Pedido", "Cliente", "Tipo", "Data da Operação", "Status", "Valor (R$)"};
        modeloTabela = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        JTable tabela = new JTable(modeloTabela);
        UIUtils.estilizarTabela(tabela);
        tabela.getTableHeader().setForeground(Color.BLACK);
        tabela.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        tabela.getTableHeader().setBackground(new Color(230, 230, 250));
        tabela.getColumnModel().getColumn(0).setMaxWidth(80);

        JScrollPane scrollPane = new JScrollPane(tabela);
        scrollPane.getViewport().setBackground(Color.WHITE);
        painelTabela.add(scrollPane, BorderLayout.CENTER);

        painelCentral.add(painelTabela, BorderLayout.CENTER);

        // ==========================================
        // 4. RODAPÉ
        // ==========================================
        JPanel rodape = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rodape.setBackground(UIUtils.COR_FUNDO);
        JButton btnVoltar = UIUtils.criarBotaoPerigo("Voltar ao Menu Principal");
        btnVoltar.addActionListener(e -> voltarAoMenu());
        rodape.add(btnVoltar);
        
        add(painelCentral, BorderLayout.CENTER);
        add(rodape, BorderLayout.SOUTH);

        carregarDadosFiltrados();
    }

    private JPanel criarCardKPI(String titulo, String valorPadrao, Color corDestaque) {
        JPanel card = UIUtils.criarCard();
        card.setLayout(new GridLayout(2, 1, 0, 5));
        card.setPreferredSize(new Dimension(200, 110));
        
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(5, 0, 0, 0, corDestaque),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel lblTitulo = UIUtils.criarLabel(titulo);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTitulo.setForeground(Color.DARK_GRAY);
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel lblValor = UIUtils.criarLabel(valorPadrao);
        lblValor.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblValor.setForeground(Color.BLACK);
        lblValor.setHorizontalAlignment(SwingConstants.CENTER);

        card.add(lblTitulo);
        card.add(lblValor);

        return card;
    }

    private void carregarDadosFiltrados() {
        String statusFiltro = (String) cbFiltroStatus.getSelectedItem();
        String tipoFiltro = (String) cbFiltroTipo.getSelectedItem();
        String periodoFiltro = (String) cbFiltroPeriodo.getSelectedItem();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        LocalDateTime agora = LocalDateTime.now();

        double faturamentoTotal = 0.0;
        int contPedidos = 0;
        int contPendentes = 0;

        modeloTabela.setRowCount(0);

        String sql = "SELECT p.id, c.nome, p.tipo_pedido, p.data_criacao, p.status, p.valor_total " +
                     "FROM Pedido p INNER JOIN Cliente c ON p.cliente_id = c.id ORDER BY p.id DESC";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String statusDB = rs.getString("status");
                String tipoDB = rs.getString("tipo_pedido");
                String dataCriacaoStr = rs.getString("data_criacao");
                double valorTotal = rs.getDouble("valor_total");

                if (!statusFiltro.equals("Todos") && !statusDB.equals(statusFiltro)) {
                    continue; 
                }

                if (!tipoFiltro.equals("Todos") && !tipoDB.equals(tipoFiltro)) {
                    continue;
                }

                if (!periodoFiltro.equals("Todo o Período")) {
                    try {
                        LocalDateTime dataPedido = LocalDateTime.parse(dataCriacaoStr, formatter);
                        if (periodoFiltro.equals("Últimos 7 Dias") && dataPedido.isBefore(agora.minusDays(7))) {
                            continue;
                        }
                        if (periodoFiltro.equals("Últimos 30 Dias") && dataPedido.isBefore(agora.minusDays(30))) {
                            continue;
                        }
                    } catch (Exception e) {
                        System.err.println("Erro ao converter data do pedido #" + rs.getInt("id"));
                    }
                }

                modeloTabela.addRow(new Object[]{
                    "#" + String.format("%04d", rs.getInt("id")),
                    rs.getString("nome"),
                    tipoDB,
                    dataCriacaoStr,
                    statusDB,
                    UIUtils.formatarMoeda(valorTotal)
                });

                contPedidos++;
                
                if (statusDB.equals("Concluído")) {
                    faturamentoTotal += valorTotal;
                }
                
                if (statusDB.equals("Em Andamento") || statusDB.equals("Atrasado")) {
                    contPendentes++;
                }
            }

            lblFaturamento.setText(UIUtils.formatarMoeda(faturamentoTotal));
            lblTotalPedidos.setText(String.valueOf(contPedidos));
            lblPendentes.setText(String.valueOf(contPendentes));

        } catch (Exception ex) {
            UIUtils.mostrarErro(this, "Erro ao aplicar filtros:\n" + ex.getMessage());
        }
    }

    private void voltarAoMenu() {
        new TelaMenuPrincipal(this.usuarioLogado).setVisible(true); 
        this.dispose();
    }
}