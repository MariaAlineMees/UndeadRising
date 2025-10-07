package br.com.pedrodamasceno.model.zumbis;

import br.com.pedrodamasceno.model.personagens.Personagem;
import br.com.pedrodamasceno.model.status.EfeitoStatus;
import br.com.pedrodamasceno.model.status.StatusEffect;

/**
 * Boss Final - Ana Silva, sobrevivente desesperada lutando pela filha
 */
public class BossFinal extends Zumbi {
    private boolean mensagem75Mostrada = false;
    private boolean mensagem50Mostrada = false;
    private boolean mensagem25Mostrada = false;
    private int vidaMaxima = 300;
    
    public BossFinal() {
        super("Ana Silva - Sobrevivente Desesperada", 300, 25, 25, "Ex-médica lutando pela vida da filha. Está disposta a tudo pela única vaga no helicóptero de resgate!");
        this.vidaMaxima = 300;
    }
    
    @Override
    protected String habilidadeEspecial(Personagem alvo) {
        // Boss final usa táticas avançadas baseadas no desespero
        int tipoAtaque = (int)(Math.random() * 4);
        
        switch (tipoAtaque) {
            case 0:
                // Ataque desesperado - reduz resistência do alvo
                alvo.adicionarEfeito(new StatusEffect(EfeitoStatus.RESISTENCIA, 2, -3));
                return "\"Por minha filha!\" Ana ataca com desespero maternal, reduzindo sua resistência!";
                
            case 1:
                // Ataque médico tático - dano preciso
                int danoCritico = this.dano * 2;
                alvo.receberDano(danoCritico);
                return "Ana usa conhecimento médico para atingir pontos vitais! " + danoCritico + " de dano!";
                
            case 2:
                // Ataque com memórias da filha - força extra
                return "\"Sofia me dá forças!\" Ana se fortalece com a lembrança da filha!";
                
            case 3:
                // Ataque defensivo - cura parcial
                this.curar(15);
                return "Ana se trata rapidamente com conhecimento médico, recuperando 15 HP!";
                
            default:
                return "Ana ataca com determinação maternal!";
        }
    }
    
    // NOVO: Método para verificar mensagens em pontos específicos de vida
    public String verificarMensagemVida() {
        double porcentagemVida = (double) this.saude / this.vidaMaxima;
        
        if (porcentagemVida <= 0.75 && !mensagem75Mostrada) {
            mensagem75Mostrada = true;
            return "\n>>> ANA (75% VIDA): \"Não... não posso desistir! Sofia precisa de mim! Ela está sofrendo sem medicamentos!\" <<<\n";
        } else if (porcentagemVida <= 0.5 && !mensagem50Mostrada) {
            mensagem50Mostrada = true;
            return "\n>>> ANA (50% VIDA): \"[Mostra foto manchada de sangue] Olhe para ela! É apenas uma criança! Por favor, deixe-me salvá-la!\" <<<\n";
        } else if (porcentagemVida <= 0.25 && !mensagem25Mostrada) {
            mensagem25Mostrada = true;
            return "\n>>> ANA (25% VIDA): \"[Lágrimas nos olhos] Se eu morrer aqui... quem vai cuidar dela? Quem vai segurar a mão dela quando tiver medo?\" <<<\n";
        }
        
        return "";
    }
    
    @Override
    protected int getChanceEspecial() {
        return 70; // 70% de chance de usar habilidade especial
    }
}
