package de.apricotroom.beans;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import de.apricotroom.bo.Lieferant;
import de.apricotroom.pers.JPAServiceLieferant;

@ManagedBean
@SessionScoped
public class LieferantenBean {
	private List<Lieferant> lieferanten = new ArrayList<>();
	private JPAServiceLieferant serviceLieferant = new JPAServiceLieferant();
	
	public List<Lieferant> getLieferanten() {
		return lieferanten;
	}

	public void setLieferanten(List<Lieferant> lieferanten) {
		this.lieferanten = lieferanten;
	}
	@PostConstruct
	public void init() {
		this.setLieferanten(serviceLieferant.getLieferanten());

	}

}
