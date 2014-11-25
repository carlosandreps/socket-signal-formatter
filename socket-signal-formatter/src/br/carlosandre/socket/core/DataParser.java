package br.carlosandre.socket.core;

import static br.carlosandre.socket.constants.Constants.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import br.carlosandre.socket.core.data.Field;
import br.carlosandre.socket.core.data.FileOutputInformation;
import br.carlosandre.socket.core.data.InputStringInformation;
import br.carlosandre.socket.core.data.TransactionData;
import br.carlosandre.socket.properties.PropertiesUtils;
import br.carlosandre.socket.settings.SocketSettings;



/**
 * @author candre
 * 
 */
public class DataParser {

	private static final Properties prop = PropertiesUtils.read(PROPERTIES_FILE_PATH);

	/**
	 * Loads settings from XML file defined in system properties, into
	 * <code> SocketSettings</code> object
	 * 
	 * @return Socket Settings to be used in the socket server and data transfer
	 *         layout
	 */
	public static void main(String[] args) {
		loadTransactionDataSettings();
	}

	public static List<TransactionData> loadTransactionDataSettings() {
		TransactionData transactionData = null;
		List<TransactionData> transactionDataList = new ArrayList<TransactionData>();
		String[] xmls = prop.getProperty(TRANSACTION_FILES).split(",");

		try {
			for (String xmlName : xmls) {

				Document doc = getXMLDocument(xmlName.trim());
				Node transactionNode = doc.getElementsByTagName(TRANSACTION).item(0);

				if (isElement(transactionNode)) {

					Element transactionElement = (Element) transactionNode;
					int port = Integer.valueOf(transactionElement.getAttribute(PORT));

					transactionData = new TransactionData();
					transactionData.setPort(port);
					NodeList ioList = transactionElement.getChildNodes();

					for (int ioIndex = 0; ioIndex < ioList.getLength(); ioIndex++) {
						Node ioNode = ioList.item(ioIndex);

						if (isElement(ioNode)) {
							Element ioElement = (Element) ioNode;

							if (ioElement.getNodeName().equals(INPUT)) {

								NodeList ioInformationList = ioElement.getChildNodes();

								for (int ioInformationIndex = 0; ioInformationIndex < ioInformationList.getLength(); ioInformationIndex++) {
									Node ioInformationNode = ioInformationList.item(ioInformationIndex);

									if (isElement(ioInformationNode)) {
										Element ioInformationElement = (Element) ioInformationNode;

										if (ioInformationElement.getNodeName().startsWith(INPUT_STRING)) {

											String tagName = ioInformationElement.getTagName();
											InputStringInformation inputStringInformation = new InputStringInformation();
											inputStringInformation.setTagName(tagName);

											NodeList fieldList = ioInformationElement.getChildNodes();

											for (int fieldIndex = 0; fieldIndex < fieldList.getLength(); fieldIndex++) {
												Node fieldNode = fieldList.item(fieldIndex);

												if (hasFields(ioInformationElement, fieldNode)) {
													Element fieldElement = (Element) fieldNode;
													Field field = new Field();

													String name = fieldElement.getAttribute(NAME);
													String type = fieldElement.getAttribute(TYPE);
													String value = fieldElement.getAttribute(VALUE);

													field.setName(name);
													field.setType(type);
													field.setValue(value);

													inputStringInformation.addFields(field);
												}
											}
											transactionData.setInputData(inputStringInformation);
										}
									}
								}
							}

							if (ioElement.getNodeName().equals(OUTPUT)) {

								NodeList ioInformationList = ioElement.getChildNodes();

								for (int ioInformationIndex = 0; ioInformationIndex < ioInformationList.getLength(); ioInformationIndex++) {
									Node ioInformationNode = ioInformationList.item(ioInformationIndex);

									if (isElement(ioInformationNode)) {
										Element ioInformationElement = (Element) ioInformationNode;

										if (ioInformationElement.getNodeName().startsWith(FILE_OUTPUT)) {

											FileOutputInformation fileOutputInformation = new FileOutputInformation();

											String fileName = ioInformationElement.getAttribute(FILE_NAME);
											String appendFile = ioInformationElement.getAttribute(APPEND_FILE);
											String suffixName = ioInformationElement.getAttribute(SUFFIX_NAME);
											String typeAttribute = ioInformationElement.getAttribute(TYPE);
											String directory = ioInformationElement.getAttribute(DIRECTORY);
											String delimited = ioInformationElement.getAttribute(DELIMITED);
											String tagName = ioInformationElement.getTagName();

											fileOutputInformation.setFileName(fileName);
											fileOutputInformation.setAppendFile(getBooleanFromString(appendFile));
											fileOutputInformation.setSuffixName(suffixName);
											fileOutputInformation.setType(typeAttribute);
											fileOutputInformation.setDirectory(directory);
											fileOutputInformation.setDelimited(getBooleanFromString(delimited));
											fileOutputInformation.setTagName(tagName);

											NodeList fieldList = ioInformationElement.getChildNodes();

											for (int fieldIndex = 0; fieldIndex < fieldList.getLength(); fieldIndex++) {
												Node fieldNode = fieldList.item(fieldIndex);

												if (hasFields(ioInformationElement, fieldNode)) {
													Element fieldElement = (Element) fieldNode;
													Field fieldData = new Field();

													String name = fieldElement.getAttribute(NAME);
													String type = fieldElement.getAttribute(TYPE);
													String value = fieldElement.getAttribute(VALUE);

													fieldData.setName(name);
													fieldData.setType(type);
													fieldData.setValue(value);

													fileOutputInformation.addFields(fieldData);
												}
											}
											transactionData.setOutputData(fileOutputInformation);
										}
									}
								}
							}
						}
					}
				}
				transactionDataList.add(transactionData);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return transactionDataList;
	}

	private static boolean getBooleanFromString(String newFile) {
		return newFile.equals(YES) ? true : false;
	}

	private static Document getXMLDocument(String xmlName) throws ParserConfigurationException, SAXException, IOException {
		File fXmlFile = new File(xmlName);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(fXmlFile);
		doc.getDocumentElement().normalize();

		return doc;
	}

	public static SocketSettings loadSocketSettings() {
		Document doc = null;
		try {
			doc = getXMLDocument(prop.getProperty(SETTING_FILE));
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return readServerConfig(doc);
	}

	private static SocketSettings readServerConfig(Document doc) {
		NodeList settingsList = doc.getElementsByTagName(CONFIG_SERVER_CONFIG);
		String serverAddres = null;
		int serverTimeout = 0;
		int adminPort = 0;

		for (int temp = 0; temp < settingsList.getLength(); temp++) {
			Node nNode = settingsList.item(temp);
			if (isElement(nNode)) {
				Element eElement = (Element) nNode;
				serverAddres = eElement.getElementsByTagName(CONFIG_ADDRESS).item(0).getTextContent();
				serverTimeout = Integer.valueOf(eElement.getElementsByTagName(CONFIG_TIMEOUT).item(0).getTextContent());
				adminPort = Integer.valueOf(eElement.getElementsByTagName(CONFIG_ADMIN_PORT).item(0).getTextContent());
			}
		}
		return new SocketSettings(serverAddres, serverTimeout, adminPort);
	}

	private static boolean isElement(Node nNode) {
		return nNode != null && nNode.getNodeType() == Node.ELEMENT_NODE;
	}

	private static boolean isOutputInformation(Element ioElement) {
		return ioElement.getNodeName().startsWith(FILE_OUTPUT);
	}

	private static boolean isInputInformation(Element ioElement) {
		return ioElement.getNodeName().startsWith(INPUT_STRING);
	}

	private static boolean hasFields(Element ioElement, Node fieldNode) {
		return isElement(fieldNode) && isOutputInformation(ioElement) || isElement(fieldNode) && isInputInformation(ioElement);
	}

}