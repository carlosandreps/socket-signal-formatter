package br.carlosandre.socket.server;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import br.carlosandre.socket.core.data.TransactionData;
import br.carlosandre.socket.settings.SocketSettings;


public class PortListenerThread extends Thread {
	private ServerSocket serverSocket;
	private SocketSettings settings;
	private TransactionData transactionData;
	private List<ConnectionThread> connectionThreadList = new ArrayList<ConnectionThread>();

	public PortListenerThread(SocketSettings settings, TransactionData transactionData) {
		this.settings = settings;
		this.transactionData = transactionData;
	}

	public void run() {
		try {
			InetAddress inetAddress = InetAddress.getByName(settings.getIp());

			serverSocket = new ServerSocket(transactionData.getPort(), 0, inetAddress);
			serverSocket.setSoTimeout(settings.getServerTimeout());

			System.out.println("Listening to port: " + transactionData.getPort());

			while (true) {
				Socket socket = serverSocket.accept();
				ConnectionThread connectionThread = new ConnectionThread(socket, settings, transactionData);
				connectionThread.start();
				connectionThreadList.add(connectionThread);

				removeDeadThreads();
			}
		} catch (SocketException ex) {
			if (SocketServer.isShuttingDown()) {
				System.out.println("Closing port: " + transactionData.getPort());
			} else {
				ex.printStackTrace();
			}
		} catch (SocketTimeoutException ex) {
			ex.printStackTrace();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				serverSocket.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	private void removeDeadThreads() {
		for (int i = connectionThreadList.size() - 1; i >= 0; i--) {
			if (!connectionThreadList.get(i).isAlive()) {
				connectionThreadList.remove(i);
			}
		}
	}

	public void shutDown() {
		try {
			serverSocket.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		for (ConnectionThread connectionThread : connectionThreadList) {
			connectionThread.shutDown();
		}
	}
}
