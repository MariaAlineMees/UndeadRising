package br.com.pedrodamasceno.model.zumbis;

import br.com.pedrodamasceno.model.personagens.Personagem;

/**
 * Zumbi Tanque: muito HP e ataques pesados (pode esmagar causando dano extra).
 */
public class ZumbiTanque extends Zumbi {
    public ZumbiTanque() {
        super("Zumbi Tanque", 70, 16, 2, "Grande e resistente; golpe forte que pode atordoar.");
    }

    @Override
    protected int getChanceEspecial() { return 20; }

    @Override
    protected String habilidadeEspecial(Personagem alvo) {
        // Esmagamento: causa dano extra imediato ao ser chamado (aplicado no SistemaCombate se preferir)
        int extra = 6;
        alvo.receberDano(extra);
        return "Esmagamento Brutál: dano extra de " + extra + " aplicado!";
    }
}