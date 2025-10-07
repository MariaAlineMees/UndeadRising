package br.com.pedrodamasceno.model.itens;

public class Arma extends Item {
    private int dano;
    protected int durabilidade;
    protected int durabilidadeMaxima;

    public Arma(String nome, String descricao, int valor, int dano, int durabilidade) {
        super(nome, descricao, TipoItem.ARMA, valor);
        this.dano = dano;
        this.durabilidade = durabilidade;
        this.durabilidadeMaxima = durabilidade; // A durabilidade inicial é a máxima
    }

    // Método para usar a arma (reduz durabilidade)
    public boolean usar() {
        if (durabilidade > 0) {
            durabilidade--;
            return true;
        }
        return false; // Arma quebrada
    }

    // GETTERS simplificados
    public int getDano() { return dano; }
    public int getDurabilidade() { return durabilidade; }
    public int getDurabilidadeMaxima() { return durabilidadeMaxima; }
    public boolean estaQuebrada() { return durabilidade <= 0; }

    @Override
    public String toString() {
        return nome + " (Dano: " + dano + ", Durabilidade: " + durabilidade + ")";
    }
}