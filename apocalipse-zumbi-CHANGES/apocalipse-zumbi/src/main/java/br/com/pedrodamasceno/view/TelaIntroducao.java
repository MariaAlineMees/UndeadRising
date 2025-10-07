package br.com.pedrodamasceno.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TelaIntroducao extends JDialog {

    public TelaIntroducao(Frame parent) {
        super(parent, "Introdução", true);
        setSize(600, 500);
        setLocationRelativeTo(parent);
        setResizable(false);
        getContentPane().setBackground(Color.BLACK);
        setLayout(new BorderLayout());

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout());
        contentPanel.setBackground(Color.BLACK);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel lblTitulo = new JLabel("BEM-VINDO(A)!", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Impact", Font.BOLD, 56));
        lblTitulo.setForeground(Color.RED);
        contentPanel.add(lblTitulo, BorderLayout.NORTH);

        JTextPane textPane = new JTextPane();
        textPane.setContentType("text/html");
        textPane.setBackground(Color.BLACK);
        textPane.setForeground(Color.WHITE);
        textPane.setEditable(false);
        textPane.setFont(new Font("Arial", Font.PLAIN, 20));
        textPane.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        String htmlContent = "<html><body style='font-family: Arial; font-size: 14pt; color: white; text-align: center; padding: 10px;'>"
                + "<p>O mundo foi devastado por um apocalipse zumbi.</p>"
                + "<br>"
                + "<p>Sua sobrevivência depende de explorar os cenários nos períodos: <b>Manhã</b>, <b>Tarde</b> e <b>Noite</b>.</p>"
                + "<br>"
                + "<p>Após completar as três explorações, você poderá dormir para avançar para o próximo dia.</p>"
                + "<br>"
                + "<p>Tenha cuidado e boa sorte!</p>"
                + "</body></html>";
        textPane.setText(htmlContent);
        contentPanel.add(textPane, BorderLayout.CENTER);

        JButton btnOk = new JButton("Entendido!");
        estiloBotao(btnOk);
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(Color.BLACK);
        buttonPanel.add(btnOk);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);

        btnOk.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        add(contentPanel, BorderLayout.CENTER);
    }

    private void estiloBotao(JButton botao) {
        botao.setFont(new Font("Arial", Font.BOLD, 18));
        botao.setBackground(new Color(30, 30, 30));
        botao.setForeground(Color.RED);
        botao.setFocusPainted(false);
        botao.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
        botao.setPreferredSize(new Dimension(150, 45));

        botao.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                botao.setBackground(new Color(60, 0, 0));
                botao.setForeground(Color.BLACK);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                botao.setBackground(new Color(30, 30, 30));
                botao.setForeground(Color.RED);
            }
        });
    }
}