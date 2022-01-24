package com.pesterenan.removedor.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import com.pesterenan.removedor.utils.Processos;

public class MainGui extends JFrame implements ActionListener {

	private static final long serialVersionUID = -2574097259188139232L;
	private static final int APP_WINDOW_HEIGHT = 350;
	private static final int APP_WINDOW_WIDTH = 450;
	private static MainGui mainGui = null;
	private Dimension appWindowSize = new Dimension(APP_WINDOW_WIDTH, APP_WINDOW_HEIGHT);
	private JLabel statusLabel;
	private static JButton bt_capturarTela;
	private static JButton bt_usarCtrlC;
	private static JButton bt_removerPixels;
	private static JButton bt_pintarPixels;
	private static JButton bt_sair;
	private static JPanel statusPanel;
	private static JProgressBar barraProgresso;
	private Border bordaRebaixada = BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2),
			BorderFactory.createBevelBorder(BevelBorder.LOWERED));
	private JPanel cbPanel;
	private JPanel patternCheckBoxGroupPanel;
	private JCheckBox padrao3px;
	private JCheckBox padrao4px;
	private JCheckBox padrao5px;
	private JPanel painelAreaAcoes;
	private JPanel painelAreaMiniatura;

	private MainGui() {
		Dimension tamanhoDaTela = Toolkit.getDefaultToolkit().getScreenSize();
		setTitle("Removedor de Imperfeições - por Pesterenan");
		setBounds((int) tamanhoDaTela.getWidth() / 2 - appWindowSize.width / 2,
				(int) tamanhoDaTela.getHeight() / 2 - appWindowSize.height / 2, 478, 349);
		setMinimumSize(appWindowSize);
		getContentPane().add(thumbnailPanel(), BorderLayout.CENTER);
		getContentPane().add(statusPanel(), BorderLayout.SOUTH);
		getContentPane().add(actionsPanel(), BorderLayout.EAST);

		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
	}

	private JPanel actionsPanel() {
		painelAreaAcoes = new JPanel();
		painelAreaAcoes.setLayout(new BorderLayout(0, 0));
		painelAreaAcoes.add(controlPanel(), BorderLayout.CENTER);
		painelAreaAcoes.add(cleaningButtonsPanel(), BorderLayout.SOUTH);
		return painelAreaAcoes;
	}

	private JPanel cleaningButtonsPanel() {
		cbPanel = new JPanel();
		cbPanel.setMinimumSize(new Dimension(10, 150));
		cbPanel.setLayout(new BorderLayout());
		cbPanel.setBorder(new CompoundBorder(new EmptyBorder(2, 2, 2, 2),
				new TitledBorder(new LineBorder(new Color(184, 207, 229)), "Padrão de Limpeza:", TitledBorder.LEADING,
						TitledBorder.TOP, null, new Color(51, 51, 51))));

		patternCheckBoxGroupPanel = new JPanel();

		padrao3px = new JCheckBox("3x3");
		patternCheckBoxGroupPanel.add(padrao3px);

		padrao4px = new JCheckBox("4x4");
		patternCheckBoxGroupPanel.add(padrao4px);

		padrao5px = new JCheckBox("5x5");
		padrao5px.setSelected(true);
		patternCheckBoxGroupPanel.add(padrao5px);

		cbPanel.add(patternCheckBoxGroupPanel, BorderLayout.CENTER);
		return cbPanel;
	}

	private JPanel controlPanel() {
		JPanel painelBotoes = new JPanel();
		painelBotoes.setBorder(
				new TitledBorder(null, "A\u00E7\u00F5es", TitledBorder.CENTER, TitledBorder.TOP, null, null));
		painelBotoes.setLayout(new GridLayout(0, 1, 0, 0));

		bt_capturarTela = new JButton("Capturar Área da Tela");
		painelBotoes.add(bt_capturarTela);

		bt_usarCtrlC = new JButton("Usar Área de Transferência");
		painelBotoes.add(bt_usarCtrlC);

		bt_removerPixels = new JButton("Remover Pixels em Canais");
		bt_removerPixels.setEnabled(false);
		painelBotoes.add(bt_removerPixels);

		bt_pintarPixels = new JButton("Pintar Pixels no RGB");
		bt_pintarPixels.setEnabled(false);
		painelBotoes.add(bt_pintarPixels);

		bt_sair = new JButton("Sair");
		bt_sair.addActionListener(this);
		painelBotoes.add(bt_sair);

		return painelBotoes;
	}

	private JPanel statusPanel() {
		statusPanel = new JPanel();
		statusPanel.setBorder(bordaRebaixada);

		barraProgresso = new JProgressBar();
		statusLabel = new JLabel("Pronto");
		barraProgresso.setStringPainted(true);
		barraProgresso.setVisible(false);
		statusPanel.add(barraProgresso, BorderLayout.CENTER);
		statusPanel.add(statusLabel, BorderLayout.CENTER);

		return statusPanel;
	}

	private JPanel thumbnailPanel() {
		painelAreaMiniatura = new JPanel();
		painelAreaMiniatura.setLayout(new BorderLayout(0, 0));
		painelAreaMiniatura
				.setBorder(new TitledBorder(null, "Miniatura", TitledBorder.CENTER, TitledBorder.TOP, null, null));
		JPanel painelMiniatura = new JPanel();
		painelMiniatura.setBorder(new CompoundBorder(new EmptyBorder(2, 2, 2, 2),
				new BevelBorder(BevelBorder.LOWERED, null, null, null, null)));
		painelAreaMiniatura.add(painelMiniatura, BorderLayout.CENTER);
		painelMiniatura.setLayout(new BorderLayout(0, 0));
		return painelAreaMiniatura;
	}

	public void setStatus(String novoStatus) {
		statusLabel.setText(novoStatus);
	}

	public static MainGui getInstance() {
		if (mainGui == null) {
			mainGui = new MainGui();
		}
		return mainGui;
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		Object fonteDoEvento = event.getSource();
		if (fonteDoEvento.equals(bt_sair)) {
			System.exit(0);
		}
		if (fonteDoEvento.equals(bt_capturarTela)) {
			Processos.capturarTela();
		}
	}
}
