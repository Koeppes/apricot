package de.apricotroom.beans;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.application.NavigationHandler;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import de.apricotroom.bo.Edelsteine;
import de.apricotroom.bo.Farben;
import de.apricotroom.bo.Kategorien;
import de.apricotroom.bo.Lieferant;
import de.apricotroom.bo.Materialien;
import de.apricotroom.bo.Produkt;
import de.apricotroom.bo.Ringgroessen;
import de.apricotroom.pers.JPAServiceLieferant;
import de.apricotroom.pers.JPAServiceProdukt;

@ViewScoped
@ManagedBean

public class ProductEditorBean {
	private static final long serialVersionUID = 1L;
	private Produkt selectedProdukt;
	private Produkt selectedProduktCopy;
	private List<Lieferant> suppliers;
	private List<Kategorien> kategorien;
	private List<Materialien> materialien;
	private List<Farben> farben;
	private List<Ringgroessen> ringgroessen;
	private List<Edelsteine> edelsteine;
	private ProductSearchBean searchBean;

	public boolean isEdelsteinDisabled() {
		return getSelectedProdukt().isGenerated() || !getSelectedProdukt().isGemstone();
	}

	public List<Edelsteine> getEdelsteine() {
		if (edelsteine == null) {
			edelsteine = new ArrayList<>(Arrays.asList(Edelsteine.values()));
		}
		return edelsteine;
	}

	public List<Kategorien> getKategorien() {
		if (kategorien == null) {
			kategorien = new ArrayList<>(Arrays.asList(Kategorien.values()));
		}
		return kategorien;
	}

	public List<Ringgroessen> getRinggroessen() {
		if (ringgroessen == null) {
			ringgroessen = new ArrayList<>(Arrays.asList(Ringgroessen.values()));
		}
		return ringgroessen;
	}

	public List<Materialien> getMaterialien() {
		if (materialien == null) {
			materialien = new ArrayList<>(Arrays.asList(Materialien.values()));
		}
		return materialien;
	}

	public void setMaterialien(List<Materialien> materialien) {
		this.materialien = materialien;
	}

	public List<Farben> getFarben() {
		if (farben == null) {
			farben = new ArrayList<>(Arrays.asList(Farben.values()));
		}
		return farben;
	}

	public boolean isSizeVisible() {
		return Kategorien.RING.getValue().equals(this.getSelectedProdukt().getKategorie());
	}

	public void setFarben(List<Farben> farben) {
		this.farben = farben;
	}

	public void setKategorien(List<Kategorien> categories) {
		this.kategorien = categories;
	}

	public List<Lieferant> getSuppliers() {
		return suppliers;
	}

	public void setSuppliers(List<Lieferant> suppliers) {
		this.suppliers = suppliers;
	}

	private JPAServiceProdukt serviceProdukt;
	private JPAServiceLieferant serviceLieferant;

	public JPAServiceLieferant getServiceLieferant() {
		return serviceLieferant;
	}

	public void setServiceLieferant(JPAServiceLieferant serviceLieferant) {
		this.serviceLieferant = serviceLieferant;
	}

	public Produkt getSelectedProduktCopy() {
		return selectedProduktCopy;
	}

	public void setSelectedProduktCopy(Produkt selectedProduktCopy) {
		this.selectedProduktCopy = selectedProduktCopy;
	}

	public Produkt getSelectedProdukt() {
		return selectedProdukt;
	}

	public void setSelectedProdukt(Produkt p) {
		this.selectedProdukt = p;
	}

	public void abmelden() {
		FacesContext fc = FacesContext.getCurrentInstance();
		NavigationHandler nav = fc.getApplication().getNavigationHandler();
		try {
			nav.handleNavigation(fc, null, "/login.xhtml");
			fc.renderResponse();
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	public void warn(String id, String message) {
		FacesContext.getCurrentInstance().addMessage(id, new FacesMessage(FacesMessage.SEVERITY_WARN, message, null));

	}

	public void info(String id, String message) {
		FacesContext.getCurrentInstance().addMessage(id, new FacesMessage(FacesMessage.SEVERITY_INFO, message, null));

	}

	public void error(String id, String message) {
		FacesContext.getCurrentInstance().addMessage(id, new FacesMessage(FacesMessage.SEVERITY_ERROR, message, null));

	}

	public JPAServiceProdukt getServiceProdukt() {
		return serviceProdukt;
	}

	public void setServiceProdukt(JPAServiceProdukt s) {
		this.serviceProdukt = s;
	}

	@PostConstruct
	public void init() {
		Produkt p = (Produkt) FacesContext.getCurrentInstance().getExternalContext().getRequestMap()
				.get("selectedProdukt");
		Lieferant lieferant = p.getLieferant();
		this.setSelectedProdukt(p);
		Produkt copy = (Produkt) FacesContext.getCurrentInstance().getExternalContext().getRequestMap()
				.get("selectedProduktCopy");
		this.setSelectedProduktCopy(copy);
		JPAServiceProdukt s = (JPAServiceProdukt) FacesContext.getCurrentInstance().getExternalContext().getRequestMap()
				.get("serviceProdukt");
		this.setServiceProdukt(s);
		JPAServiceLieferant l = (JPAServiceLieferant) FacesContext.getCurrentInstance().getExternalContext()
				.getRequestMap().get("serviceLieferant");
		this.setServiceLieferant(l);
		this.setSuppliers(l.getLieferanten());
		ProductSearchBean bean = (ProductSearchBean) FacesContext.getCurrentInstance().getExternalContext()
				.getRequestMap().get("productSearchBean");
		this.setSearchBean(bean);

	}

	public ProductSearchBean getSearchBean() {
		return searchBean;
	}

	public void setSearchBean(ProductSearchBean searchBean) {
		this.searchBean = searchBean;
	}

	public ProductEditorBean() {
		super();
	}

	public void onDeselect(Produkt p, String typeOfSelection, String indexes) {
		setSelectedProdukt(null);
	}

	public boolean produktSelected() {
		return this.getSelectedProdukt() != null;
	}

	public void cancel() {
		this.getSelectedProdukt().rollback(this.getSelectedProduktCopy());
		if (this.getSelectedProdukt().getId() == null) {
			this.getSearchBean().getProdukte().remove(this.getSelectedProdukt());
			this.getSearchBean().setSelectedProdukt(null);
		}
		navigateBack("cancel");
	}

	public void save() {
		if (this.getSelectedProdukt() != null) {
			if (this.getSelectedProdukt().getDescription() == null
					|| this.getSelectedProdukt().getDescription().isEmpty()) {
				info("Bitte einen Namen vergeben!");
			} else {
				if (this.getSelectedProdukt().getKategorie() == null
						|| "Keine Auswahl".equals(this.getSelectedProdukt().getKategorie())
						|| this.getSelectedProdukt().getKategorie().isEmpty()) {
					info("Bitte eine Kategorie auswählen!");
				} else {
					if (Kategorien.RING.getValue().equals(this.getSelectedProdukt().getKategorie())
							&& this.getSelectedProdukt().getSize() == 0) {
						info("Bitte eine Größe auswählen!");
					} else {
						if (this.getSelectedProdukt().getSellingPrice() == null) {
							info("Bitte einen Preis definieren!");
						} else {
							if (this.getSelectedProdukt().getMaterial() == null
									|| "Keine Auswahl".equals(this.getSelectedProdukt().getMaterial())
									|| this.getSelectedProdukt().getMaterial().isEmpty()) {
								info("Bitte ein Material auswählen!");
							} else {
								if (this.getSelectedProdukt().getLieferant() == null
										|| this.getSelectedProdukt().getLieferant().getId() == -1) {
									info("Bitte einen Lieferanten auswählen!");
								} else {

									if (this.getSelectedProdukt().isGemstone()
											&& (this.getSelectedProdukt().getEdelstein() == null
													|| this.getSelectedProdukt().getEdelstein().isEmpty())) {
										info("Bitte einen Edelstein auswählen!");

									} else {
										getServiceProdukt().persist(this.getSelectedProdukt());
										navigateBack("save");
									}
								}
							}
						}
					}
				}
			}
		}

	}

	public void info(String message) {
		FacesMessage success = new FacesMessage(FacesMessage.SEVERITY_INFO, "", message);
		FacesContext.getCurrentInstance().addMessage(null, success);
	}

	public void navigateBack(String action) {
		FacesContext fc = FacesContext.getCurrentInstance();
		NavigationHandler nav = fc.getApplication().getNavigationHandler();
		try {
			nav.handleNavigation(fc, action, "/all.xhtml");
			fc.renderResponse();
		} catch (Exception e) {
			System.out.println(e);
		}

	}

	public void capitalizeText() {
		System.out.println("capitalizeText");
	}
}
