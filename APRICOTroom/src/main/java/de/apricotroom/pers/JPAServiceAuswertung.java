package de.apricotroom.pers;

import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.Predicate;

import de.apricotroom.bo.Auswertung;
import de.apricotroom.bo.Benutzer;

public class JPAServiceAuswertung extends JPAService {
	@Override
	protected String getClassName() {
		return Auswertung.class.getName();
	}
	public List<Auswertung> getAllAuswertungen() {
		// create query
		List<Auswertung> resultList = null;
		String query = this.buildQuery();
		TypedQuery<Auswertung> aQuery = this.getEntityManager().createQuery(query, Auswertung.class);

		// fetch user from database
		try {
			resultList = aQuery.getResultList();
		} catch (javax.persistence.NoResultException e) {
			// do nothing
		}

		return resultList;
	}
}
