
package de.apricotroom.beans;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.application.NavigationHandler;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.apache.poi.util.IOUtils;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import de.apricotroom.bo.Auswertung;
import de.apricotroom.bo.Farben;
import de.apricotroom.bo.Kategorien;
import de.apricotroom.bo.Lieferant;
import de.apricotroom.bo.Materialien;
import de.apricotroom.bo.Produkt;
import de.apricotroom.bo.Ringgroessen;
import de.apricotroom.pers.JPAServiceLieferant;
import de.apricotroom.pers.JPAServiceProdukt;
import de.apricotroom.tools.ProduktImporter;

@SessionScoped
@ManagedBean

public class ProductSearchBean {
	private static final long serialVersionUID = 1L;
	private List<Produkt> produkte = new ArrayList<>();
	private List<Lieferant> lieferanten = new ArrayList<>();
	private UploadedFile importFile;
	private Lieferant selectedFilterLieferant;
	private String selectedRows;
	private Produkt selectedProdukt;
	private JPAServiceProdukt serviceProdukt = new JPAServiceProdukt();
	private JPAServiceLieferant serviceLieferant = new JPAServiceLieferant();
	private boolean buttonsDisabled;
	private List<Kategorien> kategorien;
	private Kategorien selectedFilterKategorie;
	private List<Materialien> materialien;
	private Materialien selectedFilterMaterial;
	private List<Ringgroessen> groessen;
	private Ringgroessen selectedFilterGroesse;
	private boolean filterModeschmuck;
	private Double filterPreisVon;
	private Double filterPreisBis;

	public Double getFilterPreisVon() {
		return filterPreisVon;
	}

	public void setFilterPreisVon(double filterPreisVon) {
		this.filterPreisVon = filterPreisVon;
	}

	public Double getFilterPreisBis() {
		return filterPreisBis;
	}

	public void setFilterPreisBis(double filterPreisBis) {
		this.filterPreisBis = filterPreisBis;
	}

	public void setKategorien(List<Kategorien> kategorien) {
		this.kategorien = kategorien;
	}

	public boolean isFilterModeschmuck() {
		return filterModeschmuck;
	}

	public void setFilterModeschmuck(boolean filterModeschmuck) {
		this.filterModeschmuck = filterModeschmuck;
	}

	public List<Ringgroessen> getGroessen() {
		if (groessen == null) {
			groessen = new ArrayList<>(Arrays.asList(Ringgroessen.values()));
		}
		return groessen;
	}

	public void setGroessen(List<Ringgroessen> groessen) {
		this.groessen = groessen;
	}

	public Ringgroessen getSelectedFilterGroesse() {
		return selectedFilterGroesse;
	}

	public void setSelectedFilterGroesse(Ringgroessen selectedFilterGroesse) {
		this.selectedFilterGroesse = selectedFilterGroesse;
	}

	public List<Farben> getFarben() {
		if (farben == null) {
			farben = new ArrayList<>(Arrays.asList(Farben.values()));
		}
		return farben;
	}

	public void setFarben(List<Farben> farben) {
		this.farben = farben;
	}

	public Farben getSelectedFilterFarbe() {
		return selectedFilterFarbe;
	}

	public void setSelectedFilterFarbe(Farben selectedFilterFarbe) {
		this.selectedFilterFarbe = selectedFilterFarbe;
	}

	public void setServiceLieferant(JPAServiceLieferant serviceLieferant) {
		this.serviceLieferant = serviceLieferant;
	}

	private List<Farben> farben;
	private Farben selectedFilterFarbe;

	public List<Materialien> getMaterialien() {
		if (materialien == null) {
			materialien = new ArrayList<>(Arrays.asList(Materialien.values()));
		}
		return materialien;
	}

	public void setMaterialien(List<Materialien> naterialien) {
		this.materialien = naterialien;
	}

	public Materialien getSelectedFilterMaterial() {
		return selectedFilterMaterial;
	}

	public void setSelectedFilterMaterial(Materialien selectedFilterMaterial) {
		this.selectedFilterMaterial = selectedFilterMaterial;
	}

	public Kategorien getSelectedFilterKategorie() {
		return selectedFilterKategorie;
	}

	public void setSelectedFilterKategorie(Kategorien selecteFilterKategorie) {
		this.selectedFilterKategorie = selecteFilterKategorie;
	}

	public Lieferant getSelectedFilterLieferant() {
		return selectedFilterLieferant;
	}

	public void setSelectedFilterLieferant(Lieferant selectedFilterLieferant) {
		this.selectedFilterLieferant = selectedFilterLieferant;
	}

	public UploadedFile getImportFile() {
		return importFile;
	}

	public List<Kategorien> getKategorien() {
		if (kategorien == null) {
			kategorien = new ArrayList<>(Arrays.asList(Kategorien.values()));
		}
		return kategorien;
	}

	public void filter() {
		List<Produkt> filteredProdukte = serviceProdukt.getProdukte();
		Produkt selected = this.getSelectedProdukt();
		List<Produkt> filteredByLiefernaten = new ArrayList<>();
		Lieferant l = this.getSelectedFilterLieferant();
		if (l != null && !l.getName().equals("Keine Auswahl")) {
			filteredByLiefernaten = filteredProdukte.stream().filter(p -> p.getLieferant() == l)
					.collect(Collectors.toList());
			filteredProdukte = filteredByLiefernaten;
		}
		Kategorien k = this.getSelectedFilterKategorie();
		if (k != null & !k.getValue().equals(Kategorien.KEINE_AUSWAHL.getValue())) {
			List<Produkt> filteredByKategorien = filteredProdukte.stream()
					.filter(p -> getSelectedFilterKategorie().getValue().equals(p.getKategorie()))
					.collect(Collectors.toList());
			filteredProdukte = filteredByKategorien;
		}
		Materialien m = this.getSelectedFilterMaterial();
		if (m != null & !m.getValue().equals(Materialien.KEINE_AUSWAHL.getValue())) {
			List<Produkt> filteredByMaterialien = filteredProdukte.stream()
					.filter(p -> getSelectedFilterMaterial().getValue().equals(p.getMaterial()))
					.collect(Collectors.toList());
			filteredProdukte = filteredByMaterialien;
		}
		if (filterModeschmuck) {
			List<Produkt> filteredByModeschmuck = filteredProdukte.stream()
					.filter(p -> Farben.GOLD.getValue().equals(p.getFarbe())
							|| Farben.SILBER.getValue().equals(p.getFarbe())
							|| Farben.ROSE.getValue().equals(p.getFarbe()))
					.collect(Collectors.toList());
			filteredProdukte = filteredByModeschmuck;
		}
		Farben f = this.getSelectedFilterFarbe();
		if (f != null & !f.getValue().equals(Farben.KEINE_AUSWAHL.getValue())) {
			List<Produkt> filteredByFarben = filteredProdukte.stream()
					.filter(p -> getSelectedFilterFarbe().getValue().equals(p.getFarbe())).collect(Collectors.toList());
			filteredProdukte = filteredByFarben;
		}
		Ringgroessen g = this.getSelectedFilterGroesse();
		if (g != null & !g.getValue().equals(Ringgroessen.KEINE_AUSWAHL.getValue())) {
			List<Produkt> filteredByGroessen = filteredProdukte.stream()
					.filter(p -> getSelectedFilterGroesse().getSize() == p.getSize()).collect(Collectors.toList());
			filteredProdukte = filteredByGroessen;
		}
		if (filterPreisVon != null && filterPreisBis == null) {
			List<Produkt> filteredByPreisVon = filteredProdukte.stream()
					.filter(p -> getFilterPreisVon().equals(p.getSellingPrice())).collect(Collectors.toList());
			filteredProdukte = filteredByPreisVon;
		}
		if (filterPreisVon != null && filterPreisBis != null) {
			List<Produkt> filteredByPreisNotNull = filteredProdukte.stream().filter(p -> p.getSellingPrice() != null).collect(Collectors.toList());
			List<Produkt> filteredByPreisVon = filteredByPreisNotNull.stream()
					.filter(p -> Double.compare(getFilterPreisVon(), p.getSellingPrice()) < 0
							&& Double.compare(getFilterPreisBis(), p.getSellingPrice()) > 0)
					.collect(Collectors.toList());
			filteredProdukte = filteredByPreisVon;
		}

		this.setProdukte(filteredProdukte);
		if (this.getProdukte().contains(selected)) {
			this.setSelectedProdukt(selected);
		}
	}

	public void setFilterPreisVon(Double filterPreisVon) {
		this.filterPreisVon = filterPreisVon;
	}

	public void setFilterPreisBis(Double filterPreisBis) {
		this.filterPreisBis = filterPreisBis;
	}

	public String uploadImportFile() {
		if (importFile.getFileName() != null && !importFile.getFileName().isEmpty()) {
			try {
				byte[] bytes = IOUtils.toByteArray(importFile.getInputstream());
				ProduktImporter importer = new ProduktImporter();
				List<Produkt> list = importer.readFile(bytes);
				serviceProdukt.persistProdukte(list);
				this.getProdukte().addAll(list);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return "";
	}

	public void setImportFile(UploadedFile importFile) {
		this.importFile = importFile;
	}

	public List<Lieferant> getLieferanten() {
		return lieferanten;
	}

	public void setLieferanten(List<Lieferant> lieferanten) {
		this.lieferanten = lieferanten;
	}

	public boolean isButtonsDisabled() {
		return buttonsDisabled;
	}

	public void setButtonsDisabled(boolean buttonsDisabled) {
		this.buttonsDisabled = buttonsDisabled;
	}

	public List<Produkt> getProdukte() {
		return produkte;
	}

	public void setProdukte(List<Produkt> list) {
		this.produkte = list;
	}

	public Produkt getSelectedProdukt() {
		return selectedProdukt;
	}

	public void setSelectedProdukt(Produkt p) {
		this.selectedProdukt = p;
	}

	@PostConstruct
	public void init() {
		this.setLieferanten(getServiceLieferant().getLieferanten());
		this.setProdukte(getServiceProdukt().getProdukte());
	}

	public void fileUploadListener(FileUploadEvent e) {
		// Get uploaded file from the FileUploadEvent
		this.importFile = e.getFile();
		// Print out the information of the file
	}

	public void abmelden() {
		FacesContext fc = FacesContext.getCurrentInstance();
		NavigationHandler nav = fc.getApplication().getNavigationHandler();
		try {
			nav.handleNavigation(fc, null, "/login.xhtml");
			fc.renderResponse();
			ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
			ec.invalidateSession();
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

	public JPAServiceLieferant getServiceLieferant() {
		return serviceLieferant;
	}

	public String getSelectedRows() {
		return selectedRows;
	}

	public void setSelectedRows(String selectedRows) {
		this.selectedRows = selectedRows;
	}

	public void setServiceProdukt(JPAServiceProdukt s) {
		this.serviceProdukt = s;
	}

	public void onSelect(Produkt p, String typeOfSelection, String indexes) {
		setSelectedProdukt(p);
		selectedRows = indexes;
		this.setButtonsDisabled(false);
	}

	public ProductSearchBean() {
		super();
	}

	public void onDeselect(Produkt p, String typeOfSelection, String indexes) {
		setSelectedProdukt(null);
		this.setButtonsDisabled(true);
	}

	public void editProdukt() {
		if (this.getSelectedProdukt() != null) {
			navigateToEditor(this.getSelectedProdukt());
		} else {
			FacesMessage success = new FacesMessage(FacesMessage.SEVERITY_INFO, "", "Bitte einen Artikel auswählen!");
			FacesContext.getCurrentInstance().addMessage(null, success);
		}
	}

	public boolean produktSelected() {
		return this.getSelectedProdukt() != null;
	}

	public void createProdukt() {
		Produkt p = new Produkt();
		p.setCounter(this.getProdukte().size());
		produkte.add(p);
		setSelectedProdukt(p);
		p.setLieferant(getServiceLieferant().getLieferantById(new Long(0)));
		selectedRows = String.valueOf(produkte.size() - 1);
		navigateToEditor(p);
	}

	private void navigateToEditor(Produkt p) {
		FacesContext.getCurrentInstance().getExternalContext().getRequestMap().put("selectedProdukt", p);
		FacesContext.getCurrentInstance().getExternalContext().getRequestMap().put("selectedProduktCopy", p.copy());
		FacesContext.getCurrentInstance().getExternalContext().getRequestMap().put("serviceProdukt",
				this.getServiceProdukt());
		FacesContext.getCurrentInstance().getExternalContext().getRequestMap().put("serviceLieferant",
				this.getServiceLieferant());
		FacesContext.getCurrentInstance().getExternalContext().getRequestMap().put("productSearchBean", this);

		FacesContext fc = FacesContext.getCurrentInstance();
		NavigationHandler nav = fc.getApplication().getNavigationHandler();
		try {
			nav.handleNavigation(fc, null, "/productEditor.xhtml");
			fc.renderResponse();
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	public boolean deleteProdukt() {
		Produkt p = getSelectedProdukt();
		if (p != null) {
			produkte.remove(p);
			getServiceProdukt().remove(p);
			setSelectedProdukt(null);
		}
		return true;
	}

	public void copyProdukt() {
		if (this.getSelectedProdukt() != null) {
			Produkt copy = this.getSelectedProdukt().copy();
			copy.setId(null);
			this.getProdukte().add(copy);
			this.navigateToEditor(copy);
		} else {
			FacesMessage success = new FacesMessage(FacesMessage.SEVERITY_INFO, "", "Bitte einen Artikel auswählen!");
			FacesContext.getCurrentInstance().addMessage(null, success);
		}
	}

	public void save() {
		getServiceProdukt().persistProdukte(this.getProdukte());
	}
}
