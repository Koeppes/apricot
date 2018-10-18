package de.apricotroom.bo;

public enum Kategorien {
	KEINE_AUSWAHL("Keine Auswahl", "00"), RING("Ring", "04"), HALSKETTE("Halskette", "02"), OHRRING("Ohrring",
			"01"), ARMBAND("Armband", "03");

	private String value;
	private String index;

	Kategorien(String value, String index) {
		this.value = value;
		this.index = index;
	}

	public String getValue() {
		return value;
	}

	public String getIndex() {
		return index;
	}

	public static Kategorien of(String v) {
		Kategorien result = null;
		// return Stream.of(Kategorien.values()).filter(value ->
		// value.getValue().equals(v)).findFirst().orElse(null);
		for (int i = 0; i < Kategorien.values().length && result == null; i++) {
			Kategorien k = Kategorien.values()[i];
			if (k.getValue().equals(v)) {
				result = k;
			}
		}
		return result;
	}
	public static Kategorien ofToString(String v) {
		Kategorien result = null;
		// return Stream.of(Kategorien.values()).filter(value ->
		// value.getValue().equals(v)).findFirst().orElse(null);
		for (int i = 0; i < Kategorien.values().length && result == null; i++) {
			Kategorien k = Kategorien.values()[i];
			if (k.toString().equals(v)) {
				result = k;
			}
		}
		return result;
	}
	public String toString() {
		if (this.getValue().equals("Keine Auswahl")) {
			return this.getValue();
		} else {
			return this.getValue() + "(" + this.getIndex() + ")";
		}
	}
}