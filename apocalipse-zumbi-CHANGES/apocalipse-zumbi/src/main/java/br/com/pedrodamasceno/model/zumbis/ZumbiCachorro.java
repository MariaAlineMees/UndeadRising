package br.com.pedrodamasceno.model.zumbis;

import br.com.pedrodamasceno.model.personagens.Personagem;
import br.com.pedrodamasceno.model.status.StatusEffect;
import br.com.pedrodamasceno.model.status.EfeitoStatus;

/**
 * Zumbi Cachorro: ágil, pode causar sangramento.
 */
public class ZumbiCachorro extends Zumbi {
    public ZumbiCachorro() {
        super("Zumbi Cachorro", 28, 9, 8, "Ágil e feroz, ataca em investidas.");
    }

    @Override
    protected int getChanceEspecial() { return 30; }

    @Override
    protected String habilidadeEspecial(Personagem alvo) {
        // aplica sangramento: 3 turns com 3 de dano por turno
        StatusEffect s = new StatusEffect(EfeitoStatus.SANGRAMENTO, 3, 3);
        alvo.adicionarEfeito(s);
        return "Investida sangrenta: aplicou SANGRAMENTO (3 turnos, 3 dano/turno).";
    }
}