package de.apricotroom.bo;

public enum Farben {
	KEINE_AUSWAHL("Keine Auswahl", ""), GOLD("Gold", "-g"), SILBER("Silber", "-s"), ROSE("Rosé", "-r");

	private String value;
	private String index;

	Farben(String value, String index) {
		this.value = value;
		this.index = index;
	}

	public String getValue() {
		return value;
	}
	public String getIndex() {
		return index;
	}
	public static Farben of(String v) {
		Farben result = null;
		for(int i = 0; i< Farben.values().length && result == null; i++) {
			Farben f = Farben.values()[i];
			if(f.getValue().equals(v)) {
				result = f;
			}
		}
		if(result == null) {
			result = Farben.KEINE_AUSWAHL;
		}
		return result;
	}
}