package br.com.pedrodamasceno.model.itens;

import br.com.pedrodamasceno.model.personagens.Personagem; // Importar Personagem
import br.com.pedrodamasceno.model.status.EfeitoStatus; // Importar EfeitoStatus

public class Armadura extends Item {
    private final int defesa;
    private int durabilidade; // Alterado para não ser final, pois a durabilidade diminui

    public Armadura(String nome, String descricao, int valor, int defesa, int durabilidade) {
        super(nome, descricao, TipoItem.ARMADURA, valor);
        this.defesa = defesa;
        this.durabilidade = durabilidade;
    }

    // NOVO MÉTODO: Usar armadura (aplica efeito de defesa)
    @Override
    public String usar(Personagem p) {
        if (durabilidade > 0) {
            this.durabilidade--; // Armadura perde durabilidade com o uso
            return nome + " equipada! Você sente-se mais protegido.";
        } else {
            return nome + " está quebrada e não pode ser usada.";
        }
    }

    // NOVO MÉTODO: Remover armadura (remove efeito de defesa)
    public void remover(Personagem p) {
        p.removerEfeito(EfeitoStatus.RESISTENCIA);
    }

    public int getDefesa() {
        return defesa;
    }

    public int getDurabilidade() {
        return durabilidade;
    }

    public boolean estaQuebrada() {
        return durabilidade <= 0;
    }

    @Override
    public String toString() {
        return nome + " (Defesa: " + defesa + ", Durabilidade: " + durabilidade + ")";
    }
}
