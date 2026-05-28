package com.mycompany.decorpuf_sistema;

import com.decorpuf.dao.ConnectionFactory;
import com.decorpuf.view.TelaLogin;
import javax.swing.SwingUtilities;

public class DecorPuf_Sistema {

    public static void main(String[] args) {
        
        // 1. INICIALIZAÇÃO DO BANCO DE DADOS
        // Isso garante que o arquivo .db e as tabelas sejam criados antes de tudo!
        try {
            ConnectionFactory.initializeDatabase();
        } catch (Exception e) {
            System.err.println("Erro crítico ao criar as tabelas: " + e.getMessage());
            return; // Se der erro no banco, o sistema nem abre
        }

        // 2. INICIALIZAÇÃO DA INTERFACE GRÁFICA (Telas)
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // Instancia e torna visível o primeiro ecrã do sistema
                TelaLogin login = new TelaLogin();
                login.setVisible(true);
            }
        });
    }
}