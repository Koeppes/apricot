
package de.apricotroom.beans;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.application.NavigationHandler;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.apache.poi.util.IOUtils;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;
import de.apricotroom.bo.Auswertung;
import de.apricotroom.bo.Ergebnis;
import de.apricotroom.bo.Farben;
import de.apricotroom.bo.Kategorien;
import de.apricotroom.bo.Lieferant;
import de.apricotroom.bo.Materialien;
import de.apricotroom.bo.Produkt;
import de.apricotroom.bo.Ringgroessen;
import de.apricotroom.bo.Verkauf;
import de.apricotroom.pers.JPAServiceAuswertung;
import de.apricotroom.pers.JPAServiceLieferant;
import de.apricotroom.pers.JPAServiceProdukt;
import de.apricotroom.pers.JPAServiceVerkauf;
import de.apricotroom.tools.ProduktImporter;
import de.apricotroom.tools.ProduktLister;

@SessionScoped
@ManagedBean

public class ProductSearchBean {
	private static int TAB_INDEX_PRODUCTS = 0;
	private static int TAB_INDEX_SUPPLIERS = 1;
	private static int TAB_INDEX_SALES = 2;
	private static int TAB_INDEX_EVALUATION = 3;
	private static final long serialVersionUID = 1L;
	private List<Produkt> produkte = new ArrayList<>();
	private List<Lieferant> lieferanten = new ArrayList<>();
	private UploadedFile importFile;
	private Lieferant selectedFilterLieferant;
	private String selectedRows;
	private String selectedRowsLieferant;
	private Produkt selectedProdukt;
	private Lieferant selectedLieferant;
	private JPAServiceProdukt serviceProdukt = new JPAServiceProdukt();
	private JPAServiceLieferant serviceLieferant = new JPAServiceLieferant();
	private JPAServiceVerkauf serviceVerkauf = new JPAServiceVerkauf();

	private boolean buttonsDisabled;
	private List<Kategorien> kategorien;
	private List<String> kategorienString;
	private Kategorien selectedFilterKategorie;
	private String selectedFilterKategorieString;
	private List<Materialien> materialien;
	private List<String> materialienString;
	private Materialien selectedFilterMaterial;
	private String selectedFilterMaterialString;
	private List<Ringgroessen> groessen;
	private int tabIndex;
	private List<Auswertung> auswertungen = new ArrayList<>();
	private List<Auswertung> selectedAuswertungen = new ArrayList<>();
	private List<Ergebnis> ergebnisse = new ArrayList<>();
	private List<Ergebnis> ergebnisseDB = new ArrayList<>();

	public List<Ergebnis> getErgebnisseDB() {
		return ergebnisseDB;
	}

	public void setErgebnisseDB(List<Ergebnis> ergebnisseDB) {
		this.ergebnisseDB = ergebnisseDB;
	}

	private Auswertung selectedAuswertung;
	private String selectedRowsAuswertung;
	private UploadedFile fileAuswertung;
	private Date date;
	private Date filterDateAuswertungVon;
	private Date filterDateAuswertungBis;
	private Date filterDateAuswertungDBVon;
	private Date filterDateAuswertungDBBis;

	public Date getFilterDateAuswertungDBVon() {
		return filterDateAuswertungDBVon;
	}

	public void setFilterDateAuswertungDBVon(Date filterDateAuswertungDBVon) {
		this.filterDateAuswertungDBVon = filterDateAuswertungDBVon;
	}

	public Date getFilterDateAuswertungDBBis() {
		return filterDateAuswertungDBBis;
	}

	public void setFilterDateAuswertungDBBis(Date filterDateAuswertungDBBis) {
		this.filterDateAuswertungDBBis = filterDateAuswertungDBBis;
	}

	private Integer saleCount = 1;
	private String saleSerial;
	private Date saleDate = new Date(System.currentTimeMillis());
	private List<Verkauf> sales = new ArrayList<>();
	private Verkauf selectedSale;

	public Verkauf getSelectedSale() {
		return selectedSale;
	}

	public void setSelectedSale(Verkauf selectedSale) {
		this.selectedSale = selectedSale;
	}

	public List<Verkauf> getSales() {
		return sales;
	}

	public void setSales(List<Verkauf> sales) {
		this.sales = sales;
	}

	public Date getSaleDate() {
		return saleDate;
	}

	public void setSaleDate(Date saleDate) {
		this.saleDate = saleDate;
	}

	public void saveSale() {
		serviceVerkauf.persistVerkaeufe(this.getSales());
		this.setSales(new ArrayList<>());
		this.info("Verkäufe erfolgreich gespeichert");
	}

	public void addSale() {
		if (this.getSaleSerial() != null && !this.getSaleSerial().isEmpty()) {
			Produkt p = serviceProdukt.getProduktBySerial(this.getSaleSerial());
			if (p != null) {
				Verkauf v = new Verkauf();
				v.setCount(this.getSaleCount());
				v.setProdukt(p);
				v.setDate(this.getSaleDate());
				sales.add(v);
			} else {
				info("Artikel " + this.getSaleSerial() + " konnte nicht gefunden werden");
			}
			saleCount = 1;
			saleSerial = "";
			saleDate = new Date(System.currentTimeMillis());
			this.setSaleSerial("");
		} else {
			info("Bitte eine Seriennummer eingeben!");
		}
	}

	public Integer getSaleCount() {
		return saleCount;
	}

	public void setSaleCount(Integer saleCount) {
		this.saleCount = saleCount;
	}

	public String getSaleSerial() {
		return saleSerial;
	}

	public void setSaleSerial(String saleSerial) {
		this.saleSerial = saleSerial;
	}

	public Date getFilterDateAuswertungVon() {
		return filterDateAuswertungVon;
	}

	public void setFilterDateAuswertungVon(Date filterDateAuswertungVon) {
		this.filterDateAuswertungVon = filterDateAuswertungVon;
	}

	public Date getFilterDateAuswertungBis() {
		return filterDateAuswertungBis;
	}

	public void setFilterDateAuswertungBis(Date filterDateAuswertungBis) {
		this.filterDateAuswertungBis = filterDateAuswertungBis;
	}

	private JPAServiceAuswertung serviceAuswertung = new JPAServiceAuswertung();

	public int getTabIndex() {
		return tabIndex;
	}

	public void setTabIndex(int tabIndex) {
		this.tabIndex = tabIndex;
	}

	public Lieferant getSelectedLieferant() {
		return selectedLieferant;
	}

	public void setSelectedLieferant(Lieferant s) {
		this.selectedLieferant = s;
	}

	public List<String> getKategorienString() {
		if (kategorienString == null) {
			kategorienString = new ArrayList<>();
			for (int i = 1; i < Kategorien.values().length; i++) {
				Kategorien k = Kategorien.values()[i];
				kategorienString.add(k.getValue());
			}
		}
		return kategorienString;
	}

	public void setKategorienString(List<String> kategorienString) {
		this.kategorienString = kategorienString;
	}

	public List<String> getMaterialienString() {
		if (materialienString == null) {
			materialienString = new ArrayList<>();
			for (int i = 1; i < Materialien.values().length; i++) {
				Materialien k = Materialien.values()[i];
				materialienString.add(k.getValue());
			}
		}

		return materialienString;
	}

	public void setMaterialienString(List<String> materialienString) {
		this.materialienString = materialienString;
	}

	public String getSelectedFilterKategorieString() {
		return selectedFilterKategorieString;
	}

	public void setSelectedFilterKategorieString(String selectedFilterKategorieString) {
		this.selectedFilterKategorieString = selectedFilterKategorieString;
	}

	public String getSelectedFilterMaterialString() {
		return selectedFilterMaterialString;
	}

	public void setSelectedFilterMaterialString(String selectedFilterMaterialString) {
		this.selectedFilterMaterialString = selectedFilterMaterialString;
	}

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

	public void filterAuswertungen() {
		this.setTabIndex(TAB_INDEX_EVALUATION);
		List<Auswertung> filteredList = new ArrayList<>();
		List<Auswertung> list = serviceAuswertung.getAllAuswertungen();
		if (this.getFilterDateAuswertungVon() != null && this.getFilterDateAuswertungBis() != null) {
			if (this.getFilterDateAuswertungVon().before(this.getFilterDateAuswertungBis())
					|| org.apache.commons.lang.time.DateUtils.isSameDay(this.getFilterDateAuswertungBis(),
							this.getFilterDateAuswertungVon())) {
				filteredList = list.stream()
						.filter(p -> p.getDate().after(this.getFilterDateAuswertungVon())
								|| org.apache.commons.lang.time.DateUtils.isSameDay(p.getDate(),
										this.getFilterDateAuswertungVon()))
						.collect(Collectors.toList());
				filteredList = filteredList.stream()
						.filter(p -> p.getDate().before(this.getFilterDateAuswertungBis())
								|| org.apache.commons.lang.time.DateUtils.isSameDay(p.getDate(),
										this.getFilterDateAuswertungBis()))
						.collect(Collectors.toList());
			} else {
				info("Das von-Datum muss vor dem bis-Datum liegen!");
			}
			this.setAuswertungen(filteredList);
		} else {
			this.setAuswertungen(list);
		}

	}

	public void filter() {
		this.setTabIndex(TAB_INDEX_PRODUCTS);
		List<Produkt> filteredProdukte = serviceProdukt.getProdukte();
		Produkt selected = this.getSelectedProdukt();
		List<Produkt> filteredByLiefernaten = new ArrayList<>();
		Lieferant l = this.getSelectedFilterLieferant();
		if (l != null && !l.getName().equals("Keine Auswahl")) {
			filteredByLiefernaten = filteredProdukte.stream().filter(p -> p.getLieferant() == l)
					.collect(Collectors.toList());
			filteredProdukte = filteredByLiefernaten;
		}
		String k = this.getSelectedFilterKategorieString();
		if (!"Keine Auswahl".equals(k) && !k.isEmpty()) {
			List<String> kats = Arrays.asList(k.split(","));
			if (k != null) {
				List<Produkt> filteredByKategorien = filteredProdukte.stream()
						.filter(p -> kats.contains(p.getKategorie())).collect(Collectors.toList());
				filteredProdukte = filteredByKategorien;
			}
		}

		String m = this.getSelectedFilterMaterialString();
		if (!m.isEmpty() && !"Keine Auswahl".equals(m)) {
			List<String> mats = Arrays.asList(m.split(","));
			if (m != null) {
				List<Produkt> filteredByMaterialien = filteredProdukte.stream()
						.filter(p -> mats.contains(p.getMaterial())).collect(Collectors.toList());
				filteredProdukte = filteredByMaterialien;
			}
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
			List<Produkt> filteredByPreisNotNull = filteredProdukte.stream().filter(p -> p.getSellingPrice() != null)
					.collect(Collectors.toList());
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
				this.info("Es wurden " + list.size() + " Artikel importiert!");
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
		List<Lieferant> list = getServiceLieferant().getLieferanten();
		list.remove(0);
		this.setLieferanten(list);
		this.setProdukte(getServiceProdukt().getProdukte());
		this.setAuswertungen(serviceAuswertung.getAllAuswertungen());
		// this.setSales(serviceVerkauf.getVerkaeufe());

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

	public void info(String message) {
		FacesMessage success = new FacesMessage(FacesMessage.SEVERITY_INFO, "", message);
		FacesContext.getCurrentInstance().addMessage(null, success);
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

	public String getSelectedRowsLieferant() {
		return selectedRowsLieferant;
	}

	public void setSelectedRows(String selectedRows) {
		this.selectedRows = selectedRows;
	}

	public void setSelectedRowsLierferant(String selectedRows) {
		this.selectedRowsLieferant = selectedRows;
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

	public void onSelectLieferant(Lieferant l, String typeOfSelection, String indexes) {
		setSelectedLieferant(l);
	}

	public void onSelectSale(Verkauf v, String typeOfSelection, String indexes) {
		setSelectedSale(v);
	}

	public void onDeselectLieferant(Lieferant l, String typeOfSelection, String indexes) {
		setSelectedLieferant(null);
	}

	public void onDeselectSale(Verkauf v, String typeOfSelection, String indexes) {
		setSelectedSale(null);
	}

	public void editProdukt() {
		this.setTabIndex(TAB_INDEX_PRODUCTS);
		if (this.getSelectedProdukt() != null) {
			navigateToEditor(this.getSelectedProdukt());
		} else {
			FacesMessage success = new FacesMessage(FacesMessage.SEVERITY_INFO, "", "Bitte einen Artikel auswählen!");
			FacesContext.getCurrentInstance().addMessage(null, success);
		}
	}

	public void editLieferant() {
		this.setTabIndex(TAB_INDEX_SUPPLIERS);
		if (this.getSelectedLieferant() != null) {
			navigateToEditor(this.getSelectedLieferant());
		} else {
			FacesMessage success = new FacesMessage(FacesMessage.SEVERITY_INFO, "",
					"Bitte einen Lieferanten auswählen!");
			FacesContext.getCurrentInstance().addMessage(null, success);
		}
	}

	public boolean produktSelected() {
		return this.getSelectedProdukt() != null;
	}

	public void createProdukt() {
		this.setTabIndex(TAB_INDEX_PRODUCTS);
		Produkt p = new Produkt();
		p.setCounter(serviceProdukt.getProdukte().size());
		produkte.add(p);
		setSelectedProdukt(p);
		p.setLieferant(getServiceLieferant().getLieferantById(new Long(0)));
		selectedRows = String.valueOf(produkte.size() - 1);
		navigateToEditor(p);
	}

	public void createLieferant() {
		this.setTabIndex(TAB_INDEX_SUPPLIERS);
		Lieferant l = new Lieferant();
		lieferanten.add(l);
		setSelectedLieferant(l);
		selectedRows = String.valueOf(produkte.size() - 1);
		navigateToEditor(l);
	}

	private void navigateToEditor(Lieferant l) {

		FacesContext.getCurrentInstance().getExternalContext().getRequestMap().put("selectedLieferant", l);
		FacesContext.getCurrentInstance().getExternalContext().getRequestMap().put("selectedLieferantCopy", l.copy());
		FacesContext.getCurrentInstance().getExternalContext().getRequestMap().put("serviceLieferant",
				this.getServiceLieferant());
		FacesContext.getCurrentInstance().getExternalContext().getRequestMap().put("productSearchBean", this);

		FacesContext fc = FacesContext.getCurrentInstance();
		NavigationHandler nav = fc.getApplication().getNavigationHandler();
		try {
			nav.handleNavigation(fc, null, "/lieferantEditor.xhtml");
			fc.renderResponse();
		} catch (Exception e) {
			System.out.println(e);
		}

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
		this.setTabIndex(TAB_INDEX_PRODUCTS);
		Produkt p = getSelectedProdukt();
		if (p != null) {
			produkte.remove(p);
			getServiceProdukt().remove(p);
			setSelectedProdukt(null);
		} else {
			info("Bitte ein Produkt auswählen!");
		}
		return true;
	}

	public boolean deleteLieferant() {
		this.setTabIndex(TAB_INDEX_SUPPLIERS);
		Lieferant l = getSelectedLieferant();
		if (l != null) {
			lieferanten.remove(l);
			getServiceLieferant().remove(l);
			setSelectedLieferant(null);
		} else {
			info("Bitte einen Lieferanten auswählen!");
		}
		return true;
	}

	public boolean deleteSale() {
		this.setTabIndex(TAB_INDEX_EVALUATION);
		Verkauf v = getSelectedSale();
		if (v != null) {
			sales.remove(v);
			setSelectedSale(null);
		} else {
			info("Bitte einen Verkauf auswählen!");
		}
		return true;
	}

	public void copyProdukt() {
		this.setTabIndex(TAB_INDEX_PRODUCTS);
		if (this.getSelectedProdukt() != null) {
			Produkt copy = this.getSelectedProdukt().copy();
			copy.buildSerialnumber(serviceProdukt.getProdukte().size());
			copy.setId(null);
			this.getProdukte().add(copy);
			selectedRows = String.valueOf(produkte.size() - 1);
			this.navigateToEditor(copy);
		} else {
			info("Bitte einen Artikel auswählen!");
		}
	}

	public void save() {
		getServiceProdukt().persistProdukte(this.getProdukte());
	}

	public List<Auswertung> getSelectedAuswertungen() {
		return selectedAuswertungen;
	}

	public void setSelectedAuswertungen(List<Auswertung> selectedAuswertungen) {
		this.selectedAuswertungen = selectedAuswertungen;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public void deleteFileAuswertung() {
		this.setTabIndex(TAB_INDEX_EVALUATION);
		if (!this.getSelectedAuswertungen().isEmpty()) {
			Iterator<Auswertung> it = this.getSelectedAuswertungen().iterator();
			while (it.hasNext()) {
				Auswertung a = it.next();
				if (a != null) {
					this.setSelectedAuswertung(null);
					this.auswertungen.remove(a);
					serviceAuswertung.remove(a);
				}
			}
		} else {
			info("Bitte ein Auswertungsdatei auswählen!");
		}
	}

	public String getSelectedRowsAuswertung() {
		return selectedRowsAuswertung;
	}

	public void setSelectedRowsAuswertung(String selectedRows) {
		this.selectedRowsAuswertung = selectedRows;
	}

	public Auswertung getSelectedAuswertung() {
		return selectedAuswertung;
	}

	public void setSelectedAuswertung(Auswertung selectedFile) {
		this.selectedAuswertung = selectedFile;
	}

	public List<Auswertung> getAuswertungen() {
		return auswertungen;
	}

	public void setAuswertungen(List<Auswertung> files) {
		this.auswertungen = files;
	}

	public void onDeselectAuswertung(Auswertung f, String typeOfSelection, String indexes) {
		// setSelectedAuswertung(null);
		if (null != f) {
			getSelectedAuswertungen().remove(f);
		} else if (null != indexes) {
			if (indexes.startsWith("[")) {
				indexes = indexes.substring(1, indexes.length() - 1);
			}
			String[] indexArray = indexes.split(",");
			for (String index : indexArray) {
				int i = Integer.valueOf(index);
				getSelectedAuswertungen().remove(auswertungen.get(i));
			}
		}
		if (!indexes.startsWith("[")) {
			selectedRowsAuswertung = "";
		} else {
			selectedRowsAuswertung = indexes;
		}
		// this.setButtonsDisabled(true);
	}

	public void onSelectAuswertung(Auswertung f, String typeOfSelection, String indexes) {
		// setSelectedAuswertung(f);
		if (null != f) {
			getSelectedAuswertungen().add(f);
		} else if (null != indexes) {
			if (indexes.startsWith("[")) {
				indexes = indexes.substring(1, indexes.length() - 1);
			}
			String[] indexArray = indexes.split(",");
			for (String index : indexArray) {
				int i = Integer.valueOf(index);
				Auswertung newAuswertung = auswertungen.get(i);
				if (!getSelectedAuswertungen().contains(newAuswertung)) {
					getSelectedAuswertungen().add(newAuswertung);
				}
			}
		}
		selectedRowsAuswertung = indexes;
		// this.setButtonsDisabled(false);
	}

	public UploadedFile getFileAuswertung() {
		return fileAuswertung;
	}

	public void setFileAuswertung(UploadedFile file) {
		this.fileAuswertung = file;
	}

	public String auswertenAll() {
		this.setTabIndex(TAB_INDEX_EVALUATION);
		this.getErgebnisse().clear();
		ProduktLister lister = new ProduktLister();
		Map<Produkt, Integer> result = lister.readFiles(this.getAuswertungen());
		Iterator<Produkt> it = result.keySet().iterator();
		while (it.hasNext()) {
			Produkt p = it.next();
			Ergebnis e = new Ergebnis();
			e.setProdukt(p);
			e.setCount(result.get(p));
			addErgebnisse(e);
		}
		this.setSelectedAuswertungen(new ArrayList<>());
		this.info("Es wurden Ergebnisse für " + this.getErgebnisse().size() + " Artikel gefunden!");
		return null;
	}

	public String auswertenDB() {
		this.setTabIndex(TAB_INDEX_EVALUATION);
		List<Verkauf> filteredList = new ArrayList<>();
		List<Verkauf> list = serviceVerkauf.getVerkaeufe();
		if (this.getFilterDateAuswertungDBVon() != null && this.getFilterDateAuswertungDBBis() != null) {
			if (this.getFilterDateAuswertungDBVon().before(this.getFilterDateAuswertungDBBis())
					|| org.apache.commons.lang.time.DateUtils.isSameDay(this.getFilterDateAuswertungDBBis(),
							this.getFilterDateAuswertungDBVon())) {
				filteredList = list.stream()
						.filter(p -> p.getDate().after(this.getFilterDateAuswertungDBVon())
								|| org.apache.commons.lang.time.DateUtils.isSameDay(p.getDate(),
										this.getFilterDateAuswertungDBVon()))
						.collect(Collectors.toList());
				filteredList = filteredList.stream()
						.filter(p -> p.getDate().before(this.getFilterDateAuswertungDBBis())
								|| org.apache.commons.lang.time.DateUtils.isSameDay(p.getDate(),
										this.getFilterDateAuswertungDBBis()))
						.collect(Collectors.toList());
			} else {
				info("Das von-Datum muss vor dem bis-Datum liegen!");
			}
		} else {
			filteredList = list;
		}
		this.setErgebnisseDB(this.buidErgebnisse(filteredList));
		return null;
	}

	private List<Ergebnis> buidErgebnisse(List<Verkauf> filteredList) {
		List<Ergebnis> result = new ArrayList<>();
		Map<String, List<Verkauf>> verkaufByProduct = filteredList.stream()
				.collect(Collectors.groupingBy(Verkauf::getSerial));
		Iterator<String> it = verkaufByProduct.keySet().iterator();
		while (it.hasNext()) {
			Ergebnis e = new Ergebnis();
			result.add(e);
			String key = it.next();
			List<Verkauf> list = verkaufByProduct.get(key);
			Iterator<Verkauf> itVerkauf = list.iterator();
			while (itVerkauf.hasNext()) {
				Verkauf v = itVerkauf.next();
				e.setCount(e.getCount() + v.getCount());
				if (e.getProdukt() == null) {
					e.setProdukt(v.getProdukt());
				}
			}

		}
		return result;
	}

	public String auswerten() {
		this.setTabIndex(TAB_INDEX_EVALUATION);
		this.getErgebnisse().clear();
		if (this.getSelectedAuswertungen().isEmpty()) {
			FacesMessage success = new FacesMessage(FacesMessage.SEVERITY_INFO, "", "Bitte eine Auswertung auswählen!");
			FacesContext.getCurrentInstance().addMessage(null, success);
		} else {
			ProduktLister lister = new ProduktLister();
			Map<Produkt, Integer> result = lister.readFiles(this.getSelectedAuswertungen());
			Iterator<Produkt> it = result.keySet().iterator();
			while (it.hasNext()) {
				Produkt p = it.next();
				Ergebnis e = new Ergebnis();
				e.setProdukt(p);
				e.setCount(result.get(p));
				addErgebnisse(e);
			}
			this.info("Es wurden Ergebnisse für " + this.getErgebnisse().size() + " Artikel gefunden!");
			this.setSelectedAuswertungen(new ArrayList<>());
		}
		return null;
	}

	private void addErgebnisse(Ergebnis e) {
		ergebnisse.add(e);

	}

	public List<Ergebnis> getErgebnisse() {
		return ergebnisse;
	}

	public void setErgebnisse(List<Ergebnis> ergebnisse) {
		this.ergebnisse = ergebnisse;
	}

	public String uploadAuswertung() {
		this.setTabIndex(TAB_INDEX_EVALUATION);
		if (fileAuswertung.getFileName() != null && !fileAuswertung.getFileName().isEmpty()) {
			try {
				byte[] bytes = IOUtils.toByteArray(fileAuswertung.getInputstream());
				ProduktImporter i = new ProduktImporter();
				Date dateFromFile = i.readDate(bytes);
				Date d = null;
				Auswertung a = new Auswertung();
				if (dateFromFile != null) {
					d = dateFromFile;
				} else {
					d = this.getDate();
				}
				if (d != null) {
					a.setDate(d);
					a.setData(bytes);
					a.setFilename(fileAuswertung.getFileName());
					this.getAuswertungen().add(a);
					serviceAuswertung.persist(a);
				} else {
					info("Bitte ein Datum definieren!");
				}
			} catch (

			IOException e) {
				e.printStackTrace();
			}
		}
		return "";

	}

	public void fileUploadListenerAuswertung(FileUploadEvent e) {
		this.setTabIndex(TAB_INDEX_EVALUATION);
		// Get uploaded file from the FileUploadEvent
		this.fileAuswertung = e.getFile();
		// Print out the information of the file
	}
}
