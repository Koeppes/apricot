package de.apricotroom.pers;

import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.Predicate;

import de.apricotroom.bo.Benutzer;

public class JPAServiceBenutzer extends JPAService {
	@Override
	protected String getClassName() {
		return Benutzer.class.getName();
	}
	public List<Benutzer> getAllBenutzer() {
		// create query
		List<Benutzer> resultList = null;
		String query = this.buildQuery();
		TypedQuery<Benutzer> aQuery = this.getEntityManager().createQuery(query, Benutzer.class);

		// fetch user from database
		try {
			resultList = aQuery.getResultList();
		} catch (javax.persistence.NoResultException e) {
			// do nothing
		}

		return resultList;
	}

	public Benutzer findBenutzer(String username, String password) {
		Benutzer user = null;
		List<Benutzer> result = null;
		javax.persistence.criteria.CriteriaBuilder qb = this.getEntityManager().getCriteriaBuilder();
		javax.persistence.criteria.CriteriaQuery<Benutzer> c = qb.createQuery(Benutzer.class);
		javax.persistence.criteria.Root<Benutzer> p = c.from(Benutzer.class);
		List<javax.persistence.criteria.Predicate> predicates = new java.util.ArrayList<javax.persistence.criteria.Predicate>();
		Predicate condition1 = qb.equal(p.get("username"), username);
		Predicate condition2 = qb.equal(p.get("password"), password);
		predicates.add(condition1);
		predicates.add(condition2);
		c.where(predicates.toArray(new Predicate[] {}));
		TypedQuery<Benutzer> q = this.getEntityManager().createQuery(c);
		result = q.getResultList();
		if (!result.isEmpty()) {
			user = result.get(0);
		}
		return user;
	}
	
	public static void main(String[] args) {
		Benutzer u = new Benutzer();
		u.setName("Hans");
		u.setUsername("Koeppes");
		u.setPassword("Wurst");
		JPAServiceBenutzer service = new JPAServiceBenutzer();
		service.persist(u);		
		List<Benutzer> users = service.getAllBenutzer();
	}

}
