package br.com.pedrodamasceno.model.personagens;

import br.com.pedrodamasceno.model.status.EfeitoStatus;
import br.com.pedrodamasceno.model.status.StatusEffect;

public class Estrategista extends Personagem {
    public Estrategista(String nome) {
        super(nome, 4, 6, 8, 4, 7);
    }

    @Override
    public void usarHabilidadeEspecial1() {
        if (getCargasHabilidade1() > 0) {
            // Plano de Fuga - chance de evitar o próximo ataque inimigo
            adicionarEfeito(new StatusEffect(EfeitoStatus.ESQUIVA_GARANTIDA, 1, 100));
            setCargasHabilidade1(getCargasHabilidade1() - 1);
        }
    }

    @Override
    public void usarHabilidadeEspecial2() {
        if (getCargasHabilidade2() > 0) {
            // Análise de Fraqueza - próximo ataque ganha +20 de dano fixo
            adicionarEfeito(new StatusEffect(EfeitoStatus.IGNORAR_DEFESA, 1, 20));
            setCargasHabilidade2(getCargasHabilidade2() - 1);
        }
    }

    @Override
    public String getDescricaoHabilidade1() {
        return "PLANO DE FUGA: Próximo ataque inimigo é automaticamente esquivado. Cargas: " + getCargasHabilidade1();
    }

    @Override
    public String getDescricaoHabilidade2() {
        return "ANÁLISE DE FRAQUEZA: Próximo ataque ganha +20 de dano adicional. Cargas: " + getCargasHabilidade2();
    }

    public int getDanoHabilidade(int numeroHabilidade) {
        if (numeroHabilidade == 1) {
            return getForca() + 5; // Dano da habilidade 1
        } else {
            return getForca() + 8; // Dano da habilidade 2
        }
    }
}