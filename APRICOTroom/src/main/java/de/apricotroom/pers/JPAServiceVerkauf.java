package de.apricotroom.pers;

import java.util.Iterator;
import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import de.apricotroom.bo.Produkt;
import de.apricotroom.bo.Verkauf;
import de.apricotroom.bo.Lieferant;

public class JPAServiceVerkauf extends JPAService {
	
	public static void main(String[] args) {
		Verkauf v = new Verkauf();
		new JPAServiceVerkauf().persist(v);
	}
	
	@Override
	protected String getClassName() {
		return Verkauf.class.getName();
	}

	public void persistVerkaeufe(List<Verkauf> list) {
		Iterator<Verkauf> it = list.iterator();
		while (it.hasNext()) {
			Verkauf p = it.next();
			persist(p);
		}
	}

	public void removeAll(List<Verkauf> entities) {
		Iterator<Verkauf> it = entities.iterator();
		while (it.hasNext()) {
			remove(it.next());
		}
	}

	
	
	public List<Verkauf> getVerkaeufe() {
		// create query
		List<Verkauf> resultList = null;
		String query = this.buildQuery();
		TypedQuery<Verkauf> aQuery = this.getEntityManager().createQuery(query, Verkauf.class);

		// fetch user from database
		try {
			resultList = aQuery.getResultList();
		} catch (javax.persistence.NoResultException e) {
			// do nothing
		}

		return resultList;
	}

}
