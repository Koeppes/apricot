package de.apricotroom.beans;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.application.NavigationHandler;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import de.apricotroom.bo.Lieferant;
import de.apricotroom.bo.Produkt;
import de.apricotroom.pers.JPAServiceLieferant;
import de.apricotroom.pers.JPAServiceProdukt;

@SessionScoped
@ManagedBean

public class ProductSearchBean {
	private static final long serialVersionUID = 1L;
	private List<Produkt> produkte = new ArrayList<>();
	private List<Lieferant> lieferanten = new ArrayList<>();

	public List<Lieferant> getLieferanten() {
		return lieferanten;
	}

	public void setLieferanten(List<Lieferant> lieferanten) {
		this.lieferanten = lieferanten;
	}

	private String selectedRows;
	private Produkt selectedProdukt;
	private JPAServiceProdukt serviceProdukt = new JPAServiceProdukt();
	private JPAServiceLieferant serviceLieferant = new JPAServiceLieferant();
	private boolean buttonsDisabled;

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
		// System.out.println("OnSelect:" + car + " typeOfSelection: " +
		// typeOfSelection + " indexes: " + indexes);
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
		navigateToEditor(this.getSelectedProdukt());
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

	public void someListener() {
		System.out.println("someListenr");
	}

	private void navigateToEditor(Produkt p) {
		FacesContext.getCurrentInstance().getExternalContext().getRequestMap().put("selectedProdukt", p);
		FacesContext.getCurrentInstance().getExternalContext().getRequestMap().put("selectedProduktCopy", p.copy());
		FacesContext.getCurrentInstance().getExternalContext().getRequestMap().put("serviceProdukt",
				this.getServiceProdukt());
		FacesContext.getCurrentInstance().getExternalContext().getRequestMap().put("serviceLieferant",
				this.getServiceLieferant());

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
	}

	public void save() {
		getServiceProdukt().persistProdukte(this.getProdukte());
	}

	public void capitalizeText() {
		System.out.println("capitalizeText");
	}
}
