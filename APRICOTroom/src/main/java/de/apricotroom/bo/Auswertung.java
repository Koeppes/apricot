package de.apricotroom.bo;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Auswertung {
	private long id;
	private String filename;
	private Date date;
	private SimpleDateFormat sf = new SimpleDateFormat("dd.MM.yyyy", Locale.GERMANY);

	public Date getDate() {
		return date;
	}

	public String getFormattedDate() {
		if (date != null) {
			return sf.format(date);
		}
		return "";
	}

	public void setDate(Date t) {
		this.date = t;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	private byte[] data;
}
