package de.apricotroom.bo;

public enum Ringgroessen {
	KEINE_AUSWAHL("Keine Auswahl", 0), FIFTY("50", 50), FIFTY_TWO("52", 52), FIFTY_FOUR("54", 54), FIFTY_SIX("56", 56), FIFTY_EIGHT("58", 58), SIXTY("60", 60);

	private String value;
	private int size;

	Ringgroessen(String value, int size) {
		this.value = value;
		this.size = size;
	}

	public String getValue() {
		return value;
	}
	public int getSize() {
		return size;
	}
	public static Ringgroessen of(int v) {
		Ringgroessen result = null;
		// return Stream.of(Kategorien.values()).filter(value ->
		// value.getValue().equals(v)).findFirst().orElse(null);
		for (int i = 0; i < Ringgroessen.values().length && result == null; i++) {
			Ringgroessen m = Ringgroessen.values()[i];
			if (m.getSize() == v) {
				result = m;
			}
		}
		return result;
	}

}