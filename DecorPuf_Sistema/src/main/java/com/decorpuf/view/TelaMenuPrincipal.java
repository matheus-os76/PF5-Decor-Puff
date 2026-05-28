package com.decorpuf.view;

import com.decorpuf.model.Funcionario;

import javax.swing.*;
import java.awt.*;

public class TelaMenuPrincipal extends JFrame {

    private Funcionario usuarioLogado;

    public TelaMenuPrincipal(Funcionario usuarioLogado) {
        this.usuarioLogado = usuarioLogado;
        
        UIUtils.configurarJanela(this, "Decor Puf - Painel Principal");
        setLayout(new BorderLayout());
        setBackground(UIUtils.COR_FUNDO);

        // 1. Header (Cabeçalho)
        JPanel header = UIUtils.criarPainelHeader("Painel de Controle", "Bem-vindo(a), " + usuarioLogado.getNome() + " | Perfil: " + usuarioLogado.getCargo());
        add(header, BorderLayout.NORTH);

        // 2. Centro (Grid de Módulos)
        JPanel painelCentral = new JPanel(new GridBagLayout());
        painelCentral.setBackground(UIUtils.COR_FUNDO);
        
        JPanel gridBotoes = new JPanel(new GridLayout(2, 3, 20, 20));
        gridBotoes.setBackground(UIUtils.COR_FUNDO);
        gridBotoes.setPreferredSize(new Dimension(800, 400));

        // Criação dos Botões dos Módulos
        JButton btnCatalogo = criarBotaoModulo("Catálogo & Vendas", "📦");
        JButton btnPedidos = criarBotaoModulo("Monitorar Pedidos", "📋");
        JButton btnClientes = criarBotaoModulo("Gerenciar Clientes", "👥");
        JButton btnProdutos = criarBotaoModulo("Gerenciar Produtos", "🏷️");
        JButton btnFuncionarios = criarBotaoModulo("Gerenciar Funcionários", "👨‍💼");
        JButton btnRelatorios = criarBotaoModulo("Relatórios", "📊");

        // =========================================================
        // EVENTOS DOS BOTÕES (Navegação Real do Sistema)
        // =========================================================
        
       btnCatalogo.addActionListener(e -> {
            new TelaCatalogoVendas(this.usuarioLogado).setVisible(true);
            this.dispose();
        });
        btnPedidos.addActionListener(e -> {
            new TelaMonitorarPedidos(this.usuarioLogado).setVisible(true);
            this.dispose();
        });
        
        btnClientes.addActionListener(e -> {
            new TelaGerenciarClientes(this.usuarioLogado).setVisible(true);
            this.dispose(); 
        });

        btnProdutos.addActionListener(e -> {
            new TelaGerenciarProdutos(this.usuarioLogado).setVisible(true);
            this.dispose();
        });

       btnFuncionarios.addActionListener(e -> {
            if (this.usuarioLogado.getCargo().equalsIgnoreCase("Gerente")) {
                // A MÁGICA ACONTECE AQUI:
                new TelaGerenciarFuncionarios(this.usuarioLogado).setVisible(true);
                this.dispose();
            } else {
                UIUtils.mostrarErro(this, "Acesso Negado: Apenas Gerentes podem acessar este módulo.");
            }
        });

       btnRelatorios.addActionListener(e -> {
            if (this.usuarioLogado.getCargo().equalsIgnoreCase("Gerente")) {
                // CORREÇÃO: Entregando o "crachá" (this.usuarioLogado) para a tela de relatórios!
                new TelaRelatorios(this.usuarioLogado).setVisible(true);
                this.dispose();
            } else {
                UIUtils.mostrarErro(this, "Acesso Negado: Apenas Gerentes podem acessar os Relatórios Financeiros.");
            }
        });

        // Adiciona ao Grid
        gridBotoes.add(btnCatalogo);
        gridBotoes.add(btnPedidos);
        gridBotoes.add(btnClientes);
        gridBotoes.add(btnProdutos);
        gridBotoes.add(btnFuncionarios);
        gridBotoes.add(btnRelatorios);

        painelCentral.add(gridBotoes);
        add(painelCentral, BorderLayout.CENTER);

        // 3. Rodapé (Logout)
        JPanel rodape = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rodape.setBackground(UIUtils.COR_FUNDO);
        rodape.setBorder(BorderFactory.createEmptyBorder(10, 10, 20, 30));
        
        JButton btnSair = UIUtils.criarBotaoPerigo("Sair do Sistema");
        btnSair.addActionListener(e -> {
            if (UIUtils.confirmar(this, "Deseja realmente sair do sistema?")) {
                new TelaLogin().setVisible(true);
                this.dispose();
            }
        });
        rodape.add(btnSair);
        add(rodape, BorderLayout.SOUTH);
    }

    // Método auxiliar para criar os "Quadrados" do Menu
    private JButton criarBotaoModulo(String titulo, String icone) {
        JButton btn = new JButton("<html><center><font size='6'>" + icone + "</font><br><br>" + titulo + "</center></html>");
        btn.setFont(UIUtils.FONTE_SUBTITULO);
        btn.setBackground(UIUtils.COR_CARD);
        btn.setForeground(UIUtils.COR_PRIMARIA_ESCURA);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(UIUtils.COR_BORDA, 2, true));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(UIUtils.COR_FUNDO);
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(UIUtils.COR_CARD);
            }
        });
        return btn;
    }
}