package br.carlosandre.socket.core;

import br.carlosandre.socket.core.data.SocketData;
import br.carlosandre.socket.core.data.TransactionData;
import br.carlosandre.socket.plugins.FilePlugin;
import br.carlosandre.socket.plugins.Plugable;
import br.carlosandre.socket.settings.SocketSettings;
import static br.carlosandre.socket.constants.Constants.*;

public class DataSender {

	public static void send(SocketSettings settings, TransactionData transactionData, SocketData socketData) {
		if (isFilePlugin(transactionData)) {
			Plugable plugin = new FilePlugin();
			plugin.process(settings, transactionData, socketData);
		}
	}

	private static boolean isFilePlugin(TransactionData transactionData) {
		return transactionData.getOutputData().getTagName().equals(FILE_OUTPUT_INFORMATION);
	}
}
