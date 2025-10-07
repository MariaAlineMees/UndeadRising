package br.com.pedrodamasceno.model.personagens;

import br.com.pedrodamasceno.model.itens.Arma;
import br.com.pedrodamasceno.model.itens.Item;
import br.com.pedrodamasceno.model.itens.TipoItem;

public class SobreviventeRival extends Personagem {
    private String historia;
    private String motivacao;
    private int nivelConfianca; // 0-100: afeta diálogo e chances de acordo pacífico
    private boolean filhaDoente;
    private boolean revelouHistoria;

    public SobreviventeRival() {
        super("Ana Silva", 12, 10, 14, 11, 50); // nome, força, destreza, inteligência, constituição, moral
        this.historia = "Ex-médica do hospital Santa Vida, perdeu toda sua família exceto sua filha de 8 anos que está gravemente doente.";
        this.motivacao = "Precisa desesperadamente salvar sua filha que está com uma doença grave e precisa de tratamento.";
        this.nivelConfianca = 50;
        this.filhaDoente = true;
        this.revelouHistoria = false;
        
        // Equipar com itens iniciais
        Arma arma = new Arma("Revólver Policial", "Arma bem conservada de uma policial falecida", 40, 15, 6);
        this.adicionarItem(arma);
        // A foto é considerada um item especial que não ocupa espaço no inventário
        this.adicionarItem(new Item("Foto da Filha", "Uma foto desgastada mostrando uma menina sorrindo em uma cama de hospital", TipoItem.COMIDA, 0));
    }

    @Override
    public void usarHabilidadeEspecial1() {
        // Habilidade: Conhecimento Médico - Cura a si mesma usando conhecimentos médicos
        if (getCargasHabilidade1() > 0) {
            curar(30);
            setCargasHabilidade1(getCargasHabilidade1() - 1);
        }
    }

    @Override
    public void usarHabilidadeEspecial2() {
        // Habilidade: Determinação Maternal - Aumenta temporariamente força e moral
        if (getCargasHabilidade2() > 0) {
            setForca(getForca() + 5);
            aumentarMoral(20);
            setCargasHabilidade2(getCargasHabilidade2() - 1);
        }
    }

    @Override
    public String getDescricaoHabilidade1() {
        return "Conhecimento Médico: Usa conhecimentos médicos para se curar em 30 pontos de vida.";
    }

    @Override
    public String getDescricaoHabilidade2() {
        return "Determinação Maternal: A lembrança da filha aumenta força em 5 e moral em 20 temporariamente.";
    }

    public String getHistoria() {
        return historia;
    }

    public String getMotivacao() {
        return motivacao;
    }

    public boolean isFilhaDoente() {
        return filhaDoente;
    }

    public void aumentarConfianca(int valor) {
        this.nivelConfianca = Math.min(100, this.nivelConfianca + valor);
    }

    public void diminuirConfianca(int valor) {
        this.nivelConfianca = Math.max(0, this.nivelConfianca - valor);
    }

    public int getNivelConfianca() {
        return nivelConfianca;
    }

    public boolean isRevelouHistoria() {
        return revelouHistoria;
    }

    public void setRevelouHistoria(boolean revelou) {
        this.revelouHistoria = revelou;
    }
}