package de.apricotroom.beans;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

@ManagedBean(name = "navigationController", eager = true)
@RequestScoped

public class NavigationController implements Serializable {
	public String moveToLogin() {
		return "login";
	}

	public String moveToRegister() {
		return "register";
	}
	public String moveToProduktEditor() {
		return "productEditor";
	}
	public String moveToProduktSearch() {
		return "productSearch";
	}
}