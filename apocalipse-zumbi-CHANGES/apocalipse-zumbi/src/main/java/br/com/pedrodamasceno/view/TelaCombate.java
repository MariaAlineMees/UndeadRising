package br.com.pedrodamasceno.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;

import br.com.pedrodamasceno.controller.ControladorJogo;
import br.com.pedrodamasceno.model.ModeloJogo;
import br.com.pedrodamasceno.model.combate.SistemaCombate;
import br.com.pedrodamasceno.model.itens.Arma;
import br.com.pedrodamasceno.model.itens.ArmaDeFogo;
import br.com.pedrodamasceno.model.itens.Item;
import br.com.pedrodamasceno.model.itens.TipoItem;
import br.com.pedrodamasceno.model.zumbis.Zumbi;

public class TelaCombate extends JDialog {
    private final ModeloJogo modelo;
    private final SistemaCombate sistema;
    private final JTextArea areaLog;
    private final JComboBox<String> comboAlvos;
    private final JButton btnBasico, btnHab1, btnHab2, btnUsarItem, btnUsarArma, btnFugir;
    private final JLabel lblJogador, lblAlvo, lblCargas;
    private JLabel lblArmaStatus;
    private boolean combateEmAndamento = false;
    private JProgressBar barraVidaJogador;
    private int tentativasFuga = 0; // NOVO: Contador de tentativas de fuga
    private static final int MAX_TENTATIVAS_FUGA = 2;

    public TelaCombate(JFrame parent, ModeloJogo modelo, List<Zumbi> inimigos) {
        super(parent, "COMBATE", true);
        this.modelo = modelo;
        this.sistema = new SistemaCombate(modelo.getJogador(), inimigos);
        modelo.setEmCombate(true);
        modelo.setInimigosAtuais(inimigos);

        setSize(950, 650);
        setLayout(new BorderLayout());
        setLocationRelativeTo(parent);
        getContentPane().setBackground(Color.BLACK);

        JPanel topo = new JPanel(new GridLayout(3,1));
        topo.setBackground(Color.BLACK);
        topo.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        Font statusFont = new Font("Arial", Font.BOLD, 20);
        lblJogador = new JLabel("Jogador: " + modelo.getJogador().getSaude() + "/" + modelo.getJogador().getSaudeMaxima());
        lblJogador.setFont(statusFont);
        lblJogador.setForeground(Color.RED);

        barraVidaJogador = new JProgressBar(0, 100);
        barraVidaJogador.setStringPainted(true);
        barraVidaJogador.setBackground(Color.DARK_GRAY);
        barraVidaJogador.setForeground(Color.CYAN);
        barraVidaJogador.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
        barraVidaJogador.setPreferredSize(new Dimension(250, 25));

        lblAlvo = new JLabel("Alvo: -");
        lblAlvo.setFont(statusFont);
        lblAlvo.setForeground(Color.RED);

        lblCargas = new JLabel("Cargas H1=" + modelo.getJogador().getCargasHabilidade1() + " H2=" + modelo.getJogador().getCargasHabilidade2());
        lblCargas.setFont(statusFont);
        lblCargas.setForeground(Color.RED);

        lblArmaStatus = new JLabel("Arma: Nenhuma");
        lblArmaStatus.setFont(statusFont);
        lblArmaStatus.setForeground(Color.RED);

        topo.add(lblJogador);
        topo.add(barraVidaJogador);
        topo.add(lblAlvo);
        topo.add(lblCargas);
        topo.add(lblArmaStatus);
        add(topo, BorderLayout.NORTH);

        areaLog = new JTextArea();
        areaLog.setEditable(false);
        areaLog.setFont(new Font("Arial", Font.PLAIN, 20));
        areaLog.setBackground(new Color(20, 20, 20));
        areaLog.setForeground(Color.WHITE);
        areaLog.setCaretColor(Color.WHITE);
        areaLog.setRows(15);
        JScrollPane scroll = new JScrollPane(areaLog);
        scroll.setPreferredSize(new Dimension(0, 350));

        comboAlvos = new JComboBox<>();
        comboAlvos.setFont(new Font("Arial", Font.BOLD, 18));
        comboAlvos.setBackground(new Color(20, 20, 20));
        comboAlvos.setForeground(Color.BLACK);
        comboAlvos.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
        atualizarListaAlvos();
        comboAlvos.addActionListener(e -> {
            int idx = comboAlvos.getSelectedIndex();
            if (idx >= 0) {
                // Lógica de seleção de alvo
            }
            atualizarStatus();
        });

        JPanel centro = new JPanel(new BorderLayout(15, 15));
        centro.setBackground(Color.BLACK);
        centro.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel lblAlvos = new JLabel("ALVOS:");
        lblAlvos.setFont(new Font("Arial", Font.BOLD, 20));
        lblAlvos.setForeground(Color.RED);

        JLabel lblLog = new JLabel("LOG DO COMBATE:");
        lblLog.setFont(new Font("Arial", Font.BOLD, 20));
        lblLog.setForeground(Color.RED);

        JPanel painelAlvos = new JPanel(new BorderLayout());
        painelAlvos.setBackground(Color.BLACK);
        painelAlvos.add(lblAlvos, BorderLayout.NORTH);
        painelAlvos.add(comboAlvos, BorderLayout.CENTER);

        JPanel painelLog = new JPanel(new BorderLayout());
        painelLog.setBackground(Color.BLACK);
        painelLog.add(lblLog, BorderLayout.NORTH);
        painelLog.add(scroll, BorderLayout.CENTER);

        centro.add(painelAlvos, BorderLayout.NORTH);
        centro.add(painelLog, BorderLayout.CENTER);
        add(centro, BorderLayout.CENTER);

        JPanel baixo = new JPanel(new BorderLayout());
        baixo.setBackground(Color.BLACK);
        baixo.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel botoes = new JPanel(new GridLayout(1, 6, 15, 15));
        botoes.setBackground(Color.BLACK);

        btnBasico = new JButton("ATAQUE");
        btnHab1 = new JButton("HABILIDADE 1");
        btnHab2 = new JButton("HABILIDADE 2");
        btnUsarItem = new JButton("USAR ITEM");
        btnUsarArma = new JButton("USAR ARMA");
        btnFugir = new JButton("FUGIR");

        estiloBotaoCombateGrande(btnBasico);
        estiloBotaoCombateGrande(btnHab1);
        estiloBotaoCombateGrande(btnHab2);
        estiloBotaoCombateGrande(btnUsarItem);
        estiloBotaoCombateGrande(btnUsarArma);
        estiloBotaoCombateGrande(btnFugir);

        botoes.add(btnBasico);
        botoes.add(btnHab1);
        botoes.add(btnHab2);
        botoes.add(btnUsarItem);
        botoes.add(btnUsarArma);
        botoes.add(btnFugir);

        btnBasico.addActionListener(e -> executarAcao(0));
        btnHab1.addActionListener(e -> executarAcao(1));
        btnHab2.addActionListener(e -> executarAcao(2));
        btnUsarItem.addActionListener(e -> mostrarSelecaoItemCombate());
        btnUsarArma.addActionListener(e -> usarArmaEspecial());
        btnFugir.addActionListener(e -> tentarFugir());

        baixo.add(botoes, BorderLayout.NORTH);

        add(baixo, BorderLayout.SOUTH);

        atualizarStatus();
        configurarBotoesHabilidades();

        mostrarInicioCombate();

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                if (modelo.isEmCombate()) {
                    modelo.setEmCombate(false);
                    modelo.marcarExploracaoConcluida();
                    modelo.concluirExploracao();
                    ((TelaPrincipal) getParent()).atualizarInterface();
                }
                dispose();
            }
        });
    }

    private void mostrarInicioCombate() {
        appendLog("=== 💀 INÍCIO DO COMBATE 💀 ===");
        List<Zumbi> inimigos = modelo.getInimigosAtuais();
        if (inimigos == null || inimigos.isEmpty()) {
            appendLog("Erro: Nenhum inimigo encontrado!");
            return;
        }

        int numInimigos = inimigos.size();
        if (numInimigos == 1) {
            appendLog("Você encontrou 1 inimigo! 🧟");
        } else {
            appendLog("Você encontrou " + numInimigos + " inimigos! 🧟 horda 🧟");
        }

        for (int i = 0; i < inimigos.size(); i++) {
            Zumbi zumbi = inimigos.get(i);
            appendLog("• " + zumbi.getNome() + " (" + zumbi.getSaude() + " HP)");
        }

        appendLog("Escolha sua ação!");
        appendLog("========================");
    }

    private void estiloBotaoCombateGrande(JButton botao) {
        botao.setFont(new Font("Arial", Font.BOLD, 18));
        botao.setBackground(new Color(30, 30, 30));
        botao.setForeground(Color.RED);
        botao.setFocusPainted(false);
        botao.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
        botao.setPreferredSize(new Dimension(180, 50));

        botao.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                botao.setBackground(new Color(60, 0, 0));
                botao.setForeground(Color.BLACK);
            }
            public void mouseExited(MouseEvent evt) {
                botao.setBackground(new Color(30, 30, 30));
                botao.setForeground(Color.RED);
            }
        });
    }

    private void configurarBotoesHabilidades() {
        btnHab1.setToolTipText(modelo.getJogador().getDescricaoHabilidade1());
        btnHab2.setToolTipText(modelo.getJogador().getDescricaoHabilidade2());

        btnHab1.setText("HAB 1 (" + modelo.getJogador().getCargasHabilidade1() + ")");
        btnHab2.setText("HAB 2 (" + modelo.getJogador().getCargasHabilidade2() + ")");

        btnHab1.setEnabled(modelo.getJogador().getCargasHabilidade1() > 0);
        btnHab2.setEnabled(modelo.getJogador().getCargasHabilidade2() > 0);

        if (modelo.isEventoFinalAtivo()) {
            btnFugir.setToolTipText("Não é possível fugir do Boss final no Dia 7.");
            btnFugir.setEnabled(false);
        } else {
            btnFugir.setToolTipText("Tentar fugir do combate. Chance baseada na sua destreza.");
            btnFugir.setEnabled(true);
        }
    }

    private void executarAcao(int tipoAtaque) {
        if (combateEmAndamento) return;
        combateEmAndamento = true;
        desabilitarBotoes();

        new Thread(() -> {
            try {
                ControladorJogo controller = new ControladorJogo((TelaPrincipal)getParent());
                controller.executarTurnoCombate(sistema, tipoAtaque, this);
                Thread.sleep(1000);
                SwingUtilities.invokeLater(() -> {
                    atualizarStatus();
                    configurarBotoesHabilidades();
                    habilitarBotoes();
                    combateEmAndamento = false;
                });
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    private void desabilitarBotoes() {
        btnBasico.setEnabled(false);
        btnHab1.setEnabled(false);
        btnHab2.setEnabled(false);
        btnUsarItem.setEnabled(false);
        btnUsarArma.setEnabled(false);
        btnFugir.setEnabled(false);
    }

    private void tentarFugir() {
        if (combateEmAndamento) return;
        if (modelo.isEventoFinalAtivo()) {
            appendLog(">>> NÃO É POSSÍVEL FUGIR! 🚫 <<<");
            appendLog("Você não pode fugir do Boss final no Dia 7!");
            JOptionPane.showMessageDialog(this,
                    "Não é possível fugir do Boss final no Dia 7.",
                    "Fuga Bloqueada",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // NOVO: Verificar limite de tentativas de fuga
        if (tentativasFuga >= MAX_TENTATIVAS_FUGA) {
            appendLog(">>> VOCÊ JÁ TENTOU FUGIR MUITAS VEZES! 🚫 <<<");
            appendLog("Não há mais opções de fuga. Enfrente o combate!");
            return;
        }

        tentativasFuga++;
        combateEmAndamento = true;
        desabilitarBotoes();

        new Thread(() -> {
            try {
                int destreza = modelo.getJogador().getDestreza();
                int chanceFuga = Math.min(80, 30 + (destreza * 5));

                appendLog(">>> TENTANDO FUGIR... 🏃💨 (Tentativa " + tentativasFuga + "/" + MAX_TENTATIVAS_FUGA + ") <<<");
                appendLog("Sua destreza: " + destreza + " (Chance de fuga: " + chanceFuga + "%)");

                Thread.sleep(1000);

                int resultado = new Random().nextInt(100);
                if (resultado < chanceFuga) {
                    appendLog(">>> FUGA BEM-SUCEDIDA! ✅ <<<");
                    appendLog("Você conseguiu escapar do combate através das sombras! 🥳");
                    Thread.sleep(1500);
                    modelo.getJogador().atualizarSentimentoJogador(1);
                    SwingUtilities.invokeLater(() -> {
                        modelo.setEmCombate(false);
                        modelo.marcarExploracaoConcluida();
                        dispose();
                    });
                } else {
                    // NOVO: Mensagens customizadas baseadas na tentativa
                    appendLog(">>> FUGA FALHOU! 😢 <<<");
                    if (tentativasFuga == 1) {
                        appendLog("Os zumbis bloquearam sua rota de fuga, cercando você lentamente...");
                        appendLog("Você ainda pode tentar uma última vez antes que seja tarde demais!");
                    } else if (tentativasFuga == 2) {
                        appendLog("Sua última chance de fuga se desfez! Os zumbis te encurralaram completamente.");
                        appendLog("Não há mais para onde correr. É hora de lutar pela sua vida! ⚔️");
                        SwingUtilities.invokeLater(() -> btnFugir.setEnabled(false)); // Desabilita botão de fuga permanentemente
                    }
                    Thread.sleep(1000);
                    modelo.getJogador().atualizarSentimentoJogador(-1);
                    SwingUtilities.invokeLater(() -> {
                        habilitarBotoes();
                        if (tentativasFuga >= MAX_TENTATIVAS_FUGA) {
                            btnFugir.setEnabled(false); // Garante que não possa mais fugir
                        }
                        combateEmAndamento = false;
                    });
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    private void habilitarBotoes() {
        btnBasico.setEnabled(true);
        btnHab1.setEnabled(modelo.getJogador().getCargasHabilidade1() > 0);
        btnHab2.setEnabled(modelo.getJogador().getCargasHabilidade2() > 0);
        btnUsarItem.setEnabled(modelo.getJogador().getInventario() != null && !modelo.getJogador().getInventario().getItens().isEmpty());
        btnUsarArma.setEnabled(modelo.getJogador().getArmaEquipada() != null);
        btnFugir.setEnabled(!modelo.isEventoFinalAtivo());
    }

    public void mostrarSelecaoItemCombate() {
        if (modelo.getJogador().getInventario().getItens().isEmpty()) {
            appendLog("Você não tem itens para usar! 🎒");
            return;
        }

        JDialog dialog = new JDialog(this, "USAR ITEM", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(650, 450);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setBackground(Color.BLACK);

        DefaultListModel<String> listModel = new DefaultListModel<>();
        List<Item> itensDisponiveis = modelo.getJogador().getInventario().getItens().stream()
                .filter(item -> {
                    TipoItem tipo = item.getTipo();
                    return tipo == TipoItem.COMIDA || tipo == TipoItem.MEDICAMENTO || tipo == TipoItem.MUNICAO || tipo == TipoItem.EXPLOSIVO || tipo == TipoItem.ARMA || tipo == TipoItem.ARMADURA;
                })
                .toList();

        for (Item item : itensDisponiveis) {
            String acao = "Usar:";
            String infoExtra = "";
            if (item.getTipo() == TipoItem.ARMA || item.getTipo() == TipoItem.ARMADURA) {
                acao = "Equipar:";
            } else if (item.getTipo() == TipoItem.MUNICAO) {
                infoExtra = " (x" + item.getValor() + ")";
            }
            listModel.addElement(acao + " " + item.getNome() + infoExtra + " - " + item.getDescricao());
        }

        JList<String> lista = new JList<>(listModel);
        lista.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lista.setFont(new Font("Arial", Font.PLAIN, 16));
        lista.setBackground(new Color(20, 20, 20));
        lista.setForeground(Color.WHITE);
        lista.setSelectionBackground(new Color(80, 0, 0));
        lista.setSelectionForeground(Color.WHITE);
        lista.setBorder(BorderFactory.createLineBorder(Color.RED, 2));

        JLabel lblInstrucao = new JLabel("<html><p style='padding: 10px; color: #fff; font-weight: bold;'>Selecione um item para usar em combate.</p></html>", JLabel.CENTER);
        lblInstrucao.setBackground(Color.BLACK);
        lblInstrucao.setForeground(Color.WHITE);
        lblInstrucao.setFont(new Font("Arial", Font.BOLD, 16));
        dialog.add(lblInstrucao, BorderLayout.NORTH);

        JButton btnUsar = new JButton("USAR 💉");
        estiloBotaoCombatePequeno(btnUsar);
        btnUsar.addActionListener(e -> {
            int indice = lista.getSelectedIndex();
            if (indice != -1) {
                Item itemSelecionado = itensDisponiveis.get(indice);
                ControladorJogo controller = new ControladorJogo((TelaPrincipal)getParent());
                controller.usarItemCombate(itemSelecionado, sistema, this);
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog, "Selecione um item primeiro!");
            }
        });

        dialog.add(new JScrollPane(lista), BorderLayout.CENTER);

        JPanel painelBotao = new JPanel(new FlowLayout(FlowLayout.CENTER));
        painelBotao.setBackground(Color.BLACK);
        painelBotao.add(btnUsar);
        dialog.add(painelBotao, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private void estiloBotaoCombatePequeno(JButton botao) {
        botao.setFont(new Font("Arial", Font.BOLD, 16));
        botao.setBackground(new Color(30, 30, 30));
        botao.setForeground(Color.RED);
        botao.setFocusPainted(false);
        botao.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
        botao.setPreferredSize(new Dimension(160, 45));

        botao.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                botao.setBackground(new Color(60, 0, 0));
                botao.setForeground(Color.BLACK);
            }
            public void mouseExited(MouseEvent evt) {
                botao.setBackground(new Color(30, 30, 30));
                botao.setForeground(Color.RED);
            }
        });
    }

    public void atualizarListaAlvos() {
        comboAlvos.removeAllItems();
        List<Zumbi> inimigos = modelo.getInimigosAtuais();
        if (inimigos == null || inimigos.isEmpty()) {
            comboAlvos.addItem("NENHUM");
        } else {
            for (int i = 0; i < inimigos.size(); i++) {
                Zumbi z = inimigos.get(i);
                if (z.estaVivo()) {
                    comboAlvos.addItem("• " + z.getNome() + " (" + z.getSaude() + " HP)");
                }
            }
        }
        if (comboAlvos.getItemCount() > 0) {
            comboAlvos.setSelectedIndex(0);
        }

        comboAlvos.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (c instanceof JLabel) {
                    JLabel label = (JLabel) c;
                    label.setForeground(Color.WHITE);
                    label.setFont(new Font("Arial", Font.BOLD, 16));
                    if (isSelected) {
                        label.setBackground(new Color(80, 0, 0));
                    } else {
                        label.setBackground(new Color(20, 20, 20));
                    }
                }
                return c;
            }
        });
    }

    public void appendLog(String linha) {
        areaLog.append(linha + "\n");
        areaLog.setCaretPosition(areaLog.getDocument().getLength());
    }

    public void atualizarStatus() {
        lblJogador.setText("Jogador: " + modelo.getJogador().getSaude() + "/" + modelo.getJogador().getSaudeMaxima());
        barraVidaJogador.setMaximum(modelo.getJogador().getSaudeMaxima());
        barraVidaJogador.setValue(modelo.getJogador().getSaude());

        double porcentagemVida = (double) modelo.getJogador().getSaude() / modelo.getJogador().getSaudeMaxima();
        if (porcentagemVida > 0.70) {
            barraVidaJogador.setForeground(Color.GREEN);
        } else if (porcentagemVida >= 0.30) {
            barraVidaJogador.setForeground(Color.ORANGE);
        } else {
            barraVidaJogador.setForeground(Color.RED);
        }

        lblCargas.setText("Cargas H1=" + modelo.getJogador().getCargasHabilidade1() + " H2=" + modelo.getJogador().getCargasHabilidade2());

        Zumbi alvoAtual = null;
        List<Zumbi> inimigos = modelo.getInimigosAtuais();
        int inimigosVivos = 0;

        if (inimigos != null) {
            for (Zumbi z : inimigos) {
                if (z.estaVivo()) {
                    inimigosVivos++;
                    if (alvoAtual == null) {
                        alvoAtual = z;
                    }
                }
            }
        }

        if (alvoAtual != null) {
            lblAlvo.setText("Alvo: " + alvoAtual.getNome() + " - " + alvoAtual.getSaude() + " HP (Restam: " + inimigosVivos + ")");
        } else {
            lblAlvo.setText("Alvo: -");
        }

        if (modelo.getJogador().getArmaEquipada() != null) {
            Arma armaEquipada = modelo.getJogador().getArmaEquipada();
            String statusArma = armaEquipada.getNome() + " (Dano: " + armaEquipada.getDano();
            if (armaEquipada instanceof ArmaDeFogo) {
                statusArma += ", Munição: " + ((ArmaDeFogo) armaEquipada).getMunicao();
            } else {
                statusArma += ", Durabilidade: " + armaEquipada.getDurabilidade();
            }
            statusArma += ")";
            lblArmaStatus.setText("Arma: " + statusArma);
        } else {
            lblArmaStatus.setText("Arma: Nenhuma equipada");
        }

        atualizarListaAlvos();
        configurarBotoesHabilidades();

        btnUsarItem.setEnabled(modelo.getJogador().getInventario() != null && !modelo.getJogador().getInventario().getItens().isEmpty());
        btnUsarArma.setEnabled(modelo.getJogador().getArmaEquipada() != null);
        btnFugir.setEnabled(!modelo.isEventoFinalAtivo());
    }

    private void usarArmaEspecial() {
        if (combateEmAndamento) return;

        if (modelo.getJogador().getArmaEquipada() == null) {
            appendLog("Você não tem uma arma equipada! 🚫");
            return;
        }

        combateEmAndamento = true;
        desabilitarBotoes();

        new Thread(() -> {
            try {
                ControladorJogo controller = new ControladorJogo((TelaPrincipal)getParent());
                controller.executarTurnoCombate(sistema, 3, this);

                SwingUtilities.invokeLater(() -> {
                    if (!sistema.combateTerminado() && !sistema.jogadorDerrotado()) {
                        habilitarBotoes();
                        combateEmAndamento = false;
                    }
                });
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    habilitarBotoes();
                    combateEmAndamento = false;
                });
            }
        }).start();
    }

    public void setMensagem(String mensagem) {
        appendLog(mensagem);
    }
}