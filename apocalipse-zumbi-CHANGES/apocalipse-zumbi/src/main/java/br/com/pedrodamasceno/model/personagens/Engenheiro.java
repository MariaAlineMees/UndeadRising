package br.com.pedrodamasceno.model.personagens;

import br.com.pedrodamasceno.model.status.EfeitoStatus;
import br.com.pedrodamasceno.model.status.StatusEffect;

public class Engenheiro extends Personagem {
    public Engenheiro(String nome) {
        super(nome, 5, 6, 8, 5, 9);
    }

    @Override
    public void usarHabilidadeEspecial1() {
        if (getCargasHabilidade1() > 0) {
            // Armadilha Explosiva - causa dano em área e atordoa inimigos
            adicionarEfeito(new StatusEffect(EfeitoStatus.DANO_AREA, 1, 15)); // Dano base de 15
            adicionarEfeito(new StatusEffect(EfeitoStatus.ATORDOAMENTO, 1, 1)); // Atordoa inimigos
            setCargasHabilidade1(getCargasHabilidade1() - 1);
        }
    }

    @Override
    public void usarHabilidadeEspecial2() {
        if (getCargasHabilidade2() > 0) {
            // Reparos Rápidos - recupera armadura e ganha defesa extra
            adicionarEfeito(new StatusEffect(EfeitoStatus.RESISTENCIA, 3, 4));
            curar(10); // Pequena cura adicional
            setCargasHabilidade2(getCargasHabilidade2() - 1);
        }
    }

    @Override
    public String getDescricaoHabilidade1() {
        return "ARMADILHA EXPLOSIVA: Causa 15 de dano em área e atordoa inimigos por 1 turno. Cargas: " + getCargasHabilidade1();
    }

    @Override
    public String getDescricaoHabilidade2() {
        return "REPAROS RÁPIDOS: Ganha +4 de defesa por 3 turnos e recupera 10 de saúde. Cargas: " + getCargasHabilidade2();
    }

    public int getDanoHabilidade(int numeroHabilidade) {
        if (numeroHabilidade == 1) {
            return getForca() + 5; // Dano da habilidade 1
        } else {
            return getForca() + 8; // Dano da habilidade 2
        }
    }
}