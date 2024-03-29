package br.carlosandre.socket.properties;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * @author ALSPestana
 * 
 */
public class PropertiesUtils {

	public final static String propertiesName = "conf.properties";

	/**
	 * L� um arquivo properties referente ao cominho do arquivo passado como
	 * par�metro.
	 * 
	 * @param fileName
	 * @return
	 * 
	 */
	public static Properties read(String fileName) {

		// o arquivo encontra-se no mesmo diret�rio //da aplica��o
		File file = new File(fileName);

		Properties props = new Properties();
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
			// l� os dados que est�o no arquivo
			props.load(fis);
			fis.close();
		} catch (IOException e) {
			System.out.println("Processo de leitura do arquivo properties...");
			e.printStackTrace();
		}

		return props;
	}

}
