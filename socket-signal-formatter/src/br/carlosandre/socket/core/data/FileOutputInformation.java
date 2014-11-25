package br.carlosandre.socket.core.data;

import static br.carlosandre.socket.constants.Constants.*;

public class FileOutputInformation extends OutputData {

	private String fileName;
	private String suffixName;
	private String type;
	private String directory;
	private boolean delimited;
	private boolean appendFile;

	public FileOutputInformation() {
		super();
		setupDefaultValues();
	}

	private void setupDefaultValues() {
		this.fileName = DEFAULT_FILE_NAME;
		this.suffixName = BLANK;
		this.type = DEFAULT_EXTENSION;
		this.directory = DEFAULT_DIRECTORY;
		this.delimited = false;
		this.appendFile = false;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getSuffixName() {
		return suffixName;
	}

	public void setSuffixName(String suffixName) {
		this.suffixName = suffixName;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDirectory() {
		return directory;
	}

	public void setDirectory(String directory) {
		this.directory = directory;
	}

	public boolean isDelimited() {
		return delimited;
	}

	public void setDelimited(boolean delimited) {
		this.delimited = delimited;
	}

	public boolean isAppendFile() {
		return appendFile;
	}

	public void setAppendFile(boolean appendFile) {
		this.appendFile = appendFile;
	}

}
