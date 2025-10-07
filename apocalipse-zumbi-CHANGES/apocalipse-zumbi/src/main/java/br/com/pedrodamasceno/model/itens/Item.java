package br.com.pedrodamasceno.model.itens;

import br.com.pedrodamasceno.model.personagens.Personagem; // Importar Personagem

public class Item {
    protected String nome;
    protected String descricao;
    protected TipoItem tipo;
    protected int valor;

    public Item(String nome, String descricao, TipoItem tipo, int valor) {
        this.nome = nome;
        this.descricao = descricao;
        this.tipo = tipo;
        this.valor = valor;
    }

    // Método 'usar' padrão para itens. Subclasses podem sobrescrever.
    public String usar(Personagem p) {
        return "Você tentou usar " + nome + ", mas nada aconteceu.";
    }

    public String getNome() { return nome; }
    public String getDescricao() { return descricao; }
    public TipoItem getTipo() { return tipo; }
    public int getValor() { return valor; }
}