package br.com.pedrodamasceno.model.zumbis;

import br.com.pedrodamasceno.model.personagens.Personagem;
import br.com.pedrodamasceno.model.status.StatusEffect;
import br.com.pedrodamasceno.model.status.EfeitoStatus;

/**
 * Zumbi Infectado: tem chance de infectar com VENENO (dano por turno).
 */
public class ZumbiInfectado extends Zumbi {
    public ZumbiInfectado() {
        super("Zumbi Infectado", 35, 12, 4, "Zumbi com vírus mutado; pode envenenar a vítima.");
    }

    @Override
    protected int getChanceEspecial() { return 30; }

    @Override
    protected String habilidadeEspecial(Personagem alvo) {
        // aplica veneno: 3 turnos de 4 de dano
        StatusEffect s = new StatusEffect(EfeitoStatus.VENENO, 3, 4);
        alvo.adicionarEfeito(s);
        return "Mordida Infectante: aplicou VENENO (3 turnos, 4 dano/turno).";
    }
}