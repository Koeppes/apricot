package de.apricotroom.beans;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
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
	private List<Auswertung> selectedAuswertungen = new ArrayList<>();
	private List<Ergebnis> ergebnisse = new ArrayList<>();
	private Auswertung selectedAuswertung;
	private String selectedRowsAuswertung;
	private UploadedFile fileAuswertung;
	private Date date;
	private JPAServiceAuswertung serviceAuswertung = new JPAServiceAuswertung();

	
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

	public void deleteFile() {
		Auswertung a = this.getSelectedAuswertung();
		if (a != null) {
			this.setSelectedAuswertung(null);
			this.auswertungen.remove(a);
			serviceAuswertung.remove(a);
		}

	}


	public String getSelectedRowsAuswertung() {
		return selectedRowsAuswertung;
	}

	public void setSelectedRows(String selectedRows) {
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
		return null;
	}

	public String auswerten() {
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
		if (fileAuswertung.getFileName() != null && !fileAuswertung.getFileName().isEmpty()) {
			try {
				byte[] bytes = IOUtils.toByteArray(fileAuswertung.getInputstream());
				Auswertung a = new Auswertung();
				a.setDate(this.getDate());
				a.setData(bytes);
				a.setFilename(fileAuswertung.getFileName());
				this.getAuswertungen().add(a);
				serviceAuswertung.persist(a);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return "";
	}

	public void fileUploadListenerAuswertung(FileUploadEvent e) {
		// Get uploaded file from the FileUploadEvent
		this.fileAuswertung = e.getFile();
		// Print out the information of the file
	}
}
