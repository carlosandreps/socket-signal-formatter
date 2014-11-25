package br.carlosandre.socket.server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

public class AdminConnectionThread extends Thread {
	private Socket socket;

	public AdminConnectionThread(Socket socket) {
		this.socket = socket;
	}

	public void run() {
		BufferedReader br = null;
		PrintWriter pr = null;
		String inputLine;

		try {
			System.out.println("Accepting connection from IP: " + socket.getInetAddress().getHostAddress());

			br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			pr = new PrintWriter(socket.getOutputStream(), true);

			while (!SocketServer.isShuttingDown() && (inputLine = br.readLine()) != null) {
				if ("shutdown".endsWith(inputLine)) {
					pr.println("ok");
					SocketServer.shutDown();
				}
			}
		} catch (SocketException ex) {
			if (SocketServer.isShuttingDown()) {
				System.out.println("Closing connection from IP: " + socket.getInetAddress().getHostAddress());
			} else {
				ex.printStackTrace();
			}
		} catch (SocketTimeoutException ex) {
			ex.printStackTrace();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				pr.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			try {
				br.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			try {
				socket.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	public void shutDown() {
		try {
			socket.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
