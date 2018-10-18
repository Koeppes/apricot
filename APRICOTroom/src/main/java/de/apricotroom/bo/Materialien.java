package de.apricotroom.bo;

public enum Materialien {
	KEINE_AUSWAHL("Keine Auswahl", "00"), VERGOLDETES_SILBER("Vergoldetes Silber", "01"), SILBER("Silber",
			"02"), VERGOLDET("Vergoldet", "03"), VERSILBERT_RHODINIERT("Versilbert/Rhodiniert",
					"04"), GEMSTONE("Gemstone", "05"), METALLLEGIERUNG_MODESCHMUCK("Metalllegierung / Modeschmuck",
							"06"), LEDER("Leder", "07"), ROSE_VERGOLDETES_SILBER("Rosé vergoldetes Silber",
									"08"), CORD("Cord", "09"), GESCHWAERZTES_SILBER("Geschwärztes Silber", "10");

	private String value;
	private String index;

	Materialien(String value, String index) {
		this.value = value;
		this.index = index;
	}

	public String getIndex() {
		return index;
	}

	public String getValue() {
		return value;
	}

	public static Materialien of(String v) {
		Materialien result = null;
		// return Stream.of(Kategorien.values()).filter(value ->
		// value.getValue().equals(v)).findFirst().orElse(null);
		for (int i = 0; i < Materialien.values().length && result == null; i++) {
			Materialien m = Materialien.values()[i];
			if (m.getValue().equalsIgnoreCase(v)) {
				result = m;
			}
		}
		return result;
	}

	public static Materialien ofToString(String v) {
		Materialien result = null;
		// return Stream.of(Kategorien.values()).filter(value ->
		// value.getValue().equals(v)).findFirst().orElse(null);
		for (int i = 0; i < Materialien.values().length && result == null; i++) {
			Materialien m = Materialien.values()[i];
			if (m.toString().equalsIgnoreCase(v)) {
				result = m;
			}
		}
		return result;
	}

	public static Materialien ofIndex(String v) {
		Materialien result = null;
		// return Stream.of(Kategorien.values()).filter(value ->
		// value.getValue().equals(v)).findFirst().orElse(null);
		for (int i = 0; i < Materialien.values().length && result == null; i++) {
			Materialien m = Materialien.values()[i];
			if (m.getIndex().equalsIgnoreCase(v)) {
				result = m;
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