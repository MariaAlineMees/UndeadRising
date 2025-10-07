package br.com.pedrodamasceno.model.combate;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import br.com.pedrodamasceno.model.personagens.Personagem;
import br.com.pedrodamasceno.model.status.EfeitoStatus;
import br.com.pedrodamasceno.model.zumbis.Zumbi;

/**
 * Sistema de combate por turnos.
 */
public class SistemaCombate {
    private final Personagem jogador;
    private final List<Zumbi> inimigos;
    private final Random random = new Random();
    private Zumbi alvoAtual;

    public SistemaCombate(Personagem jogador, List<Zumbi> inimigos) {
        this.jogador = jogador;
        this.inimigos = new ArrayList<>(inimigos);
        encontrarProximoAlvoVivo();
    }

    private void encontrarProximoAlvoVivo() {
        alvoAtual = inimigos.stream()
                .filter(Zumbi::estaVivo)
                .findFirst()
                .orElse(null);
    }

    public boolean combateTerminado() {
        return inimigos.stream().noneMatch(Zumbi::estaVivo);
    }

    public boolean jogadorDerrotado() {
        return !jogador.estaVivo();
    }

    public List<String> inimigosAtacam() {
        List<String> logs = new ArrayList<>();
        
        for (Zumbi inimigo : inimigos) {
            if (inimigo.estaVivo()) {
                // NOVO: Verificar se o inimigo está atordoado
                if (inimigo.getBuff(EfeitoStatus.ATORDOAMENTO) > 0) {
                    logs.add(inimigo.getNome() + " está atordoado e não pode atacar!");
                    continue; // Pula para o próximo inimigo
                }
                
                String log = inimigo.atacar(jogador);
                logs.add(log);
                
                // NOVO: Se for o boss final, verificar mensagens em pontos específicos de vida
                if (inimigo instanceof br.com.pedrodamasceno.model.zumbis.BossFinal bossFinal) {
                    String mensagemVida = bossFinal.verificarMensagemVida();
                    if (!mensagemVida.isEmpty()) {
                        logs.add(mensagemVida);
                    }
                }
                
                if (!jogador.estaVivo()) {
                    logs.add(">>> JOGADOR DERROTADO! <<<");
                    break;
                }
            }
        }
        
        // CORREÇÃO: Aplicar efeitos do fim do turno DEPOIS dos ataques para manter atordoamento
        for (Zumbi inimigo : inimigos) {
            if (inimigo.estaVivo()) {
                inimigo.aplicarEfeitosInicioTurno(); // Remove efeitos expirados como atordoamento
            }
        }
        
        return logs;
    }

    private int calcularDanoJogador() {
        // CORREÇÃO: Ataque básico NÃO deve consumir munição automaticamente
        int danoBase = jogador.getForca(); // Usar força base
        
        // Adicionar dano da arma SE equipada e utilizável (sem consumir)
        if (jogador.temArmaEquipada() && jogador.podeUsarArmaEquipada()) {
            danoBase = jogador.getArmaEquipada().getDano(); // Usar dano da arma sem consumir
        }
        
        // Adicionar bônus de buffs
        danoBase += jogador.getBuff(EfeitoStatus.FORCA);

        // Variação aleatória
        int variacao = random.nextInt(6); // 0-5
        danoBase += variacao;

        // Aplicar crítico (verificar se tem crítico garantido)
        boolean criticoGarantido = jogador.getBuff(EfeitoStatus.CRITICO_GARANTIDO) > 0;
        System.out.println("DEBUG: Crítico garantido ativo? " + criticoGarantido); // DEBUG
        if (criticoGarantido || random.nextInt(100) < 10) { // Crítico garantido ou 10% chance
            System.out.println("DEBUG: CRÍTICO APLICADO! Dano antes: " + danoBase + ", Dano depois: " + (danoBase * 3)); // DEBUG
            danoBase *= 3;
            // Remover efeito de crítico garantido após uso
            if (criticoGarantido) {
                jogador.removerEfeito(EfeitoStatus.CRITICO_GARANTIDO);
                System.out.println("DEBUG: Efeito de crítico garantido removido"); // DEBUG
            }
        }

        return Math.max(0, danoBase);
    }

    private int calcularDanoArmaEspecial() {
        // CORREÇÃO: Ataque especial DEVE consumir munição/durabilidade
        int danoBase = jogador.calcularDanoArma(); // Este DEVE consumir recursos
        int danoEspecial = (int)(danoBase * 1.5); // 150% do dano normal
        
        // Adicionar bônus de buffs
        danoEspecial += jogador.getBuff(EfeitoStatus.FORCA);
        
        // Variação aleatória maior
        int variacao = random.nextInt(8); // 0-7 (maior variação)
        danoEspecial += variacao;
        
        // Aplicar crítico (verificar se tem crítico garantido)
        boolean criticoGarantido = jogador.getBuff(EfeitoStatus.CRITICO_GARANTIDO) > 0;
        System.out.println("DEBUG ESPECIAL: Crítico garantido ativo? " + criticoGarantido); // DEBUG
        if (criticoGarantido || random.nextInt(100) < 15) { // Crítico garantido ou 15% chance
            System.out.println("DEBUG ESPECIAL: CRÍTICO APLICADO! Dano antes: " + danoEspecial + ", Dano depois: " + (danoEspecial * 3)); // DEBUG
            danoEspecial *= 3;
            // Remover efeito de crítico garantido após uso
            if (criticoGarantido) {
                jogador.removerEfeito(EfeitoStatus.CRITICO_GARANTIDO);
                System.out.println("DEBUG ESPECIAL: Efeito de crítico garantido removido"); // DEBUG
            }
        }
        
        return Math.max(0, danoEspecial);
    }

    public String jogadorAtacaComDetalhes(int tipoAtaque) {
        StringBuilder resultado = new StringBuilder();

        if (alvoAtual == null || !alvoAtual.estaVivo()) {
            encontrarProximoAlvoVivo();
            if (alvoAtual == null) {
                return "Não há alvos disponíveis!";
            }
        }

        // Mostrar informações do alvo antes do ataque
        resultado.append(">>> Alvo: ").append(alvoAtual.getNome())
                .append(" (HP: ").append(alvoAtual.getSaude()).append(") <<<\n");

        switch (tipoAtaque) {
            case 0 -> executarAtaqueBasico(resultado);
            case 1 -> executarHabilidade1(resultado);
            case 2 -> executarHabilidade2(resultado);
            case 3 -> executarArmaEspecial(resultado);
        }

        verificarMorteInimigo(resultado);
        return resultado.toString();
    }

    private void executarAtaqueBasico(StringBuilder resultado) {
        resultado.append(">>> ATAQUE BÁSICO <<<\n");
        boolean criticoGarantidoAntes = jogador.getBuff(EfeitoStatus.CRITICO_GARANTIDO) > 0;
        boolean analiseAtiva = jogador.getBuff(EfeitoStatus.IGNORAR_DEFESA) > 0;
        
        int dano = calcularDanoJogador();
        alvoAtual.receberDano(dano, jogador);
        
        if (analiseAtiva) {
            resultado.append("Você acerta o ponto fraco de ").append(alvoAtual.getNome())
                    .append(" causando ").append(dano).append(" de dano (+20 bônus de Análise de Fraqueza)!");
        } else {
            resultado.append("Você ataca ").append(alvoAtual.getNome())
                    .append(" causando ").append(dano).append(" de dano!");
        }
        
        if (criticoGarantidoAntes) {
            resultado.append(" (CRÍTICO GARANTIDO!)");
        }
    }

    private void executarArmaEspecial(StringBuilder resultado) {
        if (jogador.getArmaEquipada() == null) {
            resultado.append(">>> ERRO <<<\n");
            resultado.append("Você não tem uma arma equipada!");
            return;
        }
        
        resultado.append(">>> ATAQUE COM ARMA <<<\n");
        boolean criticoGarantidoAntes = jogador.getBuff(EfeitoStatus.CRITICO_GARANTIDO) > 0;
        boolean analiseAtiva = jogador.getBuff(EfeitoStatus.IGNORAR_DEFESA) > 0;
        
        int dano = calcularDanoArmaEspecial();
        alvoAtual.receberDano(dano, jogador);
        
        if (analiseAtiva) {
            resultado.append("Você mira no ponto fraco com ").append(jogador.getArmaEquipada().getNome())
                    .append(" causando ").append(dano).append(" de dano (+20 bônus de Análise de Fraqueza)!");
        } else {
            resultado.append("Você usa ").append(jogador.getArmaEquipada().getNome())
                    .append(" causando ").append(dano).append(" de dano!");
        }
        
        if (criticoGarantidoAntes) {
            resultado.append(" (CRÍTICO GARANTIDO!)");
        }
    }

    private void executarHabilidade1(StringBuilder resultado) {
        resultado.append(">>> ").append(jogador.getDescricaoHabilidade1().split(":")[0].toUpperCase()).append(" <<<\n");
        // Verificar se tem cargas antes de usar
        if (jogador.getCargasHabilidade1() <= 0) {
            resultado.append("Você não tem cargas suficientes para usar esta habilidade!");
            return;
        }
        
        // NOVO: Usar a habilidade para decrementar as cargas e aplicar efeitos
        jogador.usarHabilidadeEspecial1();
        
        // Processar habilidades especiais que não causam dano direto
        String nomeHabilidade = jogador.getDescricaoHabilidade1().split(":")[0].toUpperCase();
        
        if (nomeHabilidade.contains("FURTIVIDADE")) {
            resultado.append("Você se esconde nas sombras... Próximo ataque inimigo será esquivado!");
        } else if (nomeHabilidade.contains("CURA")) {
            resultado.append("Você se trata rapidamente, recuperando saúde!");
        } else if (nomeHabilidade.contains("FÚRIA") || nomeHabilidade.contains("FURIA")) {
            resultado.append("Você usa Fúria! Sua força aumenta e sua defesa diminui!");
        } else if (nomeHabilidade.contains("POSTURA")) {
            resultado.append("Você assume postura defensiva! +10 resistência por 3 turnos!");
        } else if (nomeHabilidade.contains("PLANO")) {
            resultado.append("Você planeja sua fuga... Próximo ataque inimigo será esquivado!");
        } else if (nomeHabilidade.contains("ARMADILHA")) {
            // Dano em área para todos os inimigos vivos
            int danoArea = 15;
            int inimigosAfetados = 0;
            for (Zumbi inimigo : inimigos) {
                if (inimigo.estaVivo()) {
                    inimigo.receberDano(danoArea, jogador); // CORREÇÃO: Usar novo método com atacante
                    // Aplicar atordoamento nos zumbis
                    inimigo.adicionarEfeito(new br.com.pedrodamasceno.model.status.StatusEffect(
                        br.com.pedrodamasceno.model.status.EfeitoStatus.ATORDOAMENTO, 1, 1));
                    inimigosAfetados++;
                }
            }
            resultado.append("Armadilha explosiva afeta ").append(inimigosAfetados).append(" inimigos! Dano: ").append(danoArea).append(" cada. Inimigos atordoados!");
        } else {
            // Habilidades que causam dano direto (apenas algumas classes)
            boolean analiseAtiva = jogador.getBuff(EfeitoStatus.IGNORAR_DEFESA) > 0;
            int dano = calcularDanoHabilidade(1);
            alvoAtual.receberDano(dano, jogador);
            
            if (analiseAtiva) {
                resultado.append("Você explora uma fraqueza causando ").append(dano).append(" de dano (+20 bônus de Análise de Fraqueza)!");
            } else {
                resultado.append("Dano: ").append(dano);
            }
        }
    }

    private void executarHabilidade2(StringBuilder resultado) {
        resultado.append(">>> ").append(jogador.getDescricaoHabilidade2().split(":")[0].toUpperCase()).append(" <<<\n");
        // Verificar se tem cargas antes de usar
        if (jogador.getCargasHabilidade2() <= 0) {
            resultado.append("Você não tem cargas suficientes para usar esta habilidade!");
            return;
        }
        
        // NOVO: Usar a habilidade para decrementar as cargas e aplicar efeitos
        jogador.usarHabilidadeEspecial2();
        
        // Processar habilidades especiais que não causam dano direto
        String nomeHabilidade = jogador.getDescricaoHabilidade2().split(":")[0].toUpperCase();
        
        if (nomeHabilidade.contains("SORTE") || nomeHabilidade.contains("INICIANTE")) {
            resultado.append("Você sente a sorte ao seu lado... Próximo ataque será crítico garantido!");
        } else if (nomeHabilidade.contains("DIAGNÓSTICO") || nomeHabilidade.contains("DIAGNOSTICO")) {
            resultado.append("Você remove todos os efeitos negativos do seu corpo e recupera 20% do seu HP!");
        } else if (nomeHabilidade.contains("SEGUNDA") || nomeHabilidade.contains("CHANCE")) {
            resultado.append("Você se prepara mentalmente... Sobreviverá ao próximo golpe fatal!");
        } else if (nomeHabilidade.contains("RESISTÊNCIA") || nomeHabilidade.contains("RESISTENCIA")) {
            resultado.append("Você se concentra e fortalece suas defesas! +3 resistência por 2 turnos e recupera saúde!");
        } else if (nomeHabilidade.contains("REPAROS") || nomeHabilidade.contains("RAPIDOS")) {
            resultado.append("Você fortalece suas defesas e se recupera!");
        } else if (nomeHabilidade.contains("ANÁLISE") || nomeHabilidade.contains("ANALISE") || nomeHabilidade.contains("FRAQUEZA")) {
            resultado.append("Você analisa as fraquezas do inimigo... Próximo ataque ganhará +20 de dano!");
        } else {
            // Habilidades que causam dano direto (apenas algumas classes)
            boolean analiseAtiva = jogador.getBuff(EfeitoStatus.IGNORAR_DEFESA) > 0;
            int dano = calcularDanoHabilidade(2);
            alvoAtual.receberDano(dano, jogador);
            
            if (analiseAtiva) {
                resultado.append("Você explora uma fraqueza causando ").append(dano).append(" de dano (+20 bônus de Análise de Fraqueza)!");
            } else {
                resultado.append("Dano: ").append(dano);
            }
        }
    }

    private int calcularDanoHabilidade(int numeroHabilidade) {
        // CORREÇÃO: Habilidades usam força do personagem, NÃO a arma equipada
        int danoBase = jogador.getForca();
        
        // Adicionar modificador da habilidade específica do personagem
        if (jogador instanceof br.com.pedrodamasceno.model.personagens.Sobrevivente sobrevivente) {
            danoBase = sobrevivente.getDanoHabilidade(numeroHabilidade);
        } else if (jogador instanceof br.com.pedrodamasceno.model.personagens.Lutador lutador) {
            danoBase = lutador.getDanoHabilidade(numeroHabilidade);
        } else if (jogador instanceof br.com.pedrodamasceno.model.personagens.Estrategista estrategista) {
            danoBase = estrategista.getDanoHabilidade(numeroHabilidade);
        } else if (jogador instanceof br.com.pedrodamasceno.model.personagens.Soldado soldado) {
            danoBase = soldado.getDanoHabilidade(numeroHabilidade);
        } else if (jogador instanceof br.com.pedrodamasceno.model.personagens.Medico medico) {
            danoBase = medico.getDanoHabilidade(numeroHabilidade);
        } else if (jogador instanceof br.com.pedrodamasceno.model.personagens.Engenheiro engenheiro) {
            danoBase = engenheiro.getDanoHabilidade(numeroHabilidade);
        }

        // Adicionar bônus de buffs
        danoBase += jogador.getBuff(EfeitoStatus.FORCA);

        // Variação aleatória
        int variacao = random.nextInt(10); // 0-9
        danoBase += variacao;

        return Math.max(0, danoBase);
    }

    private void verificarMorteInimigo(StringBuilder resultado) {
        if (alvoAtual != null) {
            // NOVO: Se for o boss final e ainda estiver vivo, verificar mensagens de vida
            if (alvoAtual instanceof br.com.pedrodamasceno.model.zumbis.BossFinal bossFinal && alvoAtual.estaVivo()) {
                String mensagemVida = bossFinal.verificarMensagemVida();
                if (!mensagemVida.isEmpty()) {
                    resultado.append(mensagemVida);
                }
            }
            
            if (!alvoAtual.estaVivo()) {
                // NOVO: Mensagem final do boss se for Ana Silva
                if (alvoAtual instanceof br.com.pedrodamasceno.model.zumbis.BossFinal) {
                    resultado.append("\n>>> ANA (DERROTADA): \"Sofia... mamãe... tentou...\" [Ana cai, segurando a foto da filha] <<<");
                } else {
                    resultado.append("\n>>> ").append(alvoAtual.getNome()).append(" foi derrotado! <<<");
                }
                encontrarProximoAlvoVivo();
            }
        }
    }

    public void ataquePersonagem(Personagem atacante, Zumbi alvo) {
        int dano = calcularDanoJogador();
        alvo.receberDano(dano);
    }

    // MÉTODOS LEGADOS (mantidos para compatibilidade)
    public List<Personagem> getPersonagens() {
        List<Personagem> personagens = new ArrayList<>();
        personagens.add(jogador);
        return personagens;
    }

    public List<Zumbi> getInimigos() {
        return inimigos;
    }

    public boolean todosCombatesTerminados() {
        List<Personagem> personagens = getPersonagens();
        List<Zumbi> zumbis = getInimigos();
        
        boolean todosMortos = personagens.stream().allMatch(p -> !p.estaVivo());
        boolean todosZumbisMortos = zumbis.stream().allMatch(z -> !z.estaVivo());
        return todosMortos || todosZumbisMortos;
    }

    public boolean jogadorAtaca(int tipoAtaque) {
        if (alvoAtual == null || !alvoAtual.estaVivo()) {
            encontrarProximoAlvoVivo();
            if (alvoAtual == null) return true;
        }

        switch (tipoAtaque) {
            case 0 -> ataquePersonagem(jogador, alvoAtual);
            case 1 -> {
                jogador.usarHabilidadeEspecial1();
                alvoAtual.receberDano(calcularDanoHabilidade(1));
            }
            case 2 -> {
                jogador.usarHabilidadeEspecial2();
                alvoAtual.receberDano(calcularDanoHabilidade(2));
            }
            case 3 -> {
                // Usar arma especial - causa mais dano
                if (jogador.getArmaEquipada() != null) {
                    alvoAtual.receberDano(calcularDanoArmaEspecial());
                } else {
                    return false; // Falha se não tem arma
                }
            }
        }

        return alvoAtual != null && !alvoAtual.estaVivo();
    }
}
