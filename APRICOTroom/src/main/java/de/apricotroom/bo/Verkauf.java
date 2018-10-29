package de.apricotroom.bo;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class Verkauf {
	private Long id;
	private int count;
	private java.util.Date date;
	private Produkt produkt;
	private SimpleDateFormat sf = new SimpleDateFormat("dd.MM.yyyy", Locale.GERMANY);
	public Long getId() {
		return id;
	}
	public String getFormattedDate() {
		if (date != null) {
			return sf.format(date);
		}
		return "";
	}

	public void setId(Long id) {
		this.id = id;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public java.util.Date getDate() {
		return date;
	}
	public void setDate(java.util.Date date) {
		this.date = date;
	}
	public Produkt getProdukt() {
		return produkt;
	}
	public void setProdukt(Produkt produkt) {
		this.produkt = produkt;
	}
	
}
