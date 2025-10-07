package br.com.pedrodamasceno.model.itens;

import java.util.ArrayList;
import java.util.List;

public class Inventario {
    private List<Item> itens;
    private int capacidade;

    public Inventario(int capacidade) {
        this.itens = new ArrayList<>();
        this.capacidade = capacidade;
    }

    public boolean adicionarItem(Item item) {
        if (itens.size() < capacidade) {
            itens.add(item);
            return true;
        }
        return false;
    }

    public void removerItem(Item item) {
        itens.remove(item);
    }

    public List<Item> getItens() {
        return new ArrayList<>(itens);
    }

    public int getCapacidade() {
        return capacidade;
    }

    public void aumentarCapacidade(int quantidade) {
        this.capacidade += quantidade;
    }

    public void setCapacidade(int novaCapacidade) {
        if (novaCapacidade < 0) novaCapacidade = 0;
        this.capacidade = novaCapacidade;
        // Se reduzir abaixo do tamanho atual, mantém os itens porém impede novas adições.
    }

    public int getTamanhoAtual() {
        return itens.size();
    }

    public boolean temEspaco() {
        return itens.size() < capacidade;
    }

    public Item encontrarItemPorNome(String nome) {
        for (Item item : itens) {
            if (item.getNome().equalsIgnoreCase(nome)) {
                return item;
            }
        }
        return null;
    }

    public boolean contemItem(String nome) {
        return encontrarItemPorNome(nome) != null;
    }
}