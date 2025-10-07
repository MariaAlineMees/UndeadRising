package br.com.pedrodamasceno.model.zumbis;

import br.com.pedrodamasceno.model.personagens.Personagem;
import br.com.pedrodamasceno.model.status.EfeitoStatus;

/**
 * Zumbi Corredor - Rápido e impossível de fugir
 */
public class ZumbiCorredor extends Zumbi {
    
    public ZumbiCorredor() {
        super("Zumbi Corredor", 25, 8, 35, "Um zumbi extremamente rápido e ágil. Impossível de fugir dele!");
    }
    
    @Override
    protected String habilidadeEspecial(Personagem alvo) {
        // Zumbi corredor sempre ataca primeiro
        return "Ataque rápido! Impossível de esquivar!";
    }
    
    @Override
    protected int getChanceEspecial() {
        return 40; // 40% de chance de usar habilidade especial
    }
}
