package br.carlosandre.socket.settings;
public class SocketSettings {

	private String ip;
	private int serverTimeout;
	private int adminPort;

	public SocketSettings(String ip, int serverTimeout, int adminPort) {
		this.ip = ip;
		this.serverTimeout = serverTimeout;
		this.adminPort = adminPort;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getServerTimeout() {
		return serverTimeout;
	}

	public void setServerTimeout(int serverTimeout) {
		this.serverTimeout = serverTimeout;
	}

	public int getAdminPort() {
		return adminPort;
	}

	public void setAdminPort(int adminPort) {
		this.adminPort = adminPort;
	}

}
