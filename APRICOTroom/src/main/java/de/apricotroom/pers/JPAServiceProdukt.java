package de.apricotroom.pers;

import java.util.Iterator;
import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import de.apricotroom.bo.Produkt;
import de.apricotroom.bo.Lieferant;

public class JPAServiceProdukt extends JPAService {

	@Override
	protected String getClassName() {
		return Produkt.class.getName();
	}

	public void persistProdukte(List<Produkt> list) {
		Iterator<Produkt> it = list.iterator();
		while (it.hasNext()) {
			Produkt p = it.next();
			persist(p);
		}
	}

	public void removeAll(List<Produkt> entities) {
		Iterator<Produkt> it = entities.iterator();
		while (it.hasNext()) {
			remove(it.next());
		}
	}

	
	public static void main(String[] args) {

		List<Lieferant> suppliers = new JPAServiceLieferant().getLieferanten();
		Lieferant s = null;
		if (!suppliers.isEmpty()) {
			s = suppliers.get(0);
		}

		Produkt p = new Produkt();
		p.setName("Halskette");
		p.setDescription("Eine sehr schöne Halskette");
		p.setLength("30-40 cm");
		p.setPurchasePrice(new Double(3));
		p.setSellingPrice(new Double(19));
		p.setSalePrice(new Double(9.9));
		p.setSerialnumber("sdksjdjf");
		p.setLieferant(s);
		JPAServiceProdukt service = new JPAServiceProdukt();
		service.persist(p);

		List<Produkt> produkte = service.getProdukte();

	}

	public List<Produkt> getProdukte() {
		// create query
		List<Produkt> resultList = null;
		String query = this.buildQuery();
		TypedQuery<Produkt> aQuery = this.getEntityManager().createQuery(query, Produkt.class);

		// fetch user from database
		try {
			resultList = aQuery.getResultList();
		} catch (javax.persistence.NoResultException e) {
			// do nothing
		}

		return resultList;
	}

	public Produkt getProduktBySerial(String serial) {
		Produkt l = null;
		List<Produkt> result = null;
		CriteriaBuilder qb = this.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Produkt> c = qb.createQuery(Produkt.class);
		Root<Produkt> p = c.from(Produkt.class);
		Predicate condition = qb.equal(p.get("serialnumber"), serial);
		c.where(condition);
		TypedQuery<Produkt> q = this.getEntityManager().createQuery(c);
		result = q.getResultList();
		if (!result.isEmpty()) {
			l = result.get(0);
		}
		return l;

	}
}
