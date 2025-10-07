package br.com.pedrodamasceno.model.locais;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import br.com.pedrodamasceno.model.itens.Arma; // ADICIONAR ESTE IMPORT
import br.com.pedrodamasceno.model.itens.Item;
import br.com.pedrodamasceno.model.itens.Medicamento;
import br.com.pedrodamasceno.model.itens.TipoItem;
import br.com.pedrodamasceno.model.zumbis.Zumbi;
import br.com.pedrodamasceno.model.zumbis.ZumbiCachorro;
import br.com.pedrodamasceno.model.zumbis.ZumbiComum;
import br.com.pedrodamasceno.model.zumbis.ZumbiEstrategista;
import br.com.pedrodamasceno.model.zumbis.ZumbiInfectado;
import br.com.pedrodamasceno.model.zumbis.ZumbiNoturno;
import br.com.pedrodamasceno.model.zumbis.ZumbiTanque;

public class Local {
    private String nome;
    private String descricao;
    private int nivelPerigo;
    private boolean temZumbis;
    private boolean temItens;
    private List<SubLocal> subLocais; // Nova lista de sublocais

    public Local(String nome, String descricao, int nivelPerigo) {
        this.nome = nome;
        this.descricao = descricao;
        this.nivelPerigo = nivelPerigo;
        this.temZumbis = false;
        this.temItens = false;
        this.subLocais = new ArrayList<>(); // Inicializa a lista de sublocais
    }

    public void explorar() {
        Random rand = new Random();
        this.temZumbis = rand.nextInt(10) < nivelPerigo;      // quanto mais perigoso, mais chance de zumbi
        this.temItens  = rand.nextInt(10) < (7 - Math.max(1, Math.min(6, nivelPerigo))); // balance
    }

    public boolean temZumbis() { return temZumbis; }
    public boolean temItens()  { return temItens; }

    public List<Zumbi> encontrarZumbis() {
        List<Zumbi> lista = new ArrayList<>();
        if (!temZumbis) return lista;

        Random rand = new Random();
        int max = Math.min(3, 1 + (nivelPerigo >= 7 ? 2 : (nivelPerigo >= 5 ? 1 : 0))); // 1–3
        int qtd = 1 + rand.nextInt(max);

        for (int i = 0; i < qtd; i++) {
            int tipo = rand.nextInt(6);
            switch (tipo) {
                case 0: lista.add(new ZumbiComum()); break;
                case 1: lista.add(new ZumbiCachorro()); break;
                case 2: lista.add(new ZumbiTanque()); break;
                case 3: lista.add(new ZumbiInfectado()); break;
                case 4: lista.add(new ZumbiEstrategista()); break;
                case 5: lista.add(new ZumbiNoturno()); break;
                default: lista.add(new ZumbiComum());
            }
        }
        return lista;
    }

    public Item encontrarItem() {
        if (!temItens) return null;
        Random rand = new Random();
        int tipoItem = rand.nextInt(10);
        switch (tipoItem) {
            case 0: return new Item("Comida Enlatada", "Comida não perecível", TipoItem.COMIDA, 15);
            case 1: return new Medicamento("Medicamento de Cura Pequeno", "Cura 20 HP", 20);
            case 2: return new Medicamento("Medicamento de Cura Médio", "Cura 40 HP", 40);
            case 3: return new Medicamento("Medicamento de Cura Grande", "Cura 60 HP", 60);
            case 4: return new Arma("Faca", "Arma branca simples", 15, 15, 5);
            case 5: return new Arma("Revólver", "Arma de fogo básica", 30, 12, 15);
            case 6: return new Arma("Espingarda", "Curto alcance", 45, 18, 10);
            case 7: return new Arma("Rifle", "Longo alcance", 60, 22, 5);
            case 8: return new Item("Munição", "Munição para armas de fogo", TipoItem.MUNICAO, 10);
            case 9: return new Item("Kit de Primeiros Socorros", "Cura ferimentos", TipoItem.MEDICAMENTO, 30);
            default: return new Item("Água", "Água potável", TipoItem.COMIDA, 10);
        }
    }

    public String getNome() { return nome; }
    public String getDescricao() { return descricao; }
    public int getNivelPerigo() { return nivelPerigo; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Local local = (Local) o;
        return nome.equals(local.nome);
    }

    @Override
    public int hashCode() {
        return nome.hashCode();
    }

    // Novos métodos para gerenciar sublocais
    public void addSubLocal(SubLocal subLocal) {
        this.subLocais.add(subLocal);
    }

    public List<SubLocal> getSubLocais() {
        return subLocais;
    }
}