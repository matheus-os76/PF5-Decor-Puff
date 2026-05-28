package com.decorpuf.view;

import com.decorpuf.dao.ConnectionFactory;
import com.decorpuf.model.Funcionario;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TelaCadastrarFuncionario extends JFrame {

    private Funcionario usuarioLogado;
    private Funcionario funcionarioEmEdicao;
    
    private JTextField txtNome;
    private JComboBox<String> cbCargo;
    private JTextField txtUsername;
    private JPasswordField txtSenha;
    private JPasswordField txtConfirmarSenha; // Novo Campo

    public TelaCadastrarFuncionario(Funcionario usuarioLogado) {
        this.usuarioLogado = usuarioLogado;
        this.funcionarioEmEdicao = null;
        inicializarComponentes("Novo Funcionário", "Crie um novo acesso ao sistema", "Salvar Funcionário");
    }

    public TelaCadastrarFuncionario(Funcionario usuarioLogado, Funcionario funcEdit) {
        this.usuarioLogado = usuarioLogado;
        this.funcionarioEmEdicao = funcEdit;
        inicializarComponentes("Editar Funcionário", "Altere as credenciais de acesso", "Atualizar Dados");
        preencherDadosParaEdicao();
    }

    private void inicializarComponentes(String titulo, String subtitulo, String textoBotao) {
        UIUtils.configurarJanela(this, "Decor Puf - " + titulo);
        setLayout(new BorderLayout());
        setBackground(UIUtils.COR_FUNDO);

        add(UIUtils.criarPainelHeader(titulo, subtitulo), BorderLayout.NORTH);

        JPanel painelCentral = new JPanel(new GridBagLayout());
        painelCentral.setBackground(UIUtils.COR_FUNDO);

        JPanel card = UIUtils.criarCard();
        card.setLayout(new GridBagLayout());
        
        // Aumentei o tamanho para caber o novo campo de Confirmar Senha
        card.setPreferredSize(new Dimension(600, 580));
        card.setMinimumSize(new Dimension(600, 580));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 15, 5, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0; gbc.gridwidth = 2;

        gbc.gridy = 0; card.add(UIUtils.criarLabel("Nome Completo:"), gbc);
        gbc.gridy = 1; txtNome = UIUtils.criarTextField(30); card.add(txtNome, gbc);

        gbc.gridy = 2; card.add(UIUtils.criarLabel("Cargo no Sistema:"), gbc);
        gbc.gridy = 3; 
        cbCargo = UIUtils.criarComboBox(new String[]{"Atendente", "Gerente"});
        card.add(cbCargo, gbc);

        gbc.gridy = 4; card.add(UIUtils.criarLabel("Nome de Usuário (Login):"), gbc);
        gbc.gridy = 5; txtUsername = UIUtils.criarTextField(30); card.add(txtUsername, gbc);

        // --- CAMPO DE SENHA COM O OLHINHO ---
        gbc.gridy = 6; card.add(UIUtils.criarLabel("Senha de Acesso:"), gbc);
        txtSenha = new JPasswordField();
        gbc.gridy = 7; card.add(criarPainelSenhaComOlho(txtSenha), gbc);

        // --- CAMPO DE CONFIRMAR SENHA COM O OLHINHO ---
        gbc.gridy = 8; card.add(UIUtils.criarLabel("Confirmar Senha:"), gbc);
        txtConfirmarSenha = new JPasswordField();
        gbc.gridy = 9; card.add(criarPainelSenhaComOlho(txtConfirmarSenha), gbc);

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        painelBotoes.setOpaque(false);
        JButton btnVoltar = UIUtils.criarBotaoPerigo("Cancelar");
        btnVoltar.addActionListener(e -> voltar());
        JButton btnSalvar = UIUtils.criarBotaoPrimario(textoBotao);
        btnSalvar.addActionListener(e -> salvar());

        painelBotoes.add(btnVoltar); 
        painelBotoes.add(btnSalvar);
        
        gbc.gridy = 10; gbc.insets = new Insets(25, 15, 10, 15);
        card.add(painelBotoes, gbc);

        painelCentral.add(card);
        add(painelCentral, BorderLayout.CENTER);
    }

    // MÁGICA DE UX: Cria o campo envelopado com o botão de revelar senha
    private JPanel criarPainelSenhaComOlho(JPasswordField campoSenha) {
        JPanel painel = new JPanel(new BorderLayout());
        painel.setBackground(Color.WHITE);
        painel.setBorder(BorderFactory.createLineBorder(UIUtils.COR_BORDA));
        
        campoSenha.setFont(UIUtils.FONTE_CORPO);
        campoSenha.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        
        JToggleButton btnOlho = new JToggleButton("👁️");
        btnOlho.setFocusable(false);
        btnOlho.setBackground(Color.WHITE);
        btnOlho.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        btnOlho.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btnOlho.addActionListener(e -> {
            if (btnOlho.isSelected()) {
                campoSenha.setEchoChar((char) 0); // Revela
                btnOlho.setText("🙈");
            } else {
                campoSenha.setEchoChar('•'); // Esconde
                btnOlho.setText("👁️");
            }
        });

        painel.add(campoSenha, BorderLayout.CENTER);
        painel.add(btnOlho, BorderLayout.EAST);
        painel.setPreferredSize(new Dimension(0, 36));
        
        return painel;
    }

    private void preencherDadosParaEdicao() {
        txtNome.setText(funcionarioEmEdicao.getNome());
        cbCargo.setSelectedItem(funcionarioEmEdicao.getCargo());
        txtUsername.setText(funcionarioEmEdicao.getUsername());
        txtSenha.setText(""); 
        txtConfirmarSenha.setText("");
    }

    private void salvar() {
        String nome = txtNome.getText().trim();
        String cargo = (String) cbCargo.getSelectedItem();
        String username = txtUsername.getText().trim();
        String senha = new String(txtSenha.getPassword()).trim();
        String confirmarSenha = new String(txtConfirmarSenha.getPassword()).trim();

        if (nome.isEmpty() || username.isEmpty()) {
            UIUtils.mostrarErro(this, "Nome e Username são obrigatórios!");
            return;
        }

        // Validação da Senha Nova
        if (funcionarioEmEdicao == null && senha.isEmpty()) {
            UIUtils.mostrarErro(this, "Digite uma senha para o novo usuário.");
            return;
        }

        // Validação de Confirmação (SÓ FAZ SE ELE DIGITOU ALGUMA SENHA)
        if (!senha.isEmpty() && !senha.equals(confirmarSenha)) {
            UIUtils.mostrarErro(this, "As senhas não batem! Verifique os campos e tente novamente.");
            return;
        }

        try (Connection conn = ConnectionFactory.getConnection()) {
            String sqlCheck = "SELECT id FROM Funcionario WHERE username = ?";
            try (PreparedStatement psCheck = conn.prepareStatement(sqlCheck)) {
                psCheck.setString(1, username);
                try (ResultSet rs = psCheck.executeQuery()) {
                    if (rs.next()) {
                        int idExistente = rs.getInt("id");
                        if (funcionarioEmEdicao == null || idExistente != funcionarioEmEdicao.getId()) {
                            UIUtils.mostrarErro(this, "Este Nome de Usuário já está em uso! Escolha outro.");
                            return;
                        }
                    }
                }
            }

            if (funcionarioEmEdicao == null) {
                String sqlInsert = "INSERT INTO Funcionario (nome, cargo, username, senha) VALUES (?, ?, ?, ?)";
                try (PreparedStatement ps = conn.prepareStatement(sqlInsert)) {
                    ps.setString(1, nome); ps.setString(2, cargo);
                    ps.setString(3, username); ps.setString(4, senha);
                    ps.executeUpdate();
                    UIUtils.mostrarSucesso(this, "Funcionário cadastrado com sucesso!");
                }
            } else {
                if (senha.isEmpty()) {
                    String sqlUpdate = "UPDATE Funcionario SET nome=?, cargo=?, username=? WHERE id=?";
                    try (PreparedStatement ps = conn.prepareStatement(sqlUpdate)) {
                        ps.setString(1, nome); ps.setString(2, cargo);
                        ps.setString(3, username); ps.setInt(4, funcionarioEmEdicao.getId());
                        ps.executeUpdate();
                    }
                } else {
                    String sqlUpdate = "UPDATE Funcionario SET nome=?, cargo=?, username=?, senha=? WHERE id=?";
                    try (PreparedStatement ps = conn.prepareStatement(sqlUpdate)) {
                        ps.setString(1, nome); ps.setString(2, cargo);
                        ps.setString(3, username); ps.setString(4, senha);
                        ps.setInt(5, funcionarioEmEdicao.getId());
                        ps.executeUpdate();
                    }
                }
                UIUtils.mostrarSucesso(this, "Dados atualizados com sucesso!");
            }
            
            voltar();

        } catch (SQLException ex) {
            UIUtils.mostrarErro(this, "Erro no banco de dados:\n" + ex.getMessage());
        }
    }

    private void voltar() {
        new TelaGerenciarFuncionarios(this.usuarioLogado).setVisible(true);
        this.dispose();
    }
}