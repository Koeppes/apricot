package de.apricotroom.beans;


import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.apache.poi.util.IOUtils;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import de.apricotroom.bo.Auswertung;
import de.apricotroom.pers.JPAServiceAuswertung;
import de.apricotroom.tools.ProduktLister;

@ManagedBean
@SessionScoped
public class AuswertungBean {
	private List<Auswertung> auswertungen = new ArrayList<>();
	private Auswertung selectedAuswertung;
	private String selectedRows;
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
		// System.out.println("OnSelect:" + car + " typeOfSelection: " +
		// typeOfSelection + " indexes: " + indexes);
		setSelectedAuswertung(f);
		selectedRows = indexes;
		// this.setButtonsDisabled(false);
	}
	@PostConstruct
	public void init() {
		this.setAuswertungen(serviceAuswertung.getAllAuswertungen());
		
	}

	private UploadedFile file;

	public UploadedFile getFile() {
		return file;
	}

	public void setFile(UploadedFile file) {
		this.file = file;
	}

	public String upload() {
		if (file.getFileName() != null && !file.getFileName().isEmpty()) {
			ProduktLister lister = new ProduktLister();
			try {
				byte[] bytes = IOUtils.toByteArray(file.getInputstream());
				Auswertung a = new Auswertung();
				a.setData(bytes);
				a.setFilename(file.getFileName());
				this.getAuswertungen().add(a);
				serviceAuswertung.persist(a);
				lister.readFile((FileInputStream) file.getInputstream());
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
		System.out.println(
				"Uploaded File Name Is :: " + file.getFileName() + " :: Uploaded File Size :: " + file.getSize());
	}
}
