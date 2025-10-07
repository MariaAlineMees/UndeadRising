package br.com.pedrodamasceno.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap; // Novo import para HashMap
import java.util.List;
import java.util.Map;    // Novo import para Map
import java.util.Random;

import br.com.pedrodamasceno.model.itens.ArmaDeFogo;
import br.com.pedrodamasceno.model.itens.Item; // Importação adicionada para a classe Item
import br.com.pedrodamasceno.model.locais.Local; // Importar a classe SubLocal
import br.com.pedrodamasceno.model.locais.SubLocal;
import br.com.pedrodamasceno.model.personagens.Personagem;
import br.com.pedrodamasceno.model.zumbis.BossFinal;
import br.com.pedrodamasceno.model.zumbis.Zumbi;
import br.com.pedrodamasceno.model.zumbis.ZumbiCachorro;
import br.com.pedrodamasceno.model.zumbis.ZumbiComum;
import br.com.pedrodamasceno.model.zumbis.ZumbiCorredor;
import br.com.pedrodamasceno.model.zumbis.ZumbiEstrategista;
import br.com.pedrodamasceno.model.zumbis.ZumbiKamikaze;
import br.com.pedrodamasceno.model.zumbis.ZumbiNoturno;
import br.com.pedrodamasceno.model.zumbis.ZumbiTanque; // Importar ArmaDeFogo
import br.com.pedrodamasceno.model.zumbis.ZumbiToxico;

public class ModeloJogo {
    private Personagem jogador;
    private int diaAtual;
    private ConfrontoFinal confrontoFinal;
    private PeriodoDia periodoAtual;
    private boolean emCombate;
    private boolean jogoTerminado;
    private boolean jogoVencido;
    private boolean eventoFinalAtivo;
    private boolean emSelecaoSubLocal; // Novo campo para controlar a seleção de sublocais
    private SubLocal subLocalAtual; // Novo campo para armazenar o sublocal atual
    private String resumoDiaAnterior; // Novo campo para armazenar o resumo do dia anterior

    // CONTROLE SIMPLIFICADO DE EXPLORAÇÃO
    private boolean dormiu = false;
    private int exploracoesDiaAtual; // Novo campo para contar as explorações diárias

    private final List<Local> locaisBase = new ArrayList<>();
    private List<Local> locaisDoDia = new ArrayList<>(3);
    private Local localAtual;
    private List<Zumbi> inimigosAtuais = new ArrayList<>();
    private String mensagem;

    // Novos campos para locais fixos
    private Local localDia1Fixo;
    private Local localDia7Fixo;

    private List<Local> locaisVisitados = new ArrayList<>(); // Nova lista para rastrear locais visitados
    private Map<Local, List<SubLocal>> sublocaisVisitadosPorLocal; // Novo campo para rastrear sublocais visitados por local

    // REMOVIDO: já existe confrontoFinal na linha 30

    // NOVO: Map para armazenar prioridades de itens por local
    private final Map<String, List<Integer>> itemPrioridadesPorLocal;

    public ModeloJogo() {
        this.diaAtual = 1;
        this.periodoAtual = PeriodoDia.MANHA;
        this.emCombate = false;
        this.jogoTerminado = false;
        this.jogoVencido = false;
        this.eventoFinalAtivo = false;
        this.emSelecaoSubLocal = false; // Inicializa como falso
        this.subLocalAtual = null; // Inicializa como nulo
        this.resumoDiaAnterior = ""; // Inicializa com string vazia
        this.locaisVisitados.clear(); // Limpa a lista de locais visitados ao iniciar um novo jogo
        this.sublocaisVisitadosPorLocal = new HashMap<>(); // Inicializa o mapa de sublocais visitados
        this.mensagem = "Bem-vindo ao Apocalipse Zumbi! Escolha seu personagem.";
        this.exploracoesDiaAtual = 0; // Inicializa o contador de explorações diárias
        inicializarLocaisBase();
        sortearLocaisDoDia();
        // NOVO: Inicializar e popular o map de prioridades de itens
        itemPrioridadesPorLocal = new HashMap<>();
        popularPrioridadesItensPorLocal();
    }

    private void inicializarLocaisBase() {
        // Locais fixos
        localDia1Fixo = new Local("Distrito Residencial - Bairro Esperança", "As casas estão vazias, portas rangem com o vento e o silêncio é quase perturbador. A sensação é de que a qualquer momento algo pode espreitar das sombras.", 4);
        localDia1Fixo.addSubLocal(new SubLocal("Garagem Velha", "Pode ter ferramentas ou esconderijos.", 3));
        localDia1Fixo.addSubLocal(new SubLocal("Carro de Luxo Abandonado", "Talvez itens de valor, mas arriscado.", 5));
        localDia1Fixo.addSubLocal(new SubLocal("Parque Infantil Destruído", "Restos de brinquedos, fácil de ver, mas barulhento.", 2));
        locaisBase.add(localDia1Fixo);

        localDia7Fixo = new Local("Ponto Alfa - Ponto Alfa", "O ambiente transmite tensão imediata: cada passo parece ecoar mais alto, como se fosse impossível permanecer despercebido.", 10);
        locaisBase.add(localDia7Fixo);

        // Outros locais (não fixos)
        Local supermercado = new Local("Supermercado", "Comida e suprimentos.", 4);
        supermercado.addSubLocal(new SubLocal("Corredor de Alimentos", "Prateleiras vazias, restos de embalagens.", 3));
        supermercado.addSubLocal(new SubLocal("Estoque dos Fundos", "Caixas empilhadas, pouca luz.", 5));
        supermercado.addSubLocal(new SubLocal("Caixas Registradoras", "Dinheiro espalhado, alarmes quebrados.", 4));
        locaisBase.add(supermercado);

        Local hospital = new Local("Hospital", "Remédios, muitos zumbis.", 7);
        hospital.addSubLocal(new SubLocal("Recepção", "Mesas viradas, papéis espalhados.", 6));
        hospital.addSubLocal(new SubLocal("Farmácia Hospitalar", "Armários de remédios, mas com risco.", 7));
        hospital.addSubLocal(new SubLocal("Enfermaria", "Camas vazias, sangue seco.", 8));
        locaisBase.add(hospital);

        Local delegacia = new Local("Delegacia", "Possível encontrar armas.", 5);
        delegacia.addSubLocal(new SubLocal("Sala de Armas", "Armários de armas, mas trancados.", 7));
        delegacia.addSubLocal(new SubLocal("Celas", "Portas abertas, cheiro de decomposição.", 6));
        delegacia.addSubLocal(new SubLocal("Escritórios", "Documentos, mesas viradas.", 4));
        locaisBase.add(delegacia);

        Local casaAbandonada = new Local("Casa Abandonada", "Abrigo temporário.", 3);
        casaAbandonada.addSubLocal(new SubLocal("Cozinha", "Restos de comida, louça quebrada.", 2));
        casaAbandonada.addSubLocal(new SubLocal("Quartos", "Roupas espalhadas, móveis revirados.", 3));
        casaAbandonada.addSubLocal(new SubLocal("Jardim", "Vegetação alta, ferramentas de jardim.", 1));
        locaisBase.add(casaAbandonada);

        Local farmacia = new Local("Farmácia", "Remédios e curativos.", 4);
        farmacia.addSubLocal(new SubLocal("Prateleiras de Remédios", "Caixas vazias, alguns remédios intactos.", 3));
        farmacia.addSubLocal(new SubLocal("Fundos da Farmácia", "Depósito pequeno, talvez algo útil.", 4));
        farmacia.addSubLocal(new SubLocal("Consultório Médico Abandonado", "Maca e equipamentos médicos revirados. Possibilidade de encontrar medicamentos mais raros.", 5));
        locaisBase.add(farmacia);

        Local postoDeGasolina = new Local("Posto de Gasolina", "Risco de explosão.", 6);
        postoDeGasolina.addSubLocal(new SubLocal("Loja de Conveniência", "Comida, bebida, pilhas.", 5));
        postoDeGasolina.addSubLocal(new SubLocal("Bombas de Combustível", "Perigoso, mas pode haver combustível.", 7));
        postoDeGasolina.addSubLocal(new SubLocal("Oficina de Reparos", "Ferramentas espalhadas, veículos danificados. Pode haver peças ou combustível.", 6));
        locaisBase.add(postoDeGasolina);

        Local escola = new Local("Escola", "Recursos e perigos.", 5);
        escola.addSubLocal(new SubLocal("Salas de Aula", "Livros, mochilas, talvez material escolar.", 4));
        escola.addSubLocal(new SubLocal("Biblioteca", "Livros, mapas, talvez informações.", 5));
        escola.addSubLocal(new SubLocal("Quadra Esportiva", "Espaço aberto, bom para visibilidade.", 3));
        locaisBase.add(escola);

        Local igreja = new Local("Igreja", "Mais seguro de dia.", 3);
        igreja.addSubLocal(new SubLocal("Santuário Silencioso", "Bancos revirados, vitrais quebrados.", 2));
        igreja.addSubLocal(new SubLocal("Cripta", "Escuro, úmido, talvez algo escondido.", 6));
        igreja.addSubLocal(new SubLocal("Confessionário", "Pequeno e claustrofóbico, com bancos empoeirados. Pode haver segredos ou itens escondidos.", 3));
        locaisBase.add(igreja);

        Local deposito = new Local("Depósito", "Itens úteis.", 5);
        deposito.addSubLocal(new SubLocal("Setor de Ferramentas", "Martelos, chaves, ferramentas variadas.", 4));
        deposito.addSubLocal(new SubLocal("Setor de Embalagens", "Caixas vazias, esconderijos.", 3));
        deposito.addSubLocal(new SubLocal("Área de Carga e Descarga", "Plataformas e empilhadeiras enferrujadas. Pode haver caixas esquecidas.", 4));
        locaisBase.add(deposito);

        Local baseMilitar = new Local("Base Militar", "Perigoso, ótimos recursos.", 8);
        baseMilitar.addSubLocal(new SubLocal("Arsenal", "Muitas armas, mas fortemente guardado.", 9));
        baseMilitar.addSubLocal(new SubLocal("Quartel", "Camas, pertences de soldados.", 7));
        baseMilitar.addSubLocal(new SubLocal("Torre de Observação", "Visão privilegiada da área, mas exposta. Possibilidade de encontrar equipamentos de comunicação ou armas leves.", 8));
        locaisBase.add(baseMilitar);
    }

    // NOVO: Método para popular o map de prioridades de itens por local
    private void popularPrioridadesItensPorLocal() {
        // Hospital: Medicamentos
        itemPrioridadesPorLocal.put("Hospital", List.of(1, 1, 2, 2, 3, 3, 9, 9, 11, 11));
        // Farmácia: Medicamentos
        itemPrioridadesPorLocal.put("Farmácia", List.of(1, 1, 2, 2, 3, 3, 9, 9, 11, 11, 15, 15)); // Inclui Kit de Primeiros Socorros
        // Base Militar: Armas, Munição, Explosivos
        itemPrioridadesPorLocal.put("Base Militar", List.of(5, 6, 7, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 14, 15)); // Inclui Granada
        // Delegacia: Armas, Munição
        itemPrioridadesPorLocal.put("Delegacia", List.of(5, 6, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8));
        // Supermercado: Comida, Ração de Combate
        itemPrioridadesPorLocal.put("Supermercado", List.of(0, 10));
        // Depósito: Armas (Faca), Armaduras, Lanterna Tática
        itemPrioridadesPorLocal.put("Depósito", List.of(4, 5, 8, 8, 8, 8, 8, 8, 12, 13));
        // Carro de Luxo Abandonado: Itens de valor (Pistola, Rifle)
        itemPrioridadesPorLocal.put("Carro de Luxo Abandonado", List.of(5, 7));
        // Garagem Velha: Ferramentas (Faca, Isqueiro e Spray)
        itemPrioridadesPorLocal.put("Garagem Velha", List.of(4, 14));
    }

    private void sortearLocaisDoDia() {
        // Locais do Dia 1 e Dia 7 são fixos
        if (diaAtual == 1) {
            locaisDoDia = List.of(localDia1Fixo); // Apenas o local fixo do dia 1
            localAtual = localDia1Fixo;
            locaisVisitados.add(localDia1Fixo); // Adicionar à lista de locais visitados
        } else if (diaAtual == 7) {
            locaisDoDia = List.of(localDia7Fixo); // Apenas o local fixo do dia 7
            localAtual = localDia7Fixo;
            locaisVisitados.add(localDia7Fixo); // Adicionar à lista de locais visitados
        } else {
            // Para dias intermediários, sortear 3 locais (excluindo os fixos E os já visitados)
            List<Local> locaisDisponiveis = new ArrayList<>(locaisBase);
            locaisDisponiveis.remove(localDia1Fixo);
            locaisDisponiveis.remove(localDia7Fixo);
            locaisDisponiveis.removeAll(locaisVisitados); // Remover locais já visitados

            Collections.shuffle(locaisDisponiveis, new Random());
            locaisDoDia = new ArrayList<>(locaisDisponiveis.subList(0, Math.min(3, locaisDisponiveis.size())));
            localAtual = null; // Definir como null para que o jogador precise selecionar o local
        }
    }

    public void iniciarNovoJogo(Personagem jogador) {
        this.jogador = jogador;
        this.diaAtual = 1;
        this.periodoAtual = PeriodoDia.MANHA;
        this.emCombate = false;
        this.jogoTerminado = false;
        this.jogoVencido = false;
        this.eventoFinalAtivo = false;
        this.emSelecaoSubLocal = false; // Inicializa como falso
        this.subLocalAtual = null; // Inicializa como nulo
        this.resumoDiaAnterior = ""; // Inicializa com string vazia
        this.locaisVisitados.clear(); // Limpa a lista de locais visitados ao iniciar um novo jogo
        this.sublocaisVisitadosPorLocal.clear(); // Limpa o mapa de sublocais visitados ao iniciar um novo jogo

        // RESETAR CONTROLES
        this.exploracoesDiaAtual = 0; // Reseta o contador de explorações

        this.mensagem =  obterDescricaoLocalDoDia(1) + "\nO que você faz?";

        if (jogador.getInventario() != null) {
            int cap = 3 + Math.max(0, jogador.getConstituicao());
            jogador.getInventario().setCapacidade(cap);
        }

        jogador.setSentimento("Determinado"); // Define o sentimento inicial do jogador
    }

    public void explorarLocal() {
        explorar();
    }

    public Map<String, Object> explorar() {
        Map<String, Object> resultados = new HashMap<>();
        resultados.put("combateIniciado", false);
        resultados.put("selecaoSubLocalIniciada", false);
        resultados.put("itemEncontrado", null);
        resultados.put("mensagemStatus", null); // Para mensagens de erro/estado

        System.out.println("DEBUG: explorar() - Início. Dia: " + diaAtual + ", Período: " + periodoAtual + ", Explorações: " + exploracoesDiaAtual); // DEBUG

        if (eventoFinalAtivo) {
            resultados.put("mensagemStatus", "Dia 7 - Evento Final: Não é possível explorar. Enfrente o Boss!");
            return resultados;
        }
        if (emCombate) {
            resultados.put("mensagemStatus", "Você está em combate! Não pode explorar agora.");
            return resultados;
        }

        // Verificar limite de explorações diárias
        if (exploracoesDiaAtual >= 3) {
            setMensagem("Você já explorou 3 vezes neste dia. Agora você deve dormir para avançar para o próximo dia.");
            resultados.put("mensagemStatus", getMensagem());
            return resultados;
        }

        if (diaAtual > 10) {
            resultados.put("mensagemStatus", "Você já completou os 10 dias!");
            return resultados;
        }

        // Se o local atual tem sublocais, entrar em modo de seleção de sublocal
        if (!localAtual.getSubLocais().isEmpty()) {
            this.emSelecaoSubLocal = true;
            this.subLocalAtual = null; // Resetar subLocalAtual ao entrar em seleção de sublocal
            resultados.put("selecaoSubLocalIniciada", true);
            setMensagem("Você chegou em " + localAtual.getNome() + ". Escolha uma área para explorar:");
            System.out.println("DEBUG: explorar() - Seleção de sublocal iniciada. Período: " + periodoAtual + ", Explorações: " + exploracoesDiaAtual); // DEBUG
            return resultados;
        }
        
        // EXPLORAÇÃO NORMAL (se não tiver sublocais)
        localAtual.explorar();
        resultados.put("localExplorado", localAtual.getNome());
        resultados.put("periodo", periodoAtual.getRotulo());

        // VERIFICAR SE ENCONTROU ZUMBIS
        if (encontrarZumbis()) {
            resultados.put("combateIniciado", true);
            // Não marcar período como explorado se encontrou zumbis - será feito após o combate
            this.emSelecaoSubLocal = false; // Resetar após exploração normal (se iniciou combate)
            System.out.println("DEBUG: explorar() - Combate iniciado. Período: " + periodoAtual + ", Explorações: " + exploracoesDiaAtual); // DEBUG
            
            // NOVO: Incrementar explorações e avançar período AQUI, mesmo com combate
            // exploracoesDiaAtual++; // REMOVIDO: Evita dupla contagem
            // avancarPeriodo(); // REMOVIDO: Evita dupla contagem
            // System.out.println("DEBUG: explorar() - Após avançar período (combate). Período: " + periodoAtual + ", Explorações: " + exploracoesDiaAtual); // DEBUG
            
            return resultados;
        }

        // VERIFICAR SE ENCONTROU ITEM
        Item itemEncontrado = encontrarItem(true);
        if (itemEncontrado != null) {
            resultados.put("itemEncontrado", itemEncontrado);
            jogador.atualizarSentimentoJogador(1); // Encontrar item bom, sentimento positivo
        }
        
        // As explorações e o avanço de período serão tratados pelo ControladorJogo após a ação ser concluída.
        System.out.println("DEBUG: explorar() - Após avançar período. Período: " + periodoAtual + ", Explorações: " + exploracoesDiaAtual); // DEBUG
        this.emSelecaoSubLocal = false; // Resetar após exploração normal
        return resultados;
    }

    public void concluirExploracao() {
        exploracoesDiaAtual++;
        avancarPeriodo();
        this.emSelecaoSubLocal = false;
    }

    // CORRIGIDO: Método para marcar exploração como concluída E avançar período
    public void marcarExploracaoConcluida() {
        exploracoesDiaAtual++;
        avancarPeriodo(); // CORRIGIDO: Fuga deve avançar o período!
        System.out.println("DEBUG: marcarExploracaoConcluida() - Após avançar período. Período: " + periodoAtual + ", Explorações: " + exploracoesDiaAtual); // DEBUG
        this.emSelecaoSubLocal = false;
    }

    public Map<String, Object> explorarSubLocal(SubLocal subLocal) {
        Map<String, Object> resultados = new HashMap<>();
        resultados.put("combateIniciado", false);
        resultados.put("itemEncontrado", null);
        resultados.put("mensagemStatus", null);

        this.subLocalAtual = subLocal; // Definir o sublocal atual imediatamente
        System.out.println("DEBUG: explorarSubLocal() - Início. Dia: " + diaAtual + ", Período: " + periodoAtual + ", Explorações: " + exploracoesDiaAtual); // DEBUG

        if (eventoFinalAtivo || emCombate || subLocal == null) {
            resultados.put("mensagemStatus", "Não é possível explorar o sublocal agora.");
            return resultados;
        }

        // Verificar limite de explorações diárias para sublocal
        if (exploracoesDiaAtual >= 3) {
            setMensagem("Você já explorou 3 vezes neste dia. Agora você deve dormir para avançar para o próximo dia.");
            resultados.put("mensagemStatus", getMensagem());
            return resultados;
        }

        // Lógica de exploração do sublocal continua (geração de zumbis/itens)
        resultados.put("subLocalExplorado", subLocalAtual.getNome());
        resultados.put("localExplorado", localAtual.getNome());
        resultados.put("periodo", periodoAtual.getRotulo());

        // VERIFICAR SE ENCONTROU ZUMBIS NO SUBLOCAL
        if (encontrarZumbisSubLocal()) {
            resultados.put("combateIniciado", true);
            this.emSelecaoSubLocal = false; // Resetar após exploração de sublocal (se iniciou combate)
            System.out.println("DEBUG: explorarSubLocal() - Combate iniciado. Período: " + periodoAtual + ", Explorações: " + exploracoesDiaAtual); // DEBUG
            
            // NOVO: Incrementar explorações e avançar período AQUI, mesmo com combate
            // exploracoesDiaAtual++; // REMOVIDO: Evita dupla contagem
            // avancarPeriodo(); // REMOVIDO: Evita dupla contagem
            // System.out.println("DEBUG: explorarSubLocal() - Após avançar período (combate). Período: " + periodoAtual + ", Explorações: " + exploracoesDiaAtual); // DEBUG
            
            return resultados;
        }

        // VERIFICAR SE ENCONTROU ITEM NO SUBLOCAL
        Item itemEncontrado = encontrarItem(true); // Usamos true porque é exploração, não combate
        if (itemEncontrado != null) {
            resultados.put("itemEncontrado", itemEncontrado);
            jogador.atualizarSentimentoJogador(1); // Encontrar item bom, sentimento positivo
        }
        
        // As explorações e o avanço de período serão tratados pelo ControladorJogo após a ação ser concluída.
        System.out.println("DEBUG: explorarSubLocal() - Após avançar período. Período: " + periodoAtual + ", Explorações: " + exploracoesDiaAtual); // DEBUG
        this.emSelecaoSubLocal = false; // Resetar após exploração de sublocal
         return resultados;
     }

    public boolean encontrarZumbisSubLocal() {
        // Gerar zumbis baseado no sublocal atual, dia e nível de perigo
        inimigosAtuais = gerarZumbisPorDificuldade(diaAtual, subLocalAtual.getNivelPerigo());
        if (!inimigosAtuais.isEmpty()) {
            emCombate = true;
            return true;
        }
        return false;
    }

    public boolean encontrarZumbis() {
        // Gerar zumbis baseado no dia atual e nível de perigo do local
        inimigosAtuais = gerarZumbisPorDificuldade(diaAtual, localAtual.getNivelPerigo());
        if (!inimigosAtuais.isEmpty()) {
            emCombate = true;
            return true;
        }
        return false;
    }
    
    private List<Zumbi> gerarZumbisPorDificuldade(int dia, int nivelPerigoAtual) {
        List<Zumbi> zumbis = new ArrayList<>();
        Random random = new Random();
        int chanceEncontroBase = 50; // Chance base de encontrar zumbis
        int maxZumbis = 1; // Número máximo de zumbis

        // Ajustar chance e maxZumbis com base no dia e nivelPerigoAtual
        chanceEncontroBase += (dia - 1) * 5; // Aumenta 5% por dia
        chanceEncontroBase += nivelPerigoAtual * 2; // Aumenta 2% por nível de perigo

        maxZumbis += (dia - 1) / 2; // Máximo de zumbis aumenta a cada 2 dias
        maxZumbis += nivelPerigoAtual / 3; // Máximo de zumbis aumenta a cada 3 níveis de perigo
        maxZumbis = Math.min(maxZumbis, 5); // Limitar o número máximo de zumbis

        // Encontro fixo para o Boss Final no Dia 7
        if (dia == 7) {
            zumbis.add(new BossFinal());
            return zumbis;
        }

        // Chance geral de encontrar zumbis
        if (random.nextInt(100) < chanceEncontroBase) {
            int numZumbis = 1 + random.nextInt(maxZumbis); // Pelo menos 1, até maxZumbis

            for (int i = 0; i < numZumbis; i++) {
                // Determinar tipo de zumbi com base no dia e nível de perigo
                int tipoZumbiChance = random.nextInt(100);

                if (dia == 1) { // Dia 1: Apenas Zumbi Comum
                    zumbis.add(new ZumbiComum());
                } else if (dia == 2) { // Dia 2: Comuns e Corredores
                    if (tipoZumbiChance < 60) { // 60% comum
                        zumbis.add(new ZumbiComum());
                    } else { // 40% corredor
                        zumbis.add(new ZumbiCorredor());
                    }
                } else if (dia == 3) { // Dia 3: Tóxicos, Comuns, Corredores
                    if (tipoZumbiChance < 40) { // 40% comum
                        zumbis.add(new ZumbiComum());
                    } else if (tipoZumbiChance < 70) { // 30% corredor
                        zumbis.add(new ZumbiCorredor());
                    } else { // 30% tóxico
                        zumbis.add(new ZumbiToxico());
                    }
                } else if (dia == 4) { // Dia 4: Tanques, Kamikazes, Tóxicos
                    if (tipoZumbiChance < 30) { // 30% toxico
                        zumbis.add(new ZumbiToxico());
                    } else if (tipoZumbiChance < 60) { // 30% tanque
                        zumbis.add(new ZumbiTanque());
                    } else { // 40% kamikaze
                        zumbis.add(new ZumbiKamikaze());
                    }
                } else if (dia == 5) { // Dia 5: Hordas de diferentes tipos
                    if (tipoZumbiChance < 20) { // 20% comum
                        zumbis.add(new ZumbiComum());
                    } else if (tipoZumbiChance < 40) { // 20% corredor
                        zumbis.add(new ZumbiCorredor());
                    } else if (tipoZumbiChance < 60) { // 20% toxico
                        zumbis.add(new ZumbiToxico());
                    } else if (tipoZumbiChance < 80) { // 20% tanque
                        zumbis.add(new ZumbiTanque());
                    } else { // 20% cachorro ou estrategista (mais raros)
                        if (random.nextBoolean()) {
                            zumbis.add(new ZumbiCachorro());
                        } else {
                            zumbis.add(new ZumbiEstrategista());
                        }
                    }
                } else if (dia == 6) { // Dia 6: Preparação para o boss, zumbis mais fortes
                    if (tipoZumbiChance < 25) { // 25% tanque
                        zumbis.add(new ZumbiTanque());
                    } else if (tipoZumbiChance < 50) { // 25% toxico
                        zumbis.add(new ZumbiToxico());
                    } else if (tipoZumbiChance < 75) { // 25% estrategista
                        zumbis.add(new ZumbiEstrategista());
                    } else { // 25% noturno (mais perigoso)
                        zumbis.add(new ZumbiNoturno());
                    }
                }
            }
        }
        
        return zumbis;
    }

    public Item encontrarItem(boolean isExploracao) {
        Random random = new Random();
        int chanceEncontrar = isExploracao ? 85 : 90; // 75% para exploração, 85% para pós-combate

        if (random.nextInt(100) < chanceEncontrar) {
            // Gerar itens baseados no dia/local (agora inclui todas as armas através de gerarItemDoDia())
            Item item = gerarItemDoDia(!isExploracao); // Passar se é pós-combate
            if (item != null && jogador.adicionarItem(item)) {
                return item;
            }
        }
        return null; // Não encontrou item ou inventário cheio
    }

    private Item gerarArmaAleatoria() {
        Random random = new Random();
        int tipo = random.nextInt(100);

        if (tipo < 50) {
            return new br.com.pedrodamasceno.model.itens.Arma("Faca", "Faca afiada de cozinha", 15, 15, 5);
        } else if (tipo < 80) {
            return new br.com.pedrodamasceno.model.itens.ArmaDeFogo("Pistola", "Pistola semi-automática", 30, 12, 8, 10); // Arma de Fogo com munição
        } else {
            return new br.com.pedrodamasceno.model.itens.ArmaDeFogo("Rifle", "Rifle de precisão", 50, 18, 5, 5); // Arma de Fogo com munição
        }
    }
    
    private Item gerarItemDoDia(boolean isPosCombate) { // Adicionar parâmetro isPosCombate
        Random random = new Random();
        int tipoItem = random.nextInt(18); // Aumentado para 18 para incluir novas chances de armas

        if (isPosCombate && random.nextInt(100) < 40) { // 50% de chance de priorizar itens de cura no pós-combate
            // Sortear especificamente entre os itens de cura
            int tipoCura = random.nextInt(6); // 6 tipos de itens de cura (Medicamento P, M, G, Antídoto, Ração, Analgésico)
            switch (tipoCura) {
                case 0: return new br.com.pedrodamasceno.model.itens.Medicamento("Medicamento de Cura Pequeno", "Cura 20 HP", 20);
                case 1: return new br.com.pedrodamasceno.model.itens.Medicamento("Medicamento de Cura Médio", "Cura 40 HP", 40);
                case 2: return new br.com.pedrodamasceno.model.itens.Medicamento("Medicamento de Cura Grande", "Cura 60 HP", 60);
                case 3: return new br.com.pedrodamasceno.model.itens.Antidoto("Antídoto", "Remove efeitos de veneno e cura 10 HP.", 10);
                case 4: return new br.com.pedrodamasceno.model.itens.RacaoCombate("Ração de Combate", "Cura 25 HP e aumenta Força por 2 turnos.", 5, 25, 5, 2);
                case 5: return new br.com.pedrodamasceno.model.itens.AnalgesicoForte("Analgésico Forte", "Cura 30 HP e remove cansaço/medo.", 30);
            }
        }

        // Lógica para priorizar munição na Delegacia e Base Militar de forma mais agressiva
        String nomeLocalParaPrioridade = (subLocalAtual != null) ? subLocalAtual.getNome() : (localAtual != null ? localAtual.getNome() : null);
        System.out.println("DEBUG: gerarItemDoDia() - Local para prioridade: " + nomeLocalParaPrioridade); // DEBUG
        String nomeDoLocalPrincipal = (localAtual != null) ? localAtual.getNome() : null; // Novo: Obtém o nome do local principal
        if (nomeDoLocalPrincipal != null) { // Verifica o nome do local principal
            if (nomeDoLocalPrincipal.equals("Delegacia") || nomeDoLocalPrincipal.equals("Base Militar")) {
                int chanceMuni = random.nextInt(100);
                System.out.println("DEBUG: gerarItemDoDia() - Local crítico (Principal: " + nomeDoLocalPrincipal + "), chance de munição: " + chanceMuni); // DEBUG
                if (chanceMuni < 60) { // 71% de chance de ser munição para atingir ~50% total
                    System.out.println("DEBUG: gerarItemDoDia() - Munição gerada (71% de chance)."); // DEBUG
                    return new br.com.pedrodamasceno.model.itens.Item("Munição", "Munição para armas de fogo", br.com.pedrodamasceno.model.itens.TipoItem.MUNICAO, 10);
                } else {
                    System.out.println("DEBUG: gerarItemDoDia() - 71% de chance de munição falhou. Cairá na lógica de prioridade do local."); // DEBUG
                }
            }
        }

        // Lógica existente para priorizar itens com base no local atual (usada se a lógica acima não for acionada ou falhar)
        if (nomeLocalParaPrioridade != null && itemPrioridadesPorLocal.containsKey(nomeLocalParaPrioridade)) {
            List<Integer> prioridades = itemPrioridadesPorLocal.get(nomeLocalParaPrioridade);
            // Dar uma chance maior de sortear um item da lista de prioridades
            if (random.nextInt(100) < 70 && !prioridades.isEmpty()) { // 70% de chance de priorizar
                tipoItem = prioridades.get(random.nextInt(prioridades.size()));
                System.out.println("DEBUG: gerarItemDoDia() - Item da lista de prioridades sorteado: ID " + tipoItem); // DEBUG
            } else {
                tipoItem = random.nextInt(16); // Sorteio normal se não priorizar
                System.out.println("DEBUG: gerarItemDoDia() - Item aleatório sorteado: ID " + tipoItem); // DEBUG
            }
        } else {
            tipoItem = random.nextInt(16); // Sorteio totalmente aleatório
            System.out.println("DEBUG: gerarItemDoDia() - Item totalmente aleatório sorteado: ID " + tipoItem); // DEBUG
        }

        // Removendo o antigo `int tipoItem = random.nextInt(16);` para evitar conflito
        // int tipoItem = random.nextInt(16); // Aumentado para 16 para incluir novos itens
        
        switch (tipoItem) {
            case 0: return new br.com.pedrodamasceno.model.itens.Item("Comida Enlatada", "Comida não perecível", br.com.pedrodamasceno.model.itens.TipoItem.COMIDA, 15);
            case 1: return new br.com.pedrodamasceno.model.itens.Medicamento("Medicamento de Cura Pequeno", "Cura 20 HP", 20);
            case 2: return new br.com.pedrodamasceno.model.itens.Medicamento("Medicamento de Cura Médio", "Cura 40 HP", 40);
            case 3: return new br.com.pedrodamasceno.model.itens.Medicamento("Medicamento de Cura Grande", "Cura 60 HP", 60);
            case 4: return new br.com.pedrodamasceno.model.itens.Arma("Faca", "Arma branca simples", 15, 15, 5);
            case 5: return new ArmaDeFogo("Revólver", "Arma de fogo básica", 30, 12, 20, 8); // Arma de fogo com munição e durabilidade
            case 6: return new ArmaDeFogo("Espingarda", "Curto alcance", 45, 18, 18, 5); // Arma de fogo com munição e durabilidade
            case 7: return new ArmaDeFogo("Rifle", "Longo alcance", 60, 22, 15, 5); // Arma de fogo com munição e durabilidade
            case 8: return new br.com.pedrodamasceno.model.itens.Item("Munição", "Munição para armas de fogo", br.com.pedrodamasceno.model.itens.TipoItem.MUNICAO, 10);
            case 9: return new br.com.pedrodamasceno.model.itens.Antidoto("Antídoto", "Remove efeitos de veneno e cura 10 HP.", 10); // Novo item
            case 10: return new br.com.pedrodamasceno.model.itens.RacaoCombate("Ração de Combate", "Cura 25 HP e aumenta Força por 2 turnos.", 5, 25, 3, 2); // Novo item
            case 11: return new br.com.pedrodamasceno.model.itens.AnalgesicoForte("Analgésico Forte", "Cura 30 HP e remove cansaço/medo.", 30); // Novo item
            case 12: return new br.com.pedrodamasceno.model.itens.Armadura("Colete Leve", "Oferece proteção básica.", 20, 5, 15); // Novo item
            case 13: return new ArmaDeFogo("Rifle", "Longo alcance", 60, 22, 15, 5); // Arma de fogo com munição e durabilidade
            case 14: return new br.com.pedrodamasceno.model.itens.IsqueiroESpray("Isqueiro e Spray", "Causa 20 de dano em área no combate.", 15, 20); // Novo item
            case 15: // Granada ou Kit de Primeiros Socorros
                if (diaAtual == 6) {
                    return new br.com.pedrodamasceno.model.itens.Item("Granada", "Explosivo de uso único (30 de dano em área)", br.com.pedrodamasceno.model.itens.TipoItem.EXPLOSIVO, 30);
                } else {
                    return new br.com.pedrodamasceno.model.itens.Medicamento("Kit de Primeiros Socorros", "Cura ferimentos", 30);
                }
            case 16: return new ArmaDeFogo("Revólver", "Arma de fogo básica", 30, 12, 20, 8); // Chance duplicada de Revólver
            case 17: return new ArmaDeFogo("Rifle", "Longo alcance", 60, 22, 15, 5); // Chance duplicada de Rifle
            case 18: return new ArmaDeFogo("Espingarda", "Curto alcance", 45, 18, 18, 5); // Arma de fogo com munição e durabilidade
            default: return new br.com.pedrodamasceno.model.itens.Item("Água", "Água potável", br.com.pedrodamasceno.model.itens.TipoItem.COMIDA, 10);
        }
    }

    public void dormir() {
        if (eventoFinalAtivo) { setMensagem("Dia 7 - Evento Final: Não é possível dormir durante o evento final."); return; }
        if (emCombate) { setMensagem("Você está em combate! Não pode dormir agora."); return; }

        if (dormiu) { setMensagem("Você já dormiu hoje!"); return; }

        if (exploracoesDiaAtual < 3) { setMensagem("Você precisa explorar 3 vezes antes de dormir!"); return; }

        System.out.println("DEBUG: dormir() - Início. Dia: " + diaAtual + ", Período: " + periodoAtual + ", Explorações: " + exploracoesDiaAtual); // DEBUG
        dormiu = true;
        
        // Recuperar saúde ao dormir
        int saudeAntes = jogador.getSaude();
        jogador.curar(jogador.getSaudeMaxima() / 3); // Recupera 1/3 da saúde máxima
        int saudeRecuperada = jogador.getSaude() - saudeAntes;
        
        // Recuperar cargas das habilidades ao dormir
        jogador.setCargasHabilidade1(2); // HAB1 volta para 2 cargas
        jogador.setCargasHabilidade2(1); // HAB2 volta para 1 carga
        
        // NOVO: Remover todos os StatusEffect temporários do jogador ao dormir
        jogador.removerTodosEfeitosTemporarios();

        // Construir resumo do dia anterior
        StringBuilder resumo = new StringBuilder();
        resumo.append("Fim do Dia ").append(diaAtual).append("!\n");
        resumo.append("Saúde recuperada: ").append(saudeRecuperada).append(" HP.\n");
        // Mensagem da noite baseada no sentimento
        resumo.append(gerarMensagemNoite(jogador.getSentimento()));
        this.resumoDiaAnterior = resumo.toString();
        
        // Avançar automaticamente para o próximo dia
        avancarParaProximoDia();
        System.out.println("DEBUG: dormir() - Após avançar para próximo dia. Dia: " + diaAtual + ", Período: " + periodoAtual + ", Explorações: " + exploracoesDiaAtual); // DEBUG
        
        // Aplicar evento especial do dia (mensagem será adicionada ao log no ControladorJogo)
        aplicarEventoEspecialDoDia();
        // A mensagem de dormir será apenas informativa, sem incluir o lembrete de seleção de local
        String mensagemDormir = "Você dormiu e recuperou " + saudeRecuperada + " de saúde e suas habilidades. Pronto para o próximo dia!";
        setMensagem(mensagemDormir);
        this.emSelecaoSubLocal = false; // Sair do modo de seleção de sublocal após dormir
    }
    
    private void aplicarEventoEspecialDoDia() {
        // Aplicar evento especial baseado no dia
        if (diaAtual == 5) {
            setMensagem(getMensagem() + "\n\nEVENTO ESPECIAL: Os zumbis estão mais agressivos!");
        } else if (diaAtual == 7) {
            setMensagem(getMensagem() + "\n\nEVENTO ESPECIAL: Uma tempestade se aproxima, limitando a visibilidade!");
        } else if (diaAtual == 9) {
            setMensagem(getMensagem() + "\n\nEVENTO ESPECIAL: O Zumbi Alfa está próximo! Prepare-se para o confronto final!");
        }
    }
    
    private void avancarParaProximoDia() {
        // AVANÇAR PARA O PRÓXIMO DIA
        diaAtual++;
        jogador.aumentarDia();

        // RESETAR CONTROLES PARA O NOVO DIA
        periodoAtual = PeriodoDia.MANHA;
        dormiu = false;
        exploracoesDiaAtual = 0; // Reseta as explorações para o novo dia
        this.emSelecaoSubLocal = false; // Resetar para o próximo dia
        System.out.println("DEBUG: avancarParaProximoDia() - Início. Dia: " + diaAtual + ", Período: " + periodoAtual + ", Explorações: " + exploracoesDiaAtual); // DEBUG

        if (diaAtual == 7) {
            // Ativar evento final (Dia 7) - sem períodos/exploração
            eventoFinalAtivo = true;
            setMensagem("Dia 7 - Evento Final: O Boss final se aproxima! Prepare-se para o confronto.");
        } else if (!jogador.estaVivo()) {
            jogoTerminado = true;
            setMensagem("Você morreu! Fim de jogo.");
        } else {
            sortearLocaisDoDia(); // Sortear novos locais para o dia
            // A mensagem para o novo dia será construída e exibida pela TelaPrincipal
            // se (diaAtual > 1 && diaAtual < 7) { ... }
            // A mensagem do modelo será atualizada pela TelaPrincipal com o que for mais relevante.
        }
    }
    
    public String obterNomeLocalDoDia(int dia) {
        return switch (dia) {
            case 1 -> "Distrito Residencial - Bairro Esperança";
            case 2 -> "Posto de Polícia - Patrulha Silenciosa";
            case 3 -> "Hospital - Santa Vida";
            case 4 -> "Fábrica Abandonada - Metalúrgica Norte";
            case 5 -> "Parque Industrial - Zona Sombria";
            case 6 -> "Base Militar - Fortaleza Eclipse";
            case 7 -> "Ponto Alfa - Ponto Alfa";
            default -> "Desconhecido";
        };
    }
    
    public String obterDescricaoLocalDoDia(int dia) {
        return switch (dia) {
            case 1 -> "As casas estão vazias, portas rangem com o vento e o silêncio é quase perturbador. A sensação é de que a qualquer momento algo pode espreitar das sombras.";
            case 2 -> "Documentos espalhados, rádios quebrados e marcas de luta sugerem que a rotina da lei terminou de forma abrupta e violenta.";
            case 3 -> "Corredores estreitos, cheiro de mofo e equipamentos médicos enferrujados criam uma atmosfera sufocante, onde cada som ecoa como um aviso.";
            case 4 -> "Máquinas enferrujadas, esteiras paradas e o som metálico ocasional ecoam, como se o prédio tivesse memória própria.";
            case 5 -> "Galpões gigantes e estruturas abandonadas formam um labirinto industrial onde a escuridão domina cada canto.";
            case 6 -> "Fortificações imponentes, arame farpado e torres de vigia vazias passam a sensação de vigilância constante, mesmo sem ninguém por perto.";
            case 7 -> "O ambiente transmite tensão imediata: cada passo parece ecoar mais alto, como se fosse impossível permanecer despercebido.";
            default -> "Um lugar estranho, com uma atmosfera que causa desconforto imediato.";
        };
    }   

    public boolean todasExploracoesConcluidas() {
        return exploracoesDiaAtual >= 3;
    }

    public void avancarPeriodo() {
        // CORRIGIDO: NÃO avançar de NOITE para MANHÃ automaticamente
        // Apenas DORMIR pode fazer essa transição!
        if (periodoAtual == PeriodoDia.NOITE) {
            // Já está na NOITE - não avança mais até dormir
            return;
        }
        
        periodoAtual = periodoAtual.proximo();
        
        // Limpar sublocal atual se necessário (nunca chegará aqui para MANHÃ via avancarPeriodo)
        if (periodoAtual == PeriodoDia.MANHA) {
            subLocalAtual = null;
        }
        // A mensagem de avanço de período será gerada no ControladorJogo
    }

    public boolean podeDormir() {
        return !eventoFinalAtivo && !emCombate && exploracoesDiaAtual >= 3 && !dormiu;
    }

    public boolean podeExplorar() {
        return !eventoFinalAtivo && !emCombate && exploracoesDiaAtual < 3 && !dormiu;
    }

    public boolean podeSelecionarSubLocal() {
        return emSelecaoSubLocal && localAtual != null && !localAtual.getSubLocais().isEmpty();
    }

    // ===== GETTERS PARA A INTERFACE =====
    public boolean isDormiu() { return dormiu; }
    public int getExploracoesDiaAtual() { return exploracoesDiaAtual; }

    // ===== GETTERS/SETTERS EXISTENTES =====
    public Personagem getJogador() { return jogador; }
    public int getDiaAtual() { return diaAtual; }
    public PeriodoDia getPeriodoAtual() { return periodoAtual; }
    public boolean isEmCombate() { return emCombate; }
    public void setEmCombate(boolean emCombate) { this.emCombate = emCombate; }
    public Local getLocalAtual() { return localAtual; }
    public void setLocalAtual(Local localAtual) {
        this.localAtual = localAtual;
        System.out.println("DEBUG ModeloJogo: setLocalAtual - Local: " + localAtual.getNome() + " (HashCode: " + localAtual.hashCode() + ")");
        if (localAtual != null && !locaisVisitados.contains(localAtual)) {
            locaisVisitados.add(localAtual); // Adicionar local à lista de visitados
        }
    }
    public List<Local> getLocaisDoDia() { return new ArrayList<>(locaisDoDia); }

    // Novo método para obter locais disponíveis para seleção nos dias intermediários
    public List<Local> getLocaisDisponiveisParaSelecao() {
        List<Local> disponiveis = new ArrayList<>(locaisBase);
        disponiveis.remove(localDia1Fixo);
        disponiveis.remove(localDia7Fixo);
        disponiveis.removeAll(locaisVisitados); // Remover locais já visitados
        return disponiveis;
    }

    public List<Local> getLocaisBase() { return new ArrayList<>(locaisBase); }
    public List<Local> getLocais() { return getLocaisDoDia(); }
    public List<Zumbi> getInimigosAtuais() { return inimigosAtuais; }
    public void setInimigosAtuais(List<Zumbi> lista) { this.inimigosAtuais = lista; }
    public boolean isJogoTerminado() { return jogoTerminado; }
    public void setJogoTerminado(boolean jogoTerminado) { this.jogoTerminado = jogoTerminado; }
    public boolean isJogoVencido() { return jogoVencido; }
    public void setJogoVencido(boolean jogoVencido) { this.jogoVencido = jogoVencido; }
    public String getMensagem() { return mensagem; }
    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }
    public boolean isEventoFinalAtivo() { return eventoFinalAtivo; }
    public void setEventoFinalAtivo(boolean ativo) { this.eventoFinalAtivo = ativo; }
    public boolean isEmSelecaoSubLocal() { return emSelecaoSubLocal; }
    public SubLocal getSubLocalAtual() { return subLocalAtual; }
    public void setSubLocalAtual(SubLocal subLocalAtual) { this.subLocalAtual = subLocalAtual; }
    public String getResumoDiaAnterior() { return resumoDiaAnterior; }
    public void setResumoDiaAnterior(String resumoDiaAnterior) { this.resumoDiaAnterior = resumoDiaAnterior; }

    // NOVO: Métodos para rastrear sublocais visitados
    public void marcarSubLocalComoVisitado(Local local, SubLocal subLocal) {
        System.out.println("DEBUG ModeloJogo: marcarSubLocalComoVisitado - Local: " + local.getNome() + " (HashCode: " + local.hashCode() + "), SubLocal: " + subLocal.getNome() + " (HashCode: " + subLocal.hashCode() + ")");
        if (local != null && subLocal != null) {
            sublocaisVisitadosPorLocal.computeIfAbsent(local, k -> new ArrayList<>()).add(subLocal);
        }
    }

    public List<SubLocal> getSublocaisVisitados(Local local) {
        System.out.println("DEBUG ModeloJogo: getSublocaisVisitados - Local: " + local.getNome() + " (HashCode: " + local.hashCode() + ")");
        return sublocaisVisitadosPorLocal.getOrDefault(local, new ArrayList<>());
    }

    // NOVO: Métodos para o confronto final
    public void iniciarConfrontoFinal() {
        this.confrontoFinal = new ConfrontoFinal(jogador);
    }
    
    public ConfrontoFinal getConfrontoFinal() {
        return confrontoFinal;
    }
    
    public boolean isConfrontoFinalAtivo() {
        return confrontoFinal != null && confrontoFinal.isConfrontoIniciado() && !confrontoFinal.isConfrontoTerminado();
    }

    // NOVO: Método para gerar mensagens da noite com base no sentimento
    public String gerarMensagemNoite(String sentimento) {
        // Mensagem neutra para o final do dia, focando no descanso e esperança
        return "Você se preparou para descansar, na esperança de um dia melhor.\n";
    }

    // NOVO: Método para atualizar o sentimento do jogador (REMOVIDO - AGORA ESTÁ EM PERSONAGEM.JAVA)
    // public void atualizarSentimentoJogador(int intensidadeEvento) { ... }
}