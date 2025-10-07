package br.com.pedrodamasceno.model.itens;

import br.com.pedrodamasceno.model.personagens.Personagem;
import br.com.pedrodamasceno.model.status.EfeitoStatus;

public class AnalgesicoForte extends Medicamento {

    public AnalgesicoForte(String nome, String descricao, int cura) {
        super(nome, descricao, cura);
    }

    @Override
    public String usar(Personagem p) {
        super.usar(p); // Aplica a cura do medicamento base
        p.removerEfeito(EfeitoStatus.CANSACO); // Remove o efeito de cansaço
        p.removerEfeito(EfeitoStatus.MEDO); // Remove o efeito de medo
        return "Você usou " + nome + " e se sente revigorado!";
    }
}
