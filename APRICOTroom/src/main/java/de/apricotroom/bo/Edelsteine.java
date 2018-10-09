package de.apricotroom.bo;

public enum Edelsteine {
	KEINE_AUSWAHL("Keine Auswahl", ""), ROSENQUARZ("Rosenquarz", "-RQ"), LABRADORIT("Labradorit", "-LB"), AQUA_CHALCEDON("Aqua Chalcdon", "-AC"), PREHNIT("Prehnit", "-PR"), MONDSTEIN_GRAU("Mondstein grau", "-MG"), MONDSTEIN_WEISS("Mondstein weiß", "-MW"), CHALCEDON("Aqua Chalcdon", "-C"), MONDSTEIN_SCHWARZ("Mondstein schwarz", "-MS");


	private String value;
	private String index;

	Edelsteine(String value, String index) {
		this.value = value;
		this.index = index;
	}

	public String getValue() {
		return value;
	}
	public String getIndex() {
		return index;
	}
	public static Edelsteine ofValue(String v) {
		Edelsteine result = null;
		for(int i = 0; i< Edelsteine.values().length && result == null; i++) {
			Edelsteine f = Edelsteine.values()[i];
			if(f.getValue().equals(v)) {
				result = f;
			}
		}
		return result;
	}
	public static Edelsteine ofIndex(String v) {
		Edelsteine result = null;
		for(int i = 0; i< Edelsteine.values().length && result == null; i++) {
			Edelsteine f = Edelsteine.values()[i];
			if(f.getIndex().equals(v)) {
				result = f;
			}
		}
		return result;
	}
}