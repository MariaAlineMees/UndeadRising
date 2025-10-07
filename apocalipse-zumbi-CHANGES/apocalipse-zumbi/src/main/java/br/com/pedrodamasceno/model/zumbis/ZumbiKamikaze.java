package br.com.pedrodamasceno.model.zumbis;

import br.com.pedrodamasceno.model.personagens.Personagem;

/**
 * Zumbi Kamikaze - Pode explodir causando alto dano e finaliza combate
 */
public class ZumbiKamikaze extends Zumbi {
    
    public ZumbiKamikaze() {
        super("Zumbi Kamikaze", 20, 15, 40, "Um zumbi instável que pode explodir a qualquer momento!");
    }
    
    @Override
    protected String habilidadeEspecial(Personagem alvo) {
        // Zumbi kamikaze explode, causando muito dano
        int danoExplosao = 25;
        alvo.receberDano(danoExplosao);
        
        // O zumbi morre na explosão
        this.saude = 0;
        
        return "EXPLODIU! Causou " + danoExplosao + " de dano e morreu na explosão!";
    }
    
    @Override
    protected int getChanceEspecial() {
        return 30; // 30% de chance de explodir
    }
}
