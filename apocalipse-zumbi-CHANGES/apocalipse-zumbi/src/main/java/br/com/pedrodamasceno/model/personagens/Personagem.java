package br.com.pedrodamasceno.model.personagens;

import java.util.ArrayList;
import java.util.Iterator; 
import java.util.List;

import br.com.pedrodamasceno.model.itens.Arma;
import br.com.pedrodamasceno.model.itens.ArmaDeFogo;
import br.com.pedrodamasceno.model.itens.Armadura; 
import br.com.pedrodamasceno.model.itens.Inventario;
import br.com.pedrodamasceno.model.itens.Item;
import br.com.pedrodamasceno.model.status.EfeitoStatus;
import br.com.pedrodamasceno.model.status.StatusEffect;

public abstract class Personagem {
    protected String nome;
    protected int saude;
    protected int saudeMaxima;
    protected int forca;
    protected int destreza;
    protected int inteligencia;
    protected int constituicao;
    protected int diasSobrevividos;
    protected int moral;
    protected Inventario inventario;
    protected int cargasHab1;
    protected int cargasHab2;
    protected Arma armaEquipada; 
    protected Armadura armaduraEquipada; 
    protected final List<StatusEffect> efeitos = new ArrayList<>();
    private String sentimento; 

    public Personagem(String nome, int forca, int destreza, int inteligencia, int constituicao, int moralInicial) {
        this.nome = nome;
        this.forca = forca;
        this.destreza = destreza;
        this.inteligencia = inteligencia;
        this.constituicao = constituicao;
        this.saudeMaxima = 100 + (constituicao * 5);
        this.saude = saudeMaxima;
        this.inventario = new Inventario(5 + Math.max(0, constituicao));
        this.diasSobrevividos = 0;
        this.moral = Math.max(0, moralInicial);
        this.cargasHab1 = 2;
        this.cargasHab2 = 1;
    }

    // Métodos abstratos
    public abstract void usarHabilidadeEspecial1();
    public abstract void usarHabilidadeEspecial2();
    public abstract String getDescricaoHabilidade1();
    public abstract String getDescricaoHabilidade2();

    // Métodos de arma
    public Arma getArmaEquipada() {
        return armaEquipada;
    }

    public void equiparArma(Arma arma) {
        if (arma != null && !arma.estaQuebrada()) {
            this.armaEquipada = arma;
        }
    }

    public void desequiparArma() {
        this.armaEquipada = null;
    }

    public boolean temArmaEquipada() {
        return armaEquipada != null && !armaEquipada.estaQuebrada();
    }

    // Métodos de armadura
    public Armadura getArmaduraEquipada() {
        return armaduraEquipada;
    }

    public void equiparArmadura(Armadura novaArmadura) {
        if (novaArmadura != null && !novaArmadura.estaQuebrada()) {
            if (this.armaduraEquipada != null) {
                this.armaduraEquipada.remover(this);
                this.inventario.adicionarItem(this.armaduraEquipada); 
            }
            this.armaduraEquipada = novaArmadura;
            this.armaduraEquipada.usar(this); 
            this.inventario.removerItem(novaArmadura); 
        }
    }

    public void desequiparArmadura() {
        if (this.armaduraEquipada != null) {
            this.inventario.adicionarItem(this.armaduraEquipada); 
            this.armaduraEquipada = null;
        }
    }

    public boolean temArmaduraEquipada() {
        return this.armaduraEquipada != null && !this.armaduraEquipada.estaQuebrada();
    }

    // Métodos para o uso de armas
    public boolean podeUsarArmaEquipada() {
        if (armaEquipada == null || armaEquipada.estaQuebrada()) {
            return false;
        }
        if (armaEquipada instanceof ArmaDeFogo) {
            return ((ArmaDeFogo) armaEquipada).getMunicao() > 0;
        }
        return armaEquipada.getDurabilidade() > 0;
    }

    public int calcularDanoArma() {
        int danoBase = forca; 

        if (temArmaEquipada() && podeUsarArmaEquipada()) {
            danoBase = armaEquipada.getDano(); 
            armaEquipada.usar(); 
        } else if (armaEquipada != null && armaEquipada.estaQuebrada()) {
            desequiparArma();
        }
        return danoBase;
    }

    // Métodos para gerenciar Efeitos de Status
    public int getBuff(EfeitoStatus tipo) {
        return efeitos.stream()
                .filter(e -> e.getTipo() == tipo)
                .mapToInt(StatusEffect::getValor)
                .sum();
    }

    public boolean temEfeito(EfeitoStatus tipo) {
        return efeitos.stream().anyMatch(e -> e.getTipo() == tipo);
    }

    public void adicionarEfeito(StatusEffect e) {
        if (e != null) efeitos.add(e);
    }

    public void removerEfeito(EfeitoStatus tipo) {
        efeitos.removeIf(s -> s.getTipo() == tipo);
    }

    public List<String> aplicarEfeitosInicioTurnoComLogs() {
        List<String> logs = new ArrayList<>();
        Iterator<StatusEffect> it = efeitos.iterator();
        while (it.hasNext()) {
            StatusEffect eff = it.next();
            switch (eff.getTipo()) {
                case VENENO:
                    int danoV = eff.getValor();
                    receberDano(danoV);
                    logs.add(nome + " sofreu " + danoV + " de VENENO (" + (eff.getDuracao()-1) + " turnos restantes).");
                    break;
                case SANGRAMENTO:
                    int danoS = eff.getValor();
                    receberDano(danoS);
                    logs.add(nome + " sangrou e perdeu " + danoS + " HP (" + (eff.getDuracao()-1) + ").");
                    break;
                case CANSACO:
                    logs.add(nome + " está cansado (redução temporária de desempenho).");
                    break;
                case MEDO:
                    logs.add(nome + " está amedrontado (moral reduzida).");
                    break;
                case RESISTENCIA:
                    logs.add(nome + " mantém sua resistência extra por " + eff.getDuracao() + " turnos.");
                    break;
                case FORCA:
                    logs.add(nome + " sente a fúria em suas veias por " + eff.getDuracao() + " turnos.");
                    break;
                default:
                    break;
            }
            eff.tick();
            if (eff.expirou()) {
                logs.add(nome + " - Efeito de " + eff.getTipo().name() + " expirou."); // Log quando o efeito expira
                it.remove();
            }
        }
        return logs;
    }

    public void aplicarEfeitosInicioTurno() {
        Iterator<StatusEffect> it = efeitos.iterator();
        while (it.hasNext()) {
            StatusEffect eff = it.next();
            eff.tick();
            if (eff.expirou()) {
                it.remove();
            }
        }
    }

    public void aplicarEfeitosFimTurno() {
        // Pode ser deixado vazio
    }

    public void receberDano(int dano) {
        if (dano < 0) {
            curar(-dano);
            return;
        }
        
        // NOVO: Verificar esquiva garantida
        if (getBuff(EfeitoStatus.ESQUIVA_GARANTIDA) > 0) {
            System.out.println(nome + " esquivou o ataque com FURTIVIDADE!"); // DEBUG
            removerEfeito(EfeitoStatus.ESQUIVA_GARANTIDA); // Remove o efeito após uso
            return; // Não recebe dano algum
        }
        
        int danoFinal = dano;

        // NOVO: Reduzir dano com base no buff de RESISTENCIA
        int buffResistencia = getBuff(EfeitoStatus.RESISTENCIA);
        System.out.println(nome + " (Jogador) Dano Original: " + dano + ", Buff Resistencia: " + buffResistencia); // DEBUG ADICIONADO
        if (buffResistencia > 0) {
            danoFinal = Math.max(0, danoFinal - buffResistencia); // Reduz o dano se o buff for positivo
        } else {
            danoFinal -= buffResistencia; // Aumenta o dano se o buff for negativo (transforma em +dano)
        }
        danoFinal = Math.max(0, danoFinal); // Garantir que o dano não seja negativo

        // NOVO: Reduzir dano com base na armadura equipada
        if (temArmaduraEquipada()) {
            int defesaArmadura = armaduraEquipada.getDefesa();
            System.out.println(nome + " (Jogador) Defesa Armadura: " + defesaArmadura); // DEBUG ADICIONADO
            danoFinal = Math.max(0, danoFinal - defesaArmadura);
            // A durabilidade da armadura será tratada em outro lugar (ex: SistemaCombate)
        }

        danoFinal = Math.max(0, danoFinal); // Garantir que o dano não seja negativo após todos os cálculos

        System.out.println(nome + " (Jogador) recebeu " + danoFinal + " de dano. Saúde antes: " + saude); // DEBUG

        // NOVO: Verificar Segunda Chance antes de aplicar dano fatal
        if (saude - danoFinal <= 0 && getBuff(EfeitoStatus.SEGUNDA_CHANCE) > 0) {
            System.out.println(nome + " usou SEGUNDA CHANCE e sobreviveu com 1 HP!"); // DEBUG
            saude = 1; // Fica com 1 HP
            removerEfeito(EfeitoStatus.SEGUNDA_CHANCE); // Remove o efeito após uso
        } else {
            saude -= danoFinal;
            if (saude < 0) saude = 0;
        }
        
        System.out.println(nome + " (Jogador) saúde depois: " + saude); // DEBUG
        if (danoFinal > 0) { // Se recebeu dano real, afeta o sentimento negativamente
            atualizarSentimentoJogador(-1);
        }
    }

    public void curar(int quantidade) {
        int saudeAntes = this.saude;
        this.saude = Math.min(this.saude + quantidade, this.saudeMaxima);
        if (this.saude > saudeAntes) { // Se a cura foi efetiva, afeta o sentimento positivamente
            atualizarSentimentoJogador(1);
        }
    }

    public boolean estaVivo() { return saude > 0; }

    public boolean adicionarItem(Item item) { return inventario.adicionarItem(item); }
    public void removerItem(Item item) { inventario.removerItem(item); }

    public int getMoral() { return moral; }
    public void setMoral(int moral) { this.moral = moral; }
    public void aumentarMoral(int q) { moral += q; }
    public void reduzirMoral(int q) { moral = Math.max(0, moral - q); }

    public String getSentimento() {
        return sentimento;
    }

    public void setSentimento(String sentimento) {
        this.sentimento = sentimento;
    }

    public int calcularChanceEsquiva() {
        return Math.min(60, destreza * 2 + (moral / 2));
    }

    public int calcularChanceCritico() {
        return Math.min(40, (int)Math.round((forca * 1.5) + (moral / 3)));
    }

    // Getters/Setters
    public String getNome() { return nome; }
    public int getSaude() { return saude; }
    public void setSaude(int saude) { this.saude = saude; }
    public int getSaudeMaxima() { return saudeMaxima; }
    public void setSaudeMaxima(int saudeMaxima) { this.saudeMaxima = saudeMaxima; }
    public int getForca() { return forca; }
    public void setForca(int forca) { this.forca = forca; }
    public int getDestreza() { return destreza; }
    public void setDestreza(int destreza) { this.destreza = destreza; }
    public int getInteligencia() { return inteligencia; }
    public void setInteligencia(int inteligencia) { this.inteligencia = inteligencia; }
    public int getConstituicao() { return constituicao; }
    public void setConstituicao(int constituicao) { this.constituicao = constituicao; }
    public int getDiasSobrevividos() { return diasSobrevividos; }
    public void aumentarDia() { diasSobrevividos++; }
    public Inventario getInventario() { return inventario; }
    public List<StatusEffect> getEfeitosAtivos() { return new ArrayList<>(efeitos); }

    // Métodos para gerenciar cargas de habilidades (Adicionados)
    public int getCargasHabilidade1() { return cargasHab1; }
    public void setCargasHabilidade1(int cargas) { this.cargasHab1 = cargas; }
    public int getCargasHabilidade2() { return cargasHab2; }
    public void setCargasHabilidade2(int cargas) { this.cargasHab2 = cargas; }

    // NOVO: Método para resetar as cargas das habilidades
    public void resetCargasHabilidades() {
        this.cargasHab1 = 2;
        this.cargasHab2 = 1;
    }

    // NOVO: Método para recarregar a arma equipada
    public String recarregarArmaEquipada(Item itemMunicao) {
        if (armaEquipada == null) {
            return "Nenhuma arma equipada para recarregar.";
        }

        if (!(armaEquipada instanceof ArmaDeFogo armaDeFogo)) {
            return "A arma equipada não é uma arma de fogo.";
        }

        // Supondo que o valor do item Munição represente a quantidade de munição
        // Apenas para o tipo MUNICAO para evitar uso indevido de outros itens
        if (itemMunicao.getTipo() == br.com.pedrodamasceno.model.itens.TipoItem.MUNICAO) {
            int quantidadeMunicao = itemMunicao.getValor();
            armaDeFogo.recarregar(quantidadeMunicao);
            inventario.removerItem(itemMunicao);
            return "Você recarregou sua " + armaDeFogo.getNome() + " com " + quantidadeMunicao + " de munição.";
        } else {
            return "Isso não é munição!";
        }
    }

    // NOVO: Método para remover todos os StatusEffect temporários do jogador
    public void removerTodosEfeitosTemporarios() {
        efeitos.removeIf(e -> e.getDuracao() > 0);
    }

    // NOVO: Método para atualizar o sentimento do jogador (movido de ModeloJogo)
    public void atualizarSentimentoJogador(int intensidadeEvento) { // intensidadeEvento: positivo para bom, negativo para ruim
        String sentimentoAtual = getSentimento();

        if (intensidadeEvento > 0) { // Evento positivo
            switch (sentimentoAtual) {
                case "Desesperançoso" -> setSentimento("Cansado");
                case "Cansado" -> setSentimento("Motivado");
                case "Assustado" -> setSentimento("Esperançoso");
                case "Furioso" -> setSentimento("Motivado");
                default -> setSentimento("Motivado"); // Sentimento base positivo
            }
        } else if (intensidadeEvento < 0) { // Evento negativo
            switch (sentimentoAtual) {
                case "Motivado" -> setSentimento("Cansado");
                case "Esperançoso" -> setSentimento("Cansado");
                case "Cansado" -> setSentimento("Desesperançoso");
                case "Assustado" -> setSentimento("Desesperançoso");
                case "Furioso" -> setSentimento("Assustado");
                default -> setSentimento("Assustado"); // Sentimento base negativo
            }
        } else { // Evento neutro
            // Não muda o sentimento, talvez uma mensagem neutra ou algo assim
        }
    }
}