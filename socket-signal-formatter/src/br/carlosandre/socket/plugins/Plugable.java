package br.carlosandre.socket.plugins;

import br.carlosandre.socket.core.data.SocketData;
import br.carlosandre.socket.core.data.TransactionData;
import br.carlosandre.socket.settings.SocketSettings;

public interface Plugable {

	public void process(SocketSettings socketSettings, TransactionData transactionData, SocketData socketData);
}
