package br.com.pedrodamasceno.model.status;

/**
 * Representa um efeito ativo em personagens ou zumbis
 * duração em turnos e valor do efeito
 */
public class StatusEffect {
    private EfeitoStatus tipo;
    private int duracao; // turnos restantes
    private int valor;   // intensidade do efeito

    public StatusEffect(EfeitoStatus tipo, int duracao, int valor) {
        this.tipo = tipo;
        this.duracao = duracao;
        this.valor = valor;
    }

    public EfeitoStatus getTipo() { return tipo; }
    public int getDuracao() { return duracao; }
    public int getValor() { return valor; }

    public void tick() {
        duracao--;
    }

    public boolean expirou() {
        return duracao <= 0;
    }
}