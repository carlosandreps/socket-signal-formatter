package br.carlosandre.socket.server;

import java.util.ArrayList;
import java.util.List;

import br.carlosandre.socket.core.DataParser;
import br.carlosandre.socket.core.data.TransactionData;
import br.carlosandre.socket.settings.SocketSettings;


public class SocketServer {
	private static boolean shuttingDown;
	private static AdminPortListenerThread adminPortListenerThread;
	private static List<PortListenerThread> portListenerThreadList = new ArrayList<PortListenerThread>();

	public static void start(String... args) {
		SocketSettings settings = DataParser.loadSocketSettings();
		List<TransactionData> transDataList = DataParser.loadTransactionDataSettings();

		openOperationalPort(settings, transDataList);
		openAdminPort(settings);
	}

	public static boolean isShuttingDown() {
		return shuttingDown;
	}

	private static void setShuttingDown(boolean shuttingDown) {
		SocketServer.shuttingDown = shuttingDown;
	}

	public static void shutDown() {
		setShuttingDown(true);
		adminPortListenerThread.shutDown();
		for (PortListenerThread portListenerThread : portListenerThreadList) {
			portListenerThread.shutDown();
		}
	}

	private static void openOperationalPort(SocketSettings settings, List<TransactionData> transDataList) {
		for (TransactionData transactionData : transDataList) {
			openPort(settings, transactionData);
		}
	}

	// TODO Teste
	private static void openAdminPort(SocketSettings settings) {
		adminPortListenerThread = new AdminPortListenerThread(settings.getAdminPort(), settings.getIp());
		adminPortListenerThread.start();
	}

	private static void openPort(SocketSettings settings, TransactionData transactionData) {
		PortListenerThread portListenerThread = new PortListenerThread(settings, transactionData);
		portListenerThread.start();
		portListenerThreadList.add(portListenerThread);
	}

}
