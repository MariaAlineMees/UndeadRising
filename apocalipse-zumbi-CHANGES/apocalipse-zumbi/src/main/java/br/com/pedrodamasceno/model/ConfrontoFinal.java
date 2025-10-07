package br.com.pedrodamasceno.model;

import java.util.ArrayList;
import java.util.List;

import br.com.pedrodamasceno.model.personagens.Personagem;
import br.com.pedrodamasceno.model.personagens.SobreviventeRival;

public class ConfrontoFinal {
    private final Personagem jogador;
    private final SobreviventeRival rival;
    private List<String> escolhasAtuais;
    private String faseAtual; // Sistema de fases baseado em strings
    private boolean confrontoIniciado;
    private boolean confrontoTerminado;
    private FinalType tipoFinal;

    public enum FinalType {
        SACRIFICIO_PROPRIO,
        SACRIFICIO_RIVAL, 
        ACORDO_PACIFICO,
        MORTE_MUTUA,
        SOBREVIVENCIA_CONJUNTA
    }

    public ConfrontoFinal(Personagem jogador) {
        this.jogador = jogador;
        this.rival = new SobreviventeRival();
        this.escolhasAtuais = new ArrayList<>();
        this.faseAtual = "inicio";
        this.confrontoIniciado = false;
        this.confrontoTerminado = false;
        inicializarEscolhasIniciais();
    }

    private void inicializarEscolhasIniciais() {
        escolhasAtuais.clear();
        escolhasAtuais.add("1. [Empatia] 'Eu entendo sua situação. Podemos conversar sobre isso?'");
        escolhasAtuais.add("2. [Ameaça] 'Abaixe essa arma. Não quero machucá-la.'");
        escolhasAtuais.add("3. [Negociação] 'Talvez possamos encontrar outra solução.'");
        escolhasAtuais.add("4. [Ataque] Tentar desarmar Ana rapidamente.");
    }

    public String iniciarConfronto() {
        confrontoIniciado = true;
        faseAtual = "inicio";
        return "O helicóptero se aproxima ao longe. Seus rotores cortam o ar da noite, trazendo esperança... e um dilema moral.\n\n" +
               "Ana: [Apontando a arma] Pare! Só há uma vaga no helicóptero... e eu preciso dela.\n" +
               "Ana: Minha filha está morrendo em um abrigo... ela precisa de tratamento médico.\n\n" +
               "O que você faz?\n" +
               String.join("\n", escolhasAtuais);
    }

    public String processarEscolha(int escolha) {
        if (!confrontoIniciado || confrontoTerminado) {
            return "O confronto já terminou.";
        }

        switch (faseAtual) {
            case "inicio":
                return processarEscolhaInicial(escolha);
            case "decisao_final":
                return processarDecisaoFinal(escolha);
            default:
                return "Erro no sistema de diálogo.";
        }
    }

    private String processarEscolhaInicial(int escolha) {
        // TODAS as escolhas levam para a mesma fase final com as mesmas 4 opções
        faseAtual = "decisao_final";
        configurarOpcoesFinals();
        
        String respostaAna;
        switch (escolha) {
            case 1: // Empatia
                respostaAna = "Ana baixa levemente a arma, seus olhos marejados.\n" +
                             "Ana: 'Você... você realmente quer me ouvir? É raro encontrar alguém que ainda se importa.'\n" +
                             "Ana: 'Minha filha Sofia... ela tem apenas 8 anos e está febril no abrigo.'\n\n";
                break;
                
            case 2: // Ameaça  
                respostaAna = "Ana aperta mais forte a arma, seus olhos endurecendo.\n" +
                             "Ana: 'Não tenho nada a perder! Minha filha morrerá se eu não conseguir essa vaga!'\n" +
                             "Ana: 'Você não entende o desespero de uma mãe!'\n\n";
                break;
                
            case 3: // Negociação
                respostaAna = "Ana hesita, curiosidade misturada com desconfiança.\n" +
                             "Ana: 'Que tipo de solução? O helicóptero vai partir em poucos minutos!'\n" +
                             "Ana: 'Minha filha não pode esperar mais... mas... talvez você tenha razão.'\n\n";
                break;
                
            case 4: // Ataque
                if (jogador.getDestreza() > rival.getDestreza()) {
                    respostaAna = "Você consegue desarmar Ana, mas ela saca uma faca!\n" +
                                 "Ana: 'Não vou desistir! Minha filha precisa de mim!'\n" +
                                 "Ana: 'Se necessário, morrerei lutando!'\n\n";
                } else {
                    respostaAna = "Ana é mais rápida e dispara! A bala passa perto de sua cabeça.\n" +
                                 "Ana: 'Próxima vez não erro! Fique onde está!'\n" +
                                 "Ana: 'Não me force a matá-lo!'\n\n";
                }
                break;
                
            default:
                return "Escolha inválida.";
        }
        
        return respostaAna + 
               "O helicóptero se aproxima cada vez mais. Você tem segundos para decidir.\n\n" +
               "O que você faz?\n" +
               String.join("\n", escolhasAtuais);
    }


    private void configurarOpcoesFinals() {
        escolhasAtuais.clear();
        escolhasAtuais.add("1. [Lutar] 'Não posso deixar você levar a vaga. Vamos resolver na força.'");
        escolhasAtuais.add("2. [Ceder] 'Você está certa. Leve a vaga e salve sua filha.'");
        escolhasAtuais.add("3. [Procurar] 'Vamos encontrar outro meio de transporte juntas.'");
        escolhasAtuais.add("4. [Dividir] 'Tenho suprimentos médicos. Talvez ajudem sua filha.'");
    }

    private String processarDecisaoFinal(int escolha) {
        switch (escolha) {
            case 1: // Lutar - inicia combate
                return "Ana aperta os olhos com determinação.\n" +
                       "Ana: 'Se é assim que tem que ser... que Deus me perdoe.'\n" +
                       "O confronto físico se torna inevitável. Apenas um de vocês sairá vivo daqui.\n\n" +
                       "PREPARE-SE PARA O COMBATE FINAL!";
                
            case 2: // Ceder - GAME OVER trágico (jogador morre)  
                return finalizarComSacrificioJogador();
                
            case 3: // Procurar outro meio - falha e vai para combate
                return "Ana balança a cabeça tristemente.\n" +
                       "Ana: 'Não há tempo! O helicóptero é nossa única chance!'\n" +
                       "Ana: 'Minha filha não pode esperar mais... sinto muito!'\n" +
                       "Ana levanta a arma novamente. O confronto se torna inevitável.\n\n" +
                       "PREPARE-SE PARA O COMBATE FINAL!";
                       
            case 4: // Dividir suprimentos - Ana rejeita, vai para combate
                return "Ana olha os suprimentos, mas balança a cabeça.\n" +
                       "Ana: 'Obrigada, mas... não é suficiente! Ela precisa de médicos reais!'\n" +
                       "Ana: 'O helicóptero é a única chance de Sofia sobreviver!'\n" +
                       "Ana empunha a arma novamente, desesperada.\n\n" +
                       "PREPARE-SE PARA O COMBATE FINAL!";
                
            default:
                return "Escolha inválida.";
        }
    }

    // FINAIS DETALHADOS ADAPTADOS PARA O NOVO SISTEMA

    private String finalizarComSacrificioJogador() {
        confrontoTerminado = true;
        tipoFinal = FinalType.SACRIFICIO_PROPRIO;
        
        return "=== FINAL: SACRIFÍCIO HEROICO ===\n\n" +
               "Você baixa as mãos em sinal de rendição.\n" +
               "Você: 'Você tem razão. Uma criança merece viver mais do que eu.'\n\n" +
               "Ana fica emocionada, lágrimas nos olhos.\n" +
               "Ana: 'Eu... eu não sei como agradecer. Sofia terá uma chance por sua causa.'\n\n" +
               "Você ajuda Ana a entrar no helicóptero enquanto hordas de zumbis se aproximam.\n" +
               "O helicóptero decola com Ana e Sofia a bordo.\n\n" +
               "Você fica para trás, cercado pelos mortos-vivos.\n" +
               "Em seus últimos momentos, você ouve Ana gritando 'Obrigada!' pela janela.\n\n" +
               "💀 GAME OVER: Você morreu, mas salvou uma mãe e sua filha.\n" +
               "Seu sacrifício não foi em vão - Sofia viverá.";
    }



    // Getters
    public List<String> getEscolhasAtuais() { return new ArrayList<>(escolhasAtuais); }
    public boolean isConfrontoIniciado() { return confrontoIniciado; }
    public boolean isConfrontoTerminado() { return confrontoTerminado; }
    public FinalType getTipoFinal() { return tipoFinal; }
}