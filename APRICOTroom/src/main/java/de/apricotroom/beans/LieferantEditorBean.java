package de.apricotroom.beans;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.application.NavigationHandler;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import de.apricotroom.bo.Lieferant;
import de.apricotroom.bo.Produkt;
import de.apricotroom.pers.JPAServiceLieferant;
import de.apricotroom.pers.JPAServiceProdukt;

@ViewScoped
@ManagedBean
public class LieferantEditorBean {

	public JPAServiceLieferant getServiceLieferant() {
		return serviceLieferant;
	}

	public void setServiceLieferant(JPAServiceLieferant serviceLieferant) {
		this.serviceLieferant = serviceLieferant;
	}

	public ProductSearchBean getSearchBean() {
		return searchBean;
	}

	public void setSearchBean(ProductSearchBean bean) {
		this.searchBean = bean;
	}

	private Lieferant selectedLieferant;
	private Lieferant selectedLieferantCopy;
	private JPAServiceLieferant serviceLieferant;
	private ProductSearchBean searchBean;

	public void save() {
		boolean done = false;
		if (this.getSelectedLieferant() != null) {
			if (!this.getSelectedLieferant().getName().isEmpty()) {
				if (this.getSelectedLieferant().getNr() != null) {
					if (this.getServiceLieferant().getLieferantByNr(this.getSelectedLieferant().getNr()) != null) {
						getServiceLieferant().persist(this.getSelectedLieferant());
						done = true;
					} else {
						info("Die Nr. ist bereits für einen anderen Lieferanten vergeben!");
					}
				} else {
					info("Bitte eine Nr. für den Lieferanten vergeben!");
				}
			} else {
				info("Bitte einen Namen für den Lieferanten vergeben!");
			}
		}
		if (done) {
			navigateBack("save");
		}
	}

	public void info(String message) {
		FacesMessage success = new FacesMessage(FacesMessage.SEVERITY_INFO, "", message);
		FacesContext.getCurrentInstance().addMessage(null, success);
	}

	public void cancel() {
		this.getSelectedLieferant().rollback(this.getSelectedLieferantCopy());
		if (this.getSelectedLieferant().getId() == null) {
			this.getSearchBean().getLieferanten().remove(this.getSelectedLieferant());
			this.getSearchBean().setSelectedProdukt(null);
		}
		navigateBack("cancel");
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

	public Lieferant getSelectedLieferantCopy() {
		return selectedLieferantCopy;
	}

	public void setSelectedLieferantCopy(Lieferant selectedLieferantCopy) {
		this.selectedLieferantCopy = selectedLieferantCopy;
	}

	public Lieferant getSelectedLieferant() {
		return selectedLieferant;
	}

	public void setSelectedLieferant(Lieferant selectedLieferant) {
		this.selectedLieferant = selectedLieferant;
	}

	@PostConstruct
	public void init() {
		Lieferant l = (Lieferant) FacesContext.getCurrentInstance().getExternalContext().getRequestMap()
				.get("selectedLieferant");
		this.setSelectedLieferant(l);
		Lieferant copy = (Lieferant) FacesContext.getCurrentInstance().getExternalContext().getRequestMap()
				.get("selectedLieferantCopy");
		this.setSelectedLieferantCopy(copy);
		JPAServiceLieferant s = (JPAServiceLieferant) FacesContext.getCurrentInstance().getExternalContext()
				.getRequestMap().get("serviceLieferant");
		this.setServiceLieferant(s);
		ProductSearchBean bean = (ProductSearchBean) FacesContext.getCurrentInstance().getExternalContext()
				.getRequestMap().get("productSearchBean");
		this.setSearchBean(bean);

	}

}
