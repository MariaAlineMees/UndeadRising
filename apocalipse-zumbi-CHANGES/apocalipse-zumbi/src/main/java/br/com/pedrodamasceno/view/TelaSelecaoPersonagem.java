package br.com.pedrodamasceno.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Toolkit;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

import br.com.pedrodamasceno.model.personagens.Engenheiro;
import br.com.pedrodamasceno.model.personagens.Estrategista;
import br.com.pedrodamasceno.model.personagens.Lutador;
import br.com.pedrodamasceno.model.personagens.Medico;
import br.com.pedrodamasceno.model.personagens.Personagem;
import br.com.pedrodamasceno.model.personagens.Sobrevivente;
import br.com.pedrodamasceno.model.personagens.Soldado;

public class TelaSelecaoPersonagem extends JDialog {
    private Personagem personagemSelecionado;

    public TelaSelecaoPersonagem(JFrame parent) {
        super(parent, "Escolha seu Personagem 👊💀", true);

        // Ajusta o tamanho da janela com base na resolução do monitor para melhor responsividade
        Dimension tela = Toolkit.getDefaultToolkit().getScreenSize();
        int largura = (int) (tela.width * 0.7);
        int altura = (int) (tela.height * 0.7);
        setSize(largura, altura);
        setLayout(new BorderLayout());
        setLocationRelativeTo(parent);

        // Fundo preto
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.BLACK);
        setContentPane(mainPanel);

        JPanel painelCards = new JPanel();
        painelCards.setLayout(new BoxLayout(painelCards, BoxLayout.Y_AXIS));
        painelCards.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        painelCards.setBackground(Color.BLACK);

        JLabel titulo = new JLabel("Escolha seu Personagem ", SwingConstants.CENTER);
        titulo.setFont(new Font("Impact", Font.BOLD, 38));
        titulo.setForeground(Color.RED);
        titulo.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        titulo.setBackground(Color.BLACK);
        titulo.setOpaque(true);

        JButton btnConfirmar = new JButton("CONFIRMAR SELEÇÃO");
        estiloBotaoApocaliptico(btnConfirmar);

        String[] nomes = {"Soldado", "Médico", "Sobrevivente", "Lutador", "Estrategista", "Engenheiro"};
        String[] descricoes = {
                "Força: 8, Destreza: 6, Inteligência: 4, Constituição: 7\nHabilidades: Postura de Combate, Segunda Chance",
                "Força: 4, Destreza: 5, Inteligência: 9, Constituição: 5\nHabilidades: Cura de Emergência, Diagnóstico Rápido",
                "Força: 6, Destreza: 7, Inteligência: 5, Constituição: 6\nHabilidades: Furtividade, Sorte do Iniciante",
                "Força: 9, Destreza: 5, Inteligência: 4, Constituição: 8\nHabilidades: Fúria, Resistência",
                "Força: 4, Destreza: 6, Inteligência: 8, Constituição: 4\nHabilidades: Plano de Fuga, Análise de Fraqueza",
                "Força: 5, Destreza: 6, Inteligência: 8, Constituição: 5\nHabilidades: Armadilha Explosiva, Reparos Rápidos"
        };
        String[] emojis = {"🎖️", "💉", "🏃", "🥊", "💡", "🔧"};

        ButtonGroup grupoBotoes = new ButtonGroup();

        for (int i = 0; i < nomes.length; i++) {
            JRadioButton radioCard = new JRadioButton();
            radioCard.setOpaque(false);
            radioCard.setForeground(Color.WHITE);
            grupoBotoes.add(radioCard);

            final int index = i;

            CardPersonagem card = new CardPersonagem(
                    nomes[i],
                    emojis[i],
                    descricoes[i],
                    radioCard
            );

            radioCard.addActionListener(e -> {
                switch (nomes[index]) {
                    case "Soldado":
                        personagemSelecionado = new Soldado("Soldado");
                        break;
                    case "Médico":
                        personagemSelecionado = new Medico("Médico");
                        break;
                    case "Sobrevivente":
                        personagemSelecionado = new Sobrevivente("Sobrevivente");
                        break;
                    case "Lutador":
                        personagemSelecionado = new Lutador("Lutador");
                        break;
                    case "Estrategista":
                        personagemSelecionado = new Estrategista("Estrategista");
                        break;
                    case "Engenheiro":
                        personagemSelecionado = new Engenheiro("Engenheiro");
                        break;
                }
                for (Component comp : painelCards.getComponents()) {
                    if (comp instanceof CardPersonagem) {
                        ((CardPersonagem) comp).atualizarBorda();
                    }
                }
            });

            painelCards.add(card);
            painelCards.add(Box.createRigidArea(new Dimension(0, 15)));
        }

        btnConfirmar.addActionListener(e -> {
            if (personagemSelecionado != null) {
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Por favor, selecione um personagem para continuar!", "Aviso", JOptionPane.WARNING_MESSAGE);
            }
        });

        add(titulo, BorderLayout.NORTH);
        JScrollPane scrollPane = new JScrollPane(painelCards);
        scrollPane.getVerticalScrollBar().setBackground(new Color(20, 20, 20));
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        add(scrollPane, BorderLayout.CENTER);

        JPanel painelInferior = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        painelInferior.setBackground(Color.BLACK);
        painelInferior.add(btnConfirmar);

        add(painelInferior, BorderLayout.SOUTH);

        pack();
    }


    private void estiloBotaoApocaliptico(JButton botao) {
        botao.setFont(new Font("Arial", Font.BOLD, 20));
        Color vermelhoSangue = new Color(120, 0, 0);

        botao.setBackground(vermelhoSangue);
        botao.setForeground(new Color(150, 0, 0));
        botao.setFocusPainted(false);
        botao.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 0, 0), 2),
                BorderFactory.createLineBorder(new Color(80, 0, 0), 3)
        ));
        botao.setOpaque(true);
        botao.setContentAreaFilled(true);
        // Ajustado para tamanho mais flexível que pode crescer em telas maiores
        botao.setPreferredSize(new Dimension(280, 70));

        botao.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                botao.setBackground(new Color(180, 0, 0));
                botao.setForeground(Color.BLACK);
                botao.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(255, 50, 50), 2),
                        BorderFactory.createLineBorder(new Color(100, 0, 0), 3)
                ));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                botao.setBackground(vermelhoSangue);
                botao.setForeground(new Color(255, 40, 40));
                botao.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(200, 0, 0), 2),
                        BorderFactory.createLineBorder(new Color(80, 0, 0), 3)
                ));
            }
        });
    }

    public Personagem getPersonagemSelecionado() {
        return personagemSelecionado;
    }

    class CardPersonagem extends JPanel {
        private JRadioButton radioButton;
        private Color corBordaNaoSelecionada = new Color(200, 0, 0);
        private Color corBordaSelecionada = new Color(255, 200, 0);

        public CardPersonagem(String nome, String emoji, String descricao, JRadioButton radioBtn) {
            this.radioButton = radioBtn;
            setLayout(new BorderLayout(10, 10));
            setBorder(BorderFactory.createLineBorder(corBordaNaoSelecionada, 3));
            setBackground(new Color(15, 15, 15));
            // Removi setPreferredSize e max size fixos para largura para permitir adaptação dinâmica
            // setPreferredSize(new Dimension(750, 140));
            // setMaximumSize(new Dimension(750, 140));
            setMinimumSize(new Dimension(600, 140)); // Define mínimo razoável para altura e largura

            JLabel lblTitulo = new JLabel("<html><font size='6'>" + emoji + " " + nome + "</font></html>");
            lblTitulo.setFont(new Font("Impact", Font.BOLD, 12));
            lblTitulo.setForeground(new Color(255, 255, 255));
            lblTitulo.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));

            String descHTML = descricao
                    .replace("Força:", "💪 Força:")
                    .replace("Destreza:", "🏃 Destreza:")
                    .replace("Inteligência:", "💡 Inteligência:")
                    .replace("Constituição:", "❤️ Constituição:")
                    .replace("Habilidades:", "✨ Habilidades:");

            JLabel lblDescricao = new JLabel("<html><div style='width:550px; color:#AAAAAA;'>" + descHTML.replace("\n", "<br>") + "</div></html>");
            lblDescricao.setFont(new Font("Arial", Font.PLAIN, 14));
            lblDescricao.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
            lblDescricao.setForeground(new Color(200, 200, 200));
            lblDescricao.setOpaque(false);

            JPanel conteudoPanel = new JPanel(new BorderLayout());
            conteudoPanel.setOpaque(false);
            conteudoPanel.add(lblTitulo, BorderLayout.NORTH);
            conteudoPanel.add(lblDescricao, BorderLayout.CENTER);

            JPanel radioPanel = new JPanel();
            radioPanel.setOpaque(false);
            // Adicionado margem para evitar corte do radioButton no final da tela
            radioPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 10));
            radioPanel.add(radioButton);

            add(conteudoPanel, BorderLayout.CENTER);
            add(radioPanel, BorderLayout.EAST);

            addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    setBackground(new Color(30, 0, 0));
                }

                @Override
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    setBackground(new Color(15, 15, 15));
                }

                @Override
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    radioButton.setSelected(true);
                    radioButton.doClick();
                }
            });
        }

        public void atualizarBorda() {
            if (radioButton.isSelected()) {
                setBorder(BorderFactory.createLineBorder(corBordaSelecionada, 3));
            } else {
                setBorder(BorderFactory.createLineBorder(corBordaNaoSelecionada, 3));
            }
        }
    }
}