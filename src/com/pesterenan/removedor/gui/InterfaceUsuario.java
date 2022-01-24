package com.pesterenan.removedor.gui;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import com.pesterenan.removedor.app.RemovedorImperfeicoes;
import com.pesterenan.removedor.utils.Processos.Processo;
import com.pesterenan.removedor.utils.TransferableImage;

class InterfaceUsuario extends JFrame implements ActionListener, PropertyChangeListener, ItemListener {
	private static final long serialVersionUID = 1L;
	private static InterfaceUsuario IU;
	private RemovedorImperfeicoes ri;
	private JButton bt_capturarTela;
	private JButton bt_usarCtrlC;
	private JButton bt_removerPixels;
	private JButton bt_pintarPixels;
	private JButton bt_sair;
	private JPanel painelMiniatura = new JPanel();
	private JPanel painelAreaMiniatura = new JPanel();
	private JPanel painelAreaBotoes = new JPanel();
	private JPanel painelBotoes = new JPanel();
	private JProgressBar barraProgresso = new JProgressBar();

	private JLabel labelMiniatura = new JLabel();
	private JLabel processo = new JLabel();
	private JCheckBox padrao3px = new JCheckBox("3x3");
	private JCheckBox padrao4px = new JCheckBox("4x4");
	private JCheckBox padrao5px = new JCheckBox("5x5");

	private Container conteudo;
	private ImageIcon imagemMini;
	private BufferedImage imagem;

	private Dimension tamanhoDaTela = Toolkit.getDefaultToolkit().getScreenSize();

//	public static void main(String[] args) throws IOException, AWTException, InterruptedException {
//		SwingUtilities.invokeLater(new Runnable() {
//			@Override
//			public void run() {
//				IU = new InterfaceUsuario();
//			}
//		});
//	}

	public InterfaceUsuario() {
		try {
			ri = new RemovedorImperfeicoes();
			ri.execute();
			criarEMostrarGUI();
		} catch (AWTException e) {
			e.printStackTrace();
		}
	}

	private void criarEMostrarGUI() {
		setTitle("Removedor de Imperfeições - por Pesterenan");
		setBounds((int) tamanhoDaTela.getWidth() / 2 - 200, (int) tamanhoDaTela.getHeight() / 2 - 115, 400, 230);
		setMinimumSize(new Dimension(400, 300));
		setVisible(true);
		// setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// Iniciar botoes:
		bt_capturarTela = new JButton("Capturar Área da Tela");
		bt_usarCtrlC = new JButton("Usar Área de Transferência");
		bt_removerPixels = new JButton("Remover Pixels em Canais");
		bt_pintarPixels = new JButton("Pintar Pixels no RGB");
		bt_sair = new JButton("Sair");
		bt_removerPixels.setEnabled(false);
		bt_pintarPixels.setEnabled(false);

		conteudo = getContentPane();

		painelBotoes.setLayout(new GridLayout(0, 1, 10, 10));
		painelMiniatura.setLayout(new BorderLayout());

		JPanel p = new JPanel();

		p.add(new JLabel("Miniatura"));
		painelMiniatura.add(p, BorderLayout.NORTH);

		p = new JPanel();
		p.setLayout(new BorderLayout(0, 0));
		JPanel b = new JPanel();
		b.add(new JLabel("Padrão de Limpeza:"));
		p.add(b, BorderLayout.NORTH);
		b = new JPanel();
		b.add(padrao3px);
		b.add(padrao4px);
		b.add(padrao5px);
		padrao5px.setSelected(true);
		p.add(b, BorderLayout.CENTER);

		painelBotoes.add(bt_capturarTela);
		painelBotoes.add(bt_usarCtrlC);
		painelBotoes.add(bt_removerPixels);
		painelBotoes.add(bt_pintarPixels);
		painelBotoes.add(bt_sair);

		painelAreaBotoes.setLayout(new BorderLayout());
		painelAreaBotoes.add(painelBotoes, BorderLayout.NORTH);
		painelAreaBotoes.add(p);
		painelAreaMiniatura.add(painelMiniatura, BorderLayout.CENTER);
		padrao3px.addItemListener(this);
		padrao4px.addItemListener(this);
		padrao5px.addItemListener(this);
		bt_capturarTela.addActionListener(this);
		bt_usarCtrlC.addActionListener(this);
		bt_removerPixels.addActionListener(this);
		bt_pintarPixels.addActionListener(this);
		bt_sair.addActionListener(this);
		conteudo.add(painelAreaMiniatura, BorderLayout.CENTER);
		conteudo.add(painelAreaBotoes, BorderLayout.EAST);
		p = new JPanel();
		p.add(processo);
		p.add(barraProgresso);
		barraProgresso.setStringPainted(true);
		barraProgresso.setVisible(false);
		conteudo.add(p, BorderLayout.SOUTH);
	}

	private void criarMiniatura() {
		int largura, altura;
		if (imagem.getWidth() > imagem.getHeight()) {
			largura = 160;
			altura = -1;
		} else {
			largura = -1;
			altura = 160;
		}
		imagemMini = new ImageIcon(imagem.getScaledInstance(largura, altura, Image.SCALE_SMOOTH));
		labelMiniatura = new JLabel(imagemMini);
	}

	private void salvarNaAreaDeTransferencia() {
		TransferableImage transImg = new TransferableImage(imagem);
		Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
		ClipboardOwner clipOwner = new ClipboardOwner() {
			@Override
			public void lostOwnership(Clipboard clipboard, Transferable contents) {

			}
		};
		clip.setContents((Transferable) transImg, clipOwner);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object fonte = e.getSource();
		if (fonte.equals(bt_sair)) {
			System.exit(0);
		}
		if (fonte.equals(bt_capturarTela)) {
			Thread t_capturarTela = new Thread(new Runnable() {
				@Override
				public void run() {
					if (labelMiniatura != null) {
						painelMiniatura.remove(labelMiniatura);
					}
					try {
						toBack();
						processo.setText("Capturando Área da Tela...");
						imagem = ri.tirarScreenShot();
						criarMiniatura();
						painelMiniatura.add(labelMiniatura, BorderLayout.CENTER);
						toFront();
						processo.setText("Captura salva em: " + "salvo");
						painelMiniatura.updateUI();
					} catch (Exception erro) {
						System.out.println("Deu erro");
						erro.printStackTrace();
					}
				}
			});
			t_capturarTela.start();
			bt_removerPixels.setEnabled(true);
		}
		if (fonte.equals(bt_removerPixels)) {
			bt_removerPixels.setEnabled(false);
			Thread t_removerPixels = new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						ri.setProcesso(Processo.LIMPAR);
						processo.setText("Removendo Pixels: ");
						barraProgresso.setVisible(true);
						ri.setImagem(imagem);
						ri.addPropertyChangeListener(IU);
						imagem = ri.doInBackground();
						painelMiniatura.remove(labelMiniatura);
						criarMiniatura();
						painelMiniatura.add(labelMiniatura);
						painelMiniatura.updateUI();
						salvarNaAreaDeTransferencia();
						bt_removerPixels.setEnabled(true);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
			t_removerPixels.start();
		}
		if (fonte.equals(bt_pintarPixels)) {
			bt_pintarPixels.setEnabled(false);
			Thread t_pintarPixels = new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						ri.setProcesso(Processo.PINTAR);
						processo.setText("Pintando Pixels: ");
						barraProgresso.setVisible(true);
						ri.setImagem(imagem);
						ri.addPropertyChangeListener(IU);
						imagem = ri.doInBackground();
						painelMiniatura.remove(labelMiniatura);
						criarMiniatura();
						painelMiniatura.add(labelMiniatura);
						painelMiniatura.updateUI();
						salvarNaAreaDeTransferencia();
						bt_pintarPixels.setEnabled(true);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
			t_pintarPixels.start();
		}
		if (fonte.equals(bt_usarCtrlC)) {
			try {
				processo.setText("Copiando imagem da Área de Transferência");
				Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
				clip.getContents(clip);
				imagem = (BufferedImage) clip.getData(DataFlavor.imageFlavor);
				if (labelMiniatura != null) {
					painelMiniatura.remove(labelMiniatura);
				}
				criarMiniatura();
				painelMiniatura.add(labelMiniatura);
				bt_removerPixels.setEnabled(true);
				bt_pintarPixels.setEnabled(true);
				processo.setText("Imagem copiada da Área de Transferência");
				painelMiniatura.updateUI();
			} catch (UnsupportedFlavorException e1) {
				processo.setText("Não há imagem na Área de Transferência");
			} catch (IOException e2) {
				processo.setText("Não há imagem na Área de Transferência");
			}
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		switch (evt.getPropertyName()) {
		case "progress":
			barraProgresso.setValue((Integer) evt.getNewValue());
			break;
		case "salvando":
			barraProgresso.setVisible(false);
			processo.setText((String) evt.getNewValue());
			break;
		case "pixelsRemovidos":
			processo.setText("Pixels removidos: " + String.valueOf((Integer) evt.getNewValue()));
			break;
		}
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		Object fonte = e.getItemSelectable();
		if (fonte.equals(padrao3px)) {
			ri.limpar3px = padrao3px.isSelected();
		}
		if (fonte.equals(padrao4px)) {
			ri.limpar4px = padrao4px.isSelected();
		}
		if (fonte.equals(padrao5px)) {
			ri.limpar5px = padrao5px.isSelected();
		}

	}
}