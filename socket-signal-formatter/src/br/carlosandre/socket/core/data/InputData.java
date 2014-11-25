package br.carlosandre.socket.core.data;

import java.util.LinkedList;

public class InputData {

	private String tagName;
	private LinkedList<Field> fields;

	public String getTagName() {
		return tagName;
	}

	public void setTagName(String tagName) {
		this.tagName = tagName;
	}

	public LinkedList<Field> getFields() {
		return fields;
	}

	public void setFields(LinkedList<Field> fields) {
		this.fields = fields;
	}

	public void addFields(Field field) {
		if (fields == null) {
			fields = new LinkedList<Field>();
		}
		fields.add(field);
	}
}
