package br.com.pedrodamasceno.model.personagens;

import br.com.pedrodamasceno.model.status.EfeitoStatus;

public class Medico extends Personagem {
    public Medico(String nome) {
        super(nome, 4, 5, 9, 5, 8);
    }

    @Override
    public void usarHabilidadeEspecial1() {
        if (getCargasHabilidade1() > 0) {
            // Cura de Emergência - cura 30% da saúde máxima
            int cura = (int) (getSaudeMaxima() * 0.3);
            curar(cura);
            setCargasHabilidade1(getCargasHabilidade1() - 1);
        }
    }

    @Override
    public void usarHabilidadeEspecial2() {
        if (getCargasHabilidade2() > 0) {
            // Diagnóstico Rápido - remove todos os efeitos negativos
            removerEfeito(EfeitoStatus.VENENO);
            removerEfeito(EfeitoStatus.SANGRAMENTO);
            removerEfeito(EfeitoStatus.CANSACO);
            removerEfeito(EfeitoStatus.MEDO);
            curar((int) (getSaudeMaxima() * 0.20)); // Adiciona cura de 20% do HP
            setCargasHabilidade2(getCargasHabilidade2() - 1);
        }
    }

    @Override
    public String getDescricaoHabilidade1() {
        return "CURA DE EMERGÊNCIA: Recupera 30% da saúde máxima. Cargas: " + getCargasHabilidade1();
    }

    @Override
    public String getDescricaoHabilidade2() {
        return "DIAGNÓSTICO RÁPIDO: Remove todos os efeitos negativos e recupera 20% do HP. Cargas: " + getCargasHabilidade2();
    }

    public int getDanoHabilidade(int numeroHabilidade) {
        if (numeroHabilidade == 1) {
            return getForca() + 5;
        } else {
            return getForca() + 8;
        }
    }
}