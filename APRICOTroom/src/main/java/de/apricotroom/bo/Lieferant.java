package de.apricotroom.bo;

import javax.persistence.Id;

public class Lieferant {
	@Id
	private Long id;
	private String name;
	private Integer nr;

	public Integer getNr() {
		return nr;
	}

	public void setNr(Integer nr) {
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

	public void rollback(Lieferant p) {
		this.setName(p.getName());
		this.setNr(p.getNr());
	}

	public String toString() {
		if (this.getName().equals("Keine Auswahl")) {
			return this.getName();
		} else {
			return this.getName() + "(" + this.getNr() + ")";
		}
	}
	public Lieferant copy() {
		return copy(this);
	}

	public Lieferant copy(Lieferant l) {
		Lieferant copy = new Lieferant();
		copy.setName(l.getName());
		copy.setNr(l.getNr());
		return copy;
	}
}
