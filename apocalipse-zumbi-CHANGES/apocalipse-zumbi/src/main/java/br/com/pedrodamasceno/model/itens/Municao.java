package br.com.pedrodamasceno.model.itens;

import br.com.pedrodamasceno.model.personagens.Personagem;

public class Municao extends Item {

    public Municao(String nome, String descricao, int valor) {
        super(nome, descricao, TipoItem.MUNICAO, valor);
    }

    @Override
    public String usar(Personagem p) {
        return "Munição não pode ser usada diretamente. Equipe uma arma de fogo e use o item 'Munição' para recarregar.";
    }
}
