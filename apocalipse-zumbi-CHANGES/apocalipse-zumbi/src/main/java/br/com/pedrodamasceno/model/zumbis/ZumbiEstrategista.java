package br.com.pedrodamasceno.model.zumbis;

import br.com.pedrodamasceno.model.personagens.Personagem;
import br.com.pedrodamasceno.model.status.StatusEffect;
import br.com.pedrodamasceno.model.status.EfeitoStatus;

/**
 * Zumbi Estrategista: pode aplicar debuff (CANSAÇO) para reduzir eficácia do jogador.
 */
public class ZumbiEstrategista extends Zumbi {
    public ZumbiEstrategista() {
        super("Zumbi Estrategista", 40, 10, 5, "Zumbi que usa tática, podendo desorganizar o inimigo.");
    }

    @Override
    protected int getChanceEspecial() { return 22; }

    @Override
    protected String habilidadeEspecial(Personagem alvo) {
        // aplica CANSAÇO: 2 turnos, poder 1 (o SistemaCombate deve interpretar)
        StatusEffect s = new StatusEffect(EfeitoStatus.CANSACO, 2, 1);
        alvo.adicionarEfeito(s);
        return "Golpe Tático: aplicou CANSAÇO (reduz eficácia por 2 turnos).";
    }
}