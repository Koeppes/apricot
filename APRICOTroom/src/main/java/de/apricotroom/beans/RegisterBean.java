package de.apricotroom.beans;

import javax.faces.application.FacesMessage;
import javax.faces.application.NavigationHandler;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import de.apricotroom.bo.Benutzer;
import de.apricotroom.pers.JPAServiceBenutzer;

@SessionScoped
@ManagedBean

public class RegisterBean {
	private static final long serialVersionUID = 1L;
	private Benutzer user = new Benutzer();
	private String password2;
	private JPAServiceBenutzer serviceBenutzer = new JPAServiceBenutzer();

	public Benutzer getUser() {
		return user;
	}

	public void setUser(Benutzer user) {
		this.user = user;
	}

	public void save() {
		if (user.getName() == null || user.getName().isEmpty()) {
			warn("register:name", "Bitte einen Vornamen angeben!");
		} else {
			if (user.getSurname() == null || user.getSurname().isEmpty()) {
				warn("register:surname", "Bitte einen Namen angeben!");
			} else {
				if (user.getUsername() == null || user.getUsername().isEmpty()) {
					warn("register:username", "Bitte einen Benutzer-Namen angeben!");
				} else {
					if (user.getPassword() == null || user.getPassword().isEmpty()) {
						warn("register:password", "Bitte ein Password angeben!");
					} else {
						if (getPassword2() == null || getPassword2().isEmpty()) {
							warn("register:password2", "Bitte das Passwort wiederholen!");
						} else {
							if (!getPassword2().equals(user.getPassword())) {
								warn("register:password", "Die eingegebenen Passwörter stimmen nicht überein!");
							} else {
								if (user.getEmail() == null || user.getEmail().isEmpty()) {
									warn("register:email", "Bitte eine E-Mail angeben!");
								} else {
									getServiceBenutzer().persist(user);
									info("register:email", "Benutzer erfolgreich angelegt");
								}
							}
						}
					}
				}
			}
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

	public String getPassword2() {
		return password2;
	}

	public void setPassword2(String password2) {
		this.password2 = password2;
	}

	public JPAServiceBenutzer getServiceBenutzer() {
		return serviceBenutzer;
	}

	public void setServiceBenutzer(JPAServiceBenutzer serviceBenutzer) {
		this.serviceBenutzer = serviceBenutzer;
	}
}
