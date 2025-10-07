package br.com.pedrodamasceno.view;

import br.com.pedrodamasceno.controller.ControladorJogo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TelaInicial extends JFrame {
    public TelaInicial() {
        setTitle("🧟 Undead Rising - Início 🧟");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Definir ícone
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/icon.ico"));
            setIconImage(icon.getImage());
        } catch (Exception e) {
            System.out.println("Ícone não encontrado, continuando sem ícone.");
        }

        // Layout principal com fundo preto
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.BLACK);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setContentPane(mainPanel);

        // Painel de título com animação
        JPanel painelTitulo = new JPanel();
        painelTitulo.setBackground(Color.BLACK);
        painelTitulo.setBorder(BorderFactory.createEmptyBorder(20, 0, 30, 0));

        JLabel titulo = new JLabel(" UNDEAD  RISING ", SwingConstants.CENTER);
        titulo.setFont(new Font("Impact", Font.BOLD, 56));
        titulo.setForeground(Color.RED);

        // Adicionar efeito de pulsação ao título
        Timer timer = new Timer(800, new ActionListener() {
            private boolean bright = true;
            @Override
            public void actionPerformed(ActionEvent e) {
                if (bright) {
                    titulo.setForeground(new Color(150, 0, 0)); // Vermelho mais escuro
                } else {
                    titulo.setForeground(new Color(255, 40, 40)); // Vermelho brilhante
                }
                bright = !bright;
            }
        });
        timer.start();

        painelTitulo.add(titulo);

        // Painel de botões - CENTRALIZADO
        JPanel painelBotoes = new JPanel();
        painelBotoes.setLayout(new GridBagLayout()); // Usando GridBagLayout para centralização perfeita
        painelBotoes.setBorder(BorderFactory.createEmptyBorder(30, 80, 30, 80));
        painelBotoes.setBackground(Color.BLACK);
        painelBotoes.setOpaque(true);

        JButton btnNovoJogo = new JButton("Novo Jogo");
        JButton btnSair = new JButton("Sair");

        // Estilizar botões com altura maior e cor vermelha
        estiloBotaoApocaliptico(btnNovoJogo);
        estiloBotaoApocaliptico(btnSair);

        // Configuração do GridBagConstraints para centralização
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 0, 10, 0); // Espaçamento entre botões

        // Adicionar botões centralizados
        painelBotoes.add(btnNovoJogo, gbc);
        painelBotoes.add(btnSair, gbc);

        // Adicionar imagem/ícone temático
        JLabel emojiLabel = new JLabel("🧟‍♂️🧟‍♀️🧟", SwingConstants.CENTER);
        emojiLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
        emojiLabel.setForeground(Color.RED);
        emojiLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        add(emojiLabel, BorderLayout.SOUTH);

        // Adicionar componentes ao frame
        add(painelTitulo, BorderLayout.NORTH);
        add(painelBotoes, BorderLayout.CENTER);

        // Ações dos botões
        btnNovoJogo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                timer.stop(); // Parar a animação ao sair da tela
                dispose();
                ControladorJogo controlador = new ControladorJogo();
                controlador.iniciarJogo();
            }
        });

        btnSair.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        // Ajustar tamanho automaticamente
        pack();
        setMinimumSize(new Dimension(600, 500));
        setLocationRelativeTo(null);

    }

    private void estiloBotaoApocaliptico(JButton botao) {
        botao.setFont(new Font("Arial", Font.BOLD, 22));
        Color cinzaEscuro = new Color(64, 64, 64); // cinza escuro
        botao.setBackground(cinzaEscuro);
        botao.setForeground(Color.RED);
        botao.setFocusPainted(false);
        botao.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
        botao.setOpaque(true);
        botao.setContentAreaFilled(true);

        // Aumentar a altura e diminuir a largura
        botao.setPreferredSize(new Dimension(220, 70)); // Largura menor, altura maior
        botao.setMinimumSize(new Dimension(220, 70));
        botao.setMaximumSize(new Dimension(250, 80));

        // Centralizar texto
        botao.setHorizontalAlignment(SwingConstants.CENTER);
        botao.setVerticalAlignment(SwingConstants.CENTER);

        // Efeito hover para os botões - CORES EM VERMELHO
        botao.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                botao.setBackground(Color.RED); // Fundo vermelho
                botao.setForeground(Color.BLACK); // Texto preto
                botao.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(255, 50, 50), 2),
                        BorderFactory.createLineBorder(new Color(100, 0, 0), 3))); // Borda vermelha escura
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                botao.setBackground(Color.BLACK); // Fundo preto
                botao.setForeground(Color.RED); // Texto vermelho
                botao.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(200, 0, 0), 2),
                        BorderFactory.createLineBorder(new Color(80, 0, 0), 3)
                )); // Borda vermelha normal
            }
        });

        // Efeito quando pressionado
        botao.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                botao.setBackground(new Color(150, 0, 0)); // Vermelho mais escuro
                botao.setForeground(Color.WHITE);
            }
        });
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                TelaInicial tela = new TelaInicial();
                tela.setVisible(true);
            }
        });
    }
}