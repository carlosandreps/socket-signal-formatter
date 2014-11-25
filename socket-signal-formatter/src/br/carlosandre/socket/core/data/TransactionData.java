package br.carlosandre.socket.core.data;

public class TransactionData {

	private int port;
	private InputData inputData;
	private OutputData outputData;

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public InputData getInputData() {
		return inputData;
	}

	public void setInputData(InputData inputData) {
		this.inputData = inputData;
	}

	public OutputData getOutputData() {
		return outputData;
	}

	public void setOutputData(OutputData outputData) {
		this.outputData = outputData;
	}

	@Override
	public String toString() {
		return "Transaction at port: " + port;
	}
}
