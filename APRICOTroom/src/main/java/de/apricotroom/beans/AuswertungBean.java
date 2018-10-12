package de.apricotroom.beans;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.apache.poi.util.IOUtils;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import de.apricotroom.bo.Auswertung;
import de.apricotroom.bo.Ergebnis;
import de.apricotroom.bo.Produkt;
import de.apricotroom.pers.JPAServiceAuswertung;
import de.apricotroom.tools.ProduktLister;

@ManagedBean
@SessionScoped
public class AuswertungBean {
	private List<Auswertung> auswertungen = new ArrayList<>();
	private List<Ergebnis> ergebnisse = new ArrayList<>();
	private Auswertung selectedAuswertung;
	private String selectedRows;
	private UploadedFile file;

	private JPAServiceAuswertung serviceAuswertung = new JPAServiceAuswertung();

	public String getSelectedRows() {
		return selectedRows;
	}

	public void setSelectedRows(String selectedRows) {
		this.selectedRows = selectedRows;
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

	public void onDeselect(Auswertung f, String typeOfSelection, String indexes) {
		setSelectedAuswertung(null);
		// this.setButtonsDisabled(true);
	}

	public void onSelect(Auswertung f, String typeOfSelection, String indexes) {
		setSelectedAuswertung(f);
		selectedRows = indexes;
		// this.setButtonsDisabled(false);
	}

	@PostConstruct
	public void init() {
		this.setAuswertungen(serviceAuswertung.getAllAuswertungen());

	}

	public UploadedFile getFile() {
		return file;
	}

	public void setFile(UploadedFile file) {
		this.file = file;
	}

	public String auswertenAll() {
		ProduktLister lister = new ProduktLister();
		Map<Produkt, Integer> result = lister.readFiles(this.getAuswertungen());
		Iterator<Produkt> it = result.keySet().iterator();
		while (it.hasNext()) {
			Produkt p = it.next();
			Ergebnis e = new Ergebnis();
			e.setProdukt(p);
			e.setCount(result.get(p));
			getErgebnisse().add(e);
		}

		return null;
	}

	public String auswerten() {
		if (this.getSelectedAuswertung() == null) {
			FacesMessage success = new FacesMessage(FacesMessage.SEVERITY_INFO, "", "Bitte eine Auswertung auswählen!");
			FacesContext.getCurrentInstance().addMessage(null, success);
		} else {
			ProduktLister lister = new ProduktLister();
			Map<Produkt, Integer> result = lister.readFile(this.getSelectedAuswertung());
			Iterator<Produkt> it = result.keySet().iterator();
			while (it.hasNext()) {
				Produkt p = it.next();
				Ergebnis e = new Ergebnis();
				e.setProdukt(p);
				e.setCount(result.get(p));
				getErgebnisse().add(e);
			}
		}
		return null;
	}

	public List<Ergebnis> getErgebnisse() {
		return ergebnisse;
	}

	public void setErgebnisse(List<Ergebnis> ergebnisse) {
		this.ergebnisse = ergebnisse;
	}

	public String upload() {
		if (file.getFileName() != null && !file.getFileName().isEmpty()) {
			try {
				byte[] bytes = IOUtils.toByteArray(file.getInputstream());
				Auswertung a = new Auswertung();
				a.setData(bytes);
				a.setFilename(file.getFileName());
				this.getAuswertungen().add(a);
				serviceAuswertung.persist(a);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return "";
	}

	public void fileUploadListener(FileUploadEvent e) {
		// Get uploaded file from the FileUploadEvent
		this.file = e.getFile();
		// Print out the information of the file
	}
}
