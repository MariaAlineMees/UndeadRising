package br.com.pedrodamasceno.model.personagens;

import br.com.pedrodamasceno.model.status.EfeitoStatus;
import br.com.pedrodamasceno.model.status.StatusEffect;

public class Lutador extends Personagem {
    public Lutador(String nome) {
        super(nome, 9, 5, 4, 8, 15);
    }

    @Override
    public void usarHabilidadeEspecial1() {
        if (getCargasHabilidade1() > 0) {
            // Fúria - aumenta força em 5 por 3 turnos, mas reduz defesa
            adicionarEfeito(new StatusEffect(EfeitoStatus.FORCA, 3, 10));
            adicionarEfeito(new StatusEffect(EfeitoStatus.RESISTENCIA, 3, -2));
            setCargasHabilidade1(getCargasHabilidade1() - 1);
        }
    }

    @Override
    public void usarHabilidadeEspecial2() {
        if (getCargasHabilidade2() > 0) {
            // Resistência - recupera 20% da saúde e ganha +3 de resistência por 2 turnos
            curar((int) (getSaudeMaxima() * 0.2));
            adicionarEfeito(new StatusEffect(EfeitoStatus.RESISTENCIA, 2, 3));
            setCargasHabilidade2(getCargasHabilidade2() - 1);
        }
    }

    @Override
    public String getDescricaoHabilidade1() {
        return "FÚRIA: Aumenta força em +10 mas reduz defesa em -2 por 3 turnos. Cargas: " + getCargasHabilidade1();
    }

    @Override
    public String getDescricaoHabilidade2() {
        return "RESISTÊNCIA: Recupera 20% da saúde e ganha +3 de defesa por 2 turnos. Cargas: " + getCargasHabilidade2();
    }

    public int getDanoHabilidade(int numeroHabilidade) {
        if (numeroHabilidade == 1) {
            return getForca() + 5; // Dano da habilidade 1
        } else {
            return getForca() + 8; // Dano da habilidade 2
        }
    }
}