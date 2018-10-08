package de.apricotroom.beans;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.application.NavigationHandler;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import de.apricotroom.bo.Benutzer;
import de.apricotroom.bo.Produkt;
import de.apricotroom.pers.JPAServiceBenutzer;
import net.bootsfaces.utils.FacesMessages;

@SessionScoped
@ManagedBean

public class LoginBean {
	private static final long serialVersionUID = 1L;
	private String username;
	private String password;

	private JPAServiceBenutzer serviceBenutzer;

	public JPAServiceBenutzer getServiceBenutzer() {
		return serviceBenutzer;
	}

	public void setServiceBenutzer(JPAServiceBenutzer serviceBenutzer) {
		this.serviceBenutzer = serviceBenutzer;
	}

	public String getUsername() {
		return username;
	}

	@PostConstruct
	public void init() {
		this.setServiceBenutzer(new JPAServiceBenutzer());
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void login() {
		Benutzer b = serviceBenutzer.findBenutzer(username, password);
		if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
			FacesContext.getCurrentInstance().addMessage("login:password",
					new FacesMessage(FacesMessage.SEVERITY_WARN, "Bitte Benutzernamen und Passwort eingeben!", null));
		} else {
			if (b == null) {
				FacesContext.getCurrentInstance().addMessage("login:password",
						new FacesMessage(FacesMessage.SEVERITY_ERROR, "Benutzername oder Passwort falsch!", null));
			} else {
				if (b.getStatus() != null && b.getStatus() == 1) {
					username = "";
					password = "";
					navigateToSearch();
				} else {
					FacesContext.getCurrentInstance().addMessage("login:password", new FacesMessage(
							FacesMessage.SEVERITY_WARN, "Benutzer zur Anmeldung nicht berechtigt!", null));

				}
			}
		}
	}

	private void navigateToSearch() {
		FacesContext fc = FacesContext.getCurrentInstance();
		NavigationHandler nav = fc.getApplication().getNavigationHandler();
		try {
			nav.handleNavigation(fc, null, "/all.xhtml");
			fc.renderResponse();
		} catch (Exception e) {
			System.out.println(e);
		}

	}
}
