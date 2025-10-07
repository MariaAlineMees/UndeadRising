package br.com.pedrodamasceno.model.zumbis;

import br.com.pedrodamasceno.model.personagens.Personagem;

/**
 * Zumbi simples. Habilidade: gemido que aumenta dano próprio (fúria temporária).
 */
public class ZumbiComum extends Zumbi {
    public ZumbiComum() {
        super("Zumbi Comum", 30, 10, 3, "Zumbi lento e fraco, perigoso em grupo.");
    }

    @Override
    protected int getChanceEspecial() { return 12; }

    @Override
    protected String habilidadeEspecial(Personagem alvo) {
        // aumenta dano próprio (efeito simples, permanente para esta instância)
        this.dano += 2;
        return "Gemido: fúria aumentada (+2 dano para próximos ataques).";
    }
}