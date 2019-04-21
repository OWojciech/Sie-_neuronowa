


public class Neuron {
	private double[] weights;
	private double b;

	public Neuron(int inputNum) {
		this.weights = new double[inputNum];
		for(int i = 0; i < weights.length; i++){
			weights[i] = Math.random()*2 - 1;
		}
		this.b = Math.random()*2 - 1;
	}
	public double getB() {
		return b;
	}

	public void setB(double b) {
		this.b = b;
	}
	
	public double calcNET(double[] tab){
		double result = 0;
		double sum = 0;
		for(int i = 0; i < tab.length; i++){
			sum += weights[i]*tab[i];
		}
		result = sum + b;
		return result;
	}

	public double calcNET(int[] tab){
		double result = 0;
		double sum = 0;
		for(int i = 0; i < tab.length; i++){
			sum += weights[i]*tab[i];
		}
		result = sum + b;
		return result;
	}
	public double[] getWeights() {
		return weights;
	}

	public void setWeights(double[] weights) {
		this.weights = weights;
	}
}
