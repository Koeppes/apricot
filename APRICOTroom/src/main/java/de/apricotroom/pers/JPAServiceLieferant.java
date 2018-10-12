package de.apricotroom.pers;

import java.util.Iterator;
import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.hibernate.cfg.Configuration;

import de.apricotroom.bo.Lieferant;
import de.apricotroom.bo.Produkt;

public class JPAServiceLieferant extends JPAService {
	
	@Override
	public Object persist(Object entity) {
		if(!((Lieferant) entity).getName().equals("Keine Auswahl")) {
			return super.persist(entity);
		}
		return null;
	}

	@Override
	protected String getClassName() {
		return Lieferant.class.getName();
	}

	public static void main(String[] args) {
		Lieferant s = new Lieferant();
		s.setName("Hans");
		JPAService service = new JPAServiceLieferant();
		service.persist(s);
		
	}
	public void removeAll(List<Lieferant> entities) {
		Iterator<Lieferant> it = entities.iterator();
		while(it.hasNext()) {
			remove(it.next());
		}
	}
	public void persistLieferanten(List<Lieferant> list) {
		Iterator<Lieferant> it = list.iterator();
		while(it.hasNext()) {
			persist(it.next());
		}
	}
	public Lieferant getLieferantByNr(int nr) {
		Lieferant l = null;
		List<Lieferant> result = null;
		CriteriaBuilder qb = this.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Lieferant> c = qb.createQuery(Lieferant.class);
		Root<Lieferant> p = c.from(Lieferant.class);
		Predicate condition = qb.equal(p.get("nr"), nr);
		c.where(condition);
		TypedQuery<Lieferant> q = this.getEntityManager().createQuery(c);
		result = q.getResultList();
		if (!result.isEmpty()) {
			l = result.get(0);
		}
		return l;
		
	}
	public Lieferant getLieferantById(Long id) {
		Lieferant l = null;
		List<Lieferant> result = null;
		CriteriaBuilder qb = this.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Lieferant> c = qb.createQuery(Lieferant.class);
		Root<Lieferant> p = c.from(Lieferant.class);
		Predicate condition = qb.equal(p.get("id"), id);
		c.where(condition);
		TypedQuery<Lieferant> q = this.getEntityManager().createQuery(c);
		result = q.getResultList();
		if (!result.isEmpty()) {
			l = result.get(0);
		}
		return l;
		
	}
	public List<Lieferant> getLieferanten() {
		List<Lieferant> resultList = null;
		try {
			CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
			CriteriaQuery<Lieferant> criteriaQuery = criteriaBuilder.createQuery(Lieferant.class);
			Root<Lieferant> from = criteriaQuery.from(Lieferant.class);
			criteriaQuery.orderBy(criteriaBuilder.asc(from.get("id")));
			resultList = getEntityManager().createQuery(criteriaQuery).getResultList();
			Lieferant keineAuswahl = new Lieferant();
			keineAuswahl.setId(new Long(-1));
			keineAuswahl.setName("Keine Auswahl");
			resultList.add(0, keineAuswahl);
		} catch (javax.persistence.NoResultException e) {
		}
		return resultList;
	}
}
