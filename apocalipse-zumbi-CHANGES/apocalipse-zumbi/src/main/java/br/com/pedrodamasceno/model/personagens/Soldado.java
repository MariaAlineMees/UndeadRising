package br.com.pedrodamasceno.model.personagens;

import br.com.pedrodamasceno.model.status.EfeitoStatus;
import br.com.pedrodamasceno.model.status.StatusEffect;

public class Soldado extends Personagem {
    public Soldado(String nome) {
        super(nome, 8, 6, 4, 7, 10);
    }

    @Override
    public void usarHabilidadeEspecial1() {
        if (getCargasHabilidade1() > 0) {
            // CORREÇÃO: Remover efeitos de resistência anteriores da postura de combate antes de aplicar novo
            removerEfeito(EfeitoStatus.RESISTENCIA);
            
            // Lógica da Postura de Combate - aplicar novo efeito
            adicionarEfeito(new StatusEffect(EfeitoStatus.RESISTENCIA, 3, 10)); // +10 resistência por 3 turnos
            setCargasHabilidade1(getCargasHabilidade1() - 1);
        }
    }

    @Override
    public void usarHabilidadeEspecial2() {
        if (getCargasHabilidade2() > 0) {
            // Lógica da Segunda Chance
            adicionarEfeito(new StatusEffect(EfeitoStatus.SEGUNDA_CHANCE, 1, 1)); // Ativa segunda chance
            setCargasHabilidade2(getCargasHabilidade2() - 1);
        }
    }

    @Override
    public String getDescricaoHabilidade1() {
        return "POSTURA DE COMBATE: Aumenta a defesa em +10 por 3 turnos. Cargas: " + getCargasHabilidade1();
    }

    @Override
    public String getDescricaoHabilidade2() {
        return "SEGUNDA CHANCE: Sobrevive a um golpe fatal com 1 HP (uso único por combate). Cargas: " + getCargasHabilidade2();
    }

    public int getDanoHabilidade(int numeroHabilidade) {
        if (numeroHabilidade == 1) {
            return getForca() + 5; // Dano da habilidade 1
        } else {
            return getForca() + 8; // Dano da habilidade 2
        }
    }
}