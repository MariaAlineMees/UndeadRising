package br.com.pedrodamasceno.model.locais;

public class SubLocal extends Local {

    public SubLocal(String nome, String descricao, int nivelPerigo) {
        super(nome, descricao, nivelPerigo);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SubLocal subLocal = (SubLocal) o;
        return getNome().equals(subLocal.getNome());
    }

    @Override
    public int hashCode() {
        return getNome().hashCode();
    }

    // Métodos específicos para SubLocal podem ser adicionados aqui no futuro,
    // como encontrar itens muito raros ou ter eventos únicos.
}

