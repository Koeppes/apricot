package de.apricotroom.pers;

import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import de.apricotroom.bo.Produkt;

public abstract class JPAService {
	private static EntityManagerFactory	factory;
	private static EntityManager			entityManager;
	public Object persist(Object entity) {
		EntityManager em;

		em = getEntityManager();
		em.getTransaction().begin();
		em.persist(entity);
		em.getTransaction().commit();

		return entity;
	}
	protected String buildQuery() {
		return "SELECT l " + "FROM " + this.getClassName() + " l";
	}
	protected abstract String getClassName();
	
	public Object remove(Object entity) {
		EntityManager em;
		em = getEntityManager();
		em.getTransaction().begin();
		em.remove(entity);
		em.getTransaction().commit();
		return entity;
	}
	public EntityManager getEntityManager() {
		if (entityManager == null) {
			entityManager = getFactory().createEntityManager();
		}
		return entityManager;
	}
	private EntityManagerFactory getFactory() {
		if (factory == null) {
			factory = Persistence.createEntityManagerFactory("Apricot");
		}
		return factory;
	}

}
