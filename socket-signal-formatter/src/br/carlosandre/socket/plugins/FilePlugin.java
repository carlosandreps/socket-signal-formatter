package br.carlosandre.socket.plugins;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import br.carlosandre.socket.core.data.Field;
import br.carlosandre.socket.core.data.FileOutputInformation;
import br.carlosandre.socket.core.data.SocketData;
import br.carlosandre.socket.core.data.TransactionData;
import br.carlosandre.socket.settings.SocketSettings;
import static br.carlosandre.socket.constants.Constants.*;

public class FilePlugin implements Plugable {

	@Override
	public void process(SocketSettings socketSettings, TransactionData transactionData, SocketData socketData) {

		List<Field> inputFields = new ArrayList<Field>(transactionData.getInputData().getFields());
		List<Field> outputFields = new ArrayList<Field>(transactionData.getOutputData().getFields());
		Map<String, Field> fieldsMap = new LinkedHashMap<String, Field>();
		FileOutputInformation fileOutputInformation = (FileOutputInformation) transactionData.getOutputData();

		StringBuilder sb = new StringBuilder();
		String content = (String) socketData.getContent();
		String newContent;
		int start = 0;
		int end = 0;

		for (Field field : inputFields) {

			if (isEmpty(field)) {
				String value = BLANK;
				Field newField = new Field();
				end = start + field.getLenght();

				if (start > content.length()) {
					newField.setValue(value);
				} else if (end > content.length()) {
					value = content.substring(start, content.length());
				} else {
					value = content.substring(start, end);
				}
				if (value.length() < field.getLenght())
					for (int i = start; i < end; i++) {
						value = value + SPACE;
					}
				newField.setName(field.getName());
				newField.setType(field.getType());
				newField.setValue(value);
				fieldsMap.put(newField.getName(), newField);

				start = end;
			} else {
				end = field.getType().length();
				start = start + end;
			}
		}

		for (int i = 0; i < outputFields.size(); i++) {

			String column;
			int lenght;
			Field outputFieldData = outputFields.get(i);
			Field inputFieldData = fieldsMap.get(outputFieldData.getName());

			if (inputFieldData == null) {
				column = outputFieldData.getValue();
			} else {
				column = inputFieldData.getValue();
			}

			lenght = outputFieldData.getLenght();

			if (fileOutputInformation.isDelimited()) {
				column = column.trim();
				if (outputFields.indexOf(outputFieldData) < outputFields.size() - 1) {
					column = column + ";";
				}
			} else {
				if (column.length() > lenght) {
					column = column.substring(0, lenght);
				} else
					while (column.length() < lenght) {
						column = column + SPACE;
					}
			}
			sb.append(column);
		}
		newContent = sb.toString();
		System.out.println("Processed value: " + newContent);
		writeFile(transactionData, newContent);
	}

	private void writeFile(TransactionData transactionData, String newContent) {
		try {
			FileOutputInformation fileOutputInformation = (FileOutputInformation) transactionData.getOutputData();

			String directory = fileOutputInformation.getDirectory();
			String fileName = fileOutputInformation.getFileName();
			String fileExtension = fileOutputInformation.getType();
			String suffixName = fileOutputInformation.getSuffixName();
			String content = BLANK;
			String fullFilePath = BLANK;
			String fileSuffix = BLANK;

			boolean appendFile = fileOutputInformation.isAppendFile();

			if (suffixName != null && suffixName.equals(TIMESTAMP_VAR)) {
				SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN);
				fileSuffix = sdf.format(new Date());
			} else if (suffixName != null && suffixName.equals(RANDOM_VAR)) {
				fileSuffix = generateRandomString(5);
			} else {
				fileSuffix = suffixName;
			}
			fullFilePath = directory + File.separator + fileName + fileSuffix + "." + fileExtension;

			File file = new File(fullFilePath);
			File fDirectory = new File(directory);
			fDirectory.mkdirs();

			if (!file.exists()) {
				file.createNewFile();
			}

			if (appendFile) {
				content = readFile(fullFilePath);
			}

			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(content + newContent);
			bw.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String readFile(String path) {
		BufferedReader br = null;
		StringBuilder sb = new StringBuilder();
		try {

			br = new BufferedReader(new FileReader(path));

			String line = br.readLine();
			sb = new StringBuilder();
			while (line != null) {
				sb.append(line);
				line = br.readLine();
				sb.append(String.format(BREAK_LINE_FORMAT));
			}

		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}

	public String generateRandomString(int size) {
		char[] chars = ALLOWED_CHARS.toCharArray();
		StringBuilder sb = new StringBuilder();
		Random random = new Random();
		for (int i = 0; i < size; i++) {
			char c = chars[random.nextInt(chars.length)];
			sb.append(c);
		}
		String output = sb.toString();

		return output;
	}

	private boolean isEmpty(Field fieldData) {
		return (fieldData.getValue() == null) || (fieldData.getValue() != null && fieldData.getValue().isEmpty());
	}
}
