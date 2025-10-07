package br.com.pedrodamasceno.view;

import br.com.pedrodamasceno.controller.ControladorJogo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class TelaGameOver extends JDialog {
    // Comentário: Aumentei o tamanho da tela para ter mais espaço para o visual
    public TelaGameOver(JFrame parent, boolean vitoria, int diasSobrevividos) {
        this(parent, vitoria, diasSobrevividos, null);
    }

    // NOVO: Construtor com mensagem personalizada para o confronto final
    public TelaGameOver(JFrame parent, boolean vitoria, int diasSobrevividos, String mensagemPersonalizada) {
        super(parent, "Fim de Jogo", true);
        setSize(600, 400);
        setLayout(new BorderLayout());
        setLocationRelativeTo(parent);
        getContentPane().setBackground(new Color(10, 10, 10)); // Cor de fundo mais escura

        // Comentário: Adicionei um painel para o título com animação
        JPanel painelTitulo = new JPanel(new FlowLayout());
        painelTitulo.setBackground(new Color(10, 10, 10));

        // Alteração: Adicionei uma borda superior para empurrar o título para baixo
        painelTitulo.setBorder(BorderFactory.createEmptyBorder(60, 0, 0, 0));

        JLabel lblTitulo = new JLabel();
        String tituloTexto = vitoria ? "VITÓRIA!" : "GAME OVER";
        lblTitulo.setText(tituloTexto);
        lblTitulo.setFont(new Font("Impact", Font.BOLD, 60));
        lblTitulo.setForeground(vitoria ? new Color(0, 150, 0) : new Color(200, 0, 0)); // Cores de vitória/derrota

        // Comentário: Lógica da animação de pulsação do título
        Timer timer = new Timer(800, new ActionListener() {
            private boolean bright = true;
            @Override
            public void actionPerformed(ActionEvent e) {
                if (bright) {
                    lblTitulo.setForeground(vitoria ? new Color(0, 100, 0) : new Color(150, 0, 0));
                } else {
                    lblTitulo.setForeground(vitoria ? new Color(50, 255, 50) : new Color(255, 40, 40));
                }
                bright = !bright;
            }
        });
        timer.start();

        painelTitulo.add(lblTitulo);

        // Comentário: Mensagem central com texto dinâmico
        JLabel lblMensagem;
        if (mensagemPersonalizada != null) {
            String cor = vitoria ? "green" : "red";
            lblMensagem = new JLabel("<html><center><font color='white' size='4'>" + mensagemPersonalizada.replace("\n", "<br>") + "</font></center></html>", SwingConstants.CENTER);
        } else {
            String mensagem = vitoria ? "Parabéns! Você sobreviveu por " : "Você sobreviveu por ";
            lblMensagem = new JLabel("<html><center><font color='white' size='4'>" + mensagem + diasSobrevividos + " dias.</font></center></html>", SwingConstants.CENTER);
        }

        // Comentário: Centralizei o texto na tela
        lblMensagem.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        lblMensagem.setBackground(new Color(10, 10, 10));

        // Comentário: Utilizei o mesmo estilo de botão da tela inicial
        JButton btnReiniciar = new JButton("Reiniciar Jogo");
        JButton btnSair = new JButton("Sair");

        estiloBotaoApocaliptico(btnReiniciar);
        estiloBotaoApocaliptico(btnSair);

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 10));
        painelBotoes.setBackground(new Color(10, 10, 10));
        painelBotoes.add(btnReiniciar);
        painelBotoes.add(btnSair);

        add(painelTitulo, BorderLayout.NORTH);
        add(lblMensagem, BorderLayout.CENTER);
        add(painelBotoes, BorderLayout.SOUTH);

        btnReiniciar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                timer.stop(); // Interromper a animação
                dispose();
                if (parent != null) {
                    parent.dispose();
                }
                new ControladorJogo().iniciarJogo();
            }
        });

        btnSair.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        pack();
        setMinimumSize(new Dimension(600, 400));
        setLocationRelativeTo(null);
    }


    private void estiloBotaoApocaliptico(JButton botao) {
        botao.setFont(new Font("Arial", Font.BOLD, 20));
        Color vermelhoSangue = new Color(120, 0, 0);

        botao.setBackground(vermelhoSangue);
        botao.setForeground(new Color(200, 0, 0));
        botao.setFocusPainted(false);
        botao.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 0, 0), 2),
                BorderFactory.createLineBorder(new Color(80, 0, 0), 3)
        ));
        botao.setOpaque(true);
        botao.setContentAreaFilled(true);
        botao.setPreferredSize(new Dimension(220, 70));

        botao.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                botao.setBackground(new Color(180, 0, 0));
                botao.setForeground(Color.BLACK);
                botao.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(255, 50, 50), 2),
                        BorderFactory.createLineBorder(new Color(100, 0, 0), 3)
                ));
            }
            public void mouseExited(MouseEvent evt) {
                botao.setBackground(vermelhoSangue);
                botao.setForeground(new Color(255, 40, 40));
                botao.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(200, 0, 0), 2),
                        BorderFactory.createLineBorder(new Color(80, 0, 0), 3)
                ));
            }
        });
    }
}