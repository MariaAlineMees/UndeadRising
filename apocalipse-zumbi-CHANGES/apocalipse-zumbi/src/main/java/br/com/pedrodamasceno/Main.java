package br.com.pedrodamasceno;

import br.com.pedrodamasceno.view.TelaInicial;

public class Main {
    public static void main(String[] args) {
        try {
            // Usar o look and feel do sistema
            javax.swing.UIManager.setLookAndFeel(
                    javax.swing.UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Iniciar pela tela inicial
        TelaInicial telaInicial = new TelaInicial();
        telaInicial.setVisible(true);
    }
}