package de.apricotroom.bo;

public class Produkt {
	private Long id;
	private String name;
	private String description;
	private Double purchasePrice;
	private Double sellingPrice;
	private Double salePrice;
	private String serialnumber;
	private int size;
	private String length;
	private Lieferant lieferant;
	private String kategorie;
	private String farbe;
	private String material;
	private boolean generated;
	private boolean imported;

	public boolean isImported() {
		return imported;
	}

	public void setImported(boolean imported) {
		this.imported = imported;
	}

	private boolean gemstone;
	private String edelstein;
	private int counter;

	public int getCounter() {
		return counter;
	}

	public void setCounter(int counter) {
		this.counter = counter;
	}

	public boolean isGemstone() {
		return gemstone;
	}

	public void setGemstone(boolean gemstone) {
		this.gemstone = gemstone;
		this.buildSerialnumber();
	}

	public String getEdelstein() {
		return edelstein;
	}

	public void setEdelstein(String edelstein) {
		this.edelstein = edelstein;
		this.buildSerialnumber();
	}

	public boolean isGenerated() {
		return generated;
	}

	public void setGenerated(boolean generated) {
		this.generated = generated;
	}

	public String getMaterial() {
		return material;
	}

	public void setMaterial(String material) {
		this.material = material;
		this.buildSerialnumber();
	}

	public String getFarbe() {
		return farbe;
	}

	public void setFarbe(String farbe) {
		if (!"Keine Auswahl".equals(farbe)) {
			this.farbe = farbe;
			this.buildSerialnumber();
		}
	}

	public String getKategorie() {
		return kategorie;
	}

	public void setKategorie(String kategorie) {
		this.kategorie = kategorie;
		this.buildSerialnumber();
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Double getPurchasePrice() {
		return purchasePrice;
	}

	public void setPurchasePrice(Double purchasePrice) {
		this.purchasePrice = purchasePrice;
	}

	public Double getSellingPrice() {
		return sellingPrice;
	}

	public void setSellingPrice(Double sellingPrice) {
		this.sellingPrice = sellingPrice;
	}

	public Double getSalePrice() {
		return salePrice;
	}

	public void setSalePrice(Double salePrice) {
		this.salePrice = salePrice;
	}

	public String getSerialnumber() {
		return serialnumber;
	}

	public void setSerialnumber(String serialnumber) {
		this.serialnumber = serialnumber;
	}

	public int getSize() {
		return size;
	}

	public void basicSetSize(int size) {
		this.size = size;
	}

	public void setSize(int size) {
		this.size = size;
		this.buildSerialnumber();
	}

	public String getLength() {
		return length;
	}

	public void setLength(String length) {
		this.length = length;
	}

	public Lieferant getLieferant() {
		return lieferant;
	}

	public void setLieferant(Lieferant supplier) {
		this.lieferant = supplier;
	}

	public Produkt copy() {
		return copy(this);
	}

	public Produkt copy(Produkt p) {
		Produkt copy = new Produkt();
		copy.setDescription(p.getDescription());
		copy.setLength(p.getLength());
		copy.setLieferant(p.getLieferant());
		copy.setName(p.getName());
		copy.setSerialnumber(p.getSerialnumber());
		copy.setPurchasePrice(p.getPurchasePrice());
		copy.setSalePrice(p.getSalePrice());
		copy.setSellingPrice(p.getSellingPrice());
		copy.setSize(p.getSize());
		copy.setGenerated(false);
		copy.setMaterial(p.getMaterial());
		copy.setKategorie(p.getKategorie());
		copy.setFarbe(p.getFarbe());
		copy.setGemstone(p.isGemstone());
		return copy;
	}

	public void rollback(Produkt p) {
		this.setDescription(p.getDescription());
		this.setLength(p.getLength());
		this.setLieferant(p.getLieferant());
		this.setName(p.getName());
		this.setSerialnumber(p.getSerialnumber());
		this.setPurchasePrice(p.getPurchasePrice());
		this.setSalePrice(p.getSalePrice());
		this.setSellingPrice(p.getSellingPrice());
		this.setSize(p.getSize());
	}

	public void buildSerialnumber() {
		if (!this.isImported() || this.getId() == null) {
			this.buildSerialnumber(counter);
		}

	}

	public void buildSerialnumber(int newCounter) {
		if (!this.isGenerated()) {
			String serial = "";
			Kategorien k = Kategorien.of(this.getKategorie());
			if (k != null) {
				serial = serial + k.getIndex();
			}
			Materialien m = Materialien.of(this.getMaterial());
			if (m != null) {
				serial = serial + m.getIndex();
			}
			serial = serial + "-" + newCounter;
			if (this.isGemstone()) {
				Edelsteine e = Edelsteine.ofIndex(this.getEdelstein());
				if (e != null) {
					serial = serial + e.getIndex();
				}
			}
			if (Kategorien.RING.getValue().equals(this.getKategorie())
					&& this.getSize() != Ringgroessen.KEINE_AUSWAHL.getSize()) {
				Ringgroessen g = Ringgroessen.of(this.getSize());
				if (g != null) {
					serial = serial + "-" + g.getValue();
				}
			}
			Farben f = Farben.of(this.getFarbe());
			if (f != null) {
				serial = serial + f.getIndex();
			}
			this.setSerialnumber(serial);
		}
	}
}
