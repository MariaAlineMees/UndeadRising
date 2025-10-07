package br.com.pedrodamasceno.model.itens;

import br.com.pedrodamasceno.model.personagens.Personagem;

public class Comida extends Item {

    public Comida(String nome, String descricao, int valor) {
        super(nome, descricao, TipoItem.COMIDA, valor);
    }

    public void consumir(Personagem personagem) {
        personagem.setMoral(personagem.getMoral() + valor);
        System.out.println(personagem.getNome() + " comeu " + nome + " e ganhou +" + valor + " de moral!");
    }
}