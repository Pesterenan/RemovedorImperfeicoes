package com.pesterenan.removedor.gui;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.JCheckBox;
import java.awt.GridLayout;
import java.awt.Toolkit;

import javax.swing.JButton;

public class MainGui extends JFrame{

	private static final long serialVersionUID = -2574097259188139232L;
	private static MainGui mainGui = null;
	private Dimension appWindowSize = new Dimension(450,350);
	

	private MainGui() {
		Dimension tamanhoDaTela = Toolkit.getDefaultToolkit().getScreenSize();
		setTitle("Removedor de Imperfeições - por Pesterenan");
		setBounds(	(int) tamanhoDaTela.getWidth() / 2 - appWindowSize.width / 2,
					(int) tamanhoDaTela.getHeight() / 2 - appWindowSize.height/ 2,
					appWindowSize.width, appWindowSize.height);
		setMinimumSize(appWindowSize);
		getContentPane().add(thumbnailPanel(), BorderLayout.CENTER);
		getContentPane().add(controlPanel(), BorderLayout.EAST);

		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
	}

	private JPanel cleaningButtonsPanel() {
		JPanel cbPanel = new JPanel();
		cbPanel.setLayout(new BorderLayout());

		JLabel cleaningPatternLabel = new JLabel("Padrão de Limpeza:");
		cbPanel.add(cleaningPatternLabel, BorderLayout.NORTH);
		
		JPanel patternCheckBoxGroupPanel = new JPanel();
		
		JCheckBox padrao3px = new JCheckBox("3x3");
		patternCheckBoxGroupPanel.add(padrao3px);
		
		JCheckBox padrao4px = new JCheckBox("4x4");
		patternCheckBoxGroupPanel.add(padrao4px);
		
		JCheckBox padrao5px = new JCheckBox("5x5");
		padrao5px.setSelected(true);
		patternCheckBoxGroupPanel.add(padrao5px);
		
		cbPanel.add(patternCheckBoxGroupPanel, BorderLayout.CENTER);
		return cbPanel;
	}

	private JPanel controlPanel() {
		JPanel painelBotoes = new JPanel();
		painelBotoes.setLayout(new GridLayout(0, 1, 10, 10));
		
		JButton bt_capturarTela = new JButton("Capturar Área da Tela");
		painelBotoes.add(bt_capturarTela);
		
		JButton bt_usarCtrlC = new JButton("Usar Área de Transferência");
		painelBotoes.add(bt_usarCtrlC);
		
		JButton bt_removerPixels = new JButton("Remover Pixels em Canais");
		bt_removerPixels.setEnabled(false);
		painelBotoes.add(bt_removerPixels);
		
		JButton bt_pintarPixels = new JButton("Pintar Pixels no RGB");
		bt_pintarPixels.setEnabled(false);
		painelBotoes.add(bt_pintarPixels);
		
		JButton bt_sair = new JButton("Sair");
		painelBotoes.add(bt_sair);
		
		painelBotoes.add(cleaningButtonsPanel());
		
		return painelBotoes;
	}

	private JPanel thumbnailPanel() {
		JPanel painelAreaMiniatura = new JPanel();
		JLabel miniaturaLabel = new JLabel("Miniatura");
		painelAreaMiniatura.add(miniaturaLabel, BorderLayout.NORTH);
		JPanel painelMiniatura = new JPanel();
		painelAreaMiniatura.add(painelMiniatura, BorderLayout.CENTER);
		painelMiniatura.setLayout(new BorderLayout());
		return painelAreaMiniatura;
	}

	public static MainGui getInstance() {
		if (mainGui == null) {
			mainGui = new MainGui();
		}
		return mainGui;
	}
}
