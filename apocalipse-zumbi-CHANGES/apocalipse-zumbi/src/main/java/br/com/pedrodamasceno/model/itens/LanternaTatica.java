package br.com.pedrodamasceno.model.itens;

import br.com.pedrodamasceno.model.personagens.Personagem;
import br.com.pedrodamasceno.model.status.EfeitoStatus;
import br.com.pedrodamasceno.model.status.StatusEffect;

public class LanternaTatica extends Item {

    private final int buffInteligencia;
    private final int duracaoBuff;

    public LanternaTatica(String nome, String descricao, int valor, int buffInteligencia, int duracaoBuff) {
        super(nome, descricao, TipoItem.UTILIDADE, valor); // TipoItem.UTILIDADE
        this.buffInteligencia = buffInteligencia;
        this.duracaoBuff = duracaoBuff;
    }

    public String usar(Personagem p) {
        p.adicionarEfeito(new StatusEffect(EfeitoStatus.BUFF_INTELIGENCIA, duracaoBuff, buffInteligencia));
        System.out.println(p.getNome() + " usou " + getNome() + " e ganhou +" + buffInteligencia + " de inteligência por " + duracaoBuff + " turnos!");
        // Não remove o item, apenas aplica o buff
        return "Você usou a " + nome + " e ganhou bônus de inteligência!";
    }
}
