package br.com.pedrodamasceno.model.zumbis;

import br.com.pedrodamasceno.model.personagens.Personagem;
import br.com.pedrodamasceno.model.status.StatusEffect;
import br.com.pedrodamasceno.model.status.EfeitoStatus;

/**
 * Zumbi Alfa: chefe com regeneração e debuffs potentes.
 */
public class ZumbiAlfa extends Zumbi {
    public ZumbiAlfa() {
        super("Zumbi Alfa", 120, 20, 7, "Líder da horda, extremamente perigoso.");
    }

    @Override
    protected int getChanceEspecial() { return 30; }

    @Override
    protected String habilidadeEspecial(Personagem alvo) {
        // Rugido: regen e aplica CANSAÇO/medo
        this.saude += 15;
        StatusEffect s = new StatusEffect(EfeitoStatus.CANSACO, 3, 2);
        alvo.adicionarEfeito(s);
        return "Rugido Assustador: alfa regenerou +15 HP e aplicou CANSAÇO ao inimigo!";
    }
}