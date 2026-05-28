package com.decorpuf.view;

import com.decorpuf.dao.ConnectionFactory;
import com.decorpuf.model.Funcionario;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class TelaLogin extends JFrame {

    private JTextField txtUsername;
    private JPasswordField txtSenha;

    public TelaLogin() {
        UIUtils.configurarJanela(this, "Decor Puf - Login");
        setLayout(new BorderLayout());
        setBackground(UIUtils.COR_FUNDO);

        // Cabeçalho de Boas-Vindas
        add(UIUtils.criarPainelHeader("Decor Puf", "Faça login para acessar o sistema de gestão"), BorderLayout.NORTH);

        JPanel painelCentral = new JPanel(new GridBagLayout());
        painelCentral.setBackground(UIUtils.COR_FUNDO);

        JPanel card = UIUtils.criarCard();
        card.setLayout(new GridBagLayout());
        card.setPreferredSize(new Dimension(400, 300));
        card.setMinimumSize(new Dimension(400, 300));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 20, 5, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0; 
        gbc.weightx = 1.0;

        // Campo Usuário
        gbc.gridy = 0; 
        card.add(UIUtils.criarLabel("Usuário:"), gbc);
        
        gbc.gridy = 1; 
        txtUsername = UIUtils.criarTextField(20); 
        card.add(txtUsername, gbc);

        // Campo Senha (Com o Olhinho)
        gbc.gridy = 2; 
        card.add(UIUtils.criarLabel("Senha:"), gbc);
        
        gbc.gridy = 3; 
        txtSenha = new JPasswordField();
        card.add(criarPainelSenhaComOlho(txtSenha), gbc);

        // Botão de Entrar
        gbc.gridy = 4; 
        gbc.insets = new Insets(25, 20, 10, 20);
        JButton btnLogin = UIUtils.criarBotaoAcento("Entrar no Sistema");
        btnLogin.setPreferredSize(new Dimension(0, 45));
        btnLogin.addActionListener(e -> fazerLogin());
        
        // Permite logar apertando o "Enter"
        getRootPane().setDefaultButton(btnLogin);
        
        card.add(btnLogin, gbc);

        painelCentral.add(card);
        add(painelCentral, BorderLayout.CENTER);
    }

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
                campoSenha.setEchoChar((char) 0); 
                btnOlho.setText("🙈");
            } else {
                campoSenha.setEchoChar('•'); 
                btnOlho.setText("👁️");
            }
        });

        painel.add(campoSenha, BorderLayout.CENTER);
        painel.add(btnOlho, BorderLayout.EAST);
        painel.setPreferredSize(new Dimension(0, 36));
        
        return painel;
    }

    private void fazerLogin() {
        String user = txtUsername.getText().trim();
        String pass = new String(txtSenha.getPassword()).trim();

        if (user.isEmpty() || pass.isEmpty()) {
            UIUtils.mostrarErro(this, "Preencha o usuário e a senha!");
            return;
        }

        String sql = "SELECT * FROM Funcionario WHERE username = ? AND senha = ?";
        
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
             
            ps.setString(1, user);
            ps.setString(2, pass);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Funcionario f = new Funcionario();
                    f.setId(rs.getInt("id"));
                    f.setNome(rs.getString("nome"));
                    f.setCargo(rs.getString("cargo"));
                    f.setUsername(rs.getString("username"));
                    
                    new TelaMenuPrincipal(f).setVisible(true);
                    this.dispose();
                } else {
                    UIUtils.mostrarErro(this, "Usuário ou senha inválidos.");
                }
            }
        } catch (Exception ex) {
            UIUtils.mostrarErro(this, "Erro ao conectar com o banco:\n" + ex.getMessage());
        }
    }
}