package br.com.pedrodamasceno.model.zumbis;

import br.com.pedrodamasceno.model.personagens.Personagem;
import br.com.pedrodamasceno.model.status.EfeitoStatus;
import br.com.pedrodamasceno.model.status.StatusEffect;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * Base para zumbis. Método atacar(alvo) faz o ataque e tenta acionar habilidade especial
 * (probabilidade definida por getChanceEspecial()).
 */
public abstract class Zumbi {
    protected String nome;
    protected int saude;
    protected int dano;
    protected int velocidade;
    protected String descricao;
    protected final List<StatusEffect> efeitos = new ArrayList<>();
    protected final Random rand = new Random();

    public Zumbi(String nome, int saude, int dano, int velocidade, String descricao) {
        this.nome = nome;
        this.saude = saude;
        this.dano = dano;
        this.velocidade = velocidade;
        this.descricao = descricao;
    }

    /**
     * Implementar habilidade especial. Deve aplicar efeitos no alvo (se for o caso)
     * e retornar uma mensagem descritiva (ou null se não houve especial).
     */
    protected abstract String habilidadeEspecial(Personagem alvo);

    /**
     * Chance (0-100) de a habilidade especial disparar no ataque.
     * Subclasses podem sobrescrever para variar a probabilidade.
     */
    protected int getChanceEspecial() { return 20; }

    /**
     * Realiza o ataque sobre o alvo, aplicando dano e possivelmente a habilidade especial.
     * Retorna a mensagem descritiva do ataque (para logs/UI).
     */
    public String atacar(Personagem alvo) {
        StringBuilder sb = new StringBuilder();
        
        // Verificar se alvo tem esquiva garantida ANTES de qualquer coisa
        boolean esquivou = alvo.getBuff(br.com.pedrodamasceno.model.status.EfeitoStatus.ESQUIVA_GARANTIDA) > 0;
        
        if (esquivou) {
            sb.append(nome).append(" tentou atacar, mas ").append(alvo.getNome()).append(" esquivou com FURTIVIDADE!");
            alvo.receberDano(0); // Isso vai remover o efeito
            return sb.toString();
        }

        // checar se habilidade especial ativa
        boolean especial = rand.nextInt(100) < getChanceEspecial();
        if (especial) {
            String msgEspecial = habilidadeEspecial(alvo);
            if (msgEspecial != null && !msgEspecial.isEmpty()) {
                sb.append(nome).append(" usou habilidade especial: ").append(msgEspecial).append("\n");
            }
        }

        // aplicar dano básico
        int danoFinal = calcularDano();
        int saudeAntes = alvo.getSaude();
        boolean tinhaSegundaChance = alvo.getBuff(br.com.pedrodamasceno.model.status.EfeitoStatus.SEGUNDA_CHANCE) > 0;
        
        alvo.receberDano(danoFinal);
        
        int saudeDepois = alvo.getSaude();
        
        // Verificar se segunda chance foi ativada
        if (tinhaSegundaChance && saudeAntes - danoFinal <= 0 && saudeDepois == 1) {
            sb.append(nome).append(" desferiu um golpe fatal, mas ").append(alvo.getNome())
              .append(" usou SEGUNDA CHANCE e sobreviveu com 1 HP!");
        } else {
            int danoRealizado = saudeAntes - saudeDepois;
            sb.append(nome).append(" atacou e causou ").append(danoRealizado).append(" de dano.");
        }

        return sb.toString();
    }

    /**
     * Calcula o dano considerando buffs e resistências
     */
    protected int calcularDano() {
        int danoFinal = dano + getBuff(EfeitoStatus.BUFF_FORCA);
        return Math.max(0, danoFinal);
    }

    // Métodos para sistema de efeitos/buffs
    public int getBuff(EfeitoStatus tipo) {
        return efeitos.stream()
                .filter(e -> e.getTipo() == tipo)
                .mapToInt(StatusEffect::getValor)
                .sum();
    }

    public int getBuffResistencia() {
        return getBuff(EfeitoStatus.RESISTENCIA);
    }

    public boolean temEfeito(EfeitoStatus tipo) {
        return efeitos.stream().anyMatch(e -> e.getTipo() == tipo);
    }

    public void adicionarEfeito(StatusEffect e) {
        if (e != null) efeitos.add(e);
    }

    public void aplicarEfeitosInicioTurno() {
        Iterator<StatusEffect> it = efeitos.iterator();
        while (it.hasNext()) {
            StatusEffect eff = it.next();
            eff.tick();
            if (eff.expirou()) {
                it.remove();
            }
        }
    }

    public void aplicarEfeitosFimTurno() {
        // Efeitos que acontecem no fim do turno (se necessário)
    }

    // Método para curar o zumbi (usado pelo boss final)
    public void curar(int quantidade) {
        this.saude = Math.min(this.saude + quantidade, 300); // Máximo 300 HP para boss
    }

    // getters/setters
    public String getNome() { return nome; }
    public int getSaude() { return saude; }
    public void receberDano(int d) {
        saude = Math.max(0, saude - d);
        System.out.println(nome + " recebeu " + d + " de dano. HP: " + saude);
    }
    
    // NOVO: Método para receber dano com possível análise de fraqueza
    public void receberDano(int d, Personagem atacante) {
        int danoFinal = d;
        
        // Verificar se o atacante tem IGNORAR_DEFESA ativo (usado como bônus de dano fixo)
        if (atacante != null && atacante.getBuff(EfeitoStatus.IGNORAR_DEFESA) > 0) {
            // CORREÇÃO: Aplicar dano adicional fixo
            int bonusFixo = atacante.getBuff(EfeitoStatus.IGNORAR_DEFESA);
            danoFinal = danoFinal + bonusFixo;
            
            System.out.println(nome + " - ANÁLISE DE FRAQUEZA ATIVA! Dano original: " + d + 
                             ", Bônus fixo: +" + bonusFixo + ", Dano total: " + danoFinal);
            
            // Remover efeito após uso
            atacante.removerEfeito(EfeitoStatus.IGNORAR_DEFESA);
        } 
        
        // Aplicar resistência normal depois do cálculo de bônus
        danoFinal = Math.max(0, danoFinal - getBuffResistencia());
        
        saude = Math.max(0, saude - danoFinal);
        System.out.println(nome + " recebeu " + danoFinal + " de dano. HP: " + saude);
    }
    public boolean estaVivo() { return saude > 0; }
    public int getDano() { return dano; }
    public void setDano(int dano) { this.dano = dano; }
    public int getVelocidade() { return velocidade; }
    public String getDescricao() { return descricao; }
    public List<StatusEffect> getEfeitos() { return new ArrayList<>(efeitos); }
}