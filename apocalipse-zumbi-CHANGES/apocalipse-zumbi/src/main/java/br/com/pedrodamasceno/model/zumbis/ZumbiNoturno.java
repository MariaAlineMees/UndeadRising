package br.com.pedrodamasceno.model.zumbis;

import br.com.pedrodamasceno.model.personagens.Personagem;

/**
 * Zumbi Noturno: perigoso; chance de atacar duas vezes (ou causar ataque adicional).
 */
public class ZumbiNoturno extends Zumbi {
    public ZumbiNoturno() {
        super("Zumbi Noturno", 45, 13, 6, "Mais perigoso durante a noite; ataques rápidos e precisos.");
    }

    @Override
    protected int getChanceEspecial() { return 28; }

    @Override
    protected String habilidadeEspecial(Personagem alvo) {
        // Ataque Duplo: aplica um segundo golpe imediato
        int extra = this.dano;
        alvo.receberDano(extra);
        return "Ataque Duplo: acertou um segundo golpe extra de " + extra + "!";
    }
}