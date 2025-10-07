package br.com.pedrodamasceno.model.itens;

public class ArmaDeFogo extends Arma {
    private int municao;

    public ArmaDeFogo(String nome, String descricao, int valor, int dano, int durabilidade, int municao) {
        super(nome, descricao, valor, dano, durabilidade);
        this.municao = municao;
        this.tipo = TipoItem.ARMA; // Garante que o tipo seja ARMA
    }

    @Override
    public boolean usar() {
        if (getDurabilidade() > 0 && municao > 0) { // Usar ArmaDeFogo gasta munição e durabilidade
            municao--;
            // Chamar super.usar() para reduzir a durabilidade da arma
            return super.usar(); 
        }
        return false;
    }

    public int getMunicao() {
        return municao;
    }

    // Renomeado de adicionarMunicao para recarregar para melhor clareza
    public void recarregar(int quantidade) {
        this.municao += quantidade;
        this.durabilidade = Math.min(getDurabilidadeMaxima(), this.durabilidade + 10); // Aumenta a durabilidade em 10, limitada ao máximo
    }

    @Override
    public String toString() {
        return nome + " (Dano: " + getDano() + ", Munição: " + municao + ", Durabilidade: " + getDurabilidade() + ")";
    }
}
