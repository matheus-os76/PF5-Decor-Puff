package com.decorpuf.view;

import com.decorpuf.dao.ProdutoDAO;
import com.decorpuf.model.Funcionario; // <- Esta era a linha que estava faltando!
import com.decorpuf.model.Produto;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class TelaCadastrarProduto extends JFrame {

    private Funcionario usuarioLogado;
    
    private JTextField txtNome;
    private JComboBox<String> cbCategoria;
    private JTextArea txtDescricao;
    private JCheckBox chkVenda, chkAluguel;
    private JTextField txtValorVenda, txtValorAluguel;

    // Construtor agora recebe o usuário e salva para não perder a sessão
    public TelaCadastrarProduto(Funcionario usuarioLogado) {
        this.usuarioLogado = usuarioLogado;
        
        UIUtils.configurarJanela(this, "Decor Puf - Cadastrar Produto");
        setLayout(new BorderLayout());
        setBackground(UIUtils.COR_FUNDO);

        add(UIUtils.criarPainelHeader("Catálogo de Produtos", "Adicione novos itens para venda ou aluguel"), BorderLayout.NORTH);

        JPanel painelCentral = new JPanel(new GridBagLayout());
        painelCentral.setBackground(UIUtils.COR_FUNDO);

        JPanel card = UIUtils.criarCard();
        card.setLayout(new GridBagLayout());
        card.setPreferredSize(new Dimension(600, 600));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Linha 1: Nome e Categoria
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.7;
        card.add(UIUtils.criarLabel("Nome do Produto:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.3;
        card.add(UIUtils.criarLabel("Categoria:"), gbc);

        gbc.gridy = 1; gbc.gridx = 0;
        txtNome = UIUtils.criarTextField(20); card.add(txtNome, gbc);
        
        gbc.gridx = 1;
        // As categorias padrão do seu projeto original!
        cbCategoria = UIUtils.criarComboBox(new String[]{"Kits", "Adereços", "Painéis", "Móveis"});
        card.add(cbCategoria, gbc);

        // Linha 2: Descrição
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        card.add(UIUtils.criarLabel("Descrição:"), gbc);
        
        gbc.gridy = 3;
        txtDescricao = new JTextArea(3, 20);
        txtDescricao.setLineWrap(true);
        txtDescricao.setWrapStyleWord(true);
        txtDescricao.setFont(UIUtils.FONTE_CORPO);
        txtDescricao.setBorder(BorderFactory.createLineBorder(UIUtils.COR_BORDA));
        card.add(new JScrollPane(txtDescricao), gbc);

        // Linha 3: Controle Financeiro Dinâmico
        JPanel painelPrecos = new JPanel(new GridLayout(2, 2, 15, 5));
        painelPrecos.setOpaque(false);
        painelPrecos.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(UIUtils.COR_BORDA), "Modalidade Comercial"));

        chkVenda = new JCheckBox("Permite Venda");
        chkAluguel = new JCheckBox("Permite Aluguel");
        
        txtValorVenda = UIUtils.criarTextField(10);
        txtValorVenda.setEnabled(false); // Inicia bloqueado
        
        txtValorAluguel = UIUtils.criarTextField(10);
        txtValorAluguel.setEnabled(false); // Inicia bloqueado

        // Filtro para impedir letras nos campos de valor
        java.awt.event.KeyAdapter filtroNumeros = new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                char c = evt.getKeyChar();
                if (!Character.isDigit(c) && c != ',' && c != '.') {
                    evt.consume();
                }
            }
        };
        txtValorVenda.addKeyListener(filtroNumeros);
        txtValorAluguel.addKeyListener(filtroNumeros);

        // Lógica visual de ativar/desativar
        chkVenda.addActionListener(e -> txtValorVenda.setEnabled(chkVenda.isSelected()));
        chkAluguel.addActionListener(e -> txtValorAluguel.setEnabled(chkAluguel.isSelected()));

        painelPrecos.add(chkVenda);
        painelPrecos.add(chkAluguel);
        painelPrecos.add(txtValorVenda);
        painelPrecos.add(txtValorAluguel);

        gbc.gridy = 4; gbc.insets = new Insets(15, 10, 15, 10);
        card.add(painelPrecos, gbc);

        // Botoes
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        painelBotoes.setOpaque(false);
        JButton btnVoltar = UIUtils.criarBotaoPerigo("Voltar");
        btnVoltar.addActionListener(e -> voltarAoMenu());
        JButton btnSalvar = UIUtils.criarBotaoPrimario("Salvar Produto");
        btnSalvar.addActionListener(e -> salvarProduto());

        painelBotoes.add(btnVoltar); painelBotoes.add(btnSalvar);
        gbc.gridy = 5; card.add(painelBotoes, gbc);

        painelCentral.add(card);
        add(painelCentral, BorderLayout.CENTER);
    }

    private void salvarProduto() {
        String nome = txtNome.getText().trim();
        String descricao = txtDescricao.getText().trim();
        String categoria = (String) cbCategoria.getSelectedItem();
        
        boolean permiteVenda = chkVenda.isSelected();
        boolean permiteAluguel = chkAluguel.isSelected();

        if (nome.isEmpty()) {
            UIUtils.mostrarErro(this, "O nome do produto é obrigatório!");
            return;
        }
        if (!permiteVenda && !permiteAluguel) {
            UIUtils.mostrarErro(this, "O produto deve estar disponível para Venda ou Aluguel.");
            return;
        }

        double valorVenda = 0.0;
        double valorAluguel = 0.0;

        try {
            if (permiteVenda) {
                String valStr = txtValorVenda.getText().replace(",", ".");
                if (valStr.isEmpty()) {
                    UIUtils.mostrarErro(this, "Preencha o valor de venda.");
                    return;
                }
                valorVenda = Double.parseDouble(valStr);
            }
            if (permiteAluguel) {
                String valStr = txtValorAluguel.getText().replace(",", ".");
                if (valStr.isEmpty()) {
                    UIUtils.mostrarErro(this, "Preencha o valor de aluguel.");
                    return;
                }
                valorAluguel = Double.parseDouble(valStr);
            }
        } catch (NumberFormatException e) {
            UIUtils.mostrarErro(this, "Insira um valor financeiro válido (ex: 150.00).");
            return;
        }

        Produto p = new Produto();
        p.setNome(nome);
        p.setCategoria(categoria);
        p.setDescricao(descricao);
        p.setPermiteVenda(permiteVenda);
        p.setPermiteAluguel(permiteAluguel);
        p.setValorVenda(valorVenda);
        p.setValorAluguel(valorAluguel);
        p.setEstoqueInicial(1);

        try {
            ProdutoDAO dao = new ProdutoDAO();
            dao.inserir(p);
            UIUtils.mostrarSucesso(this, "Produto '" + nome + "' salvo com sucesso no banco de dados!");
            
            // REDIRECIONA PARA A TELA DE GERENCIAR PRODUTOS PASSANDO O USUARIO
            new TelaGerenciarProdutos(this.usuarioLogado).setVisible(true);
            this.dispose();

        } catch (SQLException ex) {
            UIUtils.mostrarErro(this, "Erro ao salvar no banco de dados:\n" + ex.getMessage());
        }
    }

    private void voltarAoMenu() {
        // REDIRECIONA PARA A TELA DE GERENCIAR PRODUTOS PASSANDO O USUARIO
        new TelaGerenciarProdutos(this.usuarioLogado).setVisible(true);
        this.dispose();
    }
}