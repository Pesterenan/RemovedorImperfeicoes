import java.awt.AWTException;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.SwingWorker;

class RemovedorImperfeicoes extends SwingWorker<BufferedImage, String> implements MouseListener {

	private Robot robo = new Robot();
	private BufferedImage imagem;
	private boolean clicou = false;
	private Dimension tamanhoTela;
	private ArrayList<ArrayList<Color>> listaCoresImagem = new ArrayList<ArrayList<Color>>();
	private int contagemRemocao = 0;
	private JFrame telaTransparente;
	private Processo processo;
	public ArrayList<ArrayList<ArrayList<Color>>> padroes3px = new ArrayList<ArrayList<ArrayList<Color>>>();
	public ArrayList<ArrayList<ArrayList<Color>>> padroes3pxInv = new ArrayList<ArrayList<ArrayList<Color>>>();
	boolean limpar3px = false;
	public ArrayList<ArrayList<ArrayList<Color>>> padroes4px = new ArrayList<ArrayList<ArrayList<Color>>>();
	public ArrayList<ArrayList<ArrayList<Color>>> padroes4pxInv = new ArrayList<ArrayList<ArrayList<Color>>>();
	boolean limpar4px = false;
	public ArrayList<ArrayList<ArrayList<Color>>> padroes5px = new ArrayList<ArrayList<ArrayList<Color>>>();
	public ArrayList<ArrayList<ArrayList<Color>>> padroes5pxInv = new ArrayList<ArrayList<ArrayList<Color>>>();
	boolean limpar5px = true;

	public RemovedorImperfeicoes() throws AWTException {
		importarPadroes();
	}

	private static File[] getArquivosPadroesDaPasta(String pasta) {
		ClassLoader carregador = Thread.currentThread().getContextClassLoader();
		URL url = carregador.getResource(pasta);
		String caminho = url.getPath();
		return new File(caminho).listFiles();
	}

	private void importarPadroes() {
		carregarPadroesNaLista("padroes3px", padroes3px);
		carregarPadroesNaLista("padroes4px", padroes4px);
		carregarPadroesNaLista("padroes5px", padroes5px);
		carregarPadroesNaLista("padroes3pxInv", padroes3pxInv);
		carregarPadroesNaLista("padroes4pxInv", padroes4pxInv);
		carregarPadroesNaLista("padroes5pxInv", padroes5pxInv);
	}

	private void carregarPadroesNaLista(String pasta, ArrayList<ArrayList<ArrayList<Color>>> lista) {
		for (final File arquivo : getArquivosPadroesDaPasta(pasta)) {
			BufferedImage padrao = null;
			try {
				padrao = ImageIO.read(arquivo);
				lista.add(importarImagem(padrao));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private ArrayList<ArrayList<Color>> importarImagem(BufferedImage imagem) {
		ArrayList<ArrayList<Color>> lista = new ArrayList<ArrayList<Color>>();
		for (int i = 0; i < imagem.getHeight(); i++) {
			ArrayList<Color> corPixel = new ArrayList<Color>();
			for (int j = 0; j < imagem.getWidth(); j++) {
				corPixel.add(new Color(imagem.getRGB(j, i)));
			}
			lista.add(corPixel);
		}
		return lista;
	}

	private BufferedImage exportarLista(ArrayList<ArrayList<Color>> listaCores) {
		BufferedImage imagemSaida = new BufferedImage(listaCores.get(0).size(), listaCores.size(), 1);
		for (int i = 0; i < imagemSaida.getHeight(); i++) {
			for (int j = 0; j < imagemSaida.getWidth(); j++) {
				Color cor = listaCores.get(i).get(j);
				imagemSaida.setRGB(j, i, cor.getRGB());
			}
		}
		return imagemSaida;
	}

	private void limparPixels(ArrayList<ArrayList<Color>> desenho,
			ArrayList<ArrayList<ArrayList<Color>>> listaDePadroes) {
		// FAIXAS HORIZONTAIS
		int progresso = 0;
		int alturaPadroes = listaDePadroes.get(0).size();
		int larguraPadroes = listaDePadroes.get(0).get(0).size();
		for (int faixa = 0; faixa <= desenho.size() - alturaPadroes; faixa++) {
			progresso = (100 * faixa) / desenho.size();
			setProgress(progresso);
			// PIXEL NA FAIXA
			for (int pixel = 0; pixel <= desenho.get(0).size() - larguraPadroes; pixel++) {
				// Criar padrão do desenho para comparar:
				ArrayList<ArrayList<Color>> listaFaixa = new ArrayList<ArrayList<Color>>();
				for (int faixaPadrao = 0; faixaPadrao < alturaPadroes; faixaPadrao++) {
					ArrayList<Color> faixaPixel = new ArrayList<Color>();
					for (int pixelPadrao = 0; pixelPadrao < larguraPadroes; pixelPadrao++) {
						faixaPixel.add(desenho.get(faixa + faixaPadrao).get(pixel + pixelPadrao));
					}
					listaFaixa.add(faixaPixel);
				}
				// Procurar cada padrão dentro do desenho e pintar a correção:
				for (ArrayList<ArrayList<Color>> padrao : listaDePadroes) {
					// Ignorar a cor do pixel central se não precisar pintar:
					Color corIgnorar = (padrao.get(alturaPadroes / 2).get(larguraPadroes / 2).equals(Color.white))
							? Color.black
							: Color.white;
					Color corPintar = corIgnorar.equals(Color.white) ? Color.black : Color.white;
					if (listaFaixa.get(alturaPadroes / 2).get(larguraPadroes / 2).equals(corIgnorar)) {
						continue;
					}
					// Comparar a área do desenho com o padrão:
					if (listaFaixa.equals(padrao)) {
						for (int m = 0; m < alturaPadroes; m++) {
							for (int n = 0; n < padrao.get(m).size(); n++) {
								if (listaFaixa.get(m).get(n).equals(corPintar)) {
									desenho.get(faixa + m).set(pixel + n, corIgnorar);
									contagemRemocao++;
								}
							}
						}
					}
				}
			}
		}
	}

	private void pintarPixels(ArrayList<ArrayList<Color>> desenho,
			ArrayList<ArrayList<ArrayList<Color>>> listaDePadroes) {
		// FAIXAS HORIZONTAIS
		int progresso = 0;
		int alturaPadroes = listaDePadroes.get(0).size();
		int larguraPadroes = listaDePadroes.get(0).get(0).size();
		for (int faixa = 0; faixa <= desenho.size() - alturaPadroes; faixa++) {
			progresso = (100 * faixa) / desenho.size();
			setProgress(progresso);
			// PIXEL NA FAIXA
			for (int pixel = 0; pixel <= desenho.get(0).size() - larguraPadroes; pixel++) {
				// Criar padrão do desenho para comparar:
				ArrayList<ArrayList<Color>> listaFaixa = new ArrayList<ArrayList<Color>>();
				Map<Integer, Color> mapaPixels = new HashMap<Integer, Color>();
				int pixels = 0;
				for (int faixaPadrao = 0; faixaPadrao < alturaPadroes; faixaPadrao++) {
					ArrayList<Color> faixaPixel = new ArrayList<Color>();
					for (int pixelPadrao = 0; pixelPadrao < larguraPadroes; pixelPadrao++) {
						faixaPixel.add((desenho.get(faixa + faixaPadrao).get(pixel + pixelPadrao).equals(Color.white))
								? Color.white
								: Color.black);
						mapaPixels.put(pixels, desenho.get(faixa + faixaPadrao).get(pixel + pixelPadrao));
						pixels++;
					}
					listaFaixa.add(faixaPixel);
				}
				int frequencia = 0;
				Color corIgnorar = Color.black;
				Color corPintar = null;
				for (Color cor : mapaPixels.values()) {
					if (Collections.frequency(mapaPixels.values(), cor) > frequencia && !cor.equals(Color.white)){
						frequencia = Collections.frequency(mapaPixels.values(), cor);
						corPintar = cor;
					}
				}
				for (ArrayList<ArrayList<Color>> padrao : listaDePadroes) {
					// Ignorar a cor do pixel central se não precisar pintar:
					if (listaFaixa.get(alturaPadroes / 2).get(larguraPadroes / 2).equals(corIgnorar)) {
						continue;
					}
					
					if (listaFaixa.equals(padrao)) {
						for (int m = 0; m < alturaPadroes; m++) {
							for (int n = 0; n < larguraPadroes; n++) {
								if (listaFaixa.get(m).get(n).equals(Color.white)) {
									desenho.get(faixa + m).set(pixel + n, corPintar);
									contagemRemocao++;
								}
							}
						}

					}
				}
			}
		}
	}

	public BufferedImage tirarScreenShot() throws InterruptedException {
		Point primeiroPonto = null;
		Point segundoPonto = null;
		tamanhoTela = Toolkit.getDefaultToolkit().getScreenSize();
		telaTransparente = new JFrame("Selecione a área da tela:");

		telaTransparente.setUndecorated(true);
		telaTransparente.setBounds(0, 0, tamanhoTela.width, tamanhoTela.height);
		telaTransparente.setOpacity(0.05f);

		telaTransparente.addMouseListener(this);
		telaTransparente.setAlwaysOnTop(true);
		telaTransparente.setVisible(true);

		while (primeiroPonto == null) {
			if (clicou) {
				primeiroPonto = new Point(MouseInfo.getPointerInfo().getLocation());
				System.out.println("Primeiro Ponto: X: " + primeiroPonto.x + ", Y: " + primeiroPonto.y);
				clicou = false;
			}
			Thread.sleep(100);
		}
		while (segundoPonto == null) {
			if (clicou) {
				segundoPonto = new Point(MouseInfo.getPointerInfo().getLocation());
				System.out.println("Segundo Ponto: X: " + segundoPonto.x + ", Y: " + segundoPonto.y);
				clicou = false;
			}
			Thread.sleep(100);
		}
		telaTransparente.dispose();
		int x1 = Math.min(primeiroPonto.x, segundoPonto.x);
		int x2 = Math.max(primeiroPonto.x, segundoPonto.x);
		int y1 = Math.min(primeiroPonto.y, segundoPonto.y);
		int y2 = Math.max(primeiroPonto.y, segundoPonto.y);
		Thread.sleep(500);
		BufferedImage captura = robo.createScreenCapture(new Rectangle(x1, y1, x2 - x1, y2 - y1));
		System.out.println("Largura: " + captura.getWidth() + " Altura :" + captura.getHeight());
		return captura;
	}

	@SuppressWarnings("unused")
	private void trocarTodosOsPixels(ArrayList<ArrayList<Color>> listaCores, Color corProcura, Color corSubstituta) {
		for (int i = 0; i < listaCores.size(); i++) {
			for (int j = 0; j < listaCores.get(i).size(); j++) {
				Color cor = listaCores.get(i).get(j);
				if (cor.equals(corProcura)) {
					imagem.setRGB(j, i, corSubstituta.getRGB());
				}
			}
		}
	}

	void setImagem(BufferedImage imagem) {
		this.imagem = imagem;
	}

	void setProcesso(Processo proc) {
		this.processo = proc;
	}

	@Override
	protected BufferedImage doInBackground() throws Exception {
		setProgress(0);
		listaCoresImagem = importarImagem(imagem);
		switch (processo) {
		case LIMPAR:
			if (limpar5px) {
				limparPixels(listaCoresImagem, padroes5px);
				limparPixels(listaCoresImagem, padroes5pxInv);
			}
			if (limpar4px) {
				limparPixels(listaCoresImagem, padroes4px);
				limparPixels(listaCoresImagem, padroes4pxInv);
			}
			if (limpar3px) {
				limparPixels(listaCoresImagem, padroes3px);
				limparPixels(listaCoresImagem, padroes3pxInv);
			}
			break;
		case PINTAR:
			if (limpar5px) {
				pintarPixels(listaCoresImagem, padroes5pxInv);
			}
			if (limpar4px) {
				pintarPixels(listaCoresImagem, padroes4pxInv);
			}
			if (limpar3px) {
				pintarPixels(listaCoresImagem, padroes3pxInv);
			}
			break;
		}
		firePropertyChange("salvando", null, "Salvando imagem na Área de Transferência...");
		BufferedImage imagemLimpa = exportarLista(listaCoresImagem);
		firePropertyChange("pixelsRemovidos", null, contagemRemocao);
		contagemRemocao = 0;
		return imagemLimpa;
	}

	@Override
	public void mouseClicked(MouseEvent e) {

	}

	@Override
	public void mousePressed(MouseEvent e) {
		Object fonte = e.getSource();
		if (fonte.equals(telaTransparente)) {
			clicou = true;
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {

	}

	@Override
	public void mouseEntered(MouseEvent e) {

	}

	@Override
	public void mouseExited(MouseEvent e) {

	}

}
