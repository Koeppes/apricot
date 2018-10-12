package de.apricotroom.bo;

import javax.persistence.Id;

public class Lieferant {
	@Id
	private Long id;
	private String name;
	private int nr;

	public int getNr() {
		return nr;
	}

	public void setNr(int nr) {
		this.nr = nr;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String toString() {
		if (this.getName().equals("Keine Auswahl")) {
			return this.getName();
		} else {
			return this.getName() + "(" + this.getNr() + ")";
		}
	}
}
