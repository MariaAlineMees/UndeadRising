package br.com.pedrodamasceno.model.itens;

import br.com.pedrodamasceno.model.personagens.Personagem;
import br.com.pedrodamasceno.model.status.EfeitoStatus;
import br.com.pedrodamasceno.model.status.StatusEffect;

public class RacaoCombate extends Comida {

    private final int curaHP;
    private final int buffForca;
    private final int duracaoBuff;

    public RacaoCombate(String nome, String descricao, int moral, int curaHP, int buffForca, int duracaoBuff) {
        super(nome, descricao, moral);
        this.curaHP = curaHP;
        this.buffForca = buffForca;
        this.duracaoBuff = duracaoBuff;
    }

    @Override
    public void consumir(Personagem personagem) {
        super.consumir(personagem); // Aplica o bônus de moral da comida base
        personagem.curar(curaHP); // Cura uma quantidade de HP
        personagem.adicionarEfeito(new StatusEffect(EfeitoStatus.FORCA, duracaoBuff, buffForca)); // Aplica buff de força
        System.out.println(personagem.getNome() + " consumiu " + getNome() + ", recuperou " + curaHP + " HP e ganhou +" + buffForca + " de força por " + duracaoBuff + " turnos!");
    }
}
