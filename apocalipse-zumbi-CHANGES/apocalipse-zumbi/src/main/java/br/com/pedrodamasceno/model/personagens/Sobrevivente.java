package br.com.pedrodamasceno.model.personagens;

import br.com.pedrodamasceno.model.status.EfeitoStatus;
import br.com.pedrodamasceno.model.status.StatusEffect;

public class Sobrevivente extends Personagem {
    public Sobrevivente(String nome) {
        super(nome, 6, 7, 5, 6, 12);
    }

    @Override
    public void usarHabilidadeEspecial1() {
        if (getCargasHabilidade1() > 0) {
            // CORREÇÃO: Furtividade - esquiva garantida do próximo ataque
            adicionarEfeito(new StatusEffect(EfeitoStatus.ESQUIVA_GARANTIDA, 1, 100));
            setCargasHabilidade1(getCargasHabilidade1() - 1);
        }
    }

    @Override
    public void usarHabilidadeEspecial2() {
        if (getCargasHabilidade2() > 0) {
            // Sorte do Iniciante - chance de crítico aumenta para 100% no próximo ataque
            System.out.println("DEBUG SOBREVIVENTE: Aplicando crítico garantido..."); // DEBUG
            adicionarEfeito(new StatusEffect(EfeitoStatus.CRITICO_GARANTIDO, 1, 100));
            System.out.println("DEBUG SOBREVIVENTE: Crítico garantido aplicado. Efeitos ativos: " + getEfeitosAtivos().size()); // DEBUG
            setCargasHabilidade2(getCargasHabilidade2() - 1);
        }
    }

    @Override
    public String getDescricaoHabilidade1() {
        return "FURTIVIDADE: Esquiva garantida do próximo ataque inimigo. Cargas: " + getCargasHabilidade1();
    }

    @Override
    public String getDescricaoHabilidade2() {
        return "SORTE DO INICIANTE: Próximo ataque tem 100% de chance de crítico. Cargas: " + getCargasHabilidade2();
    }

    public int getDanoHabilidade(int numeroHabilidade) {
        if (numeroHabilidade == 1) {
            return getForca() + 5; // Dano da habilidade 1
        } else {
            return getForca() + 8; // Dano da habilidade 2
        }
    }
}