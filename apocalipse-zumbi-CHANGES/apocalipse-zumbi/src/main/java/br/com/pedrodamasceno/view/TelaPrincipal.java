package br.com.pedrodamasceno.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
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
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;

import br.com.pedrodamasceno.controller.ControladorJogo;
import br.com.pedrodamasceno.model.ModeloJogo;
import br.com.pedrodamasceno.model.itens.Item;
import br.com.pedrodamasceno.model.locais.Local;
import br.com.pedrodamasceno.model.locais.SubLocal;

public class TelaPrincipal extends JFrame {
    private ModeloJogo modelo;
    private ControladorJogo controlador;

    private JLabel lblDia, lblSaude, lblLocal, lblPeriodo;
    private JLabel lblSentimento;
    private JButton btnExplorar, btnDormir, btnUsarItem;
    private JTextArea areaStatus;
    private JPanel painelInventario;
    private JProgressBar barraVida;

    public TelaPrincipal(ModeloJogo modelo) {
        this.modelo = modelo;
        this.controlador = new ControladorJogo(this);

        setTitle("Apocalipse Zumbi");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);

        aplicarTemaApocaliptico();

        JPanel painelStatus = new JPanel(new GridLayout(6, 1, 5, 5));
        painelStatus.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        painelStatus.setBackground(Color.BLACK);

        Font labelFont = new Font("Arial", Font.BOLD, 18);
        lblDia = new JLabel("Dia: 1");
        lblDia.setFont(labelFont);
        lblDia.setForeground(Color.RED);

        lblPeriodo = new JLabel("Período: Manhã");
        lblPeriodo.setFont(labelFont);
        lblPeriodo.setForeground(Color.RED);

        lblSaude = new JLabel("Saúde: 100/100");
        lblSaude.setFont(labelFont);
        lblSaude.setForeground(Color.RED);

        lblLocal = new JLabel("Local: -");
        lblLocal.setFont(labelFont);
        lblLocal.setForeground(Color.RED);

        lblSentimento = new JLabel("Sentimento: Determinado");
        lblSentimento.setFont(labelFont);
        lblSentimento.setForeground(Color.RED);

        barraVida = new JProgressBar(0, 100);
        barraVida.setStringPainted(true);
        barraVida.setBackground(Color.DARK_GRAY);
        barraVida.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
        barraVida.setPreferredSize(new Dimension(200, 20));

        barraVida.setFont(new Font("Arial", Font.BOLD, 14));
        barraVida.setStringPainted(true);


        painelStatus.add(lblDia);
        painelStatus.add(lblPeriodo);
        painelStatus.add(lblSaude);
        painelStatus.add(barraVida);
        painelStatus.add(lblLocal);
        painelStatus.add(lblSentimento);

        areaStatus = new JTextArea();
        areaStatus.setEditable(false);
        areaStatus.setLineWrap(true);
        areaStatus.setWrapStyleWord(true);
        areaStatus.setFont(new Font("Arial", Font.BOLD, 16));
        areaStatus.setBackground(new Color(20, 20, 20));
        areaStatus.setForeground(Color.WHITE);
        areaStatus.setCaretColor(Color.WHITE);
        JScrollPane scrollStatus = new JScrollPane(areaStatus);

        painelInventario = new JPanel();
        painelInventario.setLayout(new BoxLayout(painelInventario, BoxLayout.Y_AXIS));
        painelInventario.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.RED, 2),
                "INVENTÁRIO",
                TitledBorder.CENTER,
                TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 18),
                Color.RED
        ));
        painelInventario.setBackground(Color.BLACK);
        JScrollPane scrollInventario = new JScrollPane(painelInventario);
        scrollInventario.setPreferredSize(new Dimension(280, 0));

        JPanel painelControles = new JPanel(new GridLayout(1, 3, 10, 10));
        painelControles.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        painelControles.setBackground(Color.BLACK);

        btnExplorar = new JButton("EXPLORAR");
        btnDormir = new JButton("DORMIR");
        btnUsarItem = new JButton("USAR ITEM");

        estiloBotaoGrande(btnExplorar);
        estiloBotaoGrande(btnDormir);
        estiloBotaoGrande(btnUsarItem);

        painelControles.add(btnExplorar);
        painelControles.add(btnDormir);
        painelControles.add(btnUsarItem);

        add(painelStatus, BorderLayout.NORTH);
        add(scrollStatus, BorderLayout.CENTER);
        add(scrollInventario, BorderLayout.EAST);
        add(painelControles, BorderLayout.SOUTH);

        btnExplorar.addActionListener(e -> {
            if (btnExplorar.getText().equals("SELECIONAR LOCAL")) {
                mostrarSelecaoLocal();
            } else {
                controlador.explorar();
            }
        });
        btnDormir.addActionListener(e -> controlador.dormir());
        btnUsarItem.addActionListener(e -> mostrarSelecaoItem());

        atualizarInterface();
    }

    private void aplicarTemaApocaliptico() {
        getContentPane().setBackground(Color.BLACK);
    }

    private void estiloBotaoGrande(JButton botao) {
        botao.setFont(new Font("Arial", Font.BOLD, 18));
        botao.setBackground(new Color(30, 30, 30));
        botao.setForeground(Color.RED);
        botao.setFocusPainted(false);
        botao.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
        botao.setPreferredSize(new Dimension(180, 50));
        botao.setMinimumSize(new Dimension(180, 50));
        botao.setMaximumSize(new Dimension(180, 50));

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

    public void setModelo(ModeloJogo modelo) {
        this.modelo = modelo;
        this.controlador = new ControladorJogo(this);
        atualizarInterface();
    }

    public ModeloJogo getModelo() {
        return modelo;
    }


    public void atualizarInterface() {
        if (modelo == null) return;

        lblDia.setText("Dia: " + modelo.getDiaAtual() + "/7");
        if (modelo.isEventoFinalAtivo()) {
            lblPeriodo.setText("Período: Evento Final");
        } else {
            lblPeriodo.setText("Período: " + modelo.getPeriodoAtual().getRotulo());
        }
        lblSaude.setText("Saúde: " + modelo.getJogador().getSaude() + "/" + modelo.getJogador().getSaudeMaxima());
        barraVida.setMaximum(modelo.getJogador().getSaudeMaxima());
        barraVida.setValue(modelo.getJogador().getSaude());
        barraVida.setForeground(Color.BLACK); // Texto preto barra progresso vida
        barraVida.setFont(new Font("Arial", Font.BOLD, 14));
        barraVida.setString(modelo.getJogador().getSaude() + " / " + modelo.getJogador().getSaudeMaxima());

        // CORREÇÃO: Definindo a cor do texto da barra de vida para preto e negrito
        UIManager.put("ProgressBar.foreground", Color.BLACK);
        UIManager.put("ProgressBar.background", Color.DARK_GRAY);
        UIManager.put("ProgressBar.selectionForeground", Color.BLACK);
        UIManager.put("ProgressBar.selectionBackground", Color.BLACK);
        barraVida.setFont(new Font("Arial", Font.BOLD, 14));

        double porcentagemVida = (double) modelo.getJogador().getSaude() / modelo.getJogador().getSaudeMaxima();
        if (porcentagemVida > 0.70) {
            barraVida.setForeground(Color.GREEN);
        } else if (porcentagemVida >= 0.30) {
            barraVida.setForeground(Color.ORANGE);
        } else {
            barraVida.setForeground(Color.RED);
        }

        lblLocal.setText("Local: " + nomeLocalDoDia(modelo.getDiaAtual()));
        lblSentimento.setText("Sentimento: " + modelo.getJogador().getSentimento());

        String rotuloPeriodo = modelo.isEventoFinalAtivo() ? "Evento Final" : modelo.getPeriodoAtual().getRotulo();
        areaStatus.setText("Dia " + modelo.getDiaAtual() + " (" + rotuloPeriodo + ")\n");
        String mensagem = modelo.getMensagem() != null ? modelo.getMensagem() : modelo.obterDescricaoLocalDoDia(modelo.getDiaAtual());
        areaStatus.append(mensagem + "\n\n");

        if (modelo.isJogoTerminado()) {
            areaStatus.append("FIM DE JOGO - A sua jornada chegou ao fim!\n");
        } else if (modelo.isJogoVencido()) {
            areaStatus.append("VITÓRIA! - Você conseguiu escapar! O pesadelo acabou... por enquanto.\n");
        } else {
            areaStatus.append("STATUS ATUAIS:\n");
            areaStatus.append("- Força: " + modelo.getJogador().getForca() + "\n");
            areaStatus.append("- Destreza: " + modelo.getJogador().getDestreza() + "\n");
            areaStatus.append("- Inteligência: " + modelo.getJogador().getInteligencia() + "\n");
            areaStatus.append("- Constituição: " + modelo.getJogador().getConstituicao() + "\n");
            areaStatus.append("INVENTÁRIO: " + modelo.getJogador().getInventario().getTamanhoAtual() +
                    "/" + modelo.getJogador().getInventario().getCapacidade() + " itens\n");
        }

        atualizarInventario();
        atualizarBotoes();
    }

    private String nomeLocalDoDia(int dia) {
        return modelo.getLocalAtual() != null ? modelo.getLocalAtual().getNome() : "Local não selecionado";
    }

    private void atualizarInventario() {
        painelInventario.removeAll();
        if (modelo != null && modelo.getJogador() != null) {
            for (Item item : modelo.getJogador().getInventario().getItens()) {
                JLabel lblItem = new JLabel("• " + item.getNome() + " (" + item.getTipo() + ")");
                lblItem.setFont(new Font("Arial", Font.BOLD, 16));
                lblItem.setForeground(Color.WHITE);
                lblItem.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
                painelInventario.add(lblItem);
            }
        }
        painelInventario.revalidate();
        painelInventario.repaint();
    }

    private void atualizarBotoes() {
        if (modelo.isJogoTerminado() || modelo.isJogoVencido()) {
            btnDormir.setEnabled(false);
            btnExplorar.setEnabled(false);
            btnUsarItem.setEnabled(false);
            return;
        }

        if (modelo.isEventoFinalAtivo()) {
            btnDormir.setEnabled(false);
            btnExplorar.setText("EXPLORAR");
            btnExplorar.setEnabled(true);
            btnUsarItem.setEnabled(!modelo.getJogador().getInventario().getItens().isEmpty());
            return;
        }

        if (modelo.getExploracoesDiaAtual() >= 3) {
            btnDormir.setEnabled(!modelo.isEmCombate() && !modelo.isDormiu());
            btnExplorar.setEnabled(false);
            btnUsarItem.setEnabled(!modelo.getJogador().getInventario().getItens().isEmpty());
            return;
        }

        btnDormir.setEnabled(false);
        btnUsarItem.setEnabled(!modelo.getJogador().getInventario().getItens().isEmpty());

        if (modelo.getDiaAtual() == 1) {
            btnExplorar.setText("EXPLORAR");
            btnExplorar.setEnabled(!modelo.isEmCombate() && modelo.getLocalAtual() != null);
        } else if (modelo.getDiaAtual() > 1 && modelo.getDiaAtual() < 7) {
            if (modelo.getLocalAtual() == null) {
                btnExplorar.setText("SELECIONAR LOCAL");
                btnExplorar.setEnabled(!modelo.isEmCombate() && modelo.getExploracoesDiaAtual() < 3 && !modelo.isDormiu());
            } else {
                btnExplorar.setText("EXPLORAR");
                btnExplorar.setEnabled(!modelo.isEmCombate() && modelo.getExploracoesDiaAtual() < 3 && !modelo.isDormiu());
            }
        } else {
            btnExplorar.setText("EXPLORAR");
            btnExplorar.setEnabled(false);
        }
    }

    public void mostrarSelecaoItem() {
        if (modelo == null || modelo.getJogador().getInventario().getItens().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Você não tem itens para usar ou descartar!", "Inventário Vazio", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog(this, "USAR ITEM", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(650, 450);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setBackground(Color.BLACK);

        DefaultListModel<String> listModel = new DefaultListModel<>();
        for (Item item : modelo.getJogador().getInventario().getItens()) {
            listModel.addElement("• " + item.getNome() + " (" + item.getTipo() + ") - " + item.getDescricao());
        }

        JList<String> lista = new JList<>(listModel);
        lista.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lista.setFont(new Font("Arial", Font.BOLD, 16));
        lista.setBackground(new Color(20, 20, 20));
        lista.setForeground(Color.WHITE);
        lista.setSelectionBackground(new Color(80, 0, 0));
        lista.setSelectionForeground(Color.WHITE);
        lista.setBorder(BorderFactory.createLineBorder(Color.RED, 2));

        JLabel lblInstrucao = new JLabel("<html><p style='padding: 10px; color: #fff; font-weight: bold;'>Escolha um item para usar ou descartar. A sua vida pode depender disso.</p></html>", JLabel.CENTER);
        lblInstrucao.setBackground(Color.BLACK);
        lblInstrucao.setForeground(Color.WHITE);
        lblInstrucao.setFont(new Font("Arial", Font.BOLD, 16));
        dialog.add(lblInstrucao, BorderLayout.NORTH);

        JButton btnUsar = new JButton("USAR ITEM");
        estiloBotaoPequeno(btnUsar);
        btnUsar.addActionListener(e -> {
            int indice = lista.getSelectedIndex();
            if (indice != -1) {
                Item itemSelecionado = modelo.getJogador().getInventario().getItens().get(indice);
                controlador.usarItem(itemSelecionado);
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog, "Você precisa selecionar um item.", "Atenção", JOptionPane.WARNING_MESSAGE);
            }
        });

        JButton btnDescartar = new JButton("DESCARTAR ITEM");
        estiloBotaoPequeno(btnDescartar);
        btnDescartar.addActionListener(e -> {
            int indice = lista.getSelectedIndex();
            if (indice != -1) {
                Item itemSelecionado = modelo.getJogador().getInventario().getItens().get(indice);
                int confirm = JOptionPane.showConfirmDialog(dialog, "Tem certeza que deseja descartar " + itemSelecionado.getNome() + "?", "Confirmação", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (confirm == JOptionPane.YES_OPTION) {
                    controlador.descartarItem(itemSelecionado);
                    dialog.dispose();
                }
            } else {
                JOptionPane.showMessageDialog(dialog, "Você precisa selecionar um item para descartar.", "Atenção", JOptionPane.WARNING_MESSAGE);
            }
        });

        dialog.add(new JScrollPane(lista), BorderLayout.CENTER);

        JPanel painelBotoesAcao = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        painelBotoesAcao.setBackground(Color.BLACK);
        painelBotoesAcao.add(btnUsar);
        painelBotoesAcao.add(btnDescartar);
        dialog.add(painelBotoesAcao, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    public void mostrarSelecaoSubLocal() {
        if (modelo == null || modelo.getLocalAtual().getSubLocais().isEmpty()) return;

        JDialog dialog = new JDialog(this, "EXPLORAR ÁREA", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(900, 500);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setBackground(Color.BLACK);

        DefaultListModel<String> listModel = new DefaultListModel<>();
        List<SubLocal> todosSubLocais = modelo.getLocalAtual().getSubLocais();
        List<SubLocal> sublocaisVisitados = modelo.getSublocaisVisitados(modelo.getLocalAtual());
        List<SubLocal> sublocaisDisponiveis = new ArrayList<>(todosSubLocais);
        sublocaisDisponiveis.removeAll(sublocaisVisitados);

        if (sublocaisDisponiveis.isEmpty()) {
            JOptionPane.showMessageDialog(dialog, "Você já explorou todas as áreas disponíveis neste local!", "Exploração Concluída", JOptionPane.INFORMATION_MESSAGE);
            dialog.dispose();
            return;
        }

        for (SubLocal subLocal : sublocaisDisponiveis) {
            listModel.addElement("• " + subLocal.getNome() + " - " + subLocal.getDescricao() + " (Perigo: " + subLocal.getNivelPerigo() + ")");
        }

        JList<String> lista = new JList<>(listModel);
        lista.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lista.setFont(new Font("Arial", Font.BOLD, 18));
        lista.setBackground(new Color(20, 20, 20));
        lista.setForeground(Color.WHITE);
        lista.setSelectionBackground(new Color(80, 0, 0));
        lista.setSelectionForeground(Color.WHITE);
        lista.setBorder(BorderFactory.createLineBorder(Color.RED, 2));

        JLabel lblInstrucao = new JLabel("<html><p style='padding: 10px; color: #fff; font-weight: bold;'>Selecione uma área para explorar com cuidado. O perigo espreita em cada canto.</p></html>", JLabel.CENTER);
        lblInstrucao.setBackground(Color.BLACK);
        lblInstrucao.setForeground(Color.WHITE);
        lblInstrucao.setFont(new Font("Arial", Font.BOLD, 16));
        dialog.add(lblInstrucao, BorderLayout.NORTH);

        JButton btnExplorarSubLocal = new JButton("EXPLORAR ÁREA");
        estiloBotaoPequeno(btnExplorarSubLocal);
        btnExplorarSubLocal.addActionListener(e -> {
            int indice = lista.getSelectedIndex();
            if (indice != -1) {
                SubLocal subLocalSelecionado = sublocaisDisponiveis.get(indice);
                controlador.explorarSubLocal(subLocalSelecionado);
                dialog.dispose();
                atualizarInterface();
            } else {
                JOptionPane.showMessageDialog(dialog, "Você precisa escolher uma área para explorar.", "Atenção", JOptionPane.WARNING_MESSAGE);
            }
        });

        dialog.add(new JScrollPane(lista), BorderLayout.CENTER);

        JPanel painelBotoesAcao = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        painelBotoesAcao.setBackground(Color.BLACK);
        painelBotoesAcao.add(btnExplorarSubLocal);
        dialog.add(painelBotoesAcao, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    public void mostrarResumoDoDia(String mensagemResumo) {
        JDialog dialog = new JDialog(this, "Resumo do Dia", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(700, 500);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setBackground(Color.BLACK);

        JTextArea areaResumo = new JTextArea();
        areaResumo.setEditable(false);
        areaResumo.setLineWrap(true);
        areaResumo.setWrapStyleWord(true);
        areaResumo.setFont(new Font("Arial", Font.PLAIN, 16));
        areaResumo.setBackground(new Color(20, 20, 20));
        areaResumo.setForeground(Color.WHITE);
        areaResumo.setText(mensagemResumo);

        JScrollPane scrollPane = new JScrollPane(areaResumo);
        dialog.add(scrollPane, BorderLayout.CENTER);

        JButton btnOk = new JButton("OK");
        estiloBotaoPequeno(btnOk);
        btnOk.addActionListener(e -> dialog.dispose());

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER));
        painelBotoes.setBackground(Color.BLACK);
        painelBotoes.add(btnOk);
        dialog.add(painelBotoes, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    public void mostrarSelecaoLocal() {
        if (modelo == null) return;

        JDialog dialog = new JDialog(this, "SELECIONAR LOCAL", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(650, 450);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setBackground(Color.BLACK);

        DefaultListModel<String> listModel = new DefaultListModel<>();
        for (Local local : modelo.getLocaisDisponiveisParaSelecao()) {
            listModel.addElement("• " + local.getNome() + " - " + local.getDescricao() + " (Perigo: " + local.getNivelPerigo() + ")");
        }

        JList<String> lista = new JList<>(listModel);
        lista.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lista.setFont(new Font("Arial", Font.BOLD, 16));
        lista.setBackground(new Color(20, 20, 20));
        lista.setForeground(Color.WHITE);
        lista.setSelectionBackground(new Color(80, 0, 0));
        lista.setSelectionForeground(Color.WHITE);
        lista.setBorder(BorderFactory.createLineBorder(Color.RED, 2));

        JLabel lblInstrucao = new JLabel("<html><p style='padding: 10px; color: #fff; font-weight: bold;'>O mundo lá fora é perigoso. Escolha seu próximo destino com sabedoria.</p></html>", JLabel.CENTER);
        lblInstrucao.setBackground(Color.BLACK);
        lblInstrucao.setForeground(Color.WHITE);
        lblInstrucao.setFont(new Font("Arial", Font.BOLD, 14));
        dialog.add(lblInstrucao, BorderLayout.NORTH);

        JButton btnConfirmarSelecao = new JButton("CONFIRMAR SELEÇÃO");
        estiloBotaoPequeno(btnConfirmarSelecao);
        btnConfirmarSelecao.addActionListener(e -> {
            int indice = lista.getSelectedIndex();
            if (indice != -1) {
                Local localSelecionado = modelo.getLocaisDisponiveisParaSelecao().get(indice);
                controlador.selecionarLocal(localSelecionado);
                dialog.dispose();
                atualizarInterface();
            } else {
                JOptionPane.showMessageDialog(dialog, "Você precisa escolher um local para ir.", "Atenção", JOptionPane.WARNING_MESSAGE);
            }
        });

        dialog.add(new JScrollPane(lista), BorderLayout.CENTER);

        JPanel painelBotoesAcao = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        painelBotoesAcao.setBackground(Color.BLACK);
        painelBotoesAcao.add(btnConfirmarSelecao);
        dialog.add(painelBotoesAcao, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private void estiloBotaoPequeno(JButton botao) {
        botao.setFont(new Font("Arial", Font.BOLD, 16));
        botao.setBackground(new Color(30, 30, 30));
        botao.setForeground(Color.RED);
        botao.setFocusPainted(false);
        botao.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
        botao.setPreferredSize(new Dimension(200, 45));

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

    public static void mostrarIntroducao() {
        TelaIntroducao telaIntroducao = new TelaIntroducao(null);
        telaIntroducao.setVisible(true);
    }
}