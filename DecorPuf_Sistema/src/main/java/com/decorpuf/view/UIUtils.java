package com.decorpuf.view;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

/**
 * Utilitários de UI compartilhados entre as telas do sistema Decor Puf.
 */
public class UIUtils {

    // =========================================================
    // PALETA DE CORES - Tema festivo e elegante
    // =========================================================
    public static final Color COR_PRIMARIA       = new Color(106, 27, 154);   // Roxo profundo
    public static final Color COR_PRIMARIA_ESCURA = new Color(74, 0, 114);    // Roxo escuro
    public static final Color COR_ACENTO         = new Color(255, 179, 0);    // Amarelo dourado
    public static final Color COR_ACENTO_CLARO   = new Color(255, 213, 79);   // Amarelo claro
    public static final Color COR_FUNDO          = new Color(250, 245, 255);  // Lavanda muito claro
    public static final Color COR_CARD           = Color.WHITE;
    public static final Color COR_TEXTO          = new Color(33, 33, 33);
    public static final Color COR_TEXTO_SUAVE    = new Color(100, 100, 120);
    public static final Color COR_SUCESSO        = new Color(46, 125, 50);
    public static final Color COR_ERRO           = new Color(183, 28, 28);
    public static final Color COR_ALERTA         = new Color(230, 81, 0);
    public static final Color COR_BORDA          = new Color(206, 147, 216);

    // =========================================================
    // FONTES
    // =========================================================
    public static final Font FONTE_TITULO    = new Font("Segoe UI", Font.BOLD, 24);
    public static final Font FONTE_SUBTITULO = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font FONTE_CORPO     = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONTE_LABEL     = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONTE_BOTAO     = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONTE_TABELA    = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FONTE_HEADER_TAB = new Font("Segoe UI", Font.BOLD, 12);

    private UIUtils() {}

    /**
     * Configura o Look and Feel do sistema para System L&F.
     */
    public static void configurarLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            // Customizações globais
            UIManager.put("Panel.background", COR_FUNDO);
            UIManager.put("OptionPane.background", COR_FUNDO);
            UIManager.put("OptionPane.messageFont", FONTE_CORPO);
            UIManager.put("Button.font", FONTE_BOTAO);
            UIManager.put("Label.font", FONTE_CORPO);
            UIManager.put("TextField.font", FONTE_CORPO);
            UIManager.put("ComboBox.font", FONTE_CORPO);
            UIManager.put("Table.font", FONTE_TABELA);
            UIManager.put("TableHeader.font", FONTE_HEADER_TAB);
        } catch (Exception e) {
            System.err.println("Não foi possível definir o L&F: " + e.getMessage());
        }
    }

    /**
     * Centraliza e maximiza um JFrame.
     */
    public static void configurarJanela(JFrame frame, String titulo) {
        frame.setTitle(titulo);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setMinimumSize(new Dimension(900, 600));
        frame.setLocationRelativeTo(null);
    }

    /**
     * Cria um JButton estilizado na cor primária.
     */
    public static JButton criarBotaoPrimario(String texto) {
        JButton btn = new JButton(texto);
        btn.setFont(FONTE_BOTAO);
        btn.setBackground(COR_PRIMARIA);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(160, 38));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(COR_PRIMARIA_ESCURA);
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(COR_PRIMARIA);
            }
        });
        return btn;
    }

    /**
     * Cria um JButton estilizado na cor de acento (dourado).
     */
    public static JButton criarBotaoAcento(String texto) {
        JButton btn = new JButton(texto);
        btn.setFont(FONTE_BOTAO);
        btn.setBackground(COR_ACENTO);
        btn.setForeground(COR_TEXTO);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(160, 38));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(COR_ACENTO_CLARO);
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(COR_ACENTO);
            }
        });
        return btn;
    }

    /**
     * Cria um JButton estilizado para ações de perigo (deletar).
     */
    public static JButton criarBotaoPerigo(String texto) {
        JButton btn = new JButton(texto);
        btn.setFont(FONTE_BOTAO);
        btn.setBackground(COR_ERRO);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(160, 38));
        return btn;
    }

    /**
     * Cria um JTextField estilizado.
     */
    public static JTextField criarTextField(int colunas) {
        JTextField tf = new JTextField(colunas);
        tf.setFont(FONTE_CORPO);
        tf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COR_BORDA, 1, true),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        tf.setPreferredSize(new Dimension(0, 36));
        return tf;
    }

    /**
     * Cria um JPasswordField estilizado.
     */
    public static JPasswordField criarPasswordField(int colunas) {
        JPasswordField pf = new JPasswordField(colunas);
        pf.setFont(FONTE_CORPO);
        pf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COR_BORDA, 1, true),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        pf.setPreferredSize(new Dimension(0, 36));
        return pf;
    }

    /**
     * Cria um JLabel de rótulo de campo.
     */
    public static JLabel criarLabel(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(FONTE_LABEL);
        lbl.setForeground(COR_TEXTO);
        return lbl;
    }

    /**
     * Cria um JComboBox estilizado.
     */
    public static <T> JComboBox<T> criarComboBox(T[] itens) {
        JComboBox<T> cb = new JComboBox<>(itens);
        cb.setFont(FONTE_CORPO);
        cb.setPreferredSize(new Dimension(0, 36));
        return cb;
    }

    /**
     * Cria um painel de cabeçalho com gradiente roxo.
     */
    public static JPanel criarPainelHeader(String titulo, String subtitulo) {
        JPanel header = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gp = new GradientPaint(0, 0, COR_PRIMARIA_ESCURA, getWidth(), 0, COR_PRIMARIA);
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        header.setOpaque(false);
        header.setPreferredSize(new Dimension(0, 90));
        header.setBorder(BorderFactory.createEmptyBorder(15, 30, 15, 30));

        JPanel textos = new JPanel(new GridLayout(2, 1));
        textos.setOpaque(false);

        JLabel lblTitulo = new JLabel("🎉 " + titulo);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo.setForeground(Color.WHITE);

        JLabel lblSub = new JLabel(subtitulo);
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSub.setForeground(COR_ACENTO_CLARO);

        textos.add(lblTitulo);
        textos.add(lblSub);
        header.add(textos, BorderLayout.WEST);

        // Logo / nome do sistema
        JLabel lblLogo = new JLabel("Decor Puf");
        lblLogo.setFont(new Font("Segoe UI", Font.BOLD | Font.ITALIC, 18));
        lblLogo.setForeground(COR_ACENTO);
        header.add(lblLogo, BorderLayout.EAST);

        return header;
    }

    /**
     * Cria um painel card com borda arredondada simulada.
     */
    public static JPanel criarCard() {
        JPanel card = new JPanel();
        card.setBackground(COR_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COR_BORDA, 1),
            BorderFactory.createEmptyBorder(20, 25, 20, 25)
        ));
        return card;
    }

    /**
     * Configura uma JTable com estilo padrão do sistema.
     */
    public static void estilizarTabela(JTable tabela) {
        tabela.setFont(FONTE_TABELA);
        tabela.setRowHeight(32);
        tabela.setShowVerticalLines(false);
        tabela.setGridColor(new Color(230, 220, 240));
        tabela.setSelectionBackground(new Color(206, 147, 216));
        tabela.setSelectionForeground(COR_TEXTO);
        tabela.getTableHeader().setFont(FONTE_HEADER_TAB);
        tabela.getTableHeader().setBackground(COR_PRIMARIA);
        tabela.getTableHeader().setForeground(Color.WHITE);
        tabela.getTableHeader().setReorderingAllowed(false);
        tabela.setFillsViewportHeight(true);
        tabela.setIntercellSpacing(new Dimension(10, 0));
    }

    /**
     * Exibe um diálogo de erro padronizado.
     */
    public static void mostrarErro(Component parent, String mensagem) {
        JOptionPane.showMessageDialog(parent, mensagem, "Erro", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Exibe um diálogo de sucesso padronizado.
     */
    public static void mostrarSucesso(Component parent, String mensagem) {
        JOptionPane.showMessageDialog(parent, mensagem, "Sucesso", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Exibe um diálogo de confirmação.
     */
    public static boolean confirmar(Component parent, String mensagem) {
        int res = JOptionPane.showConfirmDialog(parent, mensagem, "Confirmação",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        return res == JOptionPane.YES_OPTION;
    }

    /**
     * Formata double como moeda brasileira.
     */
    public static String formatarMoeda(double valor) {
        return String.format("R$ %.2f", valor);
    }
}