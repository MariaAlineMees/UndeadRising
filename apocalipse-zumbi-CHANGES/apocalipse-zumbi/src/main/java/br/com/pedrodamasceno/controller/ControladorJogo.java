package br.com.pedrodamasceno.controller;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList; // NOVO: Para acumular logs do dia
import java.util.List; // NOVO: Para armazenar resultados de exploração
import java.util.Map; // NOVO: Para lógica de chance de itens
import java.util.Random;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager; // Importar ArmaDeFogo

import br.com.pedrodamasceno.model.ModeloJogo; // Importar Armadura
import br.com.pedrodamasceno.model.combate.SistemaCombate;
import br.com.pedrodamasceno.model.itens.Arma;
import br.com.pedrodamasceno.model.itens.ArmaDeFogo; // Importar Municao
import br.com.pedrodamasceno.model.itens.Armadura; // Importar SubLocal
import br.com.pedrodamasceno.model.itens.Item; // Importar SubLocal
import br.com.pedrodamasceno.model.locais.SubLocal; // Importar SubLocal
import br.com.pedrodamasceno.model.personagens.Personagem;
import br.com.pedrodamasceno.model.status.EfeitoStatus;
import br.com.pedrodamasceno.model.status.StatusEffect;
import br.com.pedrodamasceno.model.zumbis.Zumbi;
import br.com.pedrodamasceno.view.TelaCombate;
import br.com.pedrodamasceno.view.TelaGameOver;
import br.com.pedrodamasceno.view.TelaPrincipal;
import br.com.pedrodamasceno.view.TelaSelecaoPersonagem;

public class ControladorJogo {

    private ModeloJogo modelo;
    private TelaPrincipal telaPrincipal;
    private final List<String> logsDiarios = new ArrayList<>(); // NOVO: Para acumular logs do dia, agora final

    public ControladorJogo() { }

    public ControladorJogo(TelaPrincipal telaPrincipal) {
        this.telaPrincipal = telaPrincipal;
        this.modelo = telaPrincipal.getModelo();
    }

    public void iniciarJogo() {
        TelaSelecaoPersonagem telaSelecao = new TelaSelecaoPersonagem(null);
        telaSelecao.setLocationRelativeTo(null);
        telaSelecao.setVisible(true);

        Personagem personagem = telaSelecao.getPersonagemSelecionado();
        if (personagem != null) {
            TelaPrincipal.mostrarIntroducao();
            modelo = new ModeloJogo();
            modelo.iniciarNovoJogo(personagem);

            if (telaPrincipal == null) {
                telaPrincipal = new TelaPrincipal(modelo);
            } else {
                telaPrincipal.setModelo(modelo);
            }
            this.logsDiarios.clear(); // Limpar logs ao iniciar novo jogo
            this.logsDiarios.add("Início do Dia " + modelo.getDiaAtual() + ". Local: " + modelo.obterNomeLocalDoDia(modelo.getDiaAtual()));

            telaPrincipal.atualizarInterface();
            telaPrincipal.setLocationRelativeTo(null);
            telaPrincipal.setVisible(true);
        }
    }


    public void explorar() {
        if (modelo.isEventoFinalAtivo()) {
            iniciarEventoFinalDia7();
            return;
        }
        if (modelo.isEmCombate()) {
            modelo.setMensagem("Você está em combate! Não pode explorar agora.");
            return;
        }

        Map<String, Object> resultadosExploracao = modelo.explorar();

        // Tratar mensagem de status (erros ou estados especiais do modelo)
        if (resultadosExploracao.get("mensagemStatus") != null) {
            modelo.setMensagem((String) resultadosExploracao.get("mensagemStatus"));
            logsDiarios.add((String) resultadosExploracao.get("mensagemStatus"));
            telaPrincipal.atualizarInterface(); // Adicionar para atualizar a tela
            verificarFimJogo(); // Adicionar para verificar o fim do jogo
            return; // Impede processamento adicional se for uma mensagem de erro/estado
        }

        // Tratar seleção de sublocal
        System.out.println("DEBUG: ControladorJogo.explorar() - selecaoSubLocalIniciada: " + resultadosExploracao.get("selecaoSubLocalIniciada")); // DEBUG
        System.out.println("DEBUG: ModeloJogo.isEmSelecaoSubLocal(): " + modelo.isEmSelecaoSubLocal()); // DEBUG
        if ((boolean) resultadosExploracao.get("selecaoSubLocalIniciada")) {
            modelo.setMensagem((String) resultadosExploracao.get("mensagem"));
            // logsDiarios.add("Você chegou em " + modelo.getLocalAtual().getNome() + ". Escolha uma área para explorar."); // REMOVIDO: Evita duplicação
            telaPrincipal.mostrarSelecaoSubLocal();
            telaPrincipal.atualizarInterface();
            return;
        }

        // Tratar encontro de zumbis
        if ((boolean) resultadosExploracao.get("combateIniciado")) {
            String nomeLocalOuSublocal = resultadosExploracao.get("subLocalExplorado") != null ? (String) resultadosExploracao.get("subLocalExplorado") : (String) resultadosExploracao.get("localExplorado");
            String periodo = (String) resultadosExploracao.get("periodo");
            logsDiarios.add("Dia " + modelo.getDiaAtual() + " (" + modelo.getPeriodoAtual().getRotulo() + ") - Você explorou " + nomeLocalOuSublocal + " e encontrou zumbis! Prepare-se para lutar!");
            iniciarCombate();
            return;
        }

        // Se não iniciou seleção de sublocal nem combate, então é uma exploração direta com item ou nada
        String nomeLocalOuSublocal = resultadosExploracao.get("subLocalExplorado") != null ? (String) resultadosExploracao.get("subLocalExplorado") : (String) resultadosExploracao.get("localExplorado");
        String periodo = (String) resultadosExploracao.get("periodo");
        String mensagemBaseExploracao =   " - Você explorou " + nomeLocalOuSublocal;

        // Tratar item encontrado
        Item itemEncontrado = (Item) resultadosExploracao.get("itemEncontrado");
        if (itemEncontrado != null) {
            String logItem = "Dia " + modelo.getDiaAtual() + " (" + modelo.getPeriodoAtual().getRotulo() + ") - Você explorou " + nomeLocalOuSublocal + " e encontrou: " + itemEncontrado.getNome() + "!";
            logsDiarios.add(logItem);
            String mensagemPrincipalItem = "Você explorou " + nomeLocalOuSublocal + " e encontrou: " + itemEncontrado.getNome() + "!";
            modelo.setMensagem(mensagemPrincipalItem);
        } else {
            String logNada = "Dia " + modelo.getDiaAtual() + " (" + modelo.getPeriodoAtual().getRotulo() + ") - Você explorou " + nomeLocalOuSublocal + ", mas não encontrou nada útil.";
            logsDiarios.add(logNada);
            String mensagemPrincipalNada = "Você explorou " + nomeLocalOuSublocal + ", mas não encontrou nada útil.";
            modelo.setMensagem(mensagemPrincipalNada);
        }
        
        // NOVO: Marcar exploração como concluída e avançar período APENAS se não houve combate ou seleção de sublocal
        modelo.marcarExploracaoConcluida();
        
        telaPrincipal.atualizarInterface();
        verificarFimJogo();
    }

    public void explorarSubLocal(SubLocal subLocal) {
        // NOVO: Marcar o sublocal como visitado permanentemente (para este jogo) no início da exploração
        modelo.marcarSubLocalComoVisitado(modelo.getLocalAtual(), subLocal);

        Map<String, Object> resultadosExploracao = modelo.explorarSubLocal(subLocal);
        // Remove a mensagem de exploração genérica, será substituída por mais detalhada abaixo

        // Tratar mensagem de status (erros ou estados especiais do modelo)
        if (resultadosExploracao.get("mensagemStatus") != null) {
            modelo.setMensagem((String) resultadosExploracao.get("mensagemStatus"));
            logsDiarios.add((String) resultadosExploracao.get("mensagemStatus"));
            telaPrincipal.atualizarInterface(); // Adicionar para atualizar a tela
            verificarFimJogo(); // Adicionar para verificar o fim do jogo
            return; // Impede processamento adicional se for uma mensagem de erro/estado
        }

        // Tratar encontro de zumbis
        if ((boolean) resultadosExploracao.get("combateIniciado")) {
            String nomeSublocal = (String) resultadosExploracao.get("subLocalExplorado");
            String nomeLocal = (String) resultadosExploracao.get("localExplorado");
            String periodo = (String) resultadosExploracao.get("periodo");
            logsDiarios.add("Dia " + modelo.getDiaAtual() + " (" + modelo.getPeriodoAtual().getRotulo() + ") - Você explorou " + nomeSublocal + " em " + nomeLocal + " e encontrou zumbis! Prepare-se para lutar!");
            iniciarCombate();
            return;
        }

        // Se não iniciou combate, então é uma exploração de sublocal direta com item ou nada
        String nomeSublocal = (String) resultadosExploracao.get("subLocalExplorado");
        String nomeLocal = (String) resultadosExploracao.get("localExplorado");
        String periodo = (String) resultadosExploracao.get("periodo");
        // String mensagemBaseExploracao = modelo.getDiaAtual() + " - Você explorou " + nomeSublocal + " em " + nomeLocal; // REMOVIDO

        // Tratar item encontrado
        Item itemEncontrado = (Item) resultadosExploracao.get("itemEncontrado");
        if (itemEncontrado != null) {
            String logItem = "Dia " + modelo.getDiaAtual() + " (" + modelo.getPeriodoAtual().getRotulo() + ") - Você explorou " + nomeSublocal + " em " + nomeLocal + " e encontrou: " + itemEncontrado.getNome() + "!";
            logsDiarios.add(logItem);
            String mensagemPrincipalItem = "Você explorou " + nomeSublocal + " em " + nomeLocal + " e encontrou: " + itemEncontrado.getNome() + "!";
            modelo.setMensagem(mensagemPrincipalItem);
        } else {
            String logNada = "Dia " + modelo.getDiaAtual() + " (" + modelo.getPeriodoAtual().getRotulo() + ") - Você explorou " + nomeSublocal + " em " + nomeLocal + ", mas não encontrou nada útil.";
            logsDiarios.add(logNada);
            String mensagemPrincipalNada = "Você explorou " + nomeSublocal + " em " + nomeLocal + ", mas não encontrou nada útil.";
            modelo.setMensagem(mensagemPrincipalNada);
        }

        // NOVO: Marcar exploração como concluída e avançar período APENAS se não houve combate
        modelo.marcarExploracaoConcluida();

        // Finalizar exploração (apenas marca o período, avanço de período será no ControladorJogo)

        telaPrincipal.atualizarInterface();
        verificarFimJogo();
    }

    // Novo método para selecionar um local (para dias intermediários)
    public void selecionarLocal(br.com.pedrodamasceno.model.locais.Local local) {
        modelo.setLocalAtual(local);
        
        // REMOVIDO: Linha de debug, pois a seleção de local não deve afetar a contagem de turnos.
        // String logSelecaoCompleta = "Dia " + modelo.getDiaAtual() + " (" + modelo.getPeriodoAtual().getRotulo() + ") - Você selecionou o local: " + local.getNome() + ".";
        logsDiarios.add("Dia " + modelo.getDiaAtual() + " - Você selecionou o local: " + local.getNome() + ".");
        String mensagemPrincipal = "Você selecionou o local: " + local.getNome() + ". Agora pode explorar este local.";
        modelo.setMensagem(mensagemPrincipal);
        telaPrincipal.atualizarInterface();

        // REMOVIDO: Seleção de local não é um turno, então não deve chamar marcarExploracaoConcluida()
        // E também removida a linha de debug associada a essa chamada.
    }

    private void iniciarCombate() {
        System.out.println("DEBUG: ControladorJogo.iniciarCombate - Iniciando combate. Modelo.emCombate antes: " + modelo.isEmCombate());
        // CORREÇÃO: Removido reset das habilidades - deve resetar apenas ao dormir
        // Não é necessário instanciar SistemaCombate aqui, pois TelaCombate já faz isso.
        TelaCombate telaCombate = new TelaCombate(telaPrincipal, modelo, modelo.getInimigosAtuais());
        telaCombate.setLocationRelativeTo(telaPrincipal);
        telaCombate.setVisible(true);
        System.out.println("DEBUG: ControladorJogo.iniciarCombate - Combate iniciado. Modelo.emCombate depois: " + modelo.isEmCombate());
        
        // Não chamar concluirExploracaoPeriodo aqui - será chamado quando o combate terminar
    }

    // NOVO MÉTODO: Processa o avanço de período ou a conclusão de todas as explorações
    
    public void dormir() {
        if (modelo.isEventoFinalAtivo()) {
            mostrarMensagem("Dia 7 - Evento Final: não é possível dormir agora.");
            return;
        }
        if (!modelo.podeDormir()) {
            mostrarMensagem("Você só pode dormir após explorar todos os períodos (Manhã, Tarde, Noite)!");
            return;
        }
        modelo.dormir();
        telaPrincipal.atualizarInterface();

        // Construir resumo do dia anterior usando logsDiarios
        StringBuilder resumo = new StringBuilder();
        resumo.append("Fim do Dia ").append(modelo.getDiaAtual()).append("!\n"); // Dia atual (que acabou de terminar)
        for (String log : logsDiarios) {
            resumo.append(log).append("\n");
        }
        resumo.append("Saúde recuperada: ").append(modelo.getJogador().getSaude() - modelo.getJogador().getSaudeMaxima() / 3).append(" HP.\n"); // Ajuste para mostrar o quanto realmente foi curado
        resumo.append(modelo.gerarMensagemNoite(modelo.getJogador().getSentimento())); // A mensagem da noite já está no modelo
        modelo.setResumoDiaAnterior(resumo.toString()); // Atualiza o resumo no modelo
        logsDiarios.clear(); // Limpar logs para o novo dia

        // NOVO: Mostrar pop-up de alerta de mudança de dia
        
        // Mostrar pop-up com o resumo do dia anterior
        telaPrincipal.mostrarResumoDoDia(modelo.getResumoDiaAnterior());

        verificarFimJogo();
        if (modelo.isEventoFinalAtivo() && !modelo.isEmCombate() && !modelo.isJogoTerminado()) {
            iniciarEventoFinalDia7();
        }
    }

    public void usarItem(Item item) {
        if (item == null || modelo == null) return;

        String mensagem = "Você usou: " + item.getNome();
        Personagem jogador = modelo.getJogador();

        if (item instanceof Arma arma) {
            if (!arma.estaQuebrada() && (arma instanceof ArmaDeFogo ? ((ArmaDeFogo) arma).getMunicao() > 0 : true)) {
                if (jogador.getArmaEquipada() != null) {
                    jogador.getInventario().adicionarItem(jogador.getArmaEquipada()); // Devolve a arma antiga para o inventário
                }
                jogador.equiparArma(arma);
                mensagem = "Você equipou: " + arma.getNome() + " (Dano: " + arma.getDano() + ")";
                jogador.getInventario().removerItem(arma); // Remove a arma do inventário ao equipar
            } else if (arma.estaQuebrada()) {
                mensagem = arma.getNome() + " está quebrada e não pode ser equipada.";
            } else if (arma instanceof ArmaDeFogo && ((ArmaDeFogo) arma).getMunicao() <= 0) {
                mensagem = arma.getNome() + " está sem munição e não pode ser equipada.";
            }
        } else if (item instanceof Armadura armadura) {
            if (!armadura.estaQuebrada()) {
                if (jogador.getArmaduraEquipada() != null) {
                    jogador.getInventario().adicionarItem(jogador.getArmaduraEquipada()); // Devolve a armadura antiga para o inventário
                }
                jogador.equiparArmadura(armadura);
                mensagem = "Você equipou: " + armadura.getNome() + " (Defesa: " + armadura.getDefesa() + ")";
                jogador.getInventario().removerItem(armadura); // Remove a armadura do inventário ao equipar
            } else {
                mensagem = armadura.getNome() + " está quebrada e não pode ser equipada.";
            }
        } else if (item.getTipo() == br.com.pedrodamasceno.model.itens.TipoItem.MUNICAO) {
            mensagem = jogador.recarregarArmaEquipada(item);
            System.out.println("DEBUG: Usando munição. Mensagem de recarga: " + mensagem); // DEBUG TEMPORÁRIO
        } else {
            switch (item.getTipo().name().toUpperCase()) {
                case "COMIDA":
                    int cura = item.getValor();
                    int saudeAntes = jogador.getSaude();
                    jogador.curar(cura);
                    int saudeDepois = jogador.getSaude();
                    int curado = saudeDepois - saudeAntes;
                    mensagem += " e recuperou " + curado + " de saúde!";
                    break;

                case "MEDICAMENTO":
                    int curaMed = item.getValor();
                    int saudeAntesMed = jogador.getSaude();
                    jogador.curar(curaMed);
                    int saudeDepoisMed = jogador.getSaude();
                    int curadoMed = saudeDepoisMed - saudeAntesMed;
                    mensagem += " e recuperou " + curadoMed + " de saúde!";
                    break;

                case "BUFF":
                    jogador.adicionarEfeito(new StatusEffect(EfeitoStatus.FORCA, 3, 2));
                    mensagem += " ganhando +2 de força por 3 turnos!";
                    break;

                case "EXPLOSIVO": // NOVO: Tratamento para explosivos
                    mensagem = item.getNome() + " só pode ser usado durante o combate!";
                    // Não remove o item do inventário
                    break;
                default:
                    mensagem += "!";
            }

            jogador.getInventario().removerItem(item);
        }

        modelo.setMensagem(mensagem);
        atualizarTela();
    }

    public void descartarItem(Item item) {
        if (item == null || modelo == null) return;
        modelo.getJogador().getInventario().removerItem(item);
        modelo.setMensagem("Você descartou: " + item.getNome() + ".");
        atualizarTela();
    }

    public void executarTurnoCombate(SistemaCombate sistema, int tipoAtaque, TelaCombate telaCombate) {
        try {
            // Executar ataque do jogador
            String resultadoAtaque = sistema.jogadorAtacaComDetalhes(tipoAtaque);
            telaCombate.appendLog(resultadoAtaque);
            telaCombate.atualizarStatus();

            if (sistema.combateTerminado()) {
                // MOSTRAR ALERTA DE VITÓRIA
                mostrarAlertaVitoria(telaCombate);
                finalizarCombateVitoria(telaCombate);
                return;
            }

            // Delay de 1 segundo antes dos inimigos atacarem
            Thread.sleep(1000);
            
            // Inimigos atacam com delay entre cada um
            List<String> logs = sistema.inimigosAtacam();
            for (String log : logs) {
                telaCombate.appendLog(log);
                Thread.sleep(800); // Delay de 800ms entre cada ataque de inimigo
            }

            if (sistema.jogadorDerrotado()) {
                mostrarAlertaDerrota(telaCombate);
                finalizarCombateDerrota(telaCombate);
                return;
            }

            telaCombate.atualizarStatus();
            telaCombate.setMensagem("Sua vez: escolha a ação!");
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }



    private void mostrarAlertaVitoria(TelaCombate telaCombate) {
        // Configura o UIManager para o JOptionPane com o tema apocalíptico
        UIManager.put("OptionPane.background", Color.BLACK);
        UIManager.put("Panel.background", Color.BLACK);
        UIManager.put("OptionPane.messageForeground", Color.WHITE);
        // Adiciona a cor da fonte dos botões para vermelho
        UIManager.put("Button.background", new Color(30, 30, 30));
        UIManager.put("Button.foreground", Color.RED);
        UIManager.put("Button.font", new Font("Arial", Font.BOLD, 14));

        StringBuilder mensagem = new StringBuilder("🎉 VOCÊ VENCEU!\n\n");

        for (Zumbi zumbi : modelo.getInimigosAtuais()) {
            if (!zumbi.estaVivo()) {
                mensagem.append("💀 ").append(zumbi.getNome()).append(" foi derrotado!\n");
            }
        }

        mensagem.append("\nRecuperando 20% de saúde pela vitória!");

        modelo.getJogador().curar(modelo.getJogador().getSaudeMaxima() / 5);
        modelo.getJogador().atualizarSentimentoJogador(1);

        JPanel panel = new JPanel();
        panel.setBackground(Color.BLACK);
        panel.add(new JLabel("<html><font color='white'>" + mensagem.toString().replace("\n", "<br>") + "</font></html>"));

        JOptionPane.showMessageDialog(telaCombate, panel, "Vitória!", JOptionPane.INFORMATION_MESSAGE);

        // Isso evita que outros JOptionPanes do sistema fiquem com a sua cor personalizada
        UIManager.put("Button.background", null);
        UIManager.put("Button.foreground", null);
        UIManager.put("Button.font", null);
    }

    private void mostrarAlertaDerrota(TelaCombate telaCombate) {
        // Configura o UIManager para o JOptionPane com o tema apocalíptico
        UIManager.put("OptionPane.background", Color.BLACK);
        UIManager.put("Panel.background", Color.BLACK);
        UIManager.put("OptionPane.messageForeground", Color.WHITE);
        // Adiciona a cor da fonte dos botões para vermelho
        UIManager.put("Button.background", new Color(30, 30, 30));
        UIManager.put("Button.foreground", Color.RED);
        UIManager.put("Button.font", new Font("Arial", Font.BOLD, 14));

        JPanel panel = new JPanel();
        panel.setBackground(Color.BLACK);
        panel.add(new JLabel("<html><font color='white'>💀 Você foi derrotado!<br><br>Sua jornada termina aqui...</font></html>"));

        JOptionPane.showMessageDialog(telaCombate, panel, "Derrota", JOptionPane.WARNING_MESSAGE);
        modelo.getJogador().atualizarSentimentoJogador(-1);

        // Redefine as configurações do UIManager para o padrão
        UIManager.put("Button.background", null);
        UIManager.put("Button.foreground", null);
        UIManager.put("Button.font", null);
    }

    private void finalizarCombateVitoria(TelaCombate telaCombate) {
        telaCombate.setMensagem("Você derrotou todos os inimigos!");
        logsDiarios.add("Você derrotou todos os inimigos!");
        modelo.setEmCombate(false);


        // Incrementar explorações e avançar período após combate
        modelo.marcarExploracaoConcluida();
        telaPrincipal.atualizarInterface();

        // Se for o evento final do dia 7, finalizar o jogo com vitória
        if (modelo.isEventoFinalAtivo()) {
            String msgVitoria = """
                    Dia 7 - Evento Final\n\n""" +
                    "Parabéns! Você sobreviveu por 7 dias e venceu o Boss.\n" +
                    "Você está seguro agora.";
            modelo.setMensagem(msgVitoria);
            logsDiarios.add(msgVitoria);
            modelo.setJogoTerminado(true);
            modelo.setJogoVencido(true);
            telaPrincipal.atualizarInterface();
            telaCombate.dispose();
            verificarFimJogo();
            return;
        }

        // Loot pós-combate (dias 1–6): vasculhar corpos para encontrar 1 item
        String lootMsg = null;
        if (modelo.getDiaAtual() <= 6) {
            Item loot = null;
            if (modelo.getDiaAtual() == 6) {
                Random random = new Random();
                if (random.nextInt(100) < 70) { // 70% de chance de receber Granada
                    loot = new br.com.pedrodamasceno.model.itens.Item(
                            "Granada",
                            "Explosivo de uso único (30 de dano em área)",
                            br.com.pedrodamasceno.model.itens.TipoItem.EXPLOSIVO,
                            30
                    );
                }
            }
            
            // Se não encontrou uma granada (Dia 6, 30% de chance ou inventário cheio) ou não é Dia 6, sorteia item aleatório
            if (loot == null || !modelo.getJogador().adicionarItem(loot)) {
                loot = modelo.encontrarItem(false); // Passa false para loot pós-combate
            }

            if (loot != null) {
                lootMsg = "Você encontrou no corpo do zumbi: " + loot.getNome() + "!";
                logsDiarios.add(lootMsg);
            } else {
                lootMsg = "Você vasculhou os corpos, mas não encontrou nada útil.";
                logsDiarios.add(lootMsg);
            }
        }

        // Concluir a exploração do período após vencer o combate

        // Garantir que a mensagem de loot apareça antes das mensagens de avanço/conclusão
        if (lootMsg != null && !lootMsg.isEmpty()) {
            String apos = modelo.getMensagem() == null ? "" : modelo.getMensagem();
            modelo.setMensagem(lootMsg + (apos.isEmpty() ? "" : ("\n" + apos)));
        }
        
        telaPrincipal.atualizarInterface();
        telaCombate.dispose();
        
        // Verificar se o jogador morreu mesmo após a vitória
        verificarFimJogo();

        // NOVO: Remover todos os StatusEffect temporários do jogador após o combate
        modelo.getJogador().removerTodosEfeitosTemporarios();
    }

    private void finalizarCombateDerrota(TelaCombate telaCombate) {
        telaCombate.setMensagem("Você foi derrotado!");
        logsDiarios.add("Você foi derrotado!");
        modelo.setEmCombate(false);
        modelo.setInimigosAtuais(java.util.List.of());
        
        // Desequipar arma e armadura e retornar ao inventário após a derrota
        desequiparItensAposCombate();

        // Incrementar explorações e avançar período após combate
        modelo.marcarExploracaoConcluida();
        telaPrincipal.atualizarInterface();

        // Marcar o jogo como terminado se o jogador morreu
        if (!modelo.getJogador().estaVivo()) {
            modelo.setJogoTerminado(true);
            logsDiarios.add("Fim de jogo: Você morreu.");
        }
        
        telaCombate.dispose();
        verificarFimJogo();

        // NOVO: Remover todos os StatusEffect temporários do jogador após o combate
        modelo.getJogador().removerTodosEfeitosTemporarios();
    }

    public void usarItemCombate(Item item, SistemaCombate sistema, TelaCombate tela) {
        if (item == null || sistema == null) return;

        String mensagem = processarUsoItemCombate(item);
        tela.appendLog(mensagem);
        logsDiarios.add(mensagem); // Adicionar uso de item em combate aos logs
        tela.atualizarStatus();
    }

    private String processarUsoItemCombate(Item item) {
        Personagem jogador = modelo.getJogador();
        String mensagem = "Usou " + item.getNome();

        switch (item.getTipo().name().toUpperCase()) {
            case "COMIDA":
                int cura = item.getValor();
                int saudeAntes = jogador.getSaude();
                jogador.curar(cura);
                int saudeDepois = jogador.getSaude();
                int curado = saudeDepois - saudeAntes;
                mensagem += " e recuperou " + curado + " de saúde!";
                // Se for RacaoCombate, aplicar seus efeitos adicionais
                if (item instanceof br.com.pedrodamasceno.model.itens.RacaoCombate racao) {
                    racao.consumir(jogador);
                    mensagem = racao.getNome() + " consumida. " + (curado > 0 ? "Recuperou " + curado + " HP e " : "") + "ganhou Força!";
                }
                jogador.getInventario().removerItem(item); // Remove a comida do inventário após uso
                break;
            case "MEDICAMENTO":
                int curaMed = item.getValor();
                int saudeAntesMed = jogador.getSaude();
                jogador.curar(curaMed);
                int saudeDepoisMed = jogador.getSaude();
                int curadoMed = saudeDepoisMed - saudeAntesMed;
                mensagem += " e recuperou " + curadoMed + " de saúde!";

                // Se for Antídoto, remover veneno
                if (item instanceof br.com.pedrodamasceno.model.itens.Antidoto antidoto) {
                    antidoto.usar(jogador);
                    mensagem = antidoto.getNome() + " usado. " + (curadoMed > 0 ? "Recuperou " + curadoMed + " HP e " : "") + "removeu veneno!";
                }
                // Se for Analgésico Forte, remover cansaço/medo
                else if (item instanceof br.com.pedrodamasceno.model.itens.AnalgesicoForte analgesico) {
                    analgesico.usar(jogador);
                    mensagem = analgesico.getNome() + " usado. " + (curadoMed > 0 ? "Recuperou " + curadoMed + " HP e " : "") + "removeu cansaço e medo!";
                }
                jogador.getInventario().removerItem(item); // Remove o medicamento do inventário após uso
                break;
            case "BUFF":
                // Manter lógica existente para buffs gerais
                jogador.adicionarEfeito(new StatusEffect(EfeitoStatus.FORCA, 3, 2));
                mensagem += " ganhando +2 de força por 3 turnos!";
                // Se for Lanterna Tática, aplicar buff de inteligência
                if (item instanceof br.com.pedrodamasceno.model.itens.LanternaTatica lanterna) {
                    lanterna.usar(jogador);
                    mensagem = lanterna.getNome() + " usada. Ganhou inteligência!";
                    jogador.getInventario().removerItem(item); // Remove a lanterna do inventário após uso
                } else {
                    jogador.getInventario().removerItem(item); // Remove outros buffs genéricos
                }
                break;
            case "EXPLOSIVO":
                // Para Isqueiro e Spray e Granada
                if (item instanceof br.com.pedrodamasceno.model.itens.IsqueiroESpray isqueiroESpray) {
                    aplicarDanoTodosInimigos(isqueiroESpray.getDanoBase());
                    mensagem += " e causou " + isqueiroESpray.getDanoBase() + " de dano em todos os inimigos!";
                } else { // Assume que é a Granada padrão
                    aplicarDanoTodosInimigos(item.getValor());
                    mensagem += " e causou " + item.getValor() + " de dano em todos os inimigos!";
                }
                jogador.getInventario().removerItem(item); // Remover o explosivo após o uso
                break;
            case "ARMADURA":
                if (item instanceof Armadura armadura) {
                    if (!armadura.estaQuebrada()) {
                        if (jogador.getArmaduraEquipada() != null) {
                            jogador.getInventario().adicionarItem(jogador.getArmaduraEquipada());
                        }
                        jogador.equiparArmadura(armadura);
                        mensagem = "Você equipou: " + armadura.getNome() + " (Defesa: " + armadura.getDefesa() + ") em combate!";
                        jogador.getInventario().removerItem(armadura);
                    } else {
                        mensagem = armadura.getNome() + " está quebrada e não pode ser equipada em combate.";
                    }
                }
                break;
            case "ARMA":
                if (item instanceof Arma arma) {
                    if (!arma.estaQuebrada() && (arma instanceof ArmaDeFogo armaDeFogo ? armaDeFogo.getMunicao() > 0 : true)) {
                        if (jogador.getArmaEquipada() != null) {
                            jogador.getInventario().adicionarItem(jogador.getArmaEquipada());
                        }
                        jogador.equiparArma(arma);
                        mensagem = "Você equipou: " + arma.getNome() + " (Dano: " + arma.getDano() + ") em combate!";
                        jogador.getInventario().removerItem(arma);
                    } else if (arma.estaQuebrada()) {
                        mensagem = arma.getNome() + " está quebrada e não pode ser equipada em combate.";
                    } else if (item instanceof ArmaDeFogo armaDeFogo && armaDeFogo.getMunicao() <= 0) {
                        mensagem = arma.getNome() + " está sem munição e não pode ser equipada em combate.";
                    }
                }
                break;
            case "MUNICAO":
                if (jogador.getArmaEquipada() instanceof ArmaDeFogo armaDeFogo) {
                    // Não é mais necessário o cast para Municao, pois o valor pode ser obtido de Item
                    int quantidadeMunicao = item.getValor(); 
                    armaDeFogo.recarregar(quantidadeMunicao);
                    mensagem = "Você recarregou o " + armaDeFogo.getNome() + " com " + quantidadeMunicao + " balas!";
                    jogador.getInventario().removerItem(item); // Remove a munição do inventário após uso
                } else {
                    mensagem = "Você não tem uma arma de fogo equipada para usar munição.";
                }
                break;
            case "UTILIDADE":
                if (item instanceof br.com.pedrodamasceno.model.itens.LanternaTatica lanterna) {
                    lanterna.usar(jogador);
                    mensagem = lanterna.getNome() + " usada. Ganhou inteligência em combate!";
                    jogador.getInventario().removerItem(item); // Remove a lanterna do inventário após uso
                }
                else {
                    mensagem += "!"; // Mensagem padrão para outros itens de utilidade
                    jogador.getInventario().removerItem(item); // Remove item de utilidade genérico
                }
                break;
            default:
                mensagem += "!";
                jogador.getInventario().removerItem(item); // Remover item se não for tratado acima
        }

        // A lógica de remoção do item agora é tratada dentro de cada case ou no default para itens genéricos.
        // O Mapa, Armas, Armaduras e Munição (se usada para recarregar) não são removidos aqui globalmente.
        // O jogador.getInventario().removerItem(item); já está dentro dos blocos específicos onde o item é consumido ou equipado.
        // Removendo a linha de remoção global para evitar dupla remoção ou remoção indevida.
        // if (!item.getTipo().name().equalsIgnoreCase("MUNICAO") && !(item instanceof Mapa) && !(item instanceof Arma) && !(item instanceof Armadura)) {
        //     jogador.getInventario().removerItem(item);
        // }

        return mensagem;
    }

    private void aplicarDanoTodosInimigos(int dano) {
        for (Zumbi zumbi : modelo.getInimigosAtuais()) {
            if (zumbi.estaVivo()) {
                zumbi.receberDano(dano);
            }
        }
    }


    // Dia 7 - Evento final IMERSIVO com ConfrontoFinal
    private void iniciarEventoFinalDia7() {
        if (!modelo.isEmCombate() && modelo.isEventoFinalAtivo() && !modelo.isJogoTerminado()) {
            // Inicializar o sistema de confronto final
            modelo.iniciarConfrontoFinal();
            
            // Mostrar introdução dramática
            String introducao = """
                    === DIA 7 - O HELICÓPTERO DE RESGATE ===
                    
                    O som dos rotores ecoa pela cidade devastada.
                    Um helicóptero militar se aproxima do ponto de extração.
                    Após 7 dias de sobrevivência, você finalmente chegou ao fim da jornada.
                    
                    Mas ao se aproximar da plataforma de pouso, uma figura emerge das sombras...
                    """;
                    
            JOptionPane.showMessageDialog(telaPrincipal, introducao, "Confronto Final", JOptionPane.WARNING_MESSAGE);
            
            // Iniciar o confronto com diálogo
            String mensagemConfronto = modelo.getConfrontoFinal().iniciarConfronto();
            mostrarDialogoConfrontoFinal(mensagemConfronto);
            
            logsDiarios.add("EVENTO FINAL: O confronto derradeiro começou!");
        }
    }


    private void verificarFimJogo() {
        // Verificar se o jogador morreu
        if (modelo.getJogador() != null && !modelo.getJogador().estaVivo()) {
            modelo.setJogoTerminado(true);
            modelo.setMensagem("Você morreu! Fim de jogo.");
            exibirTelaGameOver(false, modelo.getJogador().getDiasSobrevividos());
            return;
        }
        
        if (modelo.isJogoTerminado() || modelo.isJogoVencido()) {
            boolean vitoria = modelo.isJogoVencido();
            int dias = vitoria && modelo.isEventoFinalAtivo() ? 7 : modelo.getJogador().getDiasSobrevividos();
            exibirTelaGameOver(vitoria, dias);
        }
    }

    private void exibirTelaGameOver(boolean vitoria, int dias) {
        TelaGameOver telaGameOver = new TelaGameOver(telaPrincipal, vitoria, dias);
        telaGameOver.setLocationRelativeTo(telaPrincipal);
        telaGameOver.setVisible(true);
    }

    private void mostrarMensagem(String mensagem) {
        JOptionPane.showMessageDialog(telaPrincipal, mensagem);
    }

    // NOVO: Método para mostrar diálogo do confronto final
    private void mostrarDialogoConfrontoFinal(String mensagem) {
        if (modelo.getConfrontoFinal().isConfrontoTerminado()) {
            // Se o confronto terminou via diálogo, finalizar o jogo
            br.com.pedrodamasceno.model.ConfrontoFinal.FinalType tipoFinal = modelo.getConfrontoFinal().getTipoFinal();
            finalizarJogoComConfrontoFinal(tipoFinal);
            return;
        }

        List<String> opcoes = modelo.getConfrontoFinal().getEscolhasAtuais();
        
        // CORREÇÃO: Sempre processar as opções, independente se já estão na mensagem
        StringBuilder mensagemCompleta = new StringBuilder(mensagem);
        
        // Verificar se as opções estão listadas explicitamente na mensagem
        boolean opcoesListadasNaMensagem = false;
        for (String opcao : opcoes) {
            if (mensagem.contains(opcao)) {
                opcoesListadasNaMensagem = true;
                break;
            }
        }
        
        // Se as opções não estão listadas, adicionar
        if (!opcoes.isEmpty() && !opcoesListadasNaMensagem) {
            if (!mensagem.contains("O que você faz?")) {
                mensagemCompleta.append("\n\nO que você faz?");
            }
            for (String opcao : opcoes) {
                mensagemCompleta.append("\n").append(opcao);
            }
        }
        
        // CORREÇÃO: Sempre criar botões baseados no número real de opções
        String[] botoes;
        if (opcoes.isEmpty()) {
            // Se não há opções, é só mensagem informativa
            botoes = new String[]{"OK"};
        } else {
            botoes = new String[opcoes.size()];
            for (int i = 0; i < opcoes.size(); i++) {
                botoes[i] = "Opção " + (i + 1);
            }
        }

        int escolha = JOptionPane.showOptionDialog(
            telaPrincipal,
            mensagemCompleta.toString(),
            "Confronto Final - Decisão Moral",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            botoes,
            botoes[0]
        );

        if (escolha >= 0) {
            if (opcoes.isEmpty()) {
                // Se só tinha o botão OK, iniciar combate ou continuar
                if (mensagem.contains("combate") || mensagem.contains("batalha") || mensagem.contains("Lutar")) {
                    iniciarCombateBossFinalComDialogo();
                }
            } else if (escolha < opcoes.size()) {
                processarEscolhaConfrontoFinal(escolha + 1);
            }
        }
    }

    // NOVO: Processa a escolha do jogador no confronto final
    private void processarEscolhaConfrontoFinal(int escolha) {
        String resultado = modelo.getConfrontoFinal().processarEscolha(escolha);
        
        if (modelo.getConfrontoFinal().isConfrontoTerminado()) {
            // Final alcançado via diálogo
            br.com.pedrodamasceno.model.ConfrontoFinal.FinalType tipoFinal = modelo.getConfrontoFinal().getTipoFinal();
            finalizarJogoComConfrontoFinal(tipoFinal);
        } else if (resultado.contains("PREPARE-SE PARA O COMBATE FINAL!")) {
            // Se a escolha levou a combate, iniciar batalha
            JOptionPane.showMessageDialog(telaPrincipal, resultado, "Confronto Inevitável", JOptionPane.WARNING_MESSAGE);
            iniciarCombateBossFinalComDialogo();
        } else {
            // Continuar diálogo
            mostrarDialogoConfrontoFinal(resultado);
        }
    }

    // NOVO: Iniciar combate do boss final com sistema de diálogo integrado
    private void iniciarCombateBossFinalComDialogo() {
        br.com.pedrodamasceno.model.zumbis.BossFinal boss = new br.com.pedrodamasceno.model.zumbis.BossFinal();
        modelo.setInimigosAtuais(java.util.List.of(boss));
        modelo.setEmCombate(true);

        // CORREÇÃO: Removido reset das habilidades - deve resetar apenas ao dormir
        TelaCombate telaCombate = new TelaCombate(telaPrincipal, modelo, modelo.getInimigosAtuais());
        telaCombate.setLocationRelativeTo(telaPrincipal);
        telaCombate.setVisible(true);
    }

    // NOVO: Finalizar jogo com base no tipo de final do confronto
    private void finalizarJogoComConfrontoFinal(br.com.pedrodamasceno.model.ConfrontoFinal.FinalType tipoFinal) {
        modelo.setJogoTerminado(true);
        String mensagemFinal = gerarMensagemFinalPorTipo(tipoFinal);
        
        logsDiarios.add(mensagemFinal);
        
        // Determinar se é vitória ou derrota - SACRIFICIO_PROPRIO é GAME OVER (jogador morre)
        boolean vitoria = false; // No sistema simplificado, o único final via diálogo é game over
        
        // Não marcar como jogo vencido, pois é sempre game over quando termina via diálogo
        
        // CORREÇÃO: Exibir TelaGameOver diretamente com a mensagem final
        TelaGameOver telaGameOver = new TelaGameOver(telaPrincipal, vitoria, 7, mensagemFinal);
        telaGameOver.setLocationRelativeTo(telaPrincipal);
        telaGameOver.setVisible(true);
    }

    // NOVO: Gerar mensagem final baseada no tipo de confronto
    private String gerarMensagemFinalPorTipo(br.com.pedrodamasceno.model.ConfrontoFinal.FinalType tipoFinal) {
        return switch (tipoFinal) {
            case SACRIFICIO_PROPRIO -> "FINAL HEROICO: Você se sacrificou para salvar Ana e Sofia. Uma mãe e filha vivem por sua causa, mas você pagou o preço final. Seu ato de bondade será lembrado para sempre.";
            default -> "FINAL DESCONHECIDO: O destino tomou um rumo inesperado...";
        };
    }

    private void atualizarTela() {
        if (telaPrincipal != null) {
            telaPrincipal.atualizarInterface();
        }
    }

    private void desequiparItensAposCombate() {
        Personagem jogador = modelo.getJogador();
        // Desequipar arma, se houver
        if (jogador.getArmaEquipada() != null) {
            jogador.getInventario().adicionarItem(jogador.getArmaEquipada());
            jogador.desequiparArma();
        }
        // Desequipar armadura, se houver
        if (jogador.getArmaduraEquipada() != null) {
            jogador.getArmaduraEquipada().remover(jogador); // Remover efeitos de buff
            jogador.getInventario().adicionarItem(jogador.getArmaduraEquipada());
            jogador.desequiparArmadura();
        }
    }
    
    // TEMPORÁRIO: Método para pular direto para o confronto final (apenas para testes)
    public void pularParaConfrontoFinal() {
        try {
            // CORREÇÃO: Configurar Dia 6 - Noite com 3 explorações já feitas
            java.lang.reflect.Field campoDay = modelo.getClass().getDeclaredField("diaAtual");
            campoDay.setAccessible(true);
            campoDay.set(modelo, 6);
            
            java.lang.reflect.Field campoPeriodo = modelo.getClass().getDeclaredField("periodoAtual");
            campoPeriodo.setAccessible(true);
            campoPeriodo.set(modelo, br.com.pedrodamasceno.model.PeriodoDia.NOITE);
            
            // CORREÇÃO: Definir 3 explorações para forçar o jogador a dormir
            java.lang.reflect.Field campoExploracoes = modelo.getClass().getDeclaredField("exploracoesDiaAtual");
            campoExploracoes.setAccessible(true);
            campoExploracoes.set(modelo, 3); // 3 explorações = deve dormir
            
            java.lang.reflect.Field campoDormiu = modelo.getClass().getDeclaredField("dormiu");
            campoDormiu.setAccessible(true);
            campoDormiu.set(modelo, false);
            
        } catch (Exception e) {
            System.out.println("Erro ao configurar estado do teste: " + e.getMessage());
        }
        
        // Dar alguns itens básicos para o teste (com parâmetros corretos)
        modelo.getJogador().getInventario().adicionarItem(
            new br.com.pedrodamasceno.model.itens.Medicamento("Medicamento de Cura", "Cura básica para teste", 30)
        );
        modelo.getJogador().getInventario().adicionarItem(
            new br.com.pedrodamasceno.model.itens.Arma("Revólver", "Arma para teste", 100, 25, 10)
        );
        
        // Restaurar vida para teste
        modelo.getJogador().setSaude(modelo.getJogador().getSaudeMaxima());
        
        JOptionPane.showMessageDialog(telaPrincipal, 
            "🧟‍♂️ MODO TESTE ATIVADO! 🧟‍♂️\n\n" +
            "Você foi transportado para o Dia 6 - Noite\n" +
            "✅ 3 explorações já feitas (pode dormir)\n" +
            "Vida restaurada e itens básicos adicionados.\n\n" +
            "Clique em DORMIR para ir ao Dia 7 (Confronto Final).",
            "Teste - Confronto Final", 
            JOptionPane.INFORMATION_MESSAGE);
            
        atualizarTela();
    }
}