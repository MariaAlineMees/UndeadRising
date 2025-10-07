package br.com.pedrodamasceno.model.itens;

// Removidas importações não utilizadas

public class IsqueiroESpray extends Item {

    private final int danoBase; // Dano que o item causa

    public IsqueiroESpray(String nome, String descricao, int valor, int danoBase) {
        super(nome, descricao, TipoItem.EXPLOSIVO, valor); // Tipo como EXPLOSIVO para dano em área
        this.danoBase = danoBase;
    }

    // Este item será usado em combate para causar dano em área
    // O método 'usar' aqui pode ser adaptado se houver uso fora de combate
    // Por enquanto, o foco é o combate, então não há um método 'usar' para Personagem aqui
    // A lógica de dano em área será no ControladorJogo/SistemaCombate
    public int getDanoBase() {
        return danoBase;
    }
}
