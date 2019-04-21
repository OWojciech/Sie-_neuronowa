import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import javax.swing.JFrame;
import javax.swing.JProgressBar;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends Application {

	static int lambda = 1;
	static double alpha = 0.1;
	static int liczbaEpok = 5;
	static int liczbaNeuronow = 2;

	public static void main(String... args) {

		launch(args);

	}

	@Override
	public void start(Stage stage) throws Exception {

		List<Integer> epoki = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
		for (int i = 2; i < 100; i++) {
			epoki.add(10 * i);
		}
		ChoiceDialog<Integer> ileEpok = new ChoiceDialog<Integer>(10, epoki);
		ileEpok.setHeaderText("Proszę wybrać liczbę epok uczenia dla sieci.");
		ileEpok.setTitle("NAI - Projekt");
		ileEpok.setContentText("Liczba Epok = ");
		ileEpok.showAndWait().orElse(10);

		ChoiceDialog<Integer> ileNeuronow = new ChoiceDialog<Integer>(260, epoki);
		ileNeuronow.setHeaderText("Proszę wskazać ilość neuronów w warstwie ukrytej.");
		ileNeuronow.setTitle("NAI - Projekt");
		ileNeuronow.setContentText("Liczba Neuronów = ");
		ileNeuronow.showAndWait().orElse(260);

		List<Double> wspolczynniki = new ArrayList<>(Arrays.asList(0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0,
				1.1, 1.2, 1.3, 1.4, 1.5, 1.6, 1.7, 1.8, 1.9, 2.0));
		ChoiceDialog<Double> jakiWspolczynnik = new ChoiceDialog<Double>(0.1, wspolczynniki);
		jakiWspolczynnik.setHeaderText("Jaką wartość nadać współczynnikowi uczenia?");
		jakiWspolczynnik.setTitle("NAI - Projekt");
		jakiWspolczynnik.setContentText("Alpha = ");
		jakiWspolczynnik.showAndWait().orElse(0.1);

		Integer[] lambdaTab = { 1, 2, 3 };
		ChoiceDialog<Integer> wyborLambdy = new ChoiceDialog<Integer>(1, lambdaTab);
		wyborLambdy.setHeaderText("Proszę wybrać wartość lambdy.");
		wyborLambdy.setTitle("NAI - Projekt");
		wyborLambdy.setContentText("Lambda = ");
		wyborLambdy.showAndWait().orElse(1);

		lambda = wyborLambdy.getResult();
		alpha = jakiWspolczynnik.getResult();
		liczbaEpok = ileEpok.getResult();
		liczbaNeuronow = ileNeuronow.getResult();

		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("NAI - Projekt");
		alert.setHeaderText("Podsumowanie");
		alert.setContentText(
				"Liczba epok uczenia: " + liczbaEpok + "\n" + "Liczba neuronów warstwy ukrytej: " + liczbaNeuronow
						+ "\n" + "Współczynnik uczenia alpha: " + alpha + "\n" + "Współczynnik lambda: " + lambda);
		alert.showAndWait();
		
		JFrame frame = new JFrame("Ładowanie danych...");
		JProgressBar progressBar = new JProgressBar();
		progressBar.setVisible(true);
		progressBar.setIndeterminate(true);
		frame.setLocationRelativeTo(null);
		frame.setSize(410, 70);
		frame.getContentPane().add(progressBar);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		
		
		double start = System.currentTimeMillis();
		String fileName1 = "Data/train/X_train.txt";
		String fileName2 = "Data/train/Y_train.txt";
		List<double[]> DataXTrain = new ArrayList<>();
		List<Byte> classResult = new ArrayList<>();

		try {
			Stream<String> stream = Files.lines(Paths.get(fileName2));

			stream.forEach(line -> {
				classResult.add(Byte.parseByte(line));
			});
			stream.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			Stream<String> stream = Files.lines(Paths.get(fileName1));

			stream.forEach(line -> {
				String[] temp = line.replaceAll("  ", " ").split(" ");
				double[] tempTab = new double[temp.length - 1];

				for (int i = 1; i < temp.length; i++) {
					tempTab[i - 1] = Double.parseDouble(temp[i]);
				}
				DataXTrain.add(tempTab);
			});
			stream.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
		
		frame.setTitle("Uczenie sieci...");
		int num = 3;
		Neuron[] innerTab = new Neuron[liczbaNeuronow];
		// Inicjowanie warstwy wejsciowej
		for (int i = 0; i < innerTab.length; i++) {
			innerTab[i] = new Neuron(DataXTrain.get(1).length);
		}
		// Warstwa wyjsciowa
		Neuron[] outerTab = new Neuron[num];
		// Inicjowanie warstwy wyjsciowej
		for (int i = 0; i < outerTab.length; i++) {
			outerTab[i] = new Neuron(liczbaNeuronow);
		}
		int ageCount = 0;

		// GUI TUTAJ

		while (true) {
			frame.setTitle("Uczenie sieci... epoka "+ ageCount +" na "+liczbaEpok);
			ageCount++;
			boolean stopped = true;
			// ALGORYTM
			for (int k = 0; k < DataXTrain.size(); k++) {

				double[] innerNet = new double[liczbaNeuronow];
				double[] innerResult = new double[liczbaNeuronow];
				// Obliczenie NET oraz wartosci funkcji aktywacji w warstwie
				// wejsciowej
				for (int i = 0; i < innerResult.length; i++) {
					innerNet[i] = innerTab[i].calcNET(DataXTrain.get(k));
					innerResult[i] = sigFunc(innerNet[i]);
				}
				double[] outerNet = new double[num];
				double[] outerResult = new double[num];
				// Obliczenie NET oraz wartosci funkcji aktywacji w warstwie
				// wyjsciowej
				for (int i = 0; i < outerResult.length; i++) {
					outerNet[i] = outerTab[i].calcNET(innerResult);
					outerResult[i] = Math.round(sigFunc(outerNet[i]));
				}
				// Jesli rzeczywiste wyjscie nie jest takie samo jak oczekiwane
				if (!Arrays.toString(outerResult).equals(Arrays.toString(result(classResult.get(k))))) {
					stopped = false;
					double[] outerWrong = new double[num];
					// Wyznaczanie bledu dla warstwy wyjsciowej
					for (int i = 0; i < outerWrong.length; i++) {
						outerWrong[i] = derSigFunc(outerNet[i]) * (result(classResult.get(k))[i] - outerResult[i]);
					}

					double[] innerWrong = new double[liczbaNeuronow];
					// Wyznaczenie bledu dla warstwy wejsciowej
					for (int i = 0; i < innerWrong.length; i++) {
						double tmp = 0;
						for (int j = 0; j < outerWrong.length; j++) {
							tmp += outerWrong[j] * outerTab[j].getWeights()[i];
						}
						innerWrong[i] = derSigFunc(innerNet[i]) * tmp;
					}

					// Aktualizacja wag neuronow warstwy wyjsciowej
					for (int i = 0; i < outerTab.length; i++) {
						double[] newOuterWeights = new double[liczbaNeuronow];
						for (int j = 0; j < newOuterWeights.length; j++) {
							newOuterWeights[j] = outerTab[i].getWeights()[j] + alpha * outerWrong[i] * innerResult[j];
						}
						double newB = outerTab[i].getB() + alpha * outerWrong[i];
						outerTab[i].setB(newB);
						outerTab[i].setWeights(newOuterWeights);
					}
					// Aktualizacja wag neuronow warstwy wejsciowej
					for (int i = 0; i < innerTab.length; i++) {
						double[] newInnerWeights = new double[DataXTrain.get(1).length];
						for (int j = 0; j < newInnerWeights.length; j++) {
							newInnerWeights[j] = innerTab[i].getWeights()[j]
									+ alpha * innerWrong[i] * DataXTrain.get(k)[j];
						}
						double newB = innerTab[i].getB() + alpha * innerWrong[i];
						innerTab[i].setB(newB);
						innerTab[i].setWeights(newInnerWeights);
					}
				}

			}
			if (stopped || liczbaEpok == ageCount) {
				break;
			}
		/*	// Zwiekszanie wspolczynniku uczenia co 10 epok
			if (ageCount % 10 == 0) {
				alpha += 0.1;
			}*/
		}

		// TESTOWANIE
		frame.setTitle("Testowanie...");
		String xtest = "Data/test/X_test.txt";
		String ytest = "Data/test/Y_test.txt";
		DataXTrain.clear();
		classResult.clear();
		try {
			Stream<String> stream = Files.lines(Paths.get(ytest));

			stream.forEach(line -> {
				classResult.add(Byte.parseByte(line));
			});
			stream.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			Stream<String> stream = Files.lines(Paths.get(xtest));

			stream.forEach(line -> {
				String[] temp = line.replaceAll("  ", " ").split(" ");
				double[] tempTab = new double[temp.length - 1];

				for (int i = 1; i < temp.length; i++) {
					tempTab[i - 1] = Double.parseDouble(temp[i]);
				}
				DataXTrain.add(tempTab);
			});
			stream.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
		double correctGuess = 0;
		for (int k = 0; k < DataXTrain.size(); k++) {

			double[] innerNet = new double[liczbaNeuronow];
			double[] innerResult = new double[liczbaNeuronow];
			// Obliczenie NET oraz wartosci funkcji aktywacji w warstwie
			// wejsciowej
			for (int i = 0; i < innerResult.length; i++) {
				innerNet[i] = innerTab[i].calcNET(DataXTrain.get(k));
				innerResult[i] = sigFunc(innerNet[i]);
			}
			double[] outerNet = new double[num];
			double[] outerResult = new double[num];
			// Obliczenie NET oraz wartosci funkcji aktywacji w warstwie
			// wyjsciowej
			for (int i = 0; i < outerResult.length; i++) {
				outerNet[i] = outerTab[i].calcNET(innerResult);
				outerResult[i] = Math.round(sigFunc(outerNet[i]));
			}

			if (Arrays.toString(outerResult).equals(Arrays.toString(result(classResult.get(k))))) {
				correctGuess++;
			}

		}
		DecimalFormat df = new DecimalFormat("#.00"); 
		double percent = (correctGuess / DataXTrain.size()) * 100;
		double stop = System.currentTimeMillis();
		frame.setVisible(false);
		Alert alert2 = new Alert(AlertType.INFORMATION);
		alert2.setTitle("NAI - Projekt");
		alert2.setHeaderText("Podsumowanie działania sieci");
		alert2.setContentText("Sieć rozpoznała " + df.format(percent) + "% przykładów testowych" + "\n" + "Program trawał "
				+ (stop - start) / 1000 + " sekund");
		alert2.showAndWait();
		
		System.exit(1);

	}

	static double[] result(byte b) {
		double[] tmp = new double[3];
		switch (b) {
		case 1:
			double[] jeden = { 0d, 0d, 0d };
			tmp = jeden;
			break;
		case 2:
			double[] dwa = { 0d, 0d, 1d };
			tmp = dwa;
			break;
		case 3:
			double[] trzy = { 0d, 1d, 0d };
			tmp = trzy;
			break;
		case 4:
			double[] cztery = { 0d, 1d, 1d };
			tmp = cztery;
			break;
		case 5:
			double[] piec = { 1d, 0d, 0d };
			tmp = piec;
			break;
		case 6:
			double[] szesc = { 1d, 0d, 1d };
			tmp = szesc;
			break;
		}
		return tmp;
	}

	static double sigFunc(double d) {
		// Funkcja sigmoidalna unipolarna w zaleznosci od d
		return 1 / (1 + Math.pow(Math.E, -lambda * d));
	}

	static double derSigFunc(double d) {
		// Pochodna funkcji sigmoidalnej unipolarnej w zaleznosci od d
		double fx = sigFunc(d);
		return lambda * fx * (1 - fx);
	}
}
