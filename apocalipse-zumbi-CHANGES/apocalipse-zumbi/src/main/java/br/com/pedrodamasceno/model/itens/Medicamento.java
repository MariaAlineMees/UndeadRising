package br.com.pedrodamasceno.model.itens;

import br.com.pedrodamasceno.model.personagens.Personagem;

public class Medicamento extends Item {
    private final int cura;

    public Medicamento(String nome, String descricao, int cura) {
        super(nome, descricao, TipoItem.MEDICAMENTO, cura);
        this.cura = cura;
    }

    public String usar(Personagem p) {
        p.curar(cura);
        // usar medicamento recupera moral ligeiramente
        p.setMoral(p.getMoral() + 2);
        return "Você usou " + nome + " e recuperou " + cura + " de vida!";
    }
}