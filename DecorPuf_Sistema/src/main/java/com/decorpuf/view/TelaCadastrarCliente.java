package com.decorpuf.view;

import com.decorpuf.dao.ClienteDAO;
import com.decorpuf.model.Cliente;
import com.decorpuf.model.Funcionario; // Importação necessária

import javax.swing.*;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.text.ParseException;
import java.util.List;

public class TelaCadastrarCliente extends JFrame {

    private Funcionario usuarioLogado;
    private JTextField txtNome;
    private JTextField txtEmail;
    private JFormattedTextField txtCpf;
    private JFormattedTextField txtTelefone;
    private Cliente clienteEmEdicao = null; 

    public TelaCadastrarCliente(Funcionario usuarioLogado) {
        this.usuarioLogado = usuarioLogado;
        inicializarComponentes("Cadastro de Clientes", "Registre um novo cliente no sistema", "Salvar Cliente");
    }

    public TelaCadastrarCliente(Funcionario usuarioLogado, Cliente cliente) {
        this.usuarioLogado = usuarioLogado;
        this.clienteEmEdicao = cliente;
        inicializarComponentes("Editar Cliente", "Altere os dados do cliente selecionado", "Atualizar Dados");
        preencherDadosParaEdicao();
    }

    private void inicializarComponentes(String titulo, String subtitulo, String textoBotaoSalvar) {
        UIUtils.configurarJanela(this, "Decor Puf - " + titulo);
        setLayout(new BorderLayout());
        setBackground(UIUtils.COR_FUNDO);

        add(UIUtils.criarPainelHeader(titulo, subtitulo), BorderLayout.NORTH);

        JPanel painelCentral = new JPanel(new GridBagLayout());
        painelCentral.setBackground(UIUtils.COR_FUNDO);

        JPanel card = UIUtils.criarCard();
        card.setLayout(new GridBagLayout());
        card.setPreferredSize(new Dimension(600, 550));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 15, 5, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0; gbc.gridwidth = 2;

        gbc.gridy = 0; card.add(UIUtils.criarLabel("Nome Completo (Apenas letras):"), gbc);
        gbc.gridy = 1; txtNome = UIUtils.criarTextField(30); 
        txtNome.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                if (Character.isDigit(evt.getKeyChar())) evt.consume();
            }
        });
        card.add(txtNome, gbc);

        gbc.gridy = 2; card.add(UIUtils.criarLabel("E-mail:"), gbc);
        gbc.gridy = 3; txtEmail = UIUtils.criarTextField(30); card.add(txtEmail, gbc);

        try {
            gbc.gridy = 4; card.add(UIUtils.criarLabel("CPF:"), gbc);
            gbc.gridy = 5; 
            MaskFormatter mascaraCpf = new MaskFormatter("###.###.###-##");
            txtCpf = new JFormattedTextField(mascaraCpf);
            txtCpf.setFont(UIUtils.FONTE_CORPO);
            txtCpf.setPreferredSize(new Dimension(0, 36));
            card.add(txtCpf, gbc);

            gbc.gridy = 6; card.add(UIUtils.criarLabel("Telefone/WhatsApp:"), gbc);
            gbc.gridy = 7; 
            MaskFormatter mascaraTel = new MaskFormatter("(##) #####-####");
            txtTelefone = new JFormattedTextField(mascaraTel);
            txtTelefone.setFont(UIUtils.FONTE_CORPO);
            txtTelefone.setPreferredSize(new Dimension(0, 36));
            card.add(txtTelefone, gbc);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        painelBotoes.setOpaque(false);
        
        JButton btnVoltar = UIUtils.criarBotaoPerigo("Cancelar");
        btnVoltar.addActionListener(e -> voltarAoMenu());
        
        JButton btnSalvar = UIUtils.criarBotaoPrimario(textoBotaoSalvar);
        btnSalvar.addActionListener(e -> salvarCliente());

        painelBotoes.add(btnVoltar);
        painelBotoes.add(btnSalvar);
        
        gbc.gridy = 8; gbc.insets = new Insets(30, 15, 10, 15);
        card.add(painelBotoes, gbc);

        painelCentral.add(card);
        add(painelCentral, BorderLayout.CENTER);
    }

    private void preencherDadosParaEdicao() {
        txtNome.setText(clienteEmEdicao.getNome());
        txtEmail.setText(clienteEmEdicao.getEmail());
        
        String cpfBanco = clienteEmEdicao.getCpf();
        if (cpfBanco != null && cpfBanco.length() == 11) {
            cpfBanco = cpfBanco.substring(0, 3) + "." + cpfBanco.substring(3, 6) + "." + cpfBanco.substring(6, 9) + "-" + cpfBanco.substring(9, 11);
        }
        txtCpf.setText(cpfBanco);

        String telBanco = clienteEmEdicao.getTelefone();
        if (telBanco != null && telBanco.length() == 11) {
            telBanco = "(" + telBanco.substring(0, 2) + ") " + telBanco.substring(2, 7) + "-" + telBanco.substring(7, 11);
        }
        txtTelefone.setText(telBanco);
    }

    private void salvarCliente() {
        String nome = txtNome.getText().trim();
        String email = txtEmail.getText().trim();
        String cpf = txtCpf.getText().replaceAll("[^0-9]", "");
        String telefone = txtTelefone.getText().replaceAll("[^0-9]", "");

        if (nome.isEmpty() || cpf.isEmpty() || email.isEmpty() || telefone.isEmpty()) {
            UIUtils.mostrarErro(this, "Nome, E-mail, CPF e Telefone são campos obrigatórios!");
            return;
        }

        if (!email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) {
            UIUtils.mostrarErro(this, "Insira um e-mail válido (ex: cliente@email.com).");
            txtEmail.requestFocus();
            return;
        }

        try {
            ClienteDAO dao = new ClienteDAO();
            List<Cliente> clientesExistentes = dao.listarTodos();
            
            for (Cliente c : clientesExistentes) {
                if (clienteEmEdicao != null && c.getId() == clienteEmEdicao.getId()) {
                    continue; 
                }
                if (c.getCpf().equals(cpf)) {
                    UIUtils.mostrarErro(this, "Erro: Este CPF já está cadastrado no sistema!");
                    return;
                }
                if (c.getEmail().equalsIgnoreCase(email)) {
                    UIUtils.mostrarErro(this, "Erro: Este E-mail já está sendo usado!");
                    return;
                }
                if (c.getTelefone().equals(telefone)) {
                    UIUtils.mostrarErro(this, "Erro: Este Telefone já está cadastrado!");
                    return;
                }
            }

            if (clienteEmEdicao == null) {
                Cliente novoCliente = new Cliente();
                novoCliente.setNome(nome);
                novoCliente.setEmail(email);
                novoCliente.setCpf(cpf);
                novoCliente.setTelefone(telefone);
                dao.inserir(novoCliente);
                UIUtils.mostrarSucesso(this, "Cliente " + nome + " cadastrado com sucesso!");
            } else {
                clienteEmEdicao.setNome(nome);
                clienteEmEdicao.setEmail(email);
                clienteEmEdicao.setCpf(cpf);
                clienteEmEdicao.setTelefone(telefone);
                dao.atualizar(clienteEmEdicao); 
                UIUtils.mostrarSucesso(this, "Dados atualizados com sucesso!");
            }

            new TelaGerenciarClientes(this.usuarioLogado).setVisible(true);
            this.dispose(); 
            
        } catch (Exception ex) {
            UIUtils.mostrarErro(this, "Erro ao acessar o banco de dados:\n" + ex.getMessage());
        }
    }

    private void voltarAoMenu() {
        new TelaGerenciarClientes(this.usuarioLogado).setVisible(true);
        this.dispose();
    }
}