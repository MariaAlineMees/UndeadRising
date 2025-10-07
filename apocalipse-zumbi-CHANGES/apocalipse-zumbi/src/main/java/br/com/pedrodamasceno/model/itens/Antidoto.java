package br.com.pedrodamasceno.model.itens;

import br.com.pedrodamasceno.model.personagens.Personagem;
import br.com.pedrodamasceno.model.status.EfeitoStatus;

public class Antidoto extends Medicamento {

    public Antidoto(String nome, String descricao, int cura) {
        super(nome, descricao, cura);
    }

    @Override
    public String usar(Personagem p) {
        super.usar(p); // Aplica a cura do medicamento base
        p.removerEfeito(EfeitoStatus.VENENO); // Remove o efeito de veneno
        return "Você usou " + nome + " e neutralizou o veneno!";
    }
}
