package br.com.pedrodamasceno.model.zumbis;

import br.com.pedrodamasceno.model.personagens.Personagem;
import br.com.pedrodamasceno.model.status.EfeitoStatus;
import br.com.pedrodamasceno.model.status.StatusEffect;

/**
 * Zumbi Tóxico - Pode causar veneno, reduz vida 5 pontos a cada dia
 */
public class ZumbiToxico extends Zumbi {
    
    public ZumbiToxico() {
        super("Zumbi Tóxico", 30, 6, 25, "Um zumbi que exala toxinas venenosas. Causa dano contínuo!");
    }
    
    @Override
    protected String habilidadeEspecial(Personagem alvo) {
        // Aplicar efeito de veneno
        alvo.adicionarEfeito(new StatusEffect(EfeitoStatus.VENENO, 3, 2));
        return "Aplicou veneno! Você perderá 5 HP por dia por 3 dias!";
    }
    
    @Override
    protected int getChanceEspecial() {
        return 60; // 60% de chance de usar habilidade especial
    }
}
